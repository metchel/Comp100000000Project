#Usage: ./run_tcp_middleware.sh
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:"$(pwd)/" Server.Middleware.TCPMiddlewareManager $1 $2 $3 $4