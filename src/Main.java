import com.beust.jcommander.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PhotoTagger phototagger = new PhotoTagger();
		
		JCommander jc = new JCommander(phototagger, args);
		jc.usage();
		
		phototagger.runPhotoTagger();
		
	}

}
