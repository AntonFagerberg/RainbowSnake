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
    List<List<Float>> tailColors = new ArrayList<>();

    boolean dead, newSpawn;
    int x, y, xx, yy, xh, yh, xa, ya, tick, level, grow, scale = 40, tickLimit, maxScale = 800;
    float color1, color2, color3;
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
        tickLimit = 3;
        x = 0;
        y = 0;
        xx = 1;
        yy = 0;
        grow = 10;
        xh = xx;
        yh = yy;
        level = 0;
        tail.clear();
        tailColors.clear();

        color1 = random.nextFloat();
        color2 = random.nextFloat();
        color3 = random.nextFloat();

        List<Float> tailColorPiece = new ArrayList<>(3);
        tailColorPiece.add(color1);
        tailColorPiece.add(color2);
        tailColorPiece.add(color3);
        tailColors.add(tailColorPiece);

        color1 = random.nextFloat();
        color2 = random.nextFloat();
        color3 = random.nextFloat();

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
            dead = true;
        } else {
            List<Integer> coordinate = freeCoordinates.get(random.nextInt(freeCoordinates.size()));
            xa = coordinate.get(0);
            ya = coordinate.get(1);
        }
    }

    @Override
    public void render() {

        if (dead) {
            ScreenUtils.clear(Color.BLACK);
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
                reload();
            }
        } else {
            ScreenUtils.clear(0.7921569f, 0.8627451f, 0.62352943f, 0.0f);
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

            if (++tick > tickLimit) {
                newSpawn = false;
                tick = 0;


                int xp = x, yp = y;

                x += xx;
                y += yy;


                if (x < 0 || y < 0 || x >= maxScale / scale || y >= maxScale / scale) {
                    dead = true;
                    x = xp;
                    y = yp;
                } else {
                    xh = xx;
                    yh = yy;

                    for (List<Integer> coordinate : tail) {
                        if (x == coordinate.get(0) && y == coordinate.get(1)) {
                            while (!tail.isEmpty()) {
                                List<Integer> lastCoordinate = tail.removeLast();
                                if (x == lastCoordinate.get(0) && y == lastCoordinate.get(1)) {
                                    break;
                                }
                            }

                            break;
                        }
                    }

                    List<Integer> tailPiece = new ArrayList<>(2);
                    tailPiece.add(xp);
                    tailPiece.add(yp);
                    tail.addFirst(tailPiece);
                    List<Float> tailColorPiece = new ArrayList<>(3);
                    tailColorPiece.add(color1);
                    tailColorPiece.add(color2);
                    tailColorPiece.add(color3);
                    tailColors.add(tailColorPiece);
                    color1 = random.nextFloat();
                    color2 = random.nextFloat();
                    color3 = random.nextFloat();

                    grow = grow == 0 ? 0 : grow - 1;

                    if (x == xa && y == ya) {
                        grow = 10;
                        spawnDot();
                    } else if (grow == 0 && !tail.isEmpty()) {
                        tail.removeLast();
                    }
                }
            }
        }
//
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.54509807f, 0.6745098f, 0.05490196f, 0f);
        shapeRenderer.box(xa * scale, ya * scale, 0, scale, scale, 0);
        shapeRenderer.setColor(color1, color2, color3, 0f);
        shapeRenderer.box(xa * scale + 1, ya * scale + 1, 0, scale - 2, scale - 2, 0);

        int colorIndex = tailColors.size() - 1;
        shapeRenderer.setColor(0.54509807f, 0.6745098f, 0.05490196f, 0f);
        shapeRenderer.box(x * scale, y * scale, 0, scale, scale, 0);
        shapeRenderer.setColor(tailColors.get(colorIndex).get(0), tailColors.get(colorIndex).get(1), tailColors.get(colorIndex).get(2), 0.0f);
        shapeRenderer.box(x * scale + 1, y * scale + 1, 0, scale - 2, scale - 2, 0);
        colorIndex--;

        for (List<Integer> coordinate : tail) {
            shapeRenderer.setColor(0.54509807f, 0.6745098f, 0.05490196f, 0f);
            shapeRenderer.box(coordinate.get(0) * scale, coordinate.get(1) * scale, 0, scale, scale, 0);

            shapeRenderer.setColor(tailColors.get(colorIndex).get(0), tailColors.get(colorIndex).get(1), tailColors.get(colorIndex).get(2), 0.0f);
            shapeRenderer.box(coordinate.get(0) * scale + 1, coordinate.get(1) * scale + 1, 0, scale - 2, scale - 2, 0);
            colorIndex--;
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
