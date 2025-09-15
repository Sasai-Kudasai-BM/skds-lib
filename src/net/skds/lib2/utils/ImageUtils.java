package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;
import net.skds.lib2.misc.fields.IntField2D;
import net.skds.lib2.misc.fields.IntField2DImpl;
import net.w3e.lib.utils.FileUtils;

import javax.imageio.*;
import javax.imageio.spi.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
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
	private static final ImageInputStreamSpi FILE_INPUT_STREAM_SPI;

	private static final ImageWriterSpi PNG_WRITER;
	private static final ImageWriterSpi JPG_WRITER;
	private static final ImageWriterSpi GIF_WRITER;
	private static final ImageWriterSpi TIFF_WRITER;
	private static final ImageOutputStreamSpi FILE_OUTPUT_STREAM_SPI;
	private static final ImageOutputStreamSpi OUTPUT_STREAM_SPI;

	private static final String DATA_BUFFER_INT_ERR = "Unable to get DataBufferInt from image";

	public static final ColorModel COLOR_MODEL_ARGB = ColorModel.getRGBdefault();
	public static final ColorModel COLOR_MODEL_RGB = new DirectColorModel(24,
			0x00ff0000,  // Red
			0x0000ff00,  // Green
			0x000000ff,  // Blue
			0x00000000   // Alpha
	);


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

	public static BufferedImage readPNG(final File file) {
		return readImage(file, PNG_READER);
	}

	public static BufferedImage readJPG(final File file) {
		return readImage(file, JPG_READER);
	}

	public static BufferedImage readGIF(final File file) {
		return readImage(file, GIF_READER);
	}

	public static BufferedImage readTIFF(final File file) {
		return readImage(file, TIFF_READER);
	}


	public static void writePNG(final RenderedImage image, final File file) {
		writeImage(image, file, PNG_WRITER);
	}

	public static void writeJPG(final RenderedImage image, final File file) {
		writeImage(image, file, JPG_WRITER);
	}

	public static void writeGIF(final RenderedImage image, final File file) {
		writeImage(image, file, GIF_WRITER);
	}

	public static void writeTIFF(final RenderedImage image, final File file) {
		writeImage(image, file, TIFF_WRITER);
	}

	public static void writePNG(final RenderedImage image, final OutputStream os) {
		writeImage(image, os, PNG_WRITER);
	}

	public static void writeJPG(final RenderedImage image, final OutputStream os) {
		writeImage(image, os, JPG_WRITER);
	}

	public static void writeGIF(final RenderedImage image, final OutputStream os) {
		writeImage(image, os, GIF_WRITER);
	}

	public static void writeTIFF(final RenderedImage image, final OutputStream os) {
		writeImage(image, os, TIFF_WRITER);
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

	private static BufferedImage readImage(final File file, final ImageReaderSpi readerSpi) {
		try {
			final ImageReader reader = readerSpi.createReaderInstance();
			final ImageReadParam param = reader.getDefaultReadParam();
			reader.setInput(FILE_INPUT_STREAM_SPI.createInputStreamInstance(file, false, null), true, true);
			BufferedImage bi = reader.read(0, param);
			reader.dispose();
			return bi;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeImage(final RenderedImage image, final OutputStream os, final ImageWriterSpi writerSpi) {
		try {
			final ImageWriter writer = writerSpi.createWriterInstance();
			final ImageWriteParam param = writer.getDefaultWriteParam();
			writer.setOutput(OUTPUT_STREAM_SPI.createOutputStreamInstance(os, false, null));
			writer.write(image);
			writer.dispose();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void writeImage(final RenderedImage image, final File file, final ImageWriterSpi writerSpi) {
		try {
			if (!file.exists()) {
				FileUtils.createFileAndParentDirs(file);
			}
			final ImageWriter writer = writerSpi.createWriterInstance();
			final ImageWriteParam param = writer.getDefaultWriteParam();
			writer.setOutput(FILE_OUTPUT_STREAM_SPI.createOutputStreamInstance(file, false, null));
			writer.write(image);
			writer.dispose();
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

	public static byte[] writeImageToArrayPng(final RenderedImage image) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		writePNG(image, os);
		return os.toByteArray();
	}

	public static byte[] writeImageToArrayJpg(final RenderedImage image) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		writeJPG(image, os);
		return os.toByteArray();
	}

	public static byte[] writeImageToArrayGif(final RenderedImage image) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		writeGIF(image, os);
		return os.toByteArray();
	}

	public static byte[] writeImageToArrayTiff(final RenderedImage image) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		writeTIFF(image, os);
		return os.toByteArray();
	}


	public static BufferedImage fieldToImage(IntField2D field) {
		return intsToImage(field.width(), field.height(), field.toArray());
	}

	public static BufferedImage intsToImage(int w, int h, int[] data) {
		interface Masks {
			int[] ARGB_MASKS = {0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000};
		}
		DataBufferInt db = new DataBufferInt(data, data.length);
		SampleModel sm = new SinglePixelPackedSampleModel(db.getDataType(), w, h, Masks.ARGB_MASKS);
		WritableRaster raster = WritableRaster.createWritableRaster(sm, db, null);
		return new BufferedImage(COLOR_MODEL_ARGB, raster, false, null);
	}

	public static BufferedImage rgbBytesToImage(int w, int h, byte[] data) {
		interface Offsets {
			int[] B_OFFS = {0, 1, 2};
		}
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, null, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		DataBufferByte db = new DataBufferByte(data, data.length);
		WritableRaster raster = WritableRaster.createInterleavedRaster(db, w, h, w * 3, 3, Offsets.B_OFFS, null);
		return new BufferedImage(cm, raster, false, null);
	}

	public static BufferedImage argbBytesToImage(int w, int h, byte[] data) {
		interface Offsets {
			int[] B_OFFS = {0, 1, 2, 3};
		}
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, null, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
		DataBufferByte db = new DataBufferByte(data, data.length);
		WritableRaster raster = WritableRaster.createInterleavedRaster(db, w, h, w * 4, 4, Offsets.B_OFFS, null);
		return new BufferedImage(cm, raster, false, null);
	}

	public static IntField2D getIntData(BufferedImage image) {
		if (image.getRaster().getDataBuffer() instanceof DataBufferInt dbi) {
			return new IntField2DImpl(image.getWidth(), image.getHeight(), dbi.getData());
		} else {
			return null;
		}
	}

	public static byte[] getByteData(BufferedImage image) {
		if (image.getRaster().getDataBuffer() instanceof DataBufferByte dbb) {
			return dbb.getData();
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
		{
			ImageReaderSpi gif = null;
			ImageReaderSpi tiff = null;
			ImageReaderSpi jpg = null;
			ImageReaderSpi png = null;
			ImageInputStreamSpi tmpIn = null;
			ImageInputStreamSpi tmpFIn = null;
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
				} else if (File.class == p.getInputClass()) {
					tmpFIn = p;
				}
			}
			PNG_READER = png;
			JPG_READER = jpg;
			GIF_READER = gif;
			TIFF_READER = tiff;
			INPUT_STREAM_SPI = tmpIn;
			FILE_INPUT_STREAM_SPI = tmpFIn;
		}
		{
			ImageWriterSpi gif = null;
			ImageWriterSpi tiff = null;
			ImageWriterSpi jpg = null;
			ImageWriterSpi png = null;
			ImageOutputStreamSpi tmpOut = null;
			ImageOutputStreamSpi tmpFOut = null;
			for (Iterator<ImageWriterSpi> it = IIORegistry.getDefaultInstance().getServiceProviders(ImageWriterSpi.class, false); it.hasNext(); ) {
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
			for (Iterator<ImageOutputStreamSpi> it = IIORegistry.getDefaultInstance().getServiceProviders(ImageOutputStreamSpi.class, false); it.hasNext(); ) {
				var p = it.next();
				if (OutputStream.class == p.getOutputClass()) {
					tmpOut = p;
				} else if (File.class == p.getOutputClass()) {
					tmpFOut = p;
				}
			}
			PNG_WRITER = png;
			JPG_WRITER = jpg;
			GIF_WRITER = gif;
			TIFF_WRITER = tiff;
			OUTPUT_STREAM_SPI = tmpOut;
			FILE_OUTPUT_STREAM_SPI = tmpFOut;
		}
	}
}
