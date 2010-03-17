import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;


public class PhotoTagger {

	
	/* input */
	public static String inputFolder = "/Users/vinzenzweber/Pictures/OPENKITCHEN/BarWien";
	public static String tagLogo = "/Users/vinzenzweber/Pictures/OPENKITCHEN/openkitchen_phototag.png";
	public static BufferedImage tagBufferedImage = R.loadBufferedImage(tagLogo);
	public static String tagLogo2 = "/Users/vinzenzweber/Pictures/OPENKITCHEN/alessaesteban_com.png";
	public static BufferedImage tagBufferedImage2 = R.loadBufferedImage(tagLogo2);
	
	
	/* output */
	public static String outputFolder = "/Users/vinzenzweber/Pictures/OPENKITCHEN/BarWien/output";
	public static String ouputFileNamePrefix = "OPENKITCHEN Bar Wien ";
	public static int maxOutputWidth = 640;
	public static int maxOutputHeight = 480;
	public static String tagText = "alessaesteban.com";
	public static Color tagColor = Color.WHITE; 
	
	private static int imageCount = 0;
	
	
	
	
	
	
	public static void resizeImageFile(File imageFile) {
		System.out.println("process: " + ++imageCount + " " + imageFile.getName());
		
		
		BufferedImage originalBufferedImage;
		Metadata metadata;
		
		try {
			JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(new FileInputStream(imageFile));
			originalBufferedImage = jpegDecoder.decodeAsBufferedImage();
			JPEGDecodeParam decodeParam = jpegDecoder.getJPEGDecodeParam();
			metadata = JpegMetadataReader.readMetadata(decodeParam);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		
		
		
//		Value	0th Row		0th Column
//		1		top			left side
//		2		top			right side
//		3		bottom		right side
//		4		bottom		left side
//		5		left side	top
//		6		right side	top
//		7		right side	bottom
//		8		left side	bottom
//
//		  1        2       3      4         5            6           7          8
//
//		  X88888  88888X      88  88      X888888888  88                  88  8888888888
//		  88          88      88  88      88  88      88  88          88  88      88  88
//		  8888      8888    8888  8888    88          X888888888  888888888X  X       88
//		  88          88      88  88
//		  88          88  88888X  X88888
		  
		
		Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
		String cameraOrientation = exifDirectory.getString(ExifDirectory.TAG_ORIENTATION);
		int camOrientation = Integer.parseInt(cameraOrientation);
		//System.out.println(cameraOrientation);
		int rotation = 0;
		if(camOrientation == 8) {
			rotation = -90;
		} else if (camOrientation == 6) {
			rotation = 90;
		} else if (camOrientation > 1){
			System.out.println("ORIENTATION: " + camOrientation);
		}
		
		
		
//		BufferedImage originalBufferedImage = R.loadBufferedImage(imageFile.getAbsolutePath());
		BufferedImage resizedBufferedImage = R.getRotatedResizedImage(originalBufferedImage, maxOutputWidth, maxOutputHeight, rotation, 0);
		
		
		Graphics2D g2 = resizedBufferedImage.createGraphics();
		float x = 2; // (resizedBufferedImage.getWidth() / 2) - (tagBufferedImage.getWidth() / 2);
		float y = resizedBufferedImage.getHeight() - tagBufferedImage.getHeight() - 2; //(resizedBufferedImage.getHeight() / 2) - (tagBufferedImage.getHeight() / 2);
		g2.drawImage(tagBufferedImage, (int) x, (int) y, null);
		
		x = resizedBufferedImage.getWidth() - tagBufferedImage2.getWidth() - 2; // (resizedBufferedImage.getWidth() / 2) - (tagBufferedImage.getWidth() / 2);
		y= resizedBufferedImage.getHeight() - tagBufferedImage2.getHeight(); //(resizedBufferedImage.getHeight() / 2) - (tagBufferedImage.getHeight() / 2);
		g2.drawImage(tagBufferedImage2, (int) x, (int) y, null);
		
		

//		System.out.println("add text");
//	    FontMetrics metrics = g2.getFontMetrics(R.FONT.MyriadWebPro_24p);
//	    int hgt = metrics.getHeight();
//	    int adv = metrics.stringWidth(tagText);
//	    Dimension size = new Dimension(adv, hgt);
//	    x = resizedBufferedImage.getWidth() - size.width - 5;
//	    y = resizedBufferedImage.getHeight() - size.height - 5;
//	    g2.setFont(R.FONT.MyriadWebPro_18p);
//	    g2.setColor(tagColor);
//		g2.drawString(tagText, x, y);

		
		g2.dispose();
		
		
		
		File outputFile = new File(outputFolder+File.separator+ouputFileNamePrefix+imageCount+".png");
		try {
			ImageIO.write(resizedBufferedImage, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public static void main(String[] args) {
		
		File inputFile = new File(inputFolder);
		if(!inputFile.exists() || !inputFile.isDirectory()) {
			System.out.println(inputFolder + " is not a directory");
			return;
		}
		
		File outputFile = new File(outputFolder);
		if(!outputFile.exists() || !outputFile.isDirectory()) {
			System.out.println(outputFile + " is not a directory");
			return;
		}
		
		File[] inputFileArray = inputFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if(file.getName().toLowerCase().endsWith("jpg") &&
						!file.isHidden()) {
					return true;
				}
				return false;
			}
		});
		
		for(File f : inputFileArray) {
			//if(imageCount >= 10) return;
			resizeImageFile(f);
		}
		
	}

}
