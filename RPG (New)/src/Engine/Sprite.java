package Engine;
import java.awt.image.BufferedImage;

public class Sprite {
	
	private BufferedImage image;
	private int pWidth, pHeight;
	private byte tWidth, tHeight;

	public Sprite(String path, int pWidth, int pHeight) {
		this.pWidth = pWidth;
		this.pHeight = pHeight;
		image = Tools.ResourceLoader.loadImage(path);
		tWidth = (byte) (image.getWidth() / pWidth);
		tHeight = (byte) (image.getHeight() / pHeight);
	}
	
	public Sprite(String path, int pSize) {
		this.pWidth = pSize;
		this.pHeight = pSize;
		image = Tools.ResourceLoader.loadImage(path);
		tWidth = (byte) (image.getWidth() / pSize);
		tHeight = (byte) (image.getHeight() / pSize);
	}
	
	public Sprite(BufferedImage image, int pWidth, int pHeight) {
		this.pWidth = pWidth;
		this.pHeight = pHeight;
		this.image = image;
		tWidth = (byte) (image.getWidth() / pWidth);
		tHeight = (byte) (image.getHeight() / pHeight);
	}
	
	public Sprite(BufferedImage image, int pSize) {
		this.pWidth = pSize;
		this.pHeight = pSize;
		this.image = image;
		tWidth = (byte) (image.getWidth() / pSize);
		tHeight = (byte) (image.getHeight() / pSize);
	}
	
	public Sprite crop(int tx, int ty, int tWidth, int tHeight) {
		if(tx < 0 || ty < 0 || tWidth < 1 || tHeight < 1 || (tx + tWidth) > image.getWidth() / pWidth || (ty + tHeight) > image.getHeight() / pHeight)
			return null;
		else return new Sprite(image.getSubimage(tx * pWidth, ty * pHeight, tWidth * pWidth, tHeight * pHeight), pWidth, pHeight);
	}
	
	public boolean isSingleTile() { return tWidth == 1 && tHeight == 1;}
	public BufferedImage image() { return image; }
	
}
