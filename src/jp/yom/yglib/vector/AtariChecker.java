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
								result.normal = s.normal;
								
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
					// 反射ベクトルをセット(辺の点→軌跡上の点)
					result.normal = new FVector( lr.p1, p ).normalize();
					
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
		
		/** 当たった面もしくは辺の法線ベクトル */
		public FVector	normal;
		
		
		/** 面が当たったらそのSurface */
		public FSurface		surface;
		
		/** 辺に当たった場合、その線が入る */
		public FLine	atariLine = null;
		
		/** 辺に当たった場合、その当たり結果が入る */
		public FLine	atariLineResult = null;
		
		
		
		/********************************************
		 * 
		 * 運動エネルギーを与えられた時の影響を計算する
		 * 
		 * @param dst
		 */
		public FVector calcAction( FVector _action ) {
			
			// 法線方向に掛かる力を算出
			float	s = _action.getDot( normal );
			FVector	force = new FVector(normal).scale( s );
			
			// 相手の速度（自身の衝撃吸収度合いを乗算）
		//	FVector	dstSpeed = new FVector( action ).scale( 1.0f );
			
			FVector	mySpeed = (model.speed!=null) ? model.speed : new FVector(0,0,0);
			
			// 自身の速度
			FVector	affect = new FVector(mySpeed).add( force );
			
			// 反作用
			FVector	reaction = new FVector(mySpeed).add( new FVector(force).invert() );
			
			// 自身は動かないため
			reaction.add( affect.invert() );
			
			return new FVector(_action).add( reaction );
		}
		
		
	}
	
	static public void main( String[] args ) {
		
		FVector	normal = new FVector( 0.67f, 0, 0.75f );
		FVector	speed = new FVector( 3.89f, 0, 4.56f );
		
		
		float	s = speed.getDot( normal );
		
		FVector	action = new FVector(normal).scale( s );
		
		
		FVector	r = new FVector(action).invert().scale(2.0f).add( speed );
		
		System.out.println( r );
	}
	
	public static class ActionResult {
		
		/** 自身に対する作用 */
		public FVector	affect;
		
		/** 反作用 */
		public FVector	reaction;
	}
}
