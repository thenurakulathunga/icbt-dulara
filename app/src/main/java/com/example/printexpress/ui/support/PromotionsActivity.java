package com.example.printexpress.ui.support;

import android.view.View;
import android.view.ViewGroup;

import com.example.printexpress.R;
import com.example.printexpress.data.model.Promotion;
import com.example.printexpress.databinding.ItemPromoBinding;
import com.example.printexpress.ui.base.BaseListActivity;

/** All running seasonal / bulk offers. Re-uses the same card as the Home carousel. */
public class PromotionsActivity extends BaseListActivity<Promotion> {

    @Override
    protected String getScreenTitle() {
        return getString(R.string.offers_promotions);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_promo;
    }

    @Override
    protected void bindItem(View itemView, Promotion promo, int position) {
        // The Home carousel card is 290dp wide; here it fills the screen width
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.setMarginEnd(0);
        lp.bottomMargin = (int) (14 * getResources().getDisplayMetrics().density);
        itemView.setLayoutParams(lp);

        ItemPromoBinding pb = ItemPromoBinding.bind(itemView);
        PromoViews.bind(pb, promo);
        if (promo.getMinQuantity() > 1) {
            pb.textValid.setText(pb.textValid.getText() + "  •  Min " + promo.getMinQuantity() + " items");
        }
    }

    @Override
    protected void loadItems() {
        request(() -> repo.content.getActivePromotions(), false, this::showItems, this::showError);
    }

    @Override
    protected int getEmptyIcon() {
        return R.drawable.ic_offer;
    }

    @Override
    protected String getEmptyTitle() {
        return "No offers right now";
    }

    @Override
    protected String getEmptyMessage() {
        return "Check back soon for seasonal deals";
    }
}
