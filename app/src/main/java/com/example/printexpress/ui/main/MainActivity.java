package com.example.printexpress.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.printexpress.R;
import com.example.printexpress.databinding.ActivityMainBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.util.NotificationSync;

/**
 * Home screen with the bottom navigation bar.
 * Each tab is a Fragment: Home, Browse, Orders, Profile.
 */
public class MainActivity extends BaseActivity {

    public static final String EXTRA_OPEN_CATEGORY = "openCategory";

    private ActivityMainBinding b;
    private Long pendingCategoryId;     // set when Home asks Browse to open a category
    private boolean pendingSearchFocus;

    private final ActivityResultLauncher<String> notificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> { });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.bottomNav.setOnItemSelectedListener(item -> {
            showTab(item.getItemId());
            return true;
        });
        if (savedInstanceState == null) {
            b.bottomNav.setSelectedItemId(R.id.nav_home);
        }
        askNotificationPermission();
    }

    /** Another screen (e.g. Gallery) asked us to show the Browse tab for one category. */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        long categoryId = intent.getLongExtra(EXTRA_OPEN_CATEGORY, 0);
        if (categoryId > 0) openBrowse(categoryId, false);
    }

    @Override
    protected boolean resizeForKeyboard() {
        return false;   // keep the bottom bar at the bottom when the keyboard opens
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationSync.check(this);
    }

    private void showTab(int itemId) {
        Fragment fragment;
        if (itemId == R.id.nav_browse) {
            fragment = BrowseFragment.newInstance(pendingCategoryId, pendingSearchFocus);
            pendingCategoryId = null;
            pendingSearchFocus = false;
        } else if (itemId == R.id.nav_orders) {
            fragment = new OrdersFragment();
        } else if (itemId == R.id.nav_profile) {
            fragment = new ProfileFragment();
        } else {
            fragment = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    /** Called from Home: jump to the Browse tab (optionally filtered by a category). */
    public void openBrowse(Long categoryId, boolean focusSearch) {
        pendingCategoryId = categoryId;
        pendingSearchFocus = focusSearch;
        b.bottomNav.setSelectedItemId(R.id.nav_browse);
    }

    public void openOrders() {
        b.bottomNav.setSelectedItemId(R.id.nav_orders);
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
