package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ConsentCatAdapter extends RecyclerView.Adapter<ConsentCatAdapter.CatViewHolder> {

    ArrayList<ConsentCategory> mConsentCats = new ArrayList<>();

    public void setCategories(ArrayList<ConsentCategory> consentCats) {
        mConsentCats = consentCats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_consent_cat,
                viewGroup, false);
        return new CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder viewHolder, int i) {
        viewHolder.populate(mConsentCats.get(i));
    }

    @Override
    public int getItemCount() {
        return mConsentCats.size();
    }

    class CatViewHolder extends RecyclerView.ViewHolder {

        public CatViewHolder(View view) {
            super(view);
        }

        public void populate(final ConsentCategory category) {
            (itemView.findViewById(R.id.ivCatTooltip)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View root = LayoutInflater.from(itemView.getContext()).inflate(R.layout.layout_consent_tooltip, null);
                    root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    final Dialog dialog = new Dialog(itemView.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(root);
                    dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.setCancelable(false);

                    ((TextView) dialog.findViewById(R.id.tvHeading)).setText(category.mTooltipLbl.replace("||", " "));
                    ((TextView) dialog.findViewById(R.id.tvTooltip)).setText(category.mTooltipContent);

                    (dialog.findViewById(R.id.ivClose)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    (dialog.findViewById(R.id.clContainer)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });

            ((TextView) itemView.findViewById(R.id.tvCatLbl)).setText(category.mLabel);
            ((ImageView) itemView.findViewById(R.id.ivCatImg)).setImageDrawable(
                itemView.getContext().getResources().getDrawable(category.mIcon));
        }
    }
}