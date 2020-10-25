#include<Servo.h>
#include "DFRobot_EC.h"
#include <OneWire.h>

Servo valve; //Servo to press the valve timer buttons
#define phPin 5 //Set analong input 5 to ph
#define ecPin 0 //Set analog input 0 to ec
#define tempPin 1 //Set analog input 1 to temperature

//Servo variables
int manualButtonPos = 115; //Servo position to press the 'Manual' button on the irrigation timer
int enterButtonPos = 65; //Servo position to press the 'Enter' button on teh irrigation timer
long timerDelay = 180000; //Valve open time in milliseconds (3 min)

//EC variables
DFRobot_EC ec; //Define electrical conductivity probe
float voltage,ecValue,temperature = 25; //Define variables for the EC probe

OneWire ds(tempPin); //Define temperature probe

//Utility variables
float minPH = 5.7; //minimum pH of the water in each tank
float maxPH = 7.0; //maximum pH of the water in each tank
float baselineEC = 5; //Temporary value, will be determined once the water is in place
float ecBuffer = 0.2; //Error buffer for EC. Essentially, the EC can't go further than this value away from the initial EC.

void setup() {
  // put your setup code here, to run once:
  /*valve.attach(13);
  valve.write(90);
  delay(1000);
  valve.write(115);
  delay(1000);
  valve.write(65);
  delay(1000);
  valve.write(90);
  delay(3000);*/
  Serial.begin(9600);
  Serial.write("Ready");
}

void loop() {
  // put your main code here, to run repeatedly:
  float currentPH = getPH();
  float currentEC = getEC();

  if(currentPH < minPH || currentPH > maxPH || abs(currentEC-baselineEC) > ecBuffer){ //If the pH or EC is out of range, refresh the water in the tank
    pressTimerButtonAndWait(); //Basically presses the buttons on the timer to start the pump. The time that the valve is open will have to be determined through trial and error
  }

}

float getPH(){
  //Returns pH of the water
  int store[10];
  int temp, avgValue;

  for(int i=0;i<10;i++) //Get 10 readings and average them to get a more accurate value
    { 
      store[i]=analogRead(phPin);
      delay(10);
    }

    for(int i=0;i<9;i++) //Sort pH values
    {
      for(int j=i+1;j<10;j++)
      {
        if(store[i]>store[j])
        {
          temp=store[i];
          store[i]=store[j];
          store[j]=temp;
        }
      }
    }

    avgValue=0;
    for(int i=2;i<8;i++) //Take the average value of 6 center sample (roughly 1 standard deviation assuming a normal model)
      avgValue+=store[i];
    float phValue=(float)avgValue*5.0/1024/6; //Convert the analog input into millivolts
    phValue=3.5*phValue; //Convert the millivolts into pH value

    Serial.print("    pH:");  
    Serial.print(phValue,2);
    Serial.println(" ");

    digitalWrite(13, HIGH);       
    delay(800);
    digitalWrite(13, LOW);

    return phValue;
}

float getEC(){
  //Returns the electrical conductivity
  static unsigned long timepoint = millis();
    if(millis()-timepoint>1000U) //Time interval: 1s
    {
      timepoint = millis();
      voltage = analogRead(ecPin)/1024.0*5000; //Read the voltage
      temperature = readTemperature(); //Read temperature sensor to execute temperature compensation
      ecValue = ec.readEC(voltage,temperature); //Convert voltage to EC with temperature compensation

      Serial.print("temperature:");
      Serial.print(temperature,1);

      Serial.print("^C  EC:");
      Serial.print(ecValue,2);
      Serial.println("ms/cm");
    }
}

float readTemperature(){
  //returns the temperature in DEG Celsius

    byte data[12];
    byte addr[8];

    if ( !ds.search(addr)) {
        //no more sensors on chain, reset search
        ds.reset_search();
        return -1000;
    }

    if ( OneWire::crc8( addr, 7) != addr[7]) {
        Serial.println("CRC is not valid!");
        return -1000;
    }

    if ( addr[0] != 0x10 && addr[0] != 0x28) {
        Serial.print("Device is not recognized");
        return -1000;
    }

    ds.reset();
    ds.select(addr);
    ds.write(0x44,1); // start conversion, with parasite power on at the end

    byte present = ds.reset();
    ds.select(addr);
    ds.write(0xBE); // Read Scratchpad


    for (int i = 0; i < 9; i++) { // we need 9 bytes
      data[i] = ds.read();
    }

    ds.reset_search();

    byte MSB = data[1];
    byte LSB = data[0];

    float tempRead = ((MSB << 8) | LSB); //using two's compliment
    float TemperatureSum = tempRead / 16;

    return TemperatureSum;
}

void pressTimerButtonAndWait(){
  valve.write(manualButtonPos); //Press the 'Manual' button on the timer
  delay(1000);

  valve.write(enterButtonPos); //Press the 'Enter' button on the timer. The timer is already preprogrammed to open the valve for a specific length of time
  delay(1000);

  valve.write(90);
  delay(timerDelay); //Timer delay determined experimentally. This is how long the valve is open. The timer automatically closes the valve at the end of the time.
}