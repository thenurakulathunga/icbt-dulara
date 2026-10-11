package com.example.printexpress.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * ONE RecyclerView adapter for every list in the app.
 * You only say which row layout to use and how to fill it:
 *
 *   GenericAdapter<Product> adapter = new GenericAdapter<>(R.layout.item_product, (view, product, position) -> {
 *       ItemProductBinding b = ItemProductBinding.bind(view);
 *       b.textName.setText(product.getName());
 *   });
 *   adapter.setOnItemClick((product, position) -> openProduct(product));
 *   recyclerView.setAdapter(adapter);
 *   adapter.setItems(products);
 */
public class GenericAdapter<T> extends RecyclerView.Adapter<GenericAdapter.Holder> {

    public interface Binder<T> {
        void bind(View itemView, T item, int position);
    }

    public interface OnItemClick<T> {
        void onClick(T item, int position);
    }

    private final int layoutRes;
    private final Binder<T> binder;
    private final List<T> items = new ArrayList<>();
    private OnItemClick<T> onItemClick;

    public GenericAdapter(@LayoutRes int layoutRes, Binder<T> binder) {
        this.layoutRes = layoutRes;
        this.binder = binder;
    }

    public void setItems(List<T> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return items;
    }

    public void setOnItemClick(OnItemClick<T> listener) {
        this.onItemClick = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        T item = items.get(position);
        binder.bind(holder.itemView, item, position);
        if (onItemClick != null) {
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) onItemClick.onClick(items.get(pos), pos);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        Holder(View itemView) {
            super(itemView);
        }
    }
}
