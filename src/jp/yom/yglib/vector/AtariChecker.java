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

				// 交点計算を行う
				cp = s.getCrossPoint( line, r );

				if( cp!=null )
					return true;

			} else {
				cp = null;
			}

			return false;
		}
	}
}
