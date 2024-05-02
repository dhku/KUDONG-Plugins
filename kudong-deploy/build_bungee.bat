
cd ..

echo Maven Build Start........

CALL mvn clean -pl kudong-framework-bungee -am install
CALL mvn -pl kudong-framework -am install


echo Maven Build Complete!

echo Auto Server stop.....

set searchCMD="TASKLIST /v /fo csv | find /i "KUDONG TEST SERVER""
for /F "tokens=2 delims=, " %%G IN ('%searchCMD%') DO taskkill -pid %%G

echo Auto Server Complete!

set gitDIR=%cd%
set serverDir=E:\SERVER 1.20.4\TestServer

set projectName=kudong-framework-bungee

echo Copy %projectName% Plugin.....
copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\proxy_bungee\plugins\"

set projectName=kudong-framework

echo Copy %projectName% Plugin.....
copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\lobby\plugins\"

echo Copy %projectName% Plugin.....
copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\server1\plugins\"


E:
cd %serverDir%\proxy_bungee

echo START SERVER....

start cmd /c start.bat

cd %serverDir%\lobby

echo START SERVER....

start cmd /c start.bat

cd %serverDir%\server1

echo START SERVER....

start cmd /c start.bat



pause


