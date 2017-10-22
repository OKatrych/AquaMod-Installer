package eu.warble.aquamodinstaller.ui.main.install_mod;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import eu.warble.aquamodinstaller.R;
import eu.warble.aquamodinstaller.ui.base.BaseFragment;

public class InstallModFragment extends BaseFragment<InstallModPresenter> {

    View actionButton, infoFrame;
    TextView header, output, title1, title2, changelog, updateSize;
    ProgressBar progressBar;


    public static InstallModFragment newInstance() {
        InstallModFragment fragment = new InstallModFragment();
        return fragment;
    }
    
    @Override
    protected InstallModPresenter createPresenter() {
        return new InstallModPresenter(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_install_mod, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        header = view.findViewById(R.id.header);
        title1 = view.findViewById(R.id.title1);
        title2 = view.findViewById(R.id.title2);
        changelog = view.findViewById(R.id.changelog);
        updateSize = view.findViewById(R.id.update_size);
        progressBar = view.findViewById(R.id.progressBar);
        actionButton = view.findViewById(R.id.action_button);
        infoFrame = view.findViewById(R.id.info_frame);
        output = view.findViewById(R.id.output);
    }

    void showDownloadButton(){
        setLoadingViewsState(false);
        ((TextView)actionButton.findViewById(R.id.action_button_text)).setText(R.string.download_and_install);
        ((ImageView)actionButton.findViewById(R.id.action_button_image)).setImageResource(R.drawable.download);
        actionButton.setOnClickListener(l -> {
            actionButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            header.setText(R.string.downloading);
            presenter.downloadUpdate();
        });
    }

    void showInstallButton(){
        setLoadingViewsState(false);
        header.setText(R.string.ready_to_install);
        ((TextView)actionButton.findViewById(R.id.action_button_text)).setText(R.string.start_installing);
        actionButton.setOnClickListener(l -> {
            header.setText(R.string.installing);
            setLoadingViewsState(true);
            infoFrame.setVisibility(View.GONE);
            output.setVisibility(View.VISIBLE);
            presenter.installUpdate();
        });
    }

    void showCheckSumError() {
        header.setText(R.string.checking_error);
        progressBar.setVisibility(View.GONE);
    }

    void setLoadingViewsState(boolean state){
        if (state) {
            actionButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            actionButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == presenter.downloadId){
                header.setText(R.string.checking);
                presenter.checkMD5Sum();
            }
        }
    };

    public BroadcastReceiver onNotificationClick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //todo
        }
    };

    @Override
    public void onResume() {
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //registerReceiver(onNotificationClick, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(onComplete);
        //getActivity().unregisterReceiver(onNotificationClick);
    }
}
