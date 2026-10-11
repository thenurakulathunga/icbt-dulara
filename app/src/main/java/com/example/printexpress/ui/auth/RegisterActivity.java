package com.example.printexpress.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import com.example.printexpress.data.model.Requests;
import com.example.printexpress.databinding.ActivityRegisterBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.ui.common.Form;
import com.example.printexpress.ui.main.MainActivity;
import com.example.printexpress.util.Validator;

/** Create a new customer account. */
public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Form.clearErrorOnType(b.inputName, b.inputEmail, b.inputPhone, b.inputPassword, b.inputConfirm);
        b.buttonBack.setOnClickListener(v -> finish());
        b.textLogin.setOnClickListener(v -> finish());
        b.buttonRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String name = Form.text(b.inputName);
        String email = Form.text(b.inputEmail);
        String phone = Form.text(b.inputPhone);
        String password = Form.text(b.inputPassword);
        String confirm = Form.text(b.inputConfirm);

        // Single "&" so every invalid field shows its error at the same time
        boolean valid = Form.check(b.inputName, Validator.name(name))
                & Form.check(b.inputEmail, Validator.email(email))
                & Form.check(b.inputPhone, Validator.phone(phone))
                & Form.check(b.inputPassword, Validator.password(password))
                & Form.check(b.inputConfirm, Validator.confirmPassword(password, confirm));
        if (!valid) return;

        if (!b.checkTerms.isChecked()) {
            showMessage("Please accept the privacy policy to continue");
            return;
        }

        hideKeyboard();
        requestWithLoading(() -> repo.users.register(new Requests.Register(name, email, phone, password)), user -> {
            session.saveLogin(user);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
