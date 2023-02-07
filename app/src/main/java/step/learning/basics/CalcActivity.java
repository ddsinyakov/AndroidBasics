package step.learning.basics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory;
    private TextView tvResult;
    private String minusSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        tvHistory = findViewById(R.id.tvHistory);
        tvHistory.setText("");

        tvResult = findViewById(R.id.tvResult);
        tvResult.setText("");

        minusSign = getString(R.string.btn_difference_text);

        for (int i = 0; i < 10; i++) {
            findViewById(
                getResources()
                    .getIdentifier(
                        "btn" + i,
                        "id",
                        getPackageName())
            ).setOnClickListener(this::digitClick);
        }

        findViewById(R.id.btnSign).setOnClickListener(this::pmClick);
        findViewById(R.id.btnInverse).setOnClickListener(this::inverseClick);
        findViewById(R.id.btnComma).setOnClickListener(this::commaClick);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("history", tvHistory.getText());
        outState.putCharSequence("result", tvResult.getText());
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        tvHistory.setText(savedInstanceState.getCharSequence("history"));
        tvResult.setText(savedInstanceState.getCharSequence("history"));
    }

    private void digitClick(View v) {
        String result = tvResult.getText().toString();
        if (result.replace(".", "").length() >= 10) return;

        String digit = ((Button) v).getText().toString();
        tvResult.setText(result.equals("0")
            ? digit
            : result + digit);
    }

    private void commaClick(View v) {
        String result = tvResult.getText().toString();

        if (!result.contains(("."))) {
            result += ".";
        }
    }

    private void pmClick(View v) {
        String result = tvResult.getText().toString();

        if (result.equals(0)) {
            return;
        }

        if (result.startsWith(minusSign)) {
            result = result.substring(1);
        }
        else {
            result = minusSign + result;
        }

        tvResult.setText(result);
    }

    private void inverseClick(View v) {
        String result = tvResult.getText().toString().replace(minusSign, "-");
        double arg = Double.parseDouble(result);

        if (arg == 0) {
            Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_LONG).show();
            return;
        }

        tvHistory.setText(String.format("1(%s) =", result));
        arg = 1 / arg;
        result = String.format(Locale.getDefault(), "%f", arg);

        while (result.endsWith("0")) {
            result = result.substring(0, result.length() - 1);
        }

        tvResult.setText(result.replace("-", minusSign));
    }
}