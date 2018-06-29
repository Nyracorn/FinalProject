package student.example.lightsoffpuzzle;

import java.util.ArrayList;

public class Game {
    String name;
    String desc;
    int photoID;

    public Game(String name, String desc, int photoID) {
        this.name = name;
        this.desc = desc;
        this.photoID = photoID;
    }

    private ArrayList<Game> games;

    private void initializeData() {
        games = new ArrayList<>();

        games.add(new Game("Tile Puzzle", "Move all the numbers into the following pattern by clicking a number next to the blank space: \"blank, 1, 2" +
                "3, 4, 5" +
                "6, 7, 8", R.drawable.gmail));
    }

}