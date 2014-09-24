package clearcudaj;

import jcuda.CudaException;

public interface CudaCloseable extends AutoCloseable
{
	@Override
	public void close() throws CudaException;
}
