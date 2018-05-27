from bluetooth import *
import atexit

def exit_handler():
    print("Closing all connections")
    socket.close()  


def server( s ) :
   conn, addr = s.accept()
   print 'Connected by', addr

   while True:
       data = conn.recv(1024)
       if not data: break
       conn.send(data)
   conn.close()

atexit.register(exit_handler)

socket=BluetoothSocket( RFCOMM )
socket.bind(('', 4))
socket.listen(1)
server( socket )