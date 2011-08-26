package jp.yom.yglib.vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


/*******************************************
 * 
 * 面と線の当たり判定を行う
 * 
 * また、当たった時の結果を格納する
 * 
 * @author Yomusu
 *
 */
public class AtariChecker {
	
	
	ArrayList<FSurface>	list = new ArrayList<FSurface>();
	
	
	/********************************************
	 * 
	 * チェックするサーフェースを追加する
	 * 
	 * @param s
	 */
	public void addAll( FSurface[] s ) {
		list.addAll( Arrays.asList(s) );
	}
	
	
	/********************************************
	 * 
	 * イテレータを取得する
	 * 
	 */
	public AtariIterator iterator( FLine line, float r ) {
		
		return new AtariIterator( line, r );
	}
	
	
	
	
	public class AtariIterator {
		
		private Iterator<FSurface>	it;
		
		float	r;
		FLine	line;
		
		/** 当たり対象の面 */
		public FSurface	s;
		/** 交点 */
		public FPoint	cp;
		/** 当たった反射ベクトル */
		public FVector	reflection;
		
		
		protected AtariIterator( FLine line, float r ) {
			this.r = r;
			this.line = new FLine( line );
			this.it = list.iterator();
		}

		public boolean hasNext() {
			return it.hasNext();
		}

		/********************************************
		 * 
		 * 次の面を当たり判定します
		 * 
		 */
		public boolean nextAtari() {

			s = it.next();

			// 背面ではなかったら
			if( s.isBack( line.toVector() ) ) {
				cp = null;
				return false;
			}

			// 半径で外側に押し出した面との交点計算を行う
			{
				FSurface	ss = new FSurface( s ).push( r );
				cp = ss.getCrossPoint( line );
				if( cp!=null ) {
					this.reflection = s.normal;
					return true;
				}
			}
			
			// 辺(角含む)との当たり判定を行う
			if( atariLine( new FLine( s.p0, s.p1 ) ) )
				return true;
			if( atariLine( new FLine( s.p1, s.p3 ) ) )
				return true;
			if( atariLine( new FLine( s.p3, s.p2 ) ) )
				return true;
			if( atariLine( new FLine( s.p2, s.p0 ) ) )
				return true;

			return false;
		}
		
		private boolean atariLine( FLine hen ) {
			
			// 最接近する点、距離を出す
			FLine	c = FLine.getAdjacentPoint( line, hen );
			
			// 距離が半径以内なら、当たり
			if( c!=null && c.length <= r ) {
				// 辺側の点から球を描き、lineとの交点を求める
				// lineとの交点のうち、起点(line.p0)に最も近いのが交点
				FPoint	p = line.getCrossPointForSphere( c.p1, r );
				if( p!=null ) {
					
					// 交点をセット
					this.cp = p;
					// 反射ベクトルをセット
					this.reflection = new FVector( c.p1, p ).normalize();
					
					return true;
				}
			}
			
			return false;
		}
		
		private boolean atariPoint( FPoint point ) {
			
			FPoint	p = line.getCrossPointForSphere( point, r );
			if( p!=null ) {

				// 交点をセット
				this.cp = p;
				// 反射ベクトルをセット
				this.reflection = new FVector( p, point ).normalize();

				return true;
			}

			return false;
		}
	}
}
