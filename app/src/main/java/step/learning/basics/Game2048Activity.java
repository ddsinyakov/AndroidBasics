package step.learning.basics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Game2048Activity extends AppCompatActivity {

    private int[][] cells = new int[4][4];
    private TextView[][] tvCells = new TextView[4][4];
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        findViewById( R.id.layout_2048 )
            .setOnTouchListener( new OnSwipeListener( Game2048Activity.this ) {
                @Override
                public void OnSwipeRight() {
                    Toast.makeText(Game2048Activity.this, "Right", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void OnSwipeLeft() {
                    Toast.makeText(Game2048Activity.this, "Left", Toast.LENGTH_SHORT).show();
                    moveLeft();
                }
                @Override
                public void OnSwipeTop() {
                    Toast.makeText(Game2048Activity.this, "Top", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void OnSwipeBottom() {
                    Toast.makeText(Game2048Activity.this, "Bottom", Toast.LENGTH_SHORT).show();
                }
            } ) ;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tvCells[i][j] = findViewById(
                    getResources().getIdentifier(
                        "cell" + i + j,
                        "id",
                        getPackageName()
                    )
                );
            }
        }
    }

    private void showField() {
        Resources resources = getResources();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tvCells[i][j].setText(String.valueOf(cells[i][j]));

                tvCells[i][j].setTextAppearance(
                    getResources().getIdentifier(
                        "Cell_" + cells[i][j],
                        "style",
                        getPackageName()
                    )
                );
                
                tvCells[i][j].setBackgroundColor(
                    resources.getColor(
                        resources.getIdentifier(
                            "game_bg_" + cells[i][j],
                            "color",
                            getPackageName()
                        )
                    )
                );
            }
        }
    }

    private boolean moveLeft() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                cells[i][j] = random.nextInt(5);
                if (cells[i][j] != 0) {
                    cells[i][j] = (int) Math.pow(2, cells[i][j]);
                }
            }
        }
        showField();
        return true;
    }
}