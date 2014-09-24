package clearcudaj;

public class CudaComputeCapability
{

	private int mMajor;
	private int mMinor;

	public CudaComputeCapability(int pMajor, int pMinor)
	{
		mMajor = pMajor;
		mMinor = pMinor;
	}

	@Override
	public String toString()
	{
		return "CudaComputeCapability [mMajor=" + mMajor
						+ ", mMinor="
						+ mMinor
						+ "]";
	}

}
