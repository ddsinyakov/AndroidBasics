package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.calcButton)
            .setOnClickListener(this::startCalcButtonClick);

        findViewById(R.id.game2048Button)
            .setOnClickListener(this::startGame2048ButtonClick);

        findViewById(R.id.ratesButton)
                .setOnClickListener(this::startRatesButtonClick);

        findViewById(R.id.chatButton)
                .setOnClickListener(this::chatButtonClick);

        findViewById(R.id.exitButton)
            .setOnClickListener(this::exitButtonClick);
    }

    private void startCalcButtonClick(View v) {
        Intent intent = new Intent(this, CalcActivity.class);
        startActivity(intent);
    }

    private void startGame2048ButtonClick(View v) {
        Intent intent = new Intent(this, Game2048Activity.class);
        startActivity(intent);
    }

    private void startRatesButtonClick(View v) {
        Intent intent = new Intent(this, RatesActivity.class);
        startActivity(intent);
    }

    private void chatButtonClick(View v) {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    private void exitButtonClick(View v) {
        finish();
    }
}