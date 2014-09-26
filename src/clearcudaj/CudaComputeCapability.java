package clearcudaj;

public class CudaComputeCapability
{

	private final int mMajor;
	private final int mMinor;

	public CudaComputeCapability(int pMajor, int pMinor)
	{
		mMajor = pMajor;
		mMinor = pMinor;
	}

	@Override
	public String toString()
	{
		return "CudaComputeCapability [mMajor=" + getMajor()
						+ ", mMinor="
						+ getMinor()
						+ "]";
	}

	public int getMajor()
	{
		return mMajor;
	}

	public int getMinor()
	{
		return mMinor;
	}

}
