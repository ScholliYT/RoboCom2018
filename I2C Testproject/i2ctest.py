import serial
import time
class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'
current_milli_time = lambda: int(round(time.time() * 1000))

def sendData(posx, posy):
    if(posx < 0 or posx > 65535):
        print(bcolors.WARNING + "PosX is not in bounds of [0|65535]!" + bcolors.ENDC)
        return
    elif(posy < 0 or posy > 65535):
        print(bcolors.WARNING + "PosX is not in bounds of [0|65535]!" + bcolors.ENDC)
        return
    response = "<" + str(posx) + "," + str(posy) + ">"
    print(bcolors.OKGREEN + "Response: " + bcolors.ENDC + response)
    ser.write(response)    

ser = serial.Serial("/dev/ttyUSB0", 9600)
stopwatch = current_milli_time()
while 1:
    print("\n\n\n\n" + bcolors.OKBLUE + "waiting for serial input from arduino" + bcolors.ENDC)
    cmd = ser.readline()
    print("PassedTime: " + str(current_milli_time() - stopwatch))
    stopwatch = current_milli_time();
    print("Command: " + cmd)
    if(cmd == "Request\r\n"):
        print(bcolors.OKGREEN + "Got Request from arduino" + bcolors.ENDC)
        sendData(123,321)
    else:
        print(bcolors.WARNING + "Something else arrived!" + bcolors.ENDC)


