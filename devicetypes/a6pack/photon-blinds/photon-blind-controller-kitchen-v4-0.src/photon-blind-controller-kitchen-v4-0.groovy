/**
 *  Particle Photon
 *
 *  Author: Brian Anderson
 *  Date: 2016-03-12
 *  This version using multiAttributeTile
 *  It is intended to be paired with Photon code that does not contain the photoresistor
 *  THIS VERSION WORK WITH FUTABA S3004 SERVOS
 *
 */

preferences {
  input("LUXdeviceId", "text", title: "LUX Device ID") //device ID of Photon connected to Photoresistor
  input("LUXtoken", "text", title: "LUX Access Token") //Access Token of Photon connected to Photoresistor
  input("deviceId", "text", title: "Device ID")
  input("token", "text", title: "Access Token")
  input("openThresh", "text", title: "Open Threshold", defaultValue: 20, required: false, displayDuringSetup: true) //default value is 10
  input("open", "text", title: "Open", defaultValue: 20, required: false, displayDuringSetup: true) //default value is 99
  input("closed", "text", title: "Closed", defaultValue: 170, required: false, displayDuringSetup: true) //default value is 20
}
// for the UI
metadata {
  // Automatically generated. Make future change here.
  definition (name:"Photon Blind Controller -KITCHEN_V4.0", namespace:"a6pack/Photon_Blinds", author:"Brian Anderson") {
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
    attribute "tiltUp", "string"
    attribute "tiltDown", "string"
    attribute "photoresistor2", "string"
 

    command "setLevel"
    command "photoresistor"
    command "tiltUp"
    command "tiltDown"
    command "photoresistor2"

  }
  
	simulator {
	//Nothing happening here right now	
  //	status "open": "command: 9881, payload: 00 62 03 FF 00 00 FE FE"
  //	status "closed": "command: 9881, payload: 00 62 03 00 00 00 FE FE"
 //
  //	reply "9881006201FF,delay 4200,9881006202": "command: 9881, payload: 00 62 03 FF 00 00 FE FE"
  //	reply "988100620100,delay 4200,9881006202": "command: 9881, payload: 00 62 03 00 00 00 FE FE"
}

// tile definitions
tiles(scale: 2) {
    multiAttributeTile(name:"status", type: "generic", width: 6, height: 2){
        tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
            attributeState "on", label:'Open', action:"switch.off", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e", nextState:"closing"
            attributeState "off", label:'Closed', action:"switch.on", icon:"st.doors.garage.garage-closed", backgroundColor:"#79b821", nextState:"opening"
            attributeState "opening", label:'${name}', icon:"st.doors.garage.garage-opening", backgroundColor:"#ffa81e"
            attributeState "closing", label:'${name}', icon:"st.doors.garage.garage-closing", backgroundColor:"#79b821"
     	   }
		tileAttribute ("device.photoresistor2", key: "SECONDARY_CONTROL") {
			attributeState "photoresistor", label:'Light is: ${currentValue}'
			}
//			tileAttribute("device.switch", key: "SECONDARY_CONTROL") {
//            	attributeState "default", label:"test"
//			}
//			tileAttribute("device.switch", key: "SLIDER_CONTROL") {
//    			attributeState "level", action:"switch level.setLevel"
//			}
}
   standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: false) {
      state "off", label:'closed', action:"switch.on", icon:"st.doors.garage.garage-closed", backgroundColor:"#79b821", nextState:"opening"
      state "on", label:'open', action:"switch.off", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e", nextState:"closing"
      state "opening", label:'${name}', icon:"st.doors.garage.garage-opening", backgroundColor:"#ffe71e"
      state "closing", label:'${name}', icon:"st.doors.garage.garage-closing", backgroundColor:"#ffe71e"
}
	valueTile("getLUXvalue", "device.getLUXvalue", width: 2, height: 2){
    	state "default", label: 'Light is ${currentValue}', backgroundColor:"#ffa81e"
        }

	standardTile("tiltup", "device.tiltup", width: 2, height: 2, canChangeIcon: false) {
	  state "tiltup", label:'Tilt \u2191', action:"tiltUp", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e"
}
	standardTile("tiltdown", "device.tiltdown", width: 2, height: 2, canChangeIcon: false) {
	  state "tiltup", label:'Tilt \u2193', action:"tiltDown", icon:"st.doors.garage.garage-open", backgroundColor:"#ffa81e"
}
	standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
      state "default", label:'Get Light Level', action:"refresh.refresh", icon:"st.Weather.weather14"
    }
    controlTile("levelSliderControl", "device.level", "slider", height: 2, width: 4, inactiveLabel: false, range:"(0..180)") {
      state "level", label: 'Manually Set Angle', action:"switch level.setLevel"
    }
    valueTile("photoresistor", "device.photoresistor", width: 2, height: 2){
            state "default", label:'Light is ${currentValue}',
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
    details(["status", "refresh", "levelSliderControl", "photoresistor"])
  }
}

def parse(String description) {
  log.error "parse not supported"
  return null
}

def on() { 
  put'5' //was 20
  sendEvent(name: 'switch', value: 'on')
}


def tiltUp(){
sendEvent(name: 'switch', value: 'do we get here')
	put'0'
}

def tiltDown(){
    put'140'
    sendEvent(name: 'switch', value: 'tiltdown')
}

def off() {
  put'179' //was 99
  sendEvent(name: 'switch', value: 'off')
}

def setLevel(val) {
  def level = Math.min(val as Integer, 99) //was 99
  if(level>160){ // was 80
    sendEvent(name: 'switch', value: 'off')
  }
  else{
    sendEvent(name: 'switch', value: 'on')
  }
  put val
}

def photoresistor() {
	log.debug "Executing 'photoresistor'"
//    getLUXvalue()
}

def refresh() { //TODO
	log.debug "Executing 'refresh'"
    getLUXvalue()
}

private put(level) {
//Particle Photon API Call
  sendEvent(name:"level",value:level)
  sendEvent(name:"switch.setLevel",value:level) 
  httpPost(
    uri: "https://api.spark.io/v1/devices/${deviceId}/setPosition",
    body: [access_token: token, command: level],
  ) {response -> log.debug (response.data)}
}
// Get the Photoresistor Value
def getLUXvalue(){
//now - blinds will pick up LUX values from Dining Area Blinds
//  The next line of code is only used for Photons that are have a connected photoresistor
//  they will get the light level from their photoresistor.  The other Photons will pull the value from it
//	httpGet(uri: "https://api.particle.io/v1/devices/${deviceId}/LightValue?access_token=${token}",
    httpGet(uri: "https://api.particle.io/v1/devices/${LUXdeviceId}/LightValue?access_token=${LUXtoken}",
			contentType: 'application/json',)
    {resp ->           
            log.debug "resp data: ${resp.data}"
            log.debug "result: ${resp.data.result}"
		sendEvent(name: "photoresistor", value: "${resp.data.result}" )
	}
}