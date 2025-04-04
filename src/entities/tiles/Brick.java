package entities.tiles;

import core.Game;
import static core.Game.entities;
import static core.Game.table;
import entities.Entity;
import graphics.Sprite;
import javafx.application.Platform;
import javafx.scene.image.Image;

public class Brick extends Entity {
    private boolean exploded = false;

    public Brick(int x, int y, Image img) {
        super(x, y, img);
    }

    private int animate = 0;

    public void brickExploded() {
        Sprite sprite = Sprite.movingSprite(Sprite.brick_exploded, Sprite.brick_exploded1, Sprite.brick_exploded2, animate, 20);
        img = sprite.getFxImage;
        animate++;
        if (animate == 10) {
            Platform.runLater(() -> {
                entities.remove(this);
                x /= Sprite.SCALED_SIZE;
                y /= Sprite.SCALED_SIZE;
                Game.table[x][y] = null;
                Entity hiddenItem = Game.hiddenTable[x][y];
                if (hiddenItem != null) {
                    Game.table[x][y] = hiddenItem;
                    entities.add(hiddenItem);
                }
            });
        }
    }

    public void setExploded() {
        exploded = true;
    }

    @Override
    public void update() {
        table[x / Sprite.SCALED_SIZE][y / Sprite.SCALED_SIZE] = this;
        if (exploded) {
            brickExploded();
        }
    }
}