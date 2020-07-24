package com.patrickbahr.image.compression;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

import com.patrickbahr.image.compression.jpeg.JPEGImageCompression;

public class Start {

	public static void main(String[] args) throws IOException {
		JPEGImageCompression compression = new JPEGImageCompression();
		byte[] image = Files.readAllBytes(new File("C:\\Users\\Patrick\\Desktop\\teste\\carrito.jpg").toPath());
		byte[] compressedImage = compression.compressIfImageSizeIsBiggerThan(image, 200 * FileUtils.ONE_KB);
		
		try (FileOutputStream fos = new FileOutputStream("C:\\Users\\Patrick\\Desktop\\teste\\carritomenor2.jpg")) {
			fos.write(compressedImage);
		}
	}
}
