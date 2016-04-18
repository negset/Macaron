echo off

color 0a

echo.
echo adding...
git add .
echo added.
echo.

echo.
set INPUT=
set /P INPUT="commit message > "
echo committing...
git commit -am "%INPUT%"
echo committed.
echo.

echo.
echo pushing...
git push origin master
echo pushed.
echo.

pause