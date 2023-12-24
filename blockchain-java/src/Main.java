public class Main {
    public static void main(String[] args) {
        SimpleBlockChain blockChain = new SimpleBlockChain();
        blockChain.addBlock("data1");
        blockChain.addBlock("data2");
        blockChain.validateChain();
    }
}