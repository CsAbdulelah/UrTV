package sa.urtv.home;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

/** Singleton Quran player. Uses native MediaPlayer so no browser autoplay restriction. */
public final class QuranPlayer {
    private static QuranPlayer instance;
    private MediaPlayer mp;
    private String currentReciter = "";
    private int currentSurah = 0;
    private Runnable onState;

    public static synchronized QuranPlayer get() {
        if (instance == null) instance = new QuranPlayer();
        return instance;
    }
    private QuranPlayer() {}

    public void setOnStateChanged(Runnable r) { this.onState = r; }

    public boolean isPlaying() { return mp != null && mp.isPlaying(); }
    public String reciterId() { return currentReciter; }
    public int surah() { return currentSurah; }

    public static String urlFor(String reciterId, int surah) {
        return "https://cdn.mp3quran.net/audio/" + reciterId + "/r1/"
                + String.format("%03d", surah) + ".mp3";
    }

    public void play(String reciterId, int surah) {
        stop();
        currentReciter = reciterId;
        currentSurah = surah;
        try {
            mp = new MediaPlayer();
            mp.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            mp.setDataSource(urlFor(reciterId, surah));
            mp.setOnPreparedListener(p -> { p.start(); if (onState != null) onState.run(); });
            mp.setOnErrorListener((p, w, e) -> { stop(); return true; });
            mp.setOnCompletionListener(p -> stop());
            mp.prepareAsync();
        } catch (Exception e) {
            stop();
        }
    }

    public void stop() {
        if (mp != null) {
            try { if (mp.isPlaying()) mp.stop(); } catch (Exception ignored) {}
            try { mp.release(); } catch (Exception ignored) {}
            mp = null;
        }
        if (onState != null) onState.run();
    }
}
