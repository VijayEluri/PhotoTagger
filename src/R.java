

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
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
	 * @param degree The rotation in degrees. MUST be a multiple of 90.
	 * @param transparency The transparency level for the new image, set 0 for default.
	 * @return Resized image.
	 */
	public static BufferedImage getRotatedResizedImage(BufferedImage img, int maxWidth, int maxHeight, int degree, int transparency) {
		
		
		if ((img.getHeight() <= maxHeight && img.getWidth() <= maxWidth) ||
				(img.getHeight() <= maxWidth && img.getWidth() <= maxHeight)) {
			return img;
		}
		
		
		// calculate if landscape or portrait
		boolean landscape = true;
		if((Math.abs(degree) % 90) == 0) {
			landscape = (Math.abs(degree)/90) % 2 == 0;
		}
		
		// calculate scale rate
		double scaleh = (double) maxHeight / img.getHeight();
		double scalew = (double) maxWidth / img.getWidth();
		final double SCALE = (scaleh > scalew) ? scalew : scaleh;
		int targetWidth, targetHeight;
		if(landscape) {
			targetWidth = (int) (SCALE * img.getWidth());
			targetHeight = (int) (SCALE * img.getHeight());
		} else {
			targetWidth = (int) (SCALE * img.getHeight());
			targetHeight = (int) (SCALE * img.getWidth());
		}
		
		
		
		
		
        int type = (img.getTransparency() == Transparency.OPAQUE) ? 
        		BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
            
        BufferedImage rsimg = (BufferedImage)img;
        int w, h;

        // Use multi-step technique: start with original size, then
        // scale down in multiple passes with drawImage()
        // until the target size is reached
        w = img.getWidth();
        h = img.getHeight();
        
        do {
        	
            if (w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(rsimg, 0, 0, w, h, null);
            g2.dispose();

            rsimg = tmp;
        } while (w != targetWidth || h != targetHeight);

        return rsimg;

/*
		// get default graphics configuration
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		
		// create compatible image
		transparency = (transparency == 0) ? img.getColorModel().getTransparency() : transparency; 
		BufferedImage rsimg = gc.createCompatibleImage(newWidth, newHeight, transparency);
		Graphics2D g2 = rsimg.createGraphics();
	
		// resize image
		//g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		AffineTransform at = new AffineTransform();
		if((Math.abs(degree) % 90) == 0) {
			if(landscape) {
				at.rotate(Math.toRadians(degree), newWidth / 2.0, newHeight / 2.0);
			} else {
				at.rotate(Math.toRadians(degree), newHeight / 2.0, newWidth / 2.0);
// @TODO WATCH OUT JUST A QUICK FIX :
				double txy = 0;
				if(degree == -90) {
					txy = newWidth/2 - newHeight/2;
				} else if(degree == 90) {
					txy = newHeight/2 - newWidth/2;
				}
				at.translate(txy, txy);
			}
		}
		at.scale(SCALE, SCALE);
		

		
		g2.drawRenderedImage(img, at);
		g2.dispose();		
		
		return rsimg;
*/
	}
}
