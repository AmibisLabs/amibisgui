#!/bin/sh


distBase=$2
# private is: http://oberon.inrialpes.fr/netbeans/
# private is: http://oberon.inrialpes.fr/release/OMiSCIDGui/
# public is: http://omiscid.gforge.inria.fr/download/omiscidgui/

nbmfolder=$1
# this is where to find nbm files (will be scanned recursively)
#echo "The script must be customized to be used (or use --force)"
#echo "------------------------------------------------------------"
#remove the exit once you configured previous variables
#test x$3 = x--force || exit

test -d "$nbmfolder" || (echo "not a folder: '$nbmfolder'" && exit)
#echo "Running anyway"

function nbmanalysis() {
    LENBM=$1
    mkdir ,,,,
    cd ,,,,
    unzip ../$LENBM
    ( cd netbeans/ && xpath -e "//*[@name='jar']/text()" config/Modules/*.xml | xargs unzip )

    pom=`find -name pom.xml`
    pompath=`dirname $pom`
    artifact=`basename $pompath`
    group=`dirname $pompath`
    group=`basename $group`
    #version=`xsl-value-of Info/info.xml "//manifest/@OpenIDE-Module-Implementation-Version"`
    #version=`xsl-value-of Info/info.xml "//manifest/@OpenIDE-Module-Specification-Version"`
    version=`xpath -e "/*/*[name()='version']/text()" $pom`
    if echo $version | grep '${'
    then
        version=`echo $version | sed -e 's@^[$][{]@@g' -e 's@[}]$@@g'`
        version=`xpath -e "/*/*[name()='properties']/*[name()='$version']/text()" $pom`
    fi
    
    cd -

    cat <<EOF >> $2
        <dependency>
            <groupId>$group</groupId>
            <artifactId>$artifact</artifactId>
            <version>$version</version>
            <type>nbm</type>
        </dependency>
EOF

    echo $group
    echo $artifact
    echo $version
    echo $pom
    rm -rf ,,,,
}

rm -rf ,,
mkdir ,,
out=,,/pom.xml
rm -f $out

find ${nbmfolder} -name \*.nbm | while read i;
do
    echo $i $out
    nbmanalysis $i $out
done;

deps=`cat $out`

cat <<EOF > $out
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>fr.prima.omiscidguiiiii</groupId>
    <artifactId>omiscidguiiiii</artifactId>
    <packaging>nbm-application</packaging>
    <version>3.0</version>
    <name>OMiSCIDGui - Application</name>
    <dependencies>
$deps
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>nbm-maven-plugin</artifactId>
                <extensions>true</extensions>
                <inherited>false</inherited>
                <configuration>
                    <brandingToken>omiscidgui</brandingToken>
                    <distBase>${distBase}</distBase>
                    <fileName>updates.xml</fileName>
                </configuration>
                <executions>
                    <execution>
                        <id>cluster</id>
                        <phase>process-resources</phase>
                        <goals>
                            <goal>cluster-app</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    <repositories>
        <repository>
            <id>primaven1</id>
            <name>public repository</name>
            <url>http://oberon.inrialpes.fr/public-maven</url>
        </repository>
        <repository>
            <id>primaven2</id>
            <name>private repository</name>
            <url>http://oberon.inrialpes.fr/private-maven</url>
        </repository>
    </repositories>

    <profiles>
        <profile>
            <id>deployment</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>nbm-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <id>updatesite</id>
                                <phase>package</phase>
                                <goals>
                                    <goal>autoupdate</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>

EOF


cd ,,
mvn -Pdeployment nbm:autoupdate

cd -
echo "Generated in ,,/target/netbeans_site"

