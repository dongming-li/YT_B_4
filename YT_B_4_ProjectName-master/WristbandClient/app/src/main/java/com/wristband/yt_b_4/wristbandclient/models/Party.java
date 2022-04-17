package com.wristband.yt_b_4.wristbandclient.models;

/**
 * Created by Mike on 10/5/2017.
 */

public class Party {

    private String party_name, date, time, host, location;
    private int privacy, max_people, alerts;

    public Party(String party_name, String date, String time, int privacy, int max_people, int alerts, String host, String location) {
        this.party_name = party_name;
        this.date = date;
        this.time = time;
        this.privacy = privacy;
        this.max_people = max_people;
        this.alerts = alerts;
        this.host = host;
        this.location = location;
    }

    public String getPartyName() {
        return this.party_name;
    }

    public String getDate() {
        return this.date;
    }

    public String getTime() {
        return this.time;
    }

    public int getPrivacy() {
        return this.privacy;
    }

    public int getMaxPeople() {
        return this.max_people;
    }

    public int getAlerts() {
        return this.alerts;
    }

    public String getHost() {
        return this.host;
    }

    public String getLocation() {
        return this.location;
    }

    public int makePartyPublic() {
        this.privacy = 1;
        return this.privacy;
    }

    public int MakePartyPrivate() {
        this.privacy = 0;
        return this.privacy;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
