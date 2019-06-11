package com.mysports.android.SmallActivity;

import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mysports.android.Community.CommentAdapter;
import com.mysports.android.R;
import com.mysports.android.bomb.Comment;
import com.mysports.android.bomb.LikesRelation;
import com.mysports.android.bomb.Post;
import com.mysports.android.bomb.User;
import com.mysports.android.media.GlideUtil;
import com.mysports.android.util.NotiferUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

//社区界面2的动态点击进入的界面
public class PostItemActivity extends AppCompatActivity {
    private Post downloadPost;
    private String ID;

    private TextView name; //用户姓名
    private TextView time; //发布时间
    private ImageView like; //关注

    private ImageView mainImage; //主图片
    private TextView mainText; //动态内容

    private ImageView good; //点赞按钮
    private TextView goodsText; //点赞数量

    private RecyclerView recyclerView; //评论区

    private EditText area;  //输入框

    private ImageView send; //发布

    private List<Comment> comments = new ArrayList<>(); //用来存放评论信息
    private LinearLayoutManager manager;
    private CommentAdapter adapter;

    private int goods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post_item);
        ID = getIntent().getStringExtra("ID");
        init();
        downloadPost();

    }
    private void init() {
        name = (TextView) findViewById(R.id.post_user_name);
        time = (TextView) findViewById(R.id.post_time);
        mainImage = (ImageView) findViewById(R.id.main_image);
        like = (ImageView) findViewById(R.id.like_the_user);
        mainText = (TextView) findViewById(R.id.main_text);
        good = (ImageView) findViewById(R.id.post_good);
        goodsText = (TextView) findViewById(R.id.post_goods_text);
        recyclerView = (RecyclerView) findViewById(R.id.comment_list);
        area = (EditText) findViewById(R.id.comment_area);
        send = (ImageView) findViewById(R.id.comment_post);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLike();
            }
        });

        //发布评论
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneSend();
            }
        });

        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneGoods();
            }
        });
    }


    private void doneGoods() {
        goods += 1;
        downloadPost.setPageView(goods);
        downloadPost.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //TODO:消息通知
                    NotiferUtil.notiferPostGood(ID, downloadPost.getAuthor().getObjectId());
                    Toast.makeText(PostItemActivity.this,"点赞成功",Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            goodsText.setText(""+goods);
                        }
                    });
                }else {
                    goods -= 1;
                    Toast.makeText(PostItemActivity.this,"点赞失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doneSend() {
        String text = area.getText().toString().trim();
        if (text.length() > 0) {
            User user = BmobUser.getCurrentUser(User.class);
            Post post = new Post();
            post.setObjectId(downloadPost.getObjectId());
            Comment comment = new Comment();
            comment.setUser(user);
            comment.setPost(post);
            comment.setContent(text);
            comment.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        //TODO:刷新评论区
                        downloadComments();
                        //TODO:消息通知
                        NotiferUtil.notiferPostComment(ID, downloadPost.getAuthor().getObjectId());
                        Toast.makeText(PostItemActivity.this,"评论发布成功",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PostItemActivity.this,"评论发布失败",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(PostItemActivity.this,"你还没有添加内容",Toast.LENGTH_SHORT).show();
        }
    }
    //下载该帖子的全部评论
    public void downloadComments() {
        BmobQuery<Comment> query = new BmobQuery<>();
        Post post = new Post();
        post.setObjectId(downloadPost.getObjectId());
        query.addWhereEqualTo("post",new BmobPointer(post));
        query.include("user,post");
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    comments.clear();
                    for (Comment comment : list) {
                        comments.add(comment);
                        Log.d("namelist", "done: "+comment.getUser().getUsername());
                    }
                    //这里对布局进行修正 因为底部不透明 导致一条信息无法显示 在这里加入一条
                    if (comments.size() > 5) {
                        comments.add(comments.get(0));
                    }
                    initCommentList();
                }else {
                    Toast.makeText(PostItemActivity.this,"评论内容加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //初始化评论列表
    private void initCommentList() {
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new CommentAdapter(comments,this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.scrollToPosition(comments.size()-2);
    }

    //添加关注
    private void doneLike() {
        LikesRelation relation = new LikesRelation();
        User master = new User();
        master.setObjectId(downloadPost.getAuthor().getObjectId());
        User guest = new User();
        guest.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());

        relation.setMaster(master);
        relation.setGuest(guest);

        relation.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    //TODO
                    NotiferUtil.notiferGetLike(ID, downloadPost.getAuthor().getObjectId());
                    Toast.makeText(getApplicationContext(),"关注成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),"关注失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //关注前判断
    private void checkLike() {
        User master = new User();
        master.setObjectId(downloadPost.getAuthor().getObjectId());
        User guest = new User();
        guest.setObjectId(BmobUser.getCurrentUser(User.class).getObjectId());

        BmobQuery<LikesRelation> eq1 = new BmobQuery<>();
        eq1.addWhereEqualTo("master",master);

        BmobQuery<LikesRelation> eq2 = new BmobQuery<>();
        eq2.addWhereEqualTo("guest",guest);

        List<BmobQuery<LikesRelation>> allEq = new ArrayList<>();
        allEq.add(eq1);
        allEq.add(eq2);

        BmobQuery<LikesRelation> query = new BmobQuery<>();
        query.and(allEq);

        query.findObjects(new FindListener<LikesRelation>() {
            @Override
            public void done(List<LikesRelation> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        doneLike();
                    }else {
                        Toast.makeText(getApplicationContext(),"你已关注",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    doneLike();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //由传入ID下载动态数据到本地
    private void downloadPost() {
        BmobQuery<Post> query = new BmobQuery<>();
        query.include("author");
        query.getObject(ID, new QueryListener<Post>() {
            @Override
            public void done(Post post, BmobException e) {
                if (e == null) {
                    Log.d("done", "done: "+post.getPicUrl());
                    downloadPost = post;
                    goods = downloadPost.getPageView();
                    dataSet();

                    //帖子信息下载后 下载评论信息
                    downloadComments();
                } else {
                    Toast.makeText(PostItemActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //第一次进入时布局初始化
    private void dataSet() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                name.setText(downloadPost.getAuthor().getUsername());
                time.setText(downloadPost.getCreatedAt());
                mainText.setText(downloadPost.getContent());
                goodsText.setText(""+goods);
            }
        });
        GlideUtil.initImageWithFileCache(PostItemActivity.this,downloadPost.getPicUrl(),mainImage);
    }

    public void postItemFinish(View view){
        finish();
    }

}
