package com.example;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {
    Clip musicClip;
    URL[] url = new URL[5];

    public Sound(){
        url[0] = getClass().getResource("tetrisSound.wav");
        url[1] = getClass().getResource("deleteLine.wav");
        url[2] = getClass().getResource("gamOver.wav");
        url[3] = getClass().getResource("rotate.wav");
        url[4] = getClass().getResource("down.wav");
    }

    public void play(int i, boolean music) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(url[i]);
            Clip clip = AudioSystem.getClip();
            if (music) {
                if (musicClip != null){
                    musicClip.stop();
                    musicClip.close();
                }
                musicClip = clip;
            }
            clip.open(ais);
            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });
            ais.close();
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loop(){
        if(musicClip != null) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    public void stop(){
        if (musicClip != null){
            musicClip.stop();
            musicClip.close();
        }
    }
}
