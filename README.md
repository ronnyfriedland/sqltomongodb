# sqltomongodb

## Introduction

Inspired by the migration tool [SQLToNoSQLImporter](https://github.com/msathis/SQLToNoSQLImporter) I started to create yet another [MongoDB](https://www.mongodb.com/) importer which can be configured and extended in a very easy way.


    {
     description: "Migrationtool to transfer data stored in a relational database into a MongoDB datastore.",
     author: "Ronny Friedland",
     license: "MIT",
     version: "1.0-SNAPSHOT"
    }

## Configuration

### application.yml

### import.xml

## Installation

### Build and run from source

### Start executable jar

## SSL

Authentication parameter are provided in ``application.yml``. 
Currently the communication to the MongoDB can be ssl-encrypted. The configuration parameters are:

- mongodb.ssl.enabled
- mongodb.ssl.tlsversions
- mongodb.ssl.ciphersuites

In addition you can define keystores / truststores to be used. You just have to set the JVM parameters:

- javax.net.ssl.keyStore
- javax.net.ssl.keyStorePassword
- javax.net.ssl.trustStore
- javax.net.ssl.trustStorePassword

## Authentication

Authentication parameter are provided in ``application.yml``. 

MongoDB-related parameters are:
- spring.data.mongodb.authentication-database:
- spring.data.mongodb.username:
- spring.data.mongodb.password:

Database-related parameters are:
- spring.datasource.username
- spring.datasource.password

## TODOs

- move application.yml and import.xml outside of application jar  
- provide docker container  
- more documentation  
- more converter