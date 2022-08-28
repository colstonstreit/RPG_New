package Engine;

import java.awt.image.BufferedImage;

public class Sprite {

	private BufferedImage image; // The image this sprite represents
	private int pWidth, pHeight; // Size of each tile on the image
	private byte tWidth, tHeight; // Size of the image in tiles

	/**
	 * @param path    The path of the image to be loaded
	 * @param pWidth  The width of each tile on the image in pixels
	 * @param pHeight The height of each tile on the image in pixels
	 */
	public Sprite(String path, int pWidth, int pHeight) {
		this.pWidth = pWidth;
		this.pHeight = pHeight;
		image = Tools.ResourceLoader.loadImage(path);
		tWidth = (byte) (image.getWidth() / pWidth);
		tHeight = (byte) (image.getHeight() / pHeight);
	}

	/**
	 * @param path  The path of the image to be loaded
	 * @param pSize The square size of each tile on the image in pixels
	 */
	public Sprite(String path, int pSize) {
		this.pWidth = pSize;
		this.pHeight = pSize;
		image = Tools.ResourceLoader.loadImage(path);
		tWidth = (byte) (image.getWidth() / pSize);
		tHeight = (byte) (image.getHeight() / pSize);
	}

	/**
	 * @param image   The image represented by this Sprite
	 * @param pWidth  The width of each tile on the image in pixels
	 * @param pHeight The height of each tile on the image in pixels
	 */
	public Sprite(BufferedImage image, int pWidth, int pHeight) {
		this.pWidth = pWidth;
		this.pHeight = pHeight;
		this.image = image;
		tWidth = (byte) (image.getWidth() / pWidth);
		tHeight = (byte) (image.getHeight() / pHeight);
	}

	/**
	 * @param image The image represented by this Sprite
	 * @param pSize The square size of each tile on the image in pixels
	 */
	public Sprite(BufferedImage image, int pSize) {
		this.pWidth = pSize;
		this.pHeight = pSize;
		this.image = image;
		tWidth = (byte) (image.getWidth() / pSize);
		tHeight = (byte) (image.getHeight() / pSize);
	}

	/**
	 * Returns a sub-Sprite cropped out of this Sprite based on the given parameters.
	 * 
	 * @param tx      The x coordinate of the top-left corner of the desired crop selection
	 * @param ty      The y coordinate of the top-left corner of the desired crop selection
	 * @param tWidth  The width of the desired crop selection in tiles
	 * @param tHeight The height of the desired crop selection in tiles
	 * @return A new sprite cropped from this Sprite
	 */
	public Sprite crop(int tx, int ty, int tWidth, int tHeight) {
		if (tx < 0 || ty < 0 || tWidth < 1 || tHeight < 1 || (tx + tWidth) > image.getWidth() / pWidth || (ty + tHeight) > image.getHeight() / pHeight)
			return null;
		else return new Sprite(image.getSubimage(tx * pWidth, ty * pHeight, tWidth * pWidth, tHeight * pHeight), pWidth, pHeight);
	}

	/**
	 * Returns true if the width and height of this tile are equal to 1 (only one tile in image).
	 */
	public boolean isSingleTile() { return tWidth == 1 && tHeight == 1; }

	/**
	 * Returns the image contained by this sprite.
	 */
	public BufferedImage image() { return image; }

}
