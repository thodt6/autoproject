package com.unibro.utils;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.log4j.Logger;

/**
 *
 * @author Nguyen Duc Tho
 */
@ManagedBean
@ViewScoped
public class HtmlSourceController implements Serializable {

    private String htmlSource;
    private String url = "";
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public HtmlSourceController() {
        //this.loadObjects();
    }
    
    public void queryHtmlSource(){
        HttpClient client=new HttpClient();
        client.setUrlBase(this.getUrl());
        this.htmlSource=client.getDataByGetMethod(null,null);
    }

    /**
     * @return the htmlSource
     */
    public String getHtmlSource() {
        return htmlSource;
    }

    /**
     * @param htmlSource the htmlSource to set
     */
    public void setHtmlSource(String htmlSource) {
        this.htmlSource = htmlSource;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
