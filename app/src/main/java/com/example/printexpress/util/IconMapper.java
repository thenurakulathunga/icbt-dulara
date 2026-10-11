package com.example.printexpress.util;

import com.example.printexpress.R;

/** The database stores an icon NAME (e.g. "ic_cat_mugs"); this finds the matching drawable. */
public final class IconMapper {

    private IconMapper() {
    }

    public static int categoryIcon(String iconName) {
        if (iconName == null) return R.drawable.ic_print;
        switch (iconName) {
            case "ic_cat_cards":
                return R.drawable.ic_cat_cards;
            case "ic_cat_flyers":
                return R.drawable.ic_cat_flyers;
            case "ic_cat_posters":
                return R.drawable.ic_cat_posters;
            case "ic_cat_banners":
                return R.drawable.ic_cat_banners;
            case "ic_cat_stickers":
                return R.drawable.ic_cat_stickers;
            case "ic_cat_mugs":
                return R.drawable.ic_cat_mugs;
            case "ic_cat_tshirts":
                return R.drawable.ic_cat_tshirts;
            default:
                return R.drawable.ic_print;
        }
    }

    public static int notificationIcon(String type) {
        if ("ORDER".equals(type)) return R.drawable.ic_orders;
        if ("PROMOTION".equals(type)) return R.drawable.ic_offer;
        return R.drawable.ic_info;
    }
}
