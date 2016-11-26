package com.qiwang.getsomethingfromhtml;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static String mDefaultEncoding = "UTF-8";
    private Button mClick;
    private EditText mEditText;
    private WebView mWebView;
    private Button mSave;
    private String mUrl;
    private String mHtml;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setWebView();
        initClick();
    }

    private void setWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                getHTMLInfo(url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    private void initClick() {
        mClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUrl = mEditText.getText().toString().replaceAll(" ", "");
                if (mUrl != null && mUrl.length() > 0) {
                    mWebView.loadUrl(mUrl);
                    mEditText.setText("");
                }
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHtml != null && mHtml.length() > 0) {
                    //获取标题
                    int head = mHtml.indexOf("<title>") + "<title>".length();
                    int tail = mHtml.indexOf("</title>");
                    String title = mHtml.substring(head, tail);
                    Log.d(TAG, "Save: title: " + title);
                    //获取网页上面的第一张图片
                    int head1 = mHtml.indexOf("img src=\"") + "img src=\"".length();
                    String img标签后面的html残余部分 = mHtml.substring(head1);
                    int tail1 = img标签后面的html残余部分.indexOf(".png\"");
                    String picUrl = img标签后面的html残余部分.substring(0, tail1) + ".png";
                    Log.d(TAG, "Save: picUrl: " + picUrl);
                }
            }
        });
    }

    private void getHTMLInfo(String url) {
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(url);
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.webview);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mClick = (Button) findViewById(R.id.click);
        mSave = (Button) findViewById(R.id.save);

    }

    private class MyAsyncTask extends AsyncTask<String, Integer, String> {


        private URL mUrl;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder total = new StringBuilder();
            try {
                mUrl = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();

                InputStream is = connection.getInputStream();

                String encoding = connection.getContentEncoding();

                if (encoding == null) {
                    encoding = mDefaultEncoding;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
                String line;
                while ((line = br.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return total.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mHtml = s;
            Log.d(TAG, "onPostExecute: html: " + mHtml);
        }
    }
}
