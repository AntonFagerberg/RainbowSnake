package com.antonfagerberg.snaketon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SnakeTon extends ApplicationAdapter {

    LinkedList<List<Integer>> tail = new LinkedList<>();

    boolean dead;
    int x, y, xx, yy, xh, yh, xa, ya, tick, tickLimit = 15;
    ShapeRenderer shapeRenderer;
    Random random;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        random = new Random();
        reload();
    }

    void reload() {
        dead = false;
        tick = 0;
        tickLimit = 15;
        x = 10;
        y = 10;
        xx = 1;
        yy = 0;
        xh = xx;
        yh = yy;
        tail.clear();
        moveApple();
    }

    void moveApple() {
        int nx = -1, ny = -1;

        boolean conflict = true;
        while (conflict) {
            ny = 10 * random.nextInt(40);
            nx = 10 * random.nextInt(40);
            conflict = false;

            if (x == nx && y == ny) {
                conflict = true;
            } else {
                for (List<Integer> coordinate : tail) {
                    if (coordinate.get(0) == nx && coordinate.get(1) == ny) {
                        conflict = true;
                        break;
                    }
                }
            }
        }

        xa = nx;
        ya = ny;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.7921569f, 0.8627451f, 0.62352943f, 0.0f);

        if (dead) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                reload();
            }
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && yh == 0 && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                yy = 1;
                xx = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && yh == 0 && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                yy = -1;
                xx = 0;
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && xh == 0 && !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                yy = 0;
                xx = -1;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && xh == 0 && !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                yy = 0;
                xx = 1;
            }

            if (++tick > tickLimit) {
                tick = 0;


                xh = xx;
                yh = yy;

                int xp = x, yp = y;

                x += 10 * xx;
                y += 10 * yy;

                if (x < 0 || y < 0 || x >= 400 || y >= 400) {
                    dead = true;
                }

                for (List<Integer> coordinate : tail) {
                    if (x == coordinate.get(0) && y == coordinate.get(1)) {
                        dead = true;
                    }
                }

                if (x == xa && y == ya) {
                    moveApple();
                    List<Integer> tailPiece = new ArrayList<>(2);
                    tailPiece.add(xp);
                    tailPiece.add(yp);
                    tail.addFirst(tailPiece);
                    if (tickLimit > 2) {
                        tickLimit--;
                    }
                } else if (!tail.isEmpty()) {
                    List<Integer> tailPiece = new ArrayList<>(2);
                    tailPiece.add(xp);
                    tailPiece.add(yp);
                    tail.addFirst(tailPiece);
                    tail.removeLast();
                }

            }
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.54509807f, 0.6745098f, 0.05490196f, 0f);
            shapeRenderer.box(xa, ya, 0, 10, 10, 0);

            shapeRenderer.setColor(0.1882353f, 0.38431373f, 0.1882353f, 0.0f);
            shapeRenderer.box(x, y, 0, 10, 10, 0);

            for (List<Integer> coordinate : tail) {
                shapeRenderer.box(coordinate.get(0), coordinate.get(1), 0, 10, 10, 0);
            }

            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
