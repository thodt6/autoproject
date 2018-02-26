package com.unibro.utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 *
 * @author NGUYEN DUC THO
 */
class SyncPipe implements Runnable {

    InputStream iStream;
    PrintStream oStream;

    public SyncPipe(InputStream inStream, PrintStream outStream) {
        iStream = inStream;
        oStream = outStream;
    }

    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(iStream));
            String line = null;
            while ((line = input.readLine()) != null) {
                //System.out.println(line);
                oStream.print(line);
            }
        } catch (IOException ex) {
            oStream.print(ex);
        }
    }

}
