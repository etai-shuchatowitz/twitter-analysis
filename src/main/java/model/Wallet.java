package model;

public class Wallet {

    private float cash;
    private float numberOfBitcoin;

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
    }

    public float getNumberOfBitcoin() {
        return numberOfBitcoin;
    }

    public void setNumberOfBitcoin(float numberOfBitcoin) {
        this.numberOfBitcoin = numberOfBitcoin;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "cash=" + cash +
                ", numberOfBitcoin=" + numberOfBitcoin +
                '}';
    }
}
