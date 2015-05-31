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


def develope():
    pass
    #recognize_and_dump(sound_filename, text_filename)
    #content = read_text(text_filename)['result']['document']
    #offs, words = parse_content(content)
   # print(offs, '\n', words)
    #content = ' '.join(words)
    #print(content)
    #print(len(content))

def sound_to_text(sound_filename, text_filename):
    files = {'file': open(sound_filename, 'rb')}
    res = asyncpostrequest('recognizespeech', data={'interval': 0}, files=files).json()['actions'][0]
    offs, words = parse_content(res['result']['document'])
    with open(text_filename, 'w') as f:
        f.write(' '.join(words))
        f.write('\n')
        for o in offs:
            f.write(str(o))
            f.write(' ')

def find_start(text_filename, phrase, result_filename):
    offs, text = [], []
    with open(text_filename, 'r') as f:
        for w in f.readline().split(' '):
            text.append(w)
        mp = f.readline().split(' ')
        for o in mp:
            if o.isnumeric():
                offs.append(int(o))

    phrase_words = phrase.split(' ')
    k = len(phrase_words)

    res = []
    for i, (o, w) in enumerate(zip(offs, text)):
        if phrase_words[0] == w:
            if ' '.join(text[i:i+k]) == phrase:
                res.append(i)
    snippets, times = [], []
    n = len(text)
    m = 2
    for idx in res:
        st_idx = max(0, idx - m)
        times.append(offs[st_idx])
        en_idx = min(idx + m + k, n)
        snippets.append(' '.join(text[st_idx:en_idx]))

    with open(result_filename, 'w') as f:
        f.write(str(len(snippets)))
        f.write('\n')
        for snip, time in zip(snippets, times):
            f.write(str(time))
            f.write('\n')
            f.write(snip)
            f.write('\n')

#offs, content = sound_to_text(open('test.mp4', 'rb'))
#print(offs, '\n', content)


#result = asyncpostrequest('deletefromtextindex', data={'index': 'logs', 'delete_all_documents':True}, files={})
#result = asyncpostrequest('addtotextindex', data={'index': 'logs'}, files={'file': text_filename})
#result = asyncpostrequest('extractconcepts', data={'reference': 'file'})