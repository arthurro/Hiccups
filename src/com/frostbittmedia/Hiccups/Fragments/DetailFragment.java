package com.frostbittmedia.Hiccups.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.frostbittmedia.Hiccups.Activities.LogActivity;
import com.frostbittmedia.Hiccups.R;
import com.frostbittmedia.Hiccups.Utils.LogEventListAdapter;

public class DetailFragment extends Fragment {

    private ImageView eventImage;
    private TextView event;
    private TextView date;
    private TextView time;
    private TextView details;
    private Button deleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detailfragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LogEventListAdapter adapter = new LogEventListAdapter();

        eventImage = (ImageView)getActivity().findViewById(R.id.fragmentLogEntryDetailImage);
        adapter.setEventIconDetail(eventImage, LogActivity.listViewItemClicked);

        event = (TextView)getActivity().findViewById(R.id.fragmentLogEntryEventText);
        event.setText(LogActivity.listViewItemClicked.getEvent());

        date = (TextView)getActivity().findViewById(R.id.fragmentLogEntryEventDate);
        time = (TextView)getActivity().findViewById(R.id.fragmentLogEntryEventTime);
        adapter.setTime(time, date, LogActivity.listViewItemClicked);

        details = (TextView)getActivity().findViewById(R.id.fragmentLogEntryEventDetailText);
        details.setText(LogActivity.listViewItemClicked.getDetail());

        deleteBtn = (Button)getActivity().findViewById(R.id.fragmentDeleteLogEntry);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogActivity.dbClient.deleteTableRow(LogActivity.listViewItemClicked);
                LogActivity.logEventList.remove(LogActivity.listViewItemClickedPosition);
                LogActivity.listAdapter.notifyDataSetChanged();

                LogActivity.fragmentContainer.setVisibility(View.GONE);
                LogActivity.listContainer.setVisibility(View.VISIBLE);
                LogActivity.buttonPanel.setVisibility(View.VISIBLE);

                try{
                    LogActivity.fragmentManager.getBackStackEntryAt(0);
                    LogActivity.fragmentManager.popBackStack(LogActivity.fragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
