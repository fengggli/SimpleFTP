JFLAGS = -g -d server
JC = javac

all:default
	javac  server/*.java  client/*.java

default: 
	mkdir -p data
	mkdir -p client/data

clean:
	$(RM) server/*.class
	$(RM) client/*.class
	$(RM) client/data/*
