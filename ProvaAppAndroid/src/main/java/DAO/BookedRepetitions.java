package DAO;

public class BookedRepetitions {
  String day;
  String startTime;
  int IDCourse;
  int IDTeacher;

  public BookedRepetitions(String day, String startTime, int IDCourse, int IDTeacher) {
    this.day = day;
    this.startTime = startTime;
    this.IDCourse = IDCourse;
    this.IDTeacher = IDTeacher;
  }

  public String getDay() {
    return day;
  }

  public String getStartTime() {
    return startTime;
  }

  public int getIDCourse() {
    return IDCourse;
  }

  public int getIDTeacher() {
    return IDTeacher;
  }
}
