@echo off
setlocal

set DERBY_DATA=%~dp0

set JAVA=%JAVA_HOME%\bin\java

REM Client connect:
REM Embedded: CONNECT 'jdbc:derby:musiclibrarydb;create=true';
REM Network Client: CONNECT 'jdbc:derby://localhost:1527/musiclibrarydb;create=true';

REM To create database from ij: run '../resources/musiclibrary-derby.sql';

%JAVA% -Dderby.system.home=%DERBY_DATA% -jar %DERBY_HOME%/lib/derbyrun.jar ij
                 
endlocal