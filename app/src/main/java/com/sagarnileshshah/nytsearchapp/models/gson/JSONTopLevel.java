
package com.sagarnileshshah.nytsearchapp.models.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONTopLevel {

    private Response response;
    private String status;
    private String copyright;

    /**
     * 
     * @return
     *     The response
     */
    public Response getResponse() {
        return response;
    }

    /**
     * 
     * @param response
     *     The response
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * 
     * @param copyright
     *     The copyright
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public static JSONTopLevel parseJson(String json){
        Gson gson = new GsonBuilder().create();
        JSONTopLevel jsonTopLevel = gson.fromJson(json, JSONTopLevel.class);
        return jsonTopLevel;
    }

}
