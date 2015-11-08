# Client_Proxy_Server
A simple implementation of a proxy between a server and a client. Messages can be exchanged through TCP and /or UDP. 
Invoking the application requires commad line arguments and can be done using a single executable.
This will present the user with the modes of operation and associated options. 

Compile the files from the /src directory into /bin directory.


Invoke the application from the /bin directory as shown below
java tsapp -{c,s,p} [options] [server address] port [2nd port]

Command line options: ({} are required, [] are optional based on use case/application)
-c: run as client
-s: run as server
-p: run as proxy
-u: use UDP. client & proxy server applications

-t: use TCP. client & proxy server applications

-z: use UTC time. client applications.
-T <time>:  set server time. client & server applications.

--user <name>: credentials to use. client & server applications

--pass <password>: credentials to use. client & server applications.

-n <#>: number of consecutive times to query the server. client applications

--proxy-udp <#>: server UDP port to use. proxy application

--proxy-tcp <#>: server TCP port to use. proxy application

server address: address of server to connect to. client & proxy server applications

port: primary connection port. server & proxy applications use this as UDP receiving port. 
Client uses this as destination port for both UDP/TCP based on –u.

2nd port: alt. connection port. server & proxy use this as TCP listening port
