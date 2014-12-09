package com.ss.ui.domain;

import java.util.Date;

public final class Threat {

    private Date timestamp;
    private String title;
    private double count;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(final Double revenue) {
        this.count = revenue;
    }

}
