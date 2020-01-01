# Brewery-Services-spring-rest-api
Brewery Services Spring REST API Project

Rest API to manage Brewery Services DB. 
Consists of the following tables.
- Style
- Batch
- Process
- Measurement Type
- Measurement

#Architecture Design

The Brewery Services application is a Java Spring Application. In the below diagram the encapsulated green boxes indicate enviroments where it could run. 

Environments include the following:
- Local Server or PC, Mac OS, Linux or Windows
- Raspberry PI, Any OS which supports Java
- Cloud Instance, For this option a local instance should be used to curate and relay data

 ![Architecture Diagram](https://github.com/jslawinsk/brewery-services/blob/master/documentation/BrewTechDiagSpringApp.png)

To Do:
- [ ] Bluetooth interface to to receive data from temperature controller
- [ ] Angular Modules to Bluetooth process
- [X] CRUD UI Web layer
- [ ] Choose and implement a DB
- [ ] Ability to relay data to another instance running in the cloud. The data relay will be curated data to reduce DB space and costs. Data will also be sent in bulk.
- [ ] Charts and analytics
- [ ] Automated testing
- [ ] Docker container for cloud deployment
- [ ] Security
- [ ] Authentication

Tools Used:
- Eclipse
- Maven Plugin 
- Spring Plugin
- Git Plugin
- Java Plugin
- PostMan
- Draw.io