package entities;

import java.util.Random;

import core.Game;
import entities.bombs.Bomb;
import entities.tiles.Brick;
import entities.tiles.Wall;
import graphics.Sprite;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity {
    //Tọa độ X tính từ góc trái trên trong Canvas
    protected int x;

    //Tọa độ Y tính từ góc trái trên trong Canvas
    protected int y;

    protected Sprite sprite;

    protected int animate = 0;

    protected int die_time = 0;
    protected int hurt_time = 0;
    protected int life = 1;

    protected boolean died = false;
    protected boolean hurt = false;

    protected Image img;
    protected Direction direction = Direction.R;

    //Khởi tạo đối tượng, chuyển từ tọa độ đơn vị sang tọa độ trong canvas
    public Entity(int xUnit, int yUnit, Image img) {
        this.x = xUnit * Sprite.SCALED_SIZE;
        this.y = yUnit * Sprite.SCALED_SIZE;
        this.img = img;
    }

    public static Entity getEntity(int x, int y) {
        return Game.table[x][y];
    }

    public static boolean checkWall(int x, int y) {
        if (x < 0 || y < 0 || x > Sprite.SCALED_SIZE * Game.WIDTH || y > Sprite.SCALED_SIZE * Game.HEIGHT) {
            return false;
        }

        x /= Sprite.SCALED_SIZE;
        y /= Sprite.SCALED_SIZE;
        Entity cur = getEntity(x, y);
        return !(cur instanceof Wall) && !(cur instanceof Brick) && !(cur instanceof Bomb);
    }

    public static boolean checkBrick(int x, int y) {
        if (x < 0 || y < 0 || x > Sprite.SCALED_SIZE * Game.WIDTH || y > Sprite.SCALED_SIZE * Game.HEIGHT) {
            return false;
        }

        x /= Sprite.SCALED_SIZE;
        y /= Sprite.SCALED_SIZE;
        Entity cur = getEntity(x, y);
        return !(cur instanceof Wall) && !(cur instanceof Bomb);
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(img, x, y);
    }

    public abstract void update();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTileX() {
        return (x + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE;
    }

    public int getTileY() {
        return (y + Sprite.SCALED_SIZE / 2) / Sprite.SCALED_SIZE;
    }

    public void setDied() {
        this.died = true;
    }

    public Sprite getSprite() {
        return this.sprite;
    }
//    public void dieEnemy(Sprite sprite) {
//        die_time++;
//        img = sprite.getFxImage;
//        if (die_time == 20) {
//            Platform.runLater(() -> {
////                cnt_enemy--;
//                enemies.remove(this);
//                Game.table[(x + Sprite.SCALED_SIZE/2)/Sprite.SCALED_SIZE][(y + Sprite.SCALED_SIZE/2)/Sprite.SCALED_SIZE] = old_cur;
//            });
//        }
//    }

    public int getLife() {
        return life;
    }

    public void reduceLife() {
        life--;
    }

    public void setHurt() {
        if (!hurt) {
            reduceLife();
        }
        hurt = true;
    }


    public enum Direction {
        U, D, L, R, OH, OV;

        public static Direction randomDirection() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
