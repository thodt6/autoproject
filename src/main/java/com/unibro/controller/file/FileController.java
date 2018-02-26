package com.unibro.controller.file;

import com.unibro.utils.Global;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Nguyen Duc Tho
 */
public class FileController {

    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
//
//    private List<UFile> objects;
//    private UFile selectedRootObject;
//    private UFile selectedObject;
//    private UFile[] selectedObjects;
//    private final Logger logger = Logger.getLogger(this.getClass().getName());
//    private String rootDir = Global.FILE_ROOT_PATH;
//    private String selectedPath = "";
//    private String newFolder = "folder";
//    private String renameFile = "";
//    private String inputDir = "";
//
//    private StreamedContent file;
//
//    public FileController(String rootDir, String currentDir) {
//        this.rootDir = rootDir;
//        this.inputDir = currentDir;
//        logger.info(currentDir);
//        this.loadObjects();
//    }
//
//    public FileController() {
//        this.rootDir = Global.FILE_PRIVATE_PATH;
//        this.inputDir = UserSessionBean.getUserSession().getUser().getGroupid() + "/" + UserSessionBean.getUserSession().getUser().getAccountid();
//        this.loadObjects();
//    }
//
//    public void setRootDir(String rootDir, String currentDir) {
//        this.rootDir = rootDir;
//        this.inputDir = currentDir;
//        this.selectedRootObject = new UFile(rootDir + inputDir);
//        if (!this.selectedRootObject.exists()) {
//            this.selectedRootObject.mkdirs();
//        }
//        this.loadObjects();
//    }
//
//    public final void loadCurrentObjects() {
//        String path;
////        if (selectedRootObject == null) {
//        path = rootDir + inputDir;
//        logger.info(rootDir + inputDir);
//        this.selectedRootObject = new UFile(path);
//        if (!this.selectedRootObject.exists()) {
//            this.selectedRootObject.mkdirs();
//        }
//
//        if (this.objects != null && !this.objects.isEmpty()) {
//            this.objects.clear();
//        }
//        this.objects = new ArrayList();
//        File[] childs = selectedRootObject.listFiles();
////        this.objects.addAll(Arrays.asList(childs));
//        for (File f : childs) {
//            UFile uf = new UFile(f.getAbsolutePath());
//            this.objects.add(uf);
//        }
//        this.selectedObject = null;
//    }
//
//    public final void loadObjects() {
//        String path;
//        if (selectedRootObject == null) {
//            path = rootDir + inputDir;
//            logger.info(rootDir + inputDir);
//            this.selectedRootObject = new UFile(path);
//            if (!this.selectedRootObject.exists()) {
//                this.selectedRootObject.mkdirs();
//            }
//        }
//        if (this.objects != null && !this.objects.isEmpty()) {
//            this.objects.clear();
//        }
//        this.objects = new ArrayList();
//        File[] childs = selectedRootObject.listFiles();
////        this.objects.addAll(Arrays.asList(childs));
//        for (File f : childs) {
//            UFile uf = new UFile(f.getAbsolutePath());
//            this.objects.add(uf);
//        }
//        this.selectedObject = null;
//    }
//
//    public void setObjects(List<UFile> objects) {
//        this.objects = objects;
//    }
//
//    public List<UFile> getObjects() {
//        return objects;
//    }
//
//    public void setSelectedObject(UFile selectedObject) {
//        this.selectedObject = selectedObject;
//    }
//
//    public UFile getSelectedObject() {
//        return selectedObject;
//    }
//
//    public void createFolder() {
//        if (this.getNewFolder() != null) {
//            String path = this.selectedRootObject.getAbsolutePath().replaceAll("\\\\", "/") + "/" + this.getNewFolder();
//            logger.info("new folder:" + path);
//            File newPath = new File(path);
//            if (newPath.mkdirs()) {
//                this.loadObjects();
//            }
//        }
//    }
//
//    public void doRenameFile() {
//        if (this.selectedObject != null) {
//            if (this.selectedObject.isFile()) {
//                String name = this.getRenameFile();
//                String prefix = Global.getPrefixFileName(name);
//                String tail = Global.getTailFile(name);
//                name = Global.convertStringFilename(prefix) + "." + tail;
//                UFile newFile = new UFile(this.getSelectedObject().getParent() + "/" + name);
//                logger.info(newFile.getAbsolutePath());
//                if (this.selectedObject.renameTo(newFile)) {
//                    this.loadObjects();
//                }
//            } else {
//                String name = Global.convertStringFilename(this.getRenameFile());
//                UFile newFile = new UFile(this.getSelectedObject().getParent() + "/" + name);
//                logger.info(newFile.getAbsolutePath());
//                if (this.selectedObject.renameTo(newFile)) {
//                    this.loadObjects();
//                }
//            }
//        } else {
//            logger.info("Selected File is null");
//        }
//
//    }
//
//    public void deleteObject() {
//        if (this.selectedObject != null) {
//            try {
//                FileUtils.forceDelete(new File(this.getSelectedObject().getAbsolutePath()));
//                this.loadObjects();
//            } catch (IOException ex) {
//                logger.error(ex);
//            }
//
//        }
//    }
//
//    public void deleteObjects() {
//        if (this.selectedObjects != null) {
//            for (UFile selectedObject1 : this.selectedObjects) {
//                try {
//                    FileUtils.forceDelete(new File(selectedObject1.getAbsolutePath()));
//                } catch (IOException ex) {
//                    logger.error(ex);
//                }
//            }
//            this.loadObjects();
//        }
//    }
//
//    public void deleteAll() {
//        if (this.objects != null) {
//            for (UFile selectedObject1 : this.objects) {
//                try {
//                    FileUtils.forceDelete(new File(selectedObject1.getAbsolutePath()));
//                } catch (IOException ex) {
//                    logger.error(ex);
//                }
//            }
//            this.loadObjects();
//        }
//    }
//
//    public void handleFileUpload1(FileUploadEvent event) {
//        UploadedFile f = event.getFile();
//        String filename = f.getFileName();
//        logger.info("File:" + filename);
//    }
//
//    public void handleFileUpload(FileUploadEvent event) {
//        UploadedFile f = event.getFile();
//        String filename = f.getFileName();
//        logger.info("File:" + filename);
//        String prefix = Global.getPrefixFileName(filename);
//        String tail = Global.getTailFile(filename);
//        filename = Global.convertStringFilename(prefix) + "." + tail;
//        File saveFile = Global.getNewFileName(new File(this.getSelectedRootObject().getAbsolutePath() + "/" + filename));
//        logger.info(saveFile.getAbsolutePath());
//        try {
//            final int BUFFER_SIZE = 1024;
//            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
//            byte[] buffer = new byte[BUFFER_SIZE];
//            int bulk;
//            InputStream inputStream = f.getInputstream();
//            while (true) {
//                bulk = inputStream.read(buffer);
//                if (bulk < 0) {
//                    break;
//                }
//                fileOutputStream.write(buffer, 0, bulk);
//                fileOutputStream.flush();
//            }
//            fileOutputStream.close();
//            inputStream.close();
//            this.loadObjects();
//        } catch (IOException ex) {
//            logger.error("Error:" + ex);
//        }
//    }
//
//    /**
//     * @return the rootDir
//     */
//    public String getRootDir() {
//        return rootDir;
//    }
//
//    /**
//     * @param rootDir the rootDir to set
//     */
//    public void setRootDir(String rootDir) {
//        this.rootDir = rootDir;
//    }
//
//    /**
//     * @return the currentDir
//     */
//    public String getSelectedPath() {
//        if (this.selectedObject != null) {
//            logger.info(this.selectedObject.getAbsolutePath());
//            this.selectedPath = this.selectedObject.getAbsolutePath().replaceAll("\\\\", "/").replace(this.getRootDir(), "");
//            this.selectedPath = this.selectedPath.replaceAll("\\\\", "/");
//        }
//        return selectedPath;
//    }
//
//    public String getRelativePath() {
//        return this.selectedRootObject.getAbsolutePath().replaceAll("\\\\", "/").replace(this.getRootDir(), "");
//    }
//
//    /**
//     * @param selectedPath
//     */
//    public void setSelectedPath(String selectedPath) {
//        this.selectedPath = selectedPath;
//    }
//
//    /**
//     * @return the newFolder
//     */
//    public String getNewFolder() {
//        return newFolder;
//    }
//
//    /**
//     * @param newFolder the newFolder to set
//     */
//    public void setNewFolder(String newFolder) {
//        this.newFolder = newFolder;
//    }
//
//    /**
//     * @return the selectedRootObject
//     */
//    public UFile getSelectedRootObject() {
//        return selectedRootObject;
//    }
//
//    /**
//     * @param selectedRootObject the selectedRootObject to set
//     */
//    public void setSelectedRootObject(UFile selectedRootObject) {
//        this.selectedRootObject = selectedRootObject;
//    }
//
//    /**
//     * @return the renameFile
//     */
//    public String getRenameFile() {
//        return renameFile;
//    }
//
//    /**
//     * @param renameFile the renameFile to set
//     */
//    public void setRenameFile(String renameFile) {
//        this.renameFile = renameFile;
//    }
//
//    public void doUp() {
//        UFile f = new UFile(this.getSelectedRootObject().getParent());
//        UFile root = new UFile(this.getRootDir() + "/" + UserSessionBean.getUserSession().getUser().getGroupid());
//        if (!f.getAbsolutePath().equals(root.getAbsolutePath())) {
//            this.selectedRootObject = f;
//            this.loadObjects();
//        }
//    }
//
//    public void doHomeFolder() {
//        this.selectedRootObject = new UFile(this.rootDir + this.inputDir);
//        if (!this.selectedRootObject.exists()) {
//            this.selectedRootObject.mkdir();
//        }
//        this.loadObjects();
//
//    }
//
//    /**
//     * @return the inputDir
//     */
//    public String getInputDir() {
//        return inputDir;
//    }
//
//    /**
//     * @param inputDir the inputDir to set
//     */
//    public void setInputDir(String inputDir) {
//        this.inputDir = inputDir;
//    }
//
//    /**
//     * @return the selectedObjects
//     */
//    public UFile[] getSelectedObjects() {
//        return selectedObjects;
//    }
//
//    /**
//     * @param selectedObjects the selectedObjects to set
//     */
//    public void setSelectedObjects(UFile[] selectedObjects) {
//        this.selectedObjects = selectedObjects;
//    }
//
//    /**
//     * @return the file
//     */
//    public StreamedContent getFile() {
//        if (this.selectedObject.isFile()) {
//            InputStream stream;
//            try {
//                stream = new FileInputStream(this.getSelectedObject());
//                file = new DefaultStreamedContent(stream, new MimetypesFileTypeMap().getContentType(this.getSelectedObject()), this.getSelectedObject().getName());
//                return file;
//            } catch (FileNotFoundException ex) {
//                logger.error(ex);
//                return null;
//            }
//        } else {
//            try {
//                String[] files = this.selectedObject.list();
//                if (files != null && files.length > 0) {
//                    byte[] zip = Global.zipFiles(this.selectedObject, files);
//                    InputStream stream = new ByteArrayInputStream(zip);
//                    file = new DefaultStreamedContent(stream, "application/zip", this.getSelectedObject().getName() + ".zip");
//                    return file;
//                }
//            } catch (IOException ex) {
//                logger.error(ex);
//                return null;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * @param file the file to set
//     */
//    public void setFile(StreamedContent file) {
//        this.file = file;
//    }

}
