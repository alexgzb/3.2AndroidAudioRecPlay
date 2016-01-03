package com.gezelbom.app32;

import android.app.Fragment;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fragment used to record audio
 */
public class RecordFragment extends Fragment {
    private static final String TAG = "AudioRecorder";
    private static final int MEDIA_TYPE_AUDIO = 0;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;

    private Button recordButton = null;
    private MediaRecorder recorder = null;
    private Button stopButton = null;
    private Chronometer timer = null;

    public RecordFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record, container, false);
        timer = (Chronometer) v.findViewById(R.id.chronometer);
        recordButton = (Button) v.findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
                recordButton.setEnabled(false);
                stopButton.setEnabled(true);
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
            }
        });
        stopButton = (Button) v.findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                stopButton.setEnabled(false);
                recordButton.setEnabled(true);
                timer.stop();
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }


    /**
     * Create a new MediaRecorder instance and adjust settings and start recording
     */
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        String fileName = getOutputMediaFileString(MEDIA_TYPE_AUDIO);
        recorder.setOutputFile(fileName);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
            Log.e(TAG, e.toString());
        }
        recorder.start();

    }

    /**
     * Method to stop recording and releasing the instance of the MediaRecorder
     */
    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private static String getOutputMediaFileString(int type) {
        return getOutputMediaFile(type).toString();
    }

    /**
     * Method that can be used to get a URI instead of File given a type
     * @param type The type to use when setting the file name
     * @return The Uri of the file that is used for the audio recording
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * A Dynamic method that takes a type of file (AUDIO|IMAGE|VIDEO) and creates a suitable filename
     * with date and timestamps etc.
     *
     * @param type The type of file to create
     *
     * @return
     */
    private static File getOutputMediaFile(int type) {

        // First check that the External storage is mounted and read/Write access
        // is available
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment
                .getExternalStorageState())) {

            File mediaDir = null;
            if (type == MEDIA_TYPE_AUDIO ) {
                mediaDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                        "MyApp");
            }else if (type == MEDIA_TYPE_IMAGE) {
                mediaDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "MyApp");
            }else if (type == MEDIA_TYPE_VIDEO) {
                mediaDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                        "MyApp");
            }

            //Log.d(TAG, "Media directory: " + mediaDir);

            // Create the storage directory if it does not exist
            if (!mediaDir.exists()) {
                Log.d(TAG, "Media dir did not exist, creating it");
                if (!mediaDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss",Locale.getDefault()).format(new Date());
            //String timeStamp = new SimpleDateFormat("yyyyMMdd",Locale.getDefault()).format(new Date());
            File mediaFile = null;

            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaDir.getAbsolutePath() + File.separator
                        + "IMG_" + timeStamp + ".jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaDir.getAbsolutePath() + File.separator
                        + "VID_" + timeStamp + ".mp4");
            } else if (type == MEDIA_TYPE_AUDIO) {
                mediaFile = new File(mediaDir.getAbsolutePath() + File.separator
                        + "AUD_" + timeStamp + ".3gp");
            }
            Log.d(TAG, "Filename: " + mediaFile);
            return mediaFile;

        }
        return null;

    }

}
