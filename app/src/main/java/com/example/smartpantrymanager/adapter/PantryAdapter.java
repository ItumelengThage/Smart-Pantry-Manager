package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnPantryItemActionListener listener;

    public PantryAdapter(OnPantryItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int
            viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText("Quantity: " + formatQty(item.getQuantity())
                + " " + item.getUnit());
        String expiry = item.getExpiryDate();

        if (expiry == null || expiry.trim().isEmpty()) {
            holder.expiry.setVisibility(View.GONE);
        }
        else {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText("Expires: " + expiry);
        }

        holder.edit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });
        holder.delete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    private String formatQty(double q) {
        if (q == Math.floor(q)) return String.valueOf((int) q);
        return String.valueOf(q);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiry;
        Button edit, delete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtIngredientName);
            quantity = itemView.findViewById(R.id.txtIngredientQuantity);
            expiry = itemView.findViewById(R.id.txtIngredientExpiry);
            edit = itemView.findViewById(R.id.btnEditIngredient);
            delete = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}

