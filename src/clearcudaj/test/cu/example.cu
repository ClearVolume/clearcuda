
#include "helper_math.h"

extern "C" __global__ void funcname(int length, float *a, float *b, float *c)
{
  const int x = blockIdx.x*blockDim.x + threadIdx.x;
	if (x < length)
	{
		c[x] = a[x] + b[x];
	}
}

  