package com.demo.smileid.sid_sdk;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class IdListAdapter extends ItemListAdapter {

  private ArrayList<String> mIds = new ArrayList<>();

  @Override
  public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
    viewHolder.populate(mIds.get(i).replace("_", " "));
    viewHolder.itemView.setOnClickListener(v -> mListener.applyChoice(mIds.get(i)));
  }

  @Override
  public int getItemCount() {
    return mIds.size();
  }

  public void setIdList(List<String> ids) {
    mIds = new ArrayList<>(ids);
  }
}