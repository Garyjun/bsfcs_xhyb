/**
 * 
 */
package com.brainsoon.common.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.pdf.PdfReader;

/**
 * BSRCM应用com.brainsoon.bsrcm.common.support.ReadOnlineTool.java 创建时间：2011-11-7
 * 创建者： liusy ReadOnlineTool TODO
 * 
 */
public class ReadOnlineTool {
	private static Log logger = LogFactory.getLog(ReadOnlineTool.class);
    public static int createPages;

    /**
     * @param cmd
     * @param maxPages
     * @param thisIndex
     * @param pdfFile
     * @param fileName
     * @param fileTimePath
     * @return
     * @throws IOException
     */
    private static boolean initProcess(StringBuffer cmd, int thisIndex,
            int maxPages, String fileTempPath, File pdfFile, String fileName)
            throws IOException {
        cmd.append(ScaConfigUtil.getParameter("swftoolsPath"));
        int atomPages = Integer.parseInt(ScaConfigUtil.getParameter("atomPages"));
        createPages = atomPages;
        File parentFile = new File(ScaConfigUtil.getParameter("swfFilePackage")
                + File.separatorChar + fileTempPath);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        int begin = 1;
        int end = maxPages;
        if (thisIndex - atomPages > 0) {
            /*begin = thisIndex - atomPages;*/
        }
        if (thisIndex + atomPages < maxPages) {
            /*end = thisIndex + atomPages;*/
        	end = maxPages;
        }
        boolean needPdf2Swf = false;
        StringBuffer pageNumBuffer = new StringBuffer();
        for (int i = begin; i <= end; i++) {
            if (!validateExists(fileName, i, fileTempPath)) {
                needPdf2Swf = true;
                pageNumBuffer.append(",").append(i);
            }
        }
        if (needPdf2Swf) {
            cmd.append(" -p ").append(pageNumBuffer.substring(1));
            if(System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1){
            	cmd.append(" \"").append(pdfFile.getPath()).append("\"");
            }else{
            	cmd.append(" ").append(StringUtils.replace(pdfFile.getPath()," ","\\ "));
            }
            
            cmd.append(" ").append(parentFile.getPath())
                    .append(File.separatorChar).append(fileName).append("_%")
                    .append(".swf");
            cmd.append(" -T 6 -f -t  -G -j 25");
            cmd.append(" -F ").append(ScaConfigUtil.getParameter("xpdfFontPath"));
            cmd.append(" -s languagedir=").append(
            		ScaConfigUtil.getParameter("xpdfPath"));
            cmd.append(" -s flashversion=9");
        }
        return needPdf2Swf;
    }

    public static void pdf2swfProcess(final File pdfFile, int beginIndex,
            final String fileTempPath, final int maxPages, String fileName)
            throws IOException, InterruptedException {
        StringBuffer cmd = new StringBuffer();
        if (initProcess(cmd, beginIndex, maxPages, fileTempPath, pdfFile,
                fileName)) {
        	logger.debug(cmd.toString());
            Process process = Runtime.getRuntime().exec(cmd.toString());
            WatchThread wt = new WatchThread(process.getInputStream());
            WatchThread wtError = new WatchThread(process.getErrorStream());
            wt.start();
            wtError.start();
            process.waitFor();
            wt.setOver(true);
            wtError.setOver(true);
            process.destroy();
        }
    }

    /**
     * @param filePath
     * @return
     * @throws IOException
     */
    public static int getMaxPages(String filePath) throws IOException {
        PdfReader reader = null;
        try {
            reader = new PdfReader(filePath);
            int n = reader.getNumberOfPages();
            return n;
        } catch (IOException e) {
        	logger.error(e);
        } finally {
        	if(reader != null){
        		reader.close();
        	}
        }
        return 0;
    }

    /**
     * @param filePath
     * @param thisIndex
     * @return
     */
    public static boolean validateExists(String fileName, int thisIndex,
             String fileTempPath) {
        File tempFile = new File(ScaConfigUtil.getParameter("swfFilePackage")
                + File.separatorChar + fileTempPath + File.separatorChar
                + fileName + "_" + thisIndex + ".swf");
        if (tempFile.exists()) {
            tempFile.setLastModified(new Date().getTime());
            return true;
        }
        return false;
    }

    /**
     * @param filePath
     * @return
     */
    public static boolean validateExistsPdf(String fileName,
            Properties properties, String fileTempPath) {
        File tempFile = new File(properties.getProperty("swfFilePackage")
                + File.separatorChar + fileTempPath + File.separatorChar
                + fileName + ".pdf");
        if (tempFile.exists()) {
            tempFile.setLastModified(new Date().getTime());
            return true;
        }
        return false;
    }
}
