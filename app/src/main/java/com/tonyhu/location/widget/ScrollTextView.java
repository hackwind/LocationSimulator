
package com.tonyhu.location.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

public class ScrollTextView extends android.support.v7.widget.AppCompatTextView {

    ArrayList<String> lineStrings;


    float currentY;


    Handler handler;


    String scrollText="";
    


    private int exactlyWidth = -1;


    private int exactlyHeight = -1;

    public String getScrollText() {
        return scrollText;
    }

    public void setScrollText(String scrollText) {
        this.scrollText = scrollText;
        reset();
    }


    private void reset() {
        requestLayout();
        invalidate();
        currentY = 0;

    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ScrollTextView(Context context) {
        super(context);
        init();
    }


    boolean scrolling = false;


    float absolutHeight = 0;

    int delayTime = 10;


    float speed = 0.5f;

    void init() {

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (absolutHeight <= getHeight()) {
                    currentY = 0;
                    stop();
                    return;
                }
                switch (msg.what) {

                    case 0: {
                        currentY = currentY - speed;

                        resetCurrentY();
                        invalidate();
                        handler.sendEmptyMessageDelayed(0, delayTime);
                        break;
                    }
                    case 1: {

                        currentY += msg.arg1;

                        resetCurrentY();
                        invalidate();
                    }
                }

            }


            private void resetCurrentY() {
                if (currentY >= absolutHeight || currentY <= -absolutHeight || getHeight() <= 0) {
                    currentY = 0;
                }

            }
        };
        
    }


    float lastY = 0;


    boolean needStop;

    public void pause() {
        if (scrolling) {

            stop();
            needStop = true;
        }
    }

    public void goOn() {

        if (needStop) {
            play();
            needStop = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                distanceY = lastY = event.getY();
                distanceX = event.getX();
                pause();

                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = event.getY() - lastY;
                lastY = event.getY();
                // currentY = currentY + dy;
                Message msg = Message.obtain();
                msg.what = 1;
                msg.arg1 = (int)dy;
                handler.sendMessage(msg);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                goOn();
                float y = event.getY() - distanceY;
                float x = event.getX() - distanceX;

                if (Math.sqrt(y * y + x * x) < performUpScrollStateDistance) {
                    updateScrollStatus();
                }
                return true;

        }
        return super.onTouchEvent(event);
    }


    public static final long performUpScrollStateDistance = 5;

    public float distanceY = 0;

    public float distanceX = 0;


    public void updateScrollStatus() {

        if (scrolling) {
            stop();
        } else {
            play();
        }
    }


    public void play() {

        if (!scrolling) {
            handler.sendEmptyMessage(0);
            scrolling = true;
        }
    }


    public void stop() {
        if (scrolling) {
            handler.removeMessages(0);
            scrolling = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureWidth(widthMeasureSpec);
        int height = MeasureHeight(width, heightMeasureSpec);
        setMeasuredDimension(width, height);
        currentY = 0;
        if (height < absolutHeight) {
            play();
        } else {
            stop();
        }

    }


    private int MeasureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (mode == MeasureSpec.AT_MOST) {

            double abwidth = getPaint().measureText(scrollText);

            width = Math.min((int)Math.rint(abwidth), width);
            exactlyWidth = -1;
        }
        if (mode == MeasureSpec.EXACTLY) {
            exactlyWidth = width;
        }
        return width;
    }


    private int MeasureHeight(int width, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        generateTextList(width);
        int lines = lineStrings.size();

        absolutHeight = lines * getLineHeight() + getPaddingBottom() + getPaddingTop();

        if (mode == MeasureSpec.AT_MOST) {

            height = (int)Math.min(absolutHeight, height);
            exactlyHeight = -1;

        } else if (mode == MeasureSpec.EXACTLY) {
            exactlyHeight = height;
        }
        return height;
    }


    boolean isENWordStart(String str, int i) {

        if (i == 0) {
            return true;

        } else if (str.charAt(i - 1) == ' ') {
            return true;
        }
        return false;
    }


    private String getLineText(int MaxWidth, String str) {


        StringBuffer trueStringBuffer = new StringBuffer();

        StringBuffer tempStringBuffer = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            String add = "";
            if (!isChinese(c) && isENWordStart(str, i)) {

                int place = getNextSpecePlace(i, str);

                if (place > -1) {
                    add = str.substring(i, place) + " ";
                    if (getPaint().measureText(add) > MaxWidth) {
                        add = "" + c;
                    } else {
                        i = place;
                    }
                } else {
                    add = "" + c;
                }
            } else {
                add = "" + c;
            }

            tempStringBuffer.append(add);
            String temp = tempStringBuffer.toString();
            float width = getPaint().measureText(temp.toString());

            if (width <= MaxWidth) {

                trueStringBuffer.append(add);
            } else {
                break;
            }

        }

        return trueStringBuffer.toString();

    }

    int getNextSpecePlace(int i, String str) {

        for (int j = i; j < str.length(); j++) {
            char c = str.charAt(j);
            if (c == ' ') {

                return j;
            }
        }
        return -1;
    }


    public void generateTextList(int MaxWidth) {
        lineStrings = new ArrayList<String>();
        String remain = scrollText;
        //加上几行空格，隔开连续两次循环
        lineStrings.add(" ");
        lineStrings.add(" ");
        lineStrings.add(" ");
        while (!remain.equals("")) {
            String line = getLineText(MaxWidth, remain);
            lineStrings.add(line);
            remain = remain.substring(line.length(), remain.length());

        }

    };

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        float x = getPaddingLeft();
        float y = getPaddingTop();

        float lineHeight = getLineHeight() ;
        float textSize = getPaint().getTextSize();

        for (int i = 0; i < lineStrings.size(); i++) {
            y = lineHeight * i + textSize + currentY;

            float min = 0;
            if (exactlyHeight > -1) {
                min = Math.min(min, exactlyHeight - absolutHeight);
            }
            if (y < min) {

                y = y + absolutHeight;

            } else if (y >= min && y < textSize + min) {

                canvas.drawText(lineStrings.get(i), x, y + absolutHeight, getPaint());
            }
            if (y >= absolutHeight) {
                canvas.drawText(lineStrings.get(i), x, y, getPaint());
                y = y - absolutHeight;
            }
            canvas.drawText(lineStrings.get(i), x, y, getPaint());
        }
    }


    private static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

}
