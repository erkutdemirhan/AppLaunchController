package com.example.android.applaunchcontroller.screens;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.applaunchcontroller.R;
import com.example.android.applaunchcontroller.entities.InstalledApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InstalledAppsAdapter extends RecyclerView.Adapter<InstalledAppsAdapter.InstalledAppViewHolder> {

    private List<InstalledApp> installedAppList;
    private ItemClickListener itemClickListener = ItemClickListener.DO_NOTHING;

    public InstalledAppsAdapter() {
        installedAppList = new ArrayList<>();
    }

    @NonNull
    @Override
    public InstalledAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installed_app, parent, false);
        return new InstalledAppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InstalledAppViewHolder holder, int position) {
        holder.bind(installedAppList.get(position), position, itemClickListener);
    }

    @Override
    public int getItemCount() {
        return installedAppList.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        if (itemClickListener != null) {
            this.itemClickListener = itemClickListener;
        }
    }

    public void update(final List<InstalledApp> newList) {
        if (newList != null) {
            installedAppList.clear();
            installedAppList.addAll(newList);
            notifyDataSetChanged();
        }
    }

    public void update(final InstalledApp installedApp, int position) {
        if (position >= 0 && position < getItemCount()) {
            installedAppList.set(position, installedApp);
            notifyItemChanged(position);
        }
    }

    class InstalledAppViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.app_icon_iv)
        protected ImageView appIconIv;

        @BindView(R.id.app_name_tv)
        protected TextView appNameTv;

        @BindView(R.id.blacklist_label_tv)
        protected TextView blacklistLabelTv;

        InstalledAppViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(InstalledAppViewHolder.this, itemView);
        }

        public void bind(final InstalledApp installedApp, final int position, final ItemClickListener itemClickListener) {
            appNameTv.setText(installedApp.getName());
            Drawable iconDrawable = getAppIconDrawable(installedApp.getPackageName());
            if (iconDrawable != null) {
                appIconIv.setImageDrawable(iconDrawable);
            } else {
                appIconIv.setImageResource(R.drawable.ic_launcher_foreground);
            }
            blacklistLabelTv.setVisibility(installedApp.isInBlackList() ? View.VISIBLE:View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(installedApp, position);
                }
            });
        }

        private Drawable getAppIconDrawable(final String packageName) {
            Drawable appIconDrawable = null;
            try {
                appIconDrawable = itemView.getContext().getPackageManager().getApplicationIcon(packageName);
            } catch (PackageManager.NameNotFoundException ignored) {}
            return appIconDrawable;
        }
    }

    public interface ItemClickListener {
        void onItemClick(InstalledApp installedApp, int position);

        ItemClickListener DO_NOTHING = new ItemClickListener() {
            @Override
            public void onItemClick(InstalledApp installedApp, int position) {}
        };
    }
}
