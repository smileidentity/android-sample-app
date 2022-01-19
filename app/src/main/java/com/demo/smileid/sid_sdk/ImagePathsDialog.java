package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smileidentity.libsmileid.core.SIFileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImagePathsDialog {

    private Dialog mDialog;
    private DlgListener mListener;
    private RecyclerView mRvJobs;
    private RecyclerView mRvIDCards;
    private RecyclerView mRvSelfies;
    private SIFileManager mSiFileManager = new SIFileManager();
    private TagList mTagList;

    public interface DlgListener {
        void submit(String countryCode, String idType);
        void cancel();
    }

    public ImagePathsDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_img_paths_dlg, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(root);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);

        mRvJobs = mDialog.findViewById(R.id.rvJobs);
        mRvJobs.setLayoutManager(new LinearLayoutManager(mDialog.getContext()));
        mRvJobs.setHasFixedSize(false);

        mRvIDCards = mDialog.findViewById(R.id.rvIDCards);
        mRvIDCards.setLayoutManager(new GridLayoutManager(mDialog.getContext(), 2));
        mRvIDCards.setHasFixedSize(false);

        mRvSelfies = mDialog.findViewById(R.id.rvSelfies);
        mRvSelfies.setLayoutManager(new GridLayoutManager(mDialog.getContext(), 2));
        mRvSelfies.setHasFixedSize(false);

        /*mDialog.findViewById(R.id.tvSubmit).setOnClickListener(v -> {
             if (mListener == null) return;
             mDialog.cancel();
             mListener.submit("", "");
        });*/

        mDialog.findViewById(R.id.ivBtnCancel).setOnClickListener(v -> {
            if (mDialog.findViewById(R.id.nsvPictures).getVisibility() == View.VISIBLE) {
                hideTagDetails();
                return;
            }
            cancelDialog();
        });

        mDialog.findViewById(R.id.tvClearTags).setOnClickListener(v -> {
            clearTags();
        });

        mListener = listener;
    }

    public void showDialog() {
        mDialog.show();

        Set<String> tags = mSiFileManager.getIdleTags(mDialog.getContext()); //AppData.getInstance(mDialog.getContext()).getTags();

        mDialog.findViewById(R.id.rvJobs).setVisibility((!tags.isEmpty()) ? View.VISIBLE :
            View.GONE);

        mDialog.findViewById(R.id.tvEmptyList).setVisibility((tags.isEmpty()) ? View.VISIBLE :
            View.GONE);

        mDialog.findViewById(R.id.tvClearTags).setVisibility((!tags.isEmpty()) ? View.VISIBLE :
            View.GONE);

        if (!tags.isEmpty()) {
            mTagList = new TagList(tags);
            mRvJobs.setAdapter(mTagList);
        }
    }

    public void cancelDialog() {
        mDialog.cancel();
        if (mListener == null) return;
        mListener.cancel();
    }

    public void clearTags() {
        mSiFileManager.clearIdleTags(mDialog.getContext());
        mTagList.deleteAll();
        mDialog.findViewById(R.id.rvJobs).setVisibility(View.GONE);
        mDialog.findViewById(R.id.tvEmptyList).setVisibility(View.VISIBLE);
        mDialog.findViewById(R.id.tvClearTags).setVisibility(View.GONE);
    }

    private void showTagDetails(String tag, List<File> idCards, List<File> selfies) {
        mDialog.findViewById(R.id.nsvPictures).setVisibility(View.VISIBLE);
        ((ImageView) mDialog.findViewById(R.id.ivBtnCancel)).setImageResource(R.drawable.ic_back);
        ((TextView) mDialog.findViewById(R.id.tvTitle)).setText(tag);

        mDialog.findViewById(R.id.tvCards).setVisibility((!idCards.isEmpty()) ? View.VISIBLE :
            View.GONE);

        mRvIDCards.setVisibility((!idCards.isEmpty()) ? View.VISIBLE :
            View.GONE);

        if (!idCards.isEmpty()) {
            mRvIDCards.setAdapter(new ImageList(idCards));
        }

        mDialog.findViewById(R.id.tvSelfies).setVisibility((!selfies.isEmpty()) ? View.VISIBLE :
            View.GONE);

        mRvSelfies.setVisibility((!selfies.isEmpty()) ? View.VISIBLE :
            View.GONE);

        if (!selfies.isEmpty()) {
            mRvSelfies.setAdapter(new ImageList(selfies));
        }
    }

    private void hideTagDetails() {
        mDialog.findViewById(R.id.nsvPictures).setVisibility(View.GONE);
        ((ImageView) mDialog.findViewById(R.id.ivBtnCancel)).setImageResource(R.drawable.ic_cancel);
        ((TextView) mDialog.findViewById(R.id.tvTitle)).setText("OUTSTANDING JOBS");
    }

    class TagList extends RecyclerView.Adapter<TagViewHolder> {

        List<String> mTags = new ArrayList<>();

        public TagList(Set<String> tags) {
            mTags.addAll(tags);
        }

        @NonNull
        @Override
        public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new TagViewHolder(LayoutInflater.from(mDialog.getContext()).inflate(
                R.layout.layout_img_path_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TagViewHolder viewHolder, int i) {
            viewHolder.populate(mTags.get(i));
        }

        @Override
        public int getItemCount() {
            return (mTags == null) ? 0 : mTags.size();
        }

        public void deleteItem(String tag) {
            int position = mTags.indexOf(tag);
            mTags.remove(tag);
            notifyItemRemoved(position);
        }

        public void deleteAll() {
            mTags = new ArrayList<>();
            notifyDataSetChanged();
        }
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        public TagViewHolder(View view) {
            super(view);
        }

        public void populate(final String tag) {
            final List<File> idCards = mSiFileManager.getIdCards(tag, itemView.getContext());
            int idCardCount = idCards.size();
            final List<File> selfies = mSiFileManager.getSelfies(tag, itemView.getContext());
            int selfieCount = selfies.size();
            ((TextView) itemView.findViewById(R.id.tvTag)).setText(tag);

            String span = "Selfies: " + selfieCount + " · ID Cards: " + idCardCount;
            SpannableString ssb = new SpannableString(span);
            int color = mDialog.getContext().getColor(R.color.colorPrimaryDark);

            int start = span.indexOf("Selfies:");
            int end = start + "Selfies:".length();
            ForegroundColorSpan fcs = new ForegroundColorSpan(color);
            ssb.setSpan(fcs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            start = span.indexOf("ID Cards:");
            end = start + "ID Cards:".length();
            fcs = new ForegroundColorSpan(color);
            ssb.setSpan(fcs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            start = span.indexOf("·");
            end = start + "·".length();
            fcs = new ForegroundColorSpan(color);
            ssb.setSpan(fcs, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            start = span.indexOf("Selfies:");
            end = start + "Selfies:".length();
            StyleSpan ss = new StyleSpan(Typeface.BOLD);
            ssb.setSpan(ss, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            start = span.indexOf("ID Cards:");
            end = start + "ID Cards:".length();
            ss = new StyleSpan(Typeface.BOLD);
            ssb.setSpan(ss, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            start = span.indexOf("·");
            end = start + "·".length();
            ss = new StyleSpan(Typeface.BOLD);
            ssb.setSpan(ss, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ((TextView) itemView.findViewById(R.id.Details)).setText(ssb);

            itemView.findViewById(R.id.ivOpen).setOnClickListener(v -> {
                showTagDetails(tag, idCards, selfies);
            });

            itemView.findViewById(R.id.ivDelete).setOnClickListener(v -> {
                mSiFileManager.clearData(tag, itemView.getContext());
                ((TagList) getBindingAdapter()).deleteItem(tag);
            });
        }
    }

    class ImageList extends RecyclerView.Adapter<SelfieViewHolder> {

        List<File> mFiles = new ArrayList<>();

        public ImageList(List<File> files) {
            mFiles = files;
        }

        @NonNull
        @Override
        public SelfieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new SelfieViewHolder(LayoutInflater.from(mDialog.getContext()).inflate(
                R.layout.layout_captured_img_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SelfieViewHolder viewHolder, int i) {
            viewHolder.populate(mFiles.get(i));
        }

        @Override
        public int getItemCount() {
            return (mFiles == null) ? 0 : mFiles.size();
        }
    }

    class SelfieViewHolder extends RecyclerView.ViewHolder {

        public SelfieViewHolder(View view) {
            super(view);
        }

        public void populate(final File selfie) {
            Bitmap bitmap = BitmapFactory.decodeFile(selfie.getAbsolutePath());
            ((ImageView) itemView.findViewById(R.id.ivSelfie)).setImageBitmap(bitmap);
        }
    }
}