package com.brainsoon.common.util.dofile.test;

//import java.io.IOException;
//import java.util.zip.DataFormatException;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.brainsoon.common.util.dofile.content.SWFUtil;
//import com.flagstone.transform.FSColorTable;
//import com.flagstone.transform.FSDefineObject;
//import com.flagstone.transform.FSDefineShape3;
//import com.flagstone.transform.FSMovie;
//import com.flagstone.transform.FSPlaceObject2;
//import com.flagstone.transform.FSSetBackgroundColor;
//import com.flagstone.transform.FSShowFrame;
//import com.flagstone.transform.FSSolidLine;
//import com.flagstone.transform.util.FSImageConstructor;

public class SWFUtilTest {

//	 public static final String SWF_FILE = "D:/《燕子专列》动画-夺红旗.swf";  
//	  
//	    public static final String IMAGE_TARGET_FILE = "D:/img1.jpg";  
//	  
//	    public static final String IMAGE_SOURCE_FILE = "D:/小说欣赏.png";  
//	  
//	    private static SWFUtil tool = null;  
//	  
//	    @BeforeClass  
//	    public static void setUpBeforeClass() throws Exception {  
//	        tool = new SWFUtil();  
//	    }  
//	  
//	    @AfterClass  
//	    public static void tearDownAfterClass() throws Exception {  
//	        tool = null;  
//	    }  
//	  
//	    @Test  
//	    public final void testTransformToIMG() {  
//	  
//	        try {  
//	            //createMovie(IMAGE_SOURCE_FILE, SWF_FILE);  
//	            tool.transformToIMG(SWF_FILE, IMAGE_TARGET_FILE);  
//	        } catch (IOException e) {  
//	            // TODO Auto-generated catch block  
//	            e.printStackTrace();  
//	        }  
//	    }  
//	  
//	    private void createMovie(String sourceIMG, String targetSWF)  
//	            throws DataFormatException, IOException {  
//	        FSMovie movie = new FSMovie();  
//	        FSImageConstructor imageGenerator = new FSImageConstructor(sourceIMG);  
//	  
//	        int imageId = movie.newIdentifier();  
//	        int shapeId = movie.newIdentifier();  
//	  
//	        int xOrigin = imageGenerator.getWidth() / 2;  
//	        int yOrigin = imageGenerator.getHeight() / 2;  
//	  
//	        FSDefineObject image = imageGenerator.defineImage(imageId);  
//	  
//	        FSDefineShape3 shape = imageGenerator.defineEnclosingShape(shapeId,  
//	                imageId, -xOrigin, -yOrigin, new FSSolidLine(20, FSColorTable  
//	                        .black()));  
//	  
//	        movie.setFrameRate(1.0f);  
//	        movie.setFrameSize(shape.getBounds());  
//	        movie.add(new FSSetBackgroundColor(FSColorTable.lightblue()));  
//	        movie.add(image);  
//	        movie.add(shape);  
//	        movie.add(new FSPlaceObject2(shapeId, 1, 0, 0));  
//	        movie.add(new FSShowFrame());  
//	        movie.encodeToFile(targetSWF);  
//	    }  
}
