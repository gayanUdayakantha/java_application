/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection_test_2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gayan.u
 */
public class ReadFile {

    static String SYNC_FILE = "";
    static String BACKUPPATH = "";
    static String THREAD_VALUE = "";
    static String STATION_NAME = "";
    static int count = 1;

    public static void main(String[] args) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("file/Sample.txt"));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {

                    if (count == 1) {

                        SYNC_FILE = line;
                    } else if (count == 2) {
                        BACKUPPATH = line;
                    } else if (count == 3) {
                        THREAD_VALUE = line;
                    }else if(count==4){
                        STATION_NAME=line;
                    }

                    count++;
                    line = br.readLine();
                }
                //  String everything = sb.toString();

                String[] SYNC_FILE_1 = SYNC_FILE.split("=");
                String[] BACKUPPATH_1 = BACKUPPATH.split("=");
                String[] THREAD_VALUE_1 = THREAD_VALUE.split("=");
                String[] STATION_NAME_1 = STATION_NAME.split("=");
                
                SYNC_FILE=SYNC_FILE_1[1];
                BACKUPPATH=BACKUPPATH_1[1];
                THREAD_VALUE=THREAD_VALUE_1[1];
                STATION_NAME=STATION_NAME_1[1];
                
                System.out.println("SYNC_FILE="+SYNC_FILE);
                System.out.println("BACKUPPATH="+BACKUPPATH);
                System.out.println("THREAD_VALUE="+THREAD_VALUE);
                System.out.println("STATION_NAME="+STATION_NAME);


            } catch (IOException ex) {
                Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
