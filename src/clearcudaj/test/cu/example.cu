#define N (1024*1024)
#define M (1000000)

#include "helper_math.h"

extern "C" __global__ void funcname(float *a, float *b, float *c)
{
	int id = blockIdx.x;
	if (id < N)
	{
		c[id] = a[id] + b[id];
	}
}

  