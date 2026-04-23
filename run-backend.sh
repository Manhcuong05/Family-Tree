#!/bin/bash
PORT=8080
echo "Checking port $PORT..."
PID=$(lsof -t -i:$PORT)
if [ -n "$PID" ]; then
  echo "Killing process $PID on port $PORT..."
  kill -9 $PID
fi
mkdir -p backend/uploads && chmod 777 backend/uploads
cd backend && export $(cat .env | xargs) && mvn clean spring-boot:run
