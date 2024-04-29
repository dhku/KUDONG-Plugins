
cd ..

echo Maven Build Start........

:CALL mvn clean -pl kudong-entity-riding -am install

CALL mvn clean install

echo Maven Build Complete!

set gitDIR=%cd%
set serverDir=E:\SERVER 1.20.4

set projectName=kudong-entity-riding

echo Copy %projectName% Plugin.....
copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\plugins\"

set projectName=kudong-framework

echo Copy %projectName% Plugin.....
copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\plugins\"

set projectName=kudong-towny-dynmap

echo Copy %projectName% Plugin.....
copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\plugins\"

set projectName=kudong-book

echo Copy %projectName% Plugin.....
copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\plugins\"

set projectName=kudong-nickname

echo Copy %projectName% Plugin.....
copy "%gitDIR%\target\%projectName%.jar" "%serverDir%\plugins\"


E:
cd E:\SERVER 1.20.4

echo START SERVER....

start.bat

pause