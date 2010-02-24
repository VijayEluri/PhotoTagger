

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;




public class R {
	
	
	public final static class FONT {
		public final static Font MyriadWebPro = loadFont("MyriadWebPro.ttf");
		public final static Font MyriadWebPro_15p = deriveFont(MyriadWebPro, 15.0F);
		public final static Font MyriadWebPro_18p = deriveFont(MyriadWebPro, 18.0F);
		public final static Font MyriadWebPro_24p = deriveFont(MyriadWebPro, 24.0F);
	}
	
	
	
	public static Font deriveFont(Font font, float size) {
		return font.deriveFont(size);
	}
	
	public static Font loadFont(String filename) {
		Font font = null;
		try {
			//System.out.println("loading Font " +ressourcepath+filename);
			InputStream is = R.class.getResourceAsStream(filename);
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch(Exception e) {
			System.out.println("FAILED loading Font " + filename);
		}
		return font; 
	}
	
	public static Image loadImage(String filename) {
		//System.out.println("loading Image " +ressourcepath+filename);
		URL url = R.class.getResource(filename);
		if(url == null) {
			System.out.println("FAILED loading Image " +filename);
			return null;
		}
		return new ImageIcon(url).getImage();
	}
	
	public static BufferedImage loadBufferedImage(String filename) {
		BufferedImage bufferedImage = null;

		try {
			URL url = R.class.getResource(filename);
			if(url != null) {
				bufferedImage = ImageIO.read(new File(url.getPath()));
			} else {
				bufferedImage = ImageIO.read(new File(filename));	
			}
			
			//System.out.println("loading BufferedImage " +ressourcepath+filename);
		} catch (IOException e) {
			try {
				// try loading from jar and convert Image to BufferedImage
				Image image = loadImage(filename);		
				bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics2D bufImageGraphics = bufferedImage.createGraphics();
				bufImageGraphics.drawImage(image, 0, 0, null);
//				System.out.println("loading BufferedImage " +ressourcepath+filename);
			} catch (Exception ex) {
				System.out.println("FAILED loading BufferedImage " +filename);
			}
		}
		return bufferedImage;
	}
	
	public static ImageIcon loadImageIcon(String filename) {
		//System.out.println("loading ImageIcon " +ressourcepath+filename);
		URL url = R.class.getResource(filename);
		if(url == null) {
			System.out.println("FAILED loading ImageIcon " +filename);
			return null;
		}
		return new ImageIcon(url);
	}
	
	public static Image getImage(ImageIcon icon) {
		return icon.getImage();
	}

	
	/**
	 * Resize an image to max width and max height by keeping its scale.
	 * 
	 * @param img The image to be resized.
	 * @param maxWidth The maximum image width of the resulting image.
	 * @param maxHeight The maximum image height of the resulting image.
	 * @param transparency The transparency level for the new image, set 0 for default.
	 * @return Resized image.
	 */
	public static BufferedImage getFittingResizedImage(BufferedImage img, int maxWidth, int maxHeight, int transparency) {

		// calculate scale rate
		double scaleh = (double) maxHeight / img.getHeight();
		double scalew = (double) maxWidth / img.getWidth();
		final double SCALE = (scaleh > scalew) ? scalew : scaleh;
		int newWidth = (int) (SCALE * img.getWidth());
		int newHeight = (int) (SCALE * img.getHeight());

		// get default graphics configuration
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		
		// create compatible image
		transparency = (transparency == 0) ? img.getColorModel().getTransparency() : transparency; 
		BufferedImage rsimg = gc.createCompatibleImage(newWidth, newHeight, transparency);
		Graphics2D g2 = rsimg.createGraphics();
	
		// resize image
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		AffineTransform xform = AffineTransform.getScaleInstance(SCALE, SCALE);		
		g2.drawRenderedImage(img, xform);
		g2.dispose();		
		
		return rsimg;
	}
}
