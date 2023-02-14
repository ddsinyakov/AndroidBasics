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

        findViewById(R.id.exitButton)
            .setOnClickListener(this::exitButtonClick);
    }

    private void startCalcButtonClick(View v) {
        Intent calcIntent = new Intent(this, CalcActivity.class);
        startActivity(calcIntent);
    }

    private void startGame2048ButtonClick(View v) {
        Intent calcIntent = new Intent(this, Game2048Activity.class);
        startActivity(calcIntent);
    }

    private void exitButtonClick(View v) {
        finish();
    }
}