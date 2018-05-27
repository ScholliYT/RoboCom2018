import sys
from bluetooth import *
import atexit

def exit_handler():
    print("Closing all connections")
    socket.close()  

def client( host="E8:B1:FC:65:CD:B2", port=4) :
   s=BluetoothSocket( RFCOMM )
   s.connect((host, port))

   while True :
      message = raw_input('Send:')
      if not message : return
      s.send(message)
      data = s.recv(1024)
      print 'Received', `data`
   s.close()

client()