package com.example.printexpress.ui.common;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Small helpers that make every form short:
 *
 *   boolean ok = Form.check(b.inputEmail, Validator.email(Form.text(b.inputEmail)))
 *              & Form.check(b.inputPassword, Validator.password(Form.text(b.inputPassword)));
 *
 * (use single & so ALL fields show their errors, not just the first one)
 */
public final class Form {

    private Form() {
    }

    /** Trimmed text of a TextInputLayout's field. */
    public static String text(TextInputLayout layout) {
        return layout.getEditText() == null || layout.getEditText().getText() == null
                ? "" : layout.getEditText().getText().toString().trim();
    }

    public static void setText(TextInputLayout layout, String value) {
        if (layout.getEditText() != null) layout.getEditText().setText(value);
    }

    /** Shows the error under the field (or removes it when error is null). Returns true if valid. */
    public static boolean check(TextInputLayout layout, String error) {
        layout.setError(error);
        layout.setErrorEnabled(error != null);
        return error == null;
    }

    /** Removes the red error as soon as the user starts typing again. */
    public static void clearErrorOnType(TextInputLayout... layouts) {
        for (TextInputLayout layout : layouts) {
            if (layout.getEditText() == null) continue;
            layout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (layout.getError() != null) {
                        layout.setError(null);
                        layout.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }
}
