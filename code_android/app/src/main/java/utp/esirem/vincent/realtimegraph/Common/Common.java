package utp.esirem.vincent.realtimegraph.Common;

import java.util.ArrayList;
import java.util.List;

import utp.esirem.vincent.realtimegraph.Model.Question;
import utp.esirem.vincent.realtimegraph.Model.User;

public class Common {
    public static String categoryId,categoryName;
    public static User currentUser;
    public static List<Question> questionList = new ArrayList<>();

    public static final String STR_PUSH = "pushNotification";
}
