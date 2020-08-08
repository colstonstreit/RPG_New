package Engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * This class contains several utility classes, including Vec2s and Vec3s to store pairs and triplets of numbers, as well as a Matrix class that can handle
 * matrix operations, an "fRect" that is essentially a rectangle but that contains doubles and can draw itself, and a ResourceLoader class that can load images,
 * text files, and audio clips so far.
 */
public class Tools {

	public static class Vec2 extends Matrix {

		public double x, y;

		public Vec2(double x, double y) {
			super(2, 1);
			setValues(new double[][] { { x } , { y } });
			this.x = x;
			this.y = y;
		}

		public Vec2 add(Vec2 v) { return new Vec2(x + v.x, y + v.y); }

		public Vec2 subtract(Vec2 v) { return new Vec2(x - v.x, y - v.y); }

		public Vec2 scale(double c) { return new Vec2(x * c, y * c); }

		public Vec2 norm() { return new Vec2(x / getMagnitude(), y / getMagnitude()); }

		public double getMagnitude() { return Math.sqrt(x * x + y * y); }

		public double distanceTo(Vec2 v) { return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y)); }

		public double dot(Vec2 v) { return x * v.x + y * v.y; }

		public String toString() { return String.format("(%.3f, %.3f)", x, y); }

		public boolean equals(Vec2 v) { return x == v.x && y == v.y; }

	} // class Vec2

	////////////////////////////////////////////////////////////////////////////////////////////

	public static class Vec3 extends Matrix {

		public double x, y, z;

		public Vec3(double x, double y, double z) {
			super(3, 1);
			setValues(new double[][] { { x } , { y } , { z } });
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Vec3 add(Vec3 v) { return new Vec3(x + v.x, y + v.y, z + v.z); }

		public Vec3 subtract(Vec3 v) { return new Vec3(x - v.x, y - v.y, z - v.z); }

		public Vec3 scale(double d) { return new Vec3(x * d, y * d, z * d); }

		public Vec3 norm() { return scale(1.0 / getMagnitude()); }

		public double getMagnitude() { return Math.sqrt(x * x + y * y + z * z); }

		public double distanceTo(Vec3 v) { return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z)); }

		public double dot(Vec3 v) { return x * v.x + y * v.y + z * v.z; }

		public Vec3 cross(Vec3 v) { return new Vec3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x); }

		public String toString() { return String.format("(%.3f, %.3f, %.3f)", x, y, z); }

		public boolean equals(Vec3 v) { return x == v.x && y == v.y && z == v.z; }

	} // class Vec3

	////////////////////////////////////////////////////////////////////////////////////////////

	public static class fRect {

		public double x, y, width, height;

		public fRect(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		/**
		 * Returns a new Tools.fRect that is translated right by x units and down by y units.
		 * 
		 * @param x
		 * @param y
		 */
		public fRect translate(double x, double y) { return new fRect(this.x + x, this.y + y, width, height); }

		/**
		 * Returns a new Tools.fRect that is a sub-rectangle of this fRect, designated by the relative parameters passed in between 0 and 1.
		 * 
		 * @param relX
		 * @param relY
		 * @param relWidth
		 * @param relHeight
		 */
		public fRect getSubRect(double relX, double relY, double relWidth, double relHeight) {
			relX = Engine.clamp(relX, 0, 1);
			relY = Engine.clamp(relY, 0, 1);
			relWidth = Engine.clamp(relWidth, 0, 1 - relX);
			relHeight = Engine.clamp(relHeight, 0, 1 - relY);
			return new fRect(x + relX * width, y + relY * height, relWidth * width, relHeight * height);
		}

		/**
		 * Fills this fRect using the specified Graphics object and Color.
		 * 
		 * @param g
		 * @param c
		 */
		public void fill(Graphics g, Color c) {
			g.setColor(c);
			g.fillRect((int) Engine.round(x, 1), (int) Engine.round(y, 1), (int) Engine.round(width, 1), (int) Engine.round(height, 1));
		}

		/**
		 * Draws the outline of this fRect using the specified Graphics object and Color.
		 * 
		 * @param g
		 * @param c
		 */
		public void draw(Graphics g, Color c) {
			g.setColor(c);
			g.drawRect((int) Engine.round(x, 1), (int) Engine.round(y, 1), (int) Engine.round(width, 1), (int) Engine.round(height, 1));
		}

		/**
		 * Fills an oval contained within this fRect.
		 * 
		 * @param g
		 * @param c
		 */
		public void fillOval(Graphics g, Color c) {
			g.setColor(c);
			g.fillOval((int) Engine.round(x, 1), (int) Engine.round(y, 1), (int) Engine.round(width, 1), (int) Engine.round(height, 1));
		}

		/**
		 * Draws the outline of an oval contained within this fRect.
		 * 
		 * @param g
		 * @param c
		 */
		public void drawOval(Graphics g, Color c) {
			g.setColor(c);
			g.drawOval((int) Engine.round(x, 1), (int) Engine.round(y, 1), (int) Engine.round(width, 1), (int) Engine.round(height, 1));
		}

		/**
		 * Returns true if this intersects the given Rectangle r.
		 * 
		 * @param r
		 */
		public boolean intersects(fRect r) {
			double tw = this.width;
			double th = this.height;
			double rw = r.width;
			double rh = r.height;
			if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
				return false;
			}
			double tx = this.x;
			double ty = this.y;
			double rx = r.x;
			double ry = r.y;
			rw += rx;
			rh += ry;
			tw += tx;
			th += ty;
			return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
		}

		/**
		 * Returns this fRect casted to a normal Rectangle.
		 */
		public Rectangle cast() { return new Rectangle((int) x, (int) y, (int) width, (int) height); }

		/**
		 * Returns a given Rectangle as a Tools.fRect.
		 */
		public static fRect castToFRect(Rectangle r) { return new Tools.fRect(r.x, r.y, r.width, r.height); }

		/**
		 * Returns a String representation of this fRect in format of (x, y), w = width, h = height.
		 */
		public String toString() { return String.format("(%.2f, %.2f), w = %.2f & h = %.2f.", x, y, width, height); }

	} // class fRect

	////////////////////////////////////////////////////////////////////////////////////////////

	public static class Matrix {

		public double[][] data;

		public Matrix(int rows, int cols) { data = new double[rows][cols]; }

		private Matrix(double[][] data) { this.data = data; }

		public Matrix setValues(double[][] newData) {
			if (newData.length != data.length || newData[0].length != data[0].length)
				throw new IllegalArgumentException("You must pass in data with the same dimensions as the matrix you specified!");

			for (int y = 0; y < newData.length; y++) {
				for (int x = 0; x < newData[y].length; x++) {
					data[y][x] = newData[y][x];
				}
			}
			return this;
		}

		public Matrix scale(double c) {
			double[][] newData = new double[data.length][data[0].length];
			for (int y = 0; y < newData.length; y++) {
				for (int x = 0; x < newData[0].length; x++) {
					newData[y][x] = data[y][x] * c;
				}
			}
			return new Matrix(newData);
		}

		public Matrix add(Matrix m) {
			if (m.data.length != data.length || m.data[0].length != data[0].length)
				throw new IllegalArgumentException("Matrices must be the same size to be added.");

			double[][] newData = new double[data.length][data[0].length];
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[y].length; x++) {
					newData[y][x] = data[y][x] + m.data[y][x];
				}
			}
			return new Matrix(newData);
		}

		public Matrix subtract(Matrix m) {
			if (m.data.length != data.length || m.data[0].length != data[0].length)
				throw new IllegalArgumentException("Matrices must be the same size to be subtracted.");

			double[][] newData = new double[data.length][data[0].length];
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[y].length; x++) {
					newData[y][x] = data[y][x] - m.data[y][x];
				}
			}
			return new Matrix(newData);
		}

		public Matrix multiply(Matrix m) {
			if (data[0].length != m.data.length)
				throw new IllegalArgumentException("Invalid multiplication. An mxn matrix can only be multiplied by an nxp matrix to form an mxp matrix.");

			double[][] ret = new double[data.length][m.data[0].length];
			for (int y = 0; y < ret.length; y++) {
				for (int x = 0; x < ret[y].length; x++) {
					double count = 0;
					for (int c = 0; c < data[0].length; c++) {
						count += data[y][c] * m.data[c][x];
					}
					ret[y][x] = count;
				}
			}
			return new Matrix(ret);
		}

		public Matrix transpose() {
			double[][] newData = new double[data[0].length][data.length];
			for (int y = 0; y < newData.length; y++) {
				for (int x = 0; x < newData[y].length; x++) {
					newData[y][x] = data[x][y];
				}
			}
			return new Matrix(newData);
		}

		public Matrix getSubMatrixCenteredAround(int row, int col) {
			if (row < 0 || col < 0 || row > data.length || col > data[0].length) throw new IllegalArgumentException("Must enter a space within the matrix!");

			if (data.length == 1 || data[0].length == 1) return this;

			double[][] newData = new double[data.length - 1][data[0].length - 1];
			int countX = 0, countY = 0;
			for (int y = 0; y < data.length; y++) {
				if (y == row) {
					countY++;
					continue;
				}
				for (int x = 0; x < data[y].length; x++) {
					if (x == col) {
						countX++;
						continue;
					}
					newData[y - countY][x - countX] = data[y][x];
				}
				countX = 0;
			}
			return new Matrix(newData);
		}

		public double determinant(Matrix m) {

			if (m.data.length != m.data[0].length) throw new IllegalArgumentException("Matrices must be square to have a determinant!");

			if (m.data.length == 1) return m.data[0][0];

			double sum = 0;
			for (int y = 0; y < m.data.length; y++) {
				if (y % 2 == 0) sum += m.data[y][0] * determinant(m.getSubMatrixCenteredAround(y, 0));
				else sum -= m.data[y][0] * determinant(m.getSubMatrixCenteredAround(y, 0));
			}
			return sum;

		}

		public Matrix inverse() {
			if (data.length != data[0].length) throw new IllegalArgumentException("Matrices must be square to have an inverse.");

			double determinant = determinant(this);

			if (Math.abs(determinant) < 0.001) throw new IllegalArgumentException("Cannot invert a singular matrix! Determinant must not be zero.");

			double[][] newData = new double[data.length][data.length];
			for (int y = 0; y < data.length; y++) {
				for (int x = 0; x < data[y].length; x++) {
					if ((x + y) % 2 == 0) newData[y][x] = determinant(getSubMatrixCenteredAround(y, x));
					else newData[y][x] = -determinant(getSubMatrixCenteredAround(y, x));
				}
			}
			return new Matrix(newData).transpose().scale(1 / determinant);

		}

		public static Matrix rotationMatrix2D(double angle) {
			return new Matrix(new double[][] { { Math.cos(angle) , Math.sin(angle) } , { -Math.sin(angle) , Math.cos(angle) } });
		}

		public String toString() {
			StringBuilder ret = new StringBuilder();
			for (int y = 0; y < data.length; y++) {
				ret.append("|");
				for (int x = 0; x < data[y].length; x++) {
					ret.append(String.format((x == data[y].length - 1) ? "%.2f|" : "%.2f ", data[y][x]));
				}
				ret.append("\n");
			}
			return ret.toString();
		}

		/**
		 * Returns a Vector representation of the matrix if the matrix is only one column.
		 */
		public Matrix toVector() {
			if (data[0].length == 1) {
				switch (data.length) {
					case 2:
						return new Vec2(data[0][0], data[1][0]);
					case 3:
						return new Vec3(data[0][0], data[1][0], data[2][0]);
					default:
						return null;
				}
			} else {
				throw new IllegalArgumentException("Only matrices of one column can be transformed into a vector!");
			}
		}

	} // class Matrix

	////////////////////////////////////////////////////////////////////////////////////////////

	public static class ResourceLoader {

		public static final int LOAD_FILE = 1;
		public static final int LOAD_RESOURCE = 2;

		/**
		 * Returns a BufferedImage loaded from the given path.
		 * 
		 * @param path
		 */
		public static BufferedImage loadImage(String path) {
			BufferedImage image;
			try {
				image = ImageIO.read(ResourceLoader.class.getResource(path));
				return image;
			} catch (Exception e) {
				System.out.println("Image with path " + path + " not found!");
				return null;
			}
		}

		/**
		 * Returns a text file as a string from the specified path. Using mode LOAD_FILE loads it as a file: using mode LOAD_RESOURCE loads it as a resource.
		 * 
		 * @param path
		 * @param mode LOAD_FILE if as file, LOAD_RESOURCE if as resource.
		 */
		public static String loadTextFile(String path, int mode) {
			StringBuilder stringBuilder = new StringBuilder();
			try {
				BufferedReader br = null;

				if (mode == LOAD_FILE) {
					br = new BufferedReader(new FileReader(new File(path)));
				} else if (mode == LOAD_RESOURCE) {
					InputStream is = ResourceLoader.class.getResourceAsStream(path);
					br = new BufferedReader(new InputStreamReader(is));
				} else {
					throw new IllegalArgumentException("Invalid mode passed in!");
				}

				String temp;
				while ((temp = br.readLine()) != null) {
					stringBuilder.append(temp + "\n");
				}
				br.close();
				return stringBuilder.toString().trim();
			} catch (IOException e) {
				System.out.println("Text file with path " + path + " not found!");
				return "";
			}
		}

		/**
		 * Returns an audio Clip loaded from the specified path.
		 * 
		 * @param path
		 */
		public static Clip loadAudioFile(String path) {
			try {
				AudioInputStream stream = AudioSystem.getAudioInputStream(new File(path));
				AudioFormat format = stream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				Clip audioClip = (Clip) AudioSystem.getLine(info);
				audioClip.open(stream);
				return audioClip;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	} // class ResourceLoader

}
