package org.alberta.health.poc.hl7;

/**
 * Architecture Services POC for HL7V2.4.
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.parser.Parser;

public class POCHealthArchitectureHL7V24 
{
    private static final int PORT_NUMBER = 52463;// change this to whatever your port number is

    public static void main( String[] args )
    {
       
        System.out.println( "Aretecture Services POC for HL7V2.4" );
    
        HapiContext context = new DefaultHapiContext();
        
        try {

            // create the HL7 message
            // this AdtMessageFactory class is not from HAPI but my own wrapper
            System.out.println("Creating ADT A01 message...");
            ADT_A01 adtMessage = (ADT_A01) AdtMessageFactory.createMessage("A01");

            // create these parsers for file operations
            Parser pipeParser = context.getPipeParser();
            Parser xmlParser = context.getXMLParser();

            // print out the message that we constructed
            System.out.println("Message was constructed successfully..." + "\n");
            System.out.println(pipeParser.encode(adtMessage));

            // serialize the message to pipe delimited output file
            writeMessageToFile(pipeParser, adtMessage, "testPipeDelimitedOutputFile.txt");

            // serialize the message to XML format output file
            writeMessageToFile(xmlParser, adtMessage, "testXmlOutputFile.xml");
            
            //you can print out the message structure using a convenient helper method on the message class
            System.out.println("Printing message structure to console...");
            System.out.println(adtMessage.printStructure());
           
            /*
             * Now we are trying to send the message to remote application.
             */
            
            Connection connection = context.newClient("localhost", PORT_NUMBER, false);

            // The initiator which will be used to transmit our message
            Initiator initiator = connection.getInitiator();

            // send the previously created HL7 message over the connection established
            Parser parser = context.getPipeParser();
            System.out.println("Sending message:" + "\n" + parser.encode(adtMessage));
            Message response = initiator.sendAndReceive(adtMessage);

            // display the message response received from the remote party
            String responseString = parser.encode(response);
            System.out.println("Received response:\n" + responseString);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    
     }
    
    private static void writeMessageToFile(Parser parser, ADT_A01 adtMessage, String outputFilename)
            throws IOException, FileNotFoundException, HL7Exception {
        OutputStream outputStream = null;
        try {

            // Remember that the file may not show special delimiter characters when using
            // plain text editor
            File file = new File(outputFilename);

            // quick check to create the file before writing if it does not exist already
            if (!file.exists()) {
                file.createNewFile();
            }

            System.out.println("Serializing message to file...");
            outputStream = new FileOutputStream(file);
            outputStream.write(parser.encode(adtMessage).getBytes());
            outputStream.flush();

            System.out.printf("Message serialized to file '%s' successfully", file);
            System.out.println("\n");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

}

