package jp.yom.yglib.vector;


/*********************************************
 * 
 * 
 * 面を表すクラス
 * 
 * @author Yomusu
 *
 */
public class FSurface {
	
	/** 頂点 */
	public FPoint	p0,p1,p2,p3;
	
	/** 法線ベクトル */
	public FVector	normal;
	
	
	
	/**************************************************
	 * 
	 * P0---P1
	 * |    |
	 * P2---P3
	 * 
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public FSurface( FPoint _p0, FPoint _p1, FPoint _p2, FPoint _p3 ) {
		
		p0 = _p0;
		p1 = _p1;
		p2 = _p2;
		p3 = _p3;
		
		normalize();
	}
	
	/**************************************************
	 * 
	 * コピーコンストラクタ
	 * 
	 * @param src
	 */
	public FSurface( FSurface src ) {
		this( new FPoint(src.p0), new FPoint( src.p1 ), new FPoint(src.p2), new FPoint( src.p3 ) );
	}
	
	/**************************************************
	 * 
	 * 法線ベクトルを再計算する
	 * 
	 */
	public void normalize() {
		
		// P1 -> P0
		FVector	v0 = new FVector( p1, p0 );
		// P1 -> P2
		FVector	v1 = new FVector( p1, p2 );
		
		this.normal = v0.getCross(v1).normalize();
	}
	
	
	/********************************************
	 * 
	 * 線分との交点を求める
	 * 
	 * @param line
	 */
	public FPoint getCrossPoint( FLine line ) {
		
		// 線分が自身の無限面と交差しているか判定
		
		// 交点との距離を求める
		float	d0 = getDistance( line.p0 );
		float	d1 = getDistance( line.p1 );
		
		// 交点との距離を線分のベクトルに適用し、交点を割り出す
		FVector	v = new FVector( line.p0, line.p1 ).normalize().scale( Math.abs(d0) );
		FPoint	p = new FPoint(line.p0).add(v);
		
		// 交点が面を構成する線分の内側にいるかどうか判定
		
		
		FVector	v0 = new FVector( p0, p1 ).getCross( new FVector( p0, p ) );
		FVector	v1 = new FVector( p1, p3 ).getCross( new FVector( p1, p ) );
		FVector	v2 = new FVector( p3, p2 ).getCross( new FVector( p3, p ) );
		FVector	v3 = new FVector( p2, p0 ).getCross( new FVector( p2, p ) );
		
		float	f0 = v0.getDot( normal );
		float	f1 = v1.getDot( normal );
		float	f2 = v2.getDot( normal );
		float	f3 = v3.getDot( normal );
		
		if( f0>=0 && f1>=0 && f2>=0 && f3>=0 )
			return p;
		if( f0<=0 && f1<=0 && f2<=0 && f3<=0 )
			return p;
		
		return null;
	}
	
	
	/***********************************************
	 * 
	 * 指定されたベクトルがバックフリップに引っかかるか判定します
	 * バックフリップに引っかかるとは、ベクトルが法線ベクトルと同じ方向を向いていること
	 * 
	 * @param line
	 * @return	true	バック：法線と直線が同じ向き
	 */
	public boolean isBack( FVector v ) {
		
		float	f = v.getDot( normal );
		
		return f>=0f;
	}
	
	/*********************************************
	 * 
	 * 指定された点と自身の最短距離
	 * 
	 * @param pa
	 * @return
	 */
	public float getDistance( FPoint p ) {
		FVector	v = new FVector( p0, p );
		return normal.getDot( v );
	}
	
	
	static public void main( String[] args ) {
		
		FSurface	s = new FSurface( new FPoint(10,10), new FPoint(20,10), new FPoint(10,0 ), new FPoint(20,0) );
		//s.normal = new FVector(0,0,1f);
		System.out.println( "法線ベクトル:"+s.normal );
		
		System.out.println( "交点0"+s.getCrossPoint( new FLine( new FPoint(15,5,-1f), new FPoint(15,5,1f) ) ) );
		System.out.println( "交点1"+s.getCrossPoint( new FLine( new FPoint(10,10,-1f), new FPoint(10,10,1f) ) ) );
		System.out.println( "交点2"+s.getCrossPoint( new FLine( new FPoint(10f,1,-1f), new FPoint(10.1f,1,1f) ) ) );
		
		
		System.out.println( s.isBack( new FVector(0,0,1f) ) );
		System.out.println( s.isBack( new FVector(0,0,-1f) ) );
		System.out.println( s.isBack( new FVector(0,1f,0) ) );
	}
}
