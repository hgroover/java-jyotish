# Makefile for package amath_ext1

.SUFFIXES:
.SUFFIXES: .java .class

.java.class:
	javac -g -classpath ${CP_LIST} $<

TARGET_LIST:=$(patsubst %.java,%.class,$(wildcard *.java))

all: ${TARGET_LIST}
	@echo "${TARGET_LIST} up to date"

clean:
	${RM} *.class

