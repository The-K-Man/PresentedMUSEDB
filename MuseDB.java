package museDB;

import java.util.*;
import java.io.File;
import javafx.application.Application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Dragboard;
/**
 *
 * @author Lloyd Cloer
 */

public class MuseDB extends Application {
    Player player;
    Organizer org;
    MakeShiftDatabase db;
    Control control;
    FileCoordinator fc;
    View view;
    AudioSpectrumListener audioSpecListener;
    static BorderPane Globalroot;
    static Player Globalplayer;
    static HBox mediaControlBar = new HBox();
    static   recommend recommender = new   recommend();
    static BorderPane pane = new BorderPane();
    static BorderPane pane2 = new BorderPane();
    static Stage GlobalStage;
    static Scene globalScene;
    static Scene scene;
    
    
  //  VBox song_listing;
    Button play_button;
    Region library_display;
    BorderPane root;
  //  ScrollPane sp;
    
    public static void main(String[] args) {
        if (false){
            String s = "C:\\Users\\Lloyd Cloer\\Music\\Aesthetic Perfection\\Close To Human\\03 Architech.mp3";
            File file = new File(s);
            File dir = new File("C:\\Users\\Lloyd Cloer\\Videos\\ThatFolder");
            dir.mkdir();
            System.out.println(dir.getAbsolutePath());
            System.out.println(file.getName());
            String dest = dir.getAbsolutePath()+"\\"+file.getName();
            FileCoordinator.copyFile(file, dest);
        }
        
        
        if (true){
            MuseDB muse = new MuseDB();
            muse.launch();
        }
    }
    
    public void start(Stage stage) {
        //Set global stage = to primary stage
        GlobalStage = stage;
        //Creating the mediaplayer to grab hbox to add for sliders
        //PUT LOCAL SONG HERE
        /**
        File file = new File("C:\\Users\\kpari\\workspace\\MymuseDB\\src\\museDB\\03 Understand Me Now 1.mp3");
        Media media = new Media(file.toURI().toString());
        MediaPlayer media_player = new MediaPlayer(media);
        **/
        MediaPlayer media_player = null;
        //Creation of the Visualizer obj
        Visualizer vis = new Visualizer();
        //AUDIOSPECLISTENER INITIALIZED
        audioSpecListener = new AudioSpectrumListener() {
          @Override
          public void spectrumDataUpdate(double timestamp, double duration,
                  float[] magnitudes, float[] phases) {
                  vis.updateCircs(magnitudes);
                  vis.updateVtriangs(magnitudes);
                  System.out.println(magnitudes[0] +" " + magnitudes[2] + " " +magnitudes[4]);
          }
          };
          //Initialize player after audiospectrumlistener() is initialized

        
        // *** Initialize Model Classes *** //
        //Initialize player after audiospectrumlistener() is initialized
        Player player = new Player();
        player.audSpecListener = audioSpecListener;
        Globalplayer = player;

        

        
        //RECOMMENDER!!!!
        Button btnRecommended = new Button("Recommended");
        btnRecommended.setOnAction(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent e) {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(stage);
            VBox dialogVbox = new VBox(20);
            ArrayList<String> recomendations = new ArrayList();
            try {
              recomendations =  recommender.getRecsGenre("Rock");
            }
            catch (Exception e1) {
              e1.printStackTrace();
            }
            
            int artist_length = recomendations.size();
            if (artist_length < 1){
              dialogVbox.getChildren().add(new Text("Please Connect To The Internet"));
            }
            
            else {
              dialogVbox.getChildren().add(new Text("Your Recommended Artists"));
              Iterator itr=recomendations.iterator();  
              while(itr.hasNext()){  
               dialogVbox.getChildren().add(new Text((itr.next()).toString()));

              }
            }
            dialogVbox.getChildren().add(new Text("Your Recommended Artists"));
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
          }
        });
        // VISUALIZER BUTTON
        Button playVis = new Button();
        playVis.setText("Visualizer");
        playVis.setOnAction(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent e) {
            stage.setScene(vis.scene);
            stage.setTitle("VISUALIZER");
            stage.show();
            
          }
        });
        
        db = new MakeShiftDatabase();
        fc = new FileCoordinator(this);
        org = new Organizer(this);
        view = new View(this);
        control = new Control(this);
        
        
        // *** Initialize GUI *** //
        stage.setTitle("MuseDB");
        
        root = new BorderPane();
        Globalroot = new BorderPane();
        // Control Panel //
        VBox control_panel = new VBox();
        Globalroot.setLeft(control_panel);
        
        updateDisplay();

        // *** Menu Bar *** //
        
        final Menu file_menu = new Menu("File");
        
        
            // ** Import Button ** //
        MenuItem import_item = new MenuItem("Import");
        import_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                FileChooser file_chooser = new FileChooser();
                List<File> list = file_chooser.showOpenMultipleDialog(stage);
                fc.importFiles(list);
                
              //  organizer.setMedium(organizer.current_medium);
                updateDisplay();//sp.setContent(view.libraryDisplay());
            }
        });
        file_menu.getItems().add(import_item);
        
            // ** Create Playlist ** //
        MenuItem playlist_item = new MenuItem("Create Playlist");
        playlist_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Create Playlist");
                Optional<String> result = dialog.showAndWait();                
                if (result.isPresent()){
                    String pl_name = result.get();
                    control_panel.getChildren().add(new PlaylistButton(pl_name));
                    db.createPlaylist(pl_name);
                }
                
              //  organizer.setMedium(organizer.current_medium);
                updateDisplay();//sp.setContent(view.libraryDisplay());
            }
        });
        file_menu.getItems().add(playlist_item);
        
            // ** Export Playlist ** //
        MenuItem export_item = new MenuItem("Export Playlist");
        export_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Export Playlist: "+org.listing_name);
                File start_dir = new File("C:");
                dc.setInitialDirectory(start_dir);
                File selected_dir = dc.showDialog(stage);
                fc.exportPlaylist(org.listing_name, selected_dir);
            }
        });
        file_menu.getItems().add(export_item);
        
        // *** Tool Menu *** //
        final Menu tool_menu = new Menu("Tools");
        MenuItem sleep_timer_item = new MenuItem("Set Sleep Timer");
        sleep_timer_item.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Set Sleep Timer (minutes)");
                Optional<String> result = dialog.showAndWait();                
                if (result.isPresent()){
                    int time = Integer.parseInt(result.get());
                    control.setSleepTimer(time);
                }
                
              //  organizer.setMedium(organizer.current_medium);
                updateDisplay();//sp.setContent(view.libraryDisplay());
            }
        });
        tool_menu.getItems().add(sleep_timer_item);
        
        
        
        final Menu menu3 = new Menu("Help");

        MenuBar menu_bar = new MenuBar();
        menu_bar.getMenus().addAll(file_menu, tool_menu, menu3);
        
        Globalroot.setTop(menu_bar);
        //root
        
        
        //VIDEO DEMO
        /**
        Vidbutton = new Button();
        Vidbutton.setText("PlayVID");
        Vidbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (play_button.getText() == "Play"){
                    player.play();
                    play_button.setText("Pause");
                }
                else {
                    player.pause();
                    play_button.setText("Play");
                }
            }
        });
        **/
        
        //******* Play Button *******/
        
        play_button = new Button();
        play_button.setText("Play");
        play_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (play_button.getText() == "Play"){
                    player.play();
                    play_button.setText("Pause");
                }
                else {
                    player.pause();
                    play_button.setText("Play");
                }
            }
        });
        
        control_panel.getChildren().add(play_button);
        control_panel.getChildren().add(playVis);
        control_panel.getChildren().add(btnRecommended);
        
        control_panel.getChildren().add(new Label()); // leave a soace
        
        control_panel.getChildren().add(new Label("Libraries"));
        

        
        // *** Meidum Panel *** //
        
        control_panel.getChildren().add(new ListingButton("Music"));
        
        
        control_panel.getChildren().add(new Label());
        control_panel.getChildren().add(new Label("Playlists"));
        globalScene = new Scene(Globalroot, 300, 250);
        stage.setScene(globalScene);
        stage.show();
        
    }
    
    public void updateDisplay(){
        System.out.println("hi");
        root.setCenter(view.libraryDisplay());
        Globalroot.setCenter(view.libraryDisplay());
        //System.out.println("hii");
    }
    
    public static void backToHome(){
      GlobalStage.setScene(globalScene);
        ToolBar toolBar3  = new ToolBar();
        mediaControlBar = Globalplayer.mediaBar;
        toolBar3. getItems().add(mediaControlBar);
        Globalroot.setBottom(toolBar3);
      GlobalStage.setTitle("MuseDB");
      GlobalStage.show();
    }

    public class ListingButton extends Button{
        
        public ListingButton(String text){
            super(text);
            setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override 
                    public void handle(ActionEvent e) {
                       // organizer.setMedium(getText());
                       control.setListing(getText());
                    }
                }
            );
        }
    }
    public class PlaylistButton extends ListingButton{
        //String text;
        String name;
        public PlaylistButton(String text){
            super(text);
            this.name = text;
            
            PlaylistButton target = this;
            
            target.setOnDragOver(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    if (event.getGestureSource() != target &&
                            event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    event.consume();
                }
            });
            target.setOnDragDropped(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    Dragboard board = event.getDragboard();
                    boolean success = false;
                    if (board.hasString()) {
                       int songID = Integer.parseInt(board.getString());
                       db.addSongToPlaylist(target.name, songID);
                       success = true;
                    }
                    event.setDropCompleted(success);

                    event.consume();
                 }
            });
        }
                    
    }
    
}
