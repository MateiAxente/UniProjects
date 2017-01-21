build:	filtru.c
	mpicc filtru.c -o filtru

run:
	mpirun -np 12 ./filtru topologie.in imagini.in statistica.out
clean:
	rm -f filtru