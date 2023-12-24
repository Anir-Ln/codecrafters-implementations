import java.util.ArrayList;
import java.util.List;

public class SimpleBlockChain {
    List<Block> chain;
    final int DIFFICULTY;

    public SimpleBlockChain() {
        this(4);
    }
    public SimpleBlockChain(final int difficulty) {
        this.DIFFICULTY = difficulty;
        chain = new ArrayList<>();
        Block genesisBlock = new Block("Genesis Block", "0");
        genesisBlock.mineBlock(DIFFICULTY);
        chain.add(genesisBlock);
        System.out.println(genesisBlock);
    }

    public void addBlock(String data) {
        Block lastBlock = chain.getLast();
        Block newBlock = new Block(lastBlock.hash, data);
        newBlock.mineBlock(DIFFICULTY);
        chain.add(newBlock);
        System.out.println(newBlock);
    }

    public void validateChain() throws AssertionError {
        // compare previous block hash to current block previous_hash
        for (int i = 1; i < chain.size(); i++) {
            assert chain.get(i).previousHash.equals(chain.get(i-1).hash);
        }
        // validate proof of work
        String hashTarget = new String(new char[DIFFICULTY]).replace("\0", "0");
        for (Block block : chain) {
            assert block.hash.substring(0, DIFFICULTY).equals(hashTarget);
        }
        // verify that registered hash is correct
        for (Block block : chain) {
            assert block.hash.equals(block.calculateHash());
        }
        System.out.println("BLOCKCHAIN IS VALID");
    }
}
