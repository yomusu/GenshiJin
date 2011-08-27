package jp.yom.yglib.vector;




/********************************************************************
 * 
 * 
 * 
 * 線分もしくは直線を表すクラス
 * 
 * 無限直線を前提として動作するメソッドもあるので要注意です
 * 
 * 
 * @author matsumoto
 *
 */
public class FLine {
	
	/** 点情報 */
	public FPoint	p0,p1;
	
	/** 線の長さ */
	public float	length;
	
	/** 正規化ベクトル */
	public FVector	nvector;
	
	
	public FLine( FPoint p0, FPoint p1 ) {
		this.p0 = new FPoint(p0);
		this.p1 = new FPoint(p1);
		
		refresh();
	}
	
	public void refresh() {
		
		FVector	v = new FVector( p0, p1 );
		
		// 線の長さ
		this.length = v.getScalar();
		
		// 正規化されたベクトル
		this.nvector = v.normalize();
	}
	
	/******************************************
	 * 
	 * コピーコンストラクタ
	 * @param src
	 */
	public FLine( FLine src ) {
		this( new FPoint(src.p0), new FPoint(src.p1) );
	}
	
	
	/*******************************************
	 * 
	 * ベクトル化します
	 * 
	 * @return
	 */
	public FVector toVector() {
		return new FVector( p0, p1 );
	}
	
	
	/** 線分の長さを求める(スカラと同じ) */
	public float getLength() {
		return length;
	}
	
	
	/********************************************
	 * 
	 * 平行移動
	 * 
	 * @param vector
	 */
	public FLine move( FVector vector ) {
		p0.add( vector );
		p1.add( vector );
		return this;
	}
	
	/*******************************************
	 * 
	 * 指定されたマトリックスでアフィン変換を行う
	 * @param mat
	 * @return
	 */
	public FLine transform( FMatrix mat ) {
		
		p0 = mat.transform( p0.x, p0.y, p0.z, new FPoint() );
		p1 = mat.transform( p1.x, p1.y, p1.z, new FPoint() );
		
		refresh();
		
		return this;
	}
	
	/******************************************
	 * 
	 * 指定された"線分"が自分を交差していればtrueを返す
	 * on the lineでもtrueを返します
	 * 
	 * 自分を無限線とするので、確実に線分同士の交差判定をするなら
	 * 相手のisCrossも呼ぶ必要があります。
	 * 
	 * 外積ベクトルが、ベクトルの位置関係によって向きを変えるのを利用してます。
	 * 
	 * @param p
	 * @return
	 */
	public boolean isCross( FLine line ) {
		
		// 自分と相手の始点の外積ベクトル
		FVector	c0 = nvector.getCross( new FVector( p0, line.p0 ) );
		// 自分と相手の終点の外積ベクトル
		FVector	c1 = nvector.getCross( new FVector( p0, line.p1 ) );
		
		// どちらかが負なら、内積の値は負になる。つまり、符号が異なっている。
		// ということは外積ベクトルの向きが違っている。
		return c0.getDot( c1 ) <= 0.0f;
		
		// 等号を入れることでon the lineでも交差とみなしている
	}
	
	
	/*******************************************
	 * 
	 * 
	 * 
	 * @param p
	 * @param r
	 */
	public FPoint getCrossPointForSphere( FPoint p, float r ) {
		
		// 円の中心から自身へ垂線
		FVector	pv = getCrossVector(p);
		// その大きさ
		float	pvs = pv.getScalar();
		
		if( pvs < r ) {
			
			FPoint	cp;
			
			// 垂線との交点cpvを求める
			FPoint	cpv = new FPoint(p).add(pv);

			// cpvから円との交点までの距離a
			float	a = (float)Math.sqrt( (r*r) - (pvs*pvs) );
			
			
			// 円の中心から始点までの距離で、円の内側かどうかを判定
			if( new FVector(p,p0).getScalar() < r ) {
			
				// 始点が円の内側なら終点方向の交点
				FVector	cpv2p0 = new FVector(cpv,p1).normalize().scale(a);

				// 円との交点(始点に近い方)
				cp = new FPoint(cpv).add( cpv2p0 );
				
			} else {
				
				// 始点が円の外側なら始点方向の交点
				FVector	cpv2p0 = new FVector(cpv,p0).normalize().scale(a);

				// 円との交点(始点に近い方)
				cp = new FPoint(cpv).add( cpv2p0 );
			}
			
			
			//------------------
			// 円との交点が線分上にあるか確認
			if( new FLine(p,cp).isCross( this ) )
				return cp;
		}
		
		return null;
	}
	
	/******************************************
	 * 
	 * 指定された点が自身とどのくらい離れているか最短距離を求める
	 * 
	 * @param pa
	 */
	public float getDistance( FPoint pa ) {
		
		// 開始点から指定点までのベクトル
		FVector	va = new FVector( p0, pa );
		
		// 交差ざひょう
		FVector	c = nvector.getCross( va );	// 点から線上へのベクトル
		c = nvector.getCross( c ).invert();
		FPoint	cp = new FPoint(pa).add( c );
		
		// 始点から終点
		FVector	s2e = new FVector(p0,p1);
		// 始点から交差
		FVector	s2c = new FVector(p0,cp);
		
		
		// 2つのベクトルが逆向きなら始点との距離が最短距離
		if( s2e.getDot(s2c) < 0 )
			return new FVector( p0, pa ).getScalar();
		
		// 交点が終点より先なら終点からの距離が最短距離
		if( s2c.getScalar() > length )
			return new FVector( p1, pa ).getScalar();
		
		return c.getScalar();
	}
	
	/****************************************
	 * 
	 * 指定された点と自身を無限線とみなした時の
	 * 直交距離を求める
	 * 
	 * @param p
	 */
	public float getCrossDistance( FPoint pa ) {
		
		// 開始点から指定点までのベクトル
		FVector	va = new FVector( p0, pa );
		
		FVector	c = nvector.getCross( va );
		
		return c.getScalar();
	}
	
	
	/******************************************
	 * 
	 * 指定された点から線分方向へ直交するベクトルを求める
	 * 
	 * @param pa
	 */
	public FVector getCrossVector( FPoint pa ) {
		
		// 開始点から指定点までのベクトル
		FVector	va = new FVector( p0, pa );
		
		// 外積2発で出す方法
		FVector	c = nvector.getCross( va );
		return nvector.getCross( c );
		
//		// 内積パターン
//		float	p = nvector.getDot( v );
//		FVector	v0 = new FVector( nvector ).scale( p );
//		v.sub( v0 );
	}
	
	
	/********************************************
	 * 
	 * 指定された線分との交点を求めます
	 * 
	 * @param line
	 * @return
	 */
	public FPoint getCrossPoint( FLine line ) {
		
		// 各点との直行距離
		float	d0 = line.getCrossDistance( p0 );
		float	d1 = line.getCrossDistance( p1 );
		
		// 比率
		float	t = d0 / (d0+d1);
		
		// ベクトル化
		FVector	v = new FVector(nvector).scale( t * length );
		
		// 始点と足して完了
		return new FPoint( p0 ).add( v );
	}
	
	
	@Override
	public String toString() {
		
		StringBuilder	buf = new StringBuilder();
		
		buf.append(p0).append("-").append(p1);
		
		return buf.toString();
	}
	
	
	
	/***************************************************
	 * 
	 * ２本の線分が最接近する点をFLineにて返します
	 * 
	 * line1の点→line2の点
	 * 
	 * @param line1
	 * @param line2
	 * 
	 * @return
	 */
	static public FLine getAdjacentPoint( FLine line1, FLine line2 ) {
		
		FVector	v1 = new FVector( line1.nvector );
		FVector	v2 = new FVector( line2.nvector );
		
		FPoint	p1 = new FPoint( line1.p0 );
		FPoint	p2 = new FPoint( line2.p0 );
		
		FVector	p1p2 = new FVector( p1, p2 );
		
		float	d1 = p1p2.getDot( v1 );
		float	d2 = p1p2.getDot( v2 );
		float	dv = v1.getDot(v2);
		
		// 本当は距離だけでも出したい
		if( dv==1.0f || dv==-1.0f )
			return null;
		
		float	t1 = ( d1 - d2 * dv ) / ( 1.0f - dv * dv );
		float	t2 = ( d2 - d1 * dv ) / ( dv * dv - 1.0f );
		
		t1 = Math.max( t1, 0f );
		t1 = Math.min( t1, line1.length );
		
		t2 = Math.max( t2, 0f );
		t2 = Math.min( t2, line2.length );
		
		FPoint	q1 = p1.add( v1.scale(t1) );
		FPoint	q2 = p2.add( v2.scale(t2) );
		
		return new FLine(q1,q2);
	}


	/***************************************************************
	 * 
	 * テスト
	 * 
	 * @param args
	 */
	static public void _atari( FLine kabe, FLine ugoki ) {
		
		// ballUgokiを壁によって反射させる
		System.out.println("ボール="+ugoki);
		
		// 線同士の交差判定
		if( kabe.isCross( ugoki) && ugoki.isCross( kabe) ) {
			
			// 交差している場所を求める
			FPoint	cp = ugoki.getCrossPoint( kabe);
			
			System.out.println( "交点="+cp );
			
			// めり込んだ分のベクトル
			FVector	merikomi = new FVector( cp, ugoki.p1 );
			
			// 法線方向反射ベクトル
			FVector	ref = kabe.getCrossVector( ugoki.p1 );
			
			// 反射ベクトルを求める
			merikomi.add( ref.scale(2f) );
			
			System.out.println( "めり込んで反射ベクトル"+merikomi );
			System.out.println( "結果の座標"+cp.add(merikomi) );
			
		} else {
			System.out.println( "交差せず" );
		}
	}
	
	static public void main( String[] args ) {
		
		// 2点から線を作成
		FLine	kabeLine = new FLine( new FPoint(0,0), new FPoint(0,1000) );
		System.out.println("壁="+kabeLine);
		
		//----------------------------------------
		// 逆行ベクトル
		System.out.println( "壁の逆行ベクトル="+kabeLine.getCrossVector( new FPoint(3,3) ).invert() );
		
		//----------------------------------------
		// 当たるケース
		
		// 普通に当たるケース
		System.out.println( "\n== ATARI case00 ==" );
		_atari( kabeLine, new FLine( new FPoint(10,50), new FPoint(-10,500) ) );
		
		// 先っぽにon the line
		System.out.println( "\n== ATARI case01 ==" );
		_atari( kabeLine, new FLine( new FPoint(10,1000), new FPoint(-10,1000) ) );
		
		// 根本にon the line
		System.out.println( "\n== ATARI case02 ==" );
		_atari( kabeLine, new FLine( new FPoint(10,0), new FPoint(-10,0) ) );
		
		// xもon the line
		System.out.println( "\n== ATARI case02 ==" );
		_atari( kabeLine, new FLine( new FPoint(0,0), new FPoint(-10,0) ) );
		
		//----------------------------------------
		// 当たるケース
		
		// 根本がぎりぎりかすらない
		System.out.println( "\n== NOT ATARI case02 ==" );
		_atari( kabeLine, new FLine( new FPoint(10,-0.1f), new FPoint(-10,-0.1f) ) );
		
		// 先っぽがぎりぎりかすらない
		System.out.println( "\n== NOT ATARI case02 ==" );
		_atari( kabeLine, new FLine( new FPoint(10,1000.1f), new FPoint(-10,1000.1f) ) );
		
		// xがぎりぎりかすらない
		System.out.println( "== NOT ATARI case01 ==" );
		_atari( kabeLine, new FLine( new FPoint(-0.1f,1000), new FPoint(-10,1000) ) );
		
		
		System.out.println("線分内");
		kabeLine.getDistance( new FPoint(10,0) );
		
		System.out.println("線分外:逆方向");
		kabeLine.getDistance( new FPoint(0,-100) );
		
		System.out.println("線分外:逆方向");
		kabeLine.getDistance( new FPoint(10,-1) );
		
		System.out.println("線分外:順方向");
		kabeLine.getDistance( new FPoint(10,1001) );
		
		System.out.println( "== 直線同士の最短距離 ==" );
		FLine	l0 = new FLine( new FPoint(-30,20,-100), new FPoint(30,20,-100) );
		FLine	l1 = new FLine( new FPoint(-35,5,0), new FPoint(-35,5,-105) );
		System.out.println( getAdjacentPoint( l0, l1 ) );
		System.out.println( "== 無限線?の最短距離 ==" );
		FLine	l10 = new FLine( new FPoint(-30,20,-100), new FPoint(0,20,-100) );
		FLine	l11 = new FLine( new FPoint(35,5,0), new FPoint(35,5,-105) );
		System.out.println( getAdjacentPoint( l10, l11 ) );
	}
	
	static public void sphere_labo() {
		
		FLine	line = new FLine( new FPoint(-2,1), new FPoint(2,1) );
		
		FPoint	cp = line.getCrossPointForSphere( new FPoint(0,0), 2f );
		
		System.out.println( cp );
	}
	
	static public void length_labo() {
		
		FLine	kabe = new FLine( new FPoint(0,0), new FPoint(20,0) );
		
		System.out.println( kabe.getDistance( new FPoint(10,5) ) );
		
		System.out.println( kabe.getDistance( new FPoint(-5,0) ) );
		
		System.out.println( kabe.getDistance( new FPoint(25,0) ) );
		
		System.out.println( kabe.getDistance( new FPoint(25,5) ) );
	}
	
	static public void naiseki_labo() {
		
		// 内積ラボ
		
		FVector	a = new FVector(10,10,0);
		
		System.out.println( "平行="+a.getDot( new FVector(10,10,0) ) );
		System.out.println( "少しずれる1="+a.getDot( new FVector(5,4,0) ) );
		System.out.println( "少しずれる2="+a.getDot( new FVector(5,6,0) ) );
		System.out.println( "直角1="+a.getDot( new FVector(10,-10,0) ) );
		System.out.println( "直角2="+a.getDot( new FVector(-10,10,0) ) );
		System.out.println( "やや直角より逆="+a.getDot( new FVector(10,-11,0) ) );
	}
	
	
}
