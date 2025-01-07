import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();
        int[][] linesCoords = CalculateLines.calculateLines("input/input1.png", CalculateLines.DEFAULT_IMAGE_SCALEDOWN,
                true);
        for (int i = 0; i < 2; i++) {
            System.out.print((i == 0 ? "horizontal" : "vertical") + "lines: ");
            for (int line : linesCoords[i]) {
                System.out.print(line + " ");
            }
            System.out.println();
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + ((float) (endTime - startTime)) / 1000);
    }
}
