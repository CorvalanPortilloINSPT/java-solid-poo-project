@echo off
title FastFood - Frontend (puerto 8081)
rem Iniciar primero el backend. Despues abrir http://localhost:8081 en el navegador.
rem El archivo .war tiene que estar en esta misma carpeta.
cd /d "%~dp0"
java -jar frontend-fastfood-0.0.1-SNAPSHOT.war
pause
