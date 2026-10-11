package com.example.printexpress.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.printexpress.R;
import com.example.printexpress.data.model.Category;
import com.example.printexpress.data.model.Product;
import com.example.printexpress.databinding.FragmentBrowseBinding;
import com.example.printexpress.databinding.ItemProductBinding;
import com.example.printexpress.ui.base.BaseFragment;
import com.example.printexpress.ui.common.GenericAdapter;
import com.example.printexpress.ui.product.ProductDetailActivity;
import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.IconMapper;
import com.google.android.material.chip.Chip;

import java.util.List;

/** Browse tab: search box + category chips + product list. */
public class BrowseFragment extends BaseFragment {

    private static final String ARG_CATEGORY = "categoryId";
    private static final String ARG_FOCUS_SEARCH = "focusSearch";
    private static final long ALL = -1;

    private FragmentBrowseBinding b;
    private GenericAdapter<Product> adapter;
    private long selectedCategory = ALL;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());

    public static BrowseFragment newInstance(Long categoryId, boolean focusSearch) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY, categoryId == null ? ALL : categoryId);
        args.putBoolean(ARG_FOCUS_SEARCH, focusSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentBrowseBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) selectedCategory = getArguments().getLong(ARG_CATEGORY, ALL);

        adapter = new GenericAdapter<>(R.layout.item_product, (itemView, product, position) -> {
            ItemProductBinding pb = ItemProductBinding.bind(itemView);
            pb.imageIcon.setImageResource(IconMapper.categoryIcon(product.getIconName()));
            pb.textName.setText(product.getName());
            pb.textCategory.setText(product.getCategoryName());
            pb.textPrice.setText("From " + Formatter.money(product.getBasePrice()));
            pb.textInfo.setText("Min " + product.getMinQuantity() + "  •  Ready in " + product.getTurnaroundDays() + " days");
        });
        adapter.setOnItemClick((product, position) -> ProductDetailActivity.open(requireContext(), product.getProductId()));
        b.recyclerProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        b.recyclerProducts.setAdapter(adapter);

        b.swipeRefresh.setColorSchemeResources(R.color.px_primary);
        b.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.px_card);
        b.swipeRefresh.setOnRefreshListener(this::loadProducts);
        b.swipeRefresh.setOnChildScrollUpCallback((parent, child) -> b.recyclerProducts.canScrollVertically(-1));

        // Search while typing (waits 400 ms after the last key so we don't call the API on every letter)
        b.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchHandler.removeCallbacksAndMessages(null);
                searchHandler.postDelayed(BrowseFragment.this::loadProducts, 400);
            }
        });

        if (getArguments() != null && getArguments().getBoolean(ARG_FOCUS_SEARCH)) {
            b.editSearch.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            b.editSearch.postDelayed(() -> imm.showSoftInput(b.editSearch, InputMethodManager.SHOW_IMPLICIT), 200);
        }

        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        request(() -> repo().catalog.getCategories(), this::showCategoryChips, message -> { });
    }

    private void showCategoryChips(List<Category> categories) {
        b.chipGroup.removeAllViews();
        b.chipGroup.addView(createChip(ALL, "All"));
        for (Category c : categories) {
            b.chipGroup.addView(createChip(c.getCategoryId(), c.getName()));
        }
    }

    private Chip createChip(long id, String text) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChecked(id == selectedCategory);
        chip.setOnClickListener(v -> {
            selectedCategory = id;
            chip.setChecked(true);
            loadProducts();
        });
        return chip;
    }

    private void loadProducts() {
        if (b == null) return;
        String search = b.editSearch.getText() == null ? "" : b.editSearch.getText().toString().trim();
        Long categoryId = selectedCategory == ALL ? null : selectedCategory;

        if (adapter.getItemCount() == 0) b.stateView.showLoading();
        request(() -> repo().catalog.getProducts(categoryId, search), products -> {
            b.swipeRefresh.setRefreshing(false);
            adapter.setItems(products);
            b.textCount.setText(products.size() + (products.size() == 1 ? " product" : " products"));
            if (products.isEmpty()) {
                b.stateView.showEmpty(R.drawable.ic_search, "No products found", "Try another word or category");
            } else {
                b.stateView.hide();
            }
        }, message -> {
            b.swipeRefresh.setRefreshing(false);
            b.stateView.showError(message, this::loadProducts);
        });
    }

    @Override
    public void onDestroyView() {
        searchHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
        b = null;
    }
}
