package com.mysports.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mysports.android.SmallActivity.OneDataActivity;
import com.mysports.android.map.DbAdapter;
import com.mysports.android.map.PathRecord;
import com.mysports.android.map.RecordAdapter;

import java.util.ArrayList;
import java.util.List;


public class RecordListActivity extends AppCompatActivity implements OnItemClickListener {
    private RecordAdapter mAdapter;
    private ListView mAllRecordListView;
    private DbAdapter mDataBaseHelper;
    private List<PathRecord> mAllRecord = new ArrayList<PathRecord>();
    public static final String RECORD_ID = "record_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.recordlist);
        mAllRecordListView = (ListView) findViewById(R.id.recordlist);
        mDataBaseHelper = new DbAdapter(this);
        mDataBaseHelper.open();
        searchAllRecordFromDB();
        mAdapter = new RecordAdapter(this, mAllRecord);
        mAllRecordListView.setAdapter(mAdapter);
        mAllRecordListView.setOnItemClickListener(this);
        mAdapter.setOnDeleteItem(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = (Integer) v.getTag(); //当前位置
                final PathRecord recorditem = (PathRecord) mAdapter.getItem(position);
                Log.d("idididid", "onClick: "+position+" id"+recorditem.getId());
                new AlertDialog.Builder(RecordListActivity.this).setTitle("提示").setMessage("确认删除本条运动记录?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mDataBaseHelper.delete(recorditem.getId())) {
                                    mAllRecord.remove(position);
                                    mAdapter.notifyDataSetChanged();
                                    Toast.makeText(RecordListActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(RecordListActivity.this,"删除失败",Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
        //清空记录
        deleteAllRecord = (ImageView) findViewById(R.id.delete_all_record);

        deleteAllRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RecordListActivity.this).setTitle("提示").setMessage("确认删除所有记录?")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mDataBaseHelper.deleteAll()) {
                                    Toast.makeText(RecordListActivity.this,"成功删除记录",Toast.LENGTH_SHORT).show();
                                    mAdapter.clear();
                                    mAdapter.notifyDataSetChanged();
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });
    }

    private void searchAllRecordFromDB() {
        mAllRecord = mDataBaseHelper.queryRecordAll();
    }

    public void onBackClick(View view) {
        this.finish();
    }

    private ImageView deleteAllRecord;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        PathRecord recorditem = (PathRecord) parent.getAdapter().getItem(
                position);
//        Intent intent = new Intent(RecordListActivity.this,
//                RecordShowActivity.class);
        Intent intent = new Intent(RecordListActivity.this,
                OneDataActivity.class);
        intent.putExtra(RECORD_ID, recorditem.getId());
        startActivity(intent);
    }
}
