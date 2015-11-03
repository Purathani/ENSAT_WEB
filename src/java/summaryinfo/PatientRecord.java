/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package summaryinfo;

/**
 *
 * @author astell
 */
public class PatientRecord {
    
    private String ensatID, centerID, investigatorName, investigatorEmail;
    private String recordDate, dateFirstReg, sex, yearOfBirth, consentLevel;
    private String uploader/*, delete, transferFunction, extraInfo*/;
    
    public PatientRecord(){
        ensatID = "";
        centerID = "";
        investigatorName = "";
        investigatorEmail = "";
        recordDate = "";
        dateFirstReg = "";
        sex = "";
        yearOfBirth = "";
        consentLevel = "";
        uploader = "";
        /*delete = "";
        transferFunction = "";
        extraInfo = "";*/
    }
    
    public void setEnsatID(String _ensatID){
        ensatID = _ensatID;
    }
    
    public String getEnsatID(){
        return ensatID;
    }
    
    public void setCenterID(String _centerID){
        centerID = _centerID;
    }
    
    public String getCenterID(){
        return centerID;
    }

    public void setInvestigatorName(String _investigatorName){
        investigatorName = _investigatorName;
    }
    
    public String getInvestigatorName(){
        return investigatorName;
    }

    public void setInvestigatorEmail(String _investigatorEmail){
        investigatorEmail = _investigatorEmail;
    }
    
    public String getInvestigatorEmail(){
        return investigatorEmail;
    }
    
    public void setRecordDate(String _recordDate){
        recordDate = _recordDate;
    }
    
    public String getRecordDate(){
        return recordDate;
    }
    
    public void setDateFirstReg(String _dateFirstReg){
        dateFirstReg = _dateFirstReg;
    }
    
    public String getDateFirstReg(){
        return dateFirstReg;
    }

    public void setSex(String _sex){
        sex = _sex;
    }
    
    public String getSex(){
        return sex;
    }
    
    public void setYearOfBirth(String _yearOfBirth){
        yearOfBirth = _yearOfBirth;
    }
    
    public String getYearOfBirth(){
        return yearOfBirth;
    }

    public void setConsentLevel(String _consentLevel){
        consentLevel = _consentLevel;
    }
    
    public String getConsentLevel(){
        return consentLevel;
    }
    
    public void setUploader(String _uploader){
        uploader = _uploader;
    }
    
    public String getUploader(){
        return uploader;
    }
    
    
}
