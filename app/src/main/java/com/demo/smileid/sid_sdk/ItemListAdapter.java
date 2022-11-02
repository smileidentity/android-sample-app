package com.demo.smileid.sid_sdk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.demo.smileid.sid_sdk.ItemListAdapter.ItemViewHolder;

public class ItemListAdapter extends RecyclerView.Adapter<ItemViewHolder> {

  protected ItemSelectedInterface mListener = null;

  public void setListener(ItemSelectedInterface listener) {
    mListener = listener;
  }

  @NonNull
  @Override
  public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(
      R.layout.layout_spinner_item, viewGroup, false);

    return new ItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
  }

  @Override
  public int getItemCount() {
    return 0;
  }

  class ItemViewHolder extends ViewHolder {

    public ItemViewHolder(@NonNull View itemView) {
      super(itemView);
    }

    public void populate(Object item) {
      mListener.buildItem((TextView) itemView, item, true);
    }
  }

  interface ItemSelectedInterface {
    void buildItem(TextView textView, Object object, boolean isMain);
    void applyChoice(Object object);
  }
}
