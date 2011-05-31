#!/bin/sh
CP=target/scala_2.9.0/semanticExperiments-assembly-1.0.jar
java -cp $CP jade.Boot -host localhost -gui -nomtp BackendAgent:swe.backend.BackendAgent
