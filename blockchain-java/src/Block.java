import java.util.Date;

/**
 * Each block doesn't just contain the hash of the block before it,
 * but its own hash is in part, calculated from the previous hash.
 * If the previous block’s data is changed then the previous block’s
 * hash will change -> changing any data in a blockchain will break
 * the chain.
 */
public class Block {
    String hash;
    String previousHash;
    String data;
    Long timestamp;
    int nonce;

    public Block(String previousHash, String data) {
        this.previousHash = previousHash;
        this.data = data;
        this.timestamp = new Date().getTime();
        this.hash = this.calculateHash();
    }

    public String calculateHash() {
        return Utils.applySHA256(
                previousHash + Long.toString(timestamp) + Integer.toString(nonce) + data
        );
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
    }

    @Override
    public String toString() {
        return String.format("""
                {
                \t"data": %s,
                \t"previous_hash": %s,
                \t"hash": %s,
                \t"timestamp": %s,
                \t"nonce": %d
                }""", this.data, this.previousHash, this.hash, this.timestamp.toString(), this.nonce);
    }
}
