@echo off
@set APP_NAME_DEF = emulator
@set CMD = "%ANDROID_HOME%\emulator\emulator -avd  %1"
@set DEVICE_NAME = Nexus_5X_API_25
cd /d %ANDROID_HOME%\emulator
emulator -avd  %1