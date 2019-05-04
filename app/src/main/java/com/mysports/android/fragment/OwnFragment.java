package com.mysports.android.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mysports.android.MainActivity;
import com.mysports.android.R;

import cn.bmob.v3.datatype.BmobFile;

public class OwnFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.own_fragment,container,false);
        init(view);
        return view;
    }

    private Button exit;
    private Button glide;
    private TextView name;
    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;
    private void init(View view) {
        exit = view.findViewById(R.id.exit);
        glide = view.findViewById(R.id.glide);
        name = view.findViewById(R.id.user_name);
        name.setText("欢迎:"+getActivity().getIntent().getStringExtra("name"));
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        glide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
}
