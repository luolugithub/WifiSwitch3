package xyz.victor97.wifiswitch.wifiswitch3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ProgressView extends View {
    private int R;
    private Paint p = new Paint();
    private int progress = 0;
    private RectF mRectF;

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        R = DensityUtil.dp2px(context, 130);
        mRectF = new RectF(0, 0, 2 * R, 2 * R);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setColor(Color.WHITE);
        canvas.drawArc(mRectF, 270, progress, true, p);
    }

    public void setProgress(int progress) {
        if (progress < 0) progress = 0;
        if (progress > 360) progress = 360;
        this.progress = progress;
        postInvalidate();
    }
}
