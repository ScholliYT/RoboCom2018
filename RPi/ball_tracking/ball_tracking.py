# USAGE
# python ball_tracking.py --video ball_tracking_example.mp4
# python ball_tracking.py

# import the necessary packages
from collections import deque
import numpy as np
import argparse
import imutils
import cv2
from sys import exit
import time
import json
import os.path
from bluetooth import *
from pathlib import Path


def callback(value):
    pass

def setup_trackbarsDefault(range_filter):
    cv2.namedWindow("Trackbars", 0)
    for i in ["MIN", "MAX"]:
        v = 0 if i == "MIN" else 255

        for j in range_filter:
            cv2.createTrackbar("%s_%s" % (j, i), "Trackbars", v, 255, callback) 

def setup_trackbars(range_filter):
    cv2.namedWindow("Trackbars", 0)
    for i in ["MIN", "MAX"]:
        for j in range_filter:
            cv2.createTrackbar("%s_%s" % (j, i), "Trackbars", data["%s_%s" % (j, i)], 255, callback)

def get_trackbar_values(range_filter):
    values = []

    for i in ["MIN", "MAX"]:
        for j in range_filter:
            v = cv2.getTrackbarPos("%s_%s" % (j, i), "Trackbars")
            values.append(v)

    return values

def save_trackbar_values(range_filter):
    for i in ["MIN", "MAX"]:
        for j in range_filter:
            data["%s_%s" % (j, i)] = cv2.getTrackbarPos("%s_%s" % (j, i), "Trackbars")
    with open(config, 'w') as outfile:
            json.dump(data, outfile)
            
# construct the argument parse and parse the arguments
ap = argparse.ArgumentParser()
ap.add_argument("-v", "--video",
    help="path to the (optional) video file")
ap.add_argument("-a", "--address", default="E8:B1:FC:65:CD:B2",
    help="bluetooth addr of the NXT")
ap.add_argument("-b", "--buffer", type=int, default=0,
    help="max buffer size")
args = vars(ap.parse_args())

X_TEXT = 10 #position of text

# define the lower and upper boundaries of the "greedwfn"
# ball in the HSV color space, then initialize the
# list of tracked points
pts = deque(maxlen=args["buffer"])
radiuses = deque(maxlen=5) # collection to calcualte avg of last x radiuses
frameTimes = deque(maxlen=10) # collection to calcualte avg of last x frameTimes

# if a video path was not supplied, grab the reference
# to the webcam
if not args.get("video", False):
    camera = cv2.VideoCapture(0)
    if not camera.isOpened():
        print("Camera can't be initialized")
        exit()
# otherwise, grab a reference to the video file
else:
    camera = cv2.VideoCapture(args["video"])

config = os.path.join(os.path.dirname(__file__), "config.json")
data = {}
if(os.path.isfile(config)):

    with open(config) as json_data_file:
        data = json.load(json_data_file)
    print(data)
    setup_trackbars("HSV")
else:
    setup_trackbarsDefault("HSV")


socket=BluetoothSocket( RFCOMM )
socket.connect((args.get("address"), 5))
socket.send("info")
data = socket.recv(1024)
print 'BT-Server:', `data`

# keep looping
while True:
    startTime = time.time()
    # grab the current frame
    (grabbed, frame) = camera.read()

    # if we are viewing a video and we did not grab a frame,
    # then we have reached the end of the video
    if args.get("video") and not grabbed:
        break

    # resize the frame, blur it, and convert it to the HSV
    # color space
    frame = imutils.resize(frame, width=600)
    # blurred = cv2.GaussianBlur(frame, (11, 11), 0)
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)


    v1_min, v2_min, v3_min, v1_max, v2_max, v3_max = get_trackbar_values("HSV")
    # construct a mask for the color "green", then perform
    # a series of dilations and erosions to remove any small
    # blobs left in the mask
    inRangeMask = cv2.inRange(hsv, (v1_min, v2_min, v3_min), (v1_max, v2_max, v3_max))
    erodeMask = cv2.erode(inRangeMask, None, iterations=2)
    dilateMask = cv2.dilate(erodeMask, None, iterations=1)


    # find contours in the mask and initialize the current
    # (x, y) center of the ball
    cnts = cv2.findContours(dilateMask.copy(), cv2.RETR_EXTERNAL,
        cv2.CHAIN_APPROX_SIMPLE)[-2]
    center = None

    # only proceed if at least one contour was found
    if len(cnts) > 0:
        # find the largest contour in the mask, then use
        # it to compute the minimum enclosing circle and
        # centroid
        c = max(cnts, key=cv2.contourArea)
        ((x, y), radius) = cv2.minEnclosingCircle(c)
        M = cv2.moments(c)
        center = (int(M["m10"] / M["m00"]), int(M["m01"] / M["m00"]))

        # only proceed if the radius meets a minimum size
        if radius > 10:
            # draw the circle and centroid on the frame,
            # then update the list of tracked points
            cv2.circle(frame, (int(x), int(y)), int(radius),
                (0, 255, 255), 2)
            cv2.circle(frame, center, 5, (0, 0, 255), -1)
            cv2.circle(inRangeMask, (int(x), int(y)), int(radius),
                (30,128,255), 2)
            cv2.circle(inRangeMask, center, 5, (180, 255, 50), -1)
            cv2.circle(erodeMask, (int(x), int(y)), int(radius),
                (30,128,255), 2)
            cv2.circle(erodeMask, center, 5, (180, 255, 50), -1)
            cv2.circle(dilateMask, (int(x), int(y)), int(radius),
                (30,128,255), 2)
            cv2.circle(dilateMask, center, 5, (180, 255, 50), -1)

            radiuses.appendleft(radius)
            radiusAvg = np.mean(radiuses)
            cv2.putText(frame,"Radius: {:0.4f}".format(radiusAvg), (X_TEXT,20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, 255) #Draw frameTimeAVG text

    # update the points queue
    pts.appendleft(center)

    # loop over the set of tracked points
    for i in xrange(1, len(pts)):
        # if either of the tracked points are None, ignore
        # them
        if pts[i - 1] is None or pts[i] is None:
            continue

        # otherwise, compute the thickness of the line and
        # draw the connecting lines
        thickness = int(np.sqrt(args["buffer"] / float(i + 1)) * 2.5)
        cv2.line(frame, pts[i - 1], pts[i], (0, 0, 255), thickness)

    frameTime = time.time() - startTime
    frameTimes.appendleft(frameTime)

    frameTiemAvg = np.mean(frameTimes)
    cv2.putText(frame,"FrameTime: {:0.4f} ms".format(frameTiemAvg), (X_TEXT,40), cv2.FONT_HERSHEY_SIMPLEX, 0.5, 255) #Draw frameTimeAVG text
    cv2.putText(frame,"FPS: {:0.2f}".format(1/frameTiemAvg), (X_TEXT,60), cv2.FONT_HERSHEY_SIMPLEX, 0.5, 255) #Draw FPS text



    # show the frame to our screen
    cv2.imshow("Frame", frame)
    cv2.imshow("InRange", inRangeMask)
    cv2.imshow("Erode", erodeMask)
    cv2.imshow("Dilate", dilateMask)
    

    key = cv2.waitKey(1) & 0xFF

    # if the 'q' key is pressed, stop the loop
    if key == ord("q"):
        save_trackbar_values("HSV")
        break

# cleanup the camera and close any open windows
socket.close();
camera.release()
cv2.destroyAllWindows()