#!/bin/bash

git config core.hooksPath .githooks
chmod +x .githooks/*
chmod +x scripts/*

echo "Project setup complete"