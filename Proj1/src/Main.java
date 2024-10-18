import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        test();
    }

    private static void test() {
        System.out.println("---------");
        System.out.println("|TESTING|");
        System.out.println("---------\n\n");

        System.out.println("-------------------------------");
        System.out.println("|LINE BY COLUMN MULTIPLICATION|");
        System.out.println("-------------------------------");
        for (int i = 600; i <= 3000; i += 400) {
            System.out.println("\nTESTING FOR MATRIX SIZE OF " + i + "x" + i);
            System.out.println("------------------------------------");
            matrixMult(i);
        }

        System.out.println("\n\n-----------------------------");
        System.out.println("|LINE BY LINE MULTIPLICATION|");
        System.out.println("-----------------------------");
        for (int i = 600; i <= 3000; i += 400) {
            System.out.println("\nTESTING FOR MATRIX SIZE OF " + i + "x" + i);
            System.out.println("------------------------------------");
            lineMatrixMult(i);
        }
    }

    private static void matrixMult(int n) {
        double[] a = new double[n * n];
        Arrays.fill(a, 1.0);
        double[] b = new double[n * n];
        double[] c = new double[n * n];

        IntStream.range(0, n).forEach(i -> IntStream.range(0, n).forEach(j -> b[i * n + j] = i + 1));

        long start = System.nanoTime();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double temp = 0.0;
                for (int k = 0; k < n; k++) {
                    temp += a[i * n + k] * b[k * n + j];
                }
                c[i * n + j] = temp;
            }
        }

        double duration = (System.nanoTime() - start) / 1e9;

        System.out.println("Time: " + duration + " seconds");

        System.out.println("Result matrix: ");
        IntStream.range(0, Math.min(10, n)).forEach(j -> System.out.print(c[j] + " "));
        System.out.println();
    }

    private static void lineMatrixMult(int n) {
        double[] a = new double[n * n];
        Arrays.fill(a, 1.0);
        double[] b = new double[n * n];
        double[] c = new double[n * n];

        IntStream.range(0, n).forEach(i -> IntStream.range(0, n).forEach(j -> b[i * n + j] = i + 1));

        long start = System.nanoTime();

        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                for (int j = 0; j < n; j++) {
                    c[i * n + j] += a[i * n + k] * b[k * n + j];
                }
            }
        }

        double duration = (System.nanoTime() - start) / 1e9;

        System.out.println("Time: " + duration + " seconds");

        System.out.println("Result matrix: ");
        IntStream.range(0, Math.min(10, n)).forEach(j -> System.out.print(c[j] + " "));
        System.out.println();
    }
}
