JFLAGS = -g -d server
JC = javac

BIN_SERVER=server
BIN_CLIENT=client
BIN_CLIENT_CORRUPTED=client_corrupted

SERVER_PART=src/FtpServer.java \
			src/FtpProtocol.java \
			src/UserDB.java 
CLIENT_PART=src/FtpClient.java

## correct transfer and transfer with corruption
TRANSFER_PART=src/dataTransform.java
CORRUPTED_TRANSFER_PART=src/corrupted/dataTransform.java

all: server client corrupted_client

server:
	javac $(SERVER_PART) $(TRANSFER_PART) -d $(BIN_SERVER)
client:
	javac $(CLIENT_PART) $(TRANSFER_PART) -d $(BIN_CLIENT)
corrupted_client:
	javac $(CLIENT_PART) $(CORRUPTED_TRANSFER_PART) -d $(BIN_CLIENT_CORRUPTED)

default: 
	mkdir -p $(BIN_SERVER)/data


clean:
	$(RM) $(BIN_SERVER)/*.class
	$(RM) -rf $(BIN_CLIENT)/*.class
	$(RM) -rf $(BIN_CLIENT)/*.pdf
	$(RM) -rf $(BIN_CLIENT_CORRUPTED)/*.class
	$(RM) -rf $(BIN_CLIENT_CORRUPTED)/*.pdf
