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

## TODOs

- enable ssl
  - tls protocol version
  - ciphersuites
  - keystore and truststore
- authentication
- move application.yml and import.xml outside of application jar  
- provide docker container  
- more documentation  
- more converter