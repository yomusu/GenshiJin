package jp.yom.yglib.vector;


/****************************************************
 * 
 * 
 * ベクトルを表すクラス
 * 
 * @author matsumoto
 *
 */
public class FVector {
	
	public float	x,y,z;
	
	
	public FVector( float x, float y, float z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**************************************
	 * 
	 * 2点の座標からベクトルを求める
	 * 
	 * @param p0
	 * @param p1
	 */
	public FVector( FPoint p0, FPoint p1 ) {
		
		// p0からp1に行くベクトル
		x = p1.x - p0.x;
		y = p1.y - p0.y;
		z = p1.z - p0.z;
	}
	
	/****************************************
	 * 
	 * コピーコンストラクタ
	 */
	public FVector( FVector src ) {
		x = src.x;
		y = src.y;
		z = src.z;
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
	public FVector set( float x, float y, float z ) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	
	/*****************************************
	 * 
	 * 自身に相手の内容をセットする
	 * 
	 * @param v
	 * @return
	 */
	public FVector set( FVector v ) {
		
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		return this;
		
	}
	
	/****************************************
	 * 
	 * ベクトル同士の足し算
	 */
	public FVector add( FVector src ) {
		x += src.x;
		y += src.y;
		z += src.z;
		return this;
	}
	
	/****************************************
	 * 
	 * 自身から相手を引く
	 */
	public FVector sub( FVector src ) {
		x -= src.x;
		y -= src.y;
		z -= src.z;
		return this;
	}
	
	/****************************************
	 * 
	 * 倍率を掛ける
	 */
	public FVector scale( float s ) {
		x *= s;
		y *= s;
		z *= s;
		return this;
	}
	
	/****************************************
	 * 
	 * 逆行する
	 */
	public FVector invert() {
		x *= -1f;
		y *= -1f;
		z *= -1f;
		return this;
	}
	
	/****************************************
	 * 
	 * 正規化を行う
	 */
	public FVector normalize() {
		
		float	scalar = getScalar();
		x = x / scalar;
		y = y / scalar;
		z = z / scalar;
		
		return this;
	}
	
	
	/*****************************************
	 * 
	 * 指定された反射ベクトルで反射を行う
	 * 反射ベクトルは壁の法線ベクトルで正規化されているのを前提
	 * 
	 * @param ref
	 * @return
	 */
	public FVector reflection( FVector ref ) {
		
		float	refs = Math.abs(getDot( ref )) * 2f;
		add( new FVector(ref).scale(refs) );
		return this;
	}
	
	/****************************************
	 * 
	 * スカラーを求める
	 */
	public float getScalar() {
		return (float)Math.sqrt( (x*x) + (y*y) + (z*z) );
	}
	
	/*****************************************
	 * 
	 * 指定されたベクトルとの内積を求める
	 * (0に近いと直角)
	 * 
	 * cosθを求めるに近い
	 * 
	 */
	public float getDot( FVector b ) {
		return (x*b.x) + (y*b.y) + (z*b.z);
	}
	
	/******************************************
	 * 
	 * aとbの外積を求め、自身にセットする
	 * 
	 * 外積ベクトルは、
	 * 1) 向き・・・aとbで作る面に垂直。しかも外積の順序で向きが異なる。
	 * 2) 大きさ・・・aとbで作る平行四辺形の面積に等しい
	 * 
	 */
	public void setAsCross( FVector a, FVector b ) {
		x = (a.y * b.z) - (a.z * b.y);
		y = (a.z * b.x) - (a.x * b.z);
		z = (a.x * b.y) - (a.y * b.x);
	}
	
	/*******************************************
	 * 
	 * 自身と指定されたベクトルの外積ベクトルを求める
	 */
	public FVector getCross( FVector v ) {
		FVector	r = new FVector(0f,0f,0f);
		r.setAsCross(this,v);
		return r;
	}
	
	@Override
	public String toString() {
		return String.format("[%.2f, %.2f, %.2f]",x,y,z);
	}
	
	
	static public void main( String[] args ) {
		
		//--------------------------------
		// 反射のテスト
		{
			FVector	ref = new FVector(0,-2,0).normalize();

			System.out.println( "反射1="+ new FVector(2,2,0).reflection( ref ) );
			System.out.println( "反射2="+ new FVector(2,1,0).reflection( ref ) );
			System.out.println( "反射3="+ new FVector(2,-2,0).reflection( ref ) );
		}
		{
			FVector	ref = new FVector(0.32f, 0f, 0.95f).normalize();

			System.out.println( "反射101="+ new FVector(0,0,6).reflection( ref ) );
		}
	}
}

