# RoboCom2018
https://www.w-hs.de/robocom-2018/

| Path          | Usage         | 
| ------------- |:-------------:| 
| /DataDebugger | Tool to configure values on the NXT from a remote client on a computer | 
| /RPi          | Files that go on the Raspberry Pi | 
| /Robot_leJos  | Files that go on NXTs | 

## Raspberry Pi
### Dependencies for python:
 numpy, imutils, opencv (cv2), pathlib, opencv-contib-python

pip (except opencv):

```$ pip install numpy imutils pathlib```

### Usage:
Start main program:

```$ python .\RPi\ball_tracking\ball_tracking.py```

Start configurator:

```$ python .\RPi\range-detector.py -f HSV -w```
