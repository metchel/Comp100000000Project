all: java.policy compile-client

java.policy: FORCE
	@echo "Creating client java policy"
	@echo "grant codebase \"file:`pwd`/\" {" > java.policy
	@echo "permission java.security.AllPermission;" >> java.policy
	@echo "};" >> java.policy

FORCE: ;

../Server/RMIInterface.jar: ../Server/Server/Interface/IResourceManager.java
	make -C ../Server/ RMIInterface.jar

../Server/NetworkInterface.jar: ../Server/Server/Network/Request.java
	make -C ../Server/ NetworkInterface.jar

compile-client: ../Server/RMIInterface.jar ../Server/NetworkInterface.jar Client/*.java
	javac -cp ../Server/RMIInterface.jar:../Server/NetworkInterface.jar:. Client/*.java

clean:
	rm -f Client/*.class
	rm -f ../Server/RMIInterface.jar
	rm -f java.policy
