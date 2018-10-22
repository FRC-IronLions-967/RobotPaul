# Robot Paul

Robot Paul is code writen in the off season by team first robotics team 967 out of Linn-Mar robotics.

Robot Paul is a envirement made with the purpose of conecting all of the expermential code parts together like during a normal season.

# Parts of the code

## RoboRio

Code that gets deployed onto the roborio. Uses gradle to build code

### To deploy robotcode onto the robot step by step

1. Download the github repository
2. Unzip the download
3. Open folder called RoboRio in file explorer
4. Open powershell in the directory by typing "powershell" in the path (C:\Users\Matt Behnke\Downloads\RobotPaul-master\RobotPaul-master)
5. Connect to the robots wifi network, conect using ethernet, or conect using USB
6. Type ./gradlew deploy if all go well the code will be deployed to the robot

## RoboLoger

A logger that runs on the drivers station. The logger uses pynetworktables to comunicate with the robot and lissens for an array wiht the time stamp. The Logger then writes the values to a json file.

The advantages of this include

1. On robot shutdown the logger does not so the file never gets damaged.
2. The json file does not need to be retrived from the roborio.
3. There is limmited space on the roborio so keeping the logs on the driver station does not let the roborio fill up wiht useless logs.

Not used during a match but could be the data is only useful for debuging and understanding what happend to the robot.

## DashBoard

Our dashboard is an electron javascript app that is used to display the camera from the robot and update what auto to run along with many other values being displayed.

This was based and exteded from FRC Dashboard.

Used during comption

## VisionPI

VisionPI is our raspberry pi coprosser that runs python code.

VisionPI will run opencv for the image processing.

VisionPI uses pynetworktables to comunicate with the roborio.

Used during comption