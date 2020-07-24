package com.patrickbahr.image.compression;

import java.io.IOException;
import java.util.function.Predicate;

public interface ImageCompressor {

	byte[] compress(byte[] image) throws IOException;
	byte[] compressIfNecessary(byte[] image, Predicate<byte[]> condition) throws IOException;
	byte[] compressIfImageSizeIsBiggerThan(byte[] image, double size) throws IOException;
	
	
}
