package asteroidsApp;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;


public class AsteroidsApp extends Application {

    private Pane root;

    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    private Set<String> pressed = new HashSet<String>();
    private GameObject player;
    private AnimationTimer timer;
    private Double bulletTime = 0.0;
    private Double bulletFinal = 0.0;
    private Double lambdaBulletTime = (0.5) * 1000000000; // Change this to alter hwo fast you can shoot bullets. The number before * is seconds

    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(600, 600);

        player = new Player();
        player.setVelocity(new Point2D(1, 0));
        addGameObject(player, 300, 300);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

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

    private void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
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

    private void onUpdate() {

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
            System.out.println();
        }

        for (GameObject bullet : bullets) {
            for (GameObject enemy : enemies) {
                if (bullet.isColliding(enemy)) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);

                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                }
            }
        }

        bullets.removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);

        bullets.forEach(GameObject::update);
        enemies.forEach(GameObject::update);

        player.update();

        if (Math.random() < 0.02) {
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
        }
    }

    private static class Player extends GameObject {
        Player() {
            super(new Rectangle(40, 20, Color.BLUE));
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
