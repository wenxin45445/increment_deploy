package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;

public class FileUtil {

	/**
	 * 获取文件MD5码
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getFileMD5Code(File file) {
		String md5 = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MappedByteBuffer byteBuffer = in.getChannel().map(MapMode.READ_ONLY, 0, file.length());
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(byteBuffer);
			BigInteger bi = new BigInteger(1, digest.digest());
			md5 = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return md5;
	}

}
