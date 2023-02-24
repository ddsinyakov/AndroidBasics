package step.learning.basics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RatesActivity extends AppCompatActivity {

    private LinearLayout ratesContainer;
    private String content;
    private final List<Rate> rates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        ratesContainer = findViewById(R.id.ratesContainer);

        new Thread(this::loadUrl)
                .start();
    }

    private void loadUrl() {
        String endpoint = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

        try(InputStream inputStream = new URL(endpoint).openStream()) {
            int sym;
            StringBuilder sb = new StringBuilder();

            while((sym = inputStream.read()) != -1) {
                sb.append((char) sym);
            }

            content = new String(
                    sb.toString().getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8);

            new Thread(this::parseContent).start();

        } catch (MalformedURLException ex) {
            Log.d("loadUrl", "MalformedURLException: " + ex.getMessage());
        } catch (IOException ex) {
            Log.d("loadUrl", "IOException: " + ex.getMessage());
        }
    }

    private void parseContent() {
        try {
            JSONArray jRates = new JSONArray(content);

            for (int i = 0; i < jRates.length(); ++i){
                rates.add(new Rate(jRates.getJSONObject(i)));
            }

            runOnUiThread(this::showRates);
        }
        catch (JSONException ex) {
            Log.d("parseContent()", ex.getMessage());
        }
    }

    @SuppressLint("SetTextI18n")
    private void showRates() {
//        StringBuilder str = new StringBuilder();
//
//        if (rates.size() != 0) {
//            str
//                .append("Exchange date: ")
//                .append(rates.get(0).getExchangeDate())
//                .append("/n");
//
//            for (int i = 0; i < rates.size(); i++) {
//                str
//                    .append(rates.get(i).toString())
//                    .append("\n");
//            }
//
//            runOnUiThread(() -> tvJson.setText(str.toString()));
//        }

        Drawable ratesBgEven = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.rates_bg_even);

        Drawable ratesBgOdd = AppCompatResources.getDrawable(
                getApplicationContext(), R.drawable.rates_bg_odd );

        LinearLayout.LayoutParams evenLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT );
        evenLayoutParams.setMargins(7,5,7,5);

        LinearLayout.LayoutParams oddLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT );
        oddLayoutParams.setMargins(7,5,7,5);
        oddLayoutParams.gravity = Gravity.END;

        boolean isOdd = true;
        for (Rate rate : this.rates) {
            isOdd = !isOdd;
            TextView tv = new TextView(this);
            tv.setText(rate.getTxt() + "\n" + rate.getCc() + " " + rate.getRate());
            tv.setPadding(7,5,7,5);

            if (isOdd) {
                tv.setBackground(ratesBgOdd);
                tv.setLayoutParams(oddLayoutParams);
            } else {
                tv.setBackground(ratesBgEven);
                tv.setLayoutParams(evenLayoutParams);
            }

            ratesContainer.addView(tv);
        }
    }

    static class Rate {
        private int r030;
        private String txt;
        private double rate;
        private String cc;
        private String exchangeDate;

        public Rate(JSONObject obj) throws JSONException{
            setR030(obj.getInt("r030"));
            setTxt(obj.getString("txt"));
            setRate(obj.getDouble("rate"));
            setCc(obj.getString("cc"));
            setExchangeDate(obj.getString("exchangedate"));
        }

        public int getR030() { return r030; }
        public void setR030(int r030) { this.r030 = r030; }
        public String getTxt() { return txt; }
        public void setTxt(String txt) { this.txt = txt; }
        public double getRate() { return rate; }
        public void setRate(double rate) { this.rate = rate; }
        public String getCc() { return cc; }
        public void setCc(String cc) { this.cc = cc; }
        public String getExchangeDate() { return exchangeDate; }
        public void setExchangeDate(String exchangeDate) { this.exchangeDate = exchangeDate; }

        @NonNull
        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "%s, %f", getTxt(), getRate());
        }
    }
}