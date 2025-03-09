exathreat-gateway
-----------------
A SpringBoot application (built using the Spring Integration Framework) that is deployable within a Clients network. The Gateway will be listening on SysLog port that will receive event information from anything that points it. This may be routers, switches, firewalls, etc. The Gateway application will authenticate itself by communicating to the Exathreat API and if successful, bind a duplex connection to it. All SysLog events forwarded by a Clients network peripherals to the Gateway will be forwarded to the Exathreat API for ingestion into the Exathreat eco-system. 

To build your application:
*. ./gradlew clean build

To build your docker image: 
*. docker build -t exathreat-gateway .

To list your docker images:
*. docker image list | grep exathreat-gateway

To run your docker image (creates a new container):
*. docker run --name exathreat-gateway -e API_URL=http://192.168.1.71:3000/api -e API_KEY=3a6d3af1-1e0f-4778-b2b4-a1639ec3c944 -e GW_HEALTH=true -p 1514:1514 -p 5000:5000 exathreat-gateway

To get an existing container:
*. docker ps

To start an existing container (preserve data):
*. docker start <CONTAINER_NAME>

To stop a container:
*. docker stop <CONTAINER_NAME>

To remove your dangling images:
*. docker rmi -f $(docker images --filter dangling=true)

To authenticate docker to your AWS ECR registry:
*. aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com

---

To tag your docker image to your ECR repository in AWS:
*. docker tag exathreat-gateway 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-gateway

To push your tagged docker image to your ECR repository in AWS:
*. docker push 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-gateway

To pull your docker image from your ECR repository in AWS:
*. docker pull 367480315855.dkr.ecr.ap-southeast-2.amazonaws.com/exathreat-gateway

-------------

To run this via Gradle, do:
gradle bootRun "-Dapi.url=https://example.com/api/" "-Dapi.key=9c1c306c-3f59-4990-ad33-f30f247ad922" "-Dgw.health=true" "-Dnetwork.host=xxx.xxx.xxx.xxx" "-Dlog.level.app=<LEVEL>" "-Dlog.level.root=<LEVEL>" "-Dbatch.size=100" "-Dbatch.timeout=5000" "-Ddiscover.subnets=192.168.1"

api.url				 		- [req][string]	- api URL
api.key						- [req][string] - api key
gw.health			 		- [opt][string] - "true" or "false"; if not supplied, will default to "false"
network.host	 		- [opt][ip]			- ip address bound to a network interface; if not supplied, will bind to *
log.level.app	 		- [opt][string]	- "OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"; if not supplied, will default to "INFO"
log.level.root 		- [opt][string]	- "OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"; if not supplied, will default to "OFF"
batch.size		 		- [opt][string]	- between 100 to 1000; exceeding limits will be pegged; if not supplied, defaults to 100
batch.timeout	 		- [opt][string]	- in milliseconds; between 1sec to 60sec; exceeding limits will be pegged; if not supplied, defaults to 1000 (1sec)
discover.subnets	- [opt][string]	- comma separated list of subnets in format xxx.xxx.xxx - eg "192.168.1, 172.10.50"; if not supplied, will not perform asset discovery

To test your network.host is bound to the correct interface, on *nix host, run:

lsof -n -i4TCP:1514

-------------











-------------
The Inbound Adapters are:
1. HTTP
HTTP listen via http://<some-host>:5000/gateway/http
HTTP method is POST
HTTP endpoint consumes a JSON Request like this,
{
	"event": "hello world"
}

2. Syslog
Syslog listens via TCP IP: localhost, Port: 1514

-------------
The API endpoints are:

1. authenticating API key - [POST] https://<some-host>/api/auth
JSON Request: 
{
	"apiKey": "9c1c306c-3f59-4990-ad33-f30f247ad922"
}
JSON Response:
{
  "authenticated": "true",
  "orgCode": "83e53b25c7a0444a1234",
  "orgName": "Example Org Pty Ltd"
}

2. sending event - [POST] https://<some-host>/api/ingest
JSON Request:
{
  "apiKey": "9c1c306c-3f59-4990-ad33-f30f247ad922",
  "orgCode": "83e53b25c7a0444a1234",
  "orgName": "Example Org Pty Ltd",
  "events" : [
		{
				"event" : "<14>Feb  22 21:32:20 pa-fw-2 1,2016/02/24 21:45:08,007200001165,SYSTEM,globalprotect,0,2016/02/24 21:40:28,,globalprotectportal-config-succ,GP-Portal-1,0,0,general,informational,\"GlobalProtect portal client configuration generated. Login from: 216.113.183.230, User name: user3, Config name: VPN-GW-1.\",641950,0x8000000000000000,0,0,0,0,,PA-VM"
		},
		{
				"event" : "<14>Feb  22 21:32:20 pa-fw-2 1,2016/02/24 21:45:08,007200001165,SYSTEM,globalprotect,0,2016/02/24 21:40:28,,globalprotectportal-config-succ,GP-Portal-1,0,0,general,informational,\"GlobalProtect portal client configuration generated. Login from: 216.113.183.230, User name: user3, Config name: VPN-GW-1.\",641950,0x8000000000000000,0,0,0,0,,PA-VM"
		},
		{
				"event" : "<14>Feb  22 21:32:20 pa-fw-2 1,2016/02/24 21:45:08,007200001165,SYSTEM,globalprotect,0,2016/02/24 21:40:28,,globalprotectportal-config-succ,GP-Portal-1,0,0,general,informational,\"GlobalProtect portal client configuration generated. Login from: 216.113.183.230, User name: user3, Config name: VPN-GW-1.\",641950,0x8000000000000000,0,0,0,0,,PA-VM"
		}
	]
}