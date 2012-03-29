
importPackage(
              java.lang,
              java.text,
              java.io,
              java.math);

function s(p) {return java.lang.String(p)}
function p(s) {println(s);}
function callDelete(o) {o["delete"].apply(o);}

var f = java.io.FilenameFilter({accept: function (dir, name) {return s(name).endsWith(".zip")}});


var idist = new File("target").listFiles(f)[0];
var dist = idist.getName();
p(idist);
p(dist);

// remove final  zip
callDelete(new File(dist));

new File(",,").mkdir();

println ("TODO remain");
// todo unzip chmod cp zip

callDelete(new File(",,"));

/*
(cd ,, && unzip ../$idist)
chmod 755 ,,/omiscidgui/bin/*
cp src/main/custom/bin/* ,,/omiscidgui/bin/
(cd ,, && zip -r ../$dist omiscidgui)
*/
