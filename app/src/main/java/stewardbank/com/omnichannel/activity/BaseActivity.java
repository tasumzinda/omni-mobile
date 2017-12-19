package stewardbank.com.omnichannel.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import stewardbank.com.omnichannel.R;

/**
 * @uthor Tasu Muzinda
 */
public class BaseActivity extends AppCompatActivity{

    private Toolbar toolbar;

    public Toolbar getToolbar(String title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        return toolbar;
    }
}
