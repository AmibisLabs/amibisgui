
targetGroup=prima

include conf.mk

#where=nbms
#towhere=/tmp/netbeans/
#url=http://test.inrialpes.fr/netbeans

#where=/home/oberon/public-nbms/
#url=http://www-prima.imag.fr/netbeans/
#towhere=/var/www/netbeans-public/

#where=/home/oberon/private-nbms/
#url=http://oberon.inrialpes.fr/netbeans/
#towhere=/var/www/netbeans/

# to enable distant release (scp, ssh ... chmod)
cp=cp -r -f
chmod=chmod
chmodwhat=${towhere}
chgrp=chgrp

listener:
	@dnotify ${where} -r -M -q 0 -e touch ${where} &
	@sh -c 'while true; do make -f generate-update-site-from-nbm.mk ${where}/.gen; sleep 1; done;'

${where}/.gen: ${where}
	@rm -f ${towhere}/AAA__LAST_UPDATE_AT*
	@touch ${towhere}/AAA__UPDATING__
	sleep 1
	@touch ${where}/.gen
	java -jar UpdateSiteGenerator.jar "${where}" "${url}" "${towhere}/updates.xml"
#	./generate-update-site-from-nbm.sh "${where}" "${url}" --force ## old version in bash and maven
	chgrp -R ${targetGroup} ${where}/
	chmod -R g+rw ${where}/
	(rm -f ${towhere}/*.nbm || true) # handle the no-nbms case
	(${cp} ${where}/* ${towhere}/ || true) # handle the no-nbms case
#	${cp} ,,/target/netbeans_site/* ${towhere} ## old version in bash and maven
	${chgrp} -R ${targetGroup} ${chmodwhat}/
	${chmod} -R g+rw ${chmodwhat}/
	@touch "${towhere}/AAA__LAST_UPDATE_AT_$(shell date --rfc-3339=seconds)__"
	@rm ${towhere}/AAA__UPDATING__

build:
	(cd UpdateSiteGenerator && ant jar)
	cp UpdateSiteGenerator/dist/UpdateSiteGenerator.jar ./

reget:
	cp /home/prometheus/emonet/projects/MavenGui/OMiSCIDGui/scripts/UpdateSiteGenerator.jar ./
	cp /home/prometheus/emonet/projects/MavenGui/OMiSCIDGui/scripts/generate-update-site-from-nbm.mk ./

