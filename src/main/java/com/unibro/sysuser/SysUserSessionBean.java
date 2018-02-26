/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.sysuser;


import com.unibro.utils.Global;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author Nguyen Duc Tho
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped

public class SysUserSessionBean implements Serializable {

    
    private String userid;
    private String password;
    private Boolean logged = true;
    private SysUser user;
    private String localCode="en";


    static final Logger log = Logger.getLogger(SysUserSessionBean.class.getName());


    public SysUserSessionBean() {
        this.setLocalCode("en");
    }



    /**
     * @return the username
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid the username to set
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the logged
     */
    public Boolean getLogged() {
        return logged;
    }

    /**
     * @param logged the logged to set
     */
    public void setLogged(Boolean logged) {
        this.logged = logged;
    }

  
    public void doLogin() {
        if(Global.getConfigValue("admin.use").equals(this.getUserid()) && Global.getConfigValue("admin.pwd").equals(this.getPassword())){
            this.logged=true;
            this.user=new SysUser();
            this.user.setUsername(userid);
            this.user.setPassword(password);
            this.gotoHomepage();
        }
    }

    public void gotoHomepage() {
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ServletContext servletContext = (ServletContext) ec.getContext();
            log.info("Real path:" + servletContext.getRealPath("/"));
            ec.redirect(ec.getRequestContextPath() + "/portal/dashboard.html");
        } catch (IOException ex) {

        }
    }

    public void gotoPage(String uri) {
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ServletContext servletContext = (ServletContext) ec.getContext();
            log.info("Real path:" + servletContext.getRealPath("/"));
            ec.redirect(ec.getRequestContextPath() + uri);
        } catch (IOException ex) {

        }
    }

   
    public void verifyAccessPage() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        if (!this.logged) {
            try {
                this.userid = "";
                this.password = "";
                this.setSysUser(null);
                context.redirect(context.getRequestContextPath() + "/access-denied.html");
            } catch (IOException ex) {

            }
        } else {
            boolean validSysUser =true;
//            if (user == null) {
//                validSysUser = false;
//            } else {
//                validSysUser = user.getGroup().checkAccessableModules(context.getRequestServletPath());
//            }
            if (!validSysUser) {
                try {
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    context.redirect(facesContext.getExternalContext().getRequestContextPath() + "/access-denied.html");
                } catch (IOException e) {
                }
            } else {
//                HttpServletResponse response = (HttpServletResponse) context.getResponse();
//                response.setHeader("Access-Control-Allow-Origin", "*");
//                response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
//                response.setHeader("Access-Control-Allow-Headers", "Content-Type");
//                log.info("Add header");
            }
        }
    }

    @SuppressWarnings("unused")
    public void doLogout() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        if (this.logged) {
            try {
                this.logged = false;
                this.userid = "";
                this.password = "";
//                int index = this.getCookieSysUserIndex(user.getSysUserid());
//                if (index != -1) {
//                    this.getCookieSysUser().remove(index);
//                    CookieSysUser.saveCookieSysUser(this.getCookieSysUser());
//                }
                this.user = null;
                FacesContext facesContext = FacesContext.getCurrentInstance();
//                HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
//                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
//                Cookie[] cookies = request.getCookies();
//                Cookie userNameCookie = null;
//                Cookie userPasswordCookie = null;
//                Cookie rememberCookie = null;
//                for (Cookie c : cookies) {
//                    log.info("found the cookie: " + c.getName() + " domain:" + c.getDomain() + " exp:" + c.getMaxAge() + " path:" + c.getPath());
//                    if (c.getName().equals("remember")) {
//                        rememberCookie = c;
//
//                    }
//                    if (c.getName().equals("userName")) {
//                        userNameCookie = c;
//                    }
//                    if (c.getName().equals("userPassword")) {
//                        userPasswordCookie = c;
//                    }
//                }
//                if (userNameCookie != null && userPasswordCookie != null && rememberCookie != null) {
//                    log.info("Delete cookies");
//                    userNameCookie.setMaxAge(0);
//                    userPasswordCookie.setMaxAge(0);
//                    rememberCookie.setMaxAge(0);
//                    userNameCookie.setValue("");
//                    userPasswordCookie.setValue("");
//                    rememberCookie.setValue("");
//                    userNameCookie.setPath("/");
//                    userPasswordCookie.setPath("/");
//                    rememberCookie.setPath("/");
//                    response.addCookie(userNameCookie);
//                    response.addCookie(userPasswordCookie);
//                    response.addCookie(rememberCookie);
//                }
                //context.redirect(context.getRequestContextPath());
                Global.loadConfig();

                facesContext.getExternalContext().redirect(facesContext.getExternalContext().getRequestContextPath() + "/login.html");

            } catch (IOException ex) {
                //Logger.getLogger(homebean.class.getName()).log(Level.SEVERE, null, ex);
                //log.info(ex);
            }
        }
    }
//    public void doLogout(){
//        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//        if (this.logged) {
//            try {
//                this.logged=false;
//                this.username="";
//                this.password="";
//                this.user=null;
//                Map<String,Object> props = new HashMap<String, Object>();  
//                props.put("maxAge", 0);  
//                FacesContext.getCurrentInstance().getExternalContext().addResponseCookie("remember", "", props); // the cookie I want to overwrite (expire)  
//                FacesContext.getCurrentInstance().getExternalContext().addResponseCookie("userName", "", props); // random dummy cookie to see whether any is inserted
//                FacesContext.getCurrentInstance().getExternalContext().addResponseCookie("userPassword", "", props); // random dummy cookie to see whether any is inserted
//                context.redirect(context.getRequestContextPath());
//            } catch (IOException ex) {
//                //Logger.getLogger(homebean.class.getName()).log(Level.SEVERE, null, ex);
//                //log.info(ex);
//            }
//        }        
//    }

    /**
     * @return the user
     */
    public SysUser getSysUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setSysUser(SysUser user) {
        this.user = user;
    }

    public static SysUserSessionBean getUserSession() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (session != null) {
            SysUserSessionBean user = (SysUserSessionBean) session.getAttribute("sysUserSessionBean");
            //log.info("SysUserBean:" + user.toString());
            //log.info(user.getSysUser());
            return user;
        }
        return null;
    }

    

//    public void countryLocaleCodeChanged(ValueChangeEvent e) {
//
//        String newLocaleValue = e.getNewValue().toString();
//
//        //loop country map to compare the locale code
//        for (Map.Entry<String, Object> entry : countries.entrySet()) {
//
//            if (entry.getValue().toString().equals(newLocaleValue)) {
//                Locale local = new Locale((String) entry.getValue(), entry.getKey());
//                FacesContext.getCurrentInstance()
//                        .getViewRoot().setLocale(local);
//
//            }
//        }
//    }

    public String setVietnameaseLanguage() {
        log.info("Set Vietnamease language");
        Locale locale = new Locale("vi");
        this.localCode = "vi";
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        log.info("Set Vietnamease language");
        return null;
    }

    public String setEnglishLanguage() {
        log.info("Set English language");
        Locale locale = new Locale("en");
        this.localCode = "en";
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        log.info("Set English language1");
        return null;
    }

    public String setLanguage(String lang) {
        log.info("Set Language");
        Locale locale = new Locale(lang);
        this.localCode = lang;
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        return null;
    }

    /**
     * @return the localCode
     */
    public String getLocalCode() {
        return localCode;
    }

    /**
     * @param localCode the localCode to set
     */
    public final void setLocalCode(String localCode) {
        this.localCode = localCode;
    }
  

    public void updateProfile() {
//        SysUserDAO dao = new SysUserDAO();
//        AccountDAO acc_dao = new AccountDAO();
//        if (acc_dao.edit(this.getAccount())) {
//            if (dao.edit(this.getSysUser())) {
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, Global.getResourceLanguage("general.operationSuccess"), "");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//            } else {
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, Global.getResourceLanguage("general.operationFail"), "");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//            }
//        } else {
//            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, Global.getResourceLanguage("general.operationFail"), "");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        }
    }

   

}
