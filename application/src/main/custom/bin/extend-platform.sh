#!/bin/sh

guipath=$(cd -P -- "$(dirname -- "$0")" && cd .. && pwd -P)

DIR=`tempfile`
rm -f $DIR
"${guipath}/bin/omiscidgui" --userdir $DIR
cp -rf $DIR/* "${guipath}/omiscidgui/"
