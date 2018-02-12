@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Lilith Start Up Batch script
@REM (based on the mvn.bat script, pure batch file horror...)
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM LILITH_HOME - location of Lilith's installed home dir
@REM LILITH_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM LILITH_BATCH_PAUSE - set to 'on' to wait for a key stroke before ending
@REM LILITH_OPTS - parameters passed to the Java VM when running Lilith
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case LILITH_BATCH_ECHO is 'on'
@echo off
@REM enable echoing my setting LILITH_BATCH_ECHO to 'on'
@if "%LILITH_BATCH_ECHO%" == "on"  echo %LILITH_BATCH_ECHO%

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set HOME=%HOMEDRIVE%%HOMEPATH%)


set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

@REM try JAVA_EXE 
set LILITH_JAVA_EXE=%JAVA_EXE%
if not "%LILITH_JAVA_EXE%" == "" if exist "%LILITH_JAVA_EXE%" goto chkLHome

@REM find javaw from PATH
set LILITH_JAVA_EXE=javaw.exe
%LILITH_JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto chkLHome



echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error





:OkJHome
set LILITH_JAVA_EXE=%JAVA_HOME%\bin\javaw.exe
if exist "%LILITH_JAVA_EXE%" goto chkLHome

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = %JAVA_HOME%
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:chkLHome
if not "%LILITH_HOME%"=="" goto valLHome

if "%OS%"=="Windows_NT" SET LILITH_HOME=%~dp0..
if "%OS%"=="WINNT" SET LILITH_HOME=%~dp0..
if not "%LILITH_HOME%"=="" goto valLHome

echo.
echo ERROR: LILITH_HOME not found in your environment.
echo Please set the LILITH_HOME variable in your environment to match the
echo location of the Lilith installation
echo.
goto error

:valLHome

:stripLHome
if not _%LILITH_HOME:~-1%==_\ goto checkLBat
set LILITH_HOME=%LILITH_HOME:~0,-1%
goto stripLHome

:checkLBat
if exist "%LILITH_HOME%\bin\lilith.bat" goto init

echo.
echo ERROR: LILITH_HOME is set to an invalid directory.
echo LILITH_HOME = %LILITH_HOME%
echo Please set the LILITH_HOME variable in your environment to match the
echo location of the Lilith installation
echo.
goto error
@REM ==== END VALIDATION ====

:init
@REM Decide how to startup depending on the version of windows

@REM -- Windows NT with Novell Login
if "%OS%"=="WINNT" goto WinNTNovell

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

:WinNTNovell

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set LILITH_CMD_LINE_ARGS=%*
goto endInit

@REM The 4NT Shell from jp software
:4NTArgs
set LILITH_CMD_LINE_ARGS=%$
goto endInit

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set LILITH_CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto endInit
set LILITH_CMD_LINE_ARGS=%LILITH_CMD_LINE_ARGS% %1
shift
goto Win9xApp

@REM Reaching here means variables are defined and arguments have been captured
:endInit

:runm2
%LILITH_JAVA_EXE% %LILITH_OPTS% "-Dlilith.home=%LILITH_HOME%" -jar "%LILITH_HOME%\lib\lilith.jar" %LILITH_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
if "%OS%"=="WINNT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set LILITH_JAVA_EXE=
set LILITH_CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal & set ERROR_CODE=%ERROR_CODE%

:postExec
@REM pause the batch file if LILITH_BATCH_PAUSE is set to 'on'
if "%LILITH_BATCH_PAUSE%" == "on" pause

if "%LILITH_TERMINATE_CMD%" == "on" exit %ERROR_CODE%

exit /B %ERROR_CODE%

