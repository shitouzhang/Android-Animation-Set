package com.ocnyang.revealanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;

public class RevealEffectActivity extends AppCompatActivity implements View.OnClickListener {

    boolean flag = false;
    FloatingActionButton fab;
    Toolbar toolbar;
    private View mPuppet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal_effect);
        initToolbar();

        mPuppet = findViewById(R.id.view_puppet);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        doRevealAnimation();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        fab.postDelayed()
    }
//    Rect rect = new Rect();
//    view.getLocalVisibleRect(rect);
//    int cx = rect.left + rect.width() / 2;
//    int cy = rect.top + rect.height() / 2;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void doRevealAnimation() {
        int[] vLocation = new int[2];
        //获取宽高时机
        fab.getLocationInWindow(vLocation);//获取在当前窗口内的绝对坐标
        int centerX = vLocation[0] + fab.getMeasuredWidth() / 2;
        int centerY = vLocation[1] + fab.getMeasuredHeight() / 2;

        int height = mPuppet.getHeight();
        int width = mPuppet.getWidth();
        //x和y平方和的二次方根 //√（x²+y)--->求出要揭露 View 的对角线，来作为扩散圆的最大半径
        int maxRadius = (int) Math.hypot(height, width);//x和y平方和的二次方根 如果任一参数为无穷大，那么结果为正无穷大
        Log.e("hei", maxRadius + "");//1865

        if (flag) {
            //创建揭露动画 ViewAnimationUtils.createCircularReveal
            Animator animator = ViewAnimationUtils.createCircularReveal(mPuppet, centerX, centerY, maxRadius, 0);
            animator.setDuration(1000);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mPuppet.setVisibility(View.GONE);
                }
            });
            animator.start();
            flag = false;
        } else {
            //View显示
            Animator animator = ViewAnimationUtils.createCircularReveal(mPuppet, centerX, centerY, 0, maxRadius);
            animator.setDuration(500);
            //注意：这里显示 mPuppet 调用并没有在监听方法里，并且是在动画开始前调用。
//            mPuppet.setVisibility(View.VISIBLE);
//            为什么不在动画开始的回调方法里设置 View.VISIBLE ，在这里调用也是可以的，一般这样设置也不会有任何问题。
//            但我们在前面就说过揭露动画 是一个异步动画，它的回调方法都不能保证在准确的时间里调用(可能延迟调用，虽然很短)，
//            所以我们建议直接在调用动画开始方法 start() 直接设置 View.VISIBLE 为好。
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mPuppet.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();//揭露对象要先于动画开始前显示
            flag = true;
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
