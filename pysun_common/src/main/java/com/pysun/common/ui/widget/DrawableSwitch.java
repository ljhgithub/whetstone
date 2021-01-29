package com.pysun.common.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;

import com.duia.ssx.pysun_common.R;
import com.pysun.common.utils.LogUtils;


/**
 * 一个简单的带提示文字的状态开关
 */
public class DrawableSwitch extends View {
    private final static String TAG = DrawableSwitch.class.getSimpleName();
    private float radius; // 圆的半径
    private int switchOnColor; // 开关打开时的背景颜色
    private int switchOffColor; // 开关关闭时的背景颜色
    private int switchOffShaderColor; // 开关关闭时的阴影背景颜色
    private float switchWidth; // 开关宽度
    private int circleColor; // 圆的填充颜色
    private int textColor; // 文字颜色
    private boolean isSwitchOn; // 开关的状态
    private BlurMaskFilter shaderMask;
    private boolean isShader = false;

    private Paint paint, paintShader; // 画笔
    private RectF rectOut, rectInner, rectShader; // 画中间的矩形
    private int gap = 4;

    private MySwitchStateChangeListener listener;

    public DrawableSwitch(Context context) {
        super(context);
    }

    public DrawableSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public DrawableSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    /**
     * 初始化操作，获得自定义的属性值
     *
     * @param attrs   属性集合
     * @param context 上下文
     */
    private void init(AttributeSet attrs, Context context) {
        LogUtils.LOGD(TAG, "init(),  isSwitchOn=" + isSwitchOn);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.drawableSwitch);


        radius = ta.getDimension(R.styleable.drawableSwitch_radius, 30); // 圆的半径
        switchWidth = ta.getDimension(R.styleable.drawableSwitch_switch_width, 3.5f * radius); // 圆的半径

        switchOnColor = ta.getColor(R.styleable.drawableSwitch_switchOnColor, Color.GREEN); // 开关打开时控件的底色
        circleColor = ta.getColor(R.styleable.drawableSwitch_circleColor, Color.WHITE); // 圆的颜色，默认为白色

        switchOffColor = ta.getColor(R.styleable.drawableSwitch_switchOffColor, Color.parseColor("#D5D5D5")); // 开关关闭时控件的底色
        switchOffShaderColor = ta.getColor(R.styleable.drawableSwitch_switchOffColor, Color.parseColor("#BCBCBC")); // 开关关闭时控件的底色

        textColor = ta.getColor(R.styleable.drawableSwitch_textColor, Color.BLACK); // 文字颜色，默认为黑色
        isSwitchOn = ta.getBoolean(R.styleable.drawableSwitch_isSwitchOn, false); // 控件的开关状态
        isShader = ta.getBoolean(R.styleable.drawableSwitch_isShader, false); // 是否有阴影样式
        if (isShader) {
            gap = 4;
        } else {
            gap = 8;
        }
        ta.recycle();
        paint = new Paint(); // 画笔对象
        paint.setAntiAlias(true);// 设置抗锯齿
        paint.setStyle(Paint.Style.FILL);// 设置抗锯齿
        paintShader = new Paint(paint);
        paintShader.setStyle(Paint.Style.STROKE);
        paintShader.setColor(switchOffShaderColor);
        shaderMask = new BlurMaskFilter(gap / 2, BlurMaskFilter.Blur.NORMAL);
        paintShader.setMaskFilter(shaderMask);

        rectOut = new RectF();
        rectInner = new RectF();
        rectShader = new RectF();


        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
            }
        });
    }

    /**
     * 实现更改开关状态，此处需要重绘界面
     */
    protected void changeStatus() {
        LogUtils.LOGD(TAG, "changeStatus...");
        isSwitchOn = !isSwitchOn;

        if (listener != null) {
            listener.mySwitchStateChanged(isSwitchOn); // 监听开关状态更改事件
        }
        this.postInvalidate(); // 状态更改之后还要更新一下界面
    }

    protected void setShader(boolean isShader) {
        this.isShader = isShader;

        this.postInvalidate(); // 状态更改之后还要更新一下界面
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 系统帮我们测量的高度和宽度都是MATCH_PARNET，当我们设置明确的宽度和高度时，
        // 系统帮我们测量的结果就是我们设置的结果，当我们设置为WRAP_CONTENT,或者MATCH_PARENT时
        // 系统帮我们测量的结果就是MATCH_PARENT的长度。
        // 所以，当设置了WRAP_CONTENT时，我们需要自己进行测量，即重写onMesure方法”
        // 重写之前先了解MeasureSpec的specMode,一共三种类型：
        // MeasureSpec.EXACTLY 一般是设置了明确的值或者是MATCH_PARENT
        // MeasureSpec.AT_MOST 表示子布局限制在一个最大值内，一般为WARP_CONTENT
        // MeasureSpec.UNSPECIFIED表示子布局想要多大就多大，很少使用

        int width = 0;
        int height = 0;
        // 设置宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY: // 明确指定
                width = specSize;
                break;
            case MeasureSpec.AT_MOST:
                width = (int) switchWidth;
                break;
        }

        // 设置高度
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                height = specSize;
                break;
            case MeasureSpec.AT_MOST:
                height = (int) (2 * radius );
                break;
        }
        LogUtils.LOGD(TAG, "onMeasure(),width=" + width + ",height=" + height);
        setMeasuredDimension(width, height);
    }

    /**
     * 绘制操作的具体实现
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if (getWidth()<switchWidth){
            switchWidth=getWidth();
        }
        if (isShader){
            drawShaderStyle(canvas);
        }else {
            drawStyle(canvas);
        }

    }

    private void drawStyle(Canvas canvas) {
        // super.onDraw(canvas);
        // 图形的绘制：左边一个半圆，中间一个矩形，右边一个半圆。实际绘制时，分两种情况：switchOn和switchOff
        // switchOn：先绘制左边的整个圆，然后再绘制矩形，然后改变颜色，绘制右边的圆，中间的矩形被右边的圆覆盖了一部分
        // switchOff：先绘制中间的矩形，再绘制右边的圆，右边的圆的颜色和矩形相同，然后再更改颜色，绘制左边的圆
        LogUtils.LOGD(TAG, "onDraw(),  isSwitchOn=" + isSwitchOn);

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float roundRadius = radius;
        float circleRadius = radius - gap;

        float gapHalf = gap / 2;
        float switchWidthHalf = switchWidth / 2;
        rectOut.set(centerX - switchWidthHalf, centerY - roundRadius, centerX + switchWidthHalf, centerY + roundRadius);
        rectShader.set(rectOut.left + gapHalf, rectOut.top + gapHalf, rectOut.right - gapHalf, rectOut.bottom - gapHalf);
        rectInner.set(rectOut.left + gap, rectOut.top+gap, rectOut.right - gap, rectOut.bottom-gap);


        if (isSwitchOn == true) { // 打开时的状态
            paint.setColor(switchOnColor); // 开关打开时控件的底色
            canvas.drawRoundRect(rectOut, roundRadius, roundRadius, paint);
            paint.setColor(circleColor);
            canvas.drawCircle(rectInner.right - circleRadius, rectInner.top + circleRadius, circleRadius, paint); // 画右边的圆

        } else { // 关闭时的状态
            paint.setColor(switchOffColor);
            canvas.drawRoundRect(rectOut, radius, radius, paint);
            paint.setColor(circleColor);//开关圆的颜色
            canvas.drawCircle(rectInner.left + circleRadius, rectInner.top + circleRadius, circleRadius, paint); // 再画左边的圆


        }
    }

    private void drawShaderStyle(Canvas canvas) {
        // super.onDraw(canvas);
        // 图形的绘制：左边一个半圆，中间一个矩形，右边一个半圆。实际绘制时，分两种情况：switchOn和switchOff
        // switchOn：先绘制左边的整个圆，然后再绘制矩形，然后改变颜色，绘制右边的圆，中间的矩形被右边的圆覆盖了一部分
        // switchOff：先绘制中间的矩形，再绘制右边的圆，右边的圆的颜色和矩形相同，然后再更改颜色，绘制左边的圆
        LogUtils.LOGD(TAG, "onDraw(),  isSwitchOn=" + isSwitchOn);

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float roundRadius = radius;
        float circleRadius = radius - gap;

        float gapHalf = gap / 2;
        float switchWidthHalf = switchWidth / 2;
        rectOut.set(centerX - switchWidthHalf, centerY - roundRadius, centerX + switchWidthHalf, centerY + roundRadius);
        rectShader.set(rectOut.left + gapHalf, rectOut.top + gapHalf, rectOut.right - gapHalf, rectOut.bottom - gapHalf);
        rectInner.set(rectOut.left + gap, rectOut.top+gap, rectOut.right - gap, rectOut.bottom-gap);


        if (isSwitchOn == true) { // 打开时的状态
            paint.setColor(switchOnColor); // 开关打开时控件的底色
            canvas.drawRoundRect(rectOut, roundRadius, roundRadius, paint);
            paint.setColor(circleColor);
            canvas.drawCircle(rectInner.right - circleRadius, rectInner.top + circleRadius, circleRadius , paint); // 画右边的圆

//
//            paint.setColor(textColor); // 文字颜色设置为布局文件里的值
//            paint.setTextSize(24); // 设置文字大小
//            String text = "开";
//            float baseline = centerY + getBaseline(paint); // 计算文字绘制时的baseline
//            float textWidth = paint.measureText(text, 0, text.length());
//            LogUtils.LOGE("textWidth " + textWidth + "   " + switchWidth + "    " + roundRadius);
//            LogUtils.LOGE("textWidth " + rectOut.left + "   " + (switchWidth - 2 * roundRadius - textWidth) / 2);
//            canvas.drawText(text, rectOut.left + (switchWidth - 2 * circleRadius - gap - textWidth) / 2, baseline, paint); // 绘制文字，//打开时不需要获取绘制文字的初始x坐标，直接为radius
        } else { // 关闭时的状态
            switchOffShaderColor=Color.parseColor("#BCBCBC");
            paintShader.setColor(switchOffShaderColor);
            setLayerType(View.LAYER_TYPE_SOFTWARE, paintShader);
//            setLayerType(View.LAYER_TYPE_HARDWARE, null);
            canvas.drawRoundRect(rectShader, roundRadius - gapHalf, roundRadius - gapHalf, paintShader);

            switchOffColor=Color.parseColor("#F8F8F8");
            paint.setColor(switchOffColor);
            canvas.drawRoundRect(rectInner, circleRadius, circleRadius, paint);

            paint.setColor(circleColor);//开关圆的颜色
            canvas.drawCircle(rectInner.left + circleRadius, rectInner.top + circleRadius, circleRadius, paint); // 再画左边的圆


            paintShader.setColor(Color.parseColor("#D5D5D5"));
            setLayerType(View.LAYER_TYPE_SOFTWARE, paintShader);
            canvas.drawCircle(rectInner.left + circleRadius, rectInner.top + circleRadius, circleRadius, paintShader); // 再画左边的圆

//            paint.setColor(textColor); // 文字颜色设置为布局文件里的值
//            paint.setTextSize(24); // 设置文字大小
//            String text = "关";
//            float baseline = centerY + getBaseline(paint); // 计算文字绘制时的baseline
//            float textWidth = paint.measureText(text, 0, text.length());
//            LogUtils.LOGE("textWidth" + "   " + switchWidth + "    " + roundRadius);
//            LogUtils.LOGE("textWidth "+rectOut.right+"   "+ (switchWidth - 2 * roundRadius - textWidth) / 2);
//            canvas.drawText(text, rectOut.right - (switchWidth - 2 * circleRadius - gap) / 2 - textWidth / 2, baseline, paint); // 绘制文字，//打开时不需要获取绘制文字的初始x坐标，直接为radius
        }
    }

    /**
     * 获得控件的开启状态
     *
     * @return isSwitchOn 开关的状态
     */
    public boolean isSwitchOn() {
        return isSwitchOn;
    }

    /**
     * 设置控件的开关状态
     *
     * @param isSwitchOn 开关的状态
     */
    public void setSwitchOn(boolean isSwitchOn) {
        this.isSwitchOn = isSwitchOn;
        this.postInvalidate();
    }

    /**
     * 设置监听器，监听控件开关状态是否改变
     *
     * @param myListener 外部传入的监听器，用来监听控件的开关状态
     */
    public void setListener(MySwitchStateChangeListener myListener) {
        this.listener = myListener;
    }

    /**
     * 定义的一个内部接口，用来监听控件状态更改事件
     */
    public interface MySwitchStateChangeListener {
        public void mySwitchStateChanged(boolean isSwitchOn);
    }

    /**
     * 计算绘制文字时的基线到中轴线的距离
     *
     * @param p
     * @return 基线和centerY的距离
     */
    public float getBaseline(Paint p) {
        FontMetrics fontMetrics = p.getFontMetrics();
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

}
