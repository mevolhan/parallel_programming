#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/time.h>

void heavy_task(int thread_num) {
    for (int i = 0; i < 1e8; i++) {
        sqrt(i);  // тяжелая операция
    }
    printf("\tThread #%d finished\n", thread_num);
}

void sequential(int threads_num) {
    for (int i = 0; i < threads_num; i++) {
        printf("MAIN: starting thread %d\n", i);
        heavy_task(i);
    }
}

int main(int argc, char** argv) {
    int threads_num = atoi(argv[1]);

    struct timeval start, end;
    gettimeofday(&start, NULL);
    sequential(threads_num);
    gettimeofday(&end, NULL);

    double time = (end.tv_sec - start.tv_sec) * 1000.0 +
                  (end.tv_usec - start.tv_usec) / 1000.0;
    printf("Время выполнения на %d потоках (последовательно): %.4f мс\n", threads_num, time);

    return 0;
}
