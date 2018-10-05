A distributed system.
If running, be sure to edit the USER var in ./run_tcp_servers.sh to properly ssh.

Otherwise, run ./run_tcp_servers.sh to start RMs and Middleware, followed by ./run_tcp_client.sh <Middleware host> <Middleware port>

Ports are hardcoded in TCPResourceManager and TCPMiddlewareManager