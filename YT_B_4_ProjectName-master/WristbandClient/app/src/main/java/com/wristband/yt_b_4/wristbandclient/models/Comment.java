package com.wristband.yt_b_4.wristbandclient.models;

/**
 * Created by AustinKonsor on 11/7/17.
 */

public class Comment {
    private String party_id, username, cmt;

    public Comment(String party_id, String username, String cmt) {
        this.party_id = party_id;
        this.username = username;
        this.cmt = cmt;
    }

    public String getPartyId() {
        return this.party_id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getText() {
        return this.cmt;
    }

}
