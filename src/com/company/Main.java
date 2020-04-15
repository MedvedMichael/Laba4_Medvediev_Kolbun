package com.company;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

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
         String str = sc.nextLine();
         return str;
     }
}

