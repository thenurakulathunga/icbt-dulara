package com.example.printexpress.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.printexpress.R;
import com.example.printexpress.data.model.Category;
import com.example.printexpress.data.model.OrderSummary;
import com.example.printexpress.data.model.Promotion;
import com.example.printexpress.databinding.FragmentHomeBinding;
import com.example.printexpress.databinding.ItemCategoryBinding;
import com.example.printexpress.databinding.ItemPromoBinding;
import com.example.printexpress.databinding.ItemQuickActionBinding;
import com.example.printexpress.ui.base.BaseFragment;
import com.example.printexpress.ui.common.GenericAdapter;
import com.example.printexpress.ui.order.OrderViews;
import com.example.printexpress.ui.profile.NotificationsActivity;
import com.example.printexpress.ui.support.GalleryActivity;
import com.example.printexpress.ui.support.InfoListActivity;
import com.example.printexpress.ui.support.PromotionsActivity;
import com.example.printexpress.ui.support.PromoViews;
import com.example.printexpress.ui.support.SupportActivity;
import com.example.printexpress.util.IconMapper;
import com.example.printexpress.util.StatusHelper;

import java.util.Arrays;
import java.util.List;

/** Home tab: greeting, offers, categories, design help shortcuts and the latest order. */
public class HomeFragment extends BaseFragment {

    /** A shortcut tile in the "Design help" grid. */
    private static class QuickAction {
        final int icon;
        final String title;
        final String subtitle;
        final Runnable action;

        QuickAction(int icon, String title, String subtitle, Runnable action) {
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
            this.action = action;
        }
    }

    private FragmentHomeBinding b;
    private GenericAdapter<Promotion> promoAdapter;
    private GenericAdapter<Category> categoryAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        b.textGreeting.setText("Hi, " + session().getName().split(" ")[0] + "!");
        b.buttonBell.setOnClickListener(v -> startActivity(new Intent(requireContext(), NotificationsActivity.class)));
        b.searchBar.setOnClickListener(v -> main().openBrowse(null, true));
        b.textSeeAllOffers.setOnClickListener(v -> startActivity(new Intent(requireContext(), PromotionsActivity.class)));
        b.textSeeAllCategories.setOnClickListener(v -> main().openBrowse(null, false));
        b.textSeeAllOrders.setOnClickListener(v -> main().openOrders());
        b.swipeRefresh.setColorSchemeResources(R.color.px_primary);
        b.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.px_card);
        b.swipeRefresh.setOnRefreshListener(this::loadData);

        // Offers carousel
        promoAdapter = new GenericAdapter<>(R.layout.item_promo,
                (itemView, promo, position) -> PromoViews.bind(ItemPromoBinding.bind(itemView), promo));
        b.recyclerPromos.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        b.recyclerPromos.setAdapter(promoAdapter);

        // Categories row
        categoryAdapter = new GenericAdapter<>(R.layout.item_category, (itemView, category, position) -> {
            ItemCategoryBinding cb = ItemCategoryBinding.bind(itemView);
            cb.imageIcon.setImageResource(IconMapper.categoryIcon(category.getIconName()));
            cb.textName.setText(category.getName());
        });
        categoryAdapter.setOnItemClick((category, position) -> main().openBrowse(category.getCategoryId(), false));
        b.recyclerCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        b.recyclerCategories.setAdapter(categoryAdapter);

        setupQuickActions();
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUnreadCount();
    }

    private void setupQuickActions() {
        List<QuickAction> actions = Arrays.asList(
                new QuickAction(R.drawable.ic_image, getString(R.string.gallery), "Sample designs",
                        () -> startActivity(new Intent(requireContext(), GalleryActivity.class))),
                new QuickAction(R.drawable.ic_doc, getString(R.string.guidelines), "Bleed, CMYK, DPI",
                        () -> InfoListActivity.open(requireContext(), InfoListActivity.TYPE_GUIDELINES)),
                new QuickAction(R.drawable.ic_help, getString(R.string.faqs), "Quick answers",
                        () -> InfoListActivity.open(requireContext(), InfoListActivity.TYPE_FAQS)),
                new QuickAction(R.drawable.ic_chat, getString(R.string.support), "Ask our team",
                        () -> startActivity(new Intent(requireContext(), SupportActivity.class))));

        GenericAdapter<QuickAction> adapter = new GenericAdapter<>(R.layout.item_quick_action, (itemView, a, position) -> {
            ItemQuickActionBinding qb = ItemQuickActionBinding.bind(itemView);
            qb.imageIcon.setImageResource(a.icon);
            qb.textTitle.setText(a.title);
            qb.textSubtitle.setText(a.subtitle);
        });
        adapter.setOnItemClick((a, position) -> a.action.run());
        b.recyclerQuickActions.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        b.recyclerQuickActions.setNestedScrollingEnabled(false);
        b.recyclerQuickActions.setAdapter(adapter);
        adapter.setItems(actions);
    }

    private void loadData() {
        long userId = userId();
        request(() -> repo().content.getActivePromotions(), promos -> {
            promoAdapter.setItems(promos);
            b.sectionOffers.setVisibility(promos.isEmpty() ? View.GONE : View.VISIBLE);
        }, this::onError);

        request(() -> repo().catalog.getCategories(), categories -> {
            b.swipeRefresh.setRefreshing(false);
            categoryAdapter.setItems(categories);
        }, this::onError);

        // Show the newest order that is still in progress
        request(() -> repo().orders.getOrders(userId), orders -> {
            OrderSummary latest = null;
            for (OrderSummary o : orders) {
                if (StatusHelper.isActive(o.getStatus())) {
                    latest = o;
                    break;
                }
            }
            b.sectionLatestOrder.setVisibility(latest == null ? View.GONE : View.VISIBLE);
            if (latest != null) {
                final long orderId = latest.getOrderId();
                OrderViews.bind(b.cardLatestOrder.getRoot(), latest);
                b.cardLatestOrder.getRoot().setOnClickListener(v -> OrderViews.open(requireContext(), orderId));
            }
        }, message -> { });
    }

    private void loadUnreadCount() {
        long userId = userId();
        request(() -> repo().notifications.countUnread(userId), count -> {
            b.textBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            b.textBadge.setText(count > 9 ? "9+" : String.valueOf(count));
        }, message -> { });
    }

    private void onError(String message) {
        b.swipeRefresh.setRefreshing(false);
        showMessage(message);
    }

    private MainActivity main() {
        return (MainActivity) requireActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}
