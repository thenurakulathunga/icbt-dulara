package com.example.printexpress.ui.profile;

import android.view.View;

import com.example.printexpress.R;
import com.example.printexpress.data.model.Address;
import com.example.printexpress.databinding.ItemAddressBinding;
import com.example.printexpress.ui.base.BaseListActivity;

/** "My addresses": list, add, edit and delete delivery addresses. */
public class AddressesActivity extends BaseListActivity<Address> {

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showFab(getString(R.string.add_address), v -> AddressFormActivity.openNew(this));
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.my_addresses);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_address;
    }

    @Override
    protected void bindItem(View itemView, Address address, int position) {
        ItemAddressBinding ib = ItemAddressBinding.bind(itemView);
        ib.textLabel.setText(address.getLabel());
        ib.textDefault.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);
        ib.textAddress.setText(address.getFullAddress());
        ib.buttonDelete.setOnClickListener(v -> confirm("Delete address",
                "Delete \"" + address.getLabel() + "\"?", getString(R.string.delete),
                () -> requestWithLoading(() -> repo.addresses.delete(userId(), address.getAddressId()), this::showItems)));
    }

    @Override
    protected void onItemClick(Address address, int position) {
        AddressFormActivity.openEdit(this, address);
    }

    @Override
    protected void loadItems() {
        request(() -> repo.addresses.getAddresses(userId()), false, this::showItems, this::showError);
    }

    @Override
    protected int getEmptyIcon() {
        return R.drawable.ic_location;
    }

    @Override
    protected String getEmptyTitle() {
        return "No addresses yet";
    }

    @Override
    protected String getEmptyMessage() {
        return "Add an address to get your prints delivered";
    }
}
