package com.tanmaybuilds.lnmtrack.DataModels;

import java.util.List;

public class ApiResponse {
    String status;
    List<SubjectModel> data; // Hamara SubjectModel list yahan aayega

    public List<SubjectModel> getData() { return data; }
    public String getStatus() { return status; }
}
