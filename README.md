# Star Wars Data Service

Follow all the steps to install, test and deploy Star Wars Data Service. 

These steps was tested in Ubuntu 16.04.4 LTS Xenial, installed in a virtual machine from VirtualBox (Virtual machine in Bridge network and allow all in promiscuous mode).

### Install requirements

`sudo apt-get update`

`sudo apt-get upgrade`

- Install Java, JUnit, Maven, Git and Tomcat7

`sudo apt-get install -y default-jre default-jdk junit4 maven git tomcat7`

- Install MongoDB

`sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2930ADAE8CAF5059EE73BB4B58712A2291FA4AD5`

`echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.6 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.6.list`

`sudo apt-get update`

`sudo apt-get install -y mongodb-org`

`sudo service mongod start`

### Clone and test the project 

Note: Tests are executed by JerseyTest in all Jersey REST services

`cd ~/`

`git clone https://github.com/Malinoski/StarWarsDataService.git`

`cd ~/StarWarsDataService`

`mvn clean install`

`mvn test`
 
### Deploy to Tomcat

`sudo cp ~/StarWarsDataService/StarWarsDataService.war /var/lib/tomcat7/webapps/`

`sudo service tomcat7 restart`

- See the REST services documentation at (change 'HOST' with your host deploy environment):

http://[HOST]:8080/StarWarsDataService

