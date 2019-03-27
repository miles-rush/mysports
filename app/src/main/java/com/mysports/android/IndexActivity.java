package com.mysports.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class IndexActivity extends AppCompatActivity {
    private Button startRun;
    private Button recordList;
    private Button tip;
    private Button exit;

    private TextView username;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;
    private void init() {
        startRun = (Button) findViewById(R.id.start_run);
        recordList = (Button) findViewById(R.id.record_list);
        tip = (Button) findViewById(R.id.toast);

        username = (TextView) findViewById(R.id.username);
        exit = (Button) findViewById(R.id.exit);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        init();

        username.setText("欢迎:"+getIntent().getStringExtra("name"));
        startRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this,RecordActivity.class);
                startActivity(intent);
            }
        });

        recordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this,RecordListActivity.class);
                startActivity(intent);
            }
        });

        tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(IndexActivity.this,"info:2019.3.27:22:43:Jrh",Toast.LENGTH_SHORT).show();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(IndexActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
