package com.company;


import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        String path = getInput("Enter path to the file you want to alter: ");
        String pathOutput = getInput("Enter the name of the output file: ");
        double kLength = Double.parseDouble(getInput("Enter the coefficient: "));

        WaveFile waveFile = new WaveFile(path);
        waveFile.changeAudioLength(kLength);
        waveFile.fileRecording(pathOutput);
    }

     static String getInput(String message) throws NoSuchElementException {
         Scanner sc = new Scanner(System.in);
         System.out.print(message);
         return sc.nextLine();
     }
}

