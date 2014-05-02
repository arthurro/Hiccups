package com.frostbittmedia.Hiccups.Utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.frostbittmedia.Hiccups.Objects.LogEvent;
import com.frostbittmedia.Hiccups.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LogEventListAdapter extends BaseAdapter {

    // ===========================================================
    // Fields
    // ===========================================================

    private static final String TAG = "LogEventListAdapter";

    private List<LogEvent> logEvents;
    private Context context;

    // ===========================================================
    // Constructors
    // ===========================================================

    public LogEventListAdapter(){}

    public LogEventListAdapter(final List<LogEvent> logEvents, Context context){
        this.logEvents = logEvents;
        this.context = context;
    }

    // ===========================================================
    // Methods from superclass/interface
    // ===========================================================

    @Override
    public int getCount() {
        return logEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return logEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        // Get the current list item
        final LogEvent event = logEvents.get(position);
        // Get the layout for the list item
        final LinearLayout logEventLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listview_singlerow_layout, viewGroup, false);
        // Set icon
        ImageView imageView = (ImageView) logEventLayout.findViewById(R.id.logEntryImage);
        setEventIcon(imageView, event);

        TextView eventText = (TextView) logEventLayout.findViewById(R.id.logEntryEventText);
        eventText.setText(event.getEvent());

        TextView detailText = (TextView) logEventLayout.findViewById(R.id.logEntryEventDetailText);
        detailText.setText(event.getDetail());

        TextView timeText = (TextView) logEventLayout.findViewById(R.id.logEntryEventTime);
        TextView dateText = (TextView) logEventLayout.findViewById(R.id.logEntryEventDate);
        setTime(timeText, dateText, event);

        return logEventLayout;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void setEventIcon(ImageView imageView, LogEvent event){
        if (event.getEvent().equals(LogEvent.FEEDING_EVENT))
            imageView.setBackgroundResource(R.drawable.feeding);
        if (event.getEvent().equals(LogEvent.SLEEPING_EVENT))
            imageView.setBackgroundResource(R.drawable.sleeping);
        if (event.getEvent().equals(LogEvent.WOKE_UP_EVENT))
            imageView.setBackgroundResource(R.drawable.wokeup);
        if (event.getEvent().equals(LogEvent.DIAPER_CHANGE_EVENT))
            imageView.setBackgroundResource(R.drawable.diaper);
        if (event.getEvent().equals(LogEvent.NOTE_EVENT))
            imageView.setBackgroundResource(R.drawable.note);
    }

    // Sets icons in DetailFragment
    public void setEventIconDetail(ImageView imageView, LogEvent event){
        if (event.getEvent().equals(LogEvent.FEEDING_EVENT))
            imageView.setBackgroundResource(R.drawable.feeding_button);
        if (event.getEvent().equals(LogEvent.SLEEPING_EVENT))
            imageView.setBackgroundResource(R.drawable.sleeping_button);
        if (event.getEvent().equals(LogEvent.WOKE_UP_EVENT))
            imageView.setBackgroundResource(R.drawable.wokeup_button);
        if (event.getEvent().equals(LogEvent.DIAPER_CHANGE_EVENT))
            imageView.setBackgroundResource(R.drawable.diaper_button);
        if (event.getEvent().equals(LogEvent.NOTE_EVENT))
            imageView.setBackgroundResource(R.drawable.note_button);
    }

    public void setTime(TextView timeTextView, TextView dateTextView, LogEvent event){
        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");


        Date now = new Date();
        String today = dayFormat.format(now);
        int todayNum = Integer.parseInt(today);

        try {
            Date logEventDateTime = sqlFormat.parse(event.getDateTime());
            String logEventDay = dayFormat.format(logEventDateTime);
            int logEventDayNum = Integer.parseInt(logEventDay);

            timeTextView.setText(timeFormat.format(logEventDateTime));

            if (logEventDayNum == todayNum)
                dateTextView.setText("Today");
            if (logEventDayNum == (todayNum-1))
                dateTextView.setText("Yesterday");
            if (logEventDayNum < (todayNum-1))
                dateTextView.setText(dateFormat.format(logEventDateTime));
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }
    }
}
