package jp.yom.yglib.vector;



/*********************************************
 * 
 * 
 * 当たり判定のための
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
		
		p0 = new FPoint(_p0);
		p1 = new FPoint(_p1);
		p2 = new FPoint(_p2);
		p3 = new FPoint(_p3);
		
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
		
		// P0 -> P1
		FVector	v0 = new FVector( p0, p1 );
		// P1 -> P2
		FVector	v1 = new FVector( p1, p2 );
		
		this.normal = v0.getCross(v1).normalize();
	}
	
	
	/***************************************************
	 * 
	 * 指定されたマトリックスでアフィン変換を行う
	 * 
	 * @param mat
	 * @return
	 */
	public void transform( FMatrix mat ) {
		
		mat.transform( p0.x, p0.y, p0.z, p0 );
		mat.transform( p1.x, p1.y, p1.z, p1 );
		mat.transform( p2.x, p2.y, p2.z, p2 );
		mat.transform( p3.x, p3.y, p3.z, p3 );
		
		normalize();
	}
	
	
	/********************************************
	 * 
	 * 線分との交点を求める
	 * 
	 * @param line	開始点から終了点
	 * @param r		球の場合の半径
	 * 
	 * @return	交点。nullだったら交わらず
	 */
	public FPoint getCrossPoint( FLine line, float r ) {
		
		// 線分が自身の無限面と交差しているか判定
		
		//--------------------------------
		// 交点との距離を求める
		float	d0 = getDistance( line.p0 );
		float	d1 = getDistance( line.p1 );
		
		// 半径を適用
		if( d0 >= 0 ) {
			d0 -= r;
			d1 -= r;
		} else {
			d0 += r;
			d1 += r;
		}
		
		// 符号が同じだったら交差していない
		if( d0<0 && d1<0 )
			return null;
		if( d0>0 && d1>0 )
			return null;
		
		//--------------------------------
		// 交点との距離を線分のベクトルに適用し、交点を割り出す
		// ただし、この交点は半径含み
		FVector	v = new FVector( line.p0, line.p1 ).normalize().scale( Math.abs(d0) );
		FPoint	crossPoint = new FPoint(line.p0).add(v);
		
		//--------------------------------
		// 球が面に接する座標を取得
		FPoint	dp = getDropPoint( crossPoint );
		
		//--------------------------------
		// 交点が面を構成する線分の内側にいるかどうか判定
		FVector	v0 = new FVector( p0, p1 );
		FVector	v1 = new FVector( p1, p3 );
		FVector	v2 = new FVector( p3, p2 );
		FVector	v3 = new FVector( p2, p0 );
		
		FVector	cv0 = v0.getCross( new FVector( p0, dp ) );
		FVector	cv1 = v1.getCross( new FVector( p1, dp ) );
		FVector	cv2 = v2.getCross( new FVector( p3, dp ) );
		FVector	cv3 = v3.getCross( new FVector( p2, dp ) );
		
		float	f0 = cv0.getDot( normal );
		float	f1 = cv1.getDot( normal );
		float	f2 = cv2.getDot( normal );
		float	f3 = cv3.getDot( normal );
		
		if( f0>=0 && f1>=0 && f2>=0 && f3>=0 )
			return crossPoint;
		if( f0<=0 && f1<=0 && f2<=0 && f3<=0 )
			return crossPoint;
		
		//--------------------------------
		// 交点が、外側だがthicknessより内側にあるときは
		
		// 1)直線p0-p2及びp1-p3の内側にあり、直線p0-p1もしくはp2-p3との距離がthickness以内なら
		if( (f1>=0 && f3>=0) || (f1>=0 && f3>=0) ) {
			if( new FLine( p0, p1 ).getCrossDistance( dp ) < r )
				return crossPoint;
			if( new FLine( p2, p3 ).getCrossDistance( dp ) < r )
				return crossPoint;
		}
		
		// 2)直線p0-p1及びp2-p3の内側にあり、直線p0-p2もしくはp1-p3との距離がthickness以内なら
		if( (f0>=0 && f2>=0) || (f0>=0 && f2>=0) ) {
			if( new FLine( p0, p2 ).getCrossDistance( dp ) < r )
				return crossPoint;
			if( new FLine( p1, p3 ).getCrossDistance( dp ) < r )
				return crossPoint;
		}
		
		// 2)p0～p3の各点との距離がthickness以内なら角にヒット
		if( new FVector( p0, dp ).getScalar() <= r )
			return crossPoint;
		if( new FVector( p1, dp ).getScalar() <= r )
			return crossPoint;
		if( new FVector( p2, dp ).getScalar() <= r )
			return crossPoint;
		if( new FVector( p3, dp ).getScalar() <= r )
			return crossPoint;
		
		
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
	
	
	/**********************************************
	 * 
	 * 点から面に垂線を落とした時の座標
	 * 
	 * @param p
	 */
	public FPoint getDropPoint( FPoint p ) {
		
		float	d = getDistance( p );
		FVector	v = new FVector(normal).invert().scale(d);
		
		return new FPoint(p).add(v);
	}
	
	public String toString() {
		
		StringBuilder	buf = new StringBuilder();
		
		buf.append("p0=").append(p0.toString());
		buf.append(" p1=").append(p1.toString());
		buf.append(" p2=").append(p2.toString());
		buf.append(" p3=").append(p3.toString());
		buf.append(" normal=").append(normal.toString());
		
		return buf.toString();
	}
	
	static public void main( String[] args ) {
		
		FSurface	s = new FSurface( new FPoint(10,10), new FPoint(20,10), new FPoint(10,0 ), new FPoint(20,0) );
		//s.normal = new FVector(0,0,1f);
		System.out.println( "法線ベクトル:"+s.normal );
		
		System.out.println( "交点0"+s.getCrossPoint( new FLine( new FPoint(15,5,-1f), new FPoint(15,5,1f) ),1 ) );
		System.out.println( "交点1"+s.getCrossPoint( new FLine( new FPoint(10,10,-1f), new FPoint(10,10,1f) ),1 ) );
		System.out.println( "交点2"+s.getCrossPoint( new FLine( new FPoint(10f,1,-1f), new FPoint(10.1f,1,1f) ),1 ) );
		
		System.out.println( "球の交点1"+s.getCrossPoint( new FLine( new FPoint(5f,5f,-10f), new FPoint(5f,5f,-3f) ),5.1f ) );
		System.out.println( "球の交点2"+s.getCrossPoint( new FLine( new FPoint(15f,15f,-10f), new FPoint(15f,15f,-3f) ),5.1f ) );
		System.out.println( "球の交点3"+s.getCrossPoint( new FLine( new FPoint(8f,13f,-10f), new FPoint(8f,13f,-3f) ),5f ) );
		
		System.out.println( "垂線の点"+s.getDropPoint( new FPoint(15,5f,-10f) ) );
		
		System.out.println( s.isBack( new FVector(0,0,1f) ) );
		System.out.println( s.isBack( new FVector(0,0,-1f) ) );
		System.out.println( s.isBack( new FVector(0,1f,0) ) );
		
		
	}
}
