package com.example.a2learn;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
/*
    Note: I need to add read and write from and to the json file

 */
public class CourseManager {

    private ArrayList<Course> courseList;
    private static CourseManager courseManager;

    private CourseManager() {
        courseList = new ArrayList<>(10);
    }

    public static CourseManager getInstance() {
        if (courseManager == null) {
            courseManager = new CourseManager();
        }
        return courseManager;
    }

    public void addCourse(Course course) {
        if (!courseList.contains(course)) {
            courseList.add(course);
            Collections.sort(courseList);
        }

    }

    public Course removeCourseByName(String courseName) {
        int idx = -1;
        if (findCourseByName(courseName) != idx) {
            courseList.remove(idx);
        }
        return null;
    }

    public Course removeCourseById(int id) {
        int idx = -1;
        if (findCourseById(id) != idx) {
            courseList.remove(idx);
        }
        return null;
    }

    public int findCourseByName(String courseName) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getCourseName().equals(courseName)) {
                return i;
            }
        }
        return -1;
    }

    public int findCourseById(int id) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getCourseNumber() == id) {
                return i;
            }
        }
        return -1;
    }

    public void clearAllCourses() {
        courseList.clear();
    }

    public Course getCourseByName(String courseName) {
        int idx = findCourseByName(courseName);
        return idx == -1 ? null : courseList.get(idx);
    }

    public Course getCourseById(int id) {
        int idx = findCourseById(id);
        return idx == -1 ? null : courseList.get(idx);
    }


}

//
//public class SpinnerExample extends Activity {
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//        String[] arraySpinner = new String[] {
//                "1", "2", "3", "4", "5", "6", "7"
//        };
//        Spinner s = (Spinner) findViewById(R.id.Spinner01);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, arraySpinner);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        s.setAdapter(adapter);
//    }
//}