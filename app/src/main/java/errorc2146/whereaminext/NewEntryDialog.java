package errorc2146.whereaminext;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import errorc2146.whereaminext.tablelogic.TimetableEntry;
import errorc2146.whereaminext.tablelogic.WeekTime;

public class NewEntryDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    private static ArrayAdapter<WeekTime.DayOfWeek> days;

    private EditText name;
    private EditText location;
    private EditText timetext;
    private Spinner daySpinner;
    private WeekTime.DayOfWeek day;
    private int hour, min;

    private TimetableEntry old;

    public interface NewEntryListener
    {
        void newEntry(TimetableEntry e, TimetableEntry o);
    }
    private NewEntryListener caller;

    public static void init(Context ctx)
    {
        days = new ArrayAdapter<WeekTime.DayOfWeek>(ctx, android.R.layout.simple_spinner_dropdown_item, WeekTime.DayOfWeek.values());
    }

    public void editEntry(TimetableEntry e)
    {
        old = e;
    }

    public void setDay(WeekTime.DayOfWeek day)
    {
        this.day = day;
    }

    public static NewEntryDialog newInstance()
    {
        NewEntryDialog d = new NewEntryDialog();
        d.setArguments(new Bundle());
        return d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.newentrydialog, container);

        daySpinner = (Spinner)v.findViewById(R.id.dayspinner);
        daySpinner.setAdapter(days);
        if (day != null) daySpinner.setSelection(day.ordinal());

        name = (EditText)v.findViewById(R.id.enteredname);
        location = (EditText)v.findViewById(R.id.enteredloc);

        timetext = (EditText)v.findViewById(R.id.enteredtime);
        timetext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new TimePickerDialog(getContext(), NewEntryDialog.this, 0,0, true).show();
            }
        });

        Button button = (Button)v.findViewById(R.id.okay);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                TimetableEntry entry = createEntry();
                if (entry != null)
                {
                    caller.newEntry(entry, old);
                    dismiss();
                }
            }
        });

        return v;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
        {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        if (old != null)
        {
            name.setText(old.getName());
            location.setText(old.getLocation());
            timetext.setText(WeekTime.pad(old.getTime().getHours()) + ":" + WeekTime.pad(old.getTime().getMinutes()));
            daySpinner.setSelection(old.getTime().getDay().ordinal());
            hour = old.getTime().getHours();
            min = old.getTime().getMinutes();
        }

        else
        {
            hour = -1;
            min = -1;
        }
    }

    @Override
    public void onAttach(Activity caller)
    {
        try
        {
            this.caller = (NewEntryListener)caller;
        }
        catch(ClassCastException e)
        {
            Toast.makeText(getContext(), "NewEntryDialog needs you to implement NewEntryListener", Toast.LENGTH_SHORT).show();
        }
        super.onAttach(caller);
    }

    @Override
    public void onTimeSet(TimePicker p, int hour, int min)
    {
        this.hour = hour;
        this.min = min;
        timetext.setText(WeekTime.pad(hour) + ":" + WeekTime.pad(min));
    }

    private TimetableEntry createEntry()
    {
        String entryname = name.getText().toString();
        if (entryname.equals(""))
        {
            Toast.makeText(getContext(), R.string.noname, Toast.LENGTH_LONG).show();
            return null;
        }

        String locname = location.getText().toString();
        if (locname.equals(""))
        {
            Toast.makeText(getContext(), R.string.noloc, Toast.LENGTH_LONG).show();
            return null;
        }

        if (hour == -1)
        {
            Toast.makeText(getContext(), R.string.notime, Toast.LENGTH_LONG).show();
            return null;
        }

        return new TimetableEntry(entryname, locname,
                new WeekTime(daySpinner.getSelectedItemPosition() * WeekTime.MINUTESINDAY + hour * 60 + min));
    }
}
