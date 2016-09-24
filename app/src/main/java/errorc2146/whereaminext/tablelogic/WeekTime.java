package errorc2146.whereaminext.tablelogic;

import java.util.Calendar;

/**
 * Stores the time in the form of minutes, from the start of the week.
 * There are 7*24*60 = 10080 minutes in a week.
 */
public class WeekTime implements Comparable<WeekTime>
{
    private int time;

    public static final int MINUTESINDAY = 1440;

    /**
     * Simple enum for days of the week, storing names
     */
    public enum DayOfWeek
    {
        MONDAY("Monday"), TUESDAY("Tuesday"), WEDNESDAY("Wednesday"),
        THURSDAY("Thursday"), FRIDAY("Friday"), SATURDAY("Saturday"),
        SUNDAY("Sunday");

        public static DayOfWeek[] valArray = values(); //values() copies an array every call

        private String niceName;

        DayOfWeek(String niceName)
        {
            this.niceName = niceName;
        }

        public int getMinutes()
        {
            return ordinal() * MINUTESINDAY;
        }

        @Override
        public String toString()
        {
            return niceName;
        }

    }

    public WeekTime(int time)
    {
        this.time = time;
    }

    /**
     * Default constructor returns a WeekTime representing the current time.
     */
    public WeekTime()
    {
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_WEEK);
        if (day == 1)  day = 6;//Sunday
        else day -= 2;
        time = (day * MINUTESINDAY
                + now.get(Calendar.HOUR_OF_DAY) * 60
                + now.get(Calendar.MINUTE));
    }

    public DayOfWeek getDay()
    {
        return DayOfWeek.valArray[time / MINUTESINDAY];
    }

    public int getHours()
    {
        return (time % MINUTESINDAY) / 60;
    }

    public int getMinutes()
    {
        return time % 60;
    }

    public int getTime()
    {
        return time;
    }

    public void setDay(DayOfWeek newDay)
    {
        time = newDay.ordinal() * MINUTESINDAY + time % MINUTESINDAY;
    }

    public void setHour(int newHour)
    {
        if (newHour < 0 || newHour > 23) throw new IllegalArgumentException();
        time = getDay().getMinutes() + getMinutes() + newHour * 60;
    }

    public void setMinute(int newMinutes)
    {
        if (newMinutes < 0 || newMinutes > 59) throw new IllegalArgumentException();
        time = time - time % 60 + newMinutes;
    }

    @Override
    public int compareTo(WeekTime other)
    {
        return time - other.time;
    }

    @Override
    public String toString()
    {
        return getDay() + ", " + pad(getHours()) + ":" + pad(getMinutes());
    }

    public static String pad(int padme)
    {
        return (padme < 10) ? "0" + padme : String.valueOf(padme);
    }
}