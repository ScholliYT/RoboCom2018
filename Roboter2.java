import lejos.nxt.*;
import lejos.util.*;
import java.util.*;

public class Roboter2 extends Roboter1 {
   private SoundSensor sound;
   private LCD display;
   private UltrasonicSensor eye;
   private TouchSensor touchSensor;
   private double RADABSTAND = 15.6;
   private double RADDURCHMESSER = 7.5;
   private LightSensor lightsensor;
   private int target;

   public Roboter2() {
      sound = new SoundSensor(SensorPort.S2);
      display = new LCD();
      eye = new UltrasonicSensor(SensorPort.S4);
      touchSensor = new TouchSensor(SensorPort.S1);
      lightsensor = new LightSensor(SensorPort.S3);
      
      calibrateLightSensor(1);
      Delay.msDelay(1000);
      this.motorL.forward();
      this.motorR.forward();
      while (true) {
         tomsLinienVerfolger();
      }
   }
   
   
   private float kp = 1.6f;
   private float ki = 0.0f;
   private float kd = 2.0f;
   
   public void tomsLinienVerfolger(){
      int target = 50;
      
      int integral = 0;
      int derivative = 0;
      int lasterror = 0;
      
      while (true) { // Solange ausführen, bis der Touchsensor gedrückt wird
         int readValue = lightsensor.readValue(); // Atuellen Wert des Lichtsensors einlesen
         
         int error = target - readValue; // Differenz zur Kante berrechnen
         if (error == 0) { // Auf der Kante
            integral = 0; // Integral zurücksetzen
         }
         
         integral += error;
         derivative = error - lasterror;
         //System.out.println("Error: " + error);
         //System.out.println("Integral: " + integral);
         //System.out.println("Derivative: " + derivative);
         
         int turn = (int)(kp * error + ki * integral + kd * derivative);
         //System.out.println(turn);
         setMotorR(200 - turn);
         setMotorL(200 + turn);
         //System.out.println((20-turn) + ":" + (20+turn));
         lasterror = error;
      }
//      setMotorR(0);
//      setMotorL(0);
//      System.out.println("Do you want to change parameters?");
//      while (!Button.ENTER.isDown() && !Button.ESCAPE.isDown());
//         
//         if (Button.ENTER.isDown()) {
////       Parameter ändern
//         changeKP();
//         changeKI();
//         changeKD();
//         tomsLinienVerfolger(); // Linienverfolger wieder starten
//      }
   }
   
   private void changeKP() {
      Delay.msDelay(500);
      System.out.println("kp: " + kp);
      while (!Button.RIGHT.isDown() && !Button.LEFT.isDown() && !Button.ENTER.isDown());
         
         if (Button.LEFT.isDown()) {
         kp -= 0.2f;
         changeKP();
      } else if (Button.RIGHT.isDown()) {
         kp += 0.2f;
         changeKP();
      }
   }
   
   private void changeKI() {
      Delay.msDelay(500);
      System.out.println("ki: " + ki);
      while (!Button.RIGHT.isDown() && !Button.LEFT.isDown() && !Button.ENTER.isDown());
         
         if (Button.LEFT.isDown()) {
         ki -= 0.00005f;
         changeKI();
      } else if (Button.RIGHT.isDown()) {
         ki += 0.00005f;
         changeKI();
      }
   }
   
   private void changeKD() {
      Delay.msDelay(500);
      System.out.println("kd: " + kd);
      while (!Button.RIGHT.isDown() && !Button.LEFT.isDown() && !Button.ENTER.isDown());
         
         if (Button.LEFT.isDown()) {
         kd -= 0.1f;
         changeKD();
      } else if (Button.RIGHT.isDown()) {
         kd += 0.1f;
         changeKD();
      }
   }
   
   public void calibrateLightSensor(int measurements) {
      ArrayList < Integer > weiß = new ArrayList < > ();
      ArrayList < Integer > schwarz = new ArrayList < > ();
      
      for (int i = 1; i <= measurements; i++) {
         Delay.msDelay(500);
         System.out.println("Bitte auf WEISZ stellen!!");
         while (!Button.ENTER.isDown());
            weiß.add(lightsensor.getNormalizedLightValue());
         System.out.println("Weiss: " + weiß.get(i - 1));
      }
      for (int i = 1; i <= measurements; i++) {
         Delay.msDelay(500);
         System.out.println("Bitte auf SCHWARZ stellen!!");
         while (!Button.ENTER.isDown());
            schwarz.add(lightsensor.getNormalizedLightValue());
         System.out.println("Schwarz: " + schwarz.get(i - 1));
      }
      int mitteweiß = 0;
      for (int x: weiß) {
         mitteweiß += x;
      }
      lightsensor.setHigh(mitteweiß / measurements);
      
      int mitteschwarz = 0;
      for (int x: schwarz) {
         mitteschwarz += x;
      }
      lightsensor.setLow(mitteschwarz / measurements);
   }

   public void linienVerfolger() {
      this.setMotorR(150);
      long y = System.currentTimeMillis();
      while (lightsensor.readValue() <= 50) {
         setMotorL(325);
      }
      
      this.setMotorL(150);
      y = System.currentTimeMillis();
      while (lightsensor.readValue() > 50) {
         setMotorR(325);
      }
      
   }

   public void linienVerfolger3() {
      this.setMotorR(150);
      this.setMotorL(150);
      long startTime = System.currentTimeMillis();
      while (lightsensor.readValue() <= 50) {
         setMotorL((int)(System.currentTimeMillis() - startTime) / 5 + 150);
         System.out.println("Links");
      }
      Delay.msDelay(50);
      this.setMotorL(150);
      startTime = System.currentTimeMillis();
      while (lightsensor.readValue() > 50) {
         setMotorR((int)(System.currentTimeMillis() - startTime) / 5 + 150);
      }
      startTime = System.currentTimeMillis();
      this.setMotorR(150);
      while (lightsensor.readValue() <= 50) {
         setMotorR((int)(System.currentTimeMillis() - startTime) / 5 + 150);
         System.out.println("Rechts");
      }
      Delay.msDelay(50);
      this.setMotorR(150);
      startTime = System.currentTimeMillis();
      while (lightsensor.readValue() > 50) {
         setMotorL((int)(System.currentTimeMillis() - startTime) / 5 + 150);
      }
   }



   public void staubsauger() {
      Random r = new Random();
      Delay.msDelay(100);
      this.setMotorR(250);
      this.setMotorL(250);
      while (lightsensor.readValue() <= 50);
         motorR.setSpeed(0);
      motorL.setSpeed(0);
      
      rotateROBAufDerStelle((30 + r.nextInt(90)), 150);
   }


   public void spiel() {
      Delay.msDelay(1000);
      Random r = new Random();
      long time = r.nextInt(10);
      Sound.playTone(1000, 1000, 100);
      Delay.msDelay(time * 1000);
      Sound.playTone(1000, 1000, 100);
      long startTime = System.currentTimeMillis();
      while (!touchSensor.isPressed()) {
         
      }
      long passedTime = System.currentTimeMillis() - startTime;
      Sound.playTone(1000, 1000, 100);
      System.out.println("Time Diff: " + (time * 1000d - passedTime));
      
   }

   public void raumVermessen() {
      Delay.msDelay(1000);
      
      this.setMotorR(750);
      this.setMotorL(750);
      
      while (eye.getDistance() > 40);
         
         motorR.setSpeed(0);
      motorL.setSpeed(0);
      Delay.msDelay(1000);
      
      rotateROBAufDerStelle(180, 80);
      
      motorR.setSpeed(100);
      motorL.setSpeed(100);
      
      motorR.backward();
      motorL.backward();
      
      while (!touchSensor.isPressed());
         
         motorR.setSpeed(0);
      motorL.setSpeed(0);
      
   }

   public void bisvordiewand() {
      this.setMotorR(360);
      this.setMotorL(360);
      while (eye.getDistance() > 40);
         this.aufDerStelleDrehen();
      this.setMotorR(360);
      this.setMotorL(360);
      Delay.msDelay(2000);
      this.setMotorR(0);
      this.setMotorL(0);
      return;
   }

   public void rotateTest() {
      while (true) {
         for (int i = 0; i < 4; i++) {
            rotateROB(90);
            Delay.msDelay(200);
         }
         Delay.msDelay(5000);
      }
   }

   public void rotateROB(int alpha) {
      int degree = (int)((2 * RADABSTAND * alpha) / RADDURCHMESSER);
      motorR.setSpeed(360);
      motorL.setSpeed(0);
      motorR.rotate(degree);
   }

   public void rotateROBAufDerStelle(int alpha) {
      int degree = (int)((2 * RADABSTAND * alpha) / RADDURCHMESSER);
      motorR.setSpeed(100);
      motorL.setSpeed(100);
      motorR.rotate(degree / 2, true);
      motorL.rotate((-degree) / 2);
      
   }

   public void rotateROBAufDerStelle(int alpha, int speed) {
      int degree = (int)((2 * RADABSTAND * alpha) / RADDURCHMESSER);
      motorR.setSpeed(speed);
      motorL.setSpeed(speed);
      motorR.rotate(degree / 2, true);
      motorL.rotate((-degree) / 2);
      
   }

   public void inDerKiste() {
      Delay.msDelay(1000);
      this.setMotorR(360);
      this.setMotorL(360);
      while (true) {
         if (eye.getDistance() < 30) {
            rotateROB(90);
            
            Delay.msDelay(300);
            this.setMotorR(360);
            this.setMotorL(360);
         } else if (Button.ESCAPE.isDown() == true) {
            return;
         }
      }
   }

   public void anpirschen() {
      Delay.msDelay(2000);
      int y = 500;
      this.setMotorR(y);
      this.setMotorL(y);
      while (eye.getDistance() > 75);
         while (eye.getDistance() < 75 && eye.getDistance() > 25) {
         y = (eye.getDistance() - 25) * 10;
         this.setMotorR(y);
         this.setMotorL(y);
      }
      this.setMotorR(0);
      this.setMotorL(0);
      Delay.msDelay(5000);
      this.setMotorR(100);
      this.setMotorL(100);
      this.setMotorRB();
      this.setMotorLB();
      while (eye.getDistance() < 75) {
         y = (eye.getDistance() - 25) * 10;
         this.setMotorR(y);
         this.setMotorL(y);
         this.setMotorRB();
         this.setMotorLB();
      }
      this.aufDerStelleDrehen();
      this.setMotorR(500);
      this.setMotorL(500);
      Delay.msDelay(4000);
      return;
   }

   public void kollisionsAlarm() {
      int aF;
      int aV;
      int aD;
      while (Button.ESCAPE.isDown() == false) {
         if (eye.getDistance() <= 70) {
            aF = (71 - eye.getDistance()) * 40;
            aV = (71 - eye.getDistance()) * 20;
            aD = eye.getDistance() * 15;
            Sound.playTone(aF, aD, aV);
            if (Button.ESCAPE.isDown() == true) {
               return;
            }
            Delay.msDelay(aD + 200);
         }
         display.drawString(eye.getDistance() + "      ", 0, 1);
      }
   }

   public void messen() {
      Delay.msDelay(800);
      double dist = 0;
      this.setMotorR(360);
      this.setMotorL(360);
      double t = 0;
      while (sound.readValue() < 90) {
         t = System.currentTimeMillis() / 1000.;
         if (eye.getDistance() < 30) {
            dist = dist + ((t - System.currentTimeMillis() / 1000.) * 17.5925);
            t = System.currentTimeMillis() / 1000.;
            this.aufDerStelleDrehen();
            this.setMotorR(360);
            this.setMotorL(360);
         } else {
            dist = dist + ((t - System.currentTimeMillis() / 1000.) * 17.5925);
         }
         display.drawString((dist * -1) + "   ", 0, 1);
         t = System.currentTimeMillis() / 1000.;
      }
      dist = dist + ((t - System.currentTimeMillis() / 1000.) * 17.5925);
      this.setMotorR(0);
      this.setMotorL(0);
      display.drawString((dist * -1) + "   ", 0, 1);
      while (Button.ESCAPE.isDown() == false);
         
         }

   public static void main(String[] args) {
      //      new Roboter1();
      new Roboter2();
   }

   public void klatschen() {
      Delay.msDelay(800);
      this.setMotorR(360);
      this.setMotorL(360);
      while (sound.readValue() < 90) {
         //         this.setMotorR(360);
         //         this.setMotorL(360);
         
      }
      
      this.aufDerStelleDrehen();
      
      this.setMotorR(360);
      this.setMotorL(360);
      
      while (sound.readValue() < 90) {
         //         this.setMotorR(360);
         //         this.setMotorL(360);
      }
      
      this.setMotorR(0);
      this.setMotorL(0);
   }

   public void klatschen2() {
      Delay.msDelay(1000);
      while (Button.ENTER.isDown() == false) {
         this.setMotorR(360);
         this.setMotorL(360);
         
         while (sound.readValue() < 90) {
            if (Button.ENTER.isDown()) {
               return;
            }
         }
         this.aufDerStelleDrehen();
         
      }
   }

   public void klatschen3() {
      Delay.msDelay(800);
      while (true) {
         this.setMotorR(360);
         this.setMotorL(360);
         while (sound.readValue() < 90) {
            
         }
         this.setMotorR(0);
         this.setMotorL(0);
         Delay.msDelay(500);
         long x = System.currentTimeMillis();
         while ((System.currentTimeMillis() - x) <= 800) {
            if (sound.readValue() > 90) {
               return;
            }
         }
         this.aufDerStelleDrehen();
      }
   }

   public void lautstaerkeMessung() {
      int max = 00;
      display.drawString("ENTER: RESET", 0, 6);
      display.drawString("ESCAPE: END", 0, 7);
      while (Button.ESCAPE.isDown() == false) {
         display.drawString("Aktuell: " + sound.readValue() + "   ", 0, 0);
         if (sound.readValue() > max) {
            max = sound.readValue();
            display.drawString("Max: " + max + "   ", 0, 1);
         }
         if (Button.ENTER.isDown() == true) {
            max = 0;
            Delay.msDelay(100);
         }
         LCD.clear(0);
         
      }
   }


   public void linienVerfolger2() {
      this.setMotorR(250);
      this.setMotorL(250);
      while (lightsensor.readValue() <= 50);
         long x = System.currentTimeMillis();
      
      while (lightsensor.readValue() > 50) {
         setMotorR((int)(System.currentTimeMillis() - x) / 10 + 300);
      }
   }

   public void linienVerfolger22() {
      int avg = lightsensor.getHigh() + lightsensor.getLow() / 2;
      this.setMotorR(250);
      this.setMotorL(250);
      while (true) {
         int measure = lightsensor.readValue();
         if (measure > avg) {
            this.setMotorL(300);
         } else if (measure < avg) {
            
            this.setMotorR(300);
         }
      }
   }



}