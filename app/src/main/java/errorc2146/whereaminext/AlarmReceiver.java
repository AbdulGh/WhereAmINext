package errorc2146.whereaminext;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import errorc2146.whereaminext.tablelogic.Timetable;
import errorc2146.whereaminext.tablelogic.TimetableEntry;
import errorc2146.whereaminext.tablelogic.WeekTime;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (MainActivity.getNotification() && errorc2146.whereaminext.tablelogic.Timetable.getNextEntry() != null)
        {
            fireNotification(context);
            TimetableEntry te = Timetable.getNextEntry();
            ;
            Intent nexti = new Intent(context, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 1, nexti, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);

        }
    }

    private static void fireNotification(Context context)
    {
        Intent in = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,in,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.iconwhite)
                .setContentTitle("Where Am I Next?")
                .setOngoing(true)
                .setContentText(Timetable.getNextEntry().toString())
                .setContentIntent(pi);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());
    }
}
