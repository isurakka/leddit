package com.leddit.leddit.api.output;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.joda.time.DateTime;

/**
 * Created by Jonah on 6.12.2014.
 */

/*
    Container class for Reddit API output
*/
public class RedditProfile extends RedditError
{
    private boolean has_mail;
    private String name;
    private long created;
    private boolean hide_from_robots;
    private int gold_creddits;
    private long created_utc;
    private int link_karma;
    private int comment_karma;
    private boolean over_18;
    private boolean is_gold;
    private boolean is_mod;
    private long gold_expiration;
    private boolean has_verified_email;
    private String id;
    private boolean has_mod_mail;

    @JsonIgnore
    private DateTime actual_created_utc;

    public boolean isHas_mail() {
        return has_mail;
    }

    public void setHas_mail(boolean has_mail) {
        this.has_mail = has_mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public boolean isHide_from_robots() {
        return hide_from_robots;
    }

    public void setHide_from_robots(boolean hide_from_robots) {
        this.hide_from_robots = hide_from_robots;
    }

    public int getGold_creddits() {
        return gold_creddits;
    }

    public void setGold_creddits(int gold_creddits) {
        this.gold_creddits = gold_creddits;
    }

    public long getCreated_utc() {
        return created_utc;
    }

    public void setCreated_utc(long created_utc) {
        this.created_utc = created_utc;
    }

    public int getLink_karma() {
        return link_karma;
    }

    public void setLink_karma(int link_karma) {
        this.link_karma = link_karma;
    }

    public int getComment_karma() {
        return comment_karma;
    }

    public void setComment_karma(int comment_karma) {
        this.comment_karma = comment_karma;
    }

    public boolean isOver_18() {
        return over_18;
    }

    public void setOver_18(boolean over_18) {
        this.over_18 = over_18;
    }

    public boolean isIs_gold() {
        return is_gold;
    }

    public void setIs_gold(boolean is_gold) {
        this.is_gold = is_gold;
    }

    public boolean isIs_mod() {
        return is_mod;
    }

    public void setIs_mod(boolean is_mod) {
        this.is_mod = is_mod;
    }

    public long getGold_expiration() {
        return gold_expiration;
    }

    public void setGold_expiration(long gold_expiration) {
        this.gold_expiration = gold_expiration;
    }

    public boolean isHas_verified_email() {
        return has_verified_email;
    }

    public void setHas_verified_email(boolean has_verified_email) {
        this.has_verified_email = has_verified_email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isHas_mod_mail() {
        return has_mod_mail;
    }

    public void setHas_mod_mail(boolean has_mod_mail) {
        this.has_mod_mail = has_mod_mail;
    }

    public DateTime getActual_created_utc() {
        return actual_created_utc;
    }

    public void setActual_created_utc(DateTime actual_created_utc) {
        this.actual_created_utc = actual_created_utc;
    }
}
