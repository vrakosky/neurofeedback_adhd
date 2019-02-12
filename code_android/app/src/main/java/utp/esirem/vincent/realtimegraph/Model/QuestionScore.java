package utp.esirem.vincent.realtimegraph.Model;

public class QuestionScore {
    private String Question_Score;
    private String User;
    private String Score;
    private String ScorePlus;
    private String CategoryId;
    private String CategoryName;
    private String AnsweringTime;
    private String ReadingTime;
    private String start_read_time;
    private String end_read_time;
    private String start_play_time;
    private String end_play_time;





    public QuestionScore() {
    }

    public QuestionScore(String question_Score, String user, String score, String scorePlus, String categoryId, String categoryName, String answeringTime, String readingTime, String start_read_time, String end_read_time, String start_play_time, String end_play_time) {
        Question_Score = question_Score;
        User = user;
        Score = score;
        ScorePlus = scorePlus;
        CategoryId = categoryId;
        CategoryName = categoryName;
        AnsweringTime = answeringTime;
        ReadingTime = readingTime;
        this.start_read_time = start_read_time;
        this.end_read_time = end_read_time;
        this.start_play_time = start_play_time;
        this.end_play_time = end_play_time;
    }

    public String getQuestion_Score() {
        return Question_Score;
    }

    public void setQuestion_Score(String question_Score) {
        Question_Score = question_Score;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getScorePlus() {
        return ScorePlus;
    }

    public void setScorePlus(String scorePlus) {
        ScorePlus = scorePlus;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getAnsweringTime() {
        return AnsweringTime;
    }

    public void setAnsweringTime(String answeringTime) {
        AnsweringTime = answeringTime;
    }

    public String getReadingTime() {
        return ReadingTime;
    }

    public void setReadingTime(String readingTime) {
        ReadingTime = readingTime;
    }

    public String getStart_read_time() {
        return start_read_time;
    }

    public void setStart_read_time(String start_read_time) {
        this.start_read_time = start_read_time;
    }

    public String getEnd_read_time() {
        return end_read_time;
    }

    public void setEnd_read_time(String end_read_time) {
        this.end_read_time = end_read_time;
    }

    public String getStart_play_time() {
        return start_play_time;
    }

    public void setStart_play_time(String start_play_time) {
        this.start_play_time = start_play_time;
    }

    public String getEnd_play_time() {
        return end_play_time;
    }

    public void setEnd_play_time(String end_play_time) {
        this.end_play_time = end_play_time;
    }
}
