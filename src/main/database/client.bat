@echo off
setlocal

set DERBY_HOME="%JAVA_HOME%\db"

set JAVA=%JAVA_HOME%\bin\java

REM To create database
REM Network Client: CONNECT 'jdbc:derby://localhost:1527/musiclibrarydb;create=true';
REM run 'musiclibrary.sql';

%JAVA% -jar %DERBY_HOME%/lib/derbyrun.jar ij
                 
endlocal