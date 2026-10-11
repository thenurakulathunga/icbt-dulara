package com.example.printexpress.ui.order;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.printexpress.data.model.OrderSummary;
import com.example.printexpress.databinding.ItemOrderBinding;
import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.IconMapper;
import com.example.printexpress.util.StatusHelper;

/** Fills one item_order row. Shared by the Orders tab and the "latest order" card on Home. */
public final class OrderViews {

    private OrderViews() {
    }

    public static void bind(View itemView, OrderSummary order) {
        ItemOrderBinding b = ItemOrderBinding.bind(itemView);
        b.imageIcon.setImageResource(IconMapper.categoryIcon(order.getIconName()));
        b.textOrderNo.setText("Order #" + order.getOrderId());
        b.textProduct.setText(order.getQuantity() + " x " + order.getProductName());
        String how = "DELIVERY".equals(order.getFulfilmentType()) ? "Delivery" : "Pickup";
        b.textDate.setText(how + " on " + Formatter.date(order.getScheduledDate()));
        b.textTotal.setText(Formatter.money(order.getTotalAmount()));
        StatusHelper.applyPill(b.textStatus, order.getStatus());
    }

    public static void open(Context context, long orderId) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, orderId);
        context.startActivity(intent);
    }
}
