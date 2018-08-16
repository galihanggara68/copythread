#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include <windows.h>

// Public vars for sync and signaling
sem_t sem;
int done = 0;

// Struct For Store Filename To void*
typedef struct _fileName{
	char *fileSrc;
	char *fileDest;
}FileName;

// Copy thread function
void *copyThread(void *args){
	FileName *fn;
	fn = (FileName*)args;
	FILE *src = fopen(fn->fileSrc, "rb");
	FILE *dest = fopen(fn->fileDest, "wb");
	int c;
	
	if(src == NULL)
		exit(1);
	printf("Copying ");
	while((c = fgetc(src)) != EOF){
		sem_wait(&sem);
		fputc(c, dest);
		sem_post(&sem);
	}
	done = 1;
	
}

// Progress Dot for display progress when copying file
void *progressDot(void *arg){
	while(!done){
		Sleep(200);
		printf(".");
	}
	printf("Ok");
}

int main(int argc, char *argv[]){
	FileName *fn;
	int i;
	pthread_t copyT[2];
	
	// Allocate memory for new struct
	fn = (FileName*)malloc(sizeof(FileName));
	
	//Assign command line args to file name struct
	fn->fileSrc = argv[1];
	fn->fileDest = argv[2];
	
	// Initialize semaphore
	sem_init(&sem, 0, 1);
	
	// Create Copy Thread
	pthread_create(&copyT[0], NULL, copyThread, (void*)fn);
	// Create Progress Dot Thread
	pthread_create(&copyT[1], NULL, progressDot, NULL);
	
	// Looping for join both threads to main thread
	for(i = 0; i < 2; i++){
		pthread_join(copyT[i], NULL);
	}
	
	// Destroy semaphore
	sem_destroy(&sem);
	
	// Free recent allocated memory
	free(fn);
	return 0;
}
