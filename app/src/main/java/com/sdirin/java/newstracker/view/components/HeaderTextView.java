package com.sdirin.java.newstracker.view.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sdirin.java.newstracker.R;

/**
 * Created by SDirin on 02-Feb-18.
 */

public class HeaderTextView extends TextView {

    private final Context context;

    public HeaderTextView(Context context) {
        super(context);
        this.context = context;
    }

    public HeaderTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public HeaderTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public HeaderTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    char firstLetter;

    TextView firstTV;

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null && text.length()>0) {
            setContentDescription(text);
            firstLetter = text.charAt(0);
            StringBuilder sb = new StringBuilder(firstLetter);
            int spacesCount = getLetterWidth(firstLetter);
            for(int i = 0;i<spacesCount;i++){
                sb.append(" ");
            }
            text = sb.append(text.subSequence(1,text.length())).toString();
        }
        super.setText(text, type);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (firstLetter != 0){
            drawFirstLetter(canvas);
        }
    }

    private void drawFirstLetter(Canvas canvas) {
        Paint paint = getFirstLetterPaint();
        paint.setColor(ContextCompat.getColor(context, R.color.firstLetter));
        canvas.drawText( Character.toString(firstLetter),0f,58f, paint);
    }

    @NonNull
    private Paint getFirstLetterPaint() {
        Paint paint = new Paint();
        paint.setTextSize(64f);
        Typeface normal = getTypeface();
        Typeface bold = Typeface.create(normal,Typeface.BOLD);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(bold);
        return paint;
    }

    int getLetterWidth(char letter){
        Paint paint = getFirstLetterPaint();
        Rect bounds = new Rect();
        paint.getTextBounds(Character.toString(firstLetter),0,1,bounds);

        Paint normalPaint = new Paint();
        normalPaint.setTextSize(getTextSize());
        Typeface normal = getTypeface();
        normalPaint.setTypeface(normal);
        Rect normalBounds = new Rect();
        normalPaint.getTextBounds("[",0,1,normalBounds);

        if (normalBounds.right-normalBounds.left > 0){
            return (bounds.right-bounds.left)/(normalBounds.right-normalBounds.left)+1;
        } else {
            return 5;
        }
    }


}
