#!/bin/bash

# Kill any existing processes on ports 8080 and 4200
fuser -k 8080/tcp
fuser -k 4200/tcp

# Start everything
npm run dev
