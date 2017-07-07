package asteroidsApp;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;


public class AsteroidsApp extends Application {

    private Pane root;

    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    private List<GameObject> speedBoosts = new ArrayList<>();
    private Set<String> pressed = new HashSet<String>();
    private GameObject player;
    private AnimationTimer timer;
    private Double bulletTime = 0.0;
    private Double bulletFinal = 0.0;
    private Double lambdaBulletTime = (0.0) * 1000000000; // Change this to alter hwo fast you can shoot bullets. The number before * is seconds
    private double playerX;
    private double playerY;
    private int livesLeft = 5;
    private int score = 0;
    private Text livesText;
    private Text scoreText;
    public final int WIDTH = 1280;
    public final int HEIGHT = 720;

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);
        player = new Player();
        player.setVelocity(new Point2D(1, 0));
        addGameObject(player, 300, 300);
        Image img = new Image("/Images/Background/ovalOffice.jpg");
        BackgroundImage bgImg = new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false));
        root.setBackground(new Background(bgImg));
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        livesText = new Text(WIDTH - 135, 20, "Lives left: " + livesLeft);
        livesText.setFill(Color.BLACK);
        livesText.setStyle("-fx-font-size: 18px;");

        scoreText = new Text(WIDTH - 135, 40, "Score: " + score);
        scoreText.setFill(Color.BLACK);
        scoreText.setStyle("-fx-font-size: 18px;");

        root.getChildren().addAll(livesText, scoreText);

        return root;
    }
    private void addBullet(GameObject bullet, double x, double y) {
        bullets.add(bullet);
        addGameObject(bullet, x, y);
    }

    private void addEnemy(GameObject enemy, double x, double y) {
        enemies.add(enemy);
        addGameObject(enemy, x, y);
    }

    private void addSpeedBoost(GameObject speedBoost, double x, double y){
        speedBoosts.add(speedBoost);
        addGameObject(speedBoost, x, y);
    }

    private void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }

    private void setLambdaBulletTime(Double seconds){
        lambdaBulletTime = seconds * 1000000000;
    }

    private void shootBullet(){
        bulletTime = (double) System.nanoTime();
        if(bulletTime > bulletFinal) {
            bulletFinal = bulletTime + lambdaBulletTime;
            Bullet bullet = new Bullet();
            bullet.setVelocity(player.getVelocity().normalize().multiply(5));
            addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY());
        }
    }

    private void gameOver(){
        Text gameOverText = new Text((WIDTH / 2) - 170, HEIGHT / 2, "Impeached!");
        Rectangle gameOverBox = new Rectangle((WIDTH / 2) - 220, (HEIGHT / 2) - 100, 450, 250);
        int highScore = Integer.parseInt(GameObject.readHighScore());

        if(score > highScore){
            GameObject.writeHighScore(score);
        }

        ImageView gameOverImage = new ImageView(new Image("/Images/Background/trumpHead.jpg", 200, 100, false, true));

        gameOverText.setFill(Color.WHITE);
        gameOverText.setStyle("-fx-font-size: 60");

        gameOverBox.setFill(Color.BLACK);

        root.getChildren().addAll(gameOverBox, gameOverText);
        timer.stop();
    }

    private void onUpdate() {
        //Game over
        if(livesLeft < 0){
            gameOver();
        }

        //Managing buttons
        Iterator<String> it = pressed.iterator();
        while(it.hasNext()){
            String current = it.next();
            if("A".equals(current) || "LEFT".equals(current)){
                player.rotateLeft();
            }
            if("D".equals(current) || "RIGHT".equals(current)){
                player.rotateRight();
            }
            if("SPACE".equals(current)){
                shootBullet();
            }
            if("W".equals(current)){
                //player.setVelocity(new Point2D(player.getView().getTranslateX() + 5, player.getView().getTranslateY() + 5));
                player.setVelocity(new Point2D(player.velocity.getX() - 3, player.velocity.getY() + 3));
            }
        }

        //Managing killing enemies and bullets
        for (GameObject bullet : bullets) {
            for (GameObject enemy : enemies) {
                if (bullet.isColliding(enemy)) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);
                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                    score += 10;
                    scoreText.setText("Score: " + score);
                }
            }

        }

        for(GameObject enemy: enemies) {
            if (player.isColliding(enemy)) {
                enemy.setAlive(false);
                livesLeft--;
                livesText.setText("Lifes left: " + livesLeft);
                root.getChildren().remove(enemy.getView());
            }
        }

        //Manage speed boosts
        for(GameObject speedBoost: speedBoosts){
            if(player.isColliding(speedBoost)){
                speedBoost.setAlive(false);
                root.getChildren().remove(speedBoost.getView());
                //player.setVelocity(new Point2D(player.getVelocity().getX() + 1, player.getVelocity().getY() + 1));
            }
        }

        bullets.removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);

        bullets.forEach(GameObject::update);
        enemies.forEach(GameObject::update);

        //Manage leaving the screen
        playerX = player.getView().getTranslateX();
        playerY = player.getView().getTranslateY();
        if(playerY < -20){
            player.getView().setTranslateY(HEIGHT);
        } else if(playerY > HEIGHT){
            player.getView().setTranslateY(-20);
        }
        if(playerX < -20){
            player.getView().setTranslateX(WIDTH);
        } else if(playerX > WIDTH){
            player.getView().setTranslateX(-20);
        }

        player.update();

        if (Math.random() < 0.02) {//Make sure enemy does not spawn directly on player
            double randomX = Math.random() * root.getPrefWidth();
            double randomY = Math.random() * root.getPrefHeight(); //TODO make sure bullets don't spawn on lives/score text
            while(!(Math.abs(randomX - player.getView().getTranslateX()) > 50) && !(Math.abs(randomY - player.getView().getTranslateY()) > 50)){
                randomX = Math.random() * root.getPrefWidth();
                randomY = Math.random() * root.getPrefHeight();
            }
            addEnemy(new Enemy(), randomX, randomY);
        }
        if (Math.random() < 0.01) {
            double randomX = Math.random() * root.getPrefWidth();
            double randomY = Math.random() * root.getPrefHeight(); //TODO make sure bullets don't spawn on lives/score text
            while(!(Math.abs(randomX - player.getView().getTranslateX()) > 50) && !(Math.abs(randomY - player.getView().getTranslateY()) > 50)){
                randomX = Math.random() * root.getPrefWidth();
                randomY = Math.random() * root.getPrefHeight();
            }
            addSpeedBoost(new SpeedBoost(), randomX, randomY);
        }
    }

    private static class Player extends GameObject {
        Player() {
            super(new ImageView(new Image("/Images/Background/trumpHead.jpg", 100, 100, false, true)));

        }
    }

    private static class Enemy extends GameObject {
        Enemy() {
            super(new Circle(15, 15, 15, Color.RED));
        }
    }

    private static class Bullet extends GameObject {
        Bullet() {
            super(new Circle(5, 5, 5, Color.BROWN));
        }
    }

    private static class SpeedBoost extends GameObject {
        SpeedBoost() {super(new Circle(10, 10, 10, Color.GREEN)); }
    }

    public Scene setEventListeners(Scene scene){
        scene.setOnKeyPressed(e -> {
            pressed.add(e.getCode().toString());
        });
        scene.setOnKeyReleased(e -> {
            pressed.remove(e.getCode().toString());
        });
        return scene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.setScene(setEventListeners(stage.getScene()));
        stage.show();
        GameObject.readHighScore();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
