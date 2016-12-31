package AsyncTask;

import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
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
import java.net.URL;

/**
 * Created by khanhngo on 12/31/16.
 */

public class editUserTask extends AsyncTask<String, Intent, String> {

    private String m_event_id;
    private JSONArray m_member_array;

    public interface OnEditMemberListener{
        void editMemberEventSuccess();

        void editMemberEventFail();
    }

    OnEditMemberListener mListener;

    public void setOnExitEventListener(OnEditMemberListener onEditMemberListener) {
        mListener = onEditMemberListener;
    }

    public editUserTask(String event_id, JSONArray arMemeber) {
        m_event_id = event_id;
        m_member_array = arMemeber;
    }

    @Override
    protected String doInBackground(String... params) {
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
            dataInsert.put("event_id", m_event_id);
            dataInsert.put("arMemberid", m_member_array);

            //initialize and config request , then connect the server.
            URL url = new URL(urlpath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000); //milliseconds
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoOutput(true); //enable output data
            urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
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
            if ((line = bufferedReader.readLine()) != null) {
                reSult.append(line);
            }

        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (bufferedReader != null) {
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
            String CODE = resultObject.getString("CODE");
            if (CODE.equals("SUCCESS")) {
                mListener.editMemberEventSuccess();
            } else {
                mListener.editMemberEventFail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

