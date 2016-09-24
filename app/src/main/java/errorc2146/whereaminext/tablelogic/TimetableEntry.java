package errorc2146.whereaminext.tablelogic;

import java.io.IOException;
import java.io.InputStream;

/**
 *Holds the {@link errorc2146.whereaminext.tablelogic.WeekTime}, name and location of a weekly event.
 */
public class TimetableEntry implements Comparable<Object>
{
    private WeekTime time;
    private String name;
    private String location;

    public final static char recordsep = '\030';
    public final static char unitsep = '\031';

    public TimetableEntry(String name, String location, WeekTime time)
    {
        this.time = time;
        this.name = name;
        this.location = location;
    }

    public TimetableEntry()
    {
        this.name = null;
        this.time = null;
        this.location = null;
    }

    public WeekTime getTime()
    {
        return time;
    }

    public void setTime(WeekTime time)
    {
        this.time = time;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getSaveString()
    {
        return name + unitsep + location + unitsep + time.getTime();
    }

    public boolean readFromStream(InputStream in) throws IOException
    {
        int r;

        name = "";
        while ((r = in.read()) != unitsep)
        {
            if (!(Character.isLetterOrDigit(r) || Character.isSpaceChar(r))) return false;
            else name += (char)r;
        }

        location = "";
        while ((r = in.read()) != unitsep)
        {
            if (!(Character.isLetterOrDigit(r) || Character.isSpaceChar(r))) return false;
            else location += (char)r;
        }

        int readtime = 0;
        while ((r = in.read()) != recordsep)
        {
            if (!Character.isDigit(r)) return false;
            readtime *= 10;
            readtime += r - '0';
        }

        this.time = new WeekTime(readtime);
        return true;
    }

    public boolean isHeader()
    {
        return false;
    }

    @Override
    public String toString()
    {
        String val = name;
        if (location != null) val += " at " + location;
        val += " on " + time;
        return val;
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof TimetableEntry)
        {
            TimetableEntry other = (TimetableEntry)o;
            return time.compareTo(other.time);
        }
        else if (o instanceof WeekTime)
        {
            WeekTime other = (WeekTime)o;
            return time.compareTo(other);
        }
        else throw new ClassCastException();
    }
}
