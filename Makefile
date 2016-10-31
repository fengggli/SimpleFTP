JFLAGS = -g -d server
JC = javac

BIN_SERVER=server
BIN_CLIENT=client

SERVER_PART=src/FtpServer.java \
			src/FtpProtocol.java \
			src/UserDB.java 
TRANSFER_PART=src/dataTransform.java
CLIENT_PART=src/FtpClient.java


all:
	javac $(SERVER_PART) $(TRANSFER_PART) -d $(BIN_SERVER)
	javac $(CLIENT_PART) $(TRANSFER_PART) -d $(BIN_CLIENT)

default: 
	mkdir -p $(BIN_SERVER)/data


clean:
	$(RM) $(BIN_SERVER)/*.class
	$(RM) -rf $(BIN_CLIENT)/*.class
