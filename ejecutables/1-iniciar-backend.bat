@echo off
title FastFood - Backend (puerto 8080)
rem Requisitos: Java 25 y MySQL con la base "fastfood" importada (database/fastfood.sql).
rem El archivo .jar tiene que estar en esta misma carpeta.
cd /d "%~dp0"
java -jar backend-fastfood-0.0.1-SNAPSHOT.jar
pause
