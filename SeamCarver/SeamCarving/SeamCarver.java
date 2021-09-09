import java.awt.Color;

public class SeamCarver {

    private Picture pic;
    // private double[][] energies;
    private int[][] imageAsArray;
    // private int[][] oldImageAsArray;


    private int height, width;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();

        pic = new Picture(picture);

        height = pic.height();
        width = pic.width();

        // energies = new double[height][width];
        imageAsArray = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                imageAsArray[i][j] = pic.getRGB(j, i);
            }
        }

    }

    // current picture
    public Picture picture() {
        pic = new Picture(width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pic.setRGB(j, i, imageAsArray[i][j]);
            }
        }

        return pic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= imageAsArray[0].length || y < 0 || y >= imageAsArray.length)
            throw new IllegalArgumentException();

        return calculateEnergy(y, x);
    }

    private double calculateEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == height - 1 || y == width - 1) return 1000;

        int yBelow = imageAsArray[x][y - 1];
        int yAbove = imageAsArray[x][y + 1];
        int xLeft = imageAsArray[x - 1][y];
        int xRight = imageAsArray[x + 1][y];

        int rYBelow = (yBelow >> 16) & 0xFF, rYAbove = (yAbove >> 16) & 0xFF;
        int bYBelow = (yBelow) & 0xFF, bYAbove = (yAbove) & 0xFF;
        int gYBelow = (yBelow >> 8) & 0xFF, gYAbove = (yAbove >> 8) & 0xFF;

        int rXLeft = (xLeft >> 16) & 0xFF, rXRight = (xRight >> 16) & 0xFF;
        int bXLeft = (xLeft) & 0xFF, bXRight = (xRight) & 0xFF;
        int gXLeft = (xLeft >> 8) & 0xFF, gXRight = (xRight >> 8) & 0xFF;

        return Math.sqrt(
                Math.pow(rYBelow - rYAbove, 2) +
                        Math.pow(bYBelow - bYAbove, 2) +
                        Math.pow(gYBelow - gYAbove, 2) +
                        Math.pow(rXLeft - rXRight, 2) +
                        Math.pow(bXLeft - bXRight, 2) +
                        Math.pow(gXLeft - gXRight, 2)
        );
    }


    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {

        int[][] edgeTo = new int[imageAsArray.length][imageAsArray[0].length];
        double[][] distanceTo = new double[imageAsArray.length][imageAsArray[0].length];

        int[] horizSeam = new int[imageAsArray[0].length];

        for (int i = 0; i < distanceTo.length; i++) {
            for (int j = 0; j < distanceTo[i].length; j++) {
                if (j == 0) {
                    distanceTo[i][j] = 1000;
                }
                else {
                    distanceTo[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int j = 0; j < imageAsArray[0].length; j++) {

            if (j == 0) continue;

            for (int i = 0; i < imageAsArray.length; i++) {

                if (inBounds(i - 1, j - 1)) {
                    distanceTo[i][j] = distanceTo[i - 1][j - 1] + energy(j, i);
                    edgeTo[i][j] = -1;
                }

                if (inBounds(i, j - 1)) {
                    if (distanceTo[i][j] > distanceTo[i][j - 1] + energy(j, i)) {
                        distanceTo[i][j] = distanceTo[i][j - 1] + energy(j, i);
                        edgeTo[i][j] = 0;
                    }
                }

                if (inBounds(i + 1, j - 1)) {
                    if (distanceTo[i][j] > distanceTo[i + 1][j - 1] + energy(j, i)) {
                        distanceTo[i][j] = distanceTo[i + 1][j - 1] + energy(j, i);
                        edgeTo[i][j] = 1;
                    }
                }

            }

        }

        int smallest = Integer.MAX_VALUE;

        double current = Double.POSITIVE_INFINITY;

        for (int i = 0; i < distanceTo.length; i++) {
            if (distanceTo[i][distanceTo[0].length - 1] < current) {
                smallest = i;
                current = distanceTo[i][distanceTo[0].length - 1];
            }
        }

        for (int i = horizSeam.length - 1; i >= 0; i--) {
            horizSeam[i] = smallest;
            smallest += edgeTo[smallest][i];
        }


        return horizSeam;

    }

    // if row i and col y are in bounds
    private boolean inBounds(int i, int j) {
        return (i >= 0 && j >= 0 && i < imageAsArray.length && j < imageAsArray[0].length);
    }

    // sequence of indices for vertical seam
    // width = imageAsArray[0].length
    // height = imageAsArray.length
    public int[] findVerticalSeam() {
        int[][] edgeTo = new int[imageAsArray.length][imageAsArray[0].length];
        double[][] distanceTo = new double[imageAsArray.length][imageAsArray[0].length];

        int[] vertSeam = new int[imageAsArray.length];

        for (int i = 0; i < distanceTo.length; i++) {
            for (int j = 0; j < distanceTo[i].length; j++) {
                if (i == 0) {
                    distanceTo[i][j] = 1000;
                }
                else {
                    distanceTo[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int i = 0; i < imageAsArray.length; i++) {

            if (i == 0) continue;

            for (int j = 0; j < imageAsArray[i].length; j++) {

                if (inBounds(i - 1, j - 1)) {
                    distanceTo[i][j] = distanceTo[i - 1][j - 1] + energy(j, i);
                    edgeTo[i][j] = -1;
                }

                if (inBounds(i - 1, j)) {
                    if (distanceTo[i][j] > distanceTo[i - 1][j] + energy(j, i)) {
                        distanceTo[i][j] = distanceTo[i - 1][j] + energy(j, i);
                        edgeTo[i][j] = 0;
                    }
                }

                if (inBounds(i - 1, j + 1)) {
                    if (distanceTo[i][j] > distanceTo[i - 1][j + 1] + energy(j, i)) {
                        distanceTo[i][j] = distanceTo[i - 1][j + 1] + energy(j, i);
                        edgeTo[i][j] = 1;
                    }
                }

            }

        }

        int smallest = Integer.MAX_VALUE;

        double current = Double.POSITIVE_INFINITY;

        for (int i = 0; i < distanceTo[0].length; i++) {
            if (distanceTo[distanceTo.length - 1][i] < current) {
                smallest = i;
                current = distanceTo[distanceTo.length - 1][i];
            }
        }

        for (int i = vertSeam.length - 1; i >= 0; i--) {
            vertSeam[i] = smallest;
            smallest += edgeTo[i][smallest];
        }


        return vertSeam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || height <= 1 || seam.length != width)
            throw new IllegalArgumentException();

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height) {
              throw new IllegalArgumentException();
            }

            if (i < seam.length - 1) {
              if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException();
              }
            }
        }

        int[][] oldImageAsArray = imageAsArray;

        imageAsArray = new int[--height][width];
        // energies = new double[height][width];

        boolean skip;

        for (int j = 0; j < oldImageAsArray[0].length; j++) {
            skip = false;
            for (int i = 0; i < oldImageAsArray.length; i++) {
                if (i != seam[j]) {
                    imageAsArray[skip ? i - 1 : i][j] = oldImageAsArray[i][j];
                }
                else {
                    skip = true;
                }
            }
        }


        oldImageAsArray = null;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || width <= 1 || seam.length != height) {
          throw new IllegalArgumentException();
        }

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width) {
              throw new IllegalArgumentException();
            }

            if (i < seam.length - 1 && Math.abs(seam[i] - seam[i + 1]) > 1) {
              throw new IllegalArgumentException();
            }
        }

        int[][] oldImageAsArray = imageAsArray;

        imageAsArray = new int[height][--width];

        boolean skip;

        for (int i = 0; i < oldImageAsArray.length; i++) {
            skip = false;
            for (int j = 0; j < oldImageAsArray[i].length; j++) {
                if (j != seam[i]) {
                    imageAsArray[i][skip ? j - 1 : j] = oldImageAsArray[i][j];
                }
                else {
                    skip = true;
                }
            }
        }


        oldImageAsArray = null;
    }

    public static void main(String[] args) {

      Picture inputImg = new Picture(args[0]);

      SeamCarver sc = new SeamCarver(inputImg);

      int[] seam = { 6, 5, 6, 6, 6, 6, 6, 7, 8, 9, 9, 10 };

      sc.removeVerticalSeam(seam);

      
    }

}