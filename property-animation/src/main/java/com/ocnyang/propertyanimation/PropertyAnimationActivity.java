package com.ocnyang.propertyanimation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;

public class PropertyAnimationActivity extends AppCompatActivity {

    private View mPuppet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_animation);
        mPuppet = findViewById(R.id.view_puppet);
        initToolbar();

    }

    private void initToolbar() {
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_property, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.action_do_byxml) {
            doAnimation(getAnimationDrawable(true));
        } else if (i == R.id.action_bycode) {
            doAnimation(getAnimationDrawable(false));
        } else if (i == R.id.action_bycustom) {
            doAnimation(getValueAnimatorByCustom());
        } else if (i == R.id.action_byviewpropertyanimator) {
            doAnimatorByViewPropertyAnimator();
        } else if (i == R.id.action_bylayoutanimator) {
            doLayoutAnimator();
        }
        return super.onOptionsItemSelected(item);
    }

    private Animator getAnimationDrawable(boolean formXml) {
        return formXml ? getAnimationByXml() : getAnimatorSet();
    }

// Basic Usage (基本用法)----------------------------------------------------------------------------------

    /**
     * Animator usage
     *
     * @param animator
     */
    private void doAnimation(Animator animator) {
        animator.start();
        //animator.cancel();
        //animator.end();
        //animator.isPaused();
        //animator.isRunning();
        //animator.isStarted();
    }

    /**
     * 用XML表示的属性动画使用，需要通过 updateListener 里设置属性才会生效
     * @return
     */
    private Animator getAnimationByXml() {
//        mPuppet.getHeight()
        final int height = mPuppet.getLayoutParams().height;
        final int width = mPuppet.getLayoutParams().width;
        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.animatorset);

        //ValueAnimator属性动画中的时间驱动，管理着动画时间的开始、结束属性值，相应时间属性值计算方法等
        //特别注意：ValueAnimator 只是动画计算管理驱动，设置了作用目标，但没有设置属性，需要通过 updateListener 里设置属性才会生效
        ArrayList<Animator> childAnimations = animatorSet.getChildAnimations();
        ((ValueAnimator) childAnimations.get(childAnimations.size() - 1))
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float animatedValue = (float) valueAnimator.getAnimatedValue();
                        //   view.setXXX(value);  //必须通过这里设置属性值才有效
                        //   view.mXXX = value;  //不需要setXXX属性方法
                        mPuppet.getLayoutParams().height = (int) (height * animatedValue);
                        mPuppet.getLayoutParams().width = (int) (width * animatedValue);
                        mPuppet.requestLayout();
                    }
                });

        animatorSet.setTarget(mPuppet);
//        animatorSet.start();
        return animatorSet;
    }

    /**
     * ObjectAnimator动画
     * @param b
     * @return
     */
    public ObjectAnimator getObjectAnimator(boolean b) {
        if (b) {
            ObjectAnimator bgColorAnimator = ObjectAnimator.ofArgb(mPuppet,
                    "backgroundColor",
                    0xff009688, 0xff795548);
            bgColorAnimator.setRepeatCount(1);
            bgColorAnimator.setDuration(3000);
            bgColorAnimator.setRepeatMode(ValueAnimator.REVERSE);
            bgColorAnimator.setStartDelay(0);
            return bgColorAnimator;
        } else {
            ObjectAnimator rotationXAnimator = ObjectAnimator.ofFloat(mPuppet,
                    "rotationX",
                    0f, 360f);
            rotationXAnimator.setRepeatCount(1);
            rotationXAnimator.setDuration(3000);
            rotationXAnimator.setRepeatMode(ValueAnimator.REVERSE);
            return rotationXAnimator;
        }
    }

    /**
     * PropertyValuesHolder 多属性动画同时工作管理类,同时修改多个属性
     *
     * ObjectAnimator.ofPropertyValuesHolder,ValueAnimator.ofPropertyValuesHolder()
     *
     * @return
     */
    public Animator getObjectAnimatorByPropertyValuesHolder() {
        //背景变化,X轴360反转
        PropertyValuesHolder bgColorAnimator = PropertyValuesHolder.ofObject("backgroundColor",
                new ArgbEvaluator(),
                0xff009688, 0xff795548);
        PropertyValuesHolder rotationXAnimator = PropertyValuesHolder.ofFloat("rotationX",
                0f, 360f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mPuppet, bgColorAnimator, rotationXAnimator);
        objectAnimator.setDuration(3000);
        objectAnimator.setRepeatCount(1);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        return objectAnimator;
    }

    /**
     * ValueAnimator 使用，设置了作用目标，但没有设置属性，需要通过 updateListener 里设置属性才会生效。
     *
     * @return
     */
    public ValueAnimator getValueAnimator() {
        final int height = mPuppet.getLayoutParams().height;
        final int width = mPuppet.getLayoutParams().width;

        ValueAnimator sizeValueAnimator = ValueAnimator.ofFloat(1f, 3f);
        sizeValueAnimator.setDuration(3000);
        sizeValueAnimator.setRepeatCount(1);
        sizeValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        sizeValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
//                view.setXXX(value);  //必须通过这里设置属性值才有效
//                view.mXXX = value;  //不需要setXXX属性方法
                mPuppet.getLayoutParams().height = (int) (height * animatedValue);
                mPuppet.getLayoutParams().width = (int) (width * animatedValue);
                mPuppet.requestLayout();
            }
        });
        return sizeValueAnimator;
    }

    /**
     * AnimatorSet 使用,可设置动画的时序关系，如同时播放、顺序播放或延迟播放
     *
     * @return
     */
    public AnimatorSet getAnimatorSet() {
        AnimatorSet animatorSet = new AnimatorSet();

        // play together
        {
//            animatorSet.play(getObjectAnimator(true))
//                    .with(getObjectAnimator(false))
//                    .with(getValueAnimator());
        }

        //play sequentially
        {
//            animatorSet.play(getObjectAnimator(true))
//                    .after(getObjectAnimator(false))
//                    .before(getValueAnimator());
        }

        animatorSet.playTogether(getObjectAnimatorByPropertyValuesHolder(), getValueAnimator());

        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        return animatorSet;
    }

// Advanced Usage (高级用法)--------------------------------------------------------------------------

    /**
     * 自定义加速插值器 Interpolator
     * 自定义计算一个属性值 Evaluator
     *
     * @return
     */
    private Animator getValueAnimatorByCustom() {
        final int height = mPuppet.getLayoutParams().height;
        final int width = mPuppet.getLayoutParams().width;
        PropertyBean startPropertyBean = new PropertyBean(0xff009688, 0f, 1f);
        PropertyBean endPropertyBean = new PropertyBean(0xff795548, 360f, 3.0f);

//        ValueAnimator valueAnimator = ValueAnimator.ofObject(new MyTypeEvaluator(),startPropertyBean,endPropertyBean);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new SpeedUpInterpolator());//custom interpolator
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(1);

        valueAnimator.setObjectValues(startPropertyBean, endPropertyBean);
        valueAnimator.setEvaluator(new MyTypeEvaluator());//custom evaluator

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PropertyBean propertyBean = (PropertyBean) valueAnimator.getAnimatedValue();
                if (propertyBean.getBackgroundColor() != 0 && propertyBean.getBackgroundColor() != 1) {
                    mPuppet.setBackgroundColor(propertyBean.getBackgroundColor());
                }
                mPuppet.setRotationX(propertyBean.getRotationX());
                mPuppet.getLayoutParams().height = (int) (height * propertyBean.getSize());
                mPuppet.getLayoutParams().width = (int) (width * propertyBean.getSize());
                mPuppet.requestLayout();
//                mPuppet.postInvalidate();
            }
        });
        return valueAnimator;
    }

    //ViewPropertyAnimator------------------------------------------------------------------------------
    private void doAnimatorByViewPropertyAnimator() {
        //x轴旋转360 放大3
        ViewPropertyAnimator viewPropertyAnimator = mPuppet.animate()
                .rotationX(360f)
                .alpha(0.5f)
                .scaleX(3).scaleY(3)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(3000)
                .setStartDelay(0);
    }

    //LayoutAnimator--------------------------------------------------------------------------------
    private void doLayoutAnimator() {
        LayoutTransition layoutTransition = new LayoutTransition();
        //APPEARING 当View出现或者添加的时候View出现的动画
        layoutTransition.setAnimator(LayoutTransition.APPEARING, getObjectAnimator(false));
        //DISAPPEARING 当View消失或者隐藏的时候View消失的动画
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, getObjectAnimator(true));
        layoutTransition.setDuration(2000);

        //mPuppet's parentView
        ViewGroup contentView = (ViewGroup) ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
        contentView.setLayoutTransition(layoutTransition);
        if (contentView.findViewById(R.id.view_puppet) == null) {
            contentView.addView(mPuppet);
        } else {
            contentView.removeView(mPuppet);
        }
    }

}
