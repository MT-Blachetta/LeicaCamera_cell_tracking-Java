

/**
 * 
 * 
 * @author Thomas Temme
 */


//TestTestTest
public class CostMatrixIndex {
	
	private int i;
	private int j;
	private int offset;
	private EdgeType type;
	
	CostMatrixIndex(int iindex, int jindex, int koffset, EdgeType edgeType)
	{
		i=iindex;
		j=jindex;
		offset=koffset;
		type=edgeType;
	}
	
	
	public EdgeType getType() {
		return type;
	}
	public void setType(EdgeType type) {
		this.type = type;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public int getJ() {
		return j;
	}
	public void setJ(int j) {
		this.j = j;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
}
