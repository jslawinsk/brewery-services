# Brewery Services Dashboard
The Dashboard will display measurements for all sensors for active batches.

## Mobile Device

<img src="https://github.com/jslawinsk/brewery-services/blob/master/documentation/MobileDashboard.png" width="200">

## PC

<img src="https://github.com/jslawinsk/brewery-services/blob/master/documentation/PcDashboard.png" width="650">

# Architecture Design

The Brewery Services application is a Java Spring Boot Application. In the below diagram the encapsulated green boxes indicate enviroments where it could run. 

Environments include the following:
- Local Server or PC, Mac OS, Linux or Windows
- Raspberry PI, Any OS which supports Java
- Cloud Instance, For this option a local instance should be used to curate and relay data
- DB will be enabled to reside locally, cloud or both. Options will be added to automatically clean up excess data. 

 ![Architecture Diagram](https://github.com/jslawinsk/brewery-services/blob/master/documentation/BrewTechDiagSpringApp.png)

#Brewery-Services-spring-rest-api
Brewery Services Spring REST API Project

Rest API to manage Brewery Services DB. 
Consists of the following tables.
- Style
- Batch
- Process
- Measurement Type
- Measurement

#### To Do:
- [X] Bluetooth interface to to receive data from temperature controller
- [ ] Angular Modules to present Bluetooth process
- [X] CRUD UI Web layer
- [X] Choose and implement a DB, PostgreSQL implemented
- [X] Ability to relay data to another instance running in the cloud. The data relay will be curerated data to reduce DB space and costs. Data will also be sent in bulk.
- [ ] Ability to synch update and delete of data to another instance running in the cloud. 
- [X] Charts and analytics
- [ ] Add charting attribute (min value, max value, Gauge Type) to MeasurementType Object
- [ ] Automated testing
- [X] Cloud deployment, Deployed to Heroku
- [ ] Deployment to Raspberry Pi
- [ ] Security
- [ ] Authentication
- [ ] AI Module to predict future temperatures

#### Tools Used:
- Eclipse
- Maven Plugin 
- Git Plugin
- Java Plugin
- PostgreSQL Database
- H2 Database
- Spring Plugin
- Highcharts
- PostMan
- Draw.io