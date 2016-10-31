JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        PO.java \
        Main.java 

default: classes
	mkdir -p data
	mkdir -p client/data

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
	$(RM) client/data/*
