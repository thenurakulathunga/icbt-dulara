package com.example.printexpress.ui.product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.printexpress.R;
import com.example.printexpress.data.model.Address;
import com.example.printexpress.data.model.Product;
import com.example.printexpress.data.model.ProductDetail;
import com.example.printexpress.data.model.ProductOption;
import com.example.printexpress.data.model.Promotion;
import com.example.printexpress.data.model.Requests;
import com.example.printexpress.data.repository.OrderRepository;
import com.example.printexpress.databinding.ActivityProductDetailBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.ui.common.Form;
import com.example.printexpress.ui.order.OrderViews;
import com.example.printexpress.ui.profile.AddressesActivity;
import com.example.printexpress.ui.profile.DesignsActivity;
import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.IconMapper;
import com.example.printexpress.util.NotificationSync;
import com.example.printexpress.util.PriceCalculator;
import com.example.printexpress.util.Validator;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Product details + order form.
 * The customer picks size, material, quantity, artwork, pickup/delivery, date and promo code.
 * The price summary updates live as they change anything.
 */
public class ProductDetailActivity extends BaseActivity {

    public static final String EXTRA_PRODUCT_ID = "productId";
    public static final int MAX_DAYS_AHEAD = OrderRepository.MAX_DAYS_AHEAD;

    private ActivityProductDetailBinding b;
    private Product product;
    private ProductOption selectedSize;
    private ProductOption selectedMaterial;
    private boolean hasMaterials;
    private Long selectedDesignId;
    private List<Address> addresses = new ArrayList<>();
    private Address selectedAddress;
    private LocalDate scheduledDate;
    private Promotion appliedPromo;

    /** Opens "My designs" in pick mode and receives the chosen design. */
    private final ActivityResultLauncher<Intent> pickDesign = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedDesignId = result.getData().getLongExtra(DesignsActivity.RESULT_DESIGN_ID, 0);
                    String title = result.getData().getStringExtra(DesignsActivity.RESULT_DESIGN_TITLE);
                    b.textDesign.setText(title);
                    b.textDesign.setTextColor(getColor(R.color.px_text_primary));
                    b.buttonChooseDesign.setText("Change design");
                }
            });

    public static void open(Context context, long productId) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_PRODUCT_ID, productId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupToolbar(b.toolbar, "Product details");

        Form.clearErrorOnType(b.inputCustomText, b.inputNotes, b.inputPromo);
        setupArtworkToggle();
        setupFulfilmentToggle();
        b.quantityView.setOnQuantityChanged(q -> updatePrice());
        b.buttonChooseDesign.setOnClickListener(v -> pickDesign.launch(DesignsActivity.pickIntent(this)));
        b.buttonAddAddress.setOnClickListener(v -> startActivity(new Intent(this, AddressesActivity.class)));
        b.editDate.setOnClickListener(v -> showDatePicker());
        b.buttonApplyPromo.setOnClickListener(v -> applyPromo());
        b.buttonPlaceOrder.setOnClickListener(v -> placeOrder());

        loadProduct();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();   // refresh after adding an address
    }

    // ---------- Loading ----------

    private void loadProduct() {
        b.stateView.showLoading();
        long id = getIntent().getLongExtra(EXTRA_PRODUCT_ID, 0);
        request(() -> repo.catalog.getProductDetail(id), false, this::showProduct,
                message -> b.stateView.showError(message, this::loadProduct));
    }

    private void showProduct(ProductDetail detail) {
        b.stateView.hide();
        product = detail.getProduct();

        b.imageIcon.setImageResource(IconMapper.categoryIcon(product.getIconName()));
        b.textCategory.setText(product.getCategoryName());
        b.textName.setText(product.getName());
        b.textDescription.setText(product.getDescription());
        b.textFromPrice.setText("From " + Formatter.money(product.getBasePrice()) + " / unit");
        b.textTurnaround.setText("Ready in " + product.getTurnaroundDays() + " days");
        b.textQuantityRange.setText("Min " + product.getMinQuantity() + "  •  Max " + product.getMaxQuantity());

        fillOptionChips(b.chipGroupSize, detail.getSizes(), true);
        hasMaterials = !detail.getMaterials().isEmpty();
        b.sectionMaterial.setVisibility(hasMaterials ? View.VISIBLE : View.GONE);
        if (hasMaterials) fillOptionChips(b.chipGroupMaterial, detail.getMaterials(), false);

        b.quantityView.setRange(product.getMinQuantity(), product.getMaxQuantity());
        updatePrice();
    }

    /** One chip per option, e.g. "A4 (210 x 297 mm)  +LKR 12.00". The first one is selected. */
    private void fillOptionChips(ChipGroup group, List<ProductOption> options, boolean isSize) {
        group.removeAllViews();
        for (int i = 0; i < options.size(); i++) {
            ProductOption option = options.get(i);
            Chip chip = new Chip(this);
            String extra = option.getExtraPrice() > 0 ? "  +" + Formatter.money(option.getExtraPrice()) : "";
            chip.setText(option.getOptionName() + extra);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                chip.setChecked(true);
                if (isSize) selectedSize = option;
                else selectedMaterial = option;
                updatePrice();
            });
            group.addView(chip);
            if (i == 0) {
                chip.setChecked(true);
                if (isSize) selectedSize = option;
                else selectedMaterial = option;
            }
        }
    }

    private void loadAddresses() {
        request(() -> repo.addresses.getAddresses(userId()), false, list -> {
            addresses = list;
            ArrayAdapter<Address> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
            b.dropdownAddress.setAdapter(adapter);
            b.dropdownAddress.setOnItemClickListener((parent, view, position, id) -> {
                selectedAddress = addresses.get(position);
                Form.check(b.inputAddress, null);
            });
            // Pre-select the default address
            if (selectedAddress == null) {
                for (Address a : list) {
                    if (a.isDefault()) {
                        selectedAddress = a;
                        b.dropdownAddress.setText(a.toString(), false);
                    }
                }
            }
        }, message -> { });
    }

    // ---------- Toggles ----------

    private void setupArtworkToggle() {
        b.toggleArtwork.check(R.id.buttonModeUpload);
        b.toggleArtwork.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            boolean upload = checkedId == R.id.buttonModeUpload;
            b.panelUpload.setVisibility(upload ? View.VISIBLE : View.GONE);
            b.inputCustomText.setVisibility(upload ? View.GONE : View.VISIBLE);
        });
    }

    private void setupFulfilmentToggle() {
        b.toggleFulfilment.check(R.id.buttonPickup);
        b.toggleFulfilment.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            boolean delivery = checkedId == R.id.buttonDelivery;
            b.panelDelivery.setVisibility(delivery ? View.VISIBLE : View.GONE);
            b.textPickupInfo.setVisibility(delivery ? View.GONE : View.VISIBLE);
            b.inputDate.setHint(delivery ? R.string.date_delivery : R.string.date_pickup);
            updatePrice();
        });
    }

    private boolean isDelivery() {
        return b.toggleFulfilment.getCheckedButtonId() == R.id.buttonDelivery;
    }

    private boolean isUploadMode() {
        return b.toggleArtwork.getCheckedButtonId() == R.id.buttonModeUpload;
    }

    // ---------- Date ----------

    private void showDatePicker() {
        if (product == null) return;
        LocalDate earliest = LocalDate.now().plusDays(product.getTurnaroundDays());
        LocalDate latest = LocalDate.now().plusDays(MAX_DAYS_AHEAD);

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setStart(toMillis(earliest))
                .setEnd(toMillis(latest))
                .setValidator(CompositeDateValidator.allOf(Arrays.asList(
                        DateValidatorPointForward.from(toMillis(earliest)),
                        DateValidatorPointBackward.before(toMillis(latest)))))
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isDelivery() ? "Choose delivery date" : "Choose pickup date")
                .setSelection(toMillis(scheduledDate != null ? scheduledDate : earliest))
                .setCalendarConstraints(constraints)
                .build();
        picker.addOnPositiveButtonClickListener(millis -> {
            scheduledDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate();
            b.editDate.setText(Formatter.date(scheduledDate));
            Form.check(b.inputDate, null);
        });
        picker.show(getSupportFragmentManager(), "date");
    }

    private static long toMillis(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    // ---------- Promo code ----------

    private void applyPromo() {
        String code = Form.text(b.inputPromo);
        if (!Form.check(b.inputPromo, Validator.required(code, "Promo code"))) return;
        hideKeyboard();
        int quantity = b.quantityView.getQuantity();
        request(() -> repo.content.validatePromoCode(code, quantity), true, promo -> {
            appliedPromo = promo;
            b.textPromoStatus.setVisibility(View.VISIBLE);
            b.textPromoStatus.setText(promo.getPromoCode() + " applied - " + promo.getDiscountPercent() + "% off");
            updatePrice();
        }, message -> {
            appliedPromo = null;
            b.textPromoStatus.setVisibility(View.GONE);
            Form.check(b.inputPromo, message);
            updatePrice();
        });
    }

    // ---------- Price ----------

    private void updatePrice() {
        if (product == null || selectedSize == null) return;
        int quantity = b.quantityView.getQuantity();
        double unit = PriceCalculator.unitPrice(product.getBasePrice(), selectedSize.getExtraPrice(),
                selectedMaterial == null ? 0 : selectedMaterial.getExtraPrice());
        double subtotal = PriceCalculator.subtotal(unit, quantity);

        // A promo stops counting if the quantity drops below its minimum
        boolean promoValid = appliedPromo != null && quantity >= appliedPromo.getMinQuantity();
        double discount = promoValid ? PriceCalculator.discount(subtotal, appliedPromo.getDiscountPercent()) : 0;
        double delivery = PriceCalculator.deliveryFee(isDelivery());
        double total = PriceCalculator.total(subtotal, discount, delivery);

        b.priceSummary.setUnitLine(quantity + " x " + Formatter.money(unit));
        b.priceSummary.setPrices(subtotal, discount, delivery, total);
        b.textBottomTotal.setText(Formatter.money(total));
    }

    // ---------- Place order ----------

    private void placeOrder() {
        if (product == null) return;
        int quantity = b.quantityView.getQuantity();
        String customText = Form.text(b.inputCustomText);
        String notes = Form.text(b.inputNotes);
        List<String> errors = new ArrayList<>();

        String quantityError = Validator.quantity(quantity, product.getMinQuantity(), product.getMaxQuantity());
        b.textQuantityError.setVisibility(quantityError == null ? View.GONE : View.VISIBLE);
        b.textQuantityError.setText(quantityError);
        if (quantityError != null) errors.add(quantityError);

        if (selectedSize == null) errors.add("Please choose a size");
        if (hasMaterials && selectedMaterial == null) errors.add("Please choose a material");

        if (isUploadMode()) {
            if (selectedDesignId == null) errors.add("Please choose or upload your design");
        } else if (!Form.check(b.inputCustomText, Validator.length(customText, "Text", 2, 200))) {
            errors.add("Enter the text to print");
        }

        if (isDelivery() && selectedAddress == null) {
            Form.check(b.inputAddress, "Choose a delivery address");
            errors.add("Choose a delivery address");
        }
        if (scheduledDate == null) {
            Form.check(b.inputDate, "Choose a date");
            errors.add("Choose a date");
        }
        if (!Form.check(b.inputNotes, notes.length() > 300 ? "Notes can be up to 300 characters" : null)) {
            errors.add("Notes are too long");
        }
        if (appliedPromo != null && quantity < appliedPromo.getMinQuantity()) {
            errors.add("Code " + appliedPromo.getPromoCode() + " needs at least " + appliedPromo.getMinQuantity() + " items");
        }

        if (!errors.isEmpty()) {
            showMessage(errors.get(0));
            return;
        }

        Requests.Order order = new Requests.Order();
        order.productId = product.getProductId();
        order.sizeOptionId = selectedSize.getOptionId();
        order.materialOptionId = selectedMaterial == null ? null : selectedMaterial.getOptionId();
        order.quantity = quantity;
        order.designId = isUploadMode() ? selectedDesignId : null;
        order.customText = isUploadMode() ? null : customText;
        order.fulfilmentType = isDelivery() ? "DELIVERY" : "PICKUP";
        order.addressId = isDelivery() ? selectedAddress.getAddressId() : null;
        order.scheduledDate = scheduledDate.toString();
        order.promoCode = appliedPromo == null ? null : appliedPromo.getPromoCode();
        order.notes = notes.isEmpty() ? null : notes;

        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirm order")
                .setMessage(quantity + " x " + product.getName() + "\n"
                        + (isDelivery() ? "Delivery" : "Pickup") + " on " + Formatter.date(scheduledDate) + "\n\n"
                        + "Total: " + b.textBottomTotal.getText())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.place_order, (d, w) -> sendOrder(order))
                .show();
    }

    private void sendOrder(Requests.Order order) {
        requestWithLoading(() -> repo.orders.placeOrder(userId(), order), detail -> {
            NotificationSync.check(this);   // shows "Order #x confirmed" alert
            OrderViews.open(this, detail.getOrderId());
            finish();
        });
    }
}
