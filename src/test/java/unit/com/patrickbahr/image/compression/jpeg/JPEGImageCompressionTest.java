package unit.com.patrickbahr.image.compression.jpeg;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.apache.commons.io.FileUtils.ONE_MB;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.patrickbahr.image.compression.jpeg.JPEGImageCompressor;

public class JPEGImageCompressionTest {

	private final File JPEG_IMAGE_WITH_MORE_THAN_3MB = FileUtils.toFile(getClass().getResource("/some-truck.jpg"));
	private final File SOME_FILE_THAT_IS_NOT_A_JPEG_IMAGE = FileUtils
			.toFile(getClass().getResource("/some-text-file.txt"));
	
	private static final double THREE_MB = 3 * ONE_MB;
	private static final double HALF_MB = .5 * ONE_MB;
	private final JPEGImageCompressor imageCompressor = new JPEGImageCompressor();

	@Test
	public void shouldCompressAFileWhenItsAValidJPEGImage() throws IOException {
		byte[] imageInBytes = FileUtils.readFileToByteArray(JPEG_IMAGE_WITH_MORE_THAN_3MB);
		byte[] result = imageCompressor.compress(imageInBytes);
		assertThatImageWasCompressed(imageInBytes, result);
	}

	@Test
	public void shouldThrowAnExceptionWhenTheFileIsNotAValidJPEGImage() throws IOException {
		assertThrows(IOException.class, () -> {
			byte[] imageInBytes = FileUtils.readFileToByteArray(SOME_FILE_THAT_IS_NOT_A_JPEG_IMAGE);
			imageCompressor.compress(imageInBytes);
		});
	}

	@Test
	public void shouldNotCompressWhenConditionIsFalse() throws IOException {
		byte[] imageInBytes = FileUtils.readFileToByteArray(JPEG_IMAGE_WITH_MORE_THAN_3MB);
		byte[] result = imageCompressor.compressIfNecessary(imageInBytes, img -> false);
		assertTrue(imageInBytes == result);
	}

	@Test
	public void shouldCompressWhenConditionIsTrue() throws IOException {
		byte[] imageInBytes = FileUtils.readFileToByteArray(JPEG_IMAGE_WITH_MORE_THAN_3MB);
		byte[] result = imageCompressor.compressIfNecessary(imageInBytes, img -> true);
		assertThatImageWasCompressed(imageInBytes, result);
	}
	
	@Test
	public void shouldCompressWhenImageSizeIsBiggerThan3MB() throws IOException {
		byte[] imageInBytes = FileUtils.readFileToByteArray(JPEG_IMAGE_WITH_MORE_THAN_3MB);
		byte[] result = imageCompressor.compressIfImageSizeIsBiggerThan(imageInBytes, THREE_MB);
		assertThatImageSizeIsSmallerThan(result, THREE_MB);
	}
	
	public void shouldCompressAnImageToASmallSize() throws IOException {
		byte[] imageInBytes = FileUtils.readFileToByteArray(JPEG_IMAGE_WITH_MORE_THAN_3MB);
		byte[] result = imageCompressor.compressIfImageSizeIsBiggerThan(imageInBytes, HALF_MB);
		assertThatImageSizeIsSmallerThan(result, HALF_MB);	
	}

	private void assertThatImageWasCompressed(byte[] original, byte[] result) {
		assertTrue(result.length < original.length);
	}


	private void assertThatImageSizeIsSmallerThan(byte[] image, double expectedSize) {
		assertTrue(image.length < expectedSize);
	}
}
