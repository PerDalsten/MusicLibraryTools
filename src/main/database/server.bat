@echo off
setlocal

set DERBY_HOME="%JAVA_HOME%\db"
set DERBY_DATA=%~dp0
set JAVA=%JAVA_HOME%\bin\java

REM java -jar %DERBY_HOME%/lib/derbyrun.jar server start

%JAVA% -Dderby.system.home=%DERBY_DATA% -jar %DERBY_HOME%/lib/derbyrun.jar server start
              
endlocal