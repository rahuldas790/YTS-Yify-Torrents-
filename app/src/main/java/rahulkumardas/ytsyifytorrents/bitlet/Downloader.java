/*
 *              bitlet - Simple bittorrent library
 *  Copyright (C) 2008 Alessandro Bahgat Shehata, Daniele Castagna
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package rahulkumardas.ytsyifytorrents.bitlet;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import rahulkumardas.ytsyifytorrents.bitlet.disk.PlainFileSystemTorrentDisk;
import rahulkumardas.ytsyifytorrents.bitlet.disk.TorrentDisk;
import rahulkumardas.ytsyifytorrents.bitlet.peer.IncomingPeerListener;


public class Downloader {
    private static final int PORT = 6881;
    private Torrent torrent;
    private IncomingPeerListener peerListener;

    public void startDownload(String torr, File path) {
        // read torrent filename from command line arg
        String filename = torr;

        // Parse the metafile
        FileInputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;
        Metafile metafile = null;
        try {
            inputStream = new FileInputStream(filename);
            bufferedInputStream = new BufferedInputStream(inputStream);
            metafile = new Metafile(bufferedInputStream);
        } catch (Exception e) {
            Log.i("Downloader", "Error : " + e.getMessage());
        }


        // Create the torrent disk, this is the destination where the torrent file/s will be saved
        TorrentDisk tdisk = new PlainFileSystemTorrentDisk(metafile, path);

        try {
            tdisk.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        peerListener = new IncomingPeerListener(PORT);
        peerListener.start();

        torrent = new Torrent(metafile, tdisk, peerListener);
        try {
            torrent.startDownload();
        } catch (Exception e) {
            Log.i("Downloader", "Error "+e.getMessage());
        }
        new Download().execute();

    }

    public class Download extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            while (!torrent.isCompleted()) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    break;
                }

                torrent.tick();
                System.out.printf("Got %s peers, completed %d bytes\n",
                        torrent.getPeersManager().getActivePeersNumber(),
                        torrent.getTorrentDisk().getCompleted());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            torrent.interrupt();
            peerListener.interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        // read torrent filename from command line arg
        String filename = args[0];

        // Parse the metafile
        Metafile metafile = new Metafile(new BufferedInputStream(new FileInputStream(filename)));

        // Create the torrent disk, this is the destination where the torrent file/s will be saved
        TorrentDisk tdisk = new PlainFileSystemTorrentDisk(metafile, new File("."));
        tdisk.init();

        IncomingPeerListener peerListener = new IncomingPeerListener(PORT);
        peerListener.start();

        Torrent torrent = new Torrent(metafile, tdisk, peerListener);
        torrent.startDownload();

        while (!torrent.isCompleted()) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                break;
            }

            torrent.tick();
            System.out.printf("Got %s peers, completed %d bytes\n",
                    torrent.getPeersManager().getActivePeersNumber(),
                    torrent.getTorrentDisk().getCompleted());
        }

        torrent.interrupt();
        peerListener.interrupt();
    }

}
