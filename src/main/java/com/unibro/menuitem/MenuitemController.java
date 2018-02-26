package com.unibro.menuitem;

import com.unibro.language.Language;
import com.unibro.objtemplate.ObjTemplate;
import com.unibro.project.Project;
import com.unibro.project.ProjectDAO;
import com.unibro.utils.Global;
import com.unibro.utils.SortedProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Nguyen Duc Tho
 */
@ManagedBean
@ViewScoped
public class MenuitemController implements Serializable {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private String projectid;
    private Project project;
    List<Menuitem> objects;
    Menuitem newObject = Menuitem.getInstant();
    Menuitem selectedObject;
    private Boolean updatAction = true;

    private TreeNode tree;
    private TreeNode selectedNode;

    public MenuitemController() {
//        this.loadObjects();
    }

    public void initMenuList() {
        logger.info("Project id:" + this.getProjectid());
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.isPostback() && !facesContext.isValidationFailed()) {
            if (this.projectid != null && !this.projectid.equals("")) {
                ProjectDAO dao = new ProjectDAO();
                setProject(dao.getObjectByKey(this.getProjectid()));
                logger.info("Project:" + this.getProject().getProjectid());
//                this.buildTreeNode();
                this.buildDefaultMenu();
                logger.info(this.getTree().getChildCount());
                this.initRootMenu();
            }
        }
    }

    public final void loadObjects() {
        MenuitemDAO dao = new MenuitemDAO();
        this.objects = dao.load("ORDER BY createdtime");
    }

    public void setObjects(List<Menuitem> objects) {
        this.objects = objects;
    }

    public List<Menuitem> getObjects() {
        return objects;
    }

    public void setNewObject(Menuitem newObject) {
        this.newObject = newObject;
    }

    public Menuitem getNewObject() {
        return newObject;
    }

    public void setSelectedObject(Menuitem selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Menuitem getSelectedObject() {
        return selectedObject;
    }

    public void editSelected() {
        if (this.selectedObject != null) {
            MenuitemDAO dao = new MenuitemDAO();
            if (dao.edit(this.selectedObject)) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Update success", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update fail", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void deleteObject() {
        if (this.selectedObject != null) {
            if (this.selectedNode.getChildCount() == 0) {
                MenuitemDAO dao = new MenuitemDAO();
                dao.delete(selectedObject);
                this.buildTreeNode();
                this.initRootMenu();
            }
        }
    }

    /**
     * @return the projectid
     */
    public String getProjectid() {
        return projectid;
    }

    /**
     * @param projectid the projectid to set
     */
    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public void createObject() {
        if (this.newObject != null) {
            MenuitemDAO dao = new MenuitemDAO();
            if (dao.create(getNewObject())) {
                Menuitem parent = this.getNewObject().getParentMenu();
                if (parent != null) {
                    parent.setSubmenu(true);
                    dao.edit(parent);
                }
                this.buildTreeNode();
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Create menu fail", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void updateObject() {
        if (this.selectedObject != null) {
            MenuitemDAO dao = new MenuitemDAO();
            if (dao.edit(getSelectedObject())) {
                this.buildTreeNode();
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, Global.getResourceLanguage("general.operationFail"), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    private void gotoPage(String path) {
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(ec.getRequestContextPath() + path);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void initRootMenu() {
        this.newObject = Menuitem.getInstant();
        logger.info("Init New Object");

        this.newObject.setProject(this.project);
        this.newObject.setProjectid(this.getProjectid());
        this.newObject.loadNameData();
        this.newObject.setParentId("");
        this.newObject.setId("id_" + this.getProject().getProjectid() + "_" + "");
        this.newObject.setOrderindex(this.getNewObject().getLastOrderIndex() + 1);
        this.updatAction = false;
//        this.selectedObject = null;
    }

    public void initNewObject() {
        this.newObject = Menuitem.getInstant();
        logger.info("Init New Object");

        this.newObject.setProject(this.project);
        this.newObject.setProjectid(this.getProjectid());
        this.newObject.loadNameData();
        if (this.selectedObject != null) {
            this.newObject.setParentId(this.getSelectedObject().getId());
            this.newObject.setId(this.getSelectedObject().getId() + "_");
            this.newObject.setOrderindex(this.getNewObject().getLastOrderIndex() + 1);
        } else {
            this.newObject.setParentId("");
            this.newObject.setId("id_" + this.getProject().getProjectid() + "_" + "");
            this.newObject.setOrderindex(this.getNewObject().getLastOrderIndex() + 1);
        }
        this.updatAction = false;
//        this.selectedObject = null;
    }

    private void buildTreeNode() {
        this.setTree(new DefaultTreeNode(null, null));
        this.addChild(getTree());
        this.updatAction = false;
    }

    private void addChild(TreeNode parent) {
        MenuitemDAO dao = new MenuitemDAO();
        if (parent.getParent() == null) {
            List<Menuitem> list = dao.load("WHERE parent_id=? AND projectid=? ORDER BY orderindex", "", this.getProject().getProjectid());
            if (list != null && !list.isEmpty()) {
                for (Menuitem s : list) {
                    s.setProject(this.getProject());
                    s.loadNameData();
                    TreeNode node = new DefaultTreeNode(s, parent);
                    node.setExpanded(true);
                    addChild(node);
                }
            }
        } else {
            Menuitem subject = (Menuitem) parent.getData();
            List<Menuitem> list = dao.load("WHERE parent_id=? AND projectid=? ORDER BY orderindex", subject.getId(), this.getProject().getProjectid());
            if (list != null && !list.isEmpty()) {
                for (Menuitem s : list) {
                    TreeNode node = new DefaultTreeNode(s, parent);
                    node.setExpanded(true);
                    addChild(node);
                }
            }
        }
    }

    /**
     * @return the tree
     */
    public TreeNode getTree() {
        return tree;
    }

    /**
     * @param tree the tree to set
     */
    public void setTree(TreeNode tree) {
        this.tree = tree;
    }

    /**
     * @return the selectedNode
     */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    /**
     * @param selectedNode the selectedNode to set
     */
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void onNodeSelect(NodeSelectEvent event) {
        this.selectedNode = event.getTreeNode();
        this.selectedObject = (Menuitem) this.selectedNode.getData();
        this.selectedObject.loadNameData();
        this.updatAction = true;
    }

    public void onDragDrop(TreeDragDropEvent event) {
        TreeNode dragNode = event.getDragNode();
        TreeNode dropNode = event.getDropNode();
        Menuitem source = (Menuitem) dragNode.getData();
        Menuitem target = (Menuitem) dropNode.getData();
        source.setParentId(target.getId());
        MenuitemDAO dao = new MenuitemDAO();
        dao.edit(source);
        this.selectedObject = source;
        this.goBottom();
    }

    public void goTop() {
        if (this.selectedObject != null) {
            List<Menuitem> list = this.selectedObject.getBrotherList();
            if (list != null && !list.isEmpty()) {
                MenuitemDAO dao = new MenuitemDAO();
                list.remove(this.selectedObject);
                list.add(0, this.selectedObject);
                for (int i = 0; i < list.size(); i++) {
                    Menuitem s = list.get(i);
                    s.setOrderindex(i);
                    dao.edit(s);
                }
                this.buildTreeNode();
            }
        }
    }

    public void goBottom() {
        if (this.selectedObject != null) {
            List<Menuitem> list = this.selectedObject.getBrotherList();
            if (list != null && !list.isEmpty()) {
                MenuitemDAO dao = new MenuitemDAO();
                list.remove(this.selectedObject);
                list.add(list.size(), this.selectedObject);
                for (int i = 0; i < list.size(); i++) {
                    Menuitem s = list.get(i);
                    s.setOrderindex(i);
                    dao.edit(s);
                }
                this.buildTreeNode();
            }
        }
    }

    public void goDown() {
        if (this.selectedObject != null) {
            List<Menuitem> list = this.selectedObject.getBrotherList();
            if (list != null) {
                MenuitemDAO dao = new MenuitemDAO();
                int index = list.indexOf(this.selectedObject);
                if (index < list.size() - 1) {
                    Collections.swap(list, index, index + 1);
                    for (int i = 0; i < list.size(); i++) {
                        Menuitem s = list.get(i);
                        s.setOrderindex(i);
                        dao.edit(s);
                    }
                }
                this.buildTreeNode();
            }
        }
    }

    public void goUp() {
        if (this.selectedObject != null) {
            List<Menuitem> list = this.selectedObject.getBrotherList();
            if (list != null) {
                MenuitemDAO dao = new MenuitemDAO();
                int index = list.indexOf(this.selectedObject);
                if (index > 0) {
                    Collections.swap(list, index, index - 1);
                    for (int i = 0; i < list.size(); i++) {
                        Menuitem s = list.get(i);
                        s.setOrderindex(i);
                        dao.edit(s);
                    }
                }
                this.buildTreeNode();
            }
        }
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    private void buildDefaultMenu() {
        if (this.project != null) {
            List<ObjTemplate> list = this.project.loadTemplateObject();
            if (list != null) {
                logger.info("List size:" + list.size());
                MenuitemDAO dao = new MenuitemDAO();
                for (ObjTemplate temp : list) {
                    Menuitem sub = dao.getObjectByKey("id_" + this.project.getProjectid() + "_" + temp.getName());
                    if (sub == null) {
                        sub = Menuitem.getInstant();
                        sub.setCreatedtime(new java.util.Date());
                        sub.setId("id_" + this.project.getProjectid() + "_" + temp.getName());
                        sub.setOrderindex(-1);
                        sub.setOutcome("");
                        sub.setParentId("");
                        sub.setProject(project);
                        sub.setProjectid(project.getProjectid());
                        sub.setSubmenu(true);
                        sub.loadNameData();
                        for (int i = 0; i < sub.getList_name().size(); i++) {
                            sub.getList_name().get(i).setContent(temp.getClassName());
                        }
                        dao.create(sub);
                    }
                    Menuitem index = dao.getObjectByKey("id_" + this.project.getProjectid() + "_" + temp.getName() + "_index");
                    if (index == null) {
                        index = Menuitem.getInstant();
                        index.setId("id_" + this.project.getProjectid() + "_" + temp.getName() + "_index");
                        index.setOrderindex(0);
                        index.setOutcome("/portal/" + temp.getName() + "/index.html");
                        index.setParentId(sub.getId());
                        index.setProject(project);
                        index.setProjectid(this.getProjectid());
                        index.setSubmenu(false);
                        index.loadNameData();
                        for (int i = 0; i < index.getList_name().size(); i++) {
                            index.getList_name().get(i).setContent("Index");
                        }
                        dao.create(index);
                    }
                    Menuitem newItem = dao.getObjectByKey("id_" + this.project.getProjectid() + "_" + temp.getName() + "_create");
                    if (newItem == null) {
                        newItem = Menuitem.getInstant();
                        newItem.setId("id_" + this.project.getProjectid() + "_" + temp.getName() + "_create");
                        newItem.setOrderindex(0);
                        newItem.setOutcome("/portal/" + temp.getName() + "/create.html");
                        newItem.setParentId(sub.getId());
                        newItem.setProject(project);
                        newItem.setProjectid(this.getProjectid());
                        newItem.setSubmenu(false);
                        newItem.loadNameData();
                        for (int i = 0; i < newItem.getList_name().size(); i++) {
                            newItem.getList_name().get(i).setContent("Create");
                        }
                        dao.create(newItem);
                    }
                }
                this.buildTreeNode();
            }
        }

    }

    /**
     * @return the updatAction
     */
    public Boolean getUpdatAction() {
        return updatAction;
    }

    /**
     * @param updatAction the updatAction to set
     */
    public void setUpdatAction(Boolean updatAction) {
        this.updatAction = updatAction;
    }

    public void writeMenuCMSData() {
        this.writeMenuLanguageFile();
        this.writeMenuXHTMLFile();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Write done", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void writeMenuLanguageFile() {
        SortedProperties prop = new SortedProperties();
        for (Language lang : this.project.getLanguage_list()) {
            String destination = this.project.getProjectFolder() + "/src/main/resources/config/";
            File dir = new File(destination);
            dir.mkdirs();
            try {
                prop.load(new FileInputStream(destination + "/language_menu_" + lang.getLanguageid() + ".properties"));
                prop.clear();
            } catch (IOException ex) {
            }
            logger.info("Start write to file");
            try {
//                prop.load(new FileInputStream(destination + "/language_menu_" + lang.getLanguageid() + ".properties"));
                this.putString(prop, tree, lang.getLanguageid());
                prop.store(new FileOutputStream(destination + "/language_menu_" + lang.getLanguageid() + ".properties", false), lang.getName() + " menu properties file");
            } catch (IOException ex) {
                //System.out.println(ex); 
                logger.error(ex);
            }
        }
    }

    public void writeMenuXHTMLFile() {
        try {
            File f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/menu/menu_content.xhtml");
            String content = FileUtils.readFileToString(f);
//            content = content.replace("ObjClass", this.getClassName());
            xhtml_menu_content = "";
            this.buildXHTMLMenu(tree);
            content = content.replace("[CONTENT]", xhtml_menu_content);
            logger.info(xhtml_menu_content);
            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/WEB-INF/menu_content.xhtml"), content);
        } catch (IOException ex) {
            logger.error(ex);
        }

    }

    private void putString(SortedProperties prop, TreeNode tree, String languageid) {
        if (tree != null) {
            if (tree.getData() != null) {
                Menuitem data = (Menuitem) tree.getData();
                data.loadNameData();
                prop.put("menu." + data.getId(), data.getLanguageValue(languageid));
            }
            List<TreeNode> child = tree.getChildren();
            if (child != null && !child.isEmpty()) {
                for (int i = child.size() - 1; i >= 0; i--) {
                    TreeNode c = child.get(i);
                    putString(prop, c, languageid);
                }
            }
        }

    }
    String xhtml_menu_content = "";

    private void buildXHTMLMenu(TreeNode tree) {
        if (tree != null) {
            if (tree.getData() != null) {
                logger.info("Content:" + xhtml_menu_content);
                Menuitem data = (Menuitem) tree.getData();
                logger.info(data.getId());
                if (data.getSubmenu()) {
                    xhtml_menu_content += data.toXHTMLOpenQuote();
                } else {
                    xhtml_menu_content += data.toXHTMLItem();
                }
            }
            List<TreeNode> child = tree.getChildren();
            if (child != null && !child.isEmpty()) {
                logger.info("Size:" + child.size());
                for (TreeNode c : child) {
                    buildXHTMLMenu(c);
                }
            }
            if (tree.getData() != null) {
                Menuitem data = (Menuitem) tree.getData();
                if (data.getSubmenu()) {
                    xhtml_menu_content += data.toXHTMLCloseQuote();
                }
            }
        }

    }

}
