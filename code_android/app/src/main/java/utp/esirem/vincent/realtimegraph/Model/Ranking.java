package utp.esirem.vincent.realtimegraph.Model;

public class Ranking {
    private String userName;
    private long score;
    private long scorePlus;

    public Ranking() {
    }

    public Ranking(String userName, long score, long scorePlus) {
        this.userName = userName;
        this.score = score;
        this.scorePlus = scorePlus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getScorePlus() {
        return scorePlus;
    }

    public void setScorePlus(long scorePlus) {
        this.scorePlus = scorePlus;
    }
}



