package jp.yom.yglib.vector;




/***********************************************
 * 
 * 
 * 座標を表すクラス
 * 
 * @author matsumoto
 *
 */
public class FPoint {
	
	public float	x,y,z;
	
	
	public FPoint() {
		x = y = z = 0.0f;
	}
	
	/** コピーコンストラクタ */
	public FPoint( FPoint src ) {
		this( src.x, src.y, src.z );
	}
	
	public FPoint( float x, float y ) {
		this.x = x;
		this.y = y;
		this.z = 0.0f;
	}
	
	public FPoint( float x, float y, float z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	/***************************************
	 * 
	 * 座標を自身にセットする
	 * 
	 * @param p
	 * @return
	 */
	public FPoint set( FPoint p ) {
		x = p.x;
		y = p.y;
		z = p.z;
		return this;
	}
	
	
	/***************************************
	 * 
	 * 一回でセットする
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public FPoint set( float x, float y, float z ) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**************************************
	 * 
	 * 座標にベクトルを足す
	 * 
	 * @param v
	 * @return
	 */
	public FPoint add( FVector v ) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}
	
	
	@Override
	public String toString() {
		return String.format("(%.2f, %.2f, %.2f)",x,y,z);
	}

}
