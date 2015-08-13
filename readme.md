#Context Sensing Plugin for Cordova*

This plugin enables the Intel® Context Sensing SDK for Cordova Android Applications.


#API

https://software.intel.com/en-us/context-sensing-sdk


#Providers Available

```
"LOCATION"
"TERMINAL_CONTEXT"
"ACTIVITY_RECOGNITION"
"INSTANT_ACTIVITY"
"AUDIO"
"PEDOMETER"
"BATTERY"
"NETWORK"
"DEVICE_POSITION"
"TAPPING"
"SHAKING"
"GESTURE_FLICK"
"GESTURE_EAR_TOUCH"
```

#Provider example values

https://software.intel.com/en-us/articles/sensing-context-states-datasheet#01devicecontextstates

###TERMINAL_CONTEXT
```
{orientation: "ORIENTATION_UNKNOWN", face: "FACE_UP"} 
```

###ACTIVITY_RECOGNITION
```
{"activities":[{"probability":0,"name":"NONE"},{"probability":0,"name":"WALKING"},{"probability":0,"name":"BIKING"},{"probability":0,"name":"RUNNING"},{"probability":0,"name":"INCAR"},{"probability":0,"name":"INTRAIN"},{"probability":0,"name":"RANDOM"},{"probability":100,"name":"SEDENTARY"}]}
```

###INSTANT_ACTIVITY
```
{mType: "SEDENTARY"} 
```

###AUDIO
```
{"audio":[{"probability":0,"name":"SPEECH"},{"probability":21,"name":"CROWD_CHATTER"},{"probability":11,"name":"MUSIC"},{"probability":45,"name":"MECHANICAL"},{"probability":20,"name":"MOTION"}],"mTimestamp":1439389273250} 
```

###PEDOMETER
```
{"partOfDay":{"night":0,"noon":0,"morning":6,"midnight":0,"evening":0,"afternoon":0},"currentSteps":6} 
```

###BATTERY
```
{"timeOnBattery":0,"remainingBatteryLife":0,"plugged":true,"level":94,"status":"CHARGING","batteryPresent":true,"temperature":27} 
```

###NETWORK
```
{"nearNetworks":[{"ssid":"","signalStrength":"VERY_LOW","securityType":"WPA2"},{"ssid":"IDPDK-6dd8","signalStrength":"VERY_LOW","securityType":"WPA2"},{"ssid":"HP-Print-B5-Officejet Pro X476dw","signalStrength":"LOW","securityType":"WPA2"},{"ssid":"CBCI-1DA1-2.4","signalStrength":"VERY_LOW","securityType":"WPA2"},{"ssid":"Guest","signalStrength":"EXCELLENT","securityType":"OPEN"},{"ssid":"Lancaster2","signalStrength":"VERY_LOW","securityType":"WPA"},{"ssid":"xfinitywifi","signalStrength":"VERY_LOW","securityType":"OPEN"},{"ssid":"NETWORK1","signalStrength":"VERY_LOW","securityType":"WPA2"}],"cellLocation":"2978,140635817,-1","signalStrength":"EXCELLENT","trafficSent":0.45,"networkType":"WIFI","roamingActive":false,"onlineTime":0,"phoneType":"GSM","securityType":"WPA2","linkSpeed":72,"trafficReceived":35.91,"ssid":"NETWORK1","ip":"fe80::ce3a:61ff:feea:d871%wlan0"} 
```

###DEVICE_POSITION
```
{"mType":"ON_DESK"} 
Object {mType: "BAG_MOTION"} 
```

###TAPPING
```
{"mType":"SINGLE_TAP"} 
```

###GESTURE_FLICK 
```
{"mType":"FLICK_UP"} 
```

###GESTURE_EAR_TOUCH
```
{"mType":"EAR_TOUCH_BACK"} 
```

#Example

See the example folder for a test app.


#License

This plugin uses code from

1) <a href="https://github.com/google/gson">https://github.com/google/gson</a> (Apache 2 License)

2) Context Sensing Plugin (context-sensing-license.txt)