package com.smartpantry.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.app.R;
import com.smartpantry.app.logic.UnitConverter;
import com.smartpantry.app.model.PantryItem;
import com.smartpantry.app.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Binds PantryItem rows from the database to item_pantry.xml cards.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemListener {
        void onPantryItemClick(PantryItem item);

        void onPantryItemDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnPantryItemListener listener;
    private boolean metricBase;
    private boolean expiryAlerts;
    private int expiryDays;

    public PantryAdapter(OnPantryItemListener listener) {
        this.listener = listener;
    }

    public void setDisplayOptions(boolean metricBase, boolean expiryAlerts, int expiryDays) {
        this.metricBase = metricBase;
        this.expiryAlerts = expiryAlerts;
        this.expiryDays = expiryDays;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(UnitConverter.describe(item.getQuantity(), item.getUnit(), metricBase));

        if (item.hasExpiryDate()) {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText(holder.itemView.getContext()
                    .getString(R.string.expires_on, DateUtils.toDisplay(item.getExpiryDate())));
        } else {
            holder.expiry.setVisibility(View.GONE);
        }
        bindBadge(holder, item);

        holder.itemView.setOnClickListener(v -> listener.onPantryItemClick(item));
        holder.delete.setOnClickListener(v -> listener.onPantryItemDelete(item));
    }

    private void bindBadge(PantryViewHolder holder, PantryItem item) {
        Integer days = DateUtils.daysUntil(item.getExpiryDate());
        if (!expiryAlerts || days == null || days > expiryDays) {
            holder.badge.setVisibility(View.GONE);
            return;
        }
        holder.badge.setVisibility(View.VISIBLE);
        if (days < 0) {
            holder.badge.setText(R.string.badge_expired);
            holder.badge.setBackgroundResource(R.drawable.bg_badge_expired);
        } else {
            holder.badge.setText(R.string.badge_expiring);
            holder.badge.setBackgroundResource(R.drawable.bg_badge_warning);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView quantity;
        final TextView expiry;
        final TextView badge;
        final ImageButton delete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            quantity = itemView.findViewById(R.id.text_quantity);
            expiry = itemView.findViewById(R.id.text_expiry);
            badge = itemView.findViewById(R.id.text_badge);
            delete = itemView.findViewById(R.id.button_delete);
        }
    }
}
