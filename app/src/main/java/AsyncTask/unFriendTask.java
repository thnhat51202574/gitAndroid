package AsyncTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import Model.Event;

/**
 * Created by nhatth on 12/24/16.
 */

public class unFriendTask extends AsyncTask<String, Intent, String> {

    private String m_id, mfriend_id;
    public interface OnUnfriendListener{
        void unfriendSuccess();
        void unfriendFail();
    }
    OnUnfriendListener mListener;
    public void setOnUnfriendListener(OnUnfriendListener onUnfriendListener) {
        mListener = onUnfriendListener;
    }
    public unFriendTask(String my_id, String friendId) {
        m_id = my_id;
        mfriend_id = friendId;
    }

    @Override
    protected String doInBackground(String... params)
        {
            try {
                return unfriend(params[0]);
            } catch (IOException ex) {
               return "Network error!";
            } catch (JSONException e) {
                return "Network error!";
            }
        }


    private String unfriend(String urlpath) throws IOException, JSONException {
        StringBuilder reSult = new StringBuilder();
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;

        try {
            JSONObject dataInsert = new JSONObject();
            //create JSONdata to send to server
            dataInsert.put("_id",m_id);
            dataInsert.put("friend_id",mfriend_id);

            //initialize and config request , then connect the server.
            URL url = new URL(urlpath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000); //milliseconds
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoOutput(true); //enable output data
            urlConnection.setRequestProperty("Content-Type","application/json"); //set header
            urlConnection.connect();

            //write data to server
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(dataInsert.toString());
            bufferedWriter.flush();

            //read data response from server

            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            if((line = bufferedReader.readLine())!=null) {
                reSult.append(line);
            }

        } finally {
            if(bufferedWriter !=null) {
                bufferedWriter.close();
            }
            if(bufferedReader !=null) {
                bufferedReader.close();
            }
        }

        return reSult.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject resultObject = new JSONObject(result);
            String  CODE = resultObject.getString("CODE");
            if (CODE.equals("SUCCESS")) {
                mListener.unfriendSuccess();
            } else {
                mListener.unfriendFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}