package com.example.printexpress.ui.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printexpress.R;
import com.example.printexpress.databinding.ActivityListBinding;
import com.example.printexpress.ui.common.GenericAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Reusable "toolbar + pull-to-refresh list" screen.
 * Addresses, Designs, Notifications, Offers, Gallery and Guidelines/FAQs all extend this,
 * so each of them only writes: the title, the row layout, how to fill a row and how to load data.
 */
public abstract class BaseListActivity<T> extends BaseActivity {

    protected ActivityListBinding b;
    protected GenericAdapter<T> adapter;

    protected abstract String getScreenTitle();

    @LayoutRes
    protected abstract int getItemLayout();

    protected abstract void bindItem(View itemView, T item, int position);

    /** Call the API, then showItems(list) or showError(message). */
    protected abstract void loadItems();

    @DrawableRes
    protected abstract int getEmptyIcon();

    protected abstract String getEmptyTitle();

    protected String getEmptyMessage() {
        return null;
    }

    protected void onItemClick(T item, int position) {
    }

    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        setupToolbar(b.toolbar, getScreenTitle());

        adapter = new GenericAdapter<>(getItemLayout(), this::bindItem);
        adapter.setOnItemClick(this::onItemClick);
        b.recycler.setLayoutManager(createLayoutManager());
        b.recycler.setAdapter(adapter);

        b.swipeRefresh.setColorSchemeResources(R.color.px_primary);
        b.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.px_card);
        b.swipeRefresh.setOnRefreshListener(this::loadItems);
        b.swipeRefresh.setOnChildScrollUpCallback((parent, child) -> b.recycler.canScrollVertically(-1));

        b.stateView.showLoading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    protected void showItems(List<? extends T> items) {
        b.swipeRefresh.setRefreshing(false);
        adapter.setItems(new ArrayList<>(items));
        if (items.isEmpty()) {
            b.stateView.showEmpty(getEmptyIcon(), getEmptyTitle(), getEmptyMessage());
        } else {
            b.stateView.hide();
        }
    }

    protected void showError(String message) {
        b.swipeRefresh.setRefreshing(false);
        if (adapter.getItemCount() == 0) {
            b.stateView.showError(message, this::loadItems);
        } else {
            showMessage(message);
        }
    }

    /** Shows the floating "+" button (e.g. "Add address"). */
    protected void showFab(String text, View.OnClickListener onClick) {
        b.fab.setText(text);
        b.fab.setVisibility(View.VISIBLE);
        b.fab.setOnClickListener(onClick);
        b.recycler.setPadding(b.recycler.getPaddingLeft(), b.recycler.getPaddingTop(),
                b.recycler.getPaddingRight(), b.recycler.getPaddingBottom() + 200);
    }
}
