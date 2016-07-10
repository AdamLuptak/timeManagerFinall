package com.adam.sk.workingtimemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;
import com.melnykov.fab.FloatingActionButton;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkTimeRecordsFragment extends Fragment {

    private Context thisContext;

    @BindView(R.id.workTimeRecordview)
    public ListView listViewWorkTimerecord;

    @BindView(R.id.fab)
    public FloatingActionButton fabButton;

    public WorkTimeRecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_time_records, container, false);
        thisContext = container.getContext();
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List workTimeRecords = WorkTimeRecord.listAll(WorkTimeRecord.class);
        WorkTimeRecordListAdapter workTimeRecordListAdapter = new WorkTimeRecordListAdapter(
                thisContext.getApplicationContext(), workTimeRecords);
        listViewWorkTimerecord.setAdapter(workTimeRecordListAdapter);

        fabButton.attachToListView(listViewWorkTimerecord);

        listViewWorkTimerecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(thisContext,
                        WorkTimeRecordEditActivity.class);
                intent.putExtra("WorkTimeRecord", (WorkTimeRecord) parent.getItemAtPosition(position));
                startActivityForResult(intent, 0);
            }
        });

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext,
                        WorkTimeRecordEditActivity.class);
                WorkTimeRecord workTimeRecord = new WorkTimeRecord(new Date(), new Date());
                intent.putExtra("WorkTimeRecord", workTimeRecord);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
