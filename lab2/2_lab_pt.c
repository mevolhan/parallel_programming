#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include <sys/time.h>

void* heavy_task(void* arg) {
    for (int i = 0; i < 1e8; i++) {
        sqrt(i);  // тяжелая операция
    }
    int num = *((int*) arg);
    printf("\tThread #%d finished\n", num);
    free(arg);
    return NULL;
}

void pthreads(int threads_num) {
    pthread_t threads[threads_num];

    for (int i = 0; i < threads_num; i++) {
        printf("MAIN: starting thread %d\n", i);
        int* thread_num = malloc(sizeof(int));
        *thread_num = i;
        pthread_create(&threads[i], NULL, heavy_task, thread_num);
    }

    for (int i = 0; i < threads_num; i++) {
        pthread_join(threads[i], NULL);  // ожидание завершения всех потоков
    }
}

int main(int argc, char** argv) {
    int threads_num = atoi(argv[1]);

    struct timeval start, end;
    gettimeofday(&start, NULL);
    pthreads(threads_num);
    gettimeofday(&end, NULL);

    double time = (end.tv_sec - start.tv_sec) * 1000.0 +
                  (end.tv_usec - start.tv_usec) / 1000.0;
    printf("Время выполнения на %d потоках (pthreads): %.4f мс\n", threads_num, time);

    return 0;
}
