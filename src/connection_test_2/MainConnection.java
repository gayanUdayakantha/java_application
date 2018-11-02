/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainConnection.java
 *
 * Created on Jun 11, 2018, 11:09:02 AM
 */
package connection_test_2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author gayan.u
 */
public class MainConnection extends javax.swing.JFrame {

    //private static final String FILENAME_SYNC = "C:/ProgramData/Livestock Exchange/StockITSyncPPC/Setting.xml";
    // private static final String FILENAME_SYNC = "D:/Dev/ConnectionTest/pt/Setting.xml";
    //   private static final String BACKUPPATH = "C:/StockITBackup";
    //   private static final String FILENAME = "C:/ProgramData/Livestock Exchange/StockITSyncPPC/Setting.xml";
    static String serverInstance;
    static String clientInstance;
    static String serverDbName;
    static String clientDbName;
    static String fInstance = "";
    static String ServerSQLInstance_full = "";
    static String ServerDatabaseName_full = "";
    static String ClientSQLInstance_full = "";
    static String ClientDatabaseName_full = "";
    static String CommandTimeout;
    static String ConnectionTimeout;
    static String ServerDatabaseBackupPath;
    //   int tValue = 10;
    // static String[] deleteBackupFiles={};
    ArrayList<String> deleteBackupFiles = new ArrayList<String>();
    static String SYNC_FILE = "";
    static String BACKUPPATH = "";
    static String THREAD_VALUE = "10";
    static String STATION_NAME = "";
    static int count = 1;

    /** Creates new form MainConnection */
    public MainConnection() {
        initComponents();
        setTitle("Connection Test");

        getFilePathDetails();

        redjLabel13.setEnabled(false);
        greenjLabel14.setEnabled(false);

        threadshjLabel16.setText(THREAD_VALUE + " GB");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {



                loadConn();

                boolean checkPri = isAdmin();
                if (checkPri) {
                    adminprivillagejLabel9.setText("Yes");
                } else {
                    adminprivillagejLabel9.setText("No");
                }

                //checkDiskDpaces();
                diskSpace();

            }
        }, 0, 2000);



    }

    public static String getSize(File file) {
        double size = FileUtils.sizeOfDirectory(new File(BACKUPPATH));
        System.out.println("Size: " + size + " bytes");

        DecimalFormat df2 = new DecimalFormat(".##");
        double size2 = size / 1024 / 1024 / 1024;

        String sas = String.format("%.2f", size2);
        return sas;


    }

    void diskSpace() {
        File file = new File("c:");
        long totalSpace = file.getTotalSpace(); //total disk space in bytes.
        long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
        long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.

        //  System.out.println(" === Partition Detail ===");

        //    System.out.println(" === mega bytes ===");
        //    System.out.println("Total size : " + totalSpace / 1024 / 1024 / 1024 + " GB");
        //    System.out.println("Space free : " + freeSpace / 1024 / 1024 / 1024 + " GB");

        double totalSize = totalSpace / 1024 / 1024 / 1024;
        double usableSize = usableSpace / 1024 / 1024 / 1024;
        double freeSize = freeSpace / 1024 / 1024 / 1024;

        totaljLabel13.setText(totalSize + " GB");
        freejLabel15.setText(freeSize + " GB");

        File fd = new File(BACKUPPATH);

        stockitjLabel17.setText(getSize(fd) + " GB");
//freeSize=9;
//        String[] fileSync = getFilePathDetails();
        double tValue = Double.parseDouble(THREAD_VALUE);

        // 429  < 11
        if (freeSize < tValue) {
            redjLabel13.setEnabled(true);
            greenjLabel14.setEnabled(false);
            //    JOptionPane.showMessageDialog(this, "please contact the Service Desk to remove the old backup");

            String ss = getLatestFilefromDir(BACKUPPATH).getName();
            deleteBackupFiles.add(ss);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

            // System.out.println("After Format : " + sdf.format(file.lastModified()));
            System.out.println("Deleted File Name ================== " + ss + " /////last Modifed date ===== " + sdf.format(getLatestFilefromDir(BACKUPPATH).lastModified()));
            //deleteBackupFiles[1]=getLatestFilefromDir(BACKUPPATH).getName();
            //    deleteBackupFiles.add(getLatestFilefromDir(BACKUPPATH).getName());

            getLatestFilefromDir(BACKUPPATH).delete();

            delete_file_days(5, ".bak");

            deleteBackupFiles.add(getLatestFilefromDir(BACKUPPATH).getName());
            getLatestFilefromDir(BACKUPPATH).delete();

            deleteBackupFiles.add(getLatestFilefromDir(BACKUPPATH).getName());
            getLatestFilefromDir(BACKUPPATH).delete();

            deleteBackupFiles.add(getLatestFilefromDir(BACKUPPATH).getName());
            getLatestFilefromDir(BACKUPPATH).delete();

            send_mail("gudayakantha@aaco.com.au", STATION_NAME, deleteBackupFiles);

            deleteBackupFiles.clear();

        } else {
            redjLabel13.setEnabled(false);
            greenjLabel14.setEnabled(true);
        }
    }

    public void delete_file_days(long days, String fileExtension) {

        File folder = new File(BACKUPPATH);

        if (folder.exists()) {

            File[] listFiles = folder.listFiles();

            long eligibleForDeletion = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L);

            for (File listFile : listFiles) {

                if (listFile.getName().endsWith(fileExtension)
                        && listFile.lastModified() < eligibleForDeletion) {

                    deleteBackupFiles.add(listFile.getName());

                    if (!listFile.delete()) {

                        System.out.println("Sorry Unable to Delete Files..");

                    }
                }
            }
        }
    }

    private File getLatestFilefromDir(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() > files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

    static void checkDiskDpaces() {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                //      System.out.println("dddddddddd");

                File file = new File("c:");
                long totalSpace = file.getTotalSpace(); //total disk space in bytes.
                long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
                long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.

                //         System.out.println(" === Partition Detail ===");

                //         System.out.println(" === mega bytes ===");
                //         System.out.println("Total size : " + totalSpace / 1024 / 1024 / 1024 + " GB");
                //         System.out.println("Space free : " + freeSpace / 1024 / 1024 / 1024 + " GB");

                double totalSize = totalSpace / 1024 / 1024 / 1024;
                double usableSize = usableSpace / 1024 / 1024 / 1024;
                double freeSize = freeSpace / 1024 / 1024 / 1024;

                totaljLabel13.setText(totalSize + " GB");
                freejLabel15.setText(freeSize + " GB");



                if (freeSize < 5) {
                    redjLabel13.setEnabled(true);
                    greenjLabel14.setEnabled(false);
                } else {
                    redjLabel13.setEnabled(false);
                    greenjLabel14.setEnabled(true);
                }
            }
        }, 0, 1000);
    }

    public static boolean isAdmin() {
        String groups[] =
                (new com.sun.security.auth.module.NTSystem()).getGroupIDs();
        for (String group : groups) {
            if (group.equals("S-1-5-32-544")) {
                return true;
            }
        }
        return false;
    }

    void loadConn() {

        BufferedReader br = null;
        FileReader fr = null;

        try {

            //  String[] fileSync = getFilePathDetails();

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(SYNC_FILE);
            br = new BufferedReader(fr);

            String sCurrentLine;
            int lineCount = 0;

            while ((sCurrentLine = br.readLine()) != null) {

                lineCount++;

                if (lineCount == 28) {
                    //        System.out.println(sCurrentLine);
                    ServerSQLInstance_full = sCurrentLine;
                }

                if (lineCount == 29) {
                    //     System.out.println(sCurrentLine);
                    ServerDatabaseName_full = sCurrentLine;
                }

                if (lineCount == 32) {
                    //     System.out.println(sCurrentLine);
                    ServerDatabaseBackupPath = sCurrentLine;
                }


                if (lineCount == 35) {
                    //    System.out.println(sCurrentLine);
                    ClientSQLInstance_full = sCurrentLine;
                }

                if (lineCount == 36) {
                    //     System.out.println(sCurrentLine);
                    ClientDatabaseName_full = sCurrentLine;
                }

                if (lineCount == 46) {
                    //   System.out.println(sCurrentLine);
                    CommandTimeout = sCurrentLine;
                }

                if (lineCount == 47) {
                    //    System.out.println(sCurrentLine);
                    ConnectionTimeout = sCurrentLine;
                }
            }

            String loggeduser = System.getProperty("user.name");
            loguserjLabel8.setText(loggeduser);


            String[] ServerDatabase_arr = ServerDatabaseName_full.split("=");
            // System.out.println("............. " + ServerDatabase_arr[1]);
            serverDbName = ServerDatabase_arr[1].substring(1, ServerDatabase_arr[1].length() - 4);
            //     System.out.println("================= " + serverDbName);
            serverdbjLabel8.setText(serverDbName);

            String[] ServerSQLInstance_arr = ServerSQLInstance_full.split("=");
            //  System.out.println("............. " + ServerSQLInstance_arr[1]);
            serverInstance = ServerSQLInstance_arr[1].substring(1, ServerSQLInstance_arr[1].length() - 4);
            System.out.println("================= " + serverInstance);
            serverintancejLabel7.setText(serverInstance);

            String[] ClientSQLInstance_arr = ClientSQLInstance_full.split("=");
            //  System.out.println("............. " + ServerSQLInstance_arr[1]);
            clientInstance = ClientSQLInstance_arr[1].substring(1, ClientSQLInstance_arr[1].length() - 4);
            //      System.out.println("================= " + clientInstance);
            clientintancejLabel9.setText(clientInstance);

            String[] ClientDatabaseName_arr = ClientDatabaseName_full.split("=");
            //  System.out.println("............. " + ServerSQLInstance_arr[1]);
            clientDbName = ClientDatabaseName_arr[1].substring(1, ClientDatabaseName_arr[1].length() - 4);
            //        System.out.println("================= " + clientDbName);
            clientdbLabel10.setText(clientDbName);

            String[] ServerDatabaseBackupPath_arr = ServerDatabaseBackupPath.split("=");
            //  System.out.println("............. " + ServerSQLInstance_arr[1]);
            ServerDatabaseBackupPath = ServerDatabaseBackupPath_arr[1].substring(1, ServerDatabaseBackupPath_arr[1].length() - 4);
            //       System.out.println("================= " + ServerDatabaseBackupPath);



            String[] CommandTimeout_arr = CommandTimeout.split("=");
            //  System.out.println("............. " + ServerSQLInstance_arr[1]);
            CommandTimeout = CommandTimeout_arr[1].substring(1, CommandTimeout_arr[1].length() - 4);
            //        System.out.println("================= " + CommandTimeout);
            commandtimeLabel11.setText(CommandTimeout);

            String[] ConnectionTimeout_arr = ConnectionTimeout.split("=");
            //  System.out.println("............. " + ServerSQLInstance_arr[1]);
            ConnectionTimeout = ConnectionTimeout_arr[1].substring(1, ConnectionTimeout_arr[1].length() - 4);
            //      System.out.println("================= " + ConnectionTimeout);
            connectiontimeLabel12.setText(ConnectionTimeout);




            //  Runtime.getRuntime().exec(new String[] {"cmd", "/K", "Start"});

//            String ds = "\\] [";
//            String[] ddd = serverInstance.split(ds);
//            System.out.println("44444444444444444 " + ddd[0]);



        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null) {
                    br.close();
                }

                if (fr != null) {
                    fr.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }



    }

    void send_mail(String toMail, String stationName, ArrayList<String> fileNameList) {


        final String username = "grudayakantha@gmail.com";
        final String password = "gudayakantha";

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("grudayakantha@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toMail));
            message.setSubject("Backup Files Deleted - " + stationName);


            String fullMsg = " Please Find View The Deleted Backups From The - " + stationName + " \n \n";

            int count = 1;


            for (String string : fileNameList) {
                fullMsg += count + "). " + string + " \n ";
                count++;
            }

            fullMsg += " \n \n";

            fullMsg += "Confidentiality Notice: This e-mail and any attachment are confidential "
                    + "and may be privileged and are intended only for the authorised recipients of the sender. "
                    + "The information contained in this e-mail and any attachment(s) must not be published, "
                    + "copied, disclosed, or transmitted in any form to any person or entity unless expressly "
                    + "authorised by the sender. If you have received this e-mail in error you are requested to delete "
                    + "it immediately and advise the sender by return e-mail";

            message.setText(fullMsg);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    String[] getFilePathDetails() {

        String[] filePaths = new String[5];


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
                    } else if (count == 4) {
                        STATION_NAME = line;
                    }

                    count++;
                    line = br.readLine();
                }
                //  String everything = sb.toString();

                String[] SYNC_FILE_1 = SYNC_FILE.split("=");
                String[] BACKUPPATH_1 = BACKUPPATH.split("=");
                String[] THREAD_VALUE_1 = THREAD_VALUE.split("=");
                String[] STATION_NAME_1 = STATION_NAME.split("=");

                SYNC_FILE = SYNC_FILE_1[1];
                BACKUPPATH = BACKUPPATH_1[1];
                THREAD_VALUE = THREAD_VALUE_1[1];
                STATION_NAME = STATION_NAME_1[1];


                System.out.println("SYNC_FILE===" + SYNC_FILE);
                System.out.println("BACKUPPATH===" + BACKUPPATH);
                System.out.println("THREAD_VALUE===" + THREAD_VALUE);
                System.out.println("STATION_NAME====" + STATION_NAME);


                filePaths[0] = SYNC_FILE_1[1];
                filePaths[1] = BACKUPPATH_1[1];
                filePaths[2] = THREAD_VALUE_1[1];
                filePaths[3] = STATION_NAME_1[1];



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


        return filePaths;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        serverintancejLabel7 = new javax.swing.JLabel();
        serverdbjLabel8 = new javax.swing.JLabel();
        clientintancejLabel9 = new javax.swing.JLabel();
        clientdbLabel10 = new javax.swing.JLabel();
        commandtimeLabel11 = new javax.swing.JLabel();
        connectiontimeLabel12 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        loguserjLabel8 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        adminprivillagejLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Server Instance");

        jLabel2.setText("Server DB name");

        jLabel3.setText("Client Instance");

        jLabel4.setText("Client DB Name");

        jLabel5.setText("CommandTimeout");

        jLabel6.setText("ConnectionTimeout");

        jButton1.setText("Connection Check");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        serverintancejLabel7.setText("Server Instance");

        serverdbjLabel8.setText("Server Instance");

        clientintancejLabel9.setText("Server Instance");

        clientdbLabel10.setText("Server Instance");

        commandtimeLabel11.setText("Server Instance");

        connectiontimeLabel12.setText("Server Instance");

        jButton2.setText("Server Check");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Client Check");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel7.setText("Logged User");

        loguserjLabel8.setText("Logged User");

        jLabel8.setText("Admin Privileges");

        adminprivillagejLabel9.setText("Admin Privileges");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setText("Disk Spaces");

        jLabel10.setText("Drive");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel11.setText("C");

        jLabel12.setText("Total GB");

        totaljLabel13.setFont(new java.awt.Font("Tahoma", 1, 14));
        totaljLabel13.setText("Total GB");

        jLabel14.setText("Free GB");

        freejLabel15.setFont(new java.awt.Font("Tahoma", 1, 14));
        freejLabel15.setText("Free GB");

        redjLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/red.png"))); // NOI18N

        greenjLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/green.png"))); // NOI18N

        jLabel15.setText("Threshold ");

        threadshjLabel16.setFont(new java.awt.Font("Tahoma", 1, 14));
        threadshjLabel16.setText("T Value");

        jButton4.setText("Remove Last File");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel16.setText("C:/StockITBackup");

        stockitjLabel17.setFont(new java.awt.Font("Tahoma", 1, 14));
        stockitjLabel17.setText("T Value");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel10)
                                .addComponent(jLabel12))
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stockitjLabel17)
                            .addComponent(threadshjLabel16)
                            .addComponent(totaljLabel13)
                            .addComponent(jLabel11)
                            .addComponent(freejLabel15))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(redjLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(greenjLabel14)
                        .addGap(98, 98, 98))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jButton4)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(greenjLabel14)
                    .addComponent(redjLabel13)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(totaljLabel13))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(freejLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(stockitjLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(threadshjLabel16)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clientintancejLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(clientdbLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(commandtimeLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(connectiontimeLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(serverintancejLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(serverdbjLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7))
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(adminprivillagejLabel9)
                            .addComponent(loguserjLabel8))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton2)
                            .addComponent(jButton1)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel7))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(adminprivillagejLabel9)
                                .addGap(4, 4, 4)
                                .addComponent(loguserjLabel8)))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(serverintancejLabel7)
                                .addGap(4, 4, 4)
                                .addComponent(serverdbjLabel8)))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(14, 14, 14)
                                .addComponent(jLabel4)
                                .addGap(14, 14, 14)
                                .addComponent(jLabel5)
                                .addGap(14, 14, 14)
                                .addComponent(jLabel6))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(clientintancejLabel9)
                                .addGap(14, 14, 14)
                                .addComponent(clientdbLabel10)
                                .addGap(14, 14, 14)
                                .addComponent(commandtimeLabel11)
                                .addGap(14, 14, 14)
                                .addComponent(connectiontimeLabel12)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    try {
        Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"dir && ping localhost -t\"");
    } catch (IOException ex) {
        Logger.getLogger(MainConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton1ActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
//    try {
//        Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"dir && ping " + clientInstance + " -t\"");
//    } catch (IOException ex) {
//        Logger.getLogger(MainConnection.class.getName()).log(Level.SEVERE, null, ex);
//    }




    try {
        String ipAddress = "127.0.0.1";
        InetAddress inet = InetAddress.getByName(ipAddress);
        System.out.println("Sending Ping Request to " + ipAddress);
        if (inet.isReachable(5000)) {
            System.out.println(ipAddress + " is reachable.");

        } else {
            System.out.println(ipAddress + " NOT reachable.");
        }
    } catch (Exception e) {
        System.out.println("Exception:" + e.getMessage());
    }

}//GEN-LAST:event_jButton2ActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    try {
        Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"dir && ping " + serverInstance + " -t\"");
    } catch (IOException ex) {
        Logger.getLogger(MainConnection.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_jButton3ActionPerformed

private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

    String lastFile = getLatestFilefromDir(BACKUPPATH).getName();
    long time = getLatestFilefromDir(BACKUPPATH).lastModified();

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    String ldate = sdf.format(getLatestFilefromDir(BACKUPPATH).lastModified());

    int confirm = JOptionPane.showConfirmDialog(this, "File Name : " + lastFile + " // Last Modified Date :" + ldate, "Confirm Last File Details", JOptionPane.YES_NO_OPTION);

    if (confirm == 0) {
        getLatestFilefromDir(BACKUPPATH).delete();
        System.out.println("sssssssssssssssssssssssssssssssssss");
        JOptionPane.showMessageDialog(this, "The File Has Been Deleted");

    } else {
    }


}//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainConnection().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adminprivillagejLabel9;
    private javax.swing.JLabel clientdbLabel10;
    private javax.swing.JLabel clientintancejLabel9;
    private javax.swing.JLabel commandtimeLabel11;
    private javax.swing.JLabel connectiontimeLabel12;
    private static final javax.swing.JLabel freejLabel15 = new javax.swing.JLabel();
    private static final javax.swing.JLabel greenjLabel14 = new javax.swing.JLabel();
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel loguserjLabel8;
    private static final javax.swing.JLabel redjLabel13 = new javax.swing.JLabel();
    private javax.swing.JLabel serverdbjLabel8;
    private javax.swing.JLabel serverintancejLabel7;
    private static final javax.swing.JLabel stockitjLabel17 = new javax.swing.JLabel();
    private static final javax.swing.JLabel threadshjLabel16 = new javax.swing.JLabel();
    private static final javax.swing.JLabel totaljLabel13 = new javax.swing.JLabel();
    // End of variables declaration//GEN-END:variables
}
