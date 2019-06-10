package com.mysports.android.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mysports.android.MainActivity;
import com.mysports.android.R;
import com.mysports.android.SmallActivity.DataFormActivity;
import com.mysports.android.SmallActivity.OneDataActivity;
import com.mysports.android.SmallActivity.UserShowActivity;
import com.mysports.android.bomb.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class OwnFragment extends Fragment {
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.own_fragment,container,false);
            init(view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private ImageView exit;
    private ImageView about;
    private ImageView edit;
    private ImageView pic;

    private TextView name;
    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;
    private void init(final View view) {
        exit = view.findViewById(R.id.exit);
        about = view.findViewById(R.id.about);
        edit = view.findViewById(R.id.edit_password);
        pic = view.findViewById(R.id.own_pic);

        name = view.findViewById(R.id.user_name);
        name.setText(getActivity().getIntent().getStringExtra("name"));
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UserShowActivity.class);
                intent.putExtra("ID",BmobUser.getCurrentUser(User.class).getObjectId());
                startActivity(intent);
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = BmobUser.getCurrentUser(User.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("用户信息:")
                        .setMessage("账户名:"+user.getUsername()+"\n"+"注册时间:"+user.getCreatedAt()+"\n"+"账户编号:"+user.getObjectId())
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                BmobUser.logOut(); //注销时清除缓冲
                getActivity().finish();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("About")
                        .setMessage("项目GitHub地址 https://github.com/Persistr/mysports")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText checkText = new EditText(v.getContext());
                final EditText passwordText = new EditText(v.getContext());
                AlertDialog.Builder builderCheck = new AlertDialog.Builder(v.getContext());
                builderCheck.setTitle("输入旧密码").setView(checkText)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("验证", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String oldPassword = checkText.getText().toString().trim();
                        AlertDialog.Builder builderNew = new AlertDialog.Builder(view.getContext());
                        builderNew.setTitle("输入新密码").setView(passwordText)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newPassword = passwordText.getText().toString().trim();
                                BmobUser.updateCurrentUserPassword(oldPassword, newPassword, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
                                            Toast.makeText(view.getContext(),"密码修改成功",Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(view.getContext(),"操作失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).show();
                    }
                }).show();

            }
        });

    }
}
