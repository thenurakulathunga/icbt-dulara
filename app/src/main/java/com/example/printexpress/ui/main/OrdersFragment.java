package com.example.printexpress.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.printexpress.R;
import com.example.printexpress.data.model.OrderSummary;
import com.example.printexpress.databinding.FragmentOrdersBinding;
import com.example.printexpress.ui.base.BaseFragment;
import com.example.printexpress.ui.common.GenericAdapter;
import com.example.printexpress.ui.order.OrderViews;
import com.example.printexpress.util.StatusHelper;

import java.util.ArrayList;
import java.util.List;

/** Orders tab: all orders with a filter (All / Active / Completed / Cancelled). */
public class OrdersFragment extends BaseFragment {

    private FragmentOrdersBinding b;
    private GenericAdapter<OrderSummary> adapter;
    private List<OrderSummary> allOrders = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentOrdersBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new GenericAdapter<>(R.layout.item_order, (itemView, order, position) -> OrderViews.bind(itemView, order));
        adapter.setOnItemClick((order, position) -> OrderViews.open(requireContext(), order.getOrderId()));
        b.recyclerOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.recyclerOrders.setAdapter(adapter);

        b.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> showFiltered());
        b.swipeRefresh.setColorSchemeResources(R.color.px_primary);
        b.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.px_card);
        b.swipeRefresh.setOnRefreshListener(this::loadOrders);
        b.swipeRefresh.setOnChildScrollUpCallback((parent, child) -> b.recyclerOrders.canScrollVertically(-1));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();   // refresh after placing / cancelling an order
    }

    private void loadOrders() {
        if (allOrders.isEmpty()) b.stateView.showLoading();
        long userId = userId();
        request(() -> repo().orders.getOrders(userId), orders -> {
            b.swipeRefresh.setRefreshing(false);
            allOrders = orders;
            showFiltered();
        }, message -> {
            b.swipeRefresh.setRefreshing(false);
            b.stateView.showError(message, this::loadOrders);
        });
    }

    private void showFiltered() {
        int checked = b.chipGroup.getCheckedChipId();
        List<OrderSummary> filtered = new ArrayList<>();
        for (OrderSummary o : allOrders) {
            boolean keep;
            if (checked == R.id.chipActive) keep = StatusHelper.isActive(o.getStatus());
            else if (checked == R.id.chipCompleted) keep = "COMPLETED".equals(o.getStatus());
            else if (checked == R.id.chipCancelled) keep = "CANCELLED".equals(o.getStatus());
            else keep = true;
            if (keep) filtered.add(o);
        }
        adapter.setItems(filtered);

        if (!filtered.isEmpty()) {
            b.stateView.hide();
        } else if (allOrders.isEmpty()) {
            b.stateView.showEmpty(R.drawable.ic_orders, "No orders yet", "Browse products and place your first order");
        } else {
            b.stateView.showEmpty(R.drawable.ic_orders, "Nothing here", "No orders match this filter");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
