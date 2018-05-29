import static org.junit.Assert.*;
import edu.princeton.cs.algs4.Picture;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SeamCarverTest {
  private SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  @Test
  /*
   * 在energy()方法中测试当x < 0 时抛出IllegalArgumentException
   * width = 6，x的取值范围： 0 <= x <= 6 
   */
  public void testIllegalArgumentExceptionForXisSmallThanZero() {
    thrown.expect(IllegalArgumentException.class);
    // thrown.expectMessage("x or col must be between 0 and WIDTH-1");
    seamcarver.energy(-1, 3); // x < 0
  }
  
  @Test
  /*
   * 在energy()方法中测试当x < 0 时抛出IllegalArgumentException
   * width = 6，x的取值范围： 0 <= x <= 6
   */
  public void testIllegalArgumentExceptionForXisLargerThanWidthMinusOne() {
    //SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
    thrown.expect(IllegalArgumentException.class);
    // thrown.expectMessage("x or col must be between 0 and WIDTH-1");
    seamcarver.energy(7, 2); // x > width - 1
  }
  
  @Test
  /*
   * 在energy()方法中测试当 < 0 时抛出IllegalArgumentException
   * height = 5，y的取值范围： 0 <= y <= 4 
   */
  public void testIllegalArgumentExceptionForYisSmallThanZero() {
    thrown.expect(IllegalArgumentException.class);
    // thrown.expectMessage("y or row must be between 0 and HEIGHT-1");
    seamcarver.energy(1, -1); // y < 0
  }
  
  @Test
  /*
   * 在energy()方法中测试当y < 0 时抛出IllegalArgumentException
   * height = 5，y的取值范围： 0 <= y <= 4
   */
  public void testIllegalArgumentExceptionForYisLargerThanHeightMinusOne() {
    //SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
    thrown.expect(IllegalArgumentException.class);
    // thrown.expectMessage("y or row must be between 0 and HEIGHT-1");
    seamcarver.energy(1, 6); // y > height - 1
  }  
  
  @Test
  /*
   * 测试当x、y取正常范围内的值 
   * x的取值范围： 0 <= x <= 6
   * y的取值范围： 0 <= y <= 5
   */
  public void testEnergy() {
    //SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
    // consider a 6-by-5 image 
    assertEquals(237.35, seamcarver.energy(1, 1), 0.01); // 
    assertEquals(138.69, seamcarver.energy(1, 2), 0.01); // 
    
    assertEquals(1000.0, seamcarver.energy(0, 2), 0.01); // x=0
    assertEquals(1000.0, seamcarver.energy(5, 2), 0.01); // x=width-1
    
    assertEquals(1000.0, seamcarver.energy(1, 0), 0.01); // y=0
    assertEquals(1000.0, seamcarver.energy(1, 4), 0.01); // y=height-1       
  }
  
 
  
  @Test
  public void testfindVerticalSeam() {
    SeamCarver sc = new SeamCarver(new Picture("seam-testing/6x5.png"));
    int[] seam = new int[sc.height()];
    seam = sc.findVerticalSeam();
    double energy = 0.0;
    for (int i = 0; i < seam.length; i++) {
      energy = energy + sc.energy(seam[i], i); 
    }
    assertEquals(2414.97, energy, 0.01);
    
    // check image 7x10.png
    sc = new SeamCarver(new Picture("seam-testing/7x10.png"));
    seam = new int[sc.height()];
    seam = sc.findVerticalSeam();
    energy = 0.0;
    for (int i = 0; i < seam.length; i++) {
      energy = energy + sc.energy(seam[i], i); 
    }
    assertEquals(3443.20, energy, 0.01);
    
    assertEquals(2, seam[0]);
    assertEquals(3, seam[1]);
    assertEquals(4, seam[2]);
    assertEquals(3, seam[3]);
    assertEquals(4, seam[4]);
    assertEquals(3, seam[5]);
    assertEquals(3, seam[6]);
    assertEquals(2, seam[7]);
    assertEquals(2, seam[8]);
    assertEquals(1, seam[9]);
  }
  
   
  @Test
  public void testfindHorizontalSeam() {
    //SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
    int[] seam = new int[seamcarver.height()];
    seam = seamcarver.findHorizontalSeam();
   
    assertEquals(1, seam[0]);
    assertEquals(2, seam[1]);
    assertEquals(1, seam[2]);
    assertEquals(2, seam[3]);
    assertEquals(1, seam[4]);
    assertEquals(0, seam[5]);
  }
  
  @Test
  /**
   * 验证转置回初始状态的energies（二维数组）
   * 在findHorizontalSeam()中查找水平方向的seam， 需要转置energies后用调用findVerticalSeam()查找，
   * 在查找到后，需要再将energies转置回到初始状态， 此时需要验证是否正确的转置回到初始状态；
   * 调用findVerticalSeam()进行验证， 若转置回后的width，height以及seam[]的与初始状态时的保持一致
   * 则表示转置回初始状态是正确的
   */
  public void testPicTransposeBack () {
    //SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
    int[] seam = new int[seamcarver.height()];
    seam = seamcarver.findHorizontalSeam(); 
    
    /* 验证转置回到的初始状态 */
    seam = seamcarver.findVerticalSeam();   
    assertEquals(6, seamcarver.width());
    assertEquals(5, seamcarver.height());   
    assertEquals(3, seam[0]);
    assertEquals(4, seam[1]);
    assertEquals(3, seam[2]);
    assertEquals(2, seam[3]);
    assertEquals(1, seam[4]);
  }
  
  
  @Test
  /*
   * test to throw IllegalArgumentException
   * if vertical seam is null
   */
  public void testIllegalArgumentExceptionForSeamIsNull() {
    //SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
    thrown.expect(IllegalArgumentException.class);
    // thrown.expectMessage("y or row must be between 0 and HEIGHT-1");
    seamcarver.removeVerticalSeam(null); // seam equals null
  }
  
  @Test
  /*
   * test to throw IllegalArgumentException 
   * if the length of vertical seam is valid(no more than height)
   *  
   */
  public void testIllegalArgumentExceptionForSeamLenIsValid() {
    //SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
    thrown.expect(IllegalArgumentException.class);
    // thrown.expectMessage("y or row must be between 0 and HEIGHT-1");
    int[] seam = {3,4,3,2,2,3}; // the length of seam is 6, more than height, 
                                // so should throw IllegalArgumentException()
    seamcarver.removeVerticalSeam(seam);
  }
  
  @Test
  /*
   * test to throw IllegalArgumentException 
   * if two adjacent entries differ by more than 1
   *  
   */
  public void testTwoAdjacentEntriesDifferMoreThanOne() {
    //SeamCarver seamcarver = new SeamCarver(new Picture("seam-testing/6x5.png"));
    thrown.expect(IllegalArgumentException.class);
    // thrown.expectMessage("y or row must be between 0 and HEIGHT-1");
    int[] seam = {3,4,3,2,4}; // the last two adjacent entries differ by 2(more than 1)
    seamcarver.removeVerticalSeam(seam);
  }
  
  @Test
  /*
   * 测试当x、y取正常范围内的值 
   * x的取值范围： 0 <= x <= 5
   * y的取值范围： 0 <= y <= 5
   */
  public void testRemoveVerticalSeam() {   
    int[] seam = {3,4,3,2,1};
    seamcarver.removeVerticalSeam(seam);     
    Picture picture = seamcarver.picture(); // create new picture
    assertEquals(5, picture.width());
    assertEquals(112, picture.get(3, 1).getRed());
    assertEquals(156, picture.get(3, 1).getGreen());
    assertEquals(180, picture.get(3, 1).getBlue());
    
    SeamCarver sc = new SeamCarver(picture);
    assertEquals(1000.0, sc.energy(1, 4), 0.01);
    assertEquals(103.24, sc.energy(2, 3), 0.01);
    assertEquals(145.15, sc.energy(3, 2), 0.01);
    assertEquals(1000.0, sc.energy(4, 1), 0.01);
    
    assertEquals(151.28, sc.energy(3, 1), 0.01);
    assertEquals(224.49, sc.energy(2, 2), 0.01);
    assertEquals(185.83, sc.energy(1, 3), 0.01);   
   
  }
  
  @Test
  /*
   * 测试当x、y取正常范围内的值 
   * x的取值范围： 0 <= x <= 6
   * y的取值范围： 0 <= y <= 4
   */
  public void testRemoveHorizontalSeam() {   
    int[] seam = {1,2,1,2,1,0};
    seamcarver.removeHorizontalSeam(seam);     
    Picture picture = seamcarver.picture(); // create new picture
    assertEquals(4, picture.height());
    assertEquals(112, picture.get(3, 1).getRed());
    assertEquals(156, picture.get(3, 1).getGreen());
    assertEquals(180, picture.get(3, 1).getBlue());
    
    SeamCarver sc = new SeamCarver(picture);
    assertEquals(1000.0, sc.energy(1, 3), 0.01);
    assertEquals(146.57, sc.energy(1, 1), 0.01);
    assertEquals(167.82, sc.energy(3, 1), 0.01);
    assertEquals(1000.0, sc.energy(5, 3), 0.01);
    
    
  }
}
