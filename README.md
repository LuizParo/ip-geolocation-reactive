Alternative project for [ip-geolocation](https://github.com/LuizParo/ip-geolocation) that uses Spring WebFlux instead of Spring MVC.

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [About](#about)
- [Requirements](#requirements)
- [Setup and running](#setup-and-running)
  - [Installing and Running locally](#installing-and-running-locally)
  - [Running with Docker](#running-with-docker)
  - [Running the tests](#running-the-tests)
- [General information](#general-information)
  - [Application usage](#application-usage)
    - [`GET geolocation/ips/city?ip=${optionalIpAddress}` - Getting the City/State of a given IP address:](#get-geolocationipscityipoptionalipaddress---getting-the-citystate-of-a-given-ip-address)
    - [`GET geolocation/ips/country?ip=${optionalIpAddress}` - Getting the Country of a given IP address:](#get-geolocationipscountryipoptionalipaddress---getting-the-country-of-a-given-ip-address)
  - [Overall architecture](#overall-architecture)
    - [controller](#controller)
    - [api](#api)
    - [domain](#domain)
    - [service](#service)
    - [repository](#repository)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## About
Service responsible for fetching the city/state/country of a given IP address. When the IP is absent, the external IP
of the machine this service is hosted on will be used instead.

The datasource used at this project is the [GeoIP2](https://dev.maxmind.com/geoip/geoip2/geolite2/), provided by [MaxMind](https://www.maxmind.com/en/home).
It also uses Redis to cache the location associated with the IPs, in order to avoid unnecessary calls to the underlying
GeoIP2 data store when the same IP is fetched more than once.

## Requirements
- Java 11+
- Redis running locally on localhost listening to the port 6379 (can be easily run with Docker, check below how to do this).

## Setup and running

### Installing and Running locally
In order to get started, clone this repository into a directory of choice and run the following commands in it:

```shell script
# setup redis
docker run --rm -p 6379:6379 --name ip-geolocation-redis -d redis

# compiles the code and generate the executable JAR
./mvnw clean package

# runs the application on port 8080
java -jar target/geolocation-1.0.0.jar
```

### Running with Docker
The easiest way of running the application is by using Docker. Inside the project directory, run the following command to
spin up the application:
```shell script
docker-compose up
```

### Running the tests
This application contains both unit and integration tests. In order to run them, execute the following command inside the
project directory:
```shell script
./mvnw clean test
```

## General information

### Application usage
In order to use the application, make sure it's running on port 8080. The root endpoint exposed is `geolocation/ips`,
with the following specialized paths:

#### `GET geolocation/ips/city?ip=${optionalIpAddress}` - Getting the City/State of a given IP address:
In order to get the city/state location of an IP address, just call the `geolocation/ips/city` endpoint with the `ip` query
parameter, like the following:

```shell script
curl --location --request GET 'http://localhost:8080/geolocation/ips/city?ip=217.138.219.147' \
     --header 'Accept: application/json' \
     | json_pp
```

The response:
```json
{
  "city": {
    "name": "Milan",
    "geoNameId": 3173435
  },
  "state": {
    "name": "Milan",
    "geoNameId": 3173434,
    "isoCode": "MI"
  }
}
```

In case the `ip` query parameter is absent, the external IP from the host machine will be used instead:
```shell script
curl --location --request GET 'http://localhost:8080/geolocation/ips/city' \
     --header 'Accept: application/json' \
     | json_pp
```

The response:
```json
{
   "city" : {
      "name" : "Montreal",
      "geoNameId" : 6077243
   },
   "state" : {
      "name" : "Quebec",
      "geoNameId" : 6115047,
      "isoCode" : "QC"
   }
}
```

#### `GET geolocation/ips/country?ip=${optionalIpAddress}` - Getting the Country of a given IP address:
In order to get the country of an IP address, just call the `geolocation/ips/city` endpoint with the `ip` query
parameter, like the following:
```shell script
curl --location --request GET 'http://localhost:8080/geolocation/ips/country?ip=217.138.219.147' \
     --header 'Accept: application/json' \
     | json_pp
```

The response:
```json
{
    "country": {
        "name": "Italy",
        "geoNameId": 3175395,
        "isoCode": "IT",
        "inEuropeanUnion": true
    }
}
```

In case the `ip` query parameter is absent, the external IP from the host machine will be used instead:
```shell script
curl --location --request GET 'http://localhost:8080/geolocation/ips/country' \
     --header 'Accept: application/json' \
     | json_pp
```

The response:
```json
{
   "country" : {
      "isoCode" : "CA",
      "geoNameId" : 6251999,
      "inEuropeanUnion" : false,
      "name" : "Canada"
   }
}
```

### Overall architecture
This application follows the basic layered architecture, having the following flow of interactions between them:

```text
controller -> api -> service -> repository
```

The code is mainly divided in the following packages:

#### controller
Represents the `controller` layer and is the entrypoint of the application. The endpoints and DTOs are declared in this
package. The controller calls are forward to the `api` layer for processing.  

#### api
Represents the `api` layer of the application. It contains classes that validate/convert incoming DTOs into domain objects,
and coordinates subsequent calls to services. It should contain as least responsibility as possible, mostly forwarding calls
to another classes/services.

#### domain
Contains the domain objects that are used in the `service` layer of the application. They are immutable.

#### service
Represents the `service` layer of the application. Most of the application business logic is contained in this package.
It is also responsible for forwarding calls to the `repository` layer to fetch the IP address location.

#### repository
Represents the `repository` layer of the application. It contains repository interfaces to get the city/state/country of
an IP address, which uses the GeoIP2 database and Redis for caching.