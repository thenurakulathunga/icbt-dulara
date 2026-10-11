package com.example.printexpress.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.printexpress.data.model.Address;
import com.example.printexpress.data.model.Requests;
import com.example.printexpress.databinding.ActivityAddressFormBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.ui.common.Form;
import com.example.printexpress.util.Validator;

/** Add a new address, or edit an existing one (same screen). */
public class AddressFormActivity extends BaseActivity {

    private static final String EXTRA_ID = "id";
    private static final String EXTRA_LABEL = "label";
    private static final String EXTRA_LINE = "line";
    private static final String EXTRA_CITY = "city";
    private static final String EXTRA_POSTAL = "postal";
    private static final String EXTRA_DEFAULT = "default";

    private ActivityAddressFormBinding b;
    private long addressId;   // 0 = new address

    public static void openNew(Context context) {
        context.startActivity(new Intent(context, AddressFormActivity.class));
    }

    public static void openEdit(Context context, Address a) {
        Intent intent = new Intent(context, AddressFormActivity.class);
        intent.putExtra(EXTRA_ID, a.getAddressId());
        intent.putExtra(EXTRA_LABEL, a.getLabel());
        intent.putExtra(EXTRA_LINE, a.getAddressLine());
        intent.putExtra(EXTRA_CITY, a.getCity());
        intent.putExtra(EXTRA_POSTAL, a.getPostalCode());
        intent.putExtra(EXTRA_DEFAULT, a.isDefault());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAddressFormBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        addressId = getIntent().getLongExtra(EXTRA_ID, 0);
        setupToolbar(b.toolbar, addressId == 0 ? "Add address" : "Edit address");
        if (addressId != 0) {
            Form.setText(b.inputLabel, getIntent().getStringExtra(EXTRA_LABEL));
            Form.setText(b.inputLine, getIntent().getStringExtra(EXTRA_LINE));
            Form.setText(b.inputCity, getIntent().getStringExtra(EXTRA_CITY));
            Form.setText(b.inputPostal, getIntent().getStringExtra(EXTRA_POSTAL));
            b.switchDefault.setChecked(getIntent().getBooleanExtra(EXTRA_DEFAULT, false));
        }

        Form.clearErrorOnType(b.inputLabel, b.inputLine, b.inputCity, b.inputPostal);
        b.buttonSave.setOnClickListener(v -> save());
    }

    private void save() {
        String label = Form.text(b.inputLabel);
        String line = Form.text(b.inputLine);
        String city = Form.text(b.inputCity);
        String postal = Form.text(b.inputPostal);

        boolean valid = Form.check(b.inputLabel, Validator.length(label, "Label", 2, 30))
                & Form.check(b.inputLine, Validator.length(line, "Address", 5, 200))
                & Form.check(b.inputCity, Validator.length(city, "City", 2, 60))
                & Form.check(b.inputPostal, Validator.postalCode(postal));
        if (!valid) return;

        hideKeyboard();
        Requests.AddressBody body = new Requests.AddressBody(label, line, city, postal, b.switchDefault.isChecked());
        requestWithLoading(() -> repo.addresses.save(userId(), addressId, body), list -> finish());
    }
}
