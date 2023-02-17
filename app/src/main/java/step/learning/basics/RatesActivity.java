package step.learning.basics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class RatesActivity extends AppCompatActivity {

    private TextView tvJson;
    private String content;
    private final List<Rate> rates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        tvJson = findViewById(R.id.tvJson);

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
        StringBuilder str = new StringBuilder();

        try {
            JSONArray jRates = new JSONArray(content);

            for (int i = 0; i < jRates.length(); ++i){
                rates.add(new Rate(jRates.getJSONObject(i)));
            }

            new Thread(this::showRates).start();
        }
        catch (JSONException ex) {
            Log.d("parseContent()", ex.getMessage());
        }
    }

    private void showRates() {
        StringBuilder str = new StringBuilder();

        if (rates.size() != 0) {
            str
                .append("Exchange date: ")
                .append(rates.get(0).getExchangeDate())
                .append("/n");

            for (int i = 0; i < rates.size(); i++) {
                str
                    .append(rates.get(i).toString())
                    .append("\n");
            }

            runOnUiThread(() -> tvJson.setText(str.toString()));
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