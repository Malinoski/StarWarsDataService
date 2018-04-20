# Star Wars Data Service
 
### How to install in Ubuntu 16.04 

`sudo apt-get update`

`sudo apt-get upgrade`

- Install Java RE

`sudo apt-get install -y default-jre`

- Install Git

`sudo apt-get install -y git`

- Intall Tomcat

`sudo apt-get install -y tomcat7`

- Clone the project

`git clone https://github.com/Malinoski/StarWarsDataService.git`

- Install MongoDB database

`sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2930ADAE8CAF5059EE73BB4B58712A2291FA4AD5`

`echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.6 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.6.list`

