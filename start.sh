#!/bin/bash
export $(grep -v '^#' .env | xargs) #para cargar las variables de .env
./mvnw spring-boot:run