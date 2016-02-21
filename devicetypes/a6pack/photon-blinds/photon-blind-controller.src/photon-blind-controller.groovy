/**

 *  Particle Photon

 *

 *  Author: Brian Anderson

 *  Date: 2016-02-19

 */

preferences {
  input("deviceId", "text", title: "Device ID")
  input("token", "text", title: "Access Token")
  input("openThresh", "text", title: "Open Threshold", defaultValue: 10, required: false, displayDuringSetup: true) //default value is 10
  input("open", "text", title: "Open", defaultValue: 99, required: false, displayDuringSetup: true) //default value is 99
  input("closed", "text", title: "Closed", defaultValue: 20, required: false, displayDuringSetup: true) //default value is 20
}
// for the UI
metadata {
  // Automatically generated. Make future change here.
  definition (name:"Photon Blind Controller", namespace:"a6pack/Photon_Blinds", author:"Brian Anderson") {
    capability "Actuator"
    capability "Window Shade"
    capability "Sensor"
    capability "Switch Level"
    capability "Switch"
    capability "Refresh"
    capability "Polling"

    attribute "openThresh", "string"
    attribute "open", "string"
    attribute "closed", "string"
    attribute "photoresistor", "string"

    command "setLevel"
    command "photoresistor"
  //  command "getLevel"
  }
  
//TODO: command "howSunny" - determine LUX via photoresistor

	simulator {
	//Nothing happening here right now	
  //	status "open": "command: 9881, payload: 00 62 03 FF 00 00 FE FE"
  //	status "closed": "command: 9881, payload: 00 62 03 00 00 00 FE FE"
 //
  //	reply "9881006201FF,delay 4200,9881006202": "command: 9881, payload: 00 62 03 FF 00 00 FE FE"
  //	reply "988100620100,delay 4200,9881006202": "command: 9881, payload: 00 62 03 00 00 00 FE FE"
}

  // tile definitions
  tiles {
    standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: false) {
      state "off", label:'closed', action:"switch.on", icon:"st.doors.garage.garage-closed", backgroundColor:"#79b821", nextState:"opening"
      state "on", label:'open', action:"switch.off", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e", nextState:"closing"
      state "opening", label:'${name}', icon:"st.doors.garage.garage-opening", backgroundColor:"#ffe71e"
      state "closing", label:'${name}', icon:"st.doors.garage.garage-closing", backgroundColor:"#ffe71e"
    }
    standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }
    controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
      state "level", action:"switch level.setLevel"
    }
    valueTile("photoresistor", "device.photoresistor", width: 1, height: 1){
            state "default", label:'${currentValue} x',
				backgroundColors:[
					[value: 5, color: "#153591"],
					[value: 100, color: "#1e9cbb"],
					[value: 200, color: "#90d2a7"],
					[value: 500, color: "#44b621"],
					[value: 1000, color: "#f1d801"],
					[value: 2000, color: "#d04e00"],
					[value: 3000, color: "#bc2323"]
			]
}

    main(["switch"])
    details(["switch", "refresh", "photoresistor", "levelSliderControl"])
  }
}

def parse(String description) {
  log.error "parse not supported"
  return null
}

def on() { 
  put'20' //was 20
  sendEvent(name: 'switch', value: 'on')
}
def off() {
  put'99' //was 99
  sendEvent(name: 'switch', value: 'off')
}
def setLevel(val) {
  def level = Math.min(val as Integer, 99) //was 99
  if(level>80){ // was 80
    sendEvent(name: 'switch', value: 'off')
  }
  else{
    sendEvent(name: 'switch', value: 'on')
  }
  put val
}

//def getLevel(){ //TODO
//}

def photoresistor() {
	log.debug "Executing 'photoresistor'"
    getPhotoresistor()
}

def refresh() { //TODO
	log.debug "Executing 'refresh'"
}

private put(level) {
//Particle Photon API Call
  sendEvent(name:"level",value:level)
  sendEvent(name:"switch.setLevel",value:level) 
  httpPost(
    uri: "https://api.spark.io/v1/devices/${deviceId}/setstate",
    body: [access_token: token, command: level],
  ) {response -> log.debug (response.data)}
}

// Get the Photoresistor Value
private getPhotoresistor() {
//Particle Photon API Call
    def photoresistorClosure = { response ->
	  	log.debug "Photoresistor Request was successful, $response.data"
      	sendEvent(name: "photoresistor", value: response.data.return_value)
	}
    def photoresistorParams = [
  		uri: "https://api.spark.io/v1/devices/${deviceId}/getLightValue",
        body: [access_token: token],  
        success: photoresistorClosure
	]
	httpPost(photoresistorParams)
}