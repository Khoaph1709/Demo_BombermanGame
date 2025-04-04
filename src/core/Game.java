package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import audio.Sound;
import entities.Entity;
import entities.bombs.Bomb;
import entities.character.Balloom;
import entities.character.Doll;
import entities.character.Kondoria;
import entities.character.Oneal;
import entities.items.BombItem;
import entities.items.FlameItem;
import entities.items.FlamePassItem;
import entities.items.PortalItem;
import entities.items.SpeedItem;
import entities.items.WallPassItem;
import entities.player.Bomber;
import entities.tiles.Brick;
import entities.tiles.Grass;
import entities.tiles.Wall;
import graphics.Sprite;
import input.KeyListener;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Game extends Application {
    private static final int INIT_LEVEL = 1;
    private static final int MAX_LEVEL = 2;
    public static long FPS_GAME = 1000 / 60;
    public static int WIDTH;
    public static int HEIGHT;
    public static Bomber bomber = new Bomber(1, 1, null, null);
    public static int level = INIT_LEVEL;
    //    public static int cnt_enemy = 0;
//    public static int cnt_enemy = 0;
    public static Entity[][] table;
    public static Entity[][] hiddenTable;
    public static STATE gameState = STATE.MENU;
    public static List<Entity> entities = new ArrayList<>();
    public static List<Entity> enemies = new ArrayList<>();
    public static Sound bgMusic;
    private final Effect shadow = new DropShadow();
    public List<Entity> flames = new ArrayList<>();
    public static List<Entity> stillObjects = new ArrayList<>();
    public boolean isEnd = false;
    Group root = null;
    private GraphicsContext gc;
    private Canvas canvas;
    private Text textLife = null;
    Text textLevel = null;
    private Text textScore = null;
    private int MAXSCORE = 0;
    private boolean new_game = true;

    public static void main(String[] args) {
        Application.launch(Game.class);
    }

    public void setup(Stage stage, int level) {
        if (bgMusic != null) bgMusic.stop();
        Bomb.cnt = 0;
        entities = new ArrayList<>();
        enemies = new ArrayList<>();
        flames = new ArrayList<>();
        stillObjects = new ArrayList<>();
//        bgMusic = Sound.main_bgm;
//        bgMusic.loop();
        File file = new File(System.getProperty("user.dir") + "/res/levels/Level" + level + ".txt");
        try {
            Scanner scanner = new Scanner(file);
            scanner.nextInt(); // level
            int height = scanner.nextInt();
            int width = scanner.nextInt();
            HEIGHT = height;
            WIDTH = width;
//            System.out.println(WIDTH + " " + HEIGHT);
            table = new Entity[WIDTH][HEIGHT];
            hiddenTable = new Entity[WIDTH][HEIGHT];
            // Tao Canvas
            canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT + 30);
            gc = canvas.getGraphicsContext2D();
//            gc.setFill(Color.GRAY);
            // Tao root container
            root = new Group();
            root.getChildren().add(canvas);

            // Tao scene
            Scene scene = new Scene(root, Color.BLACK);
            KeyListener keyListener = new KeyListener(scene);

            // Them scene vao stage
            stage.setScene(scene);
            stage.show();
//            cnt_enemy = 0;
            scanner.nextLine();
            for (int i = 0; i < height; i++) {
                String cur = scanner.nextLine();
                for (int j = 0; j < width; j++) {
                    Entity stillObject = null;
                    Entity object = null;
                    Entity hiddenObject = null;
                    Entity enemy = null;
                    stillObjects.add(new Grass(j, i, Sprite.grass.getFxImage));
                    switch (cur.charAt(j)) {
                        // Tiles:
                        case '#':
                            stillObject = new Wall(j, i, Sprite.wall.getFxImage);
                            break;
                        case '*':
                            object = new Brick(j, i, Sprite.brick.getFxImage);
                            break;
                        // Character:
                        case 'p':
                            object = new Bomber(j, i, Sprite.player_right.getFxImage, keyListener);
                            bomber = (Bomber) object;
                            if (bomber == null) {
                                throw new RuntimeException("Không tìm thấy vị trí player trong level file!");
                            }
                            break;
                        case '1':
                            enemy = new Balloom(j, i, Sprite.balloom_right1.getFxImage);
//                            cnt_enemy++;
                            break;
                        case '2':
                            enemy = new Oneal(j, i, Sprite.oneal_right1.getFxImage);
//                            cnt_enemy++;
                            break;
                        case '3':
                            enemy = new Doll(j, i, Sprite.doll_right1.getFxImage);
//                            cnt_enemy++;
                            break;
                        case '4':
                            enemy = new Kondoria(j, i, Sprite.doll_right1.getFxImage);
//                            cnt_enemy++;
                            break;
                        // Items:
                        case 'f':
                            hiddenObject = new FlameItem(j, i, Sprite.powerup_flames.getFxImage);
                            break;
                        case 's':
                            hiddenObject = new SpeedItem(j, i, Sprite.powerup_speed.getFxImage);
                            break;
                        case 'b':
                            hiddenObject = new BombItem(j, i, Sprite.powerup_bombs.getFxImage);
                            break;
                        case 'x':
                            hiddenObject = new PortalItem(j, i, Sprite.portal.getFxImage);
                            break;
                        case 'm':
                            hiddenObject = new FlamePassItem(j, i, Sprite.powerup_flamepass.getFxImage);
                            break;
                        case 'w':
                            hiddenObject = new WallPassItem(j, i, Sprite.powerup_wallpass.getFxImage);
                            break;
                    }
                    if (stillObject != null) {
                        stillObjects.add(stillObject);
                        table[j][i] = stillObject;
                    } else if (object != null) {
                        entities.add(object);
                        table[j][i] = object;
                    } else if (hiddenObject != null) {
                        object = new Brick(j, i, Sprite.brick.getFxImage);
                        entities.add(object);
                        table[j][i] = object;
                        hiddenTable[j][i] = hiddenObject;
                    } else if (enemy != null) {
                        enemies.add(enemy);
                        table[j][i] = enemy;
                    }
                }
            }
            MAXSCORE = enemies.size();
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void single(Stage stage) {
//        if (gameState == STATE.MENU) {
//            new_game = true;
//            return;
//        }
        setup(stage, level);
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (gameState == STATE.MENU) {
                    new_game = true;
                    stop();
                    return;
                } else {
                    update();
                    render(stage);
                }
                long frameTime = (now - lastUpdate) / 1000000;
                if (frameTime < FPS_GAME) {
                    try {
                        Thread.sleep(FPS_GAME - frameTime);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
//                    else {
//                        System.out.println(frameTime == 0 ? "" : 1000/frameTime);
//                    }
                lastUpdate = System.nanoTime();
            }
        };

        timer.start();
    }

    public void menu(Stage stage) {
        level = INIT_LEVEL;
        if (bgMusic != null) bgMusic.stop();
        bgMusic = Sound.title_screen;
        bgMusic.loop();
        //Creating a Button
        Button start_button = new Button();
        Button exit_button = new Button();
        Button AI_button = new Button();
        start_button.setStyle("-fx-background-color: transparent; ");
        start_button.setPrefSize(166, 66);
        start_button.setTranslateX(Sprite.SCALED_SIZE * 30 / 2 - 166 / 2);
        start_button.setTranslateY(Sprite.SCALED_SIZE * 15 / 2 - 25);
        InputStream stream = null;
        try {
            stream = new FileInputStream("res/start.png");
        } catch (Exception e) {
            e.getMessage();
        }
        Image img = new Image(stream);
        ImageView view = new ImageView();
        view.setFitHeight(66);
        view.setFitWidth(166);
        view.setImage(img);
        start_button.setGraphic(view);
        start_button.setOnMouseEntered(e -> start_button.setEffect(shadow));
        start_button.setOnMouseExited(e -> start_button.setEffect(null));
        start_button.setOnAction(event -> {
            gameState = STATE.SINGLE;
            Bomber.AI = false;
            single(stage);
//            Platform.runLater(()->{
//                root.getChildren().removeAll(start_button, exit_button);
//            });
        });

        AI_button.setStyle("-fx-background-color: transparent; ");
        AI_button.setPrefSize(166, 66);
        AI_button.setTranslateX(Sprite.SCALED_SIZE * 30 / 2 - 166 / 2);
        AI_button.setTranslateY(Sprite.SCALED_SIZE * 15 / 2 + 66 / 2 + 20);
        try {
            stream = new FileInputStream("res/AI_button.png");
        } catch (Exception e) {
            e.getMessage();
        }
        img = new Image(stream);
        view = new ImageView();
        view.setFitHeight(66);
        view.setFitWidth(166);
        view.setImage(img);
        AI_button.setGraphic(view);
        AI_button.setOnMouseEntered(e -> AI_button.setEffect(shadow));
        AI_button.setOnMouseExited(e -> AI_button.setEffect(null));
        AI_button.setOnAction(event -> {
//            gameState = STATE.EXIT;
            gameState = STATE.SINGLE;
            Bomber.AI = true;
            single(stage);
//            Platform.runLater(()->{
//                root.getChildren().removeAll(start_button, exit_button);
//            });
        });

        exit_button.setStyle("-fx-background-color: transparent; ");
//        button.setText("Single player");
        exit_button.setPrefSize(166, 66);
        exit_button.setTranslateX(Sprite.SCALED_SIZE * 30 / 2 - 166 / 2);
        exit_button.setTranslateY(Sprite.SCALED_SIZE * 15 / 2 + 66 / 2 + 20 + 80);
        try {
            stream = new FileInputStream("res/exit.png");
        } catch (Exception e) {
            e.getMessage();
        }
        img = new Image(stream);
        view = new ImageView();
        view.setFitHeight(66);
        view.setFitWidth(166);
        view.setImage(img);
        exit_button.setGraphic(view);
        exit_button.setOnMouseEntered(e -> exit_button.setEffect(shadow));
        exit_button.setOnMouseExited(e -> exit_button.setEffect(null));
        exit_button.setOnAction(event -> {
            gameState = STATE.EXIT;
//            gameState = STATE.SINGLE;
//            Bomber.AI = true;
//            Platform.runLater(()->{
//                root.getChildren().removeAll(start_button, exit_button);
//            });
        });
        try {
            stream = new FileInputStream("res/menu.jpeg");
        } catch (Exception e) {
            e.getMessage();
        }
        Image image = new Image(stream);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setX(0);
        imageView.setY(0);
        imageView.setFitHeight(Sprite.SCALED_SIZE * 15);
        imageView.setFitWidth(Sprite.SCALED_SIZE * 30);
//        imageView.setPreserveRatio(true);
        //Setting the Scene object
        root = new Group(imageView);
//        Scene scene = new Scene(root, 595, 370);
//        Group root = new Group(button);
        root.getChildren().add(start_button);
        root.getChildren().add(exit_button);
        root.getChildren().add(AI_button);
        Scene scene = new Scene(root, Sprite.SCALED_SIZE * 30, Sprite.SCALED_SIZE * 15, Color.BLACK);
        stage.setTitle("Bomberman NES");
        stage.setScene(scene);
        stage.show();
    }

    public void end(Stage stage) {
        InputStream stream = null;
        if (bgMusic != null) bgMusic.stop();
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent; ");
        button.setPrefSize(166, 66);
        if (gameState == STATE.NEXT_LV) {
            bgMusic = Sound.win;
            bgMusic.play();
            try {
                stream = new FileInputStream("res/youwin.png");
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            bgMusic = Sound.ending;
            bgMusic.play();
            try {
                stream = new FileInputStream("res/replay.png");
            } catch (Exception e) {
                e.getMessage();
            }
        }
        Image img = new Image(stream);
        ImageView view = new ImageView();
        view.setFitHeight(60);
        view.setFitWidth(170);
        view.setImage(img);
        button.setGraphic(view);
        button.setTranslateX(Sprite.SCALED_SIZE * 15 - 170 / 2);
        button.setTranslateY(Sprite.SCALED_SIZE * 10 - 10);
        button.setOnMouseEntered(e -> button.setEffect(shadow));
        button.setOnMouseExited(e -> button.setEffect(null));
        button.setOnAction(event -> {
            if (gameState == STATE.NEXT_LV) {
                gameState = STATE.MENU;
                new_game = true;
//                System.out.println("Whoops");
            } else if (gameState == STATE.END) {
                gameState = STATE.SINGLE;
                setup(stage, level);
            }
            isEnd = false;
        });
        //Setting the stage
        root = new Group();
        try {
            stream = new FileInputStream("res/endgame.jpeg");
        } catch (Exception e) {
            e.getMessage();
        }
        Image image = new Image(stream);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setX(0);
        imageView.setY(0);
        imageView.setFitHeight(Sprite.SCALED_SIZE * 15);
        imageView.setFitWidth(Sprite.SCALED_SIZE * 30);
//        imageView.setPreserveRatio(true);
        //Setting the Scene object
        root.getChildren().add(imageView);
        root.getChildren().add(button);
        Scene scene = new Scene(root, Sprite.SCALED_SIZE * 30, Sprite.SCALED_SIZE * 15, Color.BLACK);
        stage.setTitle("Bomberman NES");
        stage.setScene(scene);
        stage.show();
    }

    public void gameLoop(Stage stage) {
        switch (gameState) {
            case MENU:
                menu(stage);
                break;

            case SINGLE:
                single(stage);
                break;

            case MULTIPLAYER:
                break;

            case PAUSE:
                break;

            case EXIT:
                Platform.exit();
                break;
            default:
                throw new IllegalArgumentException("Invalid game state");
        }
    }

    @Override
    public void start(Stage stage) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (new_game) {
                    gameLoop(stage);
                    new_game = false;
                }
                if (gameState == STATE.EXIT) {
                    Platform.exit();
                }
            }
        };
        timer.start();
    }

    public void update() {
        enemies.forEach(Entity::update);
        entities.forEach(Entity::update);
//        enemies.forEach(Entity::update);
//        if (cnt_enemy == 0) gameState = STATE.NEXT_LV;
    }

    public void render(Stage stage) {
        switch (gameState) {
            case SINGLE:
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                stillObjects.forEach(g -> g.render(gc));
                entities.forEach(g -> g.render(gc));
                enemies.forEach(g -> g.render(gc));
                if (bomber != null) bomber.render(gc);

                root.getChildren().remove(textLife);
                root.getChildren().remove(textScore);
                root.getChildren().remove(textLevel);

                Font font = new Font("pixels", 20);

                textLife = new Text(10, Sprite.SCALED_SIZE * HEIGHT + 22, "LIFE: " + Bomber.getBomberLife());
                textLife.setFont(font);
                textLife.setFill(Color.WHITE);

                textScore = new Text(100, Sprite.SCALED_SIZE * HEIGHT + 22, "SCORE: " + (MAXSCORE - enemies.size()) * 100);
                textScore.setFont(font);
                textScore.setFill(Color.WHITE);

                textLevel = new Text(Sprite.SCALED_SIZE * WIDTH / 2 - 40, Sprite.SCALED_SIZE * HEIGHT + 22, "LEVEL: " + level);
                textLevel.setFont(font);
                textLevel.setFill(Color.WHITE);

                root.getChildren().add(textLevel);
                root.getChildren().add(textLife);
                root.getChildren().add(textScore);

//                Bomber.AI = true;

                break;

//            case MENU:
//                break;

            case MULTIPLAYER:
                break;

            case PAUSE:
                break;
            case NEXT_LV:
                if (level < MAX_LEVEL) {
                    gameState = STATE.SINGLE;
                    setup(stage, ++level);
                } else if (!isEnd) {
                    
                    end(stage);
                    isEnd = true;
                }
                break;
            case END:
                if (!isEnd) {
                    end(stage);
                    isEnd = true;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid game state");
        }
    }

    public enum STATE {
        MENU, SINGLE, MULTIPLAYER, PAUSE, END, NEXT_LV, EXIT
    }
}
