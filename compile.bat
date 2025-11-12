@echo off
echo === Compiling MegaChat ===
cd /d d:\CurseForge\MegaChat
javac -d bin src\megachat\utils\Message.java src\megachat\server\ChatServer.java src\megachat\server\ClientHandler.java src\megachat\client\ChatClient.java
echo.
echo === Compile thanh cong! ===
pause
