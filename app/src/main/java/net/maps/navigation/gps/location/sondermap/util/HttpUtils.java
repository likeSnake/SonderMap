package net.maps.navigation.gps.location.sondermap.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import okhttp3.RequestBody;


public class HttpUtils {

    private static String  DEFAULT_DATA = "2554c49559a1d34e6a85ce92d37aaea91fe80c592f9edefba3c7b74e5fc11a0597b207964a8ddbc0aee86d1a26d472bf22fd51ad2e04c58eed500f99d77c597a0ad0fc29a84c93c806500b17b1dd9e30825432d4a2781d86f5dd19d250d55486f67462fc93a49824d6d515e937edce2b4c1edeb0fa07a21b78deaa9dcbf18ca31ffc0e7157bc7da56929eb81dca2813029f2d6b23cf13fb5ee8b31ce0c11a8d047c15e9a4f5e546406284b728bb169cf0a83120256b90620b08f6fe689eff0b113ce343ca0c542e5d8404658f4b8fd7b19fffcb022c469d6daf9b67c98b4f29ad02b263312c5e0a3c4528a5a007ce4b7350a65af9cf8eba2ca9224cc1cb3bf77e07eb8d29bb75b516c9d0c1723b131316c3fecc0f6eb3da83ca5f6fd8ff1a4acc46ee2e310f4e8d7e8aaa0fca13425edddea46a35203dc1f84129490d677330f2b9e141b00d2f6cffc5bfad4cc467ff1937a0308d9b4736c65e578cc05e57c9cf9695d3e183c1d33eccd9ccb85f7ee97466ff19b02f2322a0d3662af0bb626615999dda724e8dff594d55b5ae631bbc01edb5967f56acfddfe995c122e82db914127c94bb8fcde7f89c7845a8cfa9b77204fee29beb4d3e1a9d28d2b903ff070b4a269631c2323b7f20d9d4cea493dde15d8f0e4a3d4d7929a53e1987720522405087674a529222eb864612b23c09e3700af3eea4fc7140e48ae3b75c14a1df893229290d2d73676b7baa0529c9bff86cbb63776d6b183ecf7b3c436ebfddf0455f02020b234f9d3388bae393467fde75ba22630fc4621bc67e6402de0622fe5c1531d01bb3ea48dbc5f651defe8ff1a144f9767f4d4e63e49ddf71a98e895ca8ecac92e9a452ecb3b78f59f82415695dac8f0787948e8dcc9aab14f2f7b31c8131f82a3a4765a76eac20e038f85c7b6eca1c0d1d639e2d7cf00fd10325e2f8938c3128199ce9a60effc8c02d8ac9baf861e89dedce31e5eeee147f3da4bb8d9c369ca3ecdac37badc6d7b391eff07c45f870d137baa3f4cef8d28646244c6d4ed65c5823293c620d6a9ab0f57c19d767153f3f4be9640f13b244bc4b43684983eee5e321e813ec0af09a72d80af85de20d3f0950a066dae803252948ed792d69bb463ab68853937e048e08d029ec9d01a7097706408206ff62d35b68d3d7e0c0bcefd1229909b3bad5f98a71848bbb476530be8562803978d9761bb820bacadb6fee6a2ecf6ce60b8f046d06f5394a382b8dbcb613326ebced6dd74f66ac3495e8397d8cd94b686312438d2e295751e386352e94ae459caa6d778a2453b30701de26c707ac0052c66681db82482587786df514b5b1446fa610f54e4732cad0d2add357b8ba0a536fb2f4e63cc033fcc8ec702654f1f627a7f835675135884b51cc2fbe9f01f3269d7a3332baaa7225db28483e87e5ec90e5b3b07e5937fc27086789dab5f9abfea022cac0b2364d1107021c670ee02ad36c9b6f04bbc1d3d196b8f1ee3846b4c69ec6ed79f12068abf41b5746e031ab4191ded04620748396b6cec984365944437ed51a1b050a9402f";

    private static final String TAG = "MyHttpUtils";


    public static void getStringRequest(String url,Context context,  Response.Listener<String> listener,  Response.ErrorListener errorListener) {
        // 创建一个 RequestQueue 对象
        RequestQueue queue = Volley.newRequestQueue(context);

        // 创建一个 StringRequest 对象来发送 GET 请求
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);

        // 将请求添加到 RequestQueue 中
        queue.add(stringRequest);
    }
    public static void postStringRequest(String url, Context context,  String requestBody,  Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        // 创建一个 RequestQueue 对象
        RequestQueue queue = Volley.newRequestQueue(context);

        // 创建一个 StringRequest 对象来发送 POST 请求
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                listener, errorListener) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            // 将参数作为字符串传递
            @Override
            public byte[] getBody() throws AuthFailureError {
                return requestBody.getBytes();
            }
        };

        // 将请求添加到 RequestQueue 中
        queue.add(stringRequest);
    }

    public interface HttpCallback<T> {
        void onSuccess(T result);
        void onFailure(T failure);
    }
}
