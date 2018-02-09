package com.techbros.sachin.dooremedy;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class to parse json data
 */
public class ServiceJSONParser {

    // Receives a JSONObject and returns a list
    public ArrayList<ServiceListItem> parse(JSONObject jObject) {

        JSONArray jServices = null;
        try {
            // Retrieves all the elements in the 'result' array
            jServices = jObject.getJSONArray("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Invoking getServices with the array of json object
        // where each json object represent a service
        return getServices(jServices);
    }


    private ArrayList<ServiceListItem> getServices(JSONArray jServices) {
        int serviceCount = jServices.length();
        ArrayList<ServiceListItem> serviceList = new ArrayList<>();
        ServiceListItem service = null;

        // Taking each service, parses and adds to list object
        for (int i = 0; i < serviceCount; i++) {
            try {
                // Call getService with service JSON object to parse the service
                service = getService((JSONObject) jServices.get(i));
                serviceList.add(service);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return serviceList;
    }

    // Parsing the service JSON object
    private ServiceListItem getService(JSONObject jService) {
        ServiceListItem serviceListItem = null;
        String serviceName = "";
        String service_img = "";
        String sid = "";

        try {
            sid = jService.getString("sid");
            serviceName = jService.getString("service_name");
            service_img = "http://my171database.890m.com/service_list_images/"+ jService.getString("service_img");

            String details = sid;

            serviceListItem = new ServiceListItem(serviceName,details,service_img);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return serviceListItem;
    }
}