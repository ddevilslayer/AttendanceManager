package dotinc.attendancemanager2.Objects;

/**
 * Created by vellapanti on 17/1/16.
 */
public class SubjectsList {

    public int id;
    public String subjectName;

    public SubjectsList(int id, String subjectName) {
        this.id = id;
        this.subjectName = subjectName;
    }
    public SubjectsList(){}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


}
