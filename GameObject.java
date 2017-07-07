package asteroidsApp;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import java.io.*;

public class GameObject {

    private Node view;

    public Point2D velocity = new Point2D(0, 0);
    private double rotationSpeed = 5;

    private boolean alive = true;

    public GameObject(Node view) {
        this.view = view;
    }

    public void update() {
        view.setTranslateX(view.getTranslateX() + velocity.getX());
        view.setTranslateY(view.getTranslateY() + velocity.getY());
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public Node getView() {
        return view;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDead() {
        return !alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public double getRotate() {
        return view.getRotate();
    }

    public void rotateRight() {
        view.setRotate(view.getRotate() + rotationSpeed);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    public void rotateLeft() {
        view.setRotate(view.getRotate() - rotationSpeed);
        setVelocity(new Point2D(Math.cos(Math.toRadians(getRotate())), Math.sin(Math.toRadians(getRotate()))));
    }

    public boolean isColliding(GameObject other) {
        return getView().getBoundsInParent().intersects(other.getView().getBoundsInParent());
    }

    public static void writeHighScore(int score){
        BufferedWriter bw = null;
        FileWriter fw = null;
        final String FILENAME = "storage/highscore.txt";
        System.out.println("Score: " + score);
        String sScore = Integer.toString(score);
        try{
            fw = new FileWriter(FILENAME);
            bw = new BufferedWriter(fw);
            bw.write(sScore);

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }

    }

    public static String readHighScore(){
        BufferedReader br = null;
        FileReader fr = null;
        final String FILENAME = "storage/highscore.txt";

        try {

            fr = new FileReader(FILENAME);
            br = new BufferedReader(fr);

            String sCurrentLine;

            br = new BufferedReader(new FileReader(FILENAME));
            sCurrentLine = br.readLine();

            if(sCurrentLine != null){
                return sCurrentLine;
            }


        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return "0";
    }
}
