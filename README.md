# Sensel Notebook
This is a hackathon project that transforms the Sensel Morph into a track pad that connects with Android devices via Bluetooth, supporting various different features. 

<h2>Features</h2>
<h4>Change Colors</h4>

<h4>Undo Changes</h4>

<h4>Save Notes</h4>


<h2>Setup</h2>
Bluetooth connection is not yet supported on Sensel Morph, so we wrote a Python application that transmits bluetooth data to our Android device. However, the data being transmitted is not modified or analyzed, it is purely the raw data being sent from Sensel Morph. This implies if one day the developer at Sensel Morph decides to support direct bluetooth connection, our Android mobile app can be used directly without any modification.
<h4>Step1: install Python on PC side</h4>
In order to use this API, please install Python (version 2.7 or later) on your machine. You must also install pySerial to allow communication with the sensor. After cloning the project, go to [this line ](https://github.com/gges5110/HackTX2015Sensel/blob/master/SenselUSB2Bluetooth/connect_bluetooth.py#L10) and change the target_name to your device's bluetooth name.

<h4>Step2: Run Android Code</h4>
The Android program is developed with Android Studio with Gradle. The compile SDK Version is 21. To run our mobile app, simply open an existing project under the HackTX2015Sensel/SenselNotebook/ directory and hit run.

