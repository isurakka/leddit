package com.leddit.leddit.api;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

/**
 * Created by Jonah on 7.12.2014.
 */
public final class Utility
{
    private Utility()
    {

    }

    public static String redditTimePeriod(DateTime from, DateTime to)
    {
        Period p = new Period(from, to);

        if(p.getYears() > 0)
        {
            return p.getYears() + "y";
        }
        else if(p.getMonths() > 0)
        {
            return p.getMonths() + "m";
        }
        else if(p.getWeeks() > 0)
        {
            return p.getWeeks() + "w";
        }
        else if(p.getDays() > 0)
        {
            return p.getDays() + "d";
        }
        else if(p.getHours() > 0)
        {
            return p.getHours() + "h";
        }
        else if(p.getMinutes() > 0)
        {
            return p.getMinutes() + "m";
        }
        else if(p.getSeconds() > 0)
        {
            return p.getSeconds() + "s";
        }
        else if(p.getMillis() > 0)
        {
            return p.getMillis() + "ms";
        }
        else
        {
            return "??";
        }


        /*return "Y: " + p.getYears() +
           ", M: " + p.getMonths() +
           ", W: " + p.getWeeks() +
           ", D: " + p.getDays() +
           ", H: " + p.getHours() +
           ", M: " + p.getMinutes() +
           ", S: " + p.getSeconds();*/
    }
}