package com.whitelightningdev.hararexpress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private List<Store> storeList;

    public StoreAdapter(List<Store> storeList) {
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = storeList.get(position);
        holder.storeName.setText(store.getName());
        // Assuming you have more views to bind

        // Example: Initialize ItemAdapter with the list of items
        ItemAdapter itemAdapter = new ItemAdapter(store.getItems());
        holder.recyclerViewItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewItems.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        if (storeList != null) {
            return storeList.size();
        } else {
            return 0;
        }
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView storeName;
        RecyclerView recyclerViewItems;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.storeName);
            recyclerViewItems = itemView.findViewById(R.id.recyclerViewItems);
        }
    }
}
