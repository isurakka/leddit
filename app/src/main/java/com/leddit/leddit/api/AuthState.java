package com.leddit.leddit.api;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonah on 6.12.2014.
 */

/*
    Hold user authorization data
*/

public class AuthState implements Parcelable, Serializable {

    private static final long serialVersionUID = 1L;
    private String access_token;
    private String token_type;
    private DateTime expires_in;
    private String scope;
    private String refresh_token;

    private static List<AuthStateListener> listeners = new ArrayList<AuthStateListener>();

    public AuthState()
    {

    }

    public AuthState(Parcel in)
    {
        access_token = in.readString();
        token_type = in.readString();
        expires_in = new DateTime(in.readLong(), DateTimeZone.UTC);
        scope = in.readString();
        refresh_token = in.readString();
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token)
    {
        for(int i = 0; i < listeners.size(); i++)
        {
            listeners.get(i).authStateChanged();
        }

        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public DateTime getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(DateTime expires_in) {

        this.expires_in = expires_in;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(access_token);
        dest.writeString(token_type);
        dest.writeLong(expires_in.getMillis());
        dest.writeString(scope);
        dest.writeString(refresh_token);
    }

    public void addListener(AuthStateListener listener)
    {
        listeners.add(listener);
    }
}
