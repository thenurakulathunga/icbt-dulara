package com.example.printexpress.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.printexpress.R;
import com.example.printexpress.data.model.OrderDetail;
import com.example.printexpress.data.model.OrderItem;
import com.example.printexpress.databinding.ActivityOrderDetailBinding;
import com.example.printexpress.databinding.ItemOrderLineBinding;
import com.example.printexpress.ui.base.BaseActivity;
import com.example.printexpress.ui.product.ProductDetailActivity;
import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.IconMapper;
import com.example.printexpress.util.NotificationSync;
import com.example.printexpress.util.StatusHelper;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/** Order tracking: status progress, items, price, and reschedule / cancel buttons. */
public class OrderDetailActivity extends BaseActivity {

    public static final String EXTRA_ORDER_ID = "orderId";

    private ActivityOrderDetailBinding b;
    private long orderId;
    private OrderDetail order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupToolbar(b.toolbar, getString(R.string.order_tracking));

        orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, 0);
        b.swipeRefresh.setColorSchemeResources(R.color.px_primary);
        b.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.px_card);
        b.swipeRefresh.setOnRefreshListener(this::loadOrder);
        b.buttonReschedule.setOnClickListener(v -> showReschedulePicker());
        b.buttonCancel.setOnClickListener(v -> confirm(getString(R.string.cancel_order),
                getString(R.string.cancel_order_confirm), getString(R.string.cancel_order), this::cancelOrder));

        b.stateView.showLoading();
        loadOrder();
    }

    private void loadOrder() {
        request(() -> repo.orders.getOrder(userId(), orderId), false, this::showOrder, message -> {
            b.swipeRefresh.setRefreshing(false);
            b.stateView.showError(message, this::loadOrder);
        });
    }

    private void showOrder(OrderDetail detail) {
        order = detail;
        b.swipeRefresh.setRefreshing(false);
        b.stateView.hide();

        b.textOrderNo.setText("Order #" + detail.getOrderId());
        b.textPlaced.setText("Placed on " + Formatter.dateTime(detail.getCreatedAt()));
        StatusHelper.applyPill(b.textStatus, detail.getStatus());

        boolean cancelled = "CANCELLED".equals(detail.getStatus());
        b.statusView.setVisibility(cancelled ? View.GONE : View.VISIBLE);
        b.textCancelled.setVisibility(cancelled ? View.VISIBLE : View.GONE);
        if (!cancelled) b.statusView.setStatus(detail.getStatus());

        // Items
        b.containerItems.removeAllViews();
        for (OrderItem item : detail.getItems()) {
            ItemOrderLineBinding line = ItemOrderLineBinding.inflate(LayoutInflater.from(this), b.containerItems, true);
            line.imageIcon.setImageResource(IconMapper.categoryIcon(item.getIconName()));
            line.textName.setText(item.getQuantity() + " x " + item.getProductName());
            String specs = item.getSizeName() + (item.getMaterialName() != null ? "  •  " + item.getMaterialName() : "");
            line.textSpecs.setText(specs);
            line.textArtwork.setText(item.getDesignTitle() != null
                    ? "Design: " + item.getDesignTitle()
                    : "Text: \"" + item.getCustomText() + "\"");
            line.textUnitPrice.setText(Formatter.money(item.getUnitPrice()) + " each");
        }

        // Pickup / delivery
        if (detail.isDelivery()) {
            b.imageFulfilment.setImageResource(R.drawable.ic_truck);
            b.textFulfilment.setText("Delivery on " + Formatter.date(detail.getScheduledDate()));
            b.textFulfilmentInfo.setText(detail.getDeliveryAddress() == null ? "Address removed" : detail.getDeliveryAddress());
        } else {
            b.imageFulfilment.setImageResource(R.drawable.ic_store);
            b.textFulfilment.setText("Pickup on " + Formatter.date(detail.getScheduledDate()));
            b.textFulfilmentInfo.setText("PrintXpress, 45 Union Place, Colombo 02");
        }
        b.textNotes.setVisibility(detail.getNotes() == null ? View.GONE : View.VISIBLE);
        b.textNotes.setText("Notes: " + detail.getNotes());

        // Price
        b.priceSummary.setUnitLine(detail.getPromoCode() == null ? null : "Promo code " + detail.getPromoCode());
        b.priceSummary.setPrices(detail.getSubtotal(), detail.getDiscountAmount(), detail.getDeliveryFee(),
                detail.getTotalAmount());

        // Actions only allowed before printing starts
        b.panelActions.setVisibility(detail.isCanChange() ? View.VISIBLE : View.GONE);
        b.textCannotChange.setVisibility(!detail.isCanChange() && !cancelled
                && !"COMPLETED".equals(detail.getStatus()) ? View.VISIBLE : View.GONE);
    }

    private void showReschedulePicker() {
        // OrderRepository checks the real earliest date for the product; here we just block past dates
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        long start = tomorrow.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long end = LocalDate.now().plusDays(ProductDetailActivity.MAX_DAYS_AHEAD)
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Choose a new date")
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setStart(start).setEnd(end)
                        .setValidator(DateValidatorPointForward.from(start)).build())
                .build();
        picker.addOnPositiveButtonClickListener(millis -> {
            String date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString();
            requestWithLoading(() -> repo.orders.reschedule(userId(), orderId, date), detail -> {
                showOrder(detail);
                showMessage("Order rescheduled to " + Formatter.date(detail.getScheduledDate()));
                NotificationSync.check(this);
            });
        });
        picker.show(getSupportFragmentManager(), "reschedule");
    }

    private void cancelOrder() {
        requestWithLoading(() -> repo.orders.cancel(userId(), orderId), detail -> {
            showOrder(detail);
            showMessage("Order cancelled");
            NotificationSync.check(this);
        });
    }
}
