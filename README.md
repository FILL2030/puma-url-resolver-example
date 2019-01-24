# Puma URL Resolver

## Overview
The Puma URL Resolver provides a web service to the Puma Corpus Creator to retrieve publication full-text download URLs or directly the publication raw data.

The API provides a single access point where either a <em>starting</em> URL is provided or the DOI of the document is given. If a DOI is given then a <em>starting</em> URL is obtained from <em>dx.doi.org</em>.

The program will follow a URL and determine where it is redirected too: by doing this it is able to obtain a host and hence journal information.

Publication full-text is obtained through relevant APIs for individual journals.

## Quick start
### Building
```bash
# mvn clean
# mvn package 
```
### Running
Set the following <em>PUMA_RESOLVER_VM_ARGS</em> environment variable for your own needs (eg proxy settings if needed) 
```bash
# export PUMA_RESOLVER_VM_ARGS='-Ddw.server.applicationConnectors[0].bindHost=127.0.0.1 -Ddw.server.applicationConnectors[0].port=8020 -Ddw.server.adminConnectors[0].bindHost=127.0.0.1  -Ddw.server.adminConnectors[0].port=8021 -Dhttp.proxyHost=your.proxy -Dhttp.proxyPort=proxyPort -Dhttps.proxyHost=your.proxy -Dhttps.proxyPort=proxyPort'  
```

and then run the included script
```bash
# ./run.sh 
```

### Usage
The main URL to obtain full-text download URLs on your local machine (using the port paramters above) sending a DOI is  
```bash
http://localhost:8020/api/v1?doi=10.1021/cg5000205
```

Alternatively you can specify a known URL
```bash
http://localhost:8020/api/v1/?url=http://dx.doi.org/10.1021/cg5000205
```


If the relevant journal resolver has been implemented this will return you either the download URL or the full-text raw data.
 
If it has not been implemented you will receive a response indicating the host where the publication can be found and an error message indicating that the resolver is not (currently) supported.

### Integration into Puma Corpus Creator
The Puma Corpus Creator (PCC) will automatically use the Puma URL Resolver if the relevant environment variable has been set.

Before running the Puma Corpus Creator set the following environment variable (assuming you are running the URL resolver on port 8020):
```bash
 # export PUMA_PCC_RESOLVER_URL='http://localhost:8020/api/v1'  
```
When importing documents, PCC can be set to automatically download full-text: if this environment variable is set PCC will attempt to obtain a URL automatically for each document and then download the relevant full-text. 

If the environment variable is not set or the Puma URL Resolver does not respond then full-text data will not be obtained.

