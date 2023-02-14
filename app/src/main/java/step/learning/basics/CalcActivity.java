package step.learning.basics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class CalcActivity extends AppCompatActivity {

    private TextView tvHistory;
    private TextView tvResult;
    private String minusSign;
    private double history;
    private String operation = "";
    private Boolean needClear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        tvHistory = findViewById(R.id.tvHistory);
        tvHistory.setText("");

        tvResult = findViewById(R.id.tvResult);
        tvResult.setText("");

        history = 0;

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
        findViewById(R.id.btnSqrt).setOnClickListener(this::sqrtClick);
        findViewById(R.id.btnSquare).setOnClickListener(this::squareClick);
        findViewById(R.id.btnClearAll).setOnClickListener(this::clearAllClick);
        findViewById(R.id.btnClearE).setOnClickListener(this::clearClick);
        findViewById(R.id.btnBackspace).setOnClickListener(this::backSpaceClick);
        findViewById(R.id.btnSum).setOnClickListener(this::operationClick);
        findViewById(R.id.btnDifference).setOnClickListener(this::operationClick);
        findViewById(R.id.btnMultiply).setOnClickListener(this::operationClick);
        findViewById(R.id.btnDivide).setOnClickListener(this::operationClick);
        findViewById(R.id.btnResult).setOnClickListener(this::resultClick);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("history", tvHistory.getText());
        outState.putCharSequence("result", tvResult.getText());
        outState.putCharSequence("history", Double.toString(history));
        outState.putCharSequence("operation", operation);
        outState.putCharSequence("needClear", Boolean.toString(needClear));
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        tvHistory.setText(savedInstanceState.getCharSequence("history"));
        tvResult.setText(savedInstanceState.getCharSequence("history"));
        history = Double.parseDouble(savedInstanceState.getCharSequence("history").toString());
        operation = savedInstanceState.getCharSequence("operation").toString();
        needClear = Boolean.parseBoolean(savedInstanceState.getCharSequence("needClear").toString());
    }

    private void digitClick(View v) {
        String digit = ((Button) v).getText().toString();

        if (needClear) {
            needClear = false;
            tvResult.setText(digit);
            return;
        }

        String result = tvResult.getText().toString();
        if (result.replace(".", "").length() >= 10) return;

        tvResult.setText(result.equals("0")
            ? digit
            : result + digit);
    }

    private void commaClick(View v) {
        String result = tvResult.getText().toString();

        if (!result.contains(("."))) {
            result += ".";
        }

        tvResult.setText(result);
    }

    private void pmClick(View v) {
        String result = tvResult.getText().toString();

        if (result.equals("0")) {
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
        String result = tvResult.getText().toString();
        double arg = getArgument(result);

        if (arg == 0) {
            alert(R.string.calc_divide_zero_msg);
            return;
        }

        arg = 1 / arg;
        history = arg;

        setArgument(arg);

        tvHistory.setText(String.format("1/(%s) =", result));

        needClear = true;
    }

    private double getArgument( String resultText ) {
        return Double.parseDouble(
                resultText.replace( minusSign, "-" ) ) ;
    }

    private void setArgument( double arg ) {
        String result = String.format( Locale.getDefault(), "%.10f", arg ) ;

        while( result.endsWith( "0" ) ) {
            result = result.substring( 0, result.length() - 1 ) ;
        }

        if (result.endsWith( "." )) {
            result = result.substring( 0, result.length() - 1 ) ;
        }

        tvResult.setText( result.replace( "-", minusSign ) ) ;
    }

    private void sqrtClick(View v) {
        String result = tvResult.getText().toString();
        double arg = getArgument(result);

        arg = Math.sqrt(arg);

        if (arg == 0) {
            alert(R.string.calc_divide_zero_msg);
            return;
        }

        tvHistory.setText(String.format("sqrt(%s)", result));
        history = arg;

        setArgument(arg);

        needClear = true;
    }

    private void squareClick(View v) {
        String result = tvResult.getText().toString();
        double arg = getArgument(result);

        arg = Math.pow(arg, 2);

        tvHistory.setText(String.format("%s^2", result));
        history = arg;

        setArgument(arg);

        needClear = true;
    }

    private void alert(int messageId) {
        Toast.makeText(CalcActivity.this, messageId, Toast.LENGTH_LONG).show();
        Vibrator vibrator;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vibratorManager = (VibratorManager)
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE);

            vibrator = vibratorManager.getDefaultVibrator();
        }
        else {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else {
            vibrator.vibrate(300);
        }
    }

    private void clearClick(View v) {
        tvResult.setText("0");
    }

    private void clearAllClick(View v) {
        tvResult.setText("0");
        tvHistory.setText("0");
        history = 0;
        operation = "";
        needClear = false;
    }

    private void backSpaceClick(View v) {
        String result = tvResult.getText().toString();

        result = result.length() <= 1
                ? "0"
                : result.substring(0, result.length() - 1);

        if (result.equals(minusSign)) {
            result = "0";
        }

        tvResult.setText(result);
    }

    private void operationClick(View v) {
        if(!operation.equals("")){
            resultClick(v);
        }

        operation = ((Button) v).getText().toString();
        String result = tvResult.getText().toString();
        history = Double.parseDouble(result);
        tvHistory.setText(String.format("%s %s", result, operation));
        needClear = true;
    }

    private  void resultClick(View v) {
        if (operation.equals("")) return;


        String result = tvResult.getText().toString();
        double arg = getArgument(result);

        tvHistory.setText(String.format("%s %s = ", tvHistory.getText(), result));

        if (operation.equals(getString(R.string.btn_sum_text))) {
            setArgument(history + arg);
        }

        if (operation.equals(getString(R.string.btn_difference_text))) {
            setArgument(history - arg);
        }

        if (operation.equals(getString(R.string.btn_multiply_text))) {
            setArgument(history * arg);
        }

        if (operation.equals(getString(R.string.btn_divide_text))) {
            if (arg == 0) {
                alert(R.string.calc_divide_zero_msg);
                tvResult.setText("0");
                tvHistory.setText("");
                operation = null;
                return;
            }
            setArgument(history / arg);
        }

        needClear = true;
        operation = "";
    }
}
