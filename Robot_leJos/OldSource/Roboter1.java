import lejos.nxt.*;
import lejos.util.*;


public class Roboter1
{
  protected NXTRegulatedMotor motorL ,motorR;
   
  public Roboter1()
  {
    motorL = new NXTRegulatedMotor(MotorPort.B);
    motorR = new NXTRegulatedMotor(MotorPort.C);
  }
    
  public static void main(String[] args)
  {
    new Roboter1();
  }
    
  public void setMotorR(int x)
  {
    motorR.setSpeed(x);
    //motorR.forward();
  }
    
  public void setMotorL(int x)
  {
    motorL.setSpeed(x);
    //motorL.forward();
  }
    
  public void setMotorRB()
  {
    motorR.backward();
  }
    
  public void setMotorLB()
  {
    motorL.backward();
  }
  
  public void vorwärts()
  {
    motorL.setSpeed(360);
    motorR.setSpeed(360);
    motorL.forward();
    motorR.forward();
    Delay.msDelay(4000);
    motorL.setSpeed(0);
    motorR.setSpeed(0);
  }
  
  public void eineUmdrehung()
  {
    motorL.setSpeed(180);
    motorR.setSpeed(180);
    
    motorL.rotate(360, true);
    motorR.rotate(360);
  }
    
  public void vorUndZurück()
  {
    motorL.setSpeed(180);
    motorR.setSpeed(180);
    motorL.forward();
    motorR.forward();
    Delay.msDelay(3000);
    motorL.setSpeed(0);
    motorR.setSpeed(0);
    Delay.msDelay(500);
    motorL.setSpeed(180);
    motorR.setSpeed(180);
    motorL.backward();
    motorR.backward();
  }
  
  public void kurveLinks()
  {
    motorR.setSpeed(360);
    motorL.setSpeed(360);
    motorR.forward();
    motorL.backward();
    Delay.msDelay(390);
    motorR.setSpeed(0);
    motorL.setSpeed(0);
  }
  
  public void kurveRechts()
  {
    motorR.setSpeed(360);
    motorL.setSpeed(0);
    motorR.backward();
    motorL.forward();
    Delay.msDelay(400);
    motorR.setSpeed(0);
    motorL.setSpeed(0);
  }
  
  public void aufDerStelleDrehen()
  {
    int x = 360; 
    int y = 900;        
    motorL.setSpeed(x);
    motorR.setSpeed(x);
    motorL.forward();
    motorR.backward();
    Delay.msDelay(y);
    motorL.setSpeed(0);
    motorR.setSpeed(0);
  }
  
  public void slalom(int anzahl, int dauer)
  {
    int y = dauer;
    while(anzahl > 0)
    {
      kurveRechts();
      kurveLinks();
      anzahl--;
      
    }
  }
}

