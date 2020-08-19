package org.alberta.health.poc.hl7;

import java.io.IOException;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

public class AdtMessageFactory {

    
    public static Message createMessage(String messageType) throws HL7Exception, IOException {
        {
  //This patterns enables you to build other message types 
    if ( messageType.equals("A01") )
    {
        /*
         * DT messages carry patient demographic information for HL7 communications but also provide important information about trigger events (such as patient admit, discharge, transfer, registration, etc.). 
         * Some of the most important segments in the ADT message are the PID (Patient Identification) segment, the PV1 (Patient Visit) segment, and occasionally the IN1 (Insurance) segment. 
         * ADT messages are extremely common in HL7 processing and are among the most widely used of all message types.
         */
        return new CustomAdtA01MessageBuilder().Build();
    }
    
    //if other types of ADT messages are needed, then implement your builders here
    throw new RuntimeException(String.format("%s message type is not supported yet. Extend this if you need to", messageType));
     
    
        }
    }
}