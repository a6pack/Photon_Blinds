/**

 *  Window Blind Tilt Control

 *  Particle Photon

 *  Author: Brian Anderson
 * Modified from Code by Justin Wurth

 *  Date: Feb 11.2016

 */


// Pin Declarations - Servo Related
#define SERVO_1       A4  //PWM signal is only on pins D0, D1, D2, D3, A4, A5, WKP, RX, and TX
#define SERVO_2       A5  //Provides PWM for a second servo
#define SWITCH_PIN    D0  //define pin used to connect the switch

//set up the servo objects
Servo myservo1;  //Initial position settings for first servo
int state1;
int open1=0; 
int closed1=150; 

Servo myservo2;  //Initial position settings for second servo
int state2;
int open2=0; 
int closed2=170; 

//photoresistor variables
int STEADY_POWER =  A3;  //define pin used to provide a steady 3.3v to the photoresistor
int PHOTORESISTOR = A0;  //define pin used to ultimately determine level of light.  
                             //measures difference between voltage measured on pin A0 and STEADY_POWER. 
int LightValue; //used to store the value of the photoresistor

// Functions
int setState(String command); 

// Setup Function

void setup(){
  // RGB.control(true);   Insert if you want to control the RGB LED
  // RGB.color(255,255,255);   Turn the LED to the color White
  // RGB.brightness(0);  Turn the brightness to zero
//myservo1.attach(A4);  //First Servo attached to pin A4
  myservo1.attach(SERVO_1); // First Servo attaches on analog pin A4 to the servo object
  myservo2.attach(SERVO_2); //Second servo attaches on analog pin A5 to the servo object
  Particle.function("setstate", setState);
  Particle.variable("getstate", &state1, INT);
  Particle.function("setstate", setState);
  Particle.variable("getstate", &state2, INT);
  state1=myservo1.read(); // Determine the current position of Servo1
  state2=myservo2.read(); // Determine the current Position of Servo2
  pinMode(SWITCH_PIN,  INPUT_PULLUP);
  
  
  //photoresistor code
  //First - declare all pins to determine which are outputting voltages and which are reading voltages

  
  pinMode(PHOTORESISTOR,INPUT); //the pin reading the changes in values due to changes in light is set as INPUT
  pinMode(STEADY_POWER,OUTPUT); //the pin powering the photoresistor is providing consistent power and is OUTPUT
  
  digitalWrite(STEADY_POWER,HIGH); //write the power pin high as this will be used for power
  
  //delcare a Particle.variable() to allow for reading of the photoresistor value from the cloud

  Particle.variable("LightValue", LightValue);
  //Particle.variable("analogLightValue",&analogLightValue, INT); //This will reference the variable analogvalue in 
  //this app, which is an integer variable.
  
}

// Loop Function
void loop(){

  if(!digitalRead(SWITCH_PIN)){ // Execute IF momentary button is pressed
    delay(50);
// myservo1.attach(A4);
    myservo1.attach(SERVO_1);  // First Servo attaches on analog pin A4 to the servo object 
    myservo2.attach(SERVO_2);  //Second servo attaches on analog pin A5 to the servo object 
    state1=(state1)?open1:closed1; // Toggle between fully open and closed
    state2=(state2)?open1:closed2; // Toggle between fully open and closed
    myservo1.write(state1); // Send the position to Servo1
    myservo2.write(state2); // Send the position to Servo2
    delay(500);
    analogWrite(SERVO_1,0); // Comment to silence the buzzing noise on Servo1
    analogWrite(SERVO_2,0); // Comment to silence the buzzing noise on Servo2
    
    // TODO: Status
    
  }
  
  while(!digitalRead(SWITCH_PIN)); 
//Nothing to see here... continue with program! 
   delay(500);
   LightValue = 0;
   LightValue = analogRead(PHOTORESISTOR);
   delay(100); // delay 100ms
}

int setState(String command) //the actual string coming from SmartThings (e.g., open, close)
  { 
    //First, check the value of the photoresistor and store it
  // delay(500);
   //analogLightValue = analogRead(PHOTORESISTOR);
   //delay(100); // delay 100ms  
  // Particle.publish("photoresistor","reading taken",60,PRIVATE);
//now - attach the myservo object to the servo control pins    
    myservo1.attach(SERVO_1);
    myservo2.attach(SERVO_2);
    state1 = command.toInt(); 
    state1 = map (state1, 0, 99, open1, closed1); 
    state2 = command.toInt(); 
    state2 = map (state2, 0, 59, open1, closed2); 
    myservo1.write(state1); // Send the position to Servo1
    myservo2.write(state2); // Send the position to Servo2
    delay(500);
    analogWrite(SERVO_1,0); // Command to silence the buzzing noise on Servo1
    analogWrite(SERVO_2,0); // Command to silence the buzzing noise on Servo2
    return 1;
   }