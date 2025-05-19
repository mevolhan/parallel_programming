#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <xmmintrin.h>  // SSE

// Функция с использованием SSE-инструкций
void sse_multiply(float* a, float* b, float* c, int n) {
    for (int i = 0; i < n; i += 4) {
        __m128 va = _mm_loadu_ps(&a[i]);     // загрузить 4 float из a
        __m128 vb = _mm_loadu_ps(&b[i]);     // загрузить 4 float из b
        __m128 vc = _mm_mul_ps(va, vb);      // умножение 4 чисел одновременно
        _mm_storeu_ps(&c[i], vc);            // сохранить результат
    }
}

// Обычное поэлементное умножение
void seq_multiply(float* a, float* b, float* c, int n) {
    for (int i = 0; i < n; i++) {
        c[i] = a[i] * b[i];
    }
}

int main(int argc, char** argv) {
    int n = atoi(argv[1]);  // размер массива

    float *a = (float*) malloc(sizeof(float) * n);
    float *b = (float*) malloc(sizeof(float) * n);
    float *c1 = (float*) malloc(sizeof(float) * n);
    float *c2 = (float*) malloc(sizeof(float) * n);

    // Заполняем массивы значениями
    for (int i = 0; i < n; i++) {
        a[i] = i + 1;
        b[i] = i + 4;
    }

    clock_t start, end;

    // Время SSE
    start = clock();
    sse_multiply(a, b, c1, n);
    end = clock();
    printf("SSE: %.6f seconds\n", (double)(end - start) / CLOCKS_PER_SEC);

    // Время обычного метода
    start = clock();
    seq_multiply(a, b, c2, n);
    end = clock();
    printf("Обычное умножение: %.6f seconds\n", (double)(end - start) / CLOCKS_PER_SEC);

    // Вывод первых 4 элементов результата
    for (int i = 0; i < 4; i++) {
        printf("Result[%d]: %.2f\n", i, c1[i]);
    }

    free(a); free(b); free(c1); free(c2);
    return 0;
}
