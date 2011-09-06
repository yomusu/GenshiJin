package jp.yom.yglib.vector;

import java.util.Iterator;

import android.util.Log;



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
	
	public interface AtariCallback {
		public void atariCallback( AtariResult result, AtariObject src );
	}
	
	AtariObject	cancelModel = null;
	
	/** 当たった時のコールバック */
	AtariCallback	callback = null;
	
	
	/********************************************
	 * 
	 * 当たった時のコールバックをセットする
	 * 
	 * @param callback
	 */
	public void setCallback( AtariCallback callback ) {
		
		this.callback = callback;
	}
	
	
	public void setCancelModel( AtariObject model ) {
		cancelModel = model;
	}
	
	
	
	/********************************************
	 * 
	 * 指定されたオブジェクトをセットされているスピードで
	 * 前進させ、当たり判定を行います
	 * 
	 * @param obj
	 */
	public void forwardAndCheck( AtariObject obj, Iterator<AtariObject> it ) {
		
		if( obj instanceof AtariBall ) {
			ballForwardAndCheck( (AtariBall)obj, it );
		}
		
		if( obj instanceof AtariModel ) {
			modelForwardAndCheck( (AtariModel)obj, it );
		}
		
	}
	
	private void modelForwardAndCheck( AtariModel obj, Iterator<AtariObject> it ) {
		
		// ボールを探す
		AtariBall	ball = null;
		{
			while( it.hasNext() ) {
				AtariObject	o = it.next();
				if( o instanceof AtariBall ) {
					ball = (AtariBall)o;
					break;
				}
			}
		}
		
		// Modelから見たボールの移動
		FPoint	ballPos = new FPoint(ball.pos);
		ballPos.add( new FVector( obj.speed ).invert() );
		
		// 当たり判定
		Nearizer	nearizer = new Nearizer( ball.pos, ballPos, ball.r );
		nearizer.vite( obj );
		
		AtariResult	atari = nearizer.getResult();
		if( atari!=null ) {

			// ボールの速度ベクトルを反射
			ball.speed.set( atari.calcAction( ball.speed ) );
			
			// ボールの位置をModelとの交点にする
			ball.p0.set( ball.pos );
			ball.pos.set( atari.cp ).add( obj.speed );
		}
		
		// Modelの位置を進める
		obj.forward();
	}
	
	private void ballForwardAndCheck( AtariBall obj, Iterator<AtariObject> it ) {
		
		// オブジェクトを前進させる
		obj.p0.set( obj.pos );
		obj.pos.add( obj.speed );
		
		
		// 当たり判定
		Nearizer	nearizer = new Nearizer( obj.p0, obj.pos, obj.r );
		
		while( nearizer!=null ) {
			
			// 対象となるオブジェクトすべてと当たり判定を行う
			while( it.hasNext() )
				nearizer.vite( it.next() );

			// 当たり結果を取得する
			AtariResult	atari = nearizer.getResult();
			if( atari!=null ) {

				// ログ
				Log.v("atari", "-- atari ----" );
				Log.v("atari", "before: "+obj.toString() );

				// 速度ベクトルを反射
				obj.speed.set( atari.calcAction( obj.speed ) );

				// 続いて残りの移動距離を反射させる
				float	nokoriLength = new FVector( atari.cp, obj.pos ).getScalar();
				FVector	vnokori = new FVector( obj.speed ).normalize().scale( nokoriLength );

				// ボールの位置を交点に
				obj.pos.set(atari.cp).add( vnokori );
				obj.p0.set(atari.cp);

				// ログ
				Log.v("atari", atari.toString() );
				Log.v("atari", "after:"+obj.toString() );
				
				if( callback!=null )
					callback.atariCallback( atari, obj );
				
				cancelModel = atari.atariobj;

				// イテレーションしなおし
				if( nokoriLength > 1f ) {
					nearizer = new Nearizer( obj.p0, obj.pos, obj.r );
					continue;
				}
			}
			
			nearizer = null;
		}
	}
	
	class Nearizer {
		
		final float	r ;
		final FLine	kiseki;
		final FVector	vkiseki;
		
		AtariResult	result = null;
		
		
		public Nearizer(  FPoint p0, FPoint p1, final float r ) {
			
			this.kiseki = new FLine( p0, p1 );
			this.vkiseki = kiseki.toVector();
			this.r = r;
		}
		
		public void vite( AtariObject obj ) {
			
			// 面の処理
			if( obj.surfaces!=null && obj!=cancelModel ) {
				
				for( FSurface s : obj.surfaces ) {
					
					// 背面ではなかったら
					if( s.isBack( vkiseki ) == false ) {

						// 半径で外側に押し出した面との交点計算を行う
						FPoint	cp = new FSurface( s ).push( r ).getCrossPoint( kiseki );
						if( cp!=null ) {
							
							// 新しい交点の方がボールの移動元に近ければ差し替え
							float	newdis = new FVector(kiseki.p0,cp).getScalar();
							if( result==null || newdis < result.distance ) {

								AtariResult	r = new AtariResult();

								r.cp = cp;
								r.normal = s.normal;
								r.distance = newdis;

								r.atariobj = obj;
								r.surface = s;

								result = r;
							}
						}
					}
				}
			}
			
			// 線の処理
			if( obj.lines!=null && obj!=cancelModel ) {
				
				for( FLine hen : obj.lines ) {
					
					FLine	c = FLine.getAdjacentPoint( kiseki, hen );
					if( c!=null && c.length <= r ) {
						
						// 辺側の点から球を描き、lineとの交点を求める
						// lineとの交点のうち、起点(line.p0)に最も近いのが交点
						FPoint	p = kiseki.getCrossPointForSphere( c.p1, r );
						if( p!=null ) {
							
							// 新しい交点の方がボールの移動元に近ければ差し替え
							float	newdis = new FVector(kiseki.p0,p).getScalar();
							if( result==null || newdis < result.distance ) {
								
								AtariResult	r = new AtariResult();

								// 交点をセット
								r.cp = p;
								// 反射ベクトルをセット(辺の点→軌跡上の点)
								r.normal = new FVector( c.p1, p ).normalize();
								r.distance = newdis;

								r.atariobj = obj;
								r.line = hen;
								r.atariLineResult = c;
								
								result = r;
							}
						}
					}
				}
			}
			
		}
		
		public AtariResult getResult() {
			
			return result;
		}
	}
	
	
	
	public class AtariResult {
		
		/** 当たったAtariObjct */
		public AtariObject	atariobj;
		
		/** 交点 */
		public FPoint	cp;
		
		/** 当たった面もしくは辺の法線ベクトル */
		public FVector	normal;
		
		/** 交点までの距離(小さいほど近い) */
		public float	distance;
		
		
		/** 面が当たったらそのSurface */
		public FSurface		surface;
		
		/** 辺に当たった場合、その線が入る */
		public FLine	line = null;
		
		/** 辺に当たった場合、その当たり結果が入る */
		public FLine	atariLineResult = null;
		
		
		public String toString() {
			
			StringBuilder	buf = new StringBuilder();
			
			buf.append( " cp=").append( cp ).append("\n");
			buf.append( " normal=" ).append(normal).append("\n");
			buf.append( " distance=" ).append(normal).append("\n");
			buf.append( " Model=").append( atariobj ).append("\n");
			buf.append( " Surface=").append( surface ).append("\n");
			buf.append( " Line=").append( line ).append("\n");
			buf.append( " atariResult=").append( atariLineResult );
			
			return buf.toString();
		}
		
		
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
			
			FVector	mySpeed = (atariobj.speed!=null) ? atariobj.speed : new FVector(0,0,0);
			
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
