package student.example.lightsoffpuzzle;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class GameActivity extends AppCompatActivity {

    private final int ROWS = 5;
    private final int COLS = 5;
    private final int SIZE = 70;
    private int PIXEL_DENSITY;
    private ConstraintLayout layout;

    private Button[][] buttons = new Button[ROWS][COLS];
    private Button replayButton, solveButton;

    private boolean[][] lights = new boolean[ROWS][COLS];
    private boolean[][] gauss = new boolean[ROWS * COLS][ROWS * COLS + 1];

    private long startTime, endTime, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        replayButton = findViewById(R.id.button_replay);

        replayButton.setText("Shuffle");
        replayButton.setTextSize(20);
        replayButton.setTextColor(Color.WHITE);

        PIXEL_DENSITY = (int) getResources().getDisplayMetrics().density;

        solveButton = (Button) findViewById(R.id.solve_button);

        //startTime = System.currentTimeMillis();


        layout = findViewById(R.id.layout);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                buttons[row][col] = new Button(this);
                buttons[row][col].setTranslationX(col * (SIZE + 5) * PIXEL_DENSITY + 15);
                buttons[row][col].setTranslationY(row * (SIZE + 5) * PIXEL_DENSITY + 100);
                buttons[row][col].setLayoutParams(new ConstraintLayout.LayoutParams(SIZE * PIXEL_DENSITY, SIZE * PIXEL_DENSITY));
                layout.addView(buttons[row][col]);
            }
        }

        setValues();

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //enabling all buttons
                for (int r = 0; r < buttons.length; r++) {
                    for (int c = 0; c < buttons[0].length; c++) {
                        buttons[r][c].setEnabled(true);
                    }
                }

                setValues();

                Toasty.success(GameActivity.this, "Successfully reshuffled!!", Toast.LENGTH_SHORT, true).show();

                findSolution();

                solveButton.setEnabled(true);
            }
        });

        solveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solve();

                solveButton.setEnabled(false);

                for (int row = 0; row < buttons.length; row++) {
                    for (int col = 0; col < buttons[0].length; col++) {
                        buttons[row][col].setEnabled(false);
                    }
                }
            }
        });


        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[0].length; col++) {
                final int r = row, c = col;
                buttons[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeLights(r, c);

                        win();
                    }
                });
            }
        }

        findSolution();
    }

    public void setValues() {
        //true-false 2d array
        for (int i = 0; i < lights.length; i++) {
            for (int j = 0; j < lights[0].length; j++) {
                if (Math.random() * 1 <= .5) {
                    changeLights(i, j);
                }
            }
        }

        //setting the lights
        setColors();
    }

    public void changeLights(int row, int col) {
        toggleButton(row, col);

        if (isInBounds(row - 1, col)) {
            toggleButton(row - 1, col);
        }

        if (isInBounds(row + 1, col)) {
            toggleButton(row + 1, col);
        }

        if (isInBounds(row, col - 1)) {
            toggleButton(row, col - 1);
        }

        if (isInBounds(row, col + 1)) {
            toggleButton(row, col + 1);
        }

        setColors();
    }

    public void toggleButton(int row, int col) {
        lights[row][col] = !lights[row][col];
    }


    public boolean isInBounds(int row, int col) {
        return (row >= 0 && row < buttons.length) && (col >= 0 && col < buttons[0].length);
    }

    public void setColors() {
        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons[0].length; c++) {
                if (lights[r][c]) {
                    buttons[r][c].setBackgroundColor(Color.YELLOW);
                } else {
                    buttons[r][c].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        }
    }

    public boolean win() {
        for (int row = 0; row < lights.length; row++) {
            for (int col = 0; col < lights[0].length; col++) {
                if (lights[row][col]) {
                    return false;
                }
            }
        }

        for (int r = 0; r < buttons.length; r++) {
            for (int c = 0; c < buttons[0].length; c++) {
                buttons[r][c].setEnabled(false);
            }
        }

        Toasty.success(this, "You win!", Toast.LENGTH_SHORT, true).show();

        return true;
    }

    public boolean setUpSolver() {
        for (int row = 0; row < lights.length; row++) {
            for (int col = 0; col < lights[0].length; col++) {

                gauss[row * COLS + col] = generateRow(row, col);
            }
        }

        return true;
    }


    public boolean[] generateRow(int row, int col) {
        boolean[] result = new boolean[ROWS * COLS + 1];

        //checks button to the right
        if (isInBounds(row, col + 1)) {
            result[row * COLS + col + 1] = true;
        }

        //checks to the left
        if (isInBounds(row, col - 1)) {
            result[row * COLS + col - 1] = true;
        }

        //checks above
        if (isInBounds(row + 1, col)) {
            result[(row + 1) * COLS + col] = true;
        }

        //checks below
        if (isInBounds(row - 1, col)) {
            result[(row - 1) * COLS + col] = true;
        }

        result[row * COLS + col] = true;
        result[ROWS * COLS] = lights[row][col];

        return result;
    }

    public boolean[] compareRows(boolean[] a, boolean[] b) {
        boolean[] result = new boolean[a.length];

        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] ^ b[i];
        }

        return result;
    }

    public int findRowWithOne(int colNum) {
        for (int row = 0; row < gauss.length; row++) {
            if (gauss[row][colNum]) {
                if (checkIfOnlyOne(row, colNum)) {
                    return row;
                }
            }
        }
        return -1;
    }

    public boolean checkIfOnlyOne(int rowNum, int colNum) {
        while (colNum > 0) {
            colNum--;

            if (gauss[rowNum][colNum]) {
                return false;
            }
        }

        return true;
    }

    public void solveColumn(int colNum) {
        int row = findRowWithOne(colNum);


        if(row != -1) {
            for (int r = 0; r < gauss.length; r++) {
                if (r != row) {
                    if (gauss[r][colNum]) {
                        gauss[r] = compareRows(gauss[row], gauss[r]);
                    }
                }
            }
        }
    }

    public void findSolution() {
        setUpSolver();

        for (int c = 0; c < gauss[0].length - 1; c++) {
            solveColumn(c);

            for (int r = 0; r < gauss.length; r++) {
                for (int i = 0; i < gauss[0].length; i++) {
                    if (gauss[r][i]) {
                        System.out.print(1);
                    } else {
                        System.out.print(0);
                    }
                }

                System.out.println();
            }
            System.out.println(c);
        }

        for (int r = 0; r < gauss.length; r++) {
            for (int i = 0; i < gauss[0].length; i++) {
                if (gauss[r][i]) {
                    System.out.print(1);
                } else {
                    System.out.print(0);
                }
            }

            System.out.println();
        }
    }

    public void solve() {
        findSolution();

        int i = 0;

        for(int r = 0; r < gauss.length; r++) {
            for(int c = 0; c < gauss[0].length - 1; c++) {
                if(gauss[r][c]) {
                    if(gauss[r][gauss[0].length - 1]) {

                        final int col = c;

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                changeLights(col / COLS, col % COLS);
                            }
                        }, 500 * i);
                        i++;
                    }
                    break;
                }
            }
        }

        Toasty.error(GameActivity.this, "You lose!", Toast.LENGTH_SHORT, true).show();
    }

}