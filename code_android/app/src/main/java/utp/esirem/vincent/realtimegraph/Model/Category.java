package utp.esirem.vincent.realtimegraph.Model;

public class Category {
    private String Name;
    private String Image;
    private String ReadingTask;

    public Category() {

    }

    public Category(String name, String image, String readingTask) {
        Name = name;
        Image = image;
        ReadingTask = readingTask;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getReadingTask() {
        return ReadingTask;
    }

    public void setReadingTask(String readingTask) {
        ReadingTask = readingTask;
    }
}
