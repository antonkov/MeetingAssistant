__author__ = 'antonkov'

import  requests
import json

apikey = "349338d6-3398-4fc2-b5a7-09e1fc1c8f79"

def syncpostrequest(function, data={}, files={}):
    url = "http://api.idolondemand.com/1/api/sync/{}/v1"
    data["apikey"] = apikey
    callurl = url.format(function)
    r = requests.post(callurl, data=data, files=files)
    return r

def asyncpostrequest(function, data={}, files={}):
    url = "http://api.idolondemand.com/1/api/async/{}/v1"
    data["apikey"] = apikey
    callurl = url.format(function)
    job = requests.post(callurl, data=data, files=files)
    print(job.json())
    joburl = "http://api.idolondemand.com/1/job/result/{}".format(job.json()['jobID'])
    print(joburl)
    jobdata = {'apikey':apikey}
    return requests.post(joburl, data=jobdata, files={})

def recognize_and_dump(sound_filename, text_filename):
    files = {}#{'file': open(sound_filename, 'rb')}
    data = {'url': 'https://www.idolondemand.com/sample-content/videos/hpnext.mp4'}
    data['interval'] = 0
    res = asyncpostrequest('recognizespeech', data=data, files=files)
    json.dump(res.json()['actions'][0], open(text_filename, 'w'))

def read_text(text_filename):
    res = json.load(open(text_filename, 'r'))
    return res

def parse_content(content):
    offs, words = [], []
    for item in content:
        offs.append(item['offset'])
        words.append(item['content'])
    return offs, words

text_filename = 'test.txt'
sound_filename = 'test.mp4'
#recognize_and_dump(sound_filename, text_filename)
content = read_text(text_filename)['result']['document']
offs, words = parse_content(content)
print(offs, '\n', words)
content = ' '.join(words)
print(content)

result = asyncpostrequest('deletefromtextindex', data={'index': 'logs', 'delete_all_documents':True}, files={})

result = asyncpostrequest('addtotextindex', data={'index': 'logs'}, files={'file': text_filename})
print(result.json())

#result = asyncpostrequest('extractconcepts', data={'reference': '1'})
#print(result)