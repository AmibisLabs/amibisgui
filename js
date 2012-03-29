#!/bin/sh

# -Djava.library.path=/usr/lib/jni:$omi \

java \
 -classpath $omi/'*' \
 com.sun.script.javascript.RhinoScriptEngine \
 $*
