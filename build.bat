
set projectName=kudong-entity-riding

echo Maven Build Start........

CALL mvn clean -pl kudong-entity-riding -am install

echo Maven Build Complete!

echo Auto Server stop.....

set searchCMD="TASKLIST /v /fo csv | find /i "KUDONG TEST SERVER""
for /F "tokens=2 delims=, " %%G IN ('%searchCMD%') DO taskkill -pid %%G

echo Auto Server Complete!

set gitDIR=%cd%
set serverDir=E:\SERVER 1.20.4

echo Copy %projectName% Plugin.....

copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\plugins\"

E:
cd E:\SERVER 1.20.4

echo START SERVER....

start.bat

pause