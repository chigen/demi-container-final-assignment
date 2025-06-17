#!/bin/bash

# 设置Java 17环境
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# 验证设置
echo "Java version:"
java -version

echo ""
echo "Maven version:"
mvn -version

echo ""
echo "JAVA_HOME: $JAVA_HOME" 
