import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class ThreadedStrassen {

    private static class StrassenRecursiveTask extends RecursiveTask<int[][]> {
        private final int[][] A;
        private final int[][] B;
        private final int startRowA, startColA, startRowB, startColB;
        private final int size;

        public StrassenRecursiveTask(int[][] A, int[][] B, int startRowA, int startColA, int startRowB, int startColB, int size) {
            this.A = A;
            this.B = B;
            this.startRowA = startRowA;
            this.startColA = startColA;
            this.startRowB = startRowB;
            this.startColB = startColB;
            this.size = size;
        }

        @Override
        protected int[][] compute() {
            int[][] result;
            if (size <= 128) {
                result = regularMultiplication(A, B, startRowA, startColA, startRowB, startColB, size);
            } else {
                int halfSize = size / 2;
                StrassenRecursiveTask[] tasks = new StrassenRecursiveTask[7];
                tasks[0] = new StrassenRecursiveTask(A, B, startRowA, startColA, startRowB, startColB, halfSize); // P1
                tasks[1] = new StrassenRecursiveTask(A, B, startRowA, startColA + halfSize, startRowB + halfSize, startColB, halfSize); // P2
                tasks[2] = new StrassenRecursiveTask(A, B, startRowA, startColA, startRowB, startColB + halfSize, halfSize); // P3
                tasks[3] = new StrassenRecursiveTask(A, B, startRowA, startColA + halfSize, startRowB + halfSize, startColB + halfSize, halfSize); // P4
                tasks[4] = new StrassenRecursiveTask(A, B, startRowA + halfSize, startColA, startRowB, startColB, halfSize); // P5
                tasks[5] = new StrassenRecursiveTask(A, B, startRowA + halfSize, startColA + halfSize, startRowB + halfSize, startColB, halfSize); // P6
                tasks[6] = new StrassenRecursiveTask(A, B, startRowA + halfSize, startColA, startRowB, startColB + halfSize, halfSize); // P7
                for (StrassenRecursiveTask task : tasks) {
                    task.fork();
                }
                int[][] C11 = tasks[0].join();
                int[][] C12 = addMatrices(tasks[1].join(), tasks[4].join());
                int[][] C21 = addMatrices(tasks[2].join(), tasks[3].join());
                int[][] C22 = addMatrices(subMatrices(tasks[0].join(), tasks[1].join()), addMatrices(tasks[2].join(), tasks[3].join()));
                result = new int[size][size];
                joinMatrices(result, C11, 0, 0);
                joinMatrices(result, C12, 0, halfSize);
                joinMatrices(result, C21, halfSize, 0);
                joinMatrices(result, C22, halfSize, halfSize);
            }
            return result;
        }
    }

    public static int[][] strassenMatrixMultiply(int[][] A, int[][] B) {
        int n = A.length;
        StrassenRecursiveTask task = new StrassenRecursiveTask(A, B, 0, 0, 0, 0, n);
        return ForkJoinPool.commonPool().invoke(task);
    }

    public static int[][] regularMultiplication(int[][] A, int[][] B, int startRowA, int startColA, int startRowB, int startColB, int size) {
        int[][] C = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    C[i][j] += A[startRowA + i][startColA + k] * B[startRowB + k][startColB + j];
                }
            }
        }
        return C;
    }

    // Add two matrices
    public static int[][] addMatrices(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    // Subtract one matrix from another
    public static int[][] subMatrices(int[][] A, int[][] B) {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }


    public static void joinMatrices(int[][] parent, int[][] child, int startRow, int startCol) {
        for (int i = 0; i < child.length; i++) {
            for (int j = 0; j < child.length; j++) {
                parent[i + startRow][j + startCol] = child[i][j];
            }
        }
    }

    private static int[][] generateRandomMatrix(int size) {
        int[][] matrix = new int[size][size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(50);
            }
        }
        return matrix;
    }

    // Display a matrix
    private static void displayMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }



    public static void main(String[] args) throws IOException{

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please input the matrix size-> \n");
        int size = Integer.parseInt(br.readLine());

        int[][] A = generateRandomMatrix(size);
        int[][] B = generateRandomMatrix(size);


        long startTime = System.currentTimeMillis();

        int[][] result = strassenMatrixMultiply(A, B);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Execution Time: " + executionTime);
    }
}



