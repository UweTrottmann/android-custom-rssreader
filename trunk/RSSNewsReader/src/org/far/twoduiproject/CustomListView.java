
package org.far.twoduiproject;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class CustomListView extends ListView {

    boolean firstRun = false;

    private Context mContext;

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (firstRun) {
            for (int step = 0; step < 3; step++) {
                View v = getChildAt(step);
                if (v != null) {
                    ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP,
                            RSSNewsReader.EXPANDED_FONTSIZE - step * 7 - 1);
                }
            }
            firstRun = false;
        }

    }

    /**
     * Adjusts the text sizes of the first few items, if a fisheye layout is
     * used.
     */
    public void setFirstRun() {
        if (!RSSNewsReader.isSimpleList(mContext)) {
            firstRun = true;
        }
    }
}
