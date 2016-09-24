package errorc2146.whereaminext.tablelogic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Timetable
{
    private static TreeSet<TimetableEntry> entrySet;

    public final static char recordsep = '\030';

    public static void init()
    {
        entrySet = new TreeSet<TimetableEntry>();
    }

    public static TreeSet<TimetableEntry> getEntries()
    {
        return entrySet;
    }

    public static NavigableSet<TimetableEntry> getSubsetFor(WeekTime.DayOfWeek day)
    {
        TimetableEntry startdummy = new TimetableEntry(null, null, new WeekTime(day.getMinutes()));
        TimetableEntry enddummy = new TimetableEntry(null, null, new WeekTime(day.getMinutes() + WeekTime.MINUTESINDAY));

        return entrySet.subSet(startdummy, true, enddummy, false);
    }

    public static void clear()
    {
        entrySet.clear();
    }

    public static void addEntry(TimetableEntry n)
    {
        entrySet.add(n);
    }

    public static boolean removeEntry(TimetableEntry bye)
    {
        if (bye != null) return entrySet.remove(bye);
        return false;
    }

    public static TimetableEntry getNextEntry()
    {
        if (entrySet.isEmpty()) return null;

        WeekTime now = new WeekTime();
        for (TimetableEntry x: entrySet)
        {
            if (x.getTime().compareTo(now) > 0) return x;
        }

        //we're after all the entries in this week, return the first entry
        return entrySet.first();
    }

    public static void writeToStream(OutputStream out) throws IOException
    {
        for (TimetableEntry entry: entrySet)
        {
            out.write(entry.getSaveString().getBytes());
            out.write(recordsep);
        }
    }

    public static boolean loadFromStream(InputStream in) throws IOException
    {
        entrySet.clear();
        TimetableEntry entry = new TimetableEntry();
        while (entry.readFromStream(in))
        {
            entrySet.add(entry);
            entry = new TimetableEntry();
        }
        return true;
    }
}