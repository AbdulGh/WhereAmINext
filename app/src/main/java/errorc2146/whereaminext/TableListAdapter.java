package errorc2146.whereaminext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Iterator;
import java.util.NavigableSet;

import errorc2146.whereaminext.tablelogic.TimetableEntry;
import errorc2146.whereaminext.tablelogic.WeekTime;

public class TableListAdapter extends BaseAdapter
{
    private NavigableSet<TimetableEntry> list;
    private Context context;

    public TableListAdapter(Context context, NavigableSet<TimetableEntry> list)
    {
        this.context = context;
        this.list = list;
    }

    public void setList(NavigableSet<TimetableEntry> newList)
    {
        this.list = newList;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public TimetableEntry getItem(int pos)
    {
        if (pos >= list.size()) throw new ArrayIndexOutOfBoundsException();

        int count = 0;
        Iterator<TimetableEntry> it = list.iterator();
        while (it.hasNext())
        {
            if (count++ == pos) return it.next();
            else it.next();
        }

        //shouldn't ever get here
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public long getItemId(int pos)
    {
        return pos;
    }

    static class viewReferences
    {
        TextView tv1, tv2;
    }

    @Override
    public View getView(int position, View view, ViewGroup group)
    {
        viewReferences holder;

        if (view == null)
        {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.entrylistrow, group, false);

            holder = new viewReferences();
            holder.tv1 = (TextView) view.findViewById(R.id.entrylistrow);
            holder.tv2 = (TextView) view.findViewById(R.id.entrylisttime);

            view.setTag(holder);
        }

        else
        {
            holder = (viewReferences)view.getTag();
        }

        TimetableEntry te = getItem(position);
        holder.tv1.setText(te.getName() + " - " + te.getLocation());
        holder.tv2.setText(WeekTime.pad(te.getTime().getHours()) + ":" + WeekTime.pad(te.getTime().getMinutes()));

        return view;
    }
}