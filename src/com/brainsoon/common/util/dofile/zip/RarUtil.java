package com.brainsoon.common.util.dofile.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;

/**
 * 
 * @ClassName: RarUtil 
 * @Description:  rar文件解压工具类
 * @author tanghui 
 * @date 2013-10-28 下午5:44:44 
 *
 */
public class RarUtil {
	private static Log logger = LogFactory.getLog(RarUtil.class);
			
	public static void unZip(String sourceFile,String destDir) {
		ZipInputStream zipinputstream = null;
		ZipEntry zipentry = null;
		try {
			// destination folder to extract the contents
			byte[] buf = new byte[1024];
			zipinputstream = new ZipInputStream(new FileInputStream(sourceFile));
			zipentry =  zipinputstream.getNextEntry();
			while (zipentry != null) {

				// for each entry to be extracted
				String entryName = zipentry.getName();

				System.out.println(entryName);

				int n;
				FileOutputStream fileoutputstream;
				File newFile = new File(entryName);

				String directory = newFile.getParent();

				// to creating the parent directories
				if (directory == null) {
					if (newFile.isDirectory()) {
						break;
					}
				} else {
					new File(destDir + directory).mkdirs();
				}

				if (!zipentry.isDirectory()) {
					System.out.println("File to be extracted....." + entryName);
					fileoutputstream = new FileOutputStream(destDir + entryName);
					while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
						fileoutputstream.write(buf, 0, n);
					}
					fileoutputstream.close();
				}

				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != zipinputstream) {
				try {
					zipinputstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void unRar(String sourceFile,String destDir) {
		File archive = new File(sourceFile);
		File destination = new File(destDir);
		Archive arch = null;
		FileHeader fh = null;
		try {
			arch = new Archive(archive);
			if (arch != null) {
				if (arch.isEncrypted()) {
					System.out.println("archive is encrypted cannot extreact");
					return;
				}
				while (true) {
					fh = arch.nextFileHeader();
					if (fh == null) {
						break;
					}
					if (fh.isEncrypted()) {
						System.out.println("file is encrypted cannot extract: "
								+ fh.getFileNameString());
						continue;
					}
					try {
						if (fh.isDirectory()) {
							createDirectory(fh, destination);
						} else {
							File f = createFile(fh, destination);
							OutputStream stream = new FileOutputStream(f);
							arch.extractFile(fh, stream);
							stream.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fh = null;
			if (null != arch) {
				try {
					arch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public static void unRar(String sourceFile) {
		File archive = new File(sourceFile);
		String destDir = archive.getParent();
		File destination = new File(destDir);
		//logger.info("解压后的路径为：" + destDir);
		Archive arch = null;
		FileHeader fh = null;
		try {
			arch = new Archive(archive);
			if (arch != null) {
				if (arch.isEncrypted()) {
					System.out.println("archive is encrypted cannot extreact");
					return;
				}
				while (true) {
					fh = arch.nextFileHeader();
					if (fh == null) {
						break;
					}
					if (fh.isEncrypted()) {
						System.out.println("file is encrypted cannot extract: "
								+ fh.getFileNameString());
						continue;
					}
					try {
						if (fh.isDirectory()) {
							createDirectory(fh, destination);
						} else {
							File f = createFile(fh, destination);
							OutputStream stream = new FileOutputStream(f);
							arch.extractFile(fh, stream);
							stream.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			}
		} catch (RarException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			fh = null;
			if (null != arch) {
				try {
					arch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static File createFile(FileHeader fh, File destination) {
		File f = null;
		String name = null;
		if (fh.isFileHeader() && fh.isUnicode()) {
			name = fh.getFileNameW().trim();
//		if(!existZH(name)){
//			name = fh.getFileNameString().trim();
		} else {
			name = fh.getFileNameString().trim();
		}
		f = new File(destination, name);
		if (!f.exists()) {
			try {
				f = makeFile(destination, name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return f;
	}

	public static boolean existZH(String str) {
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		while (m.find()) {
		return true;
		}
		return false;
	}
	
	private static File makeFile(File destination, String name) throws IOException {
		String[] dirs = name.split("\\\\");
		if (dirs == null) {
			return null;
		}
		String path = "";
		int size = dirs.length;
		if (size == 1) {
			return new File(destination, name);
		} else if (size > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				path = path + File.separator + dirs[i];
				new File(destination, path).mkdir();
			}
			path = path + File.separator + dirs[dirs.length - 1];
			File f = new File(destination, path);
			f.createNewFile();
			return f;
		} else {
			return null;
		}
	}

	private static void createDirectory(FileHeader fh, File destination) {
		File f = null;
		if (fh.isDirectory() && fh.isUnicode()) {
			f = new File(destination, fh.getFileNameW());
			if (!f.exists()) {
				makeDirectory(destination, fh.getFileNameW());
			}
		} else if (fh.isDirectory() && !fh.isUnicode()) {
			f = new File(destination, fh.getFileNameString());
			if (!f.exists()) {
				makeDirectory(destination, fh.getFileNameString());
			}
		}
	}

	private static void makeDirectory(File destination, String fileName) {
		String[] dirs = fileName.split("\\\\");
		if (dirs == null) {
			return;
		}
		String path = "";
		for (String dir : dirs) {
			path = path + File.separator + dir;
			new File(destination, path).mkdir();
		}
	}

	      

	public static void main(String[] args) {
		// extract zip
//		RarUtil.unZip("D:/Project素材/anime/anime.rar", "d:/javazip/");
		// extract rar
//		RarUtil.unRar("D:/Project素材/anime/anime.rar", "d:/javazip");
		RarUtil.unRar("D:/第三方版权合同附件.rar","d://1");
		//new File("D:/北欧文学.rar").delete();
	}
}
