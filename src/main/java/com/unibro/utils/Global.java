package com.unibro.utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unibro.controller.file.FileController;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import jdbchelper.JdbcHelper;
import jdbchelper.SimpleDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

/**
 *
 * @author NGUYEN DUC THO
 */
public class Global {

    private static final Logger logger = Logger.getLogger(Global.class.getName());

    public static JdbcHelper jdbc;

    public static ClassLoader getCurrentClassLoader(Object defaultObject) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = defaultObject.getClass().getClassLoader();
        }
        return loader;
    }

    public static String FILE_ROOT_PATH = "";
    public static String FILE_HTTP_PATH = "";
    public static String FILE_PRIVATE_PATH = "";
    public static String FILE_SITE_PATH = "";

    public static String T3_URL = "t3://localhost:80";
    public static String T3_USER = "system";
    public static String T3_PASSWORD = "system@l0tek";
    public static String DATASOURCE = "IWeb";

    private static boolean isLoadConfigure = false;
    public static String listAllCountry;
    public static String EXTENSION = "html";

    public static MemCachedClient getMemCachedClient(String server) {
        String[] servers = {server + ":11211"};
        SockIOPool pool = SockIOPool.getInstance("Account1");
        pool.setServers(servers);
        pool.setFailover(true);
        pool.setInitConn(5);
        pool.setMinConn(1);
        pool.setMaxConn(10);
        pool.setMaintSleep(30);
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setAliveCheck(true);
        pool.initialize();
        return new MemCachedClient("Account1");
    }

    public static String getConfigValue(String key) {
        try {
            Properties p = Global.getConfigProperties("config");
            return p.getProperty(key);
        } catch (IOException ex) {
            logger.error("getConfigValue:" + ex);
            return null;
        }
    }

    public static void loadConfig() {
        if (isLoadConfigure) {
            return;
        }
        try {
            Properties p = Global.getConfigProperties("config");
            T3_URL = p.getProperty("T3_URL");
            T3_USER = p.getProperty("T3_USER");
            T3_PASSWORD = p.getProperty("T3_PASSWORD");
            DATASOURCE = p.getProperty("DATASOURCE");
            FILE_ROOT_PATH = p.getProperty("FILE_ROOT_PATH");
            FILE_HTTP_PATH = p.getProperty("FILE_HTTP_PATH");
            FILE_PRIVATE_PATH = p.getProperty("FILE_PRIVATE_PATH");
            FILE_SITE_PATH = p.getProperty("FILE_SITE_PATH");
            EXTENSION = p.getProperty("EXTENSION");

//            HttpClient client = new HttpClient();
//            client.setGetMethod(true);
//            client.setUrlBase("http://restcountries.eu/rest/v1/all");
//            Global.listAllCountry = client.getDataByGetMethod(null, null);
            isLoadConfigure = true;
        } catch (IOException ex) {
            logger.error("loadConfig:" + ex);
        } catch (JSONException ex) {
            logger.error(ex);
        }
    }

//    public static void synFile(final File f) {
//        try {
//            Thread t = new Thread() {
//                @Override
//                public void run() {
//                    Global.loadConfig();
//                    Global.excuteProgram("/bin/chmod o+r " + f.getAbsolutePath());
//                    for (int i = 0; i < Global.remoteServer.length(); i++) {
//                        String address = Global.remoteServer.getString(i);
//                        //Create folder on remote site
//                        Global.excuteProgram("/usr/bin/rmkdir.sh " + address + " " + f.getParent());
//                        Global.excuteProgram("/usr/bin/scp -r " + f.getAbsolutePath() + " " + address + ":" + f.getParent());
//                    }
//                }
//            };
//            t.start();
//        } catch (JSONException ex) {
//            logger.error(ex);
//        }
//    }
    public static String getResourceLanguage(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        return getMessageResourceString("config.language", key, null, context.getViewRoot().getLocale());
    }

    public static String getMessageResourceString(String bundleName, String key, Object params[], Locale locale) {
        String text;
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentClassLoader(params));
        try {
            text = bundle.getString(key);
        } catch (MissingResourceException e) {
            text = "?? key " + key + " not found ??";
        }
        if (params != null) {
            MessageFormat mf = new MessageFormat(text, locale);
            text = mf.format(params, new StringBuffer(), null).toString();
        }
        return text;
    }

    public static Properties getLanguageProperties(String language) throws IOException {
        Properties props = new Properties();
        props.load(Global.class.getClassLoader().getResourceAsStream("config/" + language + "_language.properties"));
        return props;
    }

    public static Properties getConfigProperties(String cfgfile) throws IOException {
        Properties props = new Properties();
        props.load(Global.class.getClassLoader().getResourceAsStream("config/" + cfgfile + ".properties"));
        return props;
    }

    public static java.sql.Date getCurrentSqlDatetime() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Date(today.getTime());
    }

    public static java.sql.Date convertDateToSqlDate(java.util.Date today) {
        if (today != null) {
            return new java.sql.Date(today.getTime());
        }
        return null;
    }

    public static java.sql.Timestamp convertDateToSqlTimestamp(java.util.Date today) {
        if (today != null) {
            return new java.sql.Timestamp(today.getTime());
        }
        return null;
    }

    public static java.util.Date convertSqlTimeStampToDate(java.sql.Timestamp today) {
        if (today != null) {
            return new java.util.Date(today.getTime());
        }
        return null;
    }

    public static java.util.Date convertSqlDateToDate(java.sql.Date today) {
        if (today != null) {
            return new java.util.Date(today.getTime());
        }
        return null;
    }

    public static String getRandomString() {
        return MD5(UUID.randomUUID().toString());
    }

    public static int[] excutePrograms(String[] commands) {
        try {
            int[] ret = new int[commands.length];
            for (int i = 0; i < commands.length; i++) {
                logger.info("Excute Command:" + commands[i]);
                Process p = Runtime.getRuntime().exec(commands[i]);
                int exitValue = p.waitFor();
                ret[i] = exitValue;
                logger.info("Exit value = " + exitValue);
            }
            return ret;
        } catch (IOException ex) {
            logger.error("excutePrograms:" + ex);
            return null;
        } catch (InterruptedException ex) {
            logger.error("excutePrograms:" + ex);
            return null;
        }
    }

    public static int excuteProgram1(String[] commands) {
        try {
            Process p = Runtime.getRuntime().exec(commands);
            int exitValue = p.waitFor();
            //ret[i]=exitValue;
            logger.info("Exit value = " + exitValue);
            //}
            return exitValue;
        } catch (IOException ex) {
            logger.error("excuteProgram1:" + ex);
            return -1;
        } catch (InterruptedException ex) {
            logger.error("excuteProgram1:" + ex);
            return -1;
        }
    }

    public static int excuteProgram(String commands) {
        try {
            logger.info("Excute Command:" + commands);
            Process p = Runtime.getRuntime().exec(commands);
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            new SyncPipe(p.getInputStream(), System.out).run();
            int exitValue = p.waitFor();
            logger.info("Exit value = " + exitValue);
            return exitValue;
        } catch (IOException ex) {
            logger.error(ex);
            return -1;
        } catch (InterruptedException ex) {
            logger.error(ex);
            return -1;
        }
    }

    public static String convertToUnsigned(String Text) {
        String retval = Text;
        String[] replaceString1 = {"ă", "ắ", "ằ", "ẳ", "ẵ", "ặ", "â", "ấ", "ầ", "ả", "ẵ", "ặ", "á", "à", "ẵ", "ả", "ạ"};
        String[] replaceString2 = {"é", "è", "ẻ", "ẽ", "ẹ", "ê", "ế", "ề", "ể", "ễ", "ệ"};
        String[] replaceString3 = {"í", "ì", "ỉ", "ĩ", "ị"};
        String[] replaceString4 = {"ó", "ò", "ỏ", "õ", "ọ", "ô", "ố", "ồ", "ổ", "ỗ", "ộ", "ơ", "ớ", "ờ", "ở", "ỡ", "ợ"};
        String[] replaceString5 = {"ú", "ù", "ủ", "ũ", "ụ", "ư", "ứ", "ừ", "ử", "ữ", "ự"};
        String[] replaceString6 = {"ý", "ỳ", "ỷ", "ỹ", "ỵ"};
        String[] replaceString7 = {"đ"};

        for (String replaceString11 : replaceString1) {
            retval = retval.replaceAll(replaceString11, "a");
        }
        for (String replaceString21 : replaceString2) {
            retval = retval.replaceAll(replaceString21, "e");
        }
        for (String replaceString31 : replaceString3) {
            retval = retval.replaceAll(replaceString31, "i");
        }
        for (String replaceString41 : replaceString4) {
            retval = retval.replaceAll(replaceString41, "o");
        }
        for (String replaceString51 : replaceString5) {
            retval = retval.replaceAll(replaceString51, "u");
        }
        for (String replaceString61 : replaceString6) {
            retval = retval.replaceAll(replaceString61, "y");
        }
        for (String replaceString71 : replaceString7) {
            retval = retval.replaceAll(replaceString71, "d");
        }
        for (String replaceString11 : replaceString1) {
            retval = retval.replaceAll(replaceString11.toUpperCase(), "A");
        }
        for (String replaceString21 : replaceString2) {
            retval = retval.replaceAll(replaceString21.toUpperCase(), "E");
        }
        for (String replaceString31 : replaceString3) {
            retval = retval.replaceAll(replaceString31.toUpperCase(), "I");
        }
        for (String replaceString41 : replaceString4) {
            retval = retval.replaceAll(replaceString41.toUpperCase(), "O");
        }
        for (String replaceString51 : replaceString5) {
            retval = retval.replaceAll(replaceString51.toUpperCase(), "U");
        }
        for (String replaceString61 : replaceString6) {
            retval = retval.replaceAll(replaceString61.toUpperCase(), "Y");
        }
        for (String replaceString71 : replaceString7) {
            retval = retval.replaceAll(replaceString71.toUpperCase(), "D");
        }
        return retval;
    }

    public static String convertStringFilename(String filename) {
        String[] delCharacter = {"\\~", "\\!", "\\@", "\\$", "%20", "\\^", " ", "\\%", "\\&", "\\*", "\\(", "\\)", "\\+", "\\=", "\\{", "\\}", "\\[", "\\]", "\\:", "\\;", "\"", "\\'", "\\<", "\\,", "\\>", "\\.", "\\?", "\\/"};
        String f = filename;
        for (String delCharacter1 : delCharacter) {
            f = f.replaceAll(delCharacter1, "_");
        }
        f = Global.convertToUnsigned(f);
        return f;
    }

    public static String getTailFile(String filename) {
        int i = filename.lastIndexOf(".");
        if (i >= 0) {
            return filename.substring(i + 1);
        }
        return "dat";
    }

    public static String getPrefixFileName(String filename) {
        int i = filename.lastIndexOf(".");
        if (i >= 0) {
            return filename.substring(0, i);
        }
        return filename;
    }

    public static String getNewRandomFileName(String filename) {
        //String prefix=getPrefixFile(filename);
        String tail = getTailFile(filename);
        //prefix=convertStringFilename(prefix);
        //return prefix + "." + tail;
        return Global.getRandomString() + "." + tail;
    }

    public static String getNewStandardFilename(String filename) {
        String prefix = getPrefixFileName(filename);
        String tail = getTailFile(filename);
        prefix = convertStringFilename(prefix);
        return prefix + "." + tail;
    }

    public static File getNewFileName(File f) {
        if (!f.exists()) {
            return f;
        } else {
            String newName = "new" + f.getName();
            return getNewFileName(new File(f.getParent() + "/" + newName));
        }
    }

    public static String getNewFilenameInTime(String filename) {
        String prefix = getPrefixFileName(filename);
        //prefix=convertFilename(prefix);
        String tail = getTailFile(filename);
        Calendar cal = Calendar.getInstance();
        return prefix + cal.getTimeInMillis() + "." + tail;
    }

    public static File convertImageFile(int width, int height, File origin, String convertType) {
        try {
            BufferedImage originalImage = ImageIO.read(origin);
            //int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizeImageJpg = resize(originalImage, width, height);
            String filename = origin.getName();
            String newfilename = filename;
            int i = filename.lastIndexOf(".");
            if (i >= 0) {
                newfilename = filename.substring(0, i) + "." + convertType;
            }
            File retFile;
            if (filename.toLowerCase().equals(newfilename.toLowerCase())) {
                retFile = origin;
            } else {
                retFile = new File(origin.getAbsolutePath() + "/" + newfilename);
            }
            ImageIO.write(resizeImageJpg, convertType, retFile);
            if (retFile.exists() && (!filename.toLowerCase().equals(newfilename.toLowerCase()))) {
                origin.delete();
            }
            return retFile;
        } catch (IOException e) {
            logger.error("Error:" + e);
            return origin;
        }
    }

    public static File convertImageFile(int width, int height, File origin) {
        try {
            BufferedImage originalImage = ImageIO.read(origin);
            //int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizeImageJpg = resize(originalImage, width, height);
            String filename = origin.getName();
            int i = filename.lastIndexOf(".");
            String filetype = filename.substring(i + 1);
            if (filetype.equals("GIF") || filetype.equals("gif")) {
                return origin;
            }
            ImageIO.write(resizeImageJpg, filetype, origin);
            return origin;
        } catch (IOException e) {
            logger.error("Error:" + e);
            return origin;
        }
    }

    public static File convertImageFileWidth(int width, File origin, String convertType) {
        try {
            BufferedImage originalImage = ImageIO.read(origin);
            int w = originalImage.getWidth();
            int h = originalImage.getHeight();
            int height = h * width / w;
            //int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizeImageJpg = resize(originalImage, width, height);
            String filename = origin.getName();
            String newfilename = filename;
            int i = filename.lastIndexOf(".");
            if (i >= 0) {
                newfilename = filename.substring(0, i) + "." + convertType;
            }
            File retFile;
            if (filename.toLowerCase().equals(newfilename.toLowerCase())) {
                retFile = origin;
            } else {
                retFile = new File(origin.getAbsolutePath() + "/" + newfilename);
            }
            ImageIO.write(resizeImageJpg, convertType, retFile);
            if (retFile.exists() && (!filename.toLowerCase().equals(newfilename.toLowerCase()))) {
                origin.delete();
            }
            return retFile;
        } catch (IOException e) {
            logger.error("Error:" + e);
            return origin;
        }
    }

    public static File convertImageFileWidth(int width, File origin) {
        try {
            BufferedImage originalImage = ImageIO.read(origin);
            int w = originalImage.getWidth();
            int h = originalImage.getHeight();
            int height = h * width / w;
            //int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizeImageJpg = resize(originalImage, width, height);
            String filename = origin.getName();
            int i = filename.lastIndexOf(".");
            String filetype = filename.substring(i + 1);
            if (filetype.equals("GIF") || filetype.equals("gif")) {
                return origin;
            }
            ImageIO.write(resizeImageJpg, filetype, origin);
            return origin;
        } catch (IOException e) {
            logger.error("Error:" + e);
            return origin;
        }
    }

    public static void convertImageFileWidth(int width, File origin, File newFile) {
        try {
            BufferedImage originalImage = ImageIO.read(origin);
            int w = originalImage.getWidth();
            int h = originalImage.getHeight();
            int height = h * width / w;
            //int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizeImageJpg = resize(originalImage, width, height);
            String filename = origin.getName();
            int i = filename.lastIndexOf(".");
            String filetype = filename.substring(i + 1);
            ImageIO.write(resizeImageJpg, filetype, newFile);
        } catch (IOException e) {
            logger.error("Error:" + e);
        }
    }

    public static File convertImageFileHeight(int height, File origin, String convertType) {
        try {
            BufferedImage originalImage = ImageIO.read(origin);
            int w = originalImage.getWidth();
            int h = originalImage.getHeight();
            int width = height * w / h;
            //int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizeImageJpg = resize(originalImage, width, height);
            String filename = origin.getName();
            String newfilename = filename;
            int i = filename.lastIndexOf(".");
            if (i >= 0) {
                newfilename = filename.substring(0, i) + "." + convertType;
            }
            File retFile;
            if (filename.toLowerCase().equals(newfilename.toLowerCase())) {
                retFile = origin;
            } else {
                retFile = new File(origin.getAbsolutePath() + "/" + newfilename);
            }
            ImageIO.write(resizeImageJpg, convertType, retFile);
            if (retFile.exists() && (!filename.toLowerCase().equals(newfilename.toLowerCase()))) {
                origin.delete();
            }
            return retFile;
        } catch (IOException e) {
            logger.error("Error:" + e);
            return origin;
        }

    }

    public static File convertImageFileHeight(int height, File origin) {
        try {
            BufferedImage originalImage = ImageIO.read(origin);
            int w = originalImage.getWidth();
            int h = originalImage.getHeight();
            int width = height * w / h;
            //int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

            BufferedImage resizeImageJpg = resize(originalImage, width, height);
            String filename = origin.getName();
            int i = filename.lastIndexOf(".");
            String filetype = filename.substring(i + 1);
            ImageIO.write(resizeImageJpg, filetype, origin);
            return origin;
        } catch (IOException e) {
            logger.error("Error:" + e);
            return origin;
        }

    }

    private static BufferedImage resize(BufferedImage image, int width, int height) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage blurImage(BufferedImage image) {
        float ninth = 1.0f / 9.0f;
        float[] blurKernel = {
            ninth, ninth, ninth,
            ninth, ninth, ninth,
            ninth, ninth, ninth
        };

        Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
        map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RenderingHints hints = new RenderingHints(map);
        BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
        return op.filter(image, null);
    }

    public static String getDateFromDotNetJson(String json) {
        int i = json.indexOf("(");
        int j = json.indexOf(")");
        String jsondate = json.substring(i + 1, j);
        int k = jsondate.indexOf("+");

        if (k >= 0) {
            jsondate = jsondate.substring(0, k);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(jsondate));
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        return format.format(cal.getTime());
    }
//  public static Date getDateFromJson(String string) {
//       try{
//         int i=string.indexOf("(");
//         int j=string.indexOf(")",i);
//         String longValueStr=string.substring(i+1,j);
//         long longValue=Long.parseLong(longValueStr);
//         Calendar cal=Calendar.getInstance();
//         cal.setTimeInMillis(longValue);
//         return cal.getTime();
//       }catch(Exception ex){
//          return new Date(); 
//       }
//  }

    public static Date getDateFromJson(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.getTime();
    }

    public static Date getDateFromJson(String string) {
        try {
            long longValue = Long.parseLong(string);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(longValue);
            return cal.getTime();
        } catch (NumberFormatException ex) {
            return new Date();
        }
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String getFolderNameByTime() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH);
    }

    public static File createThumbPictureFromVideo(String size, String videoFile, String position, String imageFile) {
        if (Global.excuteProgram1(new String[]{"/usr/local/bin/ffmpeg", "-itsoffset", "-4", "-i", videoFile, "-ss", position, "-vcodec", "mjpeg", "-vframes", "1", "-an", "-f", "rawvideo", "-s", size, "-y", imageFile}) == 0) {
            File f1 = new File(imageFile);
            return f1;
        }
        return null;
    }

    public static String createFolder(String root, String userid) {
        Calendar cal = Calendar.getInstance();
        String extension = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + "" + (cal.get(Calendar.DAY_OF_MONTH));
        File f = new File(root + "/" + userid + "/" + extension);
        f.mkdirs();
        Global.excuteProgram("/bin/chmod -R o+rx " + f.getAbsolutePath());
        return userid + "/" + extension;
    }

//    public static JdbcHelper getJDBCHelper() {
//        if (Global.jdbc == null) {
//            try {
//                Global.loadConfig();
//                Context ic = getWebLogicContext(Global.T3_URL, Global.T3_USER, Global.T3_PASSWORD);
//                DataSource ds = (DataSource) ic.lookup(Global.DATASOURCE);
//                Global.jdbc = new JdbcHelper(ds);
//            } catch (NamingException ex) {
//                logger.info("getJDBCHelper: " + ex);
//            }
//        }
//        return Global.jdbc;
//    }
    public static JdbcHelper getJDBCHelper() {
        if (Global.jdbc == null) {
            Global.loadConfig();
            DataSource ds = new SimpleDataSource(Global.getConfigValue("db.driverclass"),
                    Global.getConfigValue("db.url"), Global.getConfigValue("db.username"), Global.getConfigValue("db.password"));
            Global.jdbc = new JdbcHelper(ds);
        }
        return Global.jdbc;
    }

//    public static Connection getDbConnection() {
//        Connection conn = null;
//        try {
//            Global.loadConfig();
//            Context ic = getWebLogicContext(Global.T3_URL, Global.T3_USER, Global.T3_PASSWORD);
//            DataSource ds = (DataSource) ic.lookup(Global.DATASOURCE);
//            conn = ds.getConnection();
//        } catch (NamingException ex) {
//            logger.info("getDBConnection: " + ex);
//        } catch (SQLException ex) {
//            logger.info("getDBConnection: " + ex);
//        }
//        return conn;
//    }
//    public static Connection getDbConnection() {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            logger.info("Where is your MySQL JDBC Driver?");
//            return null;
//        }
//        logger.info("MySQL JDBC Driver Registered!");
//        Connection connection;
//        try {
//            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_stream", "root", "123qwe!");
//            return connection;
//        } catch (SQLException e) {
//            logger.info("Connection Failed! Check output console");
//            return null;
//        }
//    }
    private static Context getWebLogicContext(String url, String userName, String PWD) throws NamingException {
        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        p.put(Context.PROVIDER_URL, url);
        if (userName != null) {
            p.put(Context.SECURITY_PRINCIPAL, userName);
            if (PWD == null) {
                PWD = "";
            }
            p.put(Context.SECURITY_CREDENTIALS, PWD);
        }
        return new InitialContext(p);
    }

//    public static boolean convertPPT(String source, String dest) {
//        try {
//            FileInputStream is = new FileInputStream(source);
//            SlideShow ppt = new SlideShow(is);
//            is.close();
//            Dimension pgsize = ppt.getPageSize();
//            Slide[] slide = ppt.getSlides();
//            for (int i = 0; i < slide.length; i++) {
//                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, 1);
//                Graphics2D graphics = img.createGraphics();
//                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
//                        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//                graphics.setColor(Color.white);
//                graphics.clearRect(0, 0, pgsize.width, pgsize.height);
//                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
//                // render
//                slide[i].draw(graphics);
//                // save the output
//                File out = new File(dest + "/img" + (i + 1) + ".png");
//                javax.imageio.ImageIO.write(img, "png", out);
//                //out.close();
//            }
//            return true;
//        } catch (IOException ex) {
//            logger.info("convertPPT" + ex);
//            return false;
//        }
//    }
//
//    public static boolean convertPDF(String source, String dest) {
//        String cmd = Global.CONTENT_PRESENTATION_PDF_CMD;
//        cmd = cmd.replaceAll("source", source);
//        cmd = cmd.replaceAll("dest", dest + "/img.png");
//        return Global.excuteProgram(cmd) == 0;
//    }
//
//    public static boolean convertPresentation(String source, String dest) {
//        if (source.toLowerCase().endsWith("ppt") || source.toLowerCase().endsWith("pptx")) {
//            return convertPPT(source, dest);
//        }
//        if (source.toLowerCase().endsWith("pdf")) {
//            return convertPDF(source, dest);
//        }
//        return false;
//    }
//    public static String getHttpPrefix(String country){
//        Global.loadConfig();
//        for(int i=0;i<Global.memcachedServer.length();i++){
//            JSONObject obj=Global.memcachedServer.getJSONObject(i);
//            if(obj.get("country").equals(country)){
//                return obj.getString("prefix");
//            }
//        }
//        return Global.CONTENT_ICON_HTTP_PATH;
//    }
//    public static Connection getDbConnection(){
//       try{
//           Global.loadConfig();
//           Properties prop = new Properties();
//           prop.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
//           prop.put(javax.naming.Context.PROVIDER_URL, "t3://localhost:8080");
//           prop.put(Context.SECURITY_PRINCIPAL, "admin");
//           prop.put(Context.SECURITY_CREDENTIALS, "1234qwer");
//           Context ctx = new InitialContext(prop);
//           Object obj = ctx.lookup("jdbc/" + Global.DB_DATASOURCE);
//           logger.info("Data Source " + Global.DB_DATASOURCE + " Foundâ€¦.");
//           DataSource ds = (DataSource) obj;
//           return ds.getConnection(); 
//       }catch(Exception e){
//           logger.info("Get Connection Source:" + e);
//            //or handle more gracefully 
//            return null;
//       }
//    }
    public static void closeConnection(Connection connection, Statement statement, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exp) {
        }
    }

    public static byte[] zipFiles(File directory, String[] files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte bytes[] = new byte[2048];

        for (String fileName : files) {
            FileInputStream fis = new FileInputStream(directory.getPath()
                    + FileController.FILE_SEPARATOR + fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);

            zos.putNextEntry(new ZipEntry(fileName));

            int bytesRead;
            while ((bytesRead = bis.read(bytes)) != -1) {
                zos.write(bytes, 0, bytesRead);
            }
            zos.closeEntry();
            bis.close();
            fis.close();
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
        return baos.toByteArray();
    }

    public static String getDBName() {
        return Global.getConfigValue("db.name");
    }

    public static Gson getGsonObject(String dateformat) {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(dateformat);
        Gson gson = builder.create();
        return gson;
    }

    public static class StringConverter implements JsonSerializer<String>,
            JsonDeserializer<String> {

        public JsonElement serialize(String src, Type typeOfSrc,
                JsonSerializationContext context) {
            if (src == null) {
                return new JsonPrimitive("");
            } else {
                return new JsonPrimitive(src);
            }
        }

        public String deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context)
                throws JsonParseException {
            logger.info(typeOfT.getTypeName() + ":" + json.toString());
            return json.getAsJsonPrimitive().getAsString();
        }
    }

    public static Gson getGsonObject() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("dd/MM/yyyy'T'HH:mm:ssZ");
        Gson gson = builder.create();
        return gson;
    }

    public static Date getDateFromString(String date, String format) {
        try {
            SimpleDateFormat simple_format = new SimpleDateFormat(format);
            return simple_format.parse(date);
        } catch (ParseException ex) {
            return new java.util.Date();
        }
    }

    public static Date getRealDateFromString(String date, String format) {
        try {
            SimpleDateFormat simple_format = new SimpleDateFormat(format);
            return simple_format.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }

//    public static void sendPushToAndroid(String activity,String room,String toId,String title, String body, String regid) {
//        try {
//            org.apache.http.client.HttpClient httpClient = HttpClientBuilder.create().build();
//            HttpPost post = new HttpPost(Global.getConfigValue("FCM_URL"));
//            post.setHeader("Content-type", "application/json");
//            post.setHeader("Authorization", "key=" + Global.getConfigValue("FCM_KEY"));
//            
//            JsonObject message = new JsonObject();
//            message.addProperty("to", regid);
//            message.addProperty("priority", "high");
//            
////            JsonObject notification = new JsonObject();
////            notification.addProperty("title", title);
////            notification.addProperty("body", body);
////            notification.addProperty("activity",activity);
////            notification.addProperty("room",room);
////            notification.addProperty("toId",toId);
////            notification.addProperty("click_action",activity);
////            message.add("notification", notification);
//            
//            JsonObject data = new JsonObject();
//            data.addProperty("activity", activity);
//            data.addProperty("room",room);
//            data.addProperty("toId",toId);
//            data.addProperty("title", title);
//            data.addProperty("body", body);
//            
//            message.add("data", data);
//            
//            post.setEntity(new StringEntity(message.toString(), "UTF-8"));
//            BasicResponseHandler responseHandler = new BasicResponseHandler();
//            String response = (String) httpClient.execute(post, responseHandler);
//            logger.info("Http Response:" + response);
//        } catch (IOException ex) {
//            logger.error("Error:" + ex);
//        }
//    }
    public static String getDateInStringFormat(String format, java.util.Date date) {
        SimpleDateFormat s_format = new SimpleDateFormat(format);
        return s_format.format(date);
    }

    public static String getDataByPost(String root_url, String path, String secure_code, String[] params, String[] values) {
        try {
            String url = root_url + "/" + path + "/" + secure_code;
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps;
            nvps = new ArrayList();
            for (int i = 0; i < params.length; i++) {
                nvps.add(new BasicNameValuePair(params[i], values[i]));
            }
            UrlEncodedFormEntity form;
            form = new UrlEncodedFormEntity(nvps, "UTF-8");
            httpPost.setEntity(form);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            System.out.println(response.getStatusLine());
            HttpEntity entity2 = response.getEntity();
            String ret = EntityUtils.toString(entity2);
            logger.info("Api return: " + path + ":" + ret);
            return ret;
//            // do something useful with the response body
//            // and ensure it is fully consumed
//            EntityUtils.consume(entity2);
        } catch (UnsupportedEncodingException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return null;
    }

    public static boolean replaceString(File f, String old_str, String new_str) {
        if (f.exists()) {
            try {
                String content = FileUtils.readFileToString(f);
                content = content.replace(old_str, new_str);
                logger.info("New content:" + content);
                FileUtils.writeStringToFile(f, content);
            } catch (IOException ex) {
                return false;
            }

        }
        return false;
    }

}
