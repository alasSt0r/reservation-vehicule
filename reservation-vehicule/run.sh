#!/bin/bash
# Compile et lance l'application
cd "$(dirname "$0")"
ant compile
java -cp "build/classes:lib/*" com.example.reservation.App
