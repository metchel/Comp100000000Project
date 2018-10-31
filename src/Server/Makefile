all: java.policy compile-server-rmi compile-middleware-rmi

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

RMIInterface.jar: Server/Interface/IResourceManager.java
	@echo "Compiling RMI server interface"
	javac Server/Interface/IResourceManager.java
	jar cvf RMIInterface.jar Server/Interface/IResourceManager.class


compile-middleware-rmi: Server/ResourceManager/*.java Server/Middleware/*.java Server/Interface/IResourceManager.java Server/Common/*.java Server/Network/*.java Server/Sockets/*.java
	javac Server/Middleware/*.java Server/Interface/IResourceManager.java Server/Common/*.java Server/Network/*.java Server/Sockets/*.java Server/ResourceManager/*.java
compile-server-rmi: Server/Interface/IResourceManager.java Server/Common/*.java
	javac Server/Interface/IResourceManager.java Server/Common/*.java
clean:
	rm -f Server/Network/*.class Server/Sockets/*.class Server/Interface/*.class Server/Common/*.class Server/Middleware/*.class
	rm -f RMIInterface.jar
	rm -f java.policy 
