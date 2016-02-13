/**

 *  Window Blinds Controlled by Particle Photon

 *  Author: Brian Anderson

 *  Date: 2016-02-11
 
 *  Version 1.0

 */
preferences {
  input("deviceId", "text", title: "Device ID")
  input("token", "text", title: "Access Token")
  input("openThresh", "text", title: "Open Threshold", defaultValue: 00, required: false, displayDuringSetup: true) //default value is 10
  input("open", "text", title: "Open", defaultValue: 99, required: false, displayDuringSetup: true) //default value is 99
  input("closed", "text", title: "Closed", defaultValue: 20, required: false, displayDuringSetup: true) //default value is 20
}
metadata {
  definition (name: "Dual Photon Blinds", namespace: "a6pack", author: "Brian Anderson") {
    capability "Actuator"
    capability "Window Shade"
    capability "Sensor"
    capability "Switch"
    capability "Switch Level"
    // capability "Refresh"
    // capability "Polling"

    attribute "openThresh", "string"
    attribute "open", "string"
    attribute "closed", "string"
    attribute "opening", "string"
    attribute "closing", "string"
    attribute "partially open", "string"
    attribute "presetPosition", "string"

    command "setPosition"
    command "getPosition"
    //TODO
    //command "howSunny"
  }
//TODO: simulator
//	simulator {
	//	status "open": "command: 9881, payload: 00 62 03 FF 00 00 FE FE"
	//	status "closed": "command: 9881, payload: 00 62 03 00 00 00 FE FE"
//
//		reply "9881006201FF,delay 4200,9881006202": "command: 9881, payload: 00 62 03 FF 00 00 FE FE"
//		reply "988100620100,delay 4200,9881006202": "command: 9881, payload: 00 62 03 00 00 00 FE FE"
//	}
  
  
  
  // tile definitions
  tiles(scale:2) {
    
      multiAttributeTile(name:"WindowBlind", type:"generic", width:2, height:2) {
         tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
            
            state "closed", label:'Closed', action:"switch.on", icon:"st.doors.garage.garage-closed", backgroundColor:"#79b821", nextState:"opening"
            state "open", label:'Open', action:"switch.off", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e", nextState:"closing"
            state "opening", label:'${name}', icon:"st.doors.garage.garage-opening", backgroundColor:"#ffe71e"
            state "closing", label:'${name}', icon:"st.doors.garage.garage-closing", backgroundColor:"#ffe71e"
  }
  //TODO - Add Up andDown arrows for open/close in increments
   //  tileAttribute("device.windowShade", key: "VALUE_CONTROL") {
   // attributeState("default", action: "setPosition")
   // }
   }
  }
      //TODO - Change switch.on to windowShade.on - be consistant and get rid of reference to switch
    
    standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }
    controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
      state "level", action:"switch level.setLevel"
    }
    main(["switch"])
    details(["WindowBlind", "levelSliderControl"])
  }


def parse(String description) {
  log.error "This device does not support incoming events"
  return null
}

def on() { 
  put'20' //was 20
  sendEvent(name: 'switch', value: 'open')
}
def off() {
  put'99' //was 99
  sendEvent(name: 'switch', value: 'closed')
}
def setLevel(val) {
  def level = Math.min(val as Integer, 99) //was 99
  if(level>80){ // was 80
    sendEvent(name: 'switch', value: 'closed')
  }
  else{
    sendEvent(name: 'switch', value: 'open')
  }
  put val
}

def getLevel(){ //TODO
}

def poll() { //TODO
}

def refresh() { //TODO
}

private put(level) {
//Spark Core API Call
  sendEvent(name:"level",value:level)
  sendEvent(name:"switch.setLevel",value:level) 
  httpPost(
    uri: "https://api.spark.io/v1/devices/${deviceId}/setstate",
    body: [access_token: token, command: level],
  ) {response -> log.debug (response.data)}
}