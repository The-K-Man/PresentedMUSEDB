package museDB;

import java.io.File;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Lloyd Cloer
 */
public class Player extends BorderPane{
    MediaPlayer media_player;
    public boolean is_playing = false;
    private MediaView mediaView;  
    static final boolean repeat = false;
    static boolean stopRequested = false;
    static boolean atEndOfMedia = false;
    static Duration duration;
    static Slider timeSlider;
    static Label playTime;
    static Slider volumeSlider;
    static HBox mediaBar;
    AudioSpectrumListener audSpecListener;
    
    //INITIALIZE PLAYER CLASS HERE
    
    protected void updateValues() {
      if (playTime != null && timeSlider != null && volumeSlider != null) {
         Platform.runLater(new Runnable() {
            public void run() {
              Duration currentTime = media_player.getCurrentTime();
              playTime.setText(formatTime(currentTime, duration));
              timeSlider.setDisable(duration.isUnknown());
              if (!timeSlider.isDisabled() 
                && duration.greaterThan(Duration.ZERO) 
                && !timeSlider.isValueChanging()) {
                  timeSlider.setValue(currentTime.divide(duration).toMillis()
                      * 100.0);
              }
              if (!volumeSlider.isValueChanging()) {
                volumeSlider.setValue((int)Math.round(media_player.getVolume() 
                      * 100));
              }
            }
         });
      }
    }
    
    private static String formatTime(Duration elapsed, Duration duration) {
      int intElapsed = (int)Math.floor(elapsed.toSeconds());
      int elapsedHours = intElapsed / (60 * 60);
      if (elapsedHours > 0) {
          intElapsed -= elapsedHours * 60 * 60;
      }
      int elapsedMinutes = intElapsed / 60;
      int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
                              - elapsedMinutes * 60;
    
      if (duration.greaterThan(Duration.ZERO)) {
         int intDuration = (int)Math.floor(duration.toSeconds());
         int durationHours = intDuration / (60 * 60);
         if (durationHours > 0) {
            intDuration -= durationHours * 60 * 60;
         }
         int durationMinutes = intDuration / 60;
         int durationSeconds = intDuration - durationHours * 60 * 60 - 
             durationMinutes * 60;
         if (durationHours > 0) {
            return String.format("%d:%02d:%02d/%d:%02d:%02d", 
               elapsedHours, elapsedMinutes, elapsedSeconds,
               durationHours, durationMinutes, durationSeconds);
         } else {
             return String.format("%02d:%02d/%02d:%02d",
               elapsedMinutes, elapsedSeconds,durationMinutes, 
                   durationSeconds);
         }
         } else {
             if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, 
                       elapsedMinutes, elapsedSeconds);
               } else {
                   return String.format("%02d:%02d",elapsedMinutes, 
                       elapsedSeconds);
               }
           }
       }
    
    
    public void selectSong(File file){
        //File file = new File("C:\\Users\\Lloyd Cloer\\Music\\Aesthetic Perfection\\Close To Human\\03 Architech.mp3");
        try {
            Media media = new Media(file.toURI().toString());
            media_player = new MediaPlayer(media);
          //  media_player.play();
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception: " + ex.getMessage());
        }
        
        
        //CREATE THE MEDIA BAR
        MediaPlayer mp = media_player;
        mp.setAudioSpectrumListener(audSpecListener);
        setStyle("-fx-background-color: #bfc2c7;");
        mediaView = new MediaView(mp);
        Pane mvPane = new Pane() {                };
        mvPane.getChildren().add(mediaView);
        mvPane.setStyle("-fx-background-color: black;"); 
        setCenter(mvPane);
        mediaBar = new HBox();
        mediaBar.setAlignment(Pos.CENTER);
        mediaBar.setPadding(new Insets(6, 11, 6, 11));
        BorderPane.setAlignment(mediaBar, Pos.CENTER);

        final Button playButton  = new Button(">");
        playButton.setOnAction(new EventHandler<ActionEvent>() {
          public void handle(ActionEvent e) {
              Status status = mp.getStatus();
       
              if (status == Status.UNKNOWN  || status == Status.HALTED)
              {
                 // don't do anything in these states
                 return;
              }
       
                if ( status == Status.PAUSED
                   || status == Status.READY
                   || status == Status.STOPPED)
                {
                   // rewind the movie if we're sitting at the end
                   if (atEndOfMedia) {
                      mp.seek(mp.getStartTime());
                      atEndOfMedia = false;
                   }
                   mp.play();
                   } else {
                     mp.pause();
                   }
               }
         });
        mediaBar.getChildren().add(playButton);
        
        media_player.currentTimeProperty().addListener(new InvalidationListener() 
        {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

        mp.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    mp.pause();
                    stopRequested = false;
                } else {
                    playButton.setText("||");
                }
            }
        });

        mp.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                playButton.setText(">");
            }
        });

        mp.setOnReady(new Runnable() {
            public void run() {
                duration = mp.getMedia().getDuration();
                updateValues();
            }
        });

        mp.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        mp.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    playButton.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
       });
         
        // Add Time label
        Label timeLabel = new Label("Time: ");
        mediaBar.getChildren().add(timeLabel);
        
        Label space_section = new Label("   ");
        mediaBar.getChildren().add(space_section);
         
        // Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider,Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
          public void invalidated(Observable ov) {
             if (timeSlider.isValueChanging()) {
             // multiply duration by percentage calculated by slider position
                mp.seek(duration.multiply(timeSlider.getValue() / 100.0));
             }
          }
      });
        mediaBar.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        mediaBar.getChildren().add(playTime);
         
        // Add the volume label
        Label volumeLabel = new Label("Vol: ");
        mediaBar.getChildren().add(volumeLabel);
         
        // Add Volume slider
        volumeSlider = new Slider();        
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
          public void invalidated(Observable ov) {
             if (volumeSlider.isValueChanging()) {
                 mp.setVolume(volumeSlider.getValue() / 100.0);
             }
          }
      }); 
        mediaBar.getChildren().add(volumeSlider);
     }
    
    public void selectVideo(File file, Group VidRoot,Stage stage, AudioSpectrumListener audSpecListener){
      try {
          Media media = new Media(file.toURI().toString());
          media_player = new MediaPlayer(media);
          if(mediaView == null) {
            mediaView = new MediaView(media_player);
          }
          mediaView.setMediaPlayer(media_player);
          StackPane Vroot = new StackPane();
          Vroot.getChildren().add(mediaView);
          media_player = new MediaPlayer(media);                  
          MediaView mediaView = new MediaView(media_player);
          mediaView.setMediaPlayer(media_player);
          Player mediaControl = new Player();
          Scene scene = new Scene(VidRoot, 960, 540);
          scene.setRoot(mediaControl);
          scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
              switch (event.getCode()) {
                  case ESCAPE: MuseDB.backToHome();
              }
            }
          });
          
          stage.setScene(scene);
          media_player.play();
          stage.setTitle("VIDPLAYER");
          stage.show();
          media_player.play();
          media_player.play();
      } catch(Exception ex) {
          ex.printStackTrace();
          System.out.println("Exception: " + ex.getMessage());
      }
  }
    
    public void play(){
        media_player.play();
        is_playing =true;
    }    
    public void pause(){
        media_player.pause();
        is_playing =false;
    }
    
}
