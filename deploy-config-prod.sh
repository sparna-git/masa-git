
# la config
cp -r config-openarchaeo-dev config-openarchaeo
cp -r config-openarchaeo-prod/* config-openarchaeo

rm -rf config-openarchaeo.zip
zip -r config-openarchaeo config-openarchaeo
rm -rf config-openarchaeo

# transfert de tout ça
scp config-openarchaeo.zip tfrancart@openarchaeo.huma-num.fr:~

# deploiement de la config
ssh -t tfrancart@openarchaeo.huma-num.fr 'sudo bash -c "\
rm -rf /var/lib/tomcat8/openarchaeo/config-openarchaeo.bak;\
mv /var/lib/tomcat8/openarchaeo/config-openarchaeo /var/lib/tomcat8/openarchaeo/config-openarchaeo.bak;\
unzip /home/tfrancart/config-openarchaeo.zip -d /var/lib/tomcat8/openarchaeo;\
service tomcat8 restart"'
