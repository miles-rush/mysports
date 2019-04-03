package com.mysports.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mysports.android.IndexActivity;
import com.mysports.android.R;
import com.mysports.android.RecordActivity;
import com.mysports.android.RecordListActivity;

public class ExecriseFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private Button goRun;
    private Button recordList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exercise_frame,container,false);
        init(view);
        return view;
    }

    private void init(final View view) {
        goRun = (Button) view.findViewById(R.id.go_run);
        recordList = (Button) view.findViewById(R.id.record_list);

        goRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RecordActivity.class);
                startActivity(intent);
            }
        });
        recordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RecordListActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
