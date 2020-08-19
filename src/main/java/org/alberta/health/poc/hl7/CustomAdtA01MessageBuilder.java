package org.alberta.health.poc.hl7;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v24.datatype.PL;
import ca.uhn.hl7v2.model.v24.datatype.XAD;
import ca.uhn.hl7v2.model.v24.datatype.XCN;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.segment.EVN;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;

public class CustomAdtA01MessageBuilder {
    
    private ADT_A01 _adtMessage;

    /*You can pass in a domain object as a parameter
    when integrating with data from your application here
    I will leave that to you to explore on your own
    Using fictional data here for illustration*/

    public ADT_A01 Build() throws HL7Exception, IOException {
        String currentDateTimeString = getCurrentTimeStamp();
        _adtMessage = new ADT_A01(); 
        //you can use the context class's newMessage method to instantiate a message if you want
        _adtMessage.initQuickstart("ADT", "A01", "T");
        createMshSegment(currentDateTimeString);// creating Message Header segment.
        createEvnSegment(currentDateTimeString);//EVN segment is used to communicate trigger event information to receiving applications.
        createPidSegment();
        createPv1Segment();
        return _adtMessage;
    }

    /* Message Header segment.
     * Defines the message’s source, purpose, destination, and certain syntax specifics like delimiters (separator characters) 
     * and character sets. It is always the first segment in the HL7 message, with the only exception being HL7 batch messages.
     * */
    private void createMshSegment(String currentDateTimeString) throws DataTypeException {
        MSH mshSegment = _adtMessage.getMSH();
        mshSegment.getFieldSeparator().setValue("|");
        mshSegment.getEncodingCharacters().setValue("^~\\&");
        mshSegment.getSendingApplication().getNamespaceID().setValue("Connect Care <--Sending System");
        mshSegment.getSendingFacility().getNamespaceID().setValue("University of Alberta Hospital<--Sending Facility");
        mshSegment.getReceivingApplication().getNamespaceID().setValue("CII <-- Receiving Remote System");
        mshSegment.getReceivingFacility().getNamespaceID().setValue("CII HUB <--Receiving Remote Facility");
        mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(currentDateTimeString);
        mshSegment.getMessageControlID().setValue(getSequenceNumber());
        mshSegment.getVersionID().getVersionID().setValue("2.4");
    }

    /* 
     * EVN segment is used to communicate trigger event information to receiving applications.
     * */ 
    private void createEvnSegment(String currentDateTimeString) throws DataTypeException {
        EVN evn = _adtMessage.getEVN();
        evn.getEventTypeCode().setValue("A01");
        evn.getRecordedDateTime().getTimeOfAnEvent().setValue(currentDateTimeString);
    }
    
    /*
     * The HL7 PID segment is found in every type of ADT message (i.e. ADT-A01, ADT-A08, etc.) and contains 30 different fields with values ranging from patient ID number, 
     * to patient sex, to address, to marital status, to citizenship. The PID segment provides important identification information about the patient and, in fact, is used
     * as the primary means of communicating the identifying and demographic information about a patient between systems. Due to the nature of the information found in the 
     * PID segment, it is unlikely to change frequently.
     */
    private void createPidSegment() throws DataTypeException {
        PID pid = _adtMessage.getPID();
        XPN patientName = pid.getPatientName(0);
        patientName.getFamilyName().getSurname().setValue("Mouse");
        patientName.getGivenName().setValue("Mickey");
        pid.getPatientIdentifierList(0).getID().setValue("378785433211");
        XAD patientAddress = pid.getPatientAddress(0);
        patientAddress.getStreetAddress().getStreetOrMailingAddress().setValue("123 Main Street");
        patientAddress.getCity().setValue("Edmonton");
        patientAddress.getStateOrProvince().setValue("AB");
        patientAddress.getCountry().setValue("CA");
    }

    /*
     * The HL7 PV1 segment contains basic inpatient or outpatient encounter information and consists of 52 different fields with values ranging from assigned patient location, to 
     * admitting doctor, to visit number, to servicing facility. The PV1 segment communicates information on an account or visit-specific basis.  The default is to send account level data.
     *  If the segment is to be used for visit level data, the PV1-51 Visit Indicator must be set to “V”.
     */
    private void createPv1Segment() throws DataTypeException {
        PV1 pv1 = _adtMessage.getPV1();
        pv1.getPatientClass().setValue("O"); // to represent an 'Outpatient'
        // Patient Location
        PL assignedPatientLocation = pv1.getAssignedPatientLocation();
        assignedPatientLocation.getFacility().getNamespaceID().setValue("Radiology UofA:Some Treatment Facility Name");
        assignedPatientLocation.getPointOfCare().setValue("CT:Some Point of Care");
        pv1.getAdmissionType().setValue("ALERT");
        
        XCN referringDoctor = pv1.getReferringDoctor(0);
        referringDoctor.getIDNumber().setValue("99999999");
        referringDoctor.getFamilyName().getSurname().setValue("Smith");
        referringDoctor.getGivenName().setValue("Jack");
        referringDoctor.getIdentifierTypeCode().setValue("456789");
        pv1.getAdmitDateTime().getTimeOfAnEvent().setValue(getCurrentTimeStamp());
    }
    
    private String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private String getSequenceNumber() {
        String facilityNumberPrefix = "ABCC"; // some arbitrary prefix for the facility
        return facilityNumberPrefix.concat(getCurrentTimeStamp());
    }
}

