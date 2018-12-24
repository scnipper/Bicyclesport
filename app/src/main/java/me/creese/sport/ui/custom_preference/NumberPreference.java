package me.creese.sport.ui.custom_preference;

import android.content.Context;
import android.support.v7.preference.EditTextPreference;
import android.util.AttributeSet;

import me.creese.sport.R;

public class NumberPreference extends EditTextPreference {


    public NumberPreference(Context context) {
        this(context, null);
    }

    public NumberPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextPreferenceStyle);
    }

    public NumberPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NumberPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public int getDialogLayoutResource() {
        return R.layout.preference_edittext;
    }
}
