package com.example.printexpress.ui.support;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.printexpress.R;
import com.example.printexpress.data.model.InfoItem;
import com.example.printexpress.databinding.ItemInfoBinding;
import com.example.printexpress.ui.base.BaseListActivity;

import java.util.HashSet;
import java.util.Set;

/**
 * ONE screen for both "Print guidelines" and "FAQs".
 * Each row is a card that expands to show the answer when tapped.
 */
public class InfoListActivity extends BaseListActivity<InfoItem> {

    public static final String EXTRA_TYPE = "type";
    public static final String TYPE_GUIDELINES = "guidelines";
    public static final String TYPE_FAQS = "faqs";

    private final Set<Integer> expanded = new HashSet<>();

    public static void open(Context context, String type) {
        context.startActivity(new Intent(context, InfoListActivity.class).putExtra(EXTRA_TYPE, type));
    }

    private boolean isFaqs() {
        return TYPE_FAQS.equals(getIntent().getStringExtra(EXTRA_TYPE));
    }

    @Override
    protected String getScreenTitle() {
        return getString(isFaqs() ? R.string.faqs : R.string.guidelines);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_info;
    }

    @Override
    protected void bindItem(View itemView, InfoItem item, int position) {
        ItemInfoBinding ib = ItemInfoBinding.bind(itemView);
        boolean open = expanded.contains(position);
        ib.imageIcon.setImageResource(isFaqs() ? R.drawable.ic_help : R.drawable.ic_doc);
        ib.textHeading.setText(item.getHeading());
        ib.textBody.setText(item.getBody());
        ib.textBody.setVisibility(open ? View.VISIBLE : View.GONE);
        ib.imageExpand.setRotation(open ? 180 : 0);
    }

    @Override
    protected void onItemClick(InfoItem item, int position) {
        if (!expanded.remove(position)) expanded.add(position);
        adapter.notifyItemChanged(position);
    }

    @Override
    protected void loadItems() {
        if (isFaqs()) {
            request(() -> repo.content.getFaqs(), false, this::showItems, this::showError);
        } else {
            request(() -> repo.content.getGuidelines(), false, this::showItems, this::showError);
        }
    }

    @Override
    protected int getEmptyIcon() {
        return R.drawable.ic_info;
    }

    @Override
    protected String getEmptyTitle() {
        return "Nothing here yet";
    }
}
