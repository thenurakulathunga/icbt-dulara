package com.example.printexpress.ui.support;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.printexpress.data.model.SupportRequest;
import com.example.printexpress.databinding.ActivitySupportBinding;
import com.example.printexpress.databinding.ItemSupportBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.ui.common.Form;
import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.StatusHelper;
import com.example.printexpress.util.Validator;

import java.util.List;

/** Customer support: call / email / WhatsApp, ask the design team, and see earlier replies. */
public class SupportActivity extends BaseActivity {

    private static final String PHONE = "+94112345678";
    private static final String EMAIL = "support@printxpress.lk";
    private static final String WHATSAPP = "94771234567";

    private ActivitySupportBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySupportBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupToolbar(b.toolbar, "Help & support");

        b.buttonCall.setOnClickListener(v -> openLink(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE))));
        b.buttonEmail.setOnClickListener(v -> openLink(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + EMAIL))));
        b.buttonWhatsapp.setOnClickListener(v -> openLink(new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + WHATSAPP))));
        b.rowFaqs.setOnClickListener(v -> InfoListActivity.open(this, InfoListActivity.TYPE_FAQS));
        b.rowGuidelines.setOnClickListener(v -> InfoListActivity.open(this, InfoListActivity.TYPE_GUIDELINES));

        Form.clearErrorOnType(b.inputSubject, b.inputMessage);
        b.buttonSend.setOnClickListener(v -> send());
        loadRequests();
    }

    private void openLink(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showMessage("No app found to open this");
        }
    }

    private void send() {
        String subject = Form.text(b.inputSubject);
        String message = Form.text(b.inputMessage);
        boolean valid = Form.check(b.inputSubject, Validator.length(subject, "Subject", 3, 100))
                & Form.check(b.inputMessage, Validator.length(message, "Message", 10, 1000));
        if (!valid) return;

        hideKeyboard();
        requestWithLoading(() -> repo.support.send(userId(), subject, message), list -> {
            Form.setText(b.inputSubject, "");
            Form.setText(b.inputMessage, "");
            showRequests(list);
            showMessage("Sent! Our design team will reply soon.");
        });
    }

    private void loadRequests() {
        request(() -> repo.support.getRequests(userId()), false, this::showRequests, message -> { });
    }

    private void showRequests(List<SupportRequest> list) {
        b.sectionRequests.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
        b.containerRequests.removeAllViews();
        for (SupportRequest r : list) {
            ItemSupportBinding ib = ItemSupportBinding.inflate(LayoutInflater.from(this), b.containerRequests, true);
            ib.textSubject.setText(r.getSubject());
            ib.textMessage.setText(r.getMessage());
            ib.textDate.setText(Formatter.dateTime(r.getCreatedAt()));
            StatusHelper.applyPill(ib.textStatus, r.getStatus());
            ib.textReply.setVisibility(r.getReply() == null ? View.GONE : View.VISIBLE);
            ib.textReply.setText("PrintXpress: " + r.getReply());
        }
    }
}
