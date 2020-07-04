# Brewery-Services-spring-rest-api and Web UI Interface

Brewery Services Spring REST API Project and Web UI interface. Allows to retrieve data from various types of sensors and present data. Measurement data may also be entered manually. The API also provide for restful calls to receive data. The bluetooth interface may be used to automatically log sensor data.

## Brewery Services Dashboard

The Dashboard will display measurements for all measurements for active batches. The measurement type table will determine the type of sensor gauge displayed.

## Mobile Device

<img src="https://github.com/jslawinsk/brewery-services/blob/master/documentation/MobileDashboard.png" width="200">

## PC

<img src="https://github.com/jslawinsk/brewery-services/blob/master/documentation/PcDashboard.png" width="650">

## Architecture Design

The Brewery Services application is a Java Spring Boot Application. In the below diagram the encapsulated green boxes indicate environments where the Spring Boot Application may be deployed. All deployments are optional and designed based on need. 

Environments include the following:
- Local Server or PC, Mac OS, Linux or Windows
- Raspberry PI, Any OS which supports Java
- Cloud Instance, For this option a local instance should be used to curate and relay data
- DB will be enabled to reside locally, cloud or both. Options will be added to automatically clean up excess data. 

 ![Architecture Diagram](https://github.com/jslawinsk/brewery-services/blob/master/documentation/BrewTechDiagSpringApp.png)

## Brewery Services DB 

<img src="https://github.com/jslawinsk/brewery-services/blob/master/documentation/BreweryDB.png" width="850">

#### To Do:
- [X] Indicates completed
- [X] Bluetooth interface to to receive data from temperature controller
- [ ] Angular Modules to present Bluetooth process
- [X] CRUD UI Web layer
- [X] Choose and implement a DB, PostgreSQL implemented
- [X] Ability to add data to another instance running in the cloud. The data relay will be curerated data to reduce DB space and costs. Data will also be sent in bulk.
- [ ] Ability to synch update and delete of data to another instance running in the cloud. 
- [X] Charts and analytics
- [X] Add charting attribute (min value, max value, Gauge Type) to MeasurementType Object. Dynamically create Dashboard based on measurement type settings.
- [ ] Automated testing
- [X] Cloud deployment, Deployed to Heroku
- [ ] Deployment to Raspberry Pi
- [X] API Security
- [X] Authentication for Users and Admins
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
- dbdiagram.io