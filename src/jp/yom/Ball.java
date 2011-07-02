package jp.yom;

import java.util.Iterator;

import jp.yom.yglib.AppToolkit;
import jp.yom.yglib.YLog;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YBoundary;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.FLine;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FSurface;
import jp.yom.yglib.vector.FVector;
import android.graphics.Bitmap;
import android.graphics.Canvas;


/*************************************************
 * 
 * 
 * ボールを表すクラス
 * 
 * 
 * @author Yomusu
 *
 */
public class Ball extends YNode {
	
	
	/** 進行ベクトル */
	public final FVector	speed = new FVector(0,0,0);
	
	/** 位置 */
	public final FPoint	pos = new FPoint(200,200);
	
	/** 半径 */
	public float	hsize;
	
	Bitmap	bitmap;
	
	
	public Ball( Bitmap bm ) {
		
		this.bitmap = bm;
		
		// ボールの直径は画像の横幅から
		hsize = (float)bitmap.getWidth() / 2.0f;
	}
	
	
	/**********************************
	 * 
	 * Ballを描画する
	 * @param canvas
	 */
	@Override
	public void paint( Canvas canvas ) {
		
		float	x = pos.x - bitmap.getWidth()/2;
		float	y = pos.y - bitmap.getHeight()/2;
		
		canvas.drawBitmap( bitmap, x, y, null );
	}
	
	
	/***********************************
	 * 
	 * 1フレームの処理
	 * 
	 */
	@Override
	public void process( YNode parent, AppToolkit fh, YRendererList renderList ) {
		
		// 移動元座標バックアップ
		FPoint	oldPos = new FPoint( pos );
		
		
		// スピードに加速度を
		speed.x += fh.getGravityX() / 5.0;
		speed.y += fh.getGravityY() / 5.0;
		
		speed.x = Math.min(speed.x,10.0f);
		speed.y = Math.min(speed.y,10.0f);
		speed.x = Math.max(speed.x,-10.0f);
		speed.y = Math.max(speed.y,-10.0f);
		
		// スピードに摩擦係数を
		
		
		// 座標にスピードを
		pos.add( speed );
		
		
		//-------------------------------------
		// あたり判定
		FLine	ballLine = new FLine( oldPos,  pos );
		
		Iterator<YNode>	it = parent.childs();
		while( it.hasNext() ) {
			 YNode o = it.next();
			if( o instanceof YBoundary ) {
				
				Iterator<FSurface>	lineIt = ((YBoundary)o).getBoundsIter();
				
				while( lineIt.hasNext() ) {
					
					FSurface	s = lineIt.next();
					
					// 壁ライン
					FLine line = new FLine( s.p0, s.p1 );
					
					
					//-----------------------------
					// バックフリップ
					boolean	back = s.isBack( ballLine.toVector() );
					
					if( back )
						continue;
					
					//-----------------------------
					// 線分 vs 線分(球) 交差判定 -> 線分と線分の交差判定
					
					// lineをボールの位置方向にボール半径分平行移動する
					FVector	hv = line.getCrossVector(ballLine.p1).normalize().invert().scale(hsize);
					
					FLine	line2 = new FLine(line).move( hv );
					
					// 線同士の交差判定
					if( line2.isCross( ballLine ) && ballLine.isCross( line2 ) ) {

						// 法線方向反射ベクトル
						//FVector	ref = line2.getCrossVector( ballLine.p1 );

						// 交差している場所を求める
						FPoint	cp = ballLine.getCrossPoint( line2 );
						
						pos.set(cp);
						
						// めり込んだ分のベクトル
						//FVector	merikomi = new FVector( cp, ballLine.p1 );
						// めり込んだ分を反射させる
						//merikomi.add( ref.scale(2f) );
						// ボールの位置を反射後に変更
						//pos.set(cp).add(merikomi);

						// ボールのスピードベクトルを反射
						FPoint	cps = cp.add(speed);
						speed.add( line2.getCrossVector(cps).scale(2f) );

						break;
					}
					
					//-----------------------------
					// 点 vs 線分(球) 交差判定 -> 円と直線の交点
					{
						FPoint	cp = ballLine.getCrossPointForSphere( line.p0, hsize );
						if( cp!=null ) {
							
							pos.set(cp);
							
							// 円の中心から交点へのベクトルが反射ベクトル
							FVector	ref = new FVector(line.p0,cp).normalize();
							
							// めり込んだ分のベクトル
							//FVector	merikomi = new FVector( cp, ballLine.p1 );
							// めり込んだ分を反射させる
							//merikomi.reflection(ref);
							// ボールの位置を反射後に変更
							//pos.set(cp).add(merikomi);
							
							// ボールのスピードベクトルを反射
							speed.reflection(ref);
							
							YLog.info("ball",speed);
							
							break;
						}
					}
					{
						FPoint	cp = ballLine.getCrossPointForSphere( line.p1, hsize );
						if( cp!=null ) {
							
							pos.set(cp);
							
							// 円の中心から交点へのベクトルが反射ベクトル
							FVector	ref = new FVector(line.p1,cp).normalize();
							
							// めり込んだ分のベクトル
							//FVector	merikomi = new FVector( cp, ballLine.p1 );
							
							// めり込んだ分を反射させる
							//merikomi.reflection(ref);
							// ボールの位置を反射後に変更
							//pos.set(cp).add(merikomi);
							
							// ボールのスピードベクトルを反射
							speed.reflection(ref);
							
							break;
						}
					}
				}
				
			}
		}
	}
	
}
