package museDB;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;

import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;
import static java.lang.Math.random;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Visualizer {
  Random rand = new Random();
  Group root = new Group();
  Scene scene = new Scene(root, 1000, 700, Color.BLACK);
  Group circles = new Group();
  Group circles2 = new Group();
  Group triangs = new Group();
  Group Vtriangs = new Group();
  Group rectangles = new Group();
  float[] data = {10,20,30,40,50,10,20,30,40,50};
  
  Visualizer(){
  
  scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
    public void handle(KeyEvent event) {
      switch (event.getCode()) {
          case ESCAPE: MuseDB.backToHome();
      }
    }
  });
  
  for (int i = 0; i < 40; i++) {
      Circle circle = new Circle(200, Color.web("white", 0.05));
      circle.setStrokeType(StrokeType.OUTSIDE);
      circle.setStroke(Color.web("white", 0.16));
      if (i < 9){
      circle.setCenterX((i+1)*200.0f);
      circle.setCenterY(500.0f);
      circle.setRadius(200.0f);
      }
      circle.setStrokeWidth(4);
      circles.getChildren().add(circle);
    }
  
  for (int i = 0; i < 20; i++) {
      Rectangle r = new Rectangle();
      r.setStrokeType(StrokeType.OUTSIDE);
      r.setStroke(Color.web("white", 0.16));
      r.setX(100.0f);;
      r.setY(100.0f);
      r.setWidth(20);
      r.setHeight(10);
      r.setStrokeWidth(5);
      rectangles.getChildren().add(r);
  }
  
  for (int i = 0; i < 20; i++) {
    Polygon t = new Polygon();
    t.setStrokeType(StrokeType.OUTSIDE);
    t.setStroke(Color.web("white", 0.16));
    t.getPoints().addAll(new Double[]{
        505.0, 408.0,
        205.0, 207.0,
        350.0, 350.0 });
    t.setStrokeWidth(5);
    triangs.getChildren().add(t);
  }
  
  for (int i = 0; i < 10; i++) {
    Polygon t = new Polygon();
    t.setStrokeType(StrokeType.OUTSIDE);
    t.setStroke(Color.web("white", 0.16));
    t.getPoints().addAll(new Double[]{
        505.0, 408.0,
        105.0, 107.0,
        10.0, 1050.0 });
    t.setStrokeWidth(5);
    Vtriangs.getChildren().add(t);
  }
  
  Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
      new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.REPEAT, new Stop[]{
          new Stop(0, Color.web("#f8bd55")),
          new Stop(0.14, Color.web("#c0fe56")),
          new Stop(0.20, Color.web("#be4af7")),
          new Stop(0.28, Color.web("#5dfbc1")),
          new Stop(0.43, Color.web("#64c2f8")),
          new Stop(0.57, Color.web("#be4af7")),
          new Stop(0.71, Color.web("#ed5fc2")),
          new Stop(0.78, Color.web("#be4af7")),
          new Stop(0.85, Color.web("#ef504c")),
          new Stop(1, Color.web("#f2660f")),}));
  colors.heightProperty().bind(scene.heightProperty());
  colors.widthProperty().bind(scene.widthProperty());
  Group blendModeGroup =
        new Group(new Group(new Rectangle(scene.getWidth(), scene.getHeight(),
        Color.BLACK),Vtriangs, triangs, circles, rectangles), colors);
  colors.setBlendMode(BlendMode.OVERLAY);
  root.getChildren().add(blendModeGroup);
  Vtriangs.setEffect(new BoxBlur(10, 10, 3));
  triangs.setEffect(new BoxBlur(10, 10, 3));
  circles.setEffect(new BoxBlur(10, 10, 3));
  rectangles.setEffect(new BoxBlur(10, 10, 3));
  Timeline timeline = new Timeline();
  int z =0;
  for (Node circle : circles.getChildren()) {
    if (z >=10) {
    timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO, // set start position at 0
            new KeyValue(circle.translateXProperty(), rand.nextInt(100)),
            new KeyValue(circle.translateYProperty(), rand.nextInt(700))),
            new KeyFrame(new Duration(30000), // set end position at 10s
            new KeyValue(circle.translateXProperty(), rand.nextInt(2000)),
            new KeyValue(circle.translateYProperty(), rand.nextInt(2000))));
    }
    z++;
    }
  for (Node r : rectangles.getChildren()) {
    timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO, // set start position at 0
            new KeyValue(r.translateXProperty(), random() * 2000),
            new KeyValue(r.translateYProperty(), random() * 2000)),
            new KeyFrame(new Duration(20000), // set end position at 20s
            new KeyValue(r.translateXProperty(), random() * 1000),
            new KeyValue(r.translateYProperty(), random() * 700)));
    }
    timeline.play();
    
    Timer t = new Timer();
    t.schedule(new TimerTask() {
        public void run() {
           updateTriangs();
        }
    }, 0, 10000);
  }
  
  public void updateTriangs(){
    Timeline timeline = new Timeline();
    for (Node t : triangs.getChildren()) {
      timeline.getKeyFrames().addAll(
          new KeyFrame(Duration.ZERO, // set start position at 0
          new KeyValue(t.translateXProperty(), rand.nextInt(2000)),
          new KeyValue(t.translateYProperty(), rand.nextInt(1000))),
          new KeyFrame(new Duration(95000), // set end position at 10ms
          new KeyValue(t.translateXProperty(), rand.nextInt(2000)),
          new KeyValue(t.translateYProperty(), 100 + (2 * rand.nextInt(500)))));
    }
    timeline.play();
  }
  
  public void updateCircs(float[] magnitudes) {
    Timeline timeline = new Timeline();
    int i = 0;
    for (Node circle : circles.getChildren()) {

      if (i < 5){
        
      timeline.getKeyFrames().addAll(
              new KeyFrame(Duration.ZERO, // set start position at 0
              new KeyValue(circle.translateXProperty(), 75*i),
              new KeyValue(circle.translateYProperty(), 100 + 2*data[i])),
              new KeyFrame(new Duration(100), // set end position at 10ms
              new KeyValue(circle.translateXProperty(), 75*i),
              new KeyValue(circle.translateYProperty(), 100 + (2 * magnitudes[i]))));
      }

       if (i >= 5 && i <= 9){
        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO, // set start position at 0
            new KeyValue(circle.translateXProperty(), i*50.0f),
            new KeyValue(circle.translateYProperty(), 100 + 2*data[9-i])),
            new KeyFrame(new Duration(100), // set end position at 10ms
            new KeyValue(circle.translateXProperty(), i*50.0f),
            new KeyValue(circle.translateYProperty(), 100 + 2* magnitudes[(i)])));
      }

      i++;
      }
    data[0]= magnitudes[0];
    data[1]= magnitudes[1];
    data[2]= magnitudes[2];
    data[3]= magnitudes[3];
    data[4]= magnitudes[4];
    data[5]= magnitudes[5];
    data[6]= magnitudes[6];
    data[7]= magnitudes[7];
    data[8]= magnitudes[8];
    data[9]= magnitudes[9];
    timeline.play();
  }
  
  public void updateVtriangs(float[] magnitudes) {
    Timeline timeline = new Timeline();
    int i = 0;
    for (Node t : Vtriangs.getChildren()) {

      if (i < 5){
        
      timeline.getKeyFrames().addAll(
              new KeyFrame(Duration.ZERO, // set start position at 0
              new KeyValue(t.translateXProperty(), 150*i),
              new KeyValue(t.translateYProperty(), 100 + 2*data[i])),
              new KeyFrame(new Duration(100), // set end position at 10ms
              new KeyValue(t.translateXProperty(), 150*i),
              new KeyValue(t.translateYProperty(), 100 + (2 * magnitudes[i]))));
      }

       if (i >= 5 && i <= 9){
        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.ZERO, // set start position at 0
            new KeyValue(t.translateXProperty(), i*80.0f),
            new KeyValue(t.translateYProperty(), 100 + 2*data[9-i])),
            new KeyFrame(new Duration(100), // set end position at 10ms
            new KeyValue(t.translateXProperty(), i*80.0f),
            new KeyValue(t.translateYProperty(), 100 + 1* magnitudes[(i)])));
      }

      i++;
      }
    timeline.play();
  }
}
