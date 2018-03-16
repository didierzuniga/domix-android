package co.domix.android.utils.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import co.domix.android.R;

/**
 * Created by unicorn on 11/10/2017.
 */

public class FontEditText extends AppCompatEditText {
    public FontEditText(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(context.getString(R.string.font_montserrat_regular), context);
        setTypeface(customFont);
    }
}
