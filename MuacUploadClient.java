package com.kstechnologies.bmibeta;

import android.util.Log;

import com.Intven.StoreUtils;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by pparida on 12/3/17.
 */

public class MuacUploadClient
{
    private static final String ENDPOINT_URL = "http://ec2-54-196-239-128.compute-1.amazonaws.com/muac-api/v1.0/upload/id";
                                             // "http://fead.intvenlab.com/muac-api/v1.0/upload/id";
                                             // "http://ec2-54-196-239-128.compute-1.amazonaws.com/muac-api/v1.0/upload/id";
                                             //"https://www.example.com/api/v1.0/upload/id";
    private static final String PROTOCOL = "SSL";

    static void sendFilewithHTTP(String filePath)
    {
        //Set a global flag to check

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inStream = null;
        if(filePath == null || filePath.length() <3)
        {
            System.out.println("YES IT IS FOUND.");
            return;
        }

        String pathToOurFile = filePath;

        String urlServer = "http://ec2-54-196-239-128.compute-1.amazonaws.com/muac-api/v1.0/upload/id";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        FileInputStream fileInputStream = null;

        try
        {
            fileInputStream = new FileInputStream(new File(pathToOurFile) );

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            System.out.println("Connecting to: " + urlServer);

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            outputStream = new DataOutputStream( connection.getOutputStream() );
            //outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            //outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            //outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            //outputStream.writeBytes(lineEnd);
            //outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            System.out.println("PRINTING RESPONSE .." + serverResponseMessage);
            //System.out.println("File sent to: " + filePath, "Code: " + serverResponseCode + " Message: " + serverResponseMessage);


            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            //connection.disconnect();
        }
        catch (Exception ex)
        {
            System.out.println("File sent status Connection Failed");
            ex.printStackTrace();
            try
            {
                if(fileInputStream != null)
                    fileInputStream.close();
                if(connection != null)
                    connection.disconnect();
                if(outputStream != null)
                {
                    outputStream.flush();
                    outputStream.close();
                }

            }
            catch(Exception e)
            {

            }
        }

        try {
            inStream = new DataInputStream ( connection.getInputStream() );
            String str;

            while (( str = inStream.readLine()) != null)
            {
                System.out.println("Server response: " + str);
            }
            inStream.close();

        }
        catch (IOException ioex){

        }

        if(connection != null)
            connection.disconnect();


    } //send file.

    public static boolean uploadFile(String sourceFileUri) {
        int serverResponseCode = 0;
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);


            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(ENDPOINT_URL);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    Log.d("UPLOAD_Bytes", "Bytes = " + bytesRead);
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);


                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (Exception e) {

                Log.e("Upload_FILE", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode==HttpURLConnection.HTTP_OK;

    }

    /**
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static boolean upload(File file) throws Exception {
        boolean result = false;
        String s = new Scanner(file).useDelimiter("\\Z").next();
        //s = MuacDataCrypto.encrypt(s);
        URL url = new URL(ENDPOINT_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            //conn.setRequestProperty("Content-Type", "application/json");
            //conn.setRequestProperty("Accept", "application/json");
            //conn.setChunkedStreamingMode(0);

            Log.d("UPLOAD", "PPP00");
            OutputStream os = conn.getOutputStream();

            Log.d("UPLOAD", "PPP0");
            PrintWriter pw = new PrintWriter(os);

            Log.d("UPLOAD", "PPP1");
            pw.write(s);

            Log.d("UPLOAD", "PPP2");
            //Log.d("UPLOAD", "s = " + s.substring(s.length() - 10));
            //Files.copy(file.toPath, os); Or use apache commons IOUtils.copy
            os.flush();
            os.close();

            Log.d("UPLOAD", "PPP3");
            conn.connect();

            Log.d("UPLOAD", "PPP4");
            int responseCode = conn.getResponseCode();
            Log.d("UPLOAD", "Response = " + responseCode);
            return (responseCode == HttpURLConnection.HTTP_OK);
        }
        finally {
            conn.disconnect();
        }

        /*
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(ENDPOINT_URL);

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1, ContentType.APPLICATION_OCTET_STREAM);
            reqEntity.setChunked(true);
            // It may be more appropriate to use FileEntity class in this particular
            // instance but we are using a more generic InputStreamEntity to demonstrate
            // the capability to stream out data from any arbitrary source
            //
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");

            httppost.setEntity(reqEntity);

            Log.d("UPLOAD","Executing request: " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                StatusLine statusLine = response.getStatusLine();
                HttpEntity httpEntity = response.getEntity();
                Log.d("UPLOAD","----------------------------------------");
                Log.d("UPLOAD", statusLine.toString());
                Log.d("UPLOAD",EntityUtils.toString(httpEntity));
                if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    result = true;
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return result;
        */
    }

    /**
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static boolean uploadSSL(File file) throws Exception {
        String s = new Scanner(file).useDelimiter("\\Z").next();
        s = MuacDataCrypto.encrypt(s);

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance(PROTOCOL, MuacDataCrypto.SSL_PROVIDER);
        }
        catch (Exception e) {
            sslContext = SSLContext.getInstance(PROTOCOL);
        }
        sslContext.init(null, new TrustManager[]{getTM1()}, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        URL url = new URL(ENDPOINT_URL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        pw.write(s);
        //Files.copy(file.toPath, os); Or use apache commons IOUtils.copy
        os.flush();
        os.close();
        conn.connect();
        int responseCode = conn.getResponseCode();
        return (responseCode == HttpsURLConnection.HTTP_OK);
    }

    private static X509TrustManager getTM2() throws Exception {

        KeyStore ks = KeyStore.getInstance(MuacDataCrypto.KEYSTORE_TYPE);
        ks.load(new FileInputStream(StoreUtils.CSV_DIR_LOC + File.separator + MuacDataCrypto.KEYSTORE_FILE_NAME),
                MuacDataCrypto.KEYSTORE_PASSPHRASE.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(MuacDataCrypto.TMF_ALGORITHM);
        tmf.init(ks);
        TrustManager[] tms = tmf.getTrustManagers();

        X509TrustManager tm = null;

        for(int i=0; i<tms.length; i++) {
            if(tms[i] instanceof X509TrustManager) {
                tm = (X509TrustManager) tms[i];
                break;
            }
        }

        if(tm == null) {
            Log.e("MuacUploadClient", "getTM2() Could not initialize!");
            throw new Exception("getTM2(): Could not initialize");
        }
        return tm;
    }

    private static X509TrustManager getTM1() {
        X509TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        return tm;
    }
}
