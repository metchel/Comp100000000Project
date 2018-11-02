all: java.policy compile-server-rmi compile-middleware-rmi NetworkInterface.jar

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

RMIInterface.jar: Interface/IResourceManager.java
	@echo "Compiling RMI server interface"
	javac Interface/IResourceManager.java
	jar cvf RMIInterface.jar Interface/IResourceManager.class

compile-middleware-rmi: ResourceManager/*.java Middleware/*.java Interface/IResourceManager.java Common/*.java Network/*.java Sockets/*.java
	javac Middleware/*.java Interface/IResourceManager.java Common/*.java Network/*.java Sockets/*.java ResourceManager/*.java
compile-server-rmi: Interface/IResourceManager.java Common/*.java
	javac Interface/IResourceManager.java Common/*.java

NetworkInterface.jar: Common/Command.java Network/*.java
	@echo "Compiling network interface"
	javac Common/Command.java Network/*.java
	jar cvf NetworkInterface.jar Network/*.class Common/Command.class

clean:
	rm -f Network/*.class Sockets/*.class Interface/*.class Common/*.class Middleware/*.class
	rm -f RMIInterface.jar
	rm -f java.policy 