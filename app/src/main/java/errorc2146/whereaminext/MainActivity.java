package errorc2146.whereaminext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import errorc2146.whereaminext.tablelogic.Timetable;
import errorc2146.whereaminext.tablelogic.TimetableEntry;

public class MainActivity extends AppCompatActivity
{
    private TextView name;
    private TextView location;
    private TextView time;
    private TextView noEntryText;
    private RelativeLayout detailLayout;

    private static boolean notification;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NewEntryDialog.init(getApplicationContext());
        Timetable.init();

        File inFile = new File(getApplicationContext().getFilesDir(), getString(R.string.tablename));
        if (inFile.exists()) try
        {
            FileInputStream fi = new FileInputStream(inFile);
            Timetable.loadFromStream(fi);
            fi.close();
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(getApplicationContext(), "Table file disappeared whilst loading!", Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            Toast.makeText(getApplicationContext(), "IOException whilst loading!", Toast.LENGTH_LONG).show();
        }

        name = (TextView)findViewById(R.id.nametext);
        location = (TextView)findViewById(R.id.locationtext);
        time = (TextView)findViewById(R.id.timetext);

        noEntryText=(TextView)findViewById(R.id.noentrytext);
        detailLayout=(RelativeLayout) findViewById(R.id.detaillayout);

        ImageButton button = (ImageButton)findViewById(R.id.switchtolist);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, EntryListView.class));
            }
        });

        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.hide();

        SharedPreferences sp = getSharedPreferences("settings", Context.MODE_PRIVATE);

        if (sp.contains("notification")) notification = sp.getBoolean("notification", true);
        else
        {
            notification = false;
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("notification", notification);
            editor.apply();
        }

        update();
    }

    public void update()
    {
        TimetableEntry next = Timetable.getNextEntry();

        if (next == null)
        {
            noEntryText.setVisibility(View.VISIBLE);
            detailLayout.setVisibility(View.GONE);
        }

        else
        {
            noEntryText.setVisibility(View.GONE);
            detailLayout.setVisibility(View.VISIBLE);

            name.setText(next.getName());
            if (next.getLocation() != null)
            {
                location.setText(next.getLocation());
            }
            time.setText(next.getTime().toString());
        }
    }

    @Override
    public void onPause()
    {
        sendBroadcast(new Intent(getApplicationContext(), AlarmReceiver.class));
        super.onPause();
    }


    @Override
    public void onResume()
    {
        update();
        super.onResume();
    }

    public static void setNotification(boolean notification)
    {
        MainActivity.notification = notification;
    }

    public static boolean getNotification()
    {
        return notification;
    }
}


