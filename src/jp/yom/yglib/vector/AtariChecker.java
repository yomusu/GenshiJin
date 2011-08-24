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
		
		public FSurface	s;
		public FPoint	cp;

		protected AtariIterator( FLine line, float r ) {
			this.r = r;
			this.line = line;
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
			if( s.isBack( line.toVector() )==false ) {
				
				// 半径で外側に押し出す
				FSurface	ss = new FSurface( s ).push( r );
				
				// 押し出した面との交点計算を行う
				cp = ss.getCrossPoint( line );
				
				// 辺との当たり判定を行う
				if( cp!=null ) {
					
				}
				
				// 点との当たり判定を行う
				if( cp!=null ) {
					
				}

				if( cp!=null )
					return true;

			} else {
				cp = null;
			}

			return false;
		}
		
		private FPoint getCrossPoint( FLine line, FLine hen, float r ) {
			
			// 最接近する点、距離を出す
			FLine	c = FLine.getAdjacentPoint( line, hen );
			
			if( c!=null ) {
				// 距離が半径以内なら、当たり
				// 辺側の点から球を描き、lineとの交点を求める
				// lineとの交点のうち、起点(line.p0)に最も近いのが交点
				return line.getCrossPointForSphere( c.p1, r );
			}
			
			return null;
		}
	}
}
