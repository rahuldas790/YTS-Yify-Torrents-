package rahulkumardas.ytsyifytorrents;

/**
 * Created by Rahul Kumar Das on 07-05-2017.
 */

public class Torrent {
    public Torrent(String torUrl, String hash, String quality, String size, long seeds, long peers) {
        this.torUrl = torUrl;
        this.hash = hash;
        this.quality = quality;
        this.size = size;
        this.seeds = seeds;
        this.peers = peers;
    }

    public String torUrl, hash, quality, size;
    public long seeds, peers;
}