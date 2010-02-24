import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.imageio.ImageIO;


public class PhotoTagger {

	
	/* input */
	public static String inputFolder = "/Users/vinzenzweber/Pictures/chili/101MSDCF";
	public static String tagLogo = "/Users/vinzenzweber/Pictures/chili/openkitchen_phototag.png";
	public static BufferedImage tagBufferedImage = R.loadBufferedImage(tagLogo);
	public static String tagLogo2 = "/Users/vinzenzweber/Pictures/chili/alessaesteban_com.png";
	public static BufferedImage tagBufferedImage2 = R.loadBufferedImage(tagLogo2);
	
	
	/* output */
	public static String outputFolder = "/Users/vinzenzweber/Pictures/chili/output";
	public static int maxOutputWidth = 640;
	public static int maxOutputHeight = 480;
	public static String tagText = "alessaesteban.com";
	public static Color tagColor = Color.WHITE; 
	
	private static int imageCount = 0;
	
	
	
	
	
	
	public static void resizeImageFile(File imageFile) {
		System.out.println("process: " + ++imageCount + " " + imageFile.getName());
		
		System.out.println("load images");
		BufferedImage originalBufferedImage = R.loadBufferedImage(imageFile.getAbsolutePath());
		BufferedImage resizedBufferedImage = R.getFittingResizedImage(originalBufferedImage, maxOutputWidth, maxOutputHeight, 0);
		
		System.out.println("merge images");
		Graphics2D g2 = resizedBufferedImage.createGraphics();
		float x = 2; // (resizedBufferedImage.getWidth() / 2) - (tagBufferedImage.getWidth() / 2);
		float y = resizedBufferedImage.getHeight() - tagBufferedImage.getHeight() - 2; //(resizedBufferedImage.getHeight() / 2) - (tagBufferedImage.getHeight() / 2);
		g2.drawImage(tagBufferedImage, (int) x, (int) y, null);
		
		x = resizedBufferedImage.getWidth() - tagBufferedImage2.getWidth() - 2; // (resizedBufferedImage.getWidth() / 2) - (tagBufferedImage.getWidth() / 2);
		y= resizedBufferedImage.getHeight() - tagBufferedImage2.getHeight(); //(resizedBufferedImage.getHeight() / 2) - (tagBufferedImage.getHeight() / 2);
		g2.drawImage(tagBufferedImage2, (int) x, (int) y, null);
		
		
/*		
		System.out.println("add text");
	    FontMetrics metrics = g2.getFontMetrics(R.FONT.MyriadWebPro_24p);
	    int hgt = metrics.getHeight();
	    int adv = metrics.stringWidth(tagText);
	    Dimension size = new Dimension(adv, hgt);
	    x = resizedBufferedImage.getWidth() - size.width - 5;
	    y = resizedBufferedImage.getHeight() - size.height - 5;
	    g2.setFont(R.FONT.MyriadWebPro_18p);
	    g2.setColor(tagColor);
		g2.drawString(tagText, x, y);
*/		
		
		g2.dispose();
		
		
		System.out.println("store the image");
		File outputFile = new File(outputFolder+File.separator+imageCount+".png");
		try {
			ImageIO.write(resizedBufferedImage, "png", outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("finished");
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
			//if(imageCount >= 1) return;
			resizeImageFile(f);
		}
		
	}

}
