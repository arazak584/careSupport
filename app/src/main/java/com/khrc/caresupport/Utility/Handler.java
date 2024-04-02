package com.khrc.caresupport.Utility;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import com.khrc.caresupport.R;


public class Handler {

    @BindingAdapter("goneUnless")
    public static void goneUnless(View view, Boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        if(view.isFocusable()) {
            view.requestFocus();
        }
    }


    @InverseBindingAdapter(attribute = "selection")
    public static int getSelectItem(Spinner view) {
        int selection = 0;
        if (view != null) {
            final KeyValuePair kv = (KeyValuePair) view.getSelectedItem();
            selection = kv.codeValue;
        }
        return selection;
    }

    @BindingAdapter("selection")
    public static void setSelectItem(Spinner view, int selection) {
        for (int i = 0; i < view.getCount(); i++) {
            final KeyValuePair kv = (KeyValuePair) view.getItemAtPosition(i);
            if (selection == 0) {
                view.setSelection(selection);
                return;
            }
            if (kv != null && kv.codeValue == selection) {
                view.setSelection(i);
                return;
            }
        }
    }

    @BindingAdapter("enableButton")
    public static void enableButton(Button vg, boolean enable) {
        vg.setEnabled(enable);
    }

    @BindingAdapter("enableControls")
    public static void enableControls(ViewGroup vg, boolean enable) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            if (child.isEnabled() != enable)
                child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                enableControls((ViewGroup) child, enable);
            }
        }
    }


    public static void colorLayouts(Context context, ViewGroup vg) {
        Resources res = context.getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.border_shape, null);


        int children = vg.getChildCount();
        for (int i = 0; i < children; i++) {
            View v = vg.getChildAt(i);


            if (!(v instanceof Button) && !(v instanceof EditText)) {

                if (v instanceof TextView) {
                    ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);

                    if (!(v instanceof RadioButton) && ((TextView) v).getMaxEms() <= 0 && !((TextView) v).getTypeface().isBold()) {
                        v.setBackground(drawable);
                    } else if (((TextView) v).getTypeface().isBold()) {
                        //((TextView) v).setTextColor(Color.BLACK);
                        v.setBackgroundColor(Color.TRANSPARENT);
                        v.setPadding(5, 5, 5, 5);
                        v.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 10, 0, 10);
                        v.setLayoutParams(params);

                    } else {
                        ((TextView) v).setTextColor(Color.MAGENTA);
                        ((TextView) v).setMinHeight(50);
                    }
                }
            }

            if (v instanceof Spinner) {
                v.setBackgroundColor(Color.TRANSPARENT);
            }

            if (v instanceof RadioButton) {
                ((RadioButton) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                ((RadioButton) v).setTextColor(Color.MAGENTA);
                ((RadioButton) v).setMinHeight(50);

            }

            if (v instanceof EditText) {
                ((EditText) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                ((EditText) v).setTextColor(Color.MAGENTA);
                ((EditText) v).setMinHeight(50);

                if(((EditText) v).getInputType() == InputType.TYPE_CLASS_TEXT){
                    ((EditText) v).setFilters(new InputFilter[] { new InputFilter.LengthFilter(70) });
                }

            }

            if (v instanceof ViewGroup) {
                colorLayouts(context, ((ViewGroup) v));

            }
        }
    }

    public boolean hasInvalidInput(ViewGroup vg, final boolean validate, boolean hasErrors) {

        boolean hasInvalidInput = hasErrors;

        if (validate && vg != null && vg.getVisibility() != View.GONE) {

            for (int i = 0; i < vg.getChildCount(); i++) {

                View child = vg.getChildAt(i);

                if (child.getVisibility() != View.GONE) {

                    if (child instanceof RadioGroup) {
                        RadioGroup v = (RadioGroup) child;
                        if (v.getCheckedRadioButtonId() == -1) {
                            ((TextView) v.getChildAt(0)).setError("Required");
                            Log.e("HANDLER.TAG", "RADIO BUTTON");
                            hasInvalidInput = true;
                        } else {

                            RadioButton r = ((RadioButton) v.findViewById(v.getCheckedRadioButtonId()));
                            if (r.getVisibility() == View.INVISIBLE) {
                                ((TextView) v.getChildAt(0)).setError("Required");
                                Log.e("HANDLER.TAG", "RADIO BUTTON");
                                hasInvalidInput = true;

                            } else {

                                ((TextView) v.getChildAt(0)).setError(null);
                            }

                        }
                    }

                    if (child instanceof Spinner) {
                        Spinner v = (Spinner) child;
                        TextView t = ((TextView) v.getChildAt(0));

                        if (v.getSelectedItemPosition() <= 0) {
                            if (t != null) {
                                t.setError("Required!");
                            }
                            Log.e("HANDLER.TAG", "DROPDOWN");
                            hasInvalidInput = true;
                        } else {
                            if (t != null) {
                                t.setError(null);
                            }
                        }

                    }

                    if (child instanceof EditText) {
                        EditText v = (EditText) child;
                        if (v.getText() == null || v.getText().toString().trim().isEmpty()) {
                            v.setError("Required!");
                            Log.e("HANDLER.TAG", "EDIT TEXT");
                            hasInvalidInput = true;
                        } else {
                            v.setError(null);
                        }
                    }

                    if (child instanceof TextView
                            && ((TextView) child).getMinHeight() == 50
                            && ((TextView) child).getMinEms() > 0) {
                        TextView v = (TextView) child;
                        if (v.getText() == null || v.getText().toString().trim().isEmpty()) {
                            v.setError("Required!");
                            Log.e("HANDLER.TAG", "DATE SELECTION");
                            hasInvalidInput = true;
                        } else {
                            v.setError(null);
                        }
                    }

                    if (child instanceof ViewGroup) {
                        hasInvalidInput = hasInvalidInput((ViewGroup) child, validate, hasInvalidInput);
                    }
                }
            }

        }

        return hasInvalidInput;
    }


}
