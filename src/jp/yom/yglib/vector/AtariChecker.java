package jp.yom.yglib.vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import jp.yom.yglib.AtariModel;


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
	
	ArrayList<AtariModel>	modelList = new ArrayList<AtariModel>();
	
	AtariModel	cancelModel = null;
	
	
	/********************************************
	 * 
	 * チェックするサーフェースを追加する
	 * 
	 * @param s
	 */
	public void addAll( AtariModel[] atari ) {
		modelList.addAll( Arrays.asList(atari) );
	}
	public void add( AtariModel atari ) {
		modelList.add( atari );
	}
	
	
	public void setCancelModel( AtariModel model ) {
		cancelModel = model;
	}
	
	/********************************************
	 * 
	 * 
	 * 球体の動線によって当たり判定を行う
	 * 
	 */
	public AtariResult doAtari( FPoint p0, FPoint p1, final float r ) {
		
		final FLine	line = new FLine( p0, p1 );
		
		//------------------------------
		// 面の当たり判定
		{
			Iterator<AtariModel>	it = modelList.iterator();
			while( it.hasNext() ) {

				AtariModel	model = it.next();
				if( model.surfaces!=null && model!=cancelModel ) {
					for( FSurface s : model.surfaces ) {
						// 背面ではなかったら
						if( s.isBack( line.toVector() ) == false ) {

							// 半径で外側に押し出した面との交点計算を行う
							FSurface	ss = new FSurface( s ).push( r );
							FPoint	cp = ss.getCrossPoint( line );
							if( cp!=null ) {

								AtariResult	result = new AtariResult();

								result.cp = cp;
								result.reflection = s.normal;
								
								result.model = model;
								result.surface = s;

								return result;
							}

						}
					}
				}
			}
		}
		
		//------------------------------
		// 線の当たり判定
		{
			FLine	lr = null;
			FLine	hitLine = null;
			AtariModel	hitModel = null;
			
			Iterator<AtariModel>	it = modelList.iterator();
			while( it.hasNext() ) {
				
				AtariModel	model = it.next();
				if( model.lines!=null && model!=cancelModel ) {
					for( FLine hen : model.lines ) {
						FLine	c = FLine.getAdjacentPoint( line, hen );
						if( c!=null && c.length <= r ) {

							if( lr==null ) {

								lr = c;
								hitLine = hen;
								hitModel = model;

							} else {

								// ボールの移動元に最も近いものが当たり
								float	olddis = new FVector(line.p0,c.p0).getScalar();
								float	newdis = new FVector(line.p0,lr.p0).getScalar();
								if(  olddis > newdis ) {
									lr = c;
									hitLine = hen;
									hitModel = model;
								}
							}
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
					
					result.model = hitModel;
					result.atariLine = hitLine;
					result.atariLineResult = lr;

					return result;
				}
			}

		}
		
		return null;
	}
	
	
	
	
	public class AtariResult {
		
		/** 当たったModel */
		public AtariModel	model;
		
		/** 交点 */
		public FPoint	cp;
		
		/** 当たった反射ベクトル */
		public FVector	reflection;
		
		
		/** 面が当たったらそのSurface */
		public FSurface		surface;
		
		/** 辺に当たった場合、その線が入る */
		public FLine	atariLine = null;
		
		/** 辺に当たった場合、その当たり結果が入る */
		public FLine	atariLineResult = null;
		
	}
}
