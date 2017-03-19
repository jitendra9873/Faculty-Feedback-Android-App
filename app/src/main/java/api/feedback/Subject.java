package api.feedback;

public class Subject {

    public String teacherName;
    public String subjectName;
    public String division;
    public boolean isPracs;

    public Subject(String teacherName, String subjectName, String division, boolean isPracs){
        this.teacherName = teacherName;
        this.subjectName = subjectName;
        this.division = division;
        this.isPracs = isPracs;
    }
}
