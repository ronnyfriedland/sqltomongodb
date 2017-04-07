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

This configuration file defines which data has to be migrated and maybe converted while migrating the data.
The schema definition of this file is listed below:


    <xs:schema attributeFormDefault="unqualified" elementFormDefault="qualified" xmlns:xs="http://www.w3.org/2001/XMLSchema">
      <xs:element name="column">
        <xs:complexType>
          <xs:simpleContent>
            <xs:extension base="xs:string">
              <xs:attribute type="xs:string" name="sourceColumn"/>
              <xs:attribute type="xs:string" name="targetField"/>
              <xs:attribute type="xs:string" name="type"/>
              <xs:attribute type="xs:string" name="pk" use="optional"/>
              <xs:attribute type="xs:string" name="gridfs" use="optional"/>
              <xs:attribute type="xs:string" name="gridfsIdSourceColumn" use="optional"/>
            </xs:extension>
          </xs:simpleContent>
        </xs:complexType>
      </xs:element>
      <xs:element name="entity">
        <xs:complexType>
          <xs:sequence>
            <xs:element ref="column" maxOccurs="unbounded" minOccurs="1"/>
          </xs:sequence>
          <xs:attribute type="xs:string" name="sourceSql"/>
          <xs:attribute type="xs:string" name="targetCollection"/>
        </xs:complexType>
      </xs:element>
    </xs:schema>

- sourceColumn: the name of the column name of your source database table
- targetField: the name of the field in your target collection
- type: the datatype - can be used to convert the data while migrating
  - string
  - long
  - integer
  - date
  - timestamp
  - blob
  - mimemessagetextblob: extracts the text parts of a mime message
  - stringtointeger: converts a string to an integer
  - integertoboolean: converts an integer to a boolean value
  - xmltojson: converts the source xml into json format
- pk: flag if the current column is the primary key (not used yet)
- gridfs: flag if the data should be stored in gridfs
- gridfsIdSourceColumn: you can define if the id of the gridfs document should be taken of the source dataset

## Installation

### Build and run from source

### Start executable jar

## SSL

Authentication parameters are provided in ``application.yml``. 
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