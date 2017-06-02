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

package rahulkumardas.ytsyifytorrents.bitlet.peer.task;

import java.util.logging.Level;

import rahulkumardas.ytsyifytorrents.bitlet.Event;
import rahulkumardas.ytsyifytorrents.bitlet.Torrent;
import rahulkumardas.ytsyifytorrents.bitlet.peer.TorrentPeer;
import rahulkumardas.ytsyifytorrents.bitlet.util.thread.ThreadTask;


public class StartMessageReceiver implements ThreadTask {

    private TorrentPeer peer;

    public StartMessageReceiver(TorrentPeer peer) {
        this.peer = peer;
    }

    public boolean execute() throws Exception {

        try {
            peer.getReceiverThread().start();
        } catch (Exception e) {

            if (Torrent.verbose) {
                peer.getPeersManager().getTorrent().addEvent(new Event(this, "Problem starting MessageReceiver", Level.WARNING));
            }
            throw e;
        }
        return false;
    }

    public void interrupt() {
    }

    public void exceptionCought(Exception e) {
        peer.interrupt();
    }
}