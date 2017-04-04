package com.skmapstutorial.Application.Services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import com.skmapstutorial.Application.Model.Building;
import com.skmapstutorial.Application.SKMapsApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadService extends Service {
    private long downloadReference;
    private DownloadManager downloadManager;
    String _zipFile;
    String _location;
    String path;
    long startTime;
    long endTime;
    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> URLs = new ArrayList<>();
    static int filesDownloaded =0;

    String server = "http://198.209.246.84:8080/CampusCruiseServer/FileServlet?";
    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Download service started");
        fileNames.clear();
        fileNames.add(SKMapsApplication.getUniversity().getUniversityName().replace(" ",""));
     for(Building b: SKMapsApplication.getUniversity().getBuildings()){
       if(b.isHasData()) {
           System.out.println(b.getBuildingName() + " is aded to fileNames");
           fileNames.add(b.getBuildingName().replace(" ", ""));
       }
     }
        System.out.println("Files To be downloaded "+ fileNames.size());
        for(int i=0;i<fileNames.size();i++){
            startDownload(fileNames.get(i));
        }


        return START_STICKY;
    }


    public void startDownload(String fileName){
        System.out.println("Start Download Started");
        String url = server+"university="+ SKMapsApplication.getUniversity().getUniversityName().replace(" ","")+"&file="+fileName;
        if (!isFilePresent(fileName)) {
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

            //Restrict the types of networks over which this download may proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            //Set whether this download may proceed over a roaming connection.
            request.setAllowedOverRoaming(false);
            //Set the title of this download, to be displayed in notifications (if enabled).
            request.setTitle("My Data Download");
            //Set a description of this download, to be displayed in notifications (if enabled)
            request.setDescription("Android Data download using DownloadManager.");
            //Set the local destination for the downloaded file to a path within the application's external files directory

            request.setDestinationInExternalFilesDir(getApplicationContext(),"/", fileName+".zip");
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(downloadReceiver, filter);
            //Enqueue a new download and same the referenceId
            downloadReference = downloadManager.enqueue(request);



        } else {
            //Toast.makeText(DownloadService.this, "File is already There", //Toast.LENGTH_SHORT).show();
            filesDownloaded++;
            System.out.println("Files present / downloaded "+filesDownloaded);
            System.out.println("Files downloaded: "+fileNames.size());
            if(filesDownloaded==fileNames.size()){
                //Toast.makeText(DownloadService.this, "All Files Downloaded, unzipping Now", //Toast.LENGTH_SHORT).show();
                System.out.println("So now we have downloaded all files now lets unzip them");
                startUnZipping();
            }
        }

    }

   public void startDownload(String fileName,String url){
       System.out.println("Start Download Started");
       if (!isFilePresent(fileName)) {
           downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
           Uri Download_Uri = Uri.parse(url);
           DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

           //Restrict the types of networks over which this download may proceed.
           request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
           //Set whether this download may proceed over a roaming connection.
           request.setAllowedOverRoaming(false);
           //Set the title of this download, to be displayed in notifications (if enabled).
           request.setTitle("My Data Download");
           //Set a description of this download, to be displayed in notifications (if enabled)
           request.setDescription("Android Data download using DownloadManager.");
           //Set the local destination for the downloaded file to a path within the application's external files directory

           request.setDestinationInExternalFilesDir(getApplicationContext(),"/", fileName);
           IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
           registerReceiver(downloadReceiver, filter);
           //Enqueue a new download and same the referenceId
           downloadReference = downloadManager.enqueue(request);



       } else {
           //Toast.makeText(DownloadService.this, "File is already There", //Toast.LENGTH_SHORT).show();
            filesDownloaded++;
           System.out.println(filesDownloaded);
           if(filesDownloaded==fileNames.size()){
               //Toast.makeText(DownloadService.this, "All Files Downloaded, unzipping Now", //Toast.LENGTH_SHORT).show();
               startUnZipping();
           }
       }

    }
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadReference == referenceId) {
                filesDownloaded++;
                if(filesDownloaded==fileNames.size()){
                    //Toast.makeText(DownloadService.this, "All Files Downloaded, unzipping Now", //Toast.LENGTH_SHORT).show();
                    startUnZipping();
                }
            }
        }
    };
    public boolean isFilePresent(String fileName) {

        String path = getApplicationContext().getExternalFilesDir(null) + "/" + fileName+".zip";
        File file = new File(path);
        System.out.println(fileName+ " does it exist lets find out: checking...: file is present?" + file.exists());

        return file.exists();
    }

    public void startUnZipping(){
        System.out.println("Unzipping Started");
        //Toast.makeText(DownloadService.this, "unzipping Now", //Toast.LENGTH_SHORT).show();
        for(int i=0;i<fileNames.size();i++) {

            if(isFilePresent(fileNames.get(i))) {
                checkAndmakeDirs(fileNames.get(i));
                System.out.println(fileNames.get(i)+ " Is present and unzipping it now");
                //Toast.makeText(DownloadService.this, "Download Completed", //Toast.LENGTH_SHORT).show();
                _zipFile = getApplicationContext().getExternalFilesDir(null) + "/" + fileNames.get(i)+".zip";
                _location = getApplicationContext().getExternalFilesDir(null) + "/" + fileNames.get(i) + "/";



                path = getApplicationContext().getExternalFilesDir(null).getPath();
               System.out.println("Path: " + path);
                File directory = new File(path);
                File[] files = directory.listFiles();
               System.out.println("Size: " + files.length);
                for (int j = 0; j < files.length; j++) {
                   System.out.println("FileName:" + files[j].getName());
                }

                // unzip();
                unzipFaster();
            }
            }

    }
    public void unzipFaster() {

        try {
            FileInputStream inputStream = new FileInputStream(_zipFile);
            ZipInputStream zipStream = new ZipInputStream(inputStream);
            ZipEntry zEntry = null;

            startTime = System.currentTimeMillis();
            System.out.println("Uni Zipping Started :--->"+ startTime);

            while ((zEntry = zipStream.getNextEntry()) != null) {

                if (zEntry.isDirectory()) {
                    hanldeDirectory(zEntry.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_location + zEntry.getName());
                    BufferedOutputStream bufout = new BufferedOutputStream(fout);
                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while ((read = zipStream.read(buffer)) != -1) {
                        bufout.write(buffer, 0, read);
                    }

                    zipStream.closeEntry();
                    bufout.close();
                    fout.close();
                }
            }
            zipStream.close();
            endTime = System.currentTimeMillis();
            System.out.println("Un Zipping Finished : ------->"+endTime);
            System.out.println("Total Un Zip Time: "+(endTime - startTime) + " milli seconds");

        } catch (Exception e) {
            System.out.println("Unzipping failed");
            e.printStackTrace();
        }

    }

    public void hanldeDirectory(String dir) {
        File f = new File(_location+ dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
    public void checkAndmakeDirs(String directoryName){
        System.out.println("Entered checkAndMakeDirs");
        File file = new File(getApplicationContext().getExternalFilesDir(null)+ "/"+directoryName+"/");
        System.out.println("Checking for the directory: "+ directoryName +" is there? "+ file.exists());
        System.out.println(" I'm Checking : "+file.getAbsolutePath());
        System.out.println(" is it a Dir? "+ file.isDirectory());
        if(!file.exists()){
            System.out.println("Making the directories");
            file.mkdir();
            System.out.println(file.getPath()+" Created");
            (new File(getApplicationContext().getExternalFilesDir(null)+ "/"+directoryName+"/"+"Audio/")).mkdir();
            System.out.println(getApplicationContext().getExternalFilesDir(null)+ "/"+directoryName+"/Audio/"+" Created");
            (new File(getApplicationContext().getExternalFilesDir(null)+ "/"+directoryName+"/Video/")).mkdir();
            System.out.println(getApplicationContext().getExternalFilesDir(null)+ "/"+directoryName+"/Video/"+" Created");
            (new File(getApplicationContext().getExternalFilesDir(null)+ "/"+directoryName+"/Images/")).mkdir();
            System.out.println(getApplicationContext().getExternalFilesDir(null)+ "/"+directoryName+"/Images/"+" Created");
        }
    }
}
