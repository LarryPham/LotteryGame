package com.techiedb.game.lottery.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.techiedb.game.lottery.Constants;
import com.techiedb.game.lottery.R;

public class LotteryCircleView extends View{
    public static final String TAG = LotteryCircleView.class.getSimpleName();
    public static final int TOTAL_DEGREES = 360;
    public static final int START_DEGREE = -90;

    private Paint mPaint;
    private Paint mDividerPaint;
    private Paint mPrizesPaint;

    private RectF mOvalRect = null;

    private int mItemCount = 8;
    private int mSweepAngle;

    private int mRadius;

    private Drawable mSpinHand;
    private Drawable mGiftDrawable;

    private int[] mPieBackgroundColors;
    // pass message to UI thread
    private Handler mHandler;

    private int mCanvasWidth = 480;
    private int mCanvasHeight = 480;
    private float mPieAngle = 0;

    private int mSpinHandCenterX = 156;
    private int mSpinHandCenterY = 230;

    private int mSpinHandHalfWidth = 0;
    private int mSpinHandHalfHeight = 0;

    private boolean mIsStopLooping;

    private String[] mGiftTitles = { getResources().getString(R.string.first_gift_sample_content), getResources().getString(R.string.second_gift_sample_content),
        getResources().getString(R.string.third_gift_sample_content), getResources().getString(R.string.good_luck_gift_sample_content),
        getResources().getString(R.string.first_gift_sample_content), getResources().getString(R.string.second_gift_sample_content),
        getResources().getString(R.string.third_gift_sample_content), getResources().getString(R.string.good_luck_gift_sample_content)};

    private String[] mPrizeTitles = {getResources().getString(R.string.first_prize_sample_content), getResources().getString(R.string.second_prize_sample_content),
            getResources().getString(R.string.third_prize_sample_content), getResources().getString(R.string.good_luck_sample_content),
            getResources().getString(R.string.first_prize_sample_content), getResources().getString(R.string.second_prize_sample_content),
            getResources().getString(R.string.third_prize_sample_content),getResources().getString(R.string.good_luck_sample_content)};

    private Bitmap mPiceIcon;
    private int[] mColors = { getResources().getColor(R.color.color_ffdf45), getResources().getColor(R.color.color_ff9436),
            getResources().getColor(R.color.color_dd001f), getResources().getColor(R.color.color_e300c5),
            getResources().getColor(R.color.color_00cbfb), getResources().getColor(R.color.color_0096c6),
            getResources().getColor(R.color.color_3E00C4), getResources().getColor(R.color.color_00DB3C)};

    public void setHandler(Handler handler){
        this.mHandler = handler;
    }

    public Handler getHandler(){
        return this.mHandler;
    }

    public void setItemCount(int itemCount){
        this.mItemCount = itemCount;
        requestLayout();
        invalidate();
    }

    public int getItemCount(){
        return this.mItemCount;
    }

    public void setSweepAngle(int sweepAngle){
        this.mSweepAngle= sweepAngle;
        requestLayout();
        invalidate();
    }

    public int getSweepAngle(){
        return this.mSweepAngle;
    }

    public LotteryCircleView(Context context) {
        super(context);
    }

    public LotteryCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPrizesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray styles = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LotteryCircleView, 0, 0);
        try {
            //int dividerColor = styles.getColor(R.styleable.LotteryCircleView_SliceDivider, android.R.color.darker_gray);
            mRadius = (int) styles.getDimension(R.styleable.LotteryCircleView_Radius, 200);
            mItemCount = styles.getInteger(R.styleable.LotteryCircleView_ItemCount, 8);
            mSweepAngle = styles.getInteger(R.styleable.LotteryCircleView_SweepAngle, 0);

        }finally {
            styles.recycle();
        }
        mSweepAngle = TOTAL_DEGREES/ mItemCount;
        if (Build.VERSION.SDK_INT >= 11){
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        init();
    }

    public int getRadius(){
        final int width = getWidth();
        final int height = getHeight();
        final float minDimen = width > height ? height:width;
        float radius = (minDimen - mRadius)/2f;
        return (int) radius;
    }

    public void getCenter(PointF pointF){
        pointF.set(getWidth()/2f, getHeight()/2f);
    }

    public LotteryCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(){
        Resources res = getResources();
        mSpinHand = res.getDrawable(R.drawable.btn_spin_normal);
        mGiftDrawable = res.getDrawable(R.drawable.gift_icon);
        mPiceIcon =  BitmapFactory.decodeResource(getResources(), R.drawable.gift_icon);

        mSpinHandHalfWidth = mSpinHand.getIntrinsicWidth()/2;
        mSpinHandHalfHeight = mSpinHand.getIntrinsicHeight()/2;

        mSpinHandCenterX = mCanvasWidth/2;
        mSpinHandCenterY = mCanvasHeight/2;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (mOvalRect == null){
            mOvalRect = new RectF(width/2 - mRadius, height/2 - mRadius, width/2 + mRadius, height/2 +mRadius);
        }
        for (int i= 0; i< mItemCount; i++){
            drawPiePrizes(canvas, i);
        }
        //Drawing the SpinControl
        if (mSpinHand != null){
            canvas.save();
            mSpinHand.setBounds(width/2 - mSpinHand.getIntrinsicWidth()/2, height/2 - mSpinHand.getIntrinsicHeight()/2,
                    width/2 + mSpinHand.getIntrinsicWidth()/2, height/2 + mSpinHand.getIntrinsicHeight()/2);
            mSpinHand.draw(canvas);
            canvas.restore();
        }
    }

    public void drawPiePrizes(Canvas canvas, int index){
        // Drawing Arc Pie
        int startAngle = START_DEGREE + index * mSweepAngle;
        int rotateAngle = startAngle + mSweepAngle/2;
        mPaint.setColor(mColors[index]);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);
        // Drawing border
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);

        int centerX = (int) ((3*(mRadius)) / 4* Math.cos(Math.toRadians(startAngle + mSweepAngle/2)));
        int centerY = (int) ((3*(mRadius)) / 4* Math.sin(Math.toRadians(startAngle + mSweepAngle/2)));


        // Drawing gift icons
        if (mPiceIcon != null){
            canvas.save();
            canvas.translate(getWidth()/2 + centerX , getHeight()/2 + centerY);
            canvas.rotate(0 + index * mSweepAngle + mSweepAngle/2);
            canvas.drawBitmap(mPiceIcon,0-mPiceIcon.getWidth()/2,0-mPiceIcon.getHeight()/2, null);

            mPrizesPaint.setColor(Color.WHITE);
            mPrizesPaint.setStrokeWidth(2.0f);
            canvas.drawText(mPrizeTitles[index],- mPiceIcon.getWidth() /2 , mPiceIcon.getHeight(), mPaint);

            mPrizesPaint.setColor(Color.WHITE);
            mPrizesPaint.setStrokeWidth(2.0f);
            canvas.drawText(mGiftTitles[index],  - mPiceIcon.getWidth() / 2,
                    mPiceIcon.getHeight() / 2, mPrizesPaint);

            mPrizesPaint.setColor(Color.BLACK);
            canvas.drawCircle(0, 0, 8, mPrizesPaint);
            canvas.restore();
        }

    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int xPadding = (getPaddingLeft() + getPaddingRight());
        int yPadding = (getPaddingTop() + getPaddingBottom());
        mCanvasHeight = h - yPadding;
        mCanvasWidth = w - xPadding;
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        return super.onTouchEvent(motionEvent);
    }

    public void updateLotteryState(boolean appRunning){
        long now = System.currentTimeMillis();

    }
    public void savedState(SharedPreferences.Editor map){

    }

    public synchronized void restoreState(SharedPreferences savedState){

    }
    public void broadCastLotteryPrice(double prize){
        Bundle args = new Bundle();
        if (prize > 0){
            args.putString(Constants.PRIZE_NAME,"NAME");
            args.putDouble(Constants.PRIZE_VALUE, prize);
        }else{
            args.putString(Constants.PRIZE_NAME, "GOOD_LUCK");
            args.putDouble(Constants.PRIZE_GOOD_LUCK,0.0f);
        }
        sendMessageToHandler(args);
    }

    public void sendMessageToHandler(Bundle bundle){
        if (mHandler != null){
            Message msg = mHandler.obtainMessage();
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }
}
