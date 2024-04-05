#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

// Function to generate a random matrix of size rows x cols
void generateRandomMatrix(int rows, int cols, int **matrix) {
    #pragma omp parallel for collapse(2)
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            matrix[i][j] = rand() % 100; // Generate random numbers between 0 and 99
        }
    }
}

// Function to print a matrix of size rows x cols
void printMatrix(int rows, int cols, int **matrix) {
    printf("Matrix:\n");
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            printf("%d ", matrix[i][j]);
        }
        printf("\n");
    }
}

// Function to perform matrix multiplication
double matrixMultiply(int rows, int cols, int **matrix1, int **matrix2, 
                    int **result) {
                            
    double start_time = omp_get_wtime();
    #pragma omp parallel for collapse(2)
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            result[i][j] = 0; // Initialize result cell to 0
            for (int k = 0; k < cols; k++) {
                result[i][j] += matrix1[i][k] * matrix2[k][j]; // Perform multiplication
            }
        }
    }
    double end_time = omp_get_wtime(); // End timing
    return (end_time - start_time) * 1000.0;
}

int main() {
    int rows = 2048, cols = 2048;
    
    // // Input matrix dimensions
    printf("Enter dimensions of the first matrix (rows cols): ");
    scanf("%d %d", &rows, &cols);
    // printf("Enter dimensions of the second matrix (rows cols): ");
    // scanf("%d %d", &rows2, &cols2);
    
    
    // Check if dimensions are within the limit
    if (rows > 4096 || cols > 4096) {
        printf("Error: Matrix dimensions exceed the limit of 4096x4096.\n");
        return 1;
    }

    // Allocate memory for matrices
    int **matrix1 = (int **)malloc(rows * sizeof(int *));
    int **matrix2 = (int **)malloc(rows * sizeof(int *));
    int **result = (int **)malloc(rows * sizeof(int *));
    for (int i = 0; i < rows; i++) {
        matrix1[i] = (int *)malloc(cols * sizeof(int));
        result[i] = (int *)malloc(cols * sizeof(int));
    }
    for (int i = 0; i < rows; i++) {
        matrix2[i] = (int *)malloc(cols * sizeof(int));
    }

    // Generate random matrices
    generateRandomMatrix(rows, cols, matrix1);
    generateRandomMatrix(rows, cols, matrix2);


    // Perform matrix multiplication
    double exec_time = matrixMultiply(rows, cols, matrix1, matrix2, result);
    printf("Execution Time: %.2f milliseconds\n", exec_time);


    // Free allocated memory
    for (int i = 0; i < rows; i++) {
        free(matrix1[i]);
        free(result[i]);
    }
    for (int i = 0; i < rows; i++) {
        free(matrix2[i]);
    }
    free(matrix1);
    free(matrix2);
    free(result);

    return 0;
}