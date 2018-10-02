package com.example.adiama.quranskripsi;

import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class Help extends Fragment{
private TextView mtextView;

    public static Help newInstance() {
        Help help = new Help();
        return help;
    }

@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_help, container, false);
        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        String helpText = getString(R.string.help_text);
        Spanned result = Html.fromHtml(helpText);

        WebView webView= getView().findViewById(R.id.webViewHelp);
        webView.loadDataWithBaseURL(null, helpText, "text/html", "utf-8",null);
    }
}
