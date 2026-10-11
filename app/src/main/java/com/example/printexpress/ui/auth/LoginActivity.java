package com.example.printexpress.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import com.example.printexpress.databinding.ActivityLoginBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.ui.common.Form;
import com.example.printexpress.ui.main.MainActivity;
import com.example.printexpress.util.Validator;

/** Log in with email OR phone number + password. */
public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Form.clearErrorOnType(b.inputIdentifier, b.inputPassword);
        b.buttonLogin.setOnClickListener(v -> login());
        b.textRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String identifier = Form.text(b.inputIdentifier);
        String password = Form.text(b.inputPassword);

        boolean valid = Form.check(b.inputIdentifier, Validator.emailOrPhone(identifier))
                & Form.check(b.inputPassword, Validator.required(password, "Password"));
        if (!valid) return;

        hideKeyboard();
        requestWithLoading(() -> repo.users.login(identifier, password), user -> {
            session.saveLogin(user);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
