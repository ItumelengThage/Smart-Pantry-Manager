package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<PantryItem> items = new ArrayList<>();

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryViewHolder holder, int position) {

        PantryItem item = items.get(position);

        holder.name.setText(item.getName());
        holder.quantity.setText(
                "Quantity: " + item.getQuantity() + " " + item.getUnit());

        String expiry = item.getExpiryDate();

        if (expiry == null || expiry.trim().isEmpty()) {
            holder.expiry.setText("Expiry date: Not specified");
        } else {
            holder.expiry.setText("Expiry date: " + expiry);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView quantity;
        TextView expiry;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtIngredientName);
            quantity = itemView.findViewById(R.id.txtIngredientQuantity);
            expiry = itemView.findViewById(R.id.txtIngredientExpiry);
        }
    }
}
