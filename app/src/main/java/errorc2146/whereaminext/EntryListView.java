package errorc2146.whereaminext;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import errorc2146.whereaminext.tablelogic.Timetable;
import errorc2146.whereaminext.tablelogic.TimetableEntry;
import errorc2146.whereaminext.tablelogic.WeekTime;

public class EntryListView extends AppCompatActivity implements NewEntryDialog.NewEntryListener
{
    private ListView lview;
    private TableListAdapter adapter;
    private Spinner currentDay;
    private ArrayAdapter days;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Toolbar tb = (Toolbar)findViewById(R.id.listtoolbar);
        setSupportActionBar(tb);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        currentDay = (Spinner)findViewById(R.id.selectedday);
        days = new ArrayAdapter<WeekTime.DayOfWeek>(this,
                android.R.layout.simple_spinner_dropdown_item,
                WeekTime.DayOfWeek.values());
        currentDay.setAdapter(days);
        currentDay.setSelection(new WeekTime().getDay().ordinal());
        currentDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView parent, View v, int pos, long id)
            {
                adapter.setList(Timetable.getSubsetFor((WeekTime.DayOfWeek)parent.getSelectedItem()));
                adapter.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView parent){}
        });

        adapter = new TableListAdapter(getApplicationContext(), Timetable.getSubsetFor((WeekTime.DayOfWeek)currentDay.getSelectedItem()));
        lview = (ListView) findViewById(R.id.entrylistview);
        lview.setAdapter(adapter);
        lview.setLongClickable(true);
        registerForContextMenu(lview);
    }

    public void newEntry(TimetableEntry entry, TimetableEntry old)
    {
        Timetable.removeEntry(old);
        Timetable.addEntry(entry);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.listpopupmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TimetableEntry clickedEntry =  (TimetableEntry) lview.getItemAtPosition(info.position);

        switch(item.getItemId())
        {
            case R.id.edit:
                NewEntryDialog dialog = NewEntryDialog.newInstance();
                dialog.editEntry(clickedEntry);
                dialog.show(getSupportFragmentManager(), "New Entry Dialog");
                return true;
            case R.id.delete:
                Timetable.removeEntry(clickedEntry);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.optionsmenu, menu);

        MenuItem checked = menu.findItem(R.id.notificationcheck);
        if (checked != null) checked.setChecked(MainActivity.getNotification());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case (R.id.newentry):
                NewEntryDialog d = NewEntryDialog.newInstance();
                d.setDay((WeekTime.DayOfWeek)currentDay.getSelectedItem());
                d.show(getSupportFragmentManager(), "New Entry Dialog");
                return true;

            case (R.id.notificationcheck):
                item.setChecked(!item.isChecked());
                MainActivity.setNotification(item.isChecked());
                SharedPreferences.Editor editor = getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
                editor.putBoolean("notification", item.isChecked());
                editor.apply();

                if (!MainActivity.getNotification())
                {
                    NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    nm.cancelAll();
                }
                else
                {
                    sendBroadcast(new Intent(getApplicationContext(), AlarmReceiver.class));
                }

                return true;

            case R.id.about:
                new AlertDialog.Builder(this)
                        .setTitle("About")
                        .setView(R.layout.aboutdialog).show();
                return true;

            case R.id.cleartable:
                new AlertDialog.Builder(this)
                        .setTitle("Clear Table")
                        .setMessage("Are you sure you want to delete all entries?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                Timetable.clear();
                                adapter.notifyDataSetChanged();
                                NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                nm.cancelAll();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;

            case (android.R.id.home):
                save();
                sendBroadcast(new Intent(getApplicationContext(), AlarmReceiver.class));
                super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void save()
    {
        try
        {
            FileOutputStream output = openFileOutput(getString(R.string.tablename), MODE_PRIVATE);
            Timetable.writeToStream(output);
            output.close();
        }
        catch (FileNotFoundException e) {}
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(), "IOException whilst saving!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause()
    {
        save();
        super.onPause();
    }
}
