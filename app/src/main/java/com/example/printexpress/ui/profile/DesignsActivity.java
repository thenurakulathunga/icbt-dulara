package com.example.printexpress.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.printexpress.R;
import com.example.printexpress.data.model.Design;
import com.example.printexpress.databinding.DialogInputBinding;
import com.example.printexpress.databinding.ItemDesignBinding;
import com.example.printexpress.ui.base.BaseListActivity;
import com.example.printexpress.ui.common.Form;
import com.example.printexpress.util.FileHelper;
import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.Validator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * "My saved designs": upload artwork (PDF / PNG / JPG) and delete it.
 * Opened from the order form in PICK mode: tapping a design sends it back to the form.
 */
public class DesignsActivity extends BaseListActivity<Design> {

    public static final String EXTRA_PICK = "pick";
    public static final String RESULT_DESIGN_ID = "designId";
    public static final String RESULT_DESIGN_TITLE = "designTitle";

    private boolean pickMode;

    /** Android's file picker - only lets the customer choose allowed file types. */
    private final ActivityResultLauncher<String[]> pickFile = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) askTitleAndUpload(uri);
            });

    public static Intent pickIntent(Context context) {
        return new Intent(context, DesignsActivity.class).putExtra(EXTRA_PICK, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickMode = getIntent().getBooleanExtra(EXTRA_PICK, false);
        if (pickMode) b.toolbar.setTitle("Choose a design");
        showFab(getString(R.string.upload_design), v -> pickFile.launch(FileHelper.ALLOWED_TYPES));
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.my_designs);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_design;
    }

    @Override
    protected void bindItem(View itemView, Design design, int position) {
        ItemDesignBinding ib = ItemDesignBinding.bind(itemView);
        ib.imageType.setImageResource(design.isPdf() ? R.drawable.ic_doc : R.drawable.ic_image);
        ib.textTitle.setText(design.getTitle());
        ib.textFile.setText(design.getOriginalName());
        ib.textDate.setText("Uploaded " + Formatter.dateTime(design.getUploadedAt()));
        ib.buttonDelete.setVisibility(pickMode ? View.GONE : View.VISIBLE);
        ib.buttonDelete.setOnClickListener(v -> confirm("Delete design",
                "Delete \"" + design.getTitle() + "\"? Orders that used it will keep their print.",
                getString(R.string.delete),
                () -> requestWithLoading(() -> repo.designs.delete(userId(), design.getDesignId()), this::showItems)));
    }

    @Override
    protected void onItemClick(Design design, int position) {
        if (pickMode) returnDesign(design);
    }

    @Override
    protected void loadItems() {
        request(() -> repo.designs.getDesigns(userId()), false, this::showItems, this::showError);
    }

    @Override
    protected int getEmptyIcon() {
        return R.drawable.ic_upload;
    }

    @Override
    protected String getEmptyTitle() {
        return "No designs yet";
    }

    @Override
    protected String getEmptyMessage() {
        return "Upload a PDF, PNG or JPG (max 10 MB)";
    }

    // ---------- Upload ----------

    private void askTitleAndUpload(Uri uri) {
        if (FileHelper.getFileSize(this, uri) > FileHelper.MAX_SIZE_BYTES) {
            showMessage("File is too large. Maximum size is 10 MB");
            return;
        }
        String fileName = FileHelper.getFileName(this, uri);
        DialogInputBinding input = DialogInputBinding.inflate(LayoutInflater.from(this));
        input.input.setHint(getString(R.string.design_title));
        Form.setText(input.input, fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName);
        input.textHelper.setText("File: " + fileName);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.upload_design)
                .setView(input.getRoot())
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton("Upload", null)
                .show();

        // Validate before closing the dialog
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = Form.text(input.input);
            if (!Form.check(input.input, Validator.length(title, "Title", 3, 80))) return;
            dialog.dismiss();
            upload(uri, title);
        });
    }

    /** Copies the file into the app and saves a row in the designs table. */
    private void upload(Uri uri, String title) {
        requestWithLoading(() -> repo.designs.upload(userId(), uri, title), designs -> {
            showItems(designs);
            showMessage("Design uploaded");
            // In pick mode the newest design (first in the list) goes straight back to the order form
            if (pickMode && !designs.isEmpty()) returnDesign(designs.get(0));
        });
    }

    private void returnDesign(Design design) {
        Intent data = new Intent()
                .putExtra(RESULT_DESIGN_ID, design.getDesignId())
                .putExtra(RESULT_DESIGN_TITLE, design.getTitle() + " (" + design.getOriginalName() + ")");
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
