package com.techbros.sachin.dooremedy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class to parse json data
 */
public class EmployeeJSONParser {

    // Receives a JSONObject and returns a list
    public ArrayList<EmployeeListItem> parse(JSONObject jObject) {

        JSONArray jEmployees = null;
        try {
            // Retrieves all the elements in the 'result' array
            jEmployees = jObject.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Invoking getCountries with the array of json object
        // where each json object represent a employee
        return getEmployees(jEmployees);
    }


    private ArrayList<EmployeeListItem> getEmployees(JSONArray jEmployees) {
        int employeeCount = jEmployees.length();
        ArrayList<EmployeeListItem> employeeList = new ArrayList<>();
        EmployeeListItem employee = null;

        // Taking each service, parses and adds to list object
        for (int i = 0; i < employeeCount; i++) {
            try {
                // Call getService with service JSON object to parse the service
                employee = getEmployee((JSONObject) jEmployees.get(i));
                employeeList.add(employee);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return employeeList;
    }

    // Parsing the service JSON object
    private EmployeeListItem getEmployee(JSONObject jEmployee) {
        EmployeeListItem employeeListItem = null;
        String empName = "";
        String empAge = "";
        String empJob = "";
        String empExperience = "";
        String emp_img = "";

        try {
            empName = jEmployee.getString("emp_name");
            empAge = jEmployee.getString("emp_age");
            empJob = jEmployee.getString("emp_job");
            empExperience = jEmployee.getString("emp_experience");
            emp_img = "http://my171database.890m.com/employee_images/"+ jEmployee.getString("emp_img");

            String details = "Name : " + empName + "\n" +
                    "Age : " + empAge + "\n" +
                    "Job : " + empJob + "\n" +
                    "Experience : " + empExperience;

            employeeListItem = new EmployeeListItem(empName,details,emp_img);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return employeeListItem;
    }
}