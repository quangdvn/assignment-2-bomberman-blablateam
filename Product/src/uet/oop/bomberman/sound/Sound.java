package uet.oop.bomberman.sound;

import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import java.net.URL;
import java.util.ArrayList;

public class Sound {
    public static final String titleScreen = "/sound/01_Title Screen.mp3";
    public static final String stageStart = "/sound/02_Stage Start.mp3";
    public static final String stageTheme = "/sound/03_Stage Theme.mp3";
    public static final String findTheExit = "/sound/04_Find The Exit.mp3";
    public static final String stageComplete = "/sound/05_Stage Complete.mp3";
    public static final String invincibility = "/sound/07_Invincibility.mp3";
    public static final String lifeLost = "/sound/08_Life Lost.mp3";
    public static final String gameOver = "/sound/09_Game Over.mp3";
    public static final String ending = "/sound/10_Ending.mp3";
    public static final String explosion = "/sound/11_Explosion.mp3";
    public static final String powerUp = "/sound/12_PowerUp.mp3";

    public static ArrayList<AdvancedPlayer> soundPlayers = new ArrayList<>();
    public static void play(String fileName) {
        Thread thread = new Thread(() -> {
            try {
                URL url = Sound.class.getResource(fileName);
                AdvancedPlayer curPlayer = new AdvancedPlayer(url.openStream());
                soundPlayers.add(curPlayer);
                curPlayer.play();

            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(false); //Prevent the application to terminate before this Thread terminates
        thread.start(); //Start the current Thread
    }
    public static void closeAllPlayers() {
        for(AdvancedPlayer p : soundPlayers) {
            p.close();
        }
    }
    public static void playStageComplete() {
        closeAllPlayers();
        play(stageComplete);
    }
    public static void playStageTheme() {
        closeAllPlayers();
        play(stageTheme);
    }
    public static void playFindExit() {
        closeAllPlayers();
        play(findTheExit);
    }
    public static void playEnd() {
        closeAllPlayers();
        play(gameOver);
    }
}
