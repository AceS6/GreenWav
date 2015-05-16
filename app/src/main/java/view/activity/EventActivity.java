package view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.greenwav.greenwav.R;

import model.Event;

/**
 * Created by sauray on 16/03/15.
 */
public class EventActivity extends ActionBarActivity{

    private Event currentEvent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Bundle extras = this.getIntent().getExtras();
        currentEvent = extras.getParcelable("EVENT");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getString(R.string.activity_home));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(currentEvent.getUrl());
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.INVISIBLE); // Make the bar disappear after URL is loaded
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // do something
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(EventActivity.this, R.string.connexion_required, Toast.LENGTH_SHORT).show();
            }
        });

        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
        webView.setWebViewClient(client);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.horaire_menu);
        return true;
    }
}


