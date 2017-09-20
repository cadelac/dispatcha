package cadelac.framework.blade.core;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

	public static long getTimestamp() {
		return (new Date()).getTime();
	}
	
	public static String getDateString() {
		return getDateString(new Date());
	}
	
	public static String getDateString(final String format) {
		return getDateString(new Date(), format);
	}
	
	public static String getDateString(final Date date_) {
		return getDateString(date_, "yyyy-MM-dd");
	}
	
	public static String getDateString(final Date date_, final String format) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date_);
	}
	
	
	public static void writeFile(final File file_, final String text_) 
			throws IOException {
		final FileWriter w = new FileWriter(file_);
		w.write(text_);
		w.close();
	}

	public static String readFile(final String path_) throws IOException {
		final StringBuilder accumulated = new StringBuilder();
		Files.lines(
				Paths.get(path_)
				, StandardCharsets.UTF_8)
		.forEach(
				p -> { accumulated.append(p).append("\n");}
		);
		return accumulated.toString();
	}
	
	public static String md5Encode(final String in) throws NoSuchAlgorithmException {
		final MessageDigest md = MessageDigest.getInstance("MD5");
		final byte[] digest = md.digest(in.getBytes());	      
		StringBuffer hexString = new StringBuffer();
		for (int i=0;i<digest.length;i++) {
			hexString.append(Integer.toHexString(0xFF & digest[i]));
		}
		return hexString.toString();
	}
	
	public static File writeSourceFile(final File sourceDir_, final File classDir_, final String className_, final String sourceCode_) 
			throws IOException {
		final String normalizedClassName = className_.replace('.', File.separatorChar);
		final File sourceFile = new File(sourceDir_, normalizedClassName + ".java");
		final File sourceParentFile = sourceFile.getParentFile();
		if (sourceParentFile != null && !sourceParentFile.exists()) 
			sourceParentFile.mkdirs();
		if (sourceFile.exists())
			sourceFile.delete();
		
		final File classFile = new File(classDir_, normalizedClassName + ".class");
		File classParentFile = classFile.getParentFile();
		if (classParentFile != null && !classParentFile.exists())	
			classParentFile.mkdirs();
		
		Utilities.writeFile(sourceFile, sourceCode_);
		return sourceFile;
	}
}
