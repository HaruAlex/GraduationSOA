@echo off
title GraduationSOA - Start All Services

start "StudentService - 9001" cmd /k "cd /d D:\Java\GraduationSOA\StudentService && sbt -Dhttp.port=9001 run"

start "ThesisService - 9002" cmd /k "cd /d D:\Java\GraduationSOA\ThesisService && sbt -Dhttp.port=9002 run"

start "RegistrationService - 9003" cmd /k "cd /d D:\Java\GraduationSOA\RegistrationService && sbt -Dhttp.port=9003 run"

start "Client - 9000" cmd /k "cd /d D:\Java\GraduationSOA\Client && npx http-server -p 9000"