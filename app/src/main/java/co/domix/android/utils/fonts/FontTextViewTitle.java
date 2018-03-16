package co.domix.android.utils.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import co.domix.android.R;

/**
 * Created by unicorn on 11/10/2017.
 */

public class FontTextViewTitle extends AppCompatTextView {
    public FontTextViewTitle(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public FontTextViewTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public FontTextViewTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(context.getString(R.string.font_shrikhand_regular), context);
        setTypeface(customFont);
    }

}
