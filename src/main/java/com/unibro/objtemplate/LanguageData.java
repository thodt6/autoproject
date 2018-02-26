/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.objtemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.unibro.utils.Global;
import java.util.List;

/**
 *
 * @author THOND
 */
public class LanguageData {

    private String languageid;
    private String content;

    public LanguageData(String languageid, String content) {
        this.languageid = languageid;
        this.content = content;
    }

    public static LanguageData getObjectFromJson(String content) {
        Gson gson = Global.getGsonObject();
        return gson.fromJson(content, LanguageData.class);
    }

    public JsonObject toJson() {
        Gson gson = Global.getGsonObject();
        return gson.toJsonTree(this).getAsJsonObject();
    }

    /**
     * @return the languageid
     */
    public String getLanguageid() {
        return languageid;
    }

    /**
     * @param languageid the languageid to set
     */
    public void setLanguageid(String languageid) {
        this.languageid = languageid;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    public static JsonArray toJsonArray(List<LanguageData> list) {
        Gson gson = Global.getGsonObject();
        JsonElement element = gson.toJsonTree(list, new TypeToken<List<LanguageData>>() {
        }.getType());
        JsonArray jsonArray = element.getAsJsonArray();
        return jsonArray;
    }

    public static String toJsonArrayString(List<LanguageData> list) {
        Gson gson = Global.getGsonObject();
        return gson.toJson(list);
    }
}
