#!/bin/sh

idist=$(echo target/*.zip)
dist=$(basename $idist)

rm -f $dist

mkdir ,,
(cd ,, && unzip ../$idist)
chmod 755 ,,/omiscidgui/bin/*
cp src/main/custom/bin/* ,,/omiscidgui/bin/
(cd ,, && zip -r ../$dist omiscidgui)
rm -rf ,,
