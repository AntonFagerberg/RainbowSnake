package com.antonfagerberg.snaketon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SnakeTon extends ApplicationAdapter {

    LinkedList<List<Integer>> tail = new LinkedList<>();

    boolean dead, newSpawn;
    int[] scales = new int[]{200, 100, 50, 20, 10, 4, 2, 1};
    int x, y, xx, yy, xh, yh, xa, ya, tick, level, scale, tickLimit, maxScale = 400;
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
        x = 0;
        y = 0;
        xx = 1;
        yy = 0;
        xh = xx;
        yh = yy;
        level = 0;
        scale = scales[level];
        tail.clear();
        spawnDot();
    }

    void spawnDot() {
        List<List<Integer>> freeCoordinates = new ArrayList<>();

        for (int nx = 0; nx < maxScale / scale; nx++) {
            for (int ny = 0; ny < maxScale / scale; ny++) {
                boolean conflict = false;
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

                if (!conflict) {
                    List<Integer> coordinate = new ArrayList<>(2);
                    coordinate.add(nx);
                    coordinate.add(ny);
                    freeCoordinates.add(coordinate);
                }
            }
        }

        if (freeCoordinates.isEmpty()) {
            scale = scales[++level];
            tail.clear();
            x = 1;
            y = 0;
            xx = 1;
            yy = 0;
            for (int z = 0; z < maxScale / scale; z++) {
                List<Integer> tailPiece = new ArrayList<>(2);
                tailPiece.add(0);
                tailPiece.add(z);
                tail.addLast(tailPiece);
            }
            newSpawn = true;
            spawnDot();
        } else {
            List<Integer> coordinate = freeCoordinates.get(random.nextInt(freeCoordinates.size()));
            xa = coordinate.get(0);
            ya = coordinate.get(1);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.7921569f, 0.8627451f, 0.62352943f, 0.0f);

        if (dead) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                reload();
            }
        } else {
            if (!newSpawn) {
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    if (yh == 0) {
                        yy = 1;
                        xx = 0;
                    }
                } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    if (yh == 0) {
                        yy = -1;
                        xx = 0;
                    }
                } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    if (xh == 0) {
                        yy = 0;
                        xx = -1;
                    }
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    if (xh == 0) {
                        yy = 0;
                        xx = 1;
                    }
                }
            }

            if (++tick > tickLimit) {
                newSpawn = false;
                tick = 0;

                xh = xx;
                yh = yy;

                int xp = x, yp = y;

                x += xx;
                y += yy;

                if (x < 0 || y < 0 || x >= maxScale / scale || y >= maxScale / scale) {
                    dead = true;
                    x = xp;
                    y = yp;
                } else {
                    for (List<Integer> coordinate : tail) {
                        if (x == coordinate.get(0) && y == coordinate.get(1)) {
                            dead = true;
                            break;
                        }
                    }

                    if (x == xa && y == ya) {
                        List<Integer> tailPiece = new ArrayList<>(2);
                        tailPiece.add(xp);
                        tailPiece.add(yp);
                        tail.addFirst(tailPiece);
                        spawnDot();
                    } else if (!tail.isEmpty()) {
                        List<Integer> tailPiece = new ArrayList<>(2);
                        tailPiece.add(xp);
                        tailPiece.add(yp);
                        tail.addFirst(tailPiece);
                        tail.removeLast();
                    }
                }
            }
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.54509807f, 0.6745098f, 0.05490196f, 0f);
        shapeRenderer.box(xa * scale, ya * scale, 0, scale, scale, 0);

        shapeRenderer.setColor(0.1882353f, 0.38431373f, 0.1882353f, 0.0f);
        shapeRenderer.box(x * scale, y * scale, 0, scale, scale, 0);

        for (List<Integer> coordinate : tail) {
            shapeRenderer.box(coordinate.get(0) * scale, coordinate.get(1) * scale, 0, scale, scale, 0);
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
