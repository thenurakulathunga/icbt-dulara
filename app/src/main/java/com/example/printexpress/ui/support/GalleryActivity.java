package com.example.printexpress.ui.support;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.printexpress.R;
import com.example.printexpress.data.model.Category;
import com.example.printexpress.data.model.SampleDesign;
import com.example.printexpress.databinding.ItemGalleryBinding;
import com.example.printexpress.ui.base.BaseListActivity;
import com.example.printexpress.ui.main.MainActivity;
import com.example.printexpress.util.IconMapper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/** Gallery of sample designs (2-column grid) with a category filter. */
public class GalleryActivity extends BaseListActivity<SampleDesign> {

    private Long selectedCategory;   // null = all
    private ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Category filter chips go into the optional header area of activity_list.xml
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        chipGroup = new ChipGroup(this);
        chipGroup.setSingleLine(true);
        chipGroup.setSingleSelection(true);
        chipGroup.setSelectionRequired(true);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        chipGroup.setPadding(pad, 0, pad, pad / 2);
        scroll.addView(chipGroup);
        b.header.addView(scroll);

        request(() -> repo.catalog.getCategories(), false, this::showChips, message -> { });
    }

    private void showChips(List<Category> categories) {
        chipGroup.addView(chip(null, "All"));
        for (Category c : categories) chipGroup.addView(chip(c.getCategoryId(), c.getName()));
    }

    private Chip chip(Long categoryId, String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChecked(categoryId == null);
        chip.setOnClickListener(v -> {
            chip.setChecked(true);
            selectedCategory = categoryId;
            loadItems();
        });
        return chip;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new GridLayoutManager(this, 2);
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.gallery);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_gallery;
    }

    @Override
    protected void bindItem(View itemView, SampleDesign design, int position) {
        ItemGalleryBinding gb = ItemGalleryBinding.bind(itemView);
        gb.cardColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(design.getStyleColor())));
        gb.imageIcon.setImageResource(IconMapper.categoryIcon(design.getIconName()));
        gb.textCategory.setText(design.getCategoryName());
        gb.textTitle.setText(design.getTitle());
    }

    @Override
    protected void onItemClick(SampleDesign design, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(design.getTitle())
                .setMessage(design.getDescription()
                        + "\n\nLike this style? Choose a " + design.getCategoryName()
                        + " product and describe it in the order notes, or ask our design team.")
                .setNegativeButton("Close", null)
                .setPositiveButton("Browse " + design.getCategoryName(), (d, w) -> {
                    Intent intent = new Intent(this, MainActivity.class)
                            .putExtra(MainActivity.EXTRA_OPEN_CATEGORY, design.getCategoryId())
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                })
                .show();
    }

    @Override
    protected void loadItems() {
        Long categoryId = selectedCategory;
        request(() -> repo.content.getGallery(categoryId), false, this::showItems, this::showError);
    }

    @Override
    protected int getEmptyIcon() {
        return R.drawable.ic_image;
    }

    @Override
    protected String getEmptyTitle() {
        return "No samples in this category";
    }
}
