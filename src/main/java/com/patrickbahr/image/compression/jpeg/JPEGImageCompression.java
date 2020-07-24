package com.patrickbahr.image.compression.jpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Predicate;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.patrickbahr.image.compression.ImageCompression;

public final class JPEGImageCompression implements ImageCompression {

	private final static float DEFAULT_COMPRESSION_QUALITY = .75f;

	@Override
	public byte[] compress(byte[] input) throws IOException {
		return compressToSpecificQuality(input, DEFAULT_COMPRESSION_QUALITY);
	}

	@Override
	public byte[] compressIfNecessary(byte[] image, Predicate<byte[]> condition) throws IOException {
		boolean imageMatchCondition = condition.test(image);
		if (imageMatchCondition) {
			return compress(image);
		}

		return image;
	}

	@Override
	public byte[] compressIfImageSizeIsBiggerThan(byte[] image, double size) throws IOException {
		byte[] compressedImage = compressIfNecessary(image, img -> img.length > size);
		if (compressedImage.length > size) {
			float initialQuality = DEFAULT_COMPRESSION_QUALITY;
			compressedImage = compressUntilImageHasSize(image, initialQuality, size);
		}

		return compressedImage;
	}

	/**
	 * Compress until image size has less than maximumSize but is bigger than 90% of
	 * maximumSize. This will garantee that the result will still have some quality
	 * to it. Basically, it will reduce the image until it has less size then
	 * maximumSize while still maintaning the maximum quality possible.
	 * 
	 * @param image
	 * @param initialQuality
	 * @param maximumSize
	 * @return
	 * @throws IOException
	 */
	private byte[] compressUntilImageHasSize(byte[] image, float initialQuality, double maximumSize)
			throws IOException {
		
		double minimumSize = (maximumSize * .9);
		float newQuality = initialQuality / 2f;
		byte[] compressedImage = compressToSpecificQuality(image, newQuality);
		float qualityDifference = newQuality / 2f;
		// grants some quality to the image. If we just reduce it to the minimum size
		// possible the quality would be way worse than it would need to be.
		// also, grants that the size is smaller than the required size.
		// if qualityDifference is smaller than 0.2 then the difference from one call to
		// compress to another would be insignificant.
		boolean imageSmallerThanMinimum = compressedImage.length < minimumSize;
		boolean imageBiggerThanMaximum = compressedImage.length > maximumSize;
		boolean qualityDifferenceStillRelevant = qualityDifference > .02f;
		while ((imageSmallerThanMinimum || imageBiggerThanMaximum) && qualityDifferenceStillRelevant) {
			newQuality += (imageSmallerThanMinimum ? qualityDifference : -qualityDifference);
			compressedImage = compressToSpecificQuality(compressedImage, newQuality);
			qualityDifference = qualityDifference / 2f;
			
			imageSmallerThanMinimum = compressedImage.length < minimumSize;
			imageBiggerThanMaximum = compressedImage.length > maximumSize;
			qualityDifferenceStillRelevant = qualityDifference > .02f;
			
		}

		return compressedImage;
	}

	private byte[] compressToSpecificQuality(byte[] input, float quality) throws IOException {
		
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(255);
				ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {

			BufferedImage image = ImageIO.read(new ByteArrayInputStream(input));
			ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
			writer.setOutput(ios);

			ImageWriteParam param = writer.getDefaultWriteParam();

			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(quality);
			}

			writer.write(null, new IIOImage(image, null, null), param);
			writer.dispose();

			return out.toByteArray();
		}
	}
	

}
