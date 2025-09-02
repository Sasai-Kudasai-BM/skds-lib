package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;
import net.skds.lib2.misc.fields.IntField2D;
import net.skds.lib2.misc.fields.IntField2DImpl;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@UtilityClass
@SuppressWarnings("unused")
public class ImageUtils {

	private static final ImageReaderSpi PNG_READER;
	private static final ImageReaderSpi JPG_READER;
	private static final ImageReaderSpi GIF_READER;
	private static final ImageReaderSpi TIFF_READER;
	private static final ImageInputStreamSpi INPUT_STREAM_SPI;

	private static final String DATA_BUFFER_INT_ERR = "Unable to get DataBufferInt from image";


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

	public static BufferedImage readImageUnknown(final InputStream is) {
		try {
			return ImageIO.read(is);
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

	public static BufferedImage fieldToImage(IntField2D field) {
		interface Masks {
			int[] ARGB_MASKS = {0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000};
		}
		int[] data = field.toArray();
		DataBufferInt db = new DataBufferInt(data, data.length);
		SampleModel sm = new SinglePixelPackedSampleModel(db.getDataType(), field.width(), field.height(), Masks.ARGB_MASKS);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, db, null);
		return new BufferedImage(ColorModel.getRGBdefault(), raster, false, null);
	}

	public static IntField2D getIntData(BufferedImage image) {
		if (image.getRaster().getDataBuffer() instanceof DataBufferInt dbi) {
			return new IntField2DImpl(image.getWidth(), image.getHeight(), dbi.getData());
		} else {
			return null;
		}
	}

	public static BufferedImage drawPerPixel(int w, int h, PerPixelDraw draw) {
		return drawPerPixel(w, h, draw, true);
	}
	
	public static BufferedImage drawPerPixel(int w, int h, PerPixelDraw draw, boolean useAlpha) {
		BufferedImage image = new BufferedImage(w, h, useAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		IntField2D raster = Objects.requireNonNull(getIntData(image), DATA_BUFFER_INT_ERR);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int c = draw.draw(x, y);
				if (c != 0) {
					raster.setValue(c, x, y);
				}
			}
		}
		return image;
	}


	public static BufferedImage drawPerPixelFill(int w, int h, PerPixelDraw draw) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		IntField2D raster = Objects.requireNonNull(getIntData(image), DATA_BUFFER_INT_ERR);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				raster.setValue(draw.draw(x, y), x, y);
			}
		}
		return image;
	}

	public static void drawPerPixel(BufferedImage image, PerPixelDraw draw) {
		int w = image.getWidth();
		int h = image.getHeight();
		IntField2D raster = Objects.requireNonNull(getIntData(image), DATA_BUFFER_INT_ERR);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int c = draw.draw(x, y);
				if (c != 0) {
					raster.setValue(c, x, y);
				}
			}
		}
	}

	public static void drawPerPixelFill(BufferedImage image, PerPixelDraw draw) {
		int w = image.getWidth();
		int h = image.getHeight();
		IntField2D raster = Objects.requireNonNull(getIntData(image), DATA_BUFFER_INT_ERR);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				raster.setValue(draw.draw(x, y), x, y);
			}
		}
	}

	public static void drawPerPixelFill(BufferedImage image, PerPixelDraw draw, int x0, int y0, int w, int h) {
		int xe = Math.min(image.getWidth(), x0 + w);
		int ye = Math.min(image.getHeight(), y0 + h);
		IntField2D raster = Objects.requireNonNull(getIntData(image), DATA_BUFFER_INT_ERR);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				raster.setValue(draw.draw(x, y), x, y);
			}
		}
	}

	public static void drawPerPixelFill(IntField2D image, PerPixelDraw draw) {
		int w = image.width();
		int h = image.height();
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				image.setValue(draw.draw(x, y), x, y);
			}
		}
	}

	public static void drawPerPixelFill(IntField2D image, PerPixelDraw draw, int x0, int y0, int w, int h) {
		int xe = Math.min(image.width(), x0 + w);
		int ye = Math.min(image.height(), y0 + h);
		for (int x = x0; x < xe; x++) {
			for (int y = y0; y < ye; y++) {
				image.setValue(draw.draw(x, y), x, y);
			}
		}
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
