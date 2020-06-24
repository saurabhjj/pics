package com.pics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

//import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;

public class PicsMerge {

	private static String FINAL_PATH = "C:/saurabh/pics-merge/pics/final-images";
	private static String DUPLICATE_FILES = "C:/saurabh/pics-merge/pics/Duplicates.txt";
	private static String DELETED_FILES = "C:/saurabh/pics-merge/pics/Deleted.txt";
	private static String UN_DELETED_FILES = "C:/saurabh/pics-merge/pics/Un-Deleted.txt";
	private static int newNumber= 1;

	public static void main(String[] args) {
		System.out.println("Hello World");

		String inputPath = "C:/saurabh/pics-merge/pics/tocheck";

		listFilesForFolder(new File(inputPath));

		System.out.println(":::::: DONE :::::: ");
	}

	private static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				System.out.println(" Input file ::::: " + fileEntry.getName());
				findFileInFolder(fileEntry);
			}
		}
	}

	private static void findFileInFolder(final File srcFile) {
		File targetFolder = new File(FINAL_PATH);
		boolean response = false;
		for (final File fileEntry : targetFolder.listFiles()) {
			if (fileEntry != null && !srcFile.getName().contains(".zip")) {
				System.out.println(" target file ::::: " + fileEntry.getName());
				try{
				response = compareImages(fileEntry, srcFile);
				} catch (Exception e){
					updateTextFile("exception while compare ::::: " + fileEntry.getAbsolutePath(),DUPLICATE_FILES);
				}
				if (response) {
					break;
				}
			}else {
				updateTextFile("exception while compare ::::: " + srcFile.getAbsolutePath(),DUPLICATE_FILES);
			}
			deleteInputFile(fileEntry);
		}

		if (!response) {
			copyFileToTarget(srcFile);
		} else {
			updateTextFile(srcFile.getAbsolutePath(),DUPLICATE_FILES);
		}
	}
	
	private static void deleteInputFile(File srcFile){
		if(srcFile.delete()){
			updateTextFile(srcFile.getAbsolutePath(),DELETED_FILES);
		} else {
			updateTextFile(srcFile.getAbsolutePath(),UN_DELETED_FILES);
		}
			
	}

	private static void updateTextFile(String name,String filePath) {
		try {
			String texttobewrittentofile = name + System.lineSeparator();
		    Files.write(Paths.get(filePath), texttobewrittentofile.getBytes(), StandardOpenOption.APPEND,StandardOpenOption.CREATE);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
			System.out.println("Exception from updateTextFile while writing :::" + name );
		}		
	}

	private static void copyFileToTarget(File sourceFile) {
		try {
//			FileUtils.copyFileToDirectory(srcFile, new File (FINAL_PATH));
			File destFile = new File (FINAL_PATH+"\\"+sourceFile.getName());
			  if (!sourceFile.exists()) {
			        return;
			    }
			  
			    if(destFile.exists()){
			    	destFile =  new File(FINAL_PATH+"\\"+newNumber+"-"+sourceFile.getName());
			        newNumber++;
			    }
			  
			    if (!destFile.exists()) {
			        destFile.createNewFile();
			    }
			    FileChannel source = null;
			    FileChannel destination = null;
			    FileInputStream inputStream = new FileInputStream(sourceFile);
			    source = inputStream.getChannel();
			    
			    FileOutputStream outputStream = new FileOutputStream(destFile);
			    destination = outputStream.getChannel();
			    if (destination != null && source != null) {
			        destination.transferFrom(source, 0, source.size());
			    }
			    if (source != null) {
			        source.close();
			    }
			    if (destination != null) {
			        destination.close();
			    }
			    if (inputStream != null) {
			    	inputStream.close();
			    }
			    if (outputStream != null) {
			    	outputStream.close();
			    }
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Exception from copyFileToTarget while copying :::" + sourceFile.getName());
		}
		
	}

	private static boolean compareImages(File trgtFile, File srcFile) {
		try {
			// take buffer data from botm image files //
			BufferedImage biA = ImageIO.read(trgtFile);
			DataBuffer dbA = biA.getData().getDataBuffer();
			int sizeA = dbA.getSize();
			BufferedImage biB = ImageIO.read(srcFile);
			System.out.println("srcFile ::::" + srcFile);
//			System.out.println("biB ::::" + biB);
			DataBuffer dbB = biB.getData().getDataBuffer();
			int sizeB = dbB.getSize();
			// compare data-buffer objects //
			if (sizeA == sizeB) {
				for (int i = 0; i < sizeA; i++) {
					if (dbA.getElem(i) != dbB.getElem(i)) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			System.out.println("Failed to compare image files ..." + trgtFile.getName() + " ::: and :::" + srcFile.getName());
//			System.out.println("Exception from copyFileToTarget while copying :::" + );
			return false;
		}
	}

}
