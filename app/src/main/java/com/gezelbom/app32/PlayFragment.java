package com.gezelbom.app32;

import android.app.ListFragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to play previously recorded audio files
 */
public class PlayFragment extends ListFragment {

    private static final String TAG = "PlayFragment";
    private File file;
    private List<String> myList;
    private MediaPlayer player = null;

    public PlayFragment() {
        // Required empty public constructor
    }

    /**
     * Method to start playing a audio file
     * @param fileName The file to play
     */
    private void startPlaying(String fileName) {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPlaying();
    }

    /**
     * Method to stop playing audio
     */
    private void stopPlaying() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_play, container, false);
        //ListView listView = (ListView) v.findViewById(R.id.listView);
        getFileContent();
        return v;
    }


    /**
     * Method to load the audio files in the directory specified to the listadapter of the class
     * which extends listfragment
     */
    public void getFileContent() {
        myList = new ArrayList<>();
        String root_sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).toString();
        file = new File(root_sd + "/MyApp");
        File list[] = file.listFiles();
        for (File aList : list) {
            myList.add(aList.getName());
        }
        setListAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, myList));
    }

    @Override
    public void onResume() {
        super.onResume();
        getFileContent();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        File temp_file = new File(file, myList.get(position));

        // If the clicked item is not a file then treat it as directory and load files within
        if (!temp_file.isFile()) {
            file = new File(file, myList.get(position));
            File list[] = file.listFiles();

            myList.clear();

            for (File aList : list) {
                myList.add(aList.getName());
            }
            Toast.makeText(getActivity().getApplicationContext(), file.toString(), Toast.LENGTH_LONG).show();
            setListAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(),
                    android.R.layout.simple_list_item_1, myList));

        //if the clicked item is a file stop the current playback and play the clicked file
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Playing:\n" + temp_file.toString(), Toast.LENGTH_SHORT).show();
            stopPlaying();
            startPlaying(temp_file.toString());
        }

    }

}