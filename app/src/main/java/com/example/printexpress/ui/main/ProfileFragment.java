package com.example.printexpress.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.printexpress.R;
import com.example.printexpress.data.SessionManager;
import com.example.printexpress.databinding.FragmentProfileBinding;
import com.example.printexpress.ui.auth.LoginActivity;
import com.example.printexpress.ui.base.BaseFragment;
import com.example.printexpress.ui.profile.AddressesActivity;
import com.example.printexpress.ui.profile.DesignsActivity;
import com.example.printexpress.ui.profile.EditProfileActivity;
import com.example.printexpress.ui.profile.NotificationsActivity;
import com.example.printexpress.ui.profile.StaffActivity;
import com.example.printexpress.ui.support.GalleryActivity;
import com.example.printexpress.ui.support.InfoListActivity;
import com.example.printexpress.ui.support.PromotionsActivity;
import com.example.printexpress.ui.support.SupportActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/** Profile tab: user details + menu to every account and help screen. */
public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding b;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        b.rowEditProfile.setOnClickListener(v -> open(EditProfileActivity.class));
        b.rowAddresses.setOnClickListener(v -> open(AddressesActivity.class));
        b.rowDesigns.setOnClickListener(v -> open(DesignsActivity.class));
        b.rowNotifications.setOnClickListener(v -> open(NotificationsActivity.class));
        b.rowOffers.setOnClickListener(v -> open(PromotionsActivity.class));
        b.rowGallery.setOnClickListener(v -> open(GalleryActivity.class));
        b.rowGuidelines.setOnClickListener(v -> InfoListActivity.open(requireContext(), InfoListActivity.TYPE_GUIDELINES));
        b.rowFaqs.setOnClickListener(v -> InfoListActivity.open(requireContext(), InfoListActivity.TYPE_FAQS));
        b.rowSupport.setOnClickListener(v -> open(SupportActivity.class));
        b.rowStaff.setOnClickListener(v -> open(StaffActivity.class));
        b.rowLogout.setOnClickListener(v -> confirmLogout());
    }

    @Override
    public void onResume() {
        super.onResume();
        showUser();   // refresh after editing the profile
    }

    private void showUser() {
        SessionManager session = session();
        String name = session.getName();
        b.textName.setText(name);
        b.textEmail.setText(session.getEmail());
        b.textPhone.setText(session.getPhone());
        String[] parts = name.trim().split("\\s+");
        String initials = parts[0].isEmpty() ? "?" : parts[0].substring(0, 1)
                + (parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "");
        b.textAvatar.setText(initials.toUpperCase());
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirm)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.logout, (d, w) -> logout())
                .show();
    }

    private void logout() {
        session().logout();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void open(Class<?> screen) {
        startActivity(new Intent(requireContext(), screen));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
