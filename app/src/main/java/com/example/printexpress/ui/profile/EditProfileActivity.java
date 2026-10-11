package com.example.printexpress.ui.profile;

import android.os.Bundle;

import com.example.printexpress.data.model.Requests;
import com.example.printexpress.databinding.ActivityEditProfileBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.ui.common.Form;
import com.example.printexpress.util.Validator;

/** Update name / email / phone, and change the password. */
public class EditProfileActivity extends BaseActivity {

    private ActivityEditProfileBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupToolbar(b.toolbar, "Edit profile");

        Form.setText(b.inputName, session.getName());
        Form.setText(b.inputEmail, session.getEmail());
        Form.setText(b.inputPhone, session.getPhone());

        Form.clearErrorOnType(b.inputName, b.inputEmail, b.inputPhone,
                b.inputCurrentPassword, b.inputNewPassword, b.inputConfirmPassword);
        b.buttonSaveProfile.setOnClickListener(v -> saveProfile());
        b.buttonChangePassword.setOnClickListener(v -> changePassword());
    }

    private void saveProfile() {
        String name = Form.text(b.inputName);
        String email = Form.text(b.inputEmail);
        String phone = Form.text(b.inputPhone);

        boolean valid = Form.check(b.inputName, Validator.name(name))
                & Form.check(b.inputEmail, Validator.email(email))
                & Form.check(b.inputPhone, Validator.phone(phone));
        if (!valid) return;

        hideKeyboard();
        requestWithLoading(() -> repo.users.updateProfile(userId(), new Requests.Profile(name, email, phone)), user -> {
            session.saveUser(user);
            showMessage("Profile updated");
        });
    }

    private void changePassword() {
        String current = Form.text(b.inputCurrentPassword);
        String newPassword = Form.text(b.inputNewPassword);
        String confirm = Form.text(b.inputConfirmPassword);

        boolean valid = Form.check(b.inputCurrentPassword, Validator.required(current, "Current password"))
                & Form.check(b.inputNewPassword, Validator.password(newPassword))
                & Form.check(b.inputConfirmPassword, Validator.confirmPassword(newPassword, confirm));
        if (!valid) return;

        hideKeyboard();
        requestWithLoading(() -> {
            repo.users.changePassword(userId(), current, newPassword);
            return true;
        }, r -> {
            Form.setText(b.inputCurrentPassword, "");
            Form.setText(b.inputNewPassword, "");
            Form.setText(b.inputConfirmPassword, "");
            showMessage("Password changed");
        });
    }
}
