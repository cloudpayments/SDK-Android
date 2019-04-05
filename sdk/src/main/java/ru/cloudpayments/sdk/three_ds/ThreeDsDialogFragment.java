package ru.cloudpayments.sdk.three_ds;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.fragment.app.DialogFragment;
import ru.cloudpayments.sdk.R;

public class ThreeDsDialogFragment extends DialogFragment {

    private static final  String  POST_BACK_URL = "https://demo.cloudpayments.ru/WebFormPost/GetWebViewData";

    private static final String ACS_URL = "acs_url";
    private static final String MD = "md";
    private static final String PA_REQ = "pa_req";
    private static final String TERM_URL = "term_url";

    private String acsUrl;
    private String md;
    private String paReq;
    private String termUrl;

    private ThreeDSDialogListener listener;

    private WebView webViewThreeDs;

    public static ThreeDsDialogFragment newInstance(String acsUrl, String md, String paReq) {
        ThreeDsDialogFragment dialogFragment = new ThreeDsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ACS_URL, acsUrl);
        args.putString(MD, md);
        args.putString(PA_REQ, paReq);
        args.putString(TERM_URL, POST_BACK_URL);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acsUrl = getArguments().getString(ACS_URL);
        md = getArguments().getString(MD);
        paReq = getArguments().getString(PA_REQ);
        termUrl = getArguments().getString(TERM_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_three_ds, container, false);
        webViewThreeDs = view.findViewById(R.id.web_view_three_ds);
        webViewThreeDs.setWebViewClient(new ThreeDsWebViewClient());
        webViewThreeDs.getSettings().setDomStorageEnabled(true);
        webViewThreeDs.getSettings().setJavaScriptEnabled(true);
        webViewThreeDs.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewThreeDs.addJavascriptInterface(new ThreeDsJavaScriptInterface(), "JavaScriptThreeDs");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            String params = new StringBuilder()
                    .append("PaReq=").append(URLEncoder.encode(paReq, "UTF-8"))
                    .append("&MD=").append(URLEncoder.encode(md, "UTF-8"))
                    .append("&TermUrl=").append(URLEncoder.encode(termUrl, "UTF-8"))
                    .toString();
            webViewThreeDs.postUrl(acsUrl, params.getBytes());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private class ThreeDsWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {

            if (url.toLowerCase().equals(POST_BACK_URL.toLowerCase())) {
                view.setVisibility(View.GONE);
                view.loadUrl("javascript:window.JavaScriptThreeDs.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
        }
    }

    class ThreeDsJavaScriptInterface {

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void processHTML(final String html) {

            Document doc = Jsoup.parse(html);
            Element element = doc.select("body").first();

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(element.ownText()).getAsJsonObject();
            final String paRes = jsonObject.get("PaRes").getAsString();

            if (paRes != null && !paRes.isEmpty()) {

                if (listener != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onAuthorizationCompleted(md, paRes);
                        }
                    });
                }
            } else {
                if (listener != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onAuthorizationFailed(html);
                        }
                    });
                }
            }
            dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ThreeDSDialogListener && listener == null)
            listener = (ThreeDSDialogListener) context;
    }

    public void setListener(ThreeDSDialogListener listener) {
        this.listener = listener;
    }

}
