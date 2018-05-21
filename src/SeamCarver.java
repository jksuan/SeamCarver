import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
  private final Picture picture;
  private int width;     // width of current picture
  private int height;    // height of current picture  
  // private Color[] pixel;
  private Color[][] initColors; // preserve the color info of the current picture
  private double[][] initEnergies;
  
  // create a seam carver object based on the given picture
  public SeamCarver(Picture picture) {
    this.picture = new Picture(picture);
    this.width = picture.width();
    this.height = picture.height();
    initColors = getPixelValueFromInitPic(width, height); 
    initEnergies = getEnergyFromInitPic(width, height);    
  }
  
  private Color[][] getPixelValueFromInitPic(int width, int height) {
    Color[][] colors = new Color[width][height];
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        colors[col][row] = picture.get(col, row); // col = x, row = y
      }
    }
    return colors;
  }
  
  private double[][] getEnergyFromInitPic(int width, int height) {
    double[][] energies = new double[width][height];
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
          energies[col][row] = energy(col, row);
      }
    }
    return energies;
  }
  
  //current picture
  public Picture picture() {
    Picture pic = new Picture(width, height);
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        Color color = initColors[col][row];
        // int red = color.getRed();
        // int green = color.getGreen();
        // int blue = color.getBlue();
        pic.set(col, row, color);
      }
    }
    return pic;
  }
  
  //width of current picture
  public int width() {
    return this.width;
  }
  
  //height of current picture
  public int height() {
    return this.height;
  }
  
  private double xGradient(int x, int y) {    
    Color left = initColors[x - 1][y]; //getPixelColor(x - 1, y);picture.get(x - 1, y)
    int lRed = left.getRed();      // the red color of left of the current pixel
    int lGreen = left.getGreen();
    int lBlue = left.getBlue();
    
    Color right = initColors[x + 1][y]; //getPixelColor(x + 1, y); picture.get(x + 1, y)
    int rRed = right.getRed();     // the red color of right of the current pixel
    int rGreen = right.getGreen();
    int rBlue = right.getBlue();
    
    return Math.pow(rRed - lRed, 2) + Math.pow(rGreen - lGreen, 2) + Math.pow(rBlue - lBlue, 2);
  }
  
  private double yGradient(int x, int y) {    
    Color down = initColors[x][y - 1]; // getPixelColor(x, y - 1);  picture.get(x, y - 1)
    int dRed = down.getRed();
    int dGreen = down.getGreen();
    int dBlue = down.getBlue();
    
    Color up = initColors[x][y + 1]; // getPixelColor(x, y + 1);  picture.get(x, y + 1)
    int uRed = up.getRed();
    int uGreen = up.getGreen();
    int uBlue = up.getBlue();
    
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
  
  // energy of pixel at column x and row y of current picture
  public double energy(int x, int y) {
    validateRow(y);
    validateCol(x);
   
    if (x == 0 || (x == width() - 1) || y == 0 || (y == height() - 1)) {    
      return 1000.0;
    }    
    return Math.sqrt(xGradient(x, y) + yGradient(x, y));
  }
  
  /**
   * transpose the pixel value of the current picture
   * 
   * @param width
   * @param height
   */
  private Color[][] transposePixelValue(int width, int height) {
    Color[][] colors = new Color[width][height];
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        colors[col][row] = this.initColors[row][col]; // swap the row and col
      }
    }
    return colors;
  }  
    
  private double[][] transposeEnergy(int width, int height) {
    double[][] energies = new double[width][height];
    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        energies[col][row] = this.initEnergies[row][col];
      }
    }
    return energies;
  }
  
  /**
   * transpose the color and energy array of the picture, 
   * not truly transpose the picture
   * swap the width and the height in calling method
   */
  private void transposePic(int width, int height) {  
    this.width = width;
    this.height = height;
    this.initColors = transposePixelValue(width, height); 
    this.initEnergies = transposeEnergy(width, height); // recalculate and reserve the energy    
  }
  
  //sequence of indices for horizontal seam
  public int[] findHorizontalSeam() {
    int tmpWidth = this.width;
    int tmpHeight = this.height;
    int[] seam = new int[height];
    
    transposePic(tmpHeight, tmpWidth); // swap the width and height
    seam = findVerticalSeam();
    transposePic(tmpWidth, tmpHeight); // transpose back, swap the width and height again
                                                 
    return seam;
  }  
 
  //sequence of indices for vertical seam 
  public int[] findVerticalSeam() {
    int[] seam = new int[height];    
    int[] tmpSeam = new int[height];
    double minEnergy = Double.POSITIVE_INFINITY;
    
    for (int col = 0; col < width; col++) {
      double seamEnergy = 0;
      int column = col;
      for (int row = 0; row < height; row++) {
        double leftDownEnergy = Double.POSITIVE_INFINITY;
        double rightDownEnergy = Double.POSITIVE_INFINITY;
        double downEnergy = Double.POSITIVE_INFINITY; 
        double tempEnergy = seamEnergy;  // preserve previous energy
        
        downEnergy = tempEnergy + initEnergies[column][row];
        if (column > 0) 
          leftDownEnergy = tempEnergy + initEnergies[column-1][row];
        if (column < width - 1)
          rightDownEnergy = tempEnergy + initEnergies[column+1][row];
        
        // 保存最小能量的seam以及当前像素点所在的列  
        seamEnergy = leftDownEnergy;  // leftDownEnergy is the base to compare the other two
        tmpSeam[row] = column - 1;              
        if (seamEnergy > downEnergy) { 
          seamEnergy = downEnergy; 
          tmpSeam[row] = column;
        }
        if (seamEnergy > rightDownEnergy) {
          seamEnergy = rightDownEnergy;
          tmpSeam[row] = column + 1;
        }
        column = tmpSeam[row];  
      
      }
      if (minEnergy > seamEnergy) {
        minEnergy = seamEnergy;
        System.arraycopy(tmpSeam, 0, seam, 0, height);
      }
    }
    return seam;
  }
  
  private void validateVerticalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Vertical seam must not be null");
    }
    
    if (seam.length > height())
      throw new IllegalArgumentException("length of vertical seam must be <= " + height() + ": " + seam.length);
    
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
  
  // remove vertical seam from current picture
  public void removeVerticalSeam(int[] seam) {
    validateVerticalSeam(seam);
    int tmpWidth = this.width;
    int tmpHeight = this.height;
    
    /**
     * remove the seam, copy the array from right to left
     */    
    transposePic(tmpHeight, tmpWidth);
    for (int row = 0; row < seam.length; row++) {
      int srcPos = seam[row] + 1;
      int destPos = seam[row];
      int length = tmpWidth - seam[row] - 1;
      System.arraycopy(initColors[row], srcPos, initColors[row], destPos, length);
      System.arraycopy(initEnergies[row], srcPos, initEnergies[row], destPos, length);
    }    
    transposePic(tmpWidth, tmpHeight);

    /**
     *  recalculate the energy for the pixels along the seam that was just removed
     *  but no other energies will change
     */
    for (int row = 0; row < seam.length; row++) {
      int col = seam[row];
      initEnergies[col][row] = energy(col, row);
    }
    this.width--;
  }
  
  private void validateHorizontalSeam(int[] seam) {
    if (seam == null) {
      throw new IllegalArgumentException("Vertical seam must not be null");
    }
    
    if (seam.length > width())
      throw new IllegalArgumentException("length of vertical seam must be <= " + width() + ": " + seam.length);
    
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
  
  //remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    validateHorizontalSeam(seam);
    for (int col = 0; col < seam.length; col++) {
      int srcPos = seam[col] + 1;
      int destPos = seam[col];
      int length = height - seam[col] - 1;
      System.arraycopy(initColors[col], srcPos, initColors[col], destPos, length);
      System.arraycopy(initEnergies[col], srcPos, initEnergies[col], destPos, length);
    }    
    
    for (int col = 0; col < seam.length; col++) {
      int row = seam[col];
      initEnergies[col][row] = energy(col, row);
    }    
    this.height--;
  }
   
  // unit test
  public static void main(String[] args) {
    Picture picture = new Picture("seam-testing/6x5.png");
    int width  = picture.width();
    int height = picture.height();
    SeamCarver sc = new SeamCarver(picture);
    int[] seam = new int[height];
    seam = sc.findVerticalSeam(); 
    sc.removeVerticalSeam(seam);
    // convert to grayscale
    /* for (int col = 0; col < width; col++) {
        for (int row = 0; row < height; row++) {
            Color color = picture.get(col, row);
            Color gray = Luminance.toGray(color);
            picture.set(col, row, gray);
        }
    }
    picture.show(); */
  }
}