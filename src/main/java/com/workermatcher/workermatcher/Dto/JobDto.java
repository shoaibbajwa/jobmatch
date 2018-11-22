package com.workermatcher.workermatcher.Dto;

import java.util.Date;
import java.util.List;

public class JobDto {
    private Boolean driverLicenseRequired;
    private List<String> requiredCertificates = null;
    private LocationDto location;
    private String billRate;
    private Integer workersRequired;
    private Date startDate;
    private String about;
    private String jobTitle;
    private String company;
    private String guid;
    private Integer jobId;

    private Integer skillMatch;
    private boolean sameDayAvail = true;

    public Boolean getDriverLicenseRequired() {
        return driverLicenseRequired;
    }

    public void setDriverLicenseRequired(Boolean driverLicenseRequired) {
        this.driverLicenseRequired = driverLicenseRequired;
    }

    public List<String> getRequiredCertificates() {
        return requiredCertificates;
    }

    public void setRequiredCertificates(List<String> requiredCertificates) {
        this.requiredCertificates = requiredCertificates;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public String getBillRate() {
        return billRate;
    }

    public void setBillRate(String billRate) {
        this.billRate = billRate;
    }

    public Integer getWorkersRequired() {
        return workersRequired;
    }

    public void setWorkersRequired(Integer workersRequired) {
        this.workersRequired = workersRequired;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getSkillMatch() {
        return skillMatch;
    }

    public void setSkillMatch(Integer skillMatch) {
        this.skillMatch = skillMatch;
    }

    public boolean isSameDayAvail() {
        return sameDayAvail;
    }

    public void setSameDayAvail(boolean sameDayAvail) {
        this.sameDayAvail = sameDayAvail;
    }
}
