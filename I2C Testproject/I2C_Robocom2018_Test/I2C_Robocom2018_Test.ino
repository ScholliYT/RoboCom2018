#include <Wire.h>

#define RightRed 11
#define LeftRed 10
#define RightGreen 9
#define LeftGreen 6
#define RightBlue 5
#define LeftBlue 3
int ADDRESS = 0x43;   // The Arduino's I2C Address
uint8_t SensorVersion[9]  = "V0.1    ";
uint8_t SensorName[9]     = "RPiBrige";
uint8_t SensorType[9]     = "Camera  ";
byte eventRegisterAddr = 0;
byte sendbuffer[] = {0, 0, 0, 0, 0, 0, 0, 0};

const byte numChars = 32;
char receivedChars[numChars];
char tempChars[numChars];        // temporary array for use when parsing

// variables to hold the parsed data
bool valLocked = false; // set to true when data is wirrten/read
int posx = 0;
int posy = 0;

boolean blinkerOn = false;
boolean newData = false;

//============

void setup() {
  Serial.begin(9600);
  Serial.println("This script expects 2 pieces of data - integer, integer ");
  Serial.println("Enter data in this style <12, 24>  ");
  Serial.println();

  pinMode(RightRed, OUTPUT);
  pinMode(LeftRed, OUTPUT);
  pinMode(RightGreen, OUTPUT);
  pinMode(LeftGreen, OUTPUT);
  pinMode(RightBlue, OUTPUT);
  pinMode(LeftBlue, OUTPUT);

  Wire.begin(ADDRESS);
  Wire.onReceive(receiveEvent);
  Wire.onRequest(requestEvent);
  Serial.println("Request"); // Start first DataRequest

  digitalWrite(RightRed, 1);
  digitalWrite(LeftRed, 1);
  digitalWrite(RightGreen, 1);
  digitalWrite(LeftGreen, 1);
  digitalWrite(RightBlue, 1);
  digitalWrite(LeftBlue, 1);
  delay(100);
  digitalWrite(RightRed, 0);
  digitalWrite(LeftRed, 0);
  digitalWrite(RightGreen, 0);
  digitalWrite(LeftGreen, 0);
  digitalWrite(RightBlue, 0);
  digitalWrite(LeftBlue, 0);
}

//============

void loop() {
  recvWithStartEndMarkers();
  if (blinkerOn == true) {
    handleBlinker();
  }
  if (newData == true) {
    strcpy(tempChars, receivedChars);
    // this temporary copy is necessary to protect the original data
    //   because strtok() used in parseData() replaces the commas with \0
    parseData();
    newData = false;
    Serial.println("Request"); // Start new DataRequest
    delay(10); // 100HZ
  }
}

//============

void receiveEvent(int howMany) {
  if (Wire.available() > 0 ) {
    eventRegisterAddr = Wire.read(); // receive register address (1 byte)
  }
}

//============

void requestEvent() {
  if (eventRegisterAddr == 0x00) {
    Wire.write(SensorVersion, 8);
  }
  else if (eventRegisterAddr == 0x08) {
    Wire.write(SensorName, 8);
  }
  else if (eventRegisterAddr == 0x10) {
    Wire.write(SensorType, 8);
  }
  else if (eventRegisterAddr == 0x42) {
    if (!valLocked) {
      valLocked = true;
      sendbuffer[0] = (highByte(posx) & 0xF0) >> 4; // HighByte upper  4 bits
      sendbuffer[1] =  highByte(posx) & 0x0f;       // HighByte lower  4 bits
      sendbuffer[2] = (lowByte(posx)  & 0xF0) >> 4; // LowerByte upper 4 bits
      sendbuffer[3] =  lowByte(posx)  & 0x0f;       // LowerByte lower 4 bits

      sendbuffer[4] = (highByte(posy) & 0xF0) >> 4; // HighByte upper  4 bits
      sendbuffer[5] =  highByte(posy) & 0x0f;       // HighByte lower  4 bits
      sendbuffer[6] = (lowByte(posy)  & 0xF0) >> 4; // LowerByte upper 4 bits
      sendbuffer[7] =  lowByte(posy)  & 0x0f;       // LowerByte lower 4 bits
      valLocked = false;
      //Serial.print(posx); Serial.print(" "); Serial.print(posx, BIN); Serial.print("    "); Serial.print(sendbuffer[0], BIN); Serial.print(" "); Serial.print(sendbuffer[1], BIN); Serial.print(" "); Serial.print(sendbuffer[2], BIN); Serial.print(" "); Serial.println(sendbuffer[3], BIN);
      Wire.write(sendbuffer, 8);
    }
  }
  else if (eventRegisterAddr == 0x43) {
    blinkerOn = true;
     byte dummybuffer[] = {1};
    Wire.write(dummybuffer, 1);
  }
  else if (eventRegisterAddr == 0x44) {
    blinkerOn = false;
    byte dummybuffer[] = {1};
    Wire.write(dummybuffer, 1);
  }
}

//============

void recvWithStartEndMarkers() {
  static boolean recvInProgress = false;
  static byte ndx = 0;
  char startMarker = '<';
  char endMarker = '>';
  char rc;

  while (Serial.available() > 0 && newData == false) {
    rc = Serial.read();

    if (recvInProgress == true) {
      if (rc != endMarker) {
        receivedChars[ndx] = rc;
        ndx++;
        if (ndx >= numChars) {
          ndx = numChars - 1;
        }
      }
      else {
        receivedChars[ndx] = '\0'; // terminate the string
        recvInProgress = false;
        ndx = 0;
        newData = true;
      }
    }

    else if (rc == startMarker) {
      recvInProgress = true;
    }
  }
}

//============

void parseData() {      // split the data into its parts
  if (!valLocked) {
    valLocked = true;
    char * strtokIndx; // this is used by strtok() as an index

    strtokIndx = strtok(tempChars, ",");     // get the first part - the string
    posx = atoi(strtokIndx);     // convert this part to an integer

    strtokIndx = strtok(NULL, ","); // this continues where the previous call left off
    posy = atoi(strtokIndx);     // convert this part to an integer
    valLocked = false;
  }
}

//============

long blinkerTimer = 0;
boolean blinkerState = false;
void handleBlinker() {
  if (blinkerTimer + 500 < millis()) {
    blinkerState = !blinkerState;
    if (blinkerState) {
      analogWrite(RightRed, 255);
      analogWrite(RightGreen, 100);
      analogWrite(LeftRed, 255);
      analogWrite(LeftGreen, 100);
    } else {
      analogWrite(RightRed, 0);
      analogWrite(RightGreen, 0);
      analogWrite(LeftRed, 0);
      analogWrite(LeftGreen, 0);
    }

    blinkerTimer = millis();
  }
}

