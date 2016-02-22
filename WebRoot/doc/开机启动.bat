cd C:\Program Files\java\Tomcat 6.0.374\bin
C: 
start "" "startup.bat"

ping /n 10 127.1>nul

cd C:\Program Files\java\windows_386 
C: 
ngrok -config=ngrok.cfg -hostname http://wx.youcaihao.com 80 