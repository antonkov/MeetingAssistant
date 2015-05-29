__author__ = 'antonkov'

import  requests

asyncurl = "http://api.idolondemand.com/1/api/async/{}/v1"
syncurl = "http://api.idolondemand.com/1/api/sync/{}/v1"
apikey = "349338d6-3398-4fc2-b5a7-09e1fc1c8f79"

def postrequest(function, data={}, files={}):
    data["apikey"] = apikey
    callurl = syncurl.format(function)
    r = requests.post(callurl, data=data, files=files)
    return r

fileurl = 'https://www.idolondemand.com/sample-content/videos/hpnext.mp4'
#files = {'file': open("test.mp4", 'rb')}
#res = postrequest('recognizespeech', data={'url': fileurl}, files={})
res = postrequest('expandterms', data={'text': 'probability', 'expansion': 'fuzzy', 'max_terms': 5})
print(res.json())