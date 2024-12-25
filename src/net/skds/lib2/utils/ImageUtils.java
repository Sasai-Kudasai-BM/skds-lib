package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

@UtilityClass
@SuppressWarnings("unused")
public class ImageUtils {

	private static final ImageReaderSpi PNG_READER;
	private static final ImageReaderSpi JPG_READER;
	private static final ImageReaderSpi GIF_READER;
	private static final ImageReaderSpi TIFF_READER;
	private static final ImageInputStreamSpi INPUT_STREAM_SPI;


	public static BufferedImage readPNG(final InputStream is) {
		return readImage(is, PNG_READER);
	}

	public static BufferedImage readJPG(final InputStream is) {
		return readImage(is, JPG_READER);
	}

	public static BufferedImage readGIF(final InputStream is) {
		return readImage(is, GIF_READER);
	}

	public static BufferedImage readTIFF(final InputStream is) {
		return readImage(is, TIFF_READER);
	}

	private static BufferedImage readImage(final InputStream is, final ImageReaderSpi readerSpi) {
		try {
			final ImageReader reader = readerSpi.createReaderInstance();
			final ImageReadParam param = reader.getDefaultReadParam();
			reader.setInput(INPUT_STREAM_SPI.createInputStreamInstance(is, false, null), true, true);
			BufferedImage bi = reader.read(0, param);
			reader.dispose();
			return bi;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] writeImageToArrayPng(final BufferedImage image) {
		return writeImageToArray(image, "png");
	}

	public static byte[] writeImageToArrayJpg(final BufferedImage image) {
		return writeImageToArray(image, "jpg");
	}

	private static byte[] writeImageToArray(final BufferedImage image, String format) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(image, format, os); // TODO speedup
			return os.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedImage drawPerPixel(int w, int h, PerPixelDraw draw) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = image.getRaster();
		int[] buf = new int[1];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int c = draw.draw(x, y);
				if (c != 0) {
					buf[0] = c;
					raster.setDataElements(x, y, buf);
				}
			}
		}
		return image;
	}

	public interface PerPixelDraw {
		int draw(int x, int y);
	}

	static {
		ImageReaderSpi gif = null;
		ImageReaderSpi tiff = null;
		ImageReaderSpi jpg = null;
		ImageReaderSpi png = null;
		ImageInputStreamSpi tmpIn = null;
		for (Iterator<ImageReaderSpi> it = IIORegistry.getDefaultInstance().getServiceProviders(ImageReaderSpi.class, false); it.hasNext(); ) {
			var p = it.next();
			Set<String> formats = Set.of(p.getFormatNames());
			if (formats.contains("png")) {
				png = p;
			} else if (formats.contains("jpg")) {
				jpg = p;
			} else if (formats.contains("gif")) {
				gif = p;
			} else if (formats.contains("tiff")) {
				tiff = p;
			}
		}
		for (Iterator<ImageInputStreamSpi> it = IIORegistry.getDefaultInstance().getServiceProviders(ImageInputStreamSpi.class, false); it.hasNext(); ) {
			var p = it.next();
			if (InputStream.class == p.getInputClass()) {
				tmpIn = p;
				break;
			}
		}
		PNG_READER = png;
		JPG_READER = jpg;
		GIF_READER = gif;
		TIFF_READER = tiff;
		INPUT_STREAM_SPI = tmpIn;
	}
}
