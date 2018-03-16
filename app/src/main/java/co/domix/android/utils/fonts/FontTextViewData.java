package co.domix.android.utils.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import co.domix.android.R;

/**
 * Created by unicorn on 11/15/2017.
 */

public class FontTextViewData extends AppCompatTextView {
    public FontTextViewData(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public FontTextViewData(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public FontTextViewData(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(context.getString(R.string.font_cabin_regular), context);
        setTypeface(customFont);
    }
}
