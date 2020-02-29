package com.example.a2learn;


public class Course implements Comparable<Course> {

    private String courseName;
    private int courseNumber;

    public Course(Builder builder) {
        this.courseName = builder.courseName;
        this.courseNumber = builder.courseNumber;
    }

    @Override
    public int compareTo(Course o) {

        return 0;
    }

    public static class Builder {
        private String courseName;
        private int courseNumber;

        public Builder setCourseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public Builder setCourseNumber(int courseNumber) {
            this.courseNumber = courseNumber;
            return this;
        }


        public Course build() {
            return new Course(this);
        }
    }

    public String getCourseName() {
        return courseName;
    }


    public int getCourseNumber() {
        return courseNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((courseName == null) ? 0 : courseName.hashCode());
        result = prime * result + courseNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Course other = (Course) obj;
        if (courseName == null) {
            if (other.courseName != null)
                return false;
        } else if (!courseName.equals(other.courseName))
            return false;
        if (courseNumber != other.courseNumber)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Course [courseName=" + courseName + ", courseNumber=" + courseNumber
                + "]";
    }

}
