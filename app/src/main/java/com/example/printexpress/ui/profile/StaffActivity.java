package com.example.printexpress.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.printexpress.R;
import com.example.printexpress.data.model.OrderSummary;
import com.example.printexpress.data.model.Promotion;
import com.example.printexpress.data.model.SupportRequest;
import com.example.printexpress.databinding.DialogInputBinding;
import com.example.printexpress.databinding.ItemOrderBinding;
import com.example.printexpress.ui.base.BaseListActivity;
import com.example.printexpress.ui.common.Form;
import com.example.printexpress.ui.order.OrderViews;
import com.example.printexpress.util.NotificationSync;
import com.example.printexpress.util.StatusHelper;
import com.example.printexpress.util.Validator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * DEMO ONLY - plays the role of a PrintXpress staff member, because the database now
 * lives on the phone and there is no separate staff system. Use it to show the examiner:
 *  - order tracking (tap an order and move it to Processing / Printing / Ready ...)
 *  - promotional notifications (toolbar: send offer)
 *  - support replies (toolbar: reply)
 */
public class StaffActivity extends BaseListActivity<OrderSummary> {

    private static final String[] STATUSES = {"PENDING", "PROCESSING", "PRINTING", "READY", "COMPLETED", "CANCELLED"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b.toolbar.inflateMenu(R.menu.menu_staff);
        b.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_send_offer) {
                chooseOfferToSend();
                return true;
            }
            if (item.getItemId() == R.id.action_reply_support) {
                chooseSupportRequest();
                return true;
            }
            return false;
        });
    }

    @Override
    protected String getScreenTitle() {
        return "Staff demo panel";
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_order;
    }

    @Override
    protected void bindItem(View itemView, OrderSummary order, int position) {
        OrderViews.bind(itemView, order);
        ItemOrderBinding.bind(itemView).textOrderNo.setText("Order #" + order.getOrderId() + "  •  " + order.getCustomerName());
    }

    /** Tap an order to move it to another status. */
    @Override
    protected void onItemClick(OrderSummary order, int position) {
        String[] labels = new String[STATUSES.length];
        int checked = 0;
        for (int i = 0; i < STATUSES.length; i++) {
            labels[i] = StatusHelper.label(STATUSES[i]);
            if (STATUSES[i].equals(order.getStatus())) checked = i;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Order #" + order.getOrderId() + " status")
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    dialog.dismiss();
                    String status = STATUSES[which];
                    requestWithLoading(() -> {
                        repo.orders.updateStatus(order.getOrderId(), status);
                        return repo.orders.getAllOrders();
                    }, orders -> {
                        showItems(orders);
                        showMessage("Order #" + order.getOrderId() + " is now " + StatusHelper.label(status));
                        NotificationSync.check(this);
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    protected void loadItems() {
        request(() -> repo.orders.getAllOrders(), false, this::showItems, this::showError);
    }

    @Override
    protected int getEmptyIcon() {
        return R.drawable.ic_orders;
    }

    @Override
    protected String getEmptyTitle() {
        return "No orders yet";
    }

    // ---------- Send a promotion to every customer ----------

    private void chooseOfferToSend() {
        request(() -> repo.content.getAllPromotions(), promotions -> {
            ArrayAdapter<Promotion> list = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, promotions);
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Send offer to all customers")
                    .setAdapter(list, (dialog, which) -> sendOffer(promotions.get(which)))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });
    }

    private void sendOffer(Promotion promo) {
        requestWithLoading(() -> repo.notifications.addForAllUsers(promo.getTitle(),
                promo.getDescription() + " Use code " + promo.getPromoCode() + ".", "PROMOTION"), count -> {
            showMessage("Offer sent to " + count + " customers");
            NotificationSync.check(this);
        });
    }

    // ---------- Reply to a support question ----------

    private void chooseSupportRequest() {
        request(() -> repo.support.getOpenRequests(), requests -> {
            if (requests.isEmpty()) {
                showMessage("No open support questions");
                return;
            }
            ArrayAdapter<SupportRequest> list = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requests);
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Open questions")
                    .setAdapter(list, (dialog, which) -> askReply(requests.get(which)))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });
    }

    private void askReply(SupportRequest request) {
        DialogInputBinding input = DialogInputBinding.inflate(LayoutInflater.from(this));
        input.input.setHint("Reply");
        input.textHelper.setText(request.getMessage());

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(request.getSubject())
                .setView(input.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.send, null)
                .show();
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String reply = Form.text(input.input);
            if (!Form.check(input.input, Validator.length(reply, "Reply", 2, 1000))) return;
            dialog.dismiss();
            requestWithLoading(() -> {
                repo.support.reply(request.getRequestId(), reply);
                return true;
            }, r -> {
                showMessage("Reply sent");
                NotificationSync.check(this);
            });
        });
    }
}
