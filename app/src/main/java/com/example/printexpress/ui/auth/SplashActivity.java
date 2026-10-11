package com.example.printexpress.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.printexpress.databinding.ActivitySplashBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.ui.main.MainActivity;

/** First screen: shows the logo and decides where to go (Login or Home). */
public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.buttonRetry.setOnClickListener(v -> checkSession());
        checkSession();
    }

    private void checkSession() {
        if (!session.isLoggedIn()) {
            b.getRoot().postDelayed(() -> open(LoginActivity.class), 900);
            return;
        }
        b.progress.setVisibility(View.VISIBLE);
        b.buttonRetry.setVisibility(View.GONE);

        // Make sure the saved account still exists (and refresh the profile)
        request(() -> repo.users.getUser(userId()), false,
                user -> {
                    if (user == null) {
                        session.logout();
                        open(LoginActivity.class);
                        return;
                    }
                    session.saveUser(user);
                    open(MainActivity.class);
                },
                message -> {
                    b.progress.setVisibility(View.GONE);
                    b.buttonRetry.setVisibility(View.VISIBLE);
                    showMessage(message);
                });
    }

    private void open(Class<?> screen) {
        startActivity(new Intent(this, screen));
        finish();
    }
}
