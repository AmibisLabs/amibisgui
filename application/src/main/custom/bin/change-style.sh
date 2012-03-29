#!/bin/sh

if test \! $# = 5
then
    echo "It takes 5 parameters"
    echo "  icon in 16x16"
    echo "  icon in 48x48"
    echo "  splash screen"
    echo "  splash screen width"
    echo "  splash screen height"
    echo "Three first parameters are paths to images."
    echo "Images are automatically converted to required image format."
    echo "'convert' is used for conversion and must be installed"
    exit
fi

guipath=$(cd -P -- "$(dirname -- "$0")" && cd .. && pwd -P)
jaringuipath="$guipath/omiscidgui/core/locale/core_omiscidgui.jar"
savedjaringuipath="$jaringuipath.s"

DIR=`tempfile`
rm -f $DIR

propfile=org/netbeans/core/startup/Bundle_omiscidgui.properties
mkdir $DIR
(cd $DIR && unzip "$jaringuipath")
convert "$1" $DIR/org/netbeans/core/startup/frame_omiscidgui.gif
convert "$2" $DIR/org/netbeans/core/startup/frame48_omiscidgui.gif
convert "$3" $DIR/org/netbeans/core/startup/splash_omiscidgui.gif
perl -pi -e 's#^SPLASH_WIDTH=.*$#SPLASH_WIDTH='"$4"'#g' "$DIR/$propfile"
perl -pi -e 's#^SPLASH_HEIGHT=.*$#SPLASH_HEIGHT='"$5"'#g' "$DIR/$propfile"
perl -pi -e 's#^SplashRunningTextFontSize=.*$#SplashRunningTextFontSize=1#g' "$DIR/$propfile"

i=0
while test -f "$savedjaringuipath$i"
do
    i=`expr $i + 1`
done

savedjaringuipath="$savedjaringuipath$i"

mv "$jaringuipath" "$savedjaringuipath"
(cd $DIR && zip -r "$jaringuipath" *)
