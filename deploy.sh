
# la config
cp -r config-openarchaeo-dev config-openarchaeo
cp -r config-openarchaeo-prod/* config-openarchaeo

rm -rf config-openarchaeo.zip
zip -r config-openarchaeo config-openarchaeo
rm -rf config-openarchaeo
scp -i "/home/thomas/sparna/50-Informatique/serveurs/amazon-aws/sparna-keypair-francfort.pem" config-openarchaeo.zip ubuntu@ec2-18-197-219-20.eu-central-1.compute.amazonaws.com:config-openarchaeo.zip

# les appli
scp -i "/home/thomas/sparna/50-Informatique/serveurs/amazon-aws/sparna-keypair-francfort.pem" explorateur/target/explorateur-1.0-SNAPSHOT.war ubuntu@ec2-18-197-219-20.eu-central-1.compute.amazonaws.com:explorateur.war
scp -i "/home/thomas/sparna/50-Informatique/serveurs/amazon-aws/sparna-keypair-francfort.pem" federation/target/federation-1.0-SNAPSHOT.war ubuntu@ec2-18-197-219-20.eu-central-1.compute.amazonaws.com:federation.war

# deploiement de la config
ssh -i "/home/thomas/sparna/50-Informatique/serveurs/amazon-aws/sparna-keypair-francfort.pem" ubuntu@ec2-18-197-219-20.eu-central-1.compute.amazonaws.com 'sudo bash -c "rm -rf /var/lib/tomcat8/openarchaeo/config-openarchaeo.bak; mv /var/lib/tomcat8/openarchaeo/config-openarchaeo /var/lib/tomcat8/openarchaeo/config-openarchaeo.bak; unzip /home/ubuntu/config-openarchaeo.zip -d /var/lib/tomcat8/openarchaeo"'
ssh -i "/home/thomas/sparna/50-Informatique/serveurs/amazon-aws/sparna-keypair-francfort.pem" ubuntu@ec2-18-197-219-20.eu-central-1.compute.amazonaws.com 'sudo bash -c "rm -rf /var/lib/tomcat8/webapps/explorateur.war; rm -rf /var/lib/tomcat8/webapps/explorateur; cp /home/ubuntu/explorateur.war /var/lib/tomcat8/webapps/"'
ssh -i "/home/thomas/sparna/50-Informatique/serveurs/amazon-aws/sparna-keypair-francfort.pem" ubuntu@ec2-18-197-219-20.eu-central-1.compute.amazonaws.com 'sudo bash -c "rm -rf /var/lib/tomcat8/webapps/federation.war; rm -rf /var/lib/tomcat8/webapps/federation; cp /home/ubuntu/federation.war /var/lib/tomcat8/webapps/"'
ssh -i "/home/thomas/sparna/50-Informatique/serveurs/amazon-aws/sparna-keypair-francfort.pem" ubuntu@ec2-18-197-219-20.eu-central-1.compute.amazonaws.com 'sudo bash -c "service tomcat8 restart"'
