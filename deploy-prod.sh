
# la config
#cp -r config-openarchaeo-dev config-openarchaeo
# cp -r config-openarchaeo-prod/* config-openarchaeo

# rm -rf config-openarchaeo.zip
# zip -r config-openarchaeo config-openarchaeo
# rm -rf config-openarchaeo

# transfert de tout ça
#scp config-openarchaeo.zip explorateur/target/explorateur-1.0-SNAPSHOT.war federation/target/federation-1.0-SNAPSHOT.war tfrancart@openarchaeo.huma-num.fr:~
scp explorateur/target/explorateur-1.0-SNAPSHOT.war federation/target/federation-1.0-SNAPSHOT.war tfrancart@openarchaeo.huma-num.fr:~

# deploiement
# ssh -t tfrancart@openarchaeo.huma-num.fr 'sudo bash -c "\
# rm -rf /var/lib/tomcat8/openarchaeo/config-openarchaeo.bak;\
# mv /var/lib/tomcat8/openarchaeo/config-openarchaeo /var/lib/tomcat8/openarchaeo/config-openarchaeo.bak;\
# unzip /home/tfrancart/config-openarchaeo.zip -d /var/lib/tomcat8/openarchaeo;\
# rm -rf /var/lib/tomcat8/webapps/explorateur.war /var/lib/tomcat8/webapps/federation.war;\
# rm -rf /var/lib/tomcat8/webapps/explorateur /var/lib/tomcat8/webapps/federation;\
# cp /home/tfrancart/explorateur-1.0-SNAPSHOT.war /var/lib/tomcat8/webapps/explorateur.war;\
# cp /home/tfrancart/federation-1.0-SNAPSHOT.war /var/lib/tomcat8/webapps/federation.war;\
# service tomcat8 restart"'


ssh -t tfrancart@openarchaeo.huma-num.fr 'sudo bash -c "\
rm -rf /var/lib/tomcat8/webapps/explorateur.war /var/lib/tomcat8/webapps/federation.war;\
rm -rf /var/lib/tomcat8/webapps/explorateur /var/lib/tomcat8/webapps/federation;\
cp /home/tfrancart/explorateur-1.0-SNAPSHOT.war /var/lib/tomcat8/webapps/explorateur.war;\
cp /home/tfrancart/federation-1.0-SNAPSHOT.war /var/lib/tomcat8/webapps/federation.war;\
service tomcat8 restart"'

