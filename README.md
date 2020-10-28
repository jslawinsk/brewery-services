# Brewery-Services-spring-rest-api and Web UI Interface

Brewery Services Spring REST API Project and Web UI interface. Allows to retrieve data from various types of sensors and present data. Measurement data may also be entered manually. The API also provide for restful calls to receive data. Optionally the bluetooth interface may be used to automatically log sensor data.

Refer to the Wiki page for design and more details: https://github.com/jslawinsk/brewery-services/wiki

## Brewery Services Dashboard

The Dashboard will display measurements for all measurements for active batches. The measurement type table will determine the type of sensor gauge displayed.

## Mobile Device

<img src="https://github.com/jslawinsk/brewery-services/blob/master/documentation/MobileDashboard.png" width="200">

## PC

<img src="https://github.com/jslawinsk/brewery-services/blob/master/documentation/PcDashboard.png" width="650">

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