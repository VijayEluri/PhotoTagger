import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import com.beust.jcommander.Parameter;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;


public class PhotoTagger {


	  
	/////////////////////////////////////////////////////////////////////
	// INPUT
	  	  
	@Parameter(names = "-input", description = "Input image folder to be resized and tagged")
	public String inputFolder = "input";
	
	@Parameter(names = "-tag", description = "The image to be used for tagging")
	public String tagLogo = "tag.png";
	public BufferedImage tagBufferedImage = null;
	
	public String tagLogo2 = "phototagtext.png";
	public BufferedImage tagBufferedImage2 = null; // R.loadBufferedImage(tagLogo2);
	
	
	
	/////////////////////////////////////////////////////////////////////
	// OUTPUT
	
	public ImageWriter defaultImageWriter = null;
	public ImageWriteParam defaultImageWriteParam = null;
	
	@Parameter(names = "-output", description = "Output folder for all resized and tagged images")
	public String outputFolder = "output";
	
	@Parameter(names = "-prefix", description = "Prefix for all image names")
	public String ouputFileNamePrefix = "Image ";
	
	@Parameter(names = "-maxWidth", description = "Maximum width for images")
	public Integer maxOutputWidth = 640;
	
	@Parameter(names = "-maxHeight", description = "Maximum height for images")
	public Integer maxOutputHeight = 480;
	
	public String tagText = "tagtext";
	public Color tagColor = Color.WHITE; 
	
	private int imageCount = 0;
	
	
	
	
	
	
	public void runPhotoTagger() {
		
		File inputFile = new File(inputFolder);
		if(!inputFile.exists() || !inputFile.isDirectory()) {
			System.out.println(inputFolder + " is not a directory");
			return;
		}
		
		// load images
		File tagImageFile = new File(tagLogo);
		if(!tagImageFile.exists() || !tagImageFile.isFile()) {
			System.out.println(tagLogo + " does not exist");
			return;
		}
		tagBufferedImage = R.loadBufferedImage(tagLogo);

		
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
		
		if (inputFileArray != null && inputFileArray.length > 0) {
			prepareImageWriter();
		}
		
		for(File f : inputFileArray) {
			//if(imageCount >= 10) return;
			resizeImageFile(f);
		}
		
		//defaultImageWriter.dispose();
		
	}
	
	
	
	public void resizeImageFile(File imageFile) {
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
		int camOrientation = (cameraOrientation == null) ? 0 : Integer.parseInt(cameraOrientation);
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
		originalBufferedImage = null;
		
		
		Graphics2D g2 = resizedBufferedImage.createGraphics();
		float x = 2; // (resizedBufferedImage.getWidth() / 2) - (tagBufferedImage.getWidth() / 2);
		float y = resizedBufferedImage.getHeight() - tagBufferedImage.getHeight() - 2; //(resizedBufferedImage.getHeight() / 2) - (tagBufferedImage.getHeight() / 2);
		g2.drawImage(tagBufferedImage, (int) x, (int) y, null);
		
		if (tagBufferedImage2 != null) {
			x = resizedBufferedImage.getWidth() - tagBufferedImage2.getWidth() - 2; // (resizedBufferedImage.getWidth() / 2) - (tagBufferedImage.getWidth() / 2);
			y = resizedBufferedImage.getHeight() - tagBufferedImage2.getHeight(); //(resizedBufferedImage.getHeight() / 2) - (tagBufferedImage.getHeight() / 2);
			g2.drawImage(tagBufferedImage2, (int) x, (int) y, null);			
		}
		

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
		
		
		
		
		File outputFile = new File(outputFolder+File.separator+ouputFileNamePrefix+imageCount+".jpg");
		try {

			FileImageOutputStream os = new FileImageOutputStream(outputFile);
			defaultImageWriter.setOutput(os);
			IIOImage image = new IIOImage(resizedBufferedImage, null, null);
			defaultImageWriter.write(null, image, defaultImageWriteParam);
			
			// ImageIO.write(resizedBufferedImage, "jpg", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	private void prepareImageWriter() {
		
		Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
		defaultImageWriter = (ImageWriter)iter.next();
		
		// instantiate an ImageWriteParam object with default compression options
		defaultImageWriteParam = defaultImageWriter.getDefaultWriteParam();
		defaultImageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		defaultImageWriteParam.setCompressionQuality(1);   // an integer between 0 and 1
		
	}
	
	

}
