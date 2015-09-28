/**
 *  My First SmartApp
 *
 *  Copyright 2015 Stephane Handfield
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "My First SmartApp",
    namespace: "handfies",
    author: "Stephane Handfield",
    description: "My first App",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

import groovy.json.JsonSlurper

preferences {
    // What door should this app be configured for?
    section ("When the door lock/unlock...") {
        input "deadbolt1", "capability.lockCodes",
              title: "Where?"
    }
    // What light should this app be configured for?
    section ("Turn on/off a light...") {
        input "switch1", "capability.switch"
    }
    
    section("Send Notifications?") {
        input("recipients", "contact", title: "Send notifications to") {
            input "phone", "phone", title: "Warn with text message (optional)",
                description: "Phone Number", required: false
        }
    }
}
def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(deadbolt1,"lock",lockHandler)
}

def lockHandler(evt) {
 
    log.debug "$evt.value: $evt, $settings"
    String[] users = ["SuperUser", "Maika", "Sabrina", "Mathilde",  "Stephane", "Martine" ] as String[]
    


    if (evt.value == "locked") {
    	// Here everything to do when we lock the door
        switch1.off();
        
    } else if (evt.value == "unlocked") {
        //  Here evrything to do when we unlock a door...
        
        
        def lockData = new JsonSlurper().parseText(evt.data)
    	String opUser = users[lockData.usedCode]
    	log.debug "Door unlocked with user code $opUser - ${lockData.usedCode}"
		if (opUser == "Martine"){
        	switch1.on();
        }
        def message = "The Door ${deadbolt1.displayName} have been unlocked by $opUser - ${lockData.usedCode}!"
        if (location.contactBookEnabled && recipients) {
           log.debug "contact book enabled!"
           sendNotificationToContacts(message, recipients)
        } else {
            log.debug "contact book not enabled..send SMS to $phone"
            if (phone) {
                sendSms(phone, message)
            }
        }
    }
    
}