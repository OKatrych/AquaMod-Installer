package eu.warble.aquamodinstaller.ui.splash;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import eu.warble.aquamodinstaller.R;
import eu.warble.aquamodinstaller.ui.base.BaseActivity;
import eu.warble.aquamodinstaller.ui.main.MainActivity;
import eu.warble.aquamodinstaller.utils.Tools;

public class SplashActivity extends BaseActivity<SplashPresenter> {

    FloatingActionButton actionButton;
    TextView actionText;

    @Override
    protected SplashPresenter createPresenter() {
        return new SplashPresenter(this);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        setContentView(R.layout.splash);
        initViews();
    }

    private void initViews() {
        actionButton = findViewById(R.id.action_button);
        actionText = findViewById(R.id.action_text);

        actionButton.setOnClickListener(l -> {
            presenter.checkIMEI();
            actionText.setText(R.string.checking_imei);
            setLoadingViewsState(true);
        });
    }

    public void setLoadingViewsState(boolean state) {
        if (state) {
            actionButton.setImageResource(R.drawable.refresh);
            actionButton.setAnimation(Tools.getRotateAnimation());
        }
        else {
            //actionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
            actionButton.setEnabled(false);
            actionButton.setImageResource(R.drawable.alert);
            actionButton.setAnimation(null);
        }
    }

    public void showError(String string) {
        actionText.setText(string);
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
