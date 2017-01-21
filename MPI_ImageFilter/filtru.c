#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct Filter{
	int ponds[3][3];
	int total;
	int offset;
}Filter;

int main(int argc, char **argv) {

	MPI_Init(&argc, &argv);

	int rank, nProcesses;
	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &nProcesses);

	MPI_Status stat;

	int i, j;

/*--------------------------------------------------------------------------------
------------------------------DEFINIRE FILTRE-------------------------------------
--------------------------------------------------------------------------------*/

	struct Filter filters[4];
	int toUse;
	
	//SMOOTH
	for(i = 0; i < 3; i++)
		for(j = 0; j < 3; j++)
			filters[0].ponds[i][j] = 1;
	filters[0].total = 9;
	filters[0].offset = 0;

	//BLUR
	for(i = 0; i < 3; i++)
		for(j = 0; j < 3; j++)
			if((i + 1) % 2 && (j + 1) % 2)
				filters[1].ponds[i][j] = 1;
			else if(i + j == 1 || i + j == 3)
				filters[1].ponds[i][j] = 2;
			else
				filters[1].ponds[i][j] = 4;
	filters[1].total = 16;
	filters[1].offset = 0;

	//SHARPEN
	for(i = 0; i < 3; i++)
		for(j = 0; j < 3; j++)
			if((i + 1) % 2 && (j + 1) % 2)
				filters[2].ponds[i][j] = 0;
			else if(i + j == 1 || i + j == 3)
				filters[2].ponds[i][j] = -2;
			else
				filters[2].ponds[i][j] = 11;
	filters[2].total = 3;
	filters[2].offset = 0;

	//MEAN REMOVAL
	for(i = 0; i < 3; i++)
		for(j = 0; j < 3; j++)
			if(i != 1 || j != 1)
				filters[3].ponds[i][j] = -1;
			else
				filters[3].ponds[i][j] = 9;
	filters[3].total = 1;
	filters[3].offset = 0;

/*--------------------------------------------------------------------------------
---------------------------STABILIREA TOPOLOGIEI----------------------------------
--------------------------------------------------------------------------------*/

	int N, parent;
	int neighbours[nProcesses];

	int value;

	int SONDAJ = 0;

	//ALGORITM UNDA
	if(rank != 0) {
		MPI_Recv(&value, 1, MPI_INT, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, &stat);
		parent = stat.MPI_SOURCE;
	}

	//CITIRE VECINI
	FILE* topologie = fopen(argv[1], "r");
	char lineRank[10], lineCopy[1000], *lr;
	char lineOrig[1000];
	fgets(lineOrig, 100, topologie);
	strcpy(lineCopy, lineOrig);
	strcpy(lineRank, strtok(lineCopy, ":"));
	while(atoi(lineRank) != rank) {
		fgets(lineOrig, 100, topologie);
		strcpy(lineCopy, lineOrig);
		strcpy(lineRank, strtok(lineCopy, ":"));
	}
	fclose(topologie);

	char line[1000];
	strcpy(line, lineOrig);

	char toAdd[1000];
	char *p = strtok(line, " \n\r");
	p = strtok(NULL, " \n\r");

	while(p != NULL) {
		strcpy(toAdd, p);

		if(atoi(toAdd) < nProcesses)
			neighbours[N++] = atoi(toAdd);

		p = strtok(NULL, " \t\n\r\0");
	}

	//VERIFICARE IN CAZ DE CICLURI DACA TOPOLOGIA ESTE UN GRAF
	value = 0;
	for(i = 0; i < N; i++)
		if(neighbours[i] != parent) {
			MPI_Send(&SONDAJ, 1, MPI_INT, neighbours[i], 0, MPI_COMM_WORLD);
		}

	for(i = 0; i < N; i++) {
		if(neighbours[i] != parent) {
			MPI_Recv(&value, 1, MPI_INT, neighbours[i], 0, MPI_COMM_WORLD, &stat);
			if(value == 0) {
				value = 2;
				MPI_Send(&value, 1, MPI_INT, neighbours[i], 0, MPI_COMM_WORLD);
				neighbours[i] = -1;
			}
			else if(value == 2) {
				neighbours[i] = -1;
			}
		}
	}

	//ECOU
	int ECOU = 1;
	if(rank)
		MPI_Send(&ECOU, 1, MPI_INT, parent, 0, MPI_COMM_WORLD);

	int div = 0;
	int receivers[nProcesses];
	for(i = 0; i < N; i++)
		if(neighbours[i] != -1 && neighbours[i] != parent)
			receivers[div++] = neighbours[i];

	int processed[nProcesses];
	for(i = 0; i < nProcesses; i++)
		processed[i] = 0;

/*--------------------------------------------------------------------------------
--------------------------PARCURGEREA IMAGINILOR----------------------------------
--------------------------------------------------------------------------------*/

	int **image, **copy;

	int row;

	//IMAGE PROCESSING
	if(rank == 0) {
		char l[300];
		FILE* imagini = fopen(argv[2], "r");
		int imageNo;
		fgets(l, 300, imagini);
		imageNo = atoi(l);

		int img;

		char *filter, *name, *new;

		//IMAGE READING
		for(img = 0; img < imageNo; img++) {

			fgets(l, 300, imagini);
			filter = strtok(l, " \t\n\r");
			name = strtok(NULL, " \t\n\r");
			new = strtok(NULL, " \t\n\r");

			char read[100];

			FILE* pgm = fopen(name, "r");

			fgets(read, 100, pgm);

			char type[100];
			strcpy(type, read);

			if(!strncmp(filter, "smooth", 5))
				toUse = 0;
			if(!strncmp(filter, "blur", 4))
				toUse = 1;
			if(!strncmp(filter, "sharpen", 7))
				toUse = 2;
			if(!strncmp(filter, "mean", 4))
				toUse = 3;

			char comment[100];
			fgets(read, 100, pgm);

			if(!strncmp(read, "#", 1)) {
				strcpy(comment, read);
				fgets(read, 100, pgm);
			}

			char cpy[100];

			int width, height;
			strcpy(cpy, strtok(read, " \t\n\r"));
			width = atoi(cpy);
			strcpy(cpy, strtok(NULL, " \t\n\r"));
			height = atoi(cpy);

			// MEMORY ALLOCATION
			image = calloc(height, sizeof(int*));
			copy = calloc(height, sizeof(int*));

			for(i = 0; i < height; i++) {
				image[i] = calloc(width, sizeof(int));
				copy[i] = calloc(width, sizeof(int));
			}

			int *zeros = calloc(width, sizeof(int));
			for(i = 0; i < width; i++)
				zeros[i] = 0;

			fgets(read, 100, pgm);
			int max_value = atoi(read);

			//READ ACTUAL IMAGE FROM FILE
			for(i = 0; i < height; i++)
				for(j = 0; j < width; j++) {
					fgets(read, 100, pgm);
					image[i][j] = atoi(read);
				}

			fclose(pgm);

			if(div) {
				int newHeight = height / div;

				// SEND SECTIONS OF THE IMAGE TO CHILDREN
				for(i = 0; i < div; i++) {
					if(i == div - 1)
						newHeight += height - div * (height / div);

					// SEND FILTER
					MPI_Send(&toUse, 1, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);

					// SEND DIMENSIONS OF SECTION			
					MPI_Send(&newHeight, 1, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);
					MPI_Send(&width, 1, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);

					// SEND LINES
					int section = i * (height / div);
					for(row = section - 1; row <= section + newHeight; row++)
						if(row < 0 || row >= height)
							MPI_Send(zeros, width, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);
						else
							MPI_Send(image[row], width, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);
				}
				
				newHeight = height / div;	

				for(i = 0; i < div; i++) {
					if(i == div - 1)
						newHeight += height - div * (height / div);

					int section = i * (height / div);
					// RECEIVE PROCESSED SECTION
					for(row = section; row < section + newHeight; row++)
						MPI_Recv(copy[row], width, MPI_INT, receivers[i], 0, MPI_COMM_WORLD, &stat);
				}
			}
			else {
				//IF LEAF PROCESS SECTION
				int adjI, adjJ;
				for(i = 0; i < height; i++) {
					for(j = 0; j < width; j++) {
						copy[i][j] = 0;
						for(adjI = -1; adjI < 2; adjI++)
							for(adjJ = -1; adjJ < 2; adjJ++)
								if(i + adjI >= 0 && i + adjI < height && j + adjJ >= 0 && j + adjJ < width)
									copy[i][j] += filters[toUse].ponds[adjI + 1][adjJ + 1] * image[i + adjI][j + adjJ];
						copy[i][j] /= filters[toUse].total;

						if(copy[i][j] < 0)
							copy[i][j] = 0;

						if(copy[i][j] > 255)
							copy[i][j] = 255;
					}
					// COUNT PROCESSED LINES
					processed[rank]++;
				}
			}

			//PRINT RESULTING IMAGE TO FILE
			FILE* rez = fopen(new, "w");
			fprintf(rez, "%s", type);
			fprintf(rez, "%s", comment);
			fprintf(rez, "%d %d\n", width, height);
			fprintf(rez, "%d\n", max_value);

			for(i = 0; i < height; i++)
				for(j = 0; j < width; j++) {
					fprintf(rez, "%d\n", copy[i][j]);
				}

			fclose(rez);

			// MEMORY FREEING
			for(i = 0; i < height; i++) {
				free(image[i]);
				free(copy[i]);
			}
			free(image);
			free(copy);
		}

/*--------------------------------------------------------------------------------
--------------------------TERMINARE SI STATISTICA---------------------------------
--------------------------------------------------------------------------------*/

		int STOP = -1;

		int aux[20];

		for(i = 0; i < div; i++) {
			MPI_Send(&STOP, 1, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);
			MPI_Recv(aux, nProcesses, MPI_INT, receivers[i], 0, MPI_COMM_WORLD, &stat);
			for(j = 0; j < nProcesses; j++)
				processed[j] += aux[j];
		}

		FILE* statistica = fopen(argv[3], "w");
		for(i = 0; i < nProcesses; i++)
			if(i < nProcesses - 1)
				fprintf(statistica, "%d: %d\n", i, processed[i]);
			else
				fprintf(statistica, "%d: %d", i, processed[i]);
		fclose(statistica);

	}
	else {

/*--------------------------------------------------------------------------------
------------------------------ALGORITM NODURI-------------------------------------
--------------------------------------------------------------------------------*/

		int height, width;
		int active = 1;
		while(active) {
			// RECEIVE FILTER TO USE
			MPI_Recv(&toUse, 1, MPI_INT, parent, 0, MPI_COMM_WORLD, &stat);

			// SEND FILTER FURTHER
			for(i = 0; i < div; i++)
				MPI_Send(&toUse, 1, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);

			if(toUse > -1) {
				// RECEIVE DIMENSIONS FOR SECTION
				MPI_Recv(&height, 1, MPI_INT, parent, 0, MPI_COMM_WORLD, &stat);
				MPI_Recv(&width, 1, MPI_INT, parent, 0, MPI_COMM_WORLD, &stat);

				// MEMORY ALLOCATION
				image = calloc(height + 2, sizeof(int*));
				copy = calloc(height + 2, sizeof(int*));

				for(i = 0; i < height + 2; i++) {
					image[i] = calloc(width, sizeof(int));
					copy[i] = calloc(width, sizeof(int));
				}

				// RECEIVE SECTION
				for(row = 0; row <= height + 1; row++)
					MPI_Recv(image[row], width, MPI_INT, parent, 0, MPI_COMM_WORLD, &stat);

				if(div) {
					int newHeight = height / div;

					// SEND SMALLER SECTIONS FURTHER IF NOT LEAF
					for(i = 0; i < div; i++) {
						if(i == div - 1)
							newHeight += height - div * (height / div);
						
						//MPI_Send(&sendStart, 1, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);
						MPI_Send(&newHeight, 1, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);
						MPI_Send(&width, 1, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);

						int section = 1 + i * (height / div);
						for(row = section - 1; row <= section + newHeight; row++)
							MPI_Send(image[row], width, MPI_INT, receivers[i], 0, MPI_COMM_WORLD);
					}

					newHeight = height / div;

					for(i = 0; i < div; i++) {
						if(i == div - 1)
							newHeight += height - div * (height / div);

						int section = i * (height / div);
						for(row = section; row < section + newHeight; row++)
							MPI_Recv(copy[row], width, MPI_INT, receivers[i], 0, MPI_COMM_WORLD, &stat);
					}
				}
				else {
					//IF LEAF PROCESS SECTION
					int adjI, adjJ;
					for(i = 0; i < height; i++) {
						for(j = 0; j < width; j++) {
							copy[i][j] = 0;
							for(adjI = -1; adjI < 2; adjI++)
								for(adjJ = -1; adjJ < 2; adjJ++)
									if(j + adjJ >= 0 && j + adjJ < width)
										copy[i][j] += filters[toUse].ponds[adjI + 1][adjJ + 1] * image[i + 1 + adjI][j + adjJ];
							copy[i][j] /= filters[toUse].total;

							if(copy[i][j] < 0)
								copy[i][j] = 0;

							if(copy[i][j] > 255)
								copy[i][j] = 255;
						}
						// COUNT PROCESSED LINES
						processed[rank]++;
					}
				}

				// SEND PROCESSED SECTION BACK TO PARENT
				for(row = 0; row < height; row++)
					MPI_Send(copy[row], width, MPI_INT, parent, 0, MPI_COMM_WORLD);

				// MEMORY FREEING
				for(i = 0; i < height + 2; i++) {
					free(image[i]);
					free(copy[i]);
				}
				free(image);
				free(copy);

			}
			else {
				int aux[20];

				// RECEIVE STATISTICS
				for(i = 0; i < div; i++) {
					MPI_Recv(aux, nProcesses, MPI_INT, receivers[i], 0, MPI_COMM_WORLD, &stat);
					for(j = 0; j < nProcesses; j++)
						processed[j] += aux[j];
				}
				
				// SEND STATISTICS
				MPI_Send(processed, nProcesses, MPI_INT, parent, 0, MPI_COMM_WORLD);
				active = 0;
			}

		}

	}

	MPI_Finalize();
	return 0;
}