# NextGen-Hydroponics-Indoor
Code for UT Austin research group NextGen Hydroponics indoor hydroponics system

Main code is contained in the sensor_controller_v1.ino file. The libraries subfolder contains third-party libraries for controlling the sensors.

10/25/20: Initial commit. Includes basic functionality such as reading sensor data and manually pressing the buttons on the irrigation timer. Still needs to be updated with accurate initial EC values and add water pump functionality via a relay system.

10/30/20: Added water pump functionality via relay. Also added potential for automated nutrient delivery.

11/8/20: Added code for the app. It is based on a Firebase database, which holds all the sensor data from the Arduino

11/20/20: Added ability to add multiple systems per user

11/24/20: Changed app to read sensor data and system IDs from the Realtime Database instead of Cloud Firestore to make it compatible with the Arduino

12/5/20: Updated the color scheme to green and white instead of black and purple.
