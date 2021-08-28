package io.punxe.fakefranchises.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    private int claimAmount;

    private int[][] challengeResults;

    private int prevTurn;
    private String winner;
    private int whoseTurn;
    private String[] players;
    private int playerAmount;
    private String[] lastMove;
    private HashMap<String, String> playerClaims = new HashMap<String, String>();
    private HashMap<String, Integer> playerCoins = new HashMap<String, Integer>();
    private HashMap<String, Integer> playerProperties = new HashMap<String, Integer>();
    private HashMap<String, Integer> playerLocations = new HashMap<String, Integer>();
    private HashMap<String, String> playerBankrupt = new HashMap<String, String>();
    public Game(HashMap<String, Player> players){
        
        //[1, 1] = did challenge, challenge successful
        //[1, 2] = did challenge, challenge unsuccessful
        //[2] = did not challenge
        this.claimAmount = 0;
        this.lastMove = new String[] {"null"};
        this.whoseTurn = 0;
        this.prevTurn = 0;
        this.winner = "ThereIsNoWinnerYet00112233445566778899";
        this.playerAmount = players.size();
        this.challengeResults = new int[this.playerAmount][2];
        this.players = new String[playerAmount];
        Iterator<String> itr = players.keySet().iterator();
        int iteration = 0;
        while(itr.hasNext()){
            this.players[iteration] = itr.next();
            iteration++;
        }
        for(int i = 0; i < playerAmount; i++){
            this.playerCoins.put(this.players[i], 3);
            setRandomProperty(this.players[i]);
            this.playerLocations.put(this.players[i], 0);
            this.playerClaims.put(this.players[i], "null");
            this.playerBankrupt.put(this.players[i], "false");
        }
    }
    public void setRandomProperty(String player){
        int x = ThreadLocalRandom.current().nextInt(0, playerAmount*2);
        while(playerProperties.containsValue(x)){
            x = ThreadLocalRandom.current().nextInt(0, playerAmount*2);
        }
        playerProperties.put(player, x);
        
    }
    public int getWhoseTurn() {
        return whoseTurn;
    }
    public void setWhoseTurn(int whoseTurn) {
        this.whoseTurn = whoseTurn;
    }
    public String[] getPlayers() {
        return players;
    }
    public void setPlayers(String[] players) {
        this.players = players;
    }
    public int getPlayerAmount() {
        return playerAmount;
    }
    public void setPlayerAmount(int playerAmount) {
        this.playerAmount = playerAmount;
    }
    public int getCoinAmount(String player){
        return playerCoins.get(player);
    }
    public void setCoinAmount(String player, int amount){
        playerCoins.put(player, amount);
    }
    public int getProperty(String player){
        return playerProperties.get(player);
    }
    public void setProperty(String player, int propertyNumber){
        playerProperties.put(player, propertyNumber);
    }
    public HashMap<String, Integer> getPlayerCoins() {
        return playerCoins;
    }
    public void setPlayerCoins(HashMap<String, Integer> playerCoins) {
        this.playerCoins = playerCoins;
    }
    public HashMap<String, Integer> getPlayerProperties() {
        return playerProperties;
    }
    public void setPlayerProperties(HashMap<String, Integer> playerProperties) {
        this.playerProperties = playerProperties;
    }
    public HashMap<String, Integer> getPlayerLocations() {
        return playerLocations;
    }
    public void setPlayerLocations(HashMap<String, Integer> playerLocations) {
        this.playerLocations = playerLocations;
    }
    public void movePlayer(String player, int steps){
        this.prevTurn = this.whoseTurn;
        int currentLoc = playerLocations.get(player);
        int newLoc = (currentLoc + steps)%(playerAmount*2);
        playerLocations.put(player, newLoc);
        setLastMove(new String[]{player, Integer.toString(steps), Integer.toString(currentLoc), Integer.toString(newLoc)});
    }
    public void refreshPlayerClaims(){
        for(int i = 0; i < playerAmount; i++){
            if(this.playerBankrupt.get(players[i]).equals("false")){
                this.playerClaims.put(this.players[i], "null");
            }else{
                this.playerClaims.put(this.players[i], "false");
            }
        }
    }
    public int getPlayerLocation(String player){
        return playerLocations.get(player);
    }
    public void nextPlayer(){
        for(int i = 0; i < playerAmount; i++){
            if(playerCoins.get(players[i]) < 0 && playerBankrupt.get(players[i]).equals("false")){
                bankrupt(players[i]);
            }
        }
        do{
        whoseTurn++;
        if(whoseTurn == playerAmount){
            whoseTurn = 0;
        }
        }while(playerBankrupt.get(players[whoseTurn]).equals("true"));
        
        resetLastMove();
    }
    public HashMap<String, String> getPlayerClaims() {
        return playerClaims;
    }
    public void setPlayerClaims(HashMap<String, String> playerClaims) {
        this.playerClaims = playerClaims;
    }
    public String[] getLastMove() {
        return lastMove;
    }
    public void setLastMove(String[] lastMove) {
        this.lastMove = lastMove;
    }
    public void setPlayerClaim(String player, String claim){
        if(claim.equals("true")){
            claimAmount++;
        }
        playerClaims.put(player, claim);
    }
    public void challenge(String player){
        claimAmount--;
        int playerLoc = getIndexOfPlayer(player);
        if(playerProperties.get(player) == playerLocations.get(players[whoseTurn])){
            transaction(players[whoseTurn], player);
            transaction(players[whoseTurn], player);
            challengeResults[playerLoc][0] = 1;
            challengeResults[playerLoc][1] = 2;
            setRandomProperty(player);
        }else{
            transaction(player, players[whoseTurn]);
            transaction(player, players[whoseTurn]);
            challengeResults[playerLoc][0] = 1;
            challengeResults[playerLoc][1] = 1;
        }

        checkNoMoreClaims();
    }
    public void noChallenge(String player){
        claimAmount--;
        transaction(players[whoseTurn], player);
        
        challengeResults[getIndexOfPlayer(player)][0] = 2;
        checkNoMoreClaims();
    }
    public void checkNoMoreClaims(){
        if(claimAmount == 0){
            nextPlayer();
        }
    }
    public void checkNoClaimsAtAll(){
        int nullAmount = 0;
        for (int i = 0; i < players.length; i++) {
            if (playerClaims.get(players[i]) == "null") {
                nullAmount++;
            }
        }
        if (nullAmount == 1) {
            checkNoMoreClaims();
        }
    }
    public int getIndexOfPlayer(String player){
        for(int i = 0; i < players.length; i++){
            if(players[i].equals(player)){
                return i;
            }
        }
        return -1;
    }
    public int getClaimAmount() {
        return claimAmount;
    }
    public void setClaimAmount(int claimAmount) {
        this.claimAmount = claimAmount;
    }
    public void transaction(String from, String to){
        if(playerCoins.get(from) > 0){
            playerCoins.put(to, playerCoins.get(to) + 1);
        }
        playerCoins.put(from, playerCoins.get(from) - 1);
    }
    public void resetChallengeResults(){
        challengeResults = new int[playerAmount][2];
    }
    public void resetLastMove(){
        lastMove = new String[]{"null"};
    }
    public int[][] getChallengeResults() {
        return challengeResults;
    }
    public void setChallengeResults(int[][] challengeResults) {
        this.challengeResults = challengeResults;
    }
    public int getPrevTurn() {
        return prevTurn;
    }
    public void setPrevTurn(int prevTurn) {
        this.prevTurn = prevTurn;
    }
    public void bankrupt(String player){
        playerBankrupt.put(player, "true");
        playerProperties.put(player, -1);
        int bankruptPlayerCount = 0;
        String nonBankruptPlayer = "ThereIsNoWinnerYet00112233445566778899";
        for(int i = 0; i < playerAmount; i++){
            if(playerBankrupt.get(players[i]).equals("true")){
                bankruptPlayerCount++;
            }else{
                nonBankruptPlayer = players[i];
            }
        }
        if(bankruptPlayerCount == playerAmount - 1){
            winner = nonBankruptPlayer;
        }
    }
    public HashMap<String, String> getPlayerBankrupt() {
        return playerBankrupt;
    }
    public void setPlayerBankrupt(HashMap<String, String> playerBankrupt) {
        this.playerBankrupt = playerBankrupt;
       
        
    }
    public String getWinner() {
        return winner;
    }
    public void setWinner(String winner) {
        this.winner = winner;
    }
    
}

