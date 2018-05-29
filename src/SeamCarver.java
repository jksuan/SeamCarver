/******************************************************************************
 *  Compilation:  javac SeamCarver.java
 *  Execution:    java SeamCarver *.png
 *  Dependencies: edu.princeton.cs.algs4.Picture
 *                edu.princeton.cs.algs4.Stack;
 *                java.awt.Color;
 *                
 *  Data files:   /seam-testing/6x5.png
 *                /seam-testing/7x10.png
 *                /seam-testing/1x8.png 
 *                /seam-testing/8x1.png 
 ******************************************************************************/

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import java.awt.Color;

/**
 *  Note: 
 *  1. Reserve the RGB to a 2D array, and maintain it, 
 *     don't reserver the color object in a 2D array, 
 *     because an object occupies much more memory space.
 *  2. To get the color info from RGB, use bit shift operation, for example:
 *     {@code int rgb = picture.get(x, y).getRGB();}
 *     {@code int red =   (rgb >> 16) & 0xFF;}
 *     {@code int green = (rgb >> 8) & 0xFF;}
 *     {@code int blue =  (rgb >> 0) & 0xFF;}
 *  3. Refer to the AcyclicSP.java and make sure to understand the topological sort algorithm 
 *     for computing a shortest path in a DAG.
 *  4. To find a minimum energy seam in method {@code findVerticalSeam()}, 
 *     use {@code distTo[width][height])} to record the minimum energy 
 *     from top row to the current pixel, similarly to find the shortest path in a DAG;
 *     and use {@code egdeTo[width][height])} to record the parent pixel for tracking back.
 *  <p>
 *  For additional documentation, see
 *  <i>/specifications/specification-Programming_Assignment 2_ SeamCarving.html</i>
 *  <i>/specifications/Checklists-Programming_Assignment 2_ SeamCarving.html</i>
 *  
 *  @author Chihkai Sun, email: jksuan@163.com
 *  @date 29th May 2018
 *  
 */

public class SeamCarver {
  private final Picture picture;
  private int width;     // width of current picture
  private int height;    // height of current picture  
  private int[][] initRGB; // reserve the RGB info of the current picture  
  
  /**
   * Constructor
   * Create a seam carver object based on the given picture
   * 
   * @param picture the given {@code picture}
   * @throws IllegalArgumentException if the {@code picture} is null
   */
  public SeamCarver(Picture picture) {
    if (picture == null) {
      throw new IllegalArgumentException("constructor must be called with a non-null argument");
    }
    this.picture = new Picture(picture); // deep copy the picture object
    this.width = picture.width();
    this.height = picture.height();
    initRGB = getPixelValueFromInitPic(width, height);  
  }
  
  private int[][] getPixelValueFromInitPic(int width, int height) {
    int[][] rgb = new int[width][height];
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        rgb[col][row] = picture.get(col, row).getRGB(); // col = x, row = y
      }
    }
    return rgb;
  }
        
  /**
   * Creates a new picture and returns it
   * 
   * @return the new picture
   */
  public Picture picture() {
    Picture pic = new Picture(width, height);
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        Color color = new Color(initRGB[col][row]);
        pic.set(col, row, color);
      }
    }
    return pic;
  }
  
  
  /**
   * Returns the width of current picture
   * 
   * @return the width of current picture
   */
  public int width() {
    return this.width;
  }
  
  /**
   * Returns the height of current picture
   * 
   * @return the height of current picture
   */
  public int height() {
    return this.height;
  }
  
  private double xGradient(int x, int y) {    
    int left = initRGB[x - 1][y]; 
    int lRed = (left >> 16) & 0xFF;  // get the red value 
    int lGreen = (left >> 8) & 0xFF; // get the green value 
    int lBlue = (left >> 0) & 0xFF;  // get the blue value 
    
    int right = initRGB[x + 1][y]; 
    int rRed = (right >> 16) & 0xFF;   
    int rGreen = (right >> 8) & 0xFF;
    int rBlue = (right >> 0) & 0xFF;
    
    return Math.pow(rRed - lRed, 2) + Math.pow(rGreen - lGreen, 2) + Math.pow(rBlue - lBlue, 2);
  }
  
  private double yGradient(int x, int y) {    
    int down = initRGB[x][y - 1]; 
    int dRed = (down >> 16) & 0xFF;
    int dGreen = (down >> 8) & 0xFF;
    int dBlue = (down >> 0) & 0xFF;
    
    int up = initRGB[x][y + 1]; 
    int uRed = (up >> 16) & 0xFF;
    int uGreen = (up >> 8) & 0xFF;
    int uBlue = (up >> 0) & 0xFF;
    
    return Math.pow(dRed - uRed, 2) + Math.pow(dGreen - uGreen, 2) + Math.pow(dBlue - uBlue, 2);
  }
  
  private void validateRow(int y) {
    if (y < 0 || y >= height())
      throw new IllegalArgumentException("y must be between 0 and " + (height() - 1) + ": " + y);      
  }
  
  private void validateCol(int x) {
    if (x < 0 || x >= width())
      throw new IllegalArgumentException("x must be between 0 and " + (width() - 1) + ": " + x);      
  }
    
  /**
   * Calculate the energy of pixel at column x and row y of current picture
   * 
   * @param x the column
   * @param y the row 
   * @return the energy of pixel 
   * @throws IllegalArgumentException if {@code x} is not between 0 and {@code width() - 1}
   * @throws IllegalArgumentException if {@code y} is not between 0 and {@code height() - 1}
   */
  public double energy(int x, int y) {
    validateRow(y);
    validateCol(x);
   
    if (x == 0 || (x == width() - 1) || y == 0 || (y == height() - 1)) {    
      return 1000.0;
    }    
    return Math.sqrt(xGradient(x, y) + yGradient(x, y));
  }
  
  // swap the width and height of the initRGB array 
  private int[][] transposePixelValue(int width, int height) {
    int[][] rgb = new int[width][height];
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        rgb[col][row] = this.initRGB[row][col]; // swap the row and col
      }
    }
    return rgb;
  }  
  
  // Transpose RGB array of the picture, 
  // not truly transpose the picture
  // Swap the width and the height in calling method
  private void transposePic(int width, int height) {  
    this.width = width;
    this.height = height;
    this.initRGB = transposePixelValue(width, height);     
  }
  
  /**
   * Find the horizontal seam of minimal energy
   * Transpose the picture, call {@code findVerticalSeam()}, and transpose it back
   * 
   * @return sequence of indices for horizontal seam
   */
  public int[] findHorizontalSeam() {
    int tmpWidth = this.width;
    int tmpHeight = this.height;
    int[] seam;
    
    transposePic(tmpHeight, tmpWidth); // swap the width and height
    seam = findVerticalSeam();
    transposePic(tmpWidth, tmpHeight); // transpose back, swap the width and height again
                                                 
    return seam;
  }  
  
  
  /**
   * Find the vertical seam of minimal energy
   * Refer to the topological.java 
   * 
   * @return sequence of indices for vertical seam
   */
  public int[] findVerticalSeam() {
    int[] seam = new int[height];    
    int[][] edgeTo = new int[width][height];       // the column of parent pixel of the current pixel
    double[][] distTo = new double[width][height]; // minimal energy from the first row to the current pixel
    
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        if (row == 0) {
          distTo[col][row] = 1000.0;
          edgeTo[col][row] = col;
        } else {
          distTo[col][row] = Double.POSITIVE_INFINITY;
          edgeTo[col][row] = 0;
        }
      }
    }
    
    for (int row = 0; row < height - 1; row++) {
      for (int col = 0; col < width - 1; col++) {
        int pos = col - 1;
        if (pos < 0) {
          pos = 0;
        }
        for (; pos < col + 2; pos++) {
          if (distTo[pos][row + 1] > distTo[col][row] + energy(pos, row + 1)) {
            distTo[pos][row + 1] = distTo[col][row] + energy(pos, row + 1);
            edgeTo[pos][row + 1] = col; // record the column of parent pixel 
          }
        } // end pos
      } // end col
    } // end row
    
    /* for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        System.out.print(distTo[col][row] + " ");
      }
      System.out.println();
    } */
    
    // find the column of the minimum energy at the bottom row
    double minEnergy = distTo[0][height - 1];
    int bottomPoint = 0;
    for (int col = 0; col < width; col++) {
      if (minEnergy > distTo[col][height - 1]) {
        minEnergy = distTo[col][height - 1];
        bottomPoint = col;
      }
    }
    
    // track back from the bottom
    Stack<Integer> reverseSeam = new Stack<Integer>();
    reverseSeam.push(bottomPoint);
    for (int row = height - 1; row > 0; row--) {
      int otherPoint = reverseSeam.peek();
      reverseSeam.push(edgeTo[otherPoint][row]);
    }
    
    // construct the seam
    for (int row = 0; row < height; row++) {
      seam[row] = reverseSeam.pop();
    }   
    
    return seam;
  }
  
  private void validatePictureWidth() {
    if (width() <= 1) {
      throw new IllegalArgumentException("The width of picture must not be less than 1");
    }
  }
  
  private void validateVerticalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Vertical seam must not be null");
    }
    
    if (seam.length != height())
      throw new IllegalArgumentException("length of vertical seam must be = " + height() + ": " + seam.length);
    
    for (int i = 0; i < seam.length; i++) {
      if (seam[i] < 0 || seam[i] >= width())
        throw new IllegalArgumentException("col must be between 0 and " + (width() - 1) + ": " + seam[i]);
    }
    
    // check two adjacent entries differ by more than 1
    for (int i = 0; i < seam.length - 1; i++) {
      int diff = Math.abs(seam[i] - seam[i + 1]);
      if (diff > 1)
        throw new IllegalArgumentException("two adjacent entries must not differ by more than 1: " + diff);
    }    
  }
  
  /**
   * Remove vertical seam from current picture
   * Work for any valid seam(not only the minimum energy seams)
   * 
   * @param seam sequence of indices for vertical seam
   * @throws IllegalArgumentException if the {@code seam} width of picture is less than 1
   * @throws IllegalArgumentException if the {@code seam} is null
   * @throws IllegalArgumentException if the length of {@code seam} doesn't equal the height of the picture
   * @throws IllegalArgumentException if an entry of {@code seam} is not between 0 and {@code width() - 1}
   * @throws IllegalArgumentException if two adjacent entries of {@code seam} differ by more than 1
   */
  public void removeVerticalSeam(int[] seam) {
    validatePictureWidth();
    validateVerticalSeam(seam);
    int tmpWidth = this.width;
    int tmpHeight = this.height;
    
    // remove the seam, copy the array from right to left
    transposePic(tmpHeight, tmpWidth);
    for (int row = 0; row < seam.length; row++) {
      int srcPos = seam[row] + 1;
      int destPos = seam[row];
      int length = tmpWidth - seam[row] - 1;
      System.arraycopy(initRGB[row], srcPos, initRGB[row], destPos, length);
    }    
    transposePic(tmpWidth, tmpHeight);
    
    this.width--;
  }
  
  private void validatePictureHeight() {
    if (height() <= 1) {
      throw new IllegalArgumentException("The height of picture must not be less than 1");
    }
  }
  
  private void validateHorizontalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Vertical seam must not be null");
    }
    
    if (seam.length != width())
      throw new IllegalArgumentException("length of vertical seam must be = " + width() + ": " + seam.length);
    
    for (int i = 0; i < seam.length; i++) {
      if (seam[i] < 0 || seam[i] >= height())
        throw new IllegalArgumentException("col must be between 0 and " + (height() - 1) + ": " + seam[i]);
    }
    
    // check two adjacent entries differ by more than 1
    for (int i = 0; i < seam.length - 1; i++) {
      int diff = Math.abs(seam[i] - seam[i + 1]);
      if (diff > 1)
        throw new IllegalArgumentException("two adjacent entries must not differ by more than 1: " + diff);
    }    
  }
  
  
  /**
   * Remove horizontal seam from current picture
   * Work for any valid seam(not only the minimum energy seams)
   * 
   * @param seam sequence of indices for horizontal seam
   * @throws IllegalArgumentException if the height of picture is less than 1
   * @throws IllegalArgumentException if the {@code seam} is null
   * @throws IllegalArgumentException if the length of {@code seam} doesn't equal the width of the picture
   * @throws IllegalArgumentException if an entry of {@code seam} is not between 0 and {@code height() - 1}
   * @throws IllegalArgumentException if two adjacent entries of {@code seam} differ by more than 1
   */
  public void removeHorizontalSeam(int[] seam) {
    validatePictureHeight();
    validateHorizontalSeam(seam);
    
    for (int col = 0; col < seam.length; col++) {
      int srcPos = seam[col] + 1;
      int destPos = seam[col];
      int length = height - seam[col] - 1;
      System.arraycopy(initRGB[col], srcPos, initRGB[col], destPos, length);
    }    
    
    this.height--;
  }
   
  
  /**
   * Unit tests the {@code picture} data type.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    Picture picture = new Picture("seam-testing/6x5.png");
    int width  = picture.width();
    int height = picture.height();
    SeamCarver sc = new SeamCarver(picture);
    int[] seam = new int[height];
    seam = sc.findVerticalSeam(); 
    sc.removeVerticalSeam(seam);
    picture = sc.picture();
   
    picture.show();
  }
}