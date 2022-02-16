package appServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {

    
    public String sendRequest() throws URISyntaxException, IOException {

        // basically builds corresponding GET request that will be returnd to
        // the front-end…
        String appKey = "9zy23106ps1zihe"; // get from AppConsole when create
                                           // the DropBox App
        String redirectURI = "http://localhost:8080/MyDBoxClient/"; // any url
                                                                    // to where
                                                                    // you want
                                                                    // to
                                                                    // redirect
                                                                    // the user
        URI uri = new URI("https://www.dropbox.com/oauth2/authorize");
        StringBuilder requestUri = new StringBuilder(uri.toString());
        requestUri.append("?client_id=");
        requestUri.append(URLEncoder.encode(appKey, "UTF-8"));
        requestUri.append("&response_type=code");
        requestUri.append("&redirect_uri=" + redirectURI.toString());
        
        return requestUri.toString();

    }


    public String accessToken(String codeStr) throws IOException {

        String code = "" + codeStr; // code get from previous step
        String appKey = "9zy23106ps1zihe"; // get from AppConsole when create
                                           // the DropBox App
        String appSecret = "x0if9e2fsjdis79"; // get from AppConsole when create
                                              // the DropBox App
        String redirectURI = "http://localhost:8080/MyDBoxClient/"; // any url
                                                                    // to where
                                                                    // you want
                                                                    // to
                                                                    // redirect
                                                                    // the user
        StringBuilder tokenUri = new StringBuilder("code=");
        tokenUri.append(URLEncoder.encode(code, "UTF-8"));
        tokenUri.append("&grant_type=");
        tokenUri.append(URLEncoder.encode("authorization_code", "UTF-8"));
        tokenUri.append("&client_id=");
        tokenUri.append(URLEncoder.encode(appKey, "UTF-8"));
        tokenUri.append("&client_secret=");
        tokenUri.append(URLEncoder.encode(appSecret, "UTF-8"));
        tokenUri.append("&redirect_uri=" + redirectURI);

        URL url = new URL("https://api.dropbox.com/oauth2/token");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",
                    "" + tokenUri.toString().length());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    connection.getOutputStream());
            outputStreamWriter.write(tokenUri.toString());
            outputStreamWriter.flush();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
            
        } finally {
            connection.disconnect();
        }
    }


    public String getAccountInfo(String tokenStr, String accountIDStr)
            throws IOException {
        
        String access_token = "" + tokenStr;
        String content = "{\"account_id\": \"" + accountIDStr + "\"}";
        URL url = new URL("https://api.dropboxapi.com/2/users/get_account");
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + access_token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length","" + content.length());
            
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

            outputStreamWriter.write(content);
            outputStreamWriter.flush();
            
            // In case the connection failed but server sent useful data (e.g. 400)
            InputStream is = connection.getErrorStream();
            if (is == null) {
                is = connection.getInputStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
            
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
            return null;
        }
        finally {
            connection.disconnect();
        }
    }


    public String uploadFile(String token, String path) throws IOException {
        
        String access_token = "" + token;
        String sourcePath = "" + path; // required file path on local file
                                       // system
        URL url = new URL("https://content.dropboxapi.com/2/files/upload");
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
        Path pathFile = Paths.get(sourcePath);
        byte[] data = Files.readAllBytes(pathFile);
        String content = "{\"path\":\"/MyDBoxClient_App01_files/images/image_initial_uploaded.png\",\"mode\":\"add\",\"autorename\": true,\"mute\": false,\"strict_conflict\":false}";

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization","Bearer " + access_token);
            connection.setRequestProperty("Content-Type","application/octet-stream");
            connection.setRequestProperty("Dropbox-API-Arg", "" + content);
            connection.setRequestProperty("Content-Length",String.valueOf(data.length));
            
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            
            InputStream is = connection.getErrorStream();
            if (is == null) {
                is = connection.getInputStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
            
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
            return null;
        }
        finally {
            connection.disconnect();
        }
    }


    /**
     * Deletes the given file from the DropBox folder
     * @param token access token
     * @param pathToDBFile path to the file
     * @return metadata about the deleted file at the time of deletion
     * @throws IOException
     */
    public String deleteFile(String token, String pathToDBFile) throws IOException {
        
        String access_token = "" + token;
        String filePath = "" + pathToDBFile; 
        
        URL url = new URL("https://api.dropboxapi.com/2/files/delete_v2");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String data = "{\"path\":\"" + filePath +  "\"}";
        
        try {
            
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + access_token);
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
            
            connection.getResponseCode();
            InputStream is = connection.getErrorStream();
            if (is == null) {
                is = connection.getInputStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuffer response = new StringBuffer();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return response.toString();
            
        }
        finally {
            connection.disconnect();
        }
    }

}
