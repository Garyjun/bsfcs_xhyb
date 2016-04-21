package com.brainsoon.common.util.dofile.content;

//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.List;
//import java.util.zip.DataFormatException;
//
//import javax.imageio.ImageIO;
//
//import com.flagstone.transform.FSDefineJPEGImage2;
//import com.flagstone.transform.FSMovie;
//import com.flagstone.transform.FSMovieObject;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 
 * @ClassName: SWFUtil 
 * @Description: SWF文件提取工具类 
 * @author tanghui 
 * @date 2014-11-10 下午3:04:16 
 *
 */
public class SWFUtil {
//
//	/**
//	 * 
//	 * @Title: transformToIMG 
//	 * @Description: 解析SWF中的图片
//	 * @param   
//	 * @return void 
//	 * @throws
//	 */
//    public void transformToIMG(String swfFilePath, String imgFilePath)  
//            throws IOException {  
//        FSMovie movie;  
//        try {  
//            movie = new FSMovie(swfFilePath);  
//        } catch (IOException e) {  
//            e.printStackTrace();  
//            return;  
//        } catch (DataFormatException e) {  
//            e.printStackTrace();  
//            return;  
//        }  
//        List<FSMovieObject> oList = movie.getObjects();  
//  
//        for (FSMovieObject o : oList) {  
//            if (o.getType() == FSMovieObject.DefineImage  
//                    || o.getType() == FSMovieObject.DefineImage2  
//                    || o.getType() == FSMovieObject.DefineJPEGImage  
//                    || o.getType() == FSMovieObject.DefineJPEGImage2  
//                    || o.getType() == FSMovieObject.DefineJPEGImage3) {  
//                // System.out.println("catch image.... "+o.getType());  
//  
//                // TODO add more logic to process different image type.  
//                // Currently there is only FSDefineJPEGImage2  
//                BufferedImage bio = ImageIO.read(new ByteArrayInputStream(  
//                        ((FSDefineJPEGImage2) o).getImage()));  
//                FileOutputStream fos = new FileOutputStream(imgFilePath);  
//                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);  
//                encoder.encode(bio);  
//  
//                break;  
//            }  
//        }  
//    }  
}
