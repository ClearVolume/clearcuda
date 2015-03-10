package clearcuda;

import coremem.MemoryBase;
import coremem.ContiguousMemoryInterface;
import coremem.interfaces.MemoryType;
import coremem.rgc.Cleaner;

public class CudaMemoryRegion<T> extends MemoryBase<T> implements
																														ContiguousMemoryInterface<T>
{

	private CudaHostPointer mCudaHostPointer;

	public CudaMemoryRegion(CudaHostPointer pCudaHostPointer)
	{
		super();
		mCudaHostPointer = pCudaHostPointer;
	}


	@Override
	public Cleaner getCleaner()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContiguousMemoryInterface<T> subRegion(long pOffset,
																				long pLenghInBytes)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemoryType getMemoryType()
	{
		return MemoryType.CPURAMGPUMAPPED;
	}


}
