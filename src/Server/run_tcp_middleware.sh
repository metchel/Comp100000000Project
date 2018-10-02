#Usage: ./run_tcp_middleware.sh

./run_rmi.sh > /dev/null 2>&1
java -Djava.security.policy=java.policy -Djava.rmi.server.codebase=file:"$(pwd)/" Server.Middleware.MiddlewareManager $1 $2 $3 $4