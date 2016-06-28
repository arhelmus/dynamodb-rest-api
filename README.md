DynamoDB RESTful API
=========================
[![Build Status](https://travis-ci.org/ArchDev/dynamodb-rest-api.svg?branch=master)](https://travis-ci.org/ArchDev/dynamodb-rest-api)

Its an example of RESTful API builded on top of Play Framework and DynamoDB.

That service fully checked by unit tests. There are only 2 IT tests that checks is service launched up and is they answer on preflight CORS requests. Used async DynamoDB driver and in memory database instance for testing, so service can be easily built on CI.

## URL mappings
* **GET /api/health/isAlive** - health check url for load balancer
* **GET /api/cars?sortField=id&sortDirection=ASC** - get sorted list of all cars (sort parameters optional)
* **POST /api/cars** - create new car from JSON body of request
* **GET /api/cars/:id** - get car by ID
* **PUT /api/cars/:id** - update car by ID from JSON body of request
* **DELETE /api/cars/:id** - remove car by ID

## Car model
* **id** (_required_): **string**;
* **title** (_required_): **string**, e.g. _"Audi A4 Avant"_;
* **fuel** (_required_): gasoline or diesel;
* **price** (_required_): **integer**;
* **new** (_required_): **boolean**, indicates if car is new or used;
* **mileage** (_only for used cars_): **integer**;
* **first registration** (_only for used cars_): **date** default javascript date string.

## Run application
To run application, call:
```
activator run
```

## Run tests
To run test, call:
```
activator test
```

## Copyright  
Copyright (C) 2016 Arthur Kushka.  
Distributed under the MIT License.
