package eu.warble.aquamodinstaller.ui.main.search_mod;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import eu.warble.aquamodinstaller.R;
import eu.warble.aquamodinstaller.ui.base.BaseFragment;
import eu.warble.aquamodinstaller.ui.main.install_mod.InstallModFragment;

public class SearchModFragment extends BaseFragment<SearchModPresenter> {

    TextView lastCheck;
    View checkUpdateBtn;
    ProgressBar progressBar;

    public static SearchModFragment newInstance(){
        SearchModFragment fragment = new SearchModFragment();
        return fragment;
    }

    @Override
    protected SearchModPresenter createPresenter() {
        return new SearchModPresenter(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_mod, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        lastCheck = view.findViewById(R.id.last_checked_date);
        checkUpdateBtn = view.findViewById(R.id.action_button);
        progressBar = view.findViewById(R.id.progressBar);
        checkUpdateBtn.setOnClickListener(l ->{
            presenter.checkModAvailable();
            setLoadingViewsState(true);
        });
        ((ImageView)checkUpdateBtn.findViewById(R.id.action_button_image))
                .setColorFilter(ContextCompat.getColor(context, R.color.dividerColor));
    }

    void onModDownloadFound(){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, InstallModFragment.newInstance())
                .commit();
    }

    void setLoadingViewsState(boolean state){
        if (state) {
            checkUpdateBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            checkUpdateBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
