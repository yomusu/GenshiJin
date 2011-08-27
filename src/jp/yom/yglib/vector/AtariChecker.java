package jp.yom.yglib.vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


/*******************************************
 * 
 * 
 * 移動する球体と
 * 面や線の当たり判定を行う
 * 
 * また、当たった時の結果を格納する
 * 
 * @author Yomusu
 *
 */
public class AtariChecker {
	
	/** 面リスト */
	ArrayList<FSurface>	surfaceList = new ArrayList<FSurface>();
	/** 直線リスト */
	ArrayList<FLine>	lineList = new ArrayList<FLine>();
	
	
	/********************************************
	 * 
	 * チェックするサーフェースを追加する
	 * 
	 * @param s
	 */
	public void addAll( FSurface[] s ) {
		surfaceList.addAll( Arrays.asList(s) );
	}
	
	/********************************************
	 * 
	 * チェックするサーフェースを追加する
	 * 
	 * @param s
	 */
	public void addAll( FLine[] s ) {
		lineList.addAll( Arrays.asList(s) );
	}
	
	
	/********************************************
	 * 
	 * イテレータを取得する
	 * 
	 */
	public AtariResult iterator( FPoint p0, FPoint p1, final float r ) {
		
		final FLine	line = new FLine( p0, p1 );
		
		//------------------------------
		// 面の当たり判定
		{
			Iterator<FSurface>	it = surfaceList.iterator();
			while( it.hasNext() ) {

				FSurface	s = it.next();

				// 背面ではなかったら
				if( s.isBack( line.toVector() ) == false ) {

					// 半径で外側に押し出した面との交点計算を行う
					FSurface	ss = new FSurface( s ).push( r );
					FPoint	cp = ss.getCrossPoint( line );
					if( cp!=null ) {

						AtariResult	result = new AtariResult();

						result.cp = cp;
						result.reflection = s.normal;

						return result;
					}

				}
			}
		}
		
		//------------------------------
		// 線の当たり判定
		{
			FLine	lr = null;
			FLine	atari = null;
			
			Iterator<FLine>	it = lineList.iterator();
			while( it.hasNext() ) {
				
				FLine	hen = it.next();
				
				FLine	c = FLine.getAdjacentPoint( line, hen );
				if( c!=null && c.length <= r ) {
					
					if( lr==null ) {
						
						lr = c;
						
					} else {
						
						// ボールの移動元に最も近いものが当たり
						float	olddis = new FVector(line.p0,c.p0).getScalar();
						float	newdis = new FVector(line.p0,lr.p0).getScalar();
						if(  olddis > newdis ) {
							lr = c;
							atari = hen;
						}
					}
				}
				
			}
			
			// 距離が半径以内なら、当たり
			if( lr!=null ) {
				
				// 辺側の点から球を描き、lineとの交点を求める
				// lineとの交点のうち、起点(line.p0)に最も近いのが交点
				FPoint	p = line.getCrossPointForSphere( lr.p1, r );
				if( p!=null ) {
					
					AtariResult	result = new AtariResult();

					// 交点をセット
					result.cp = p;
					// 反射ベクトルをセット
					result.reflection = new FVector( lr.p1, p ).normalize();
					
					result.atariLine = atari;
					result.atariLineResult = lr;

					return result;
				}
			}

		}
		
		return null;
	}
	
	
	
	
	public class AtariResult {
		
		/** 交点 */
		public FPoint	cp;
		
		/** 当たった反射ベクトル */
		public FVector	reflection;
		
		
		/** 辺に当たった場合、その線が入る */
		public FLine	atariLine = null;
		
		/** 辺に当たった場合、その当たり結果が入る */
		public FLine	atariLineResult = null;
		
	}
}
