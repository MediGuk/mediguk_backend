#!/bin/bash

echo "Setting up project..."

git config core.hooksPath .githooks

chmod +x mvnw
chmod +x .githooks/*
chmod +x scripts/*

echo "Installing Maven dependencies..."
./mvnw clean install

echo "Setup complete"