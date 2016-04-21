//
// JODConverter - Java OpenDocument Converter
// Copyright 2004-2011 Mirko Nasato and contributors
//
// JODConverter is free software: you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// JODConverter is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General
// Public License along with JODConverter.  If not, see
// <http://www.gnu.org/licenses/>.
//
package org.artofsolving.jodconverter;

import static org.artofsolving.jodconverter.office.OfficeUtils.SERVICE_DESKTOP;
import static org.artofsolving.jodconverter.office.OfficeUtils.cast;
import static org.artofsolving.jodconverter.office.OfficeUtils.toUnoProperties;
import static org.artofsolving.jodconverter.office.OfficeUtils.toUrl;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.office.OfficeContext;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeTask;

import com.brainsoon.common.support.GlobalAppCacheMap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.task.ErrorCodeIOException;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;

public abstract class AbstractConversionTask implements OfficeTask {
	protected static final Logger logger = Logger.getLogger(AbstractConversionTask.class);
    private final File inputFile;
    private final File outputFile;
    private int threadNum;

    public AbstractConversionTask(File inputFile, File outputFile,int threadNum) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.threadNum = threadNum;
    }

    
    protected abstract Map<String,?> getLoadProperties(File inputFile);

    protected abstract Map<String,?> getStoreProperties(File outputFile, XComponent document);

    public  void execute(OfficeContext context){
        XComponent document = null;
        try {
        	System.out.println("文档加载中...");
            document = loadDocument(context, inputFile);
            System.out.println("文档加载完毕.");
            modifyDocument(document);
            storeDocument(document, outputFile);
            GlobalAppCacheMap.putKey("converstatus", "ok");
        } catch (OfficeException officeException) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
        	officeException.printStackTrace();
            //throw officeException;
        } catch (Exception exception) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
        	exception.printStackTrace();
            //throw new OfficeException("conversion failed", exception);
        } finally {
            if (document != null) {
                XCloseable closeable = cast(XCloseable.class, document);
                if (closeable != null) {
                    try {
                        closeable.close(true);
                    } catch (CloseVetoException closeVetoException) {
                    	GlobalAppCacheMap.putKey("converstatus", "error");
                    	closeVetoException.printStackTrace();
                    }
                } else {
                    document.dispose();
                }
            }
        }
    }

    private  XComponent loadDocument(OfficeContext context, File inputFile) throws OfficeException {
        if (!inputFile.exists()) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
            throw new OfficeException("input document not found");
        }
        XComponentLoader loader = cast(XComponentLoader.class, context.getService(SERVICE_DESKTOP));
        Map<String,?> loadProperties = getLoadProperties(inputFile);
        XComponent document = null;
        try {
            document = loader.loadComponentFromURL(toUrl(inputFile), "_blank", 0, toUnoProperties(loadProperties));
        } catch (IllegalArgumentException illegalArgumentException) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
            throw new OfficeException("could not load document1: " + inputFile.getName(), illegalArgumentException);
        } catch (ErrorCodeIOException errorCodeIOException) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
            throw new OfficeException("could not load document2: "  + inputFile.getName() + "; errorCode: " + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (IOException ioException) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
            throw new OfficeException("could not load document3: "  + inputFile.getName(), ioException);
        }
        if (document == null) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
            throw new OfficeException("could not load document4: "  + inputFile.getName());
        }
        return document;
    }

    /**
     * Override to modify the document after it has been loaded and before it gets
     * saved in the new format.
     * <p>
     * Does nothing by default.
     * 
     * @param document
     * @throws OfficeException
     */
    protected void modifyDocument(XComponent document) throws OfficeException {
    	// noop
    }

    private void storeDocument(XComponent document, File outputFile) throws OfficeException {
        Map<String,?> storeProperties = getStoreProperties(outputFile, document);
        if (storeProperties == null) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
            throw new OfficeException("unsupported conversion");
        }
        try {
        	System.out.println("文档转换中...");
            cast(XStorable.class, document).storeToURL(toUrl(outputFile), toUnoProperties(storeProperties));
            System.out.println("文档转换存储完毕.");
        } catch (ErrorCodeIOException errorCodeIOException) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
            throw new OfficeException("could not store document1: " + outputFile.getName() + "; errorCode: " + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (IOException ioException) {
        	GlobalAppCacheMap.putKey("converstatus", "error");
            throw new OfficeException("could not store document2: " + outputFile.getName(), ioException);
        }
    }

}
