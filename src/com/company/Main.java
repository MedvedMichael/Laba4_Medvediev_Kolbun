package com.company;

public class Main {

    public static void main(String[] args) {

        WaveFile waveFile = new WaveFile("input.wav");

        waveFile.changeAudioLength(1.5);
        waveFile.fileRecording("test.wav");
    }
}

