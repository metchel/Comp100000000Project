all: clean java.policy compile-middleware-rmi

java.policy: FORCE
	@echo "Creating server java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

compile-middleware-rmi: ResourceManager/*.java Middleware/*.java Interface/IResourceManager.java Common/*.java Network/*.java Sockets/*.java Client/*.java LockManager/*.java Transactions/*.java
	javac Middleware/*.java Interface/IResourceManager.java Common/*.java Network/*.java Sockets/*.java ResourceManager/*.java Client/*.java LockManager/*.java Transactions/*.java

clean:
	rm -f */*.class
	rm -f java.policy 
	rm -f logfiles/*.log
