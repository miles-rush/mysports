package com.mysports.android.Community;
//社区运动记录界面的列表适配器

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysports.android.R;
import com.mysports.android.SmallActivity.PostRecordShowActivity;
import com.mysports.android.bomb.Record;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class RecordPostItemAdapter extends RecyclerView.Adapter<RecordPostItemAdapter.ViewHolder>{
    private List<Record> records;

    public RecordPostItemAdapter(List<Record> records) {
        this.records = records;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        TextView top;
        TextView end;
        TextView content;
        TextView goods;
        View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.record_post_name);
            time = view.findViewById(R.id.record_post_time);
            top = view.findViewById(R.id.r_text_top);
            end = view.findViewById(R.id.r_text_end);
            content = view.findViewById(R.id.r_content);
            goods = view.findViewById(R.id.r_goods);
        }
    }

    DecimalFormat distanceFormat = new DecimalFormat("0.00"); //距离格式
    SimpleDateFormat timeFormatTop = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat timeFormatEnd = new SimpleDateFormat("mm:ss");
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Record record = records.get(i);
        viewHolder.name.setText(record.getAuthor().getUsername());
        viewHolder.time.setText(record.getCreatedAt());
        String sDistance = record.getDistance();
        String sTime = record.getDuration();
        double distance = Double.parseDouble(sDistance);
        double time = Double.parseDouble(sTime);
        double km = 1.0* distance / 1000; //公里数
        double kmTime = 1.0 * time / km;

        viewHolder.top.setText("完成 "+distanceFormat.format(distance*1.0/1000)+
                "公里跑  时长:"+timeFormatTop.format(time*1000-8*60*60*1000));

        viewHolder.end.setText("每公里耗时: "+timeFormatEnd.format(kmTime*1000));

        viewHolder.content.setText(record.getText());
        viewHolder.goods.setText(record.getGoods()+"");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.record_post_item,viewGroup,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                Record record = records.get(pos);
                Intent intent = new Intent(v.getContext(),PostRecordShowActivity.class);
                intent.putExtra("ID",record.getObjectId());
                v.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return records.size();
    }
}
