package com.momeni.meetup.rsvp.cron;

import java.util.Date;

public class TaskDefinition {
    private String cronExpression;
    private Date dateInstance;
    private String actionType;
    private String data;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Date getDateInstance() {
        return dateInstance;
    }

    public void setDateInstance(Date dateInstance) {
        this.dateInstance = dateInstance;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}