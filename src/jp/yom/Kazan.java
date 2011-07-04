package jp.yom;

import java.util.ArrayList;
import java.util.Iterator;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.StopWatch;
import jp.yom.yglib.gl.Sprite;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FVector;


/*******************************************
 * 
 * 火山ノード
 * 
 * 噴火します
 * 
 * @author Yomusu
 *
 */
public class Kazan extends YNode {

	
	class Tama {
		
		/** 現在地 */
		FPoint	pos;
		
		/** 回転角 */
		float	rotate;
		
		/** 進行ベクトル */
		FVector	speed;
		
		/** 角速度 */
		float	rspeed;
		
		/** カウンタ */
		StopWatch	stopWatch;
		
		Sprite	sprite;
	}
	
	
	final ArrayList<Tama>	tamaList = new ArrayList<Tama>();
	
	/************************************
	 * 
	 * 
	 * 噴火する
	 * 
	 */
	public void funka() {
		
		Tama	tama = new Tama();
		
		// 弾数
		
		// 方向
		double	angle = rangeRandom( -15.0, 15.0 );
		angle = (angle * Math.PI) / 180.0;
		
		// スピード
		double	speed = rangeRandom( 4.0, 8.0 );
		FPoint	pos = new FPoint();
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.rotateZ( (float)angle );
		mat.transform( 0f,1.0f,0f, pos );
		
		tama.speed = new FVector( pos.x, pos.y, pos.z ).scale( (float)speed );
		
		// 回転スピード
		tama.rotate = 0f;
		tama.rspeed = 4.0f;
		
		
		// 位置
		tama.pos = new FPoint(0f,0f);
		
		
		tama.sprite = new Sprite();
		tama.sprite.setSize( 32f, 32f );
		tama.sprite.setCenter( 16f, 16f );
		tama.sprite.setScale( 1f, 1f );
		tama.sprite.texkey = "iwa";
		
		// 生存カウンタ
		tama.stopWatch = new StopWatch();
		tama.stopWatch.reset();
		
		tamaList.add( tama );

	}
	
	private double rangeRandom( double min, double max ) {
		double	r = Math.random();
		return ( min * r ) + ( max * (1.0-r) );
	}
	
	/***********************************
	 * 
	 * 1フレームの処理
	 * 
	 */
	@Override
	public void process( YNode parent, GameActivity fh, YRendererList renderList  ) {
		
		Iterator<Tama>	it = tamaList.iterator();
		while( it.hasNext() ) {
			
			Tama t = it.next();
			
			// 位置
			t.pos.add( t.speed );
			
			// 回転角
			t.rotate += t.rspeed;
			if( t.rotate > 360f )
				t.rotate -= 360f;
			
			//----------------------------
			// 画面外判定
			long	time = t.stopWatch.watch();
			if( time > 1000 ) {
				
				it.remove();
				
			} else {
				
				//----------------------------
				// 描画設定
				t.sprite.x = t.pos.x;
				t.sprite.y = t.pos.y;

				t.sprite.rz = t.rotate;
				t.sprite.setAlpha( (float)((double)(1000-time) / 1000.0) );

				renderList.add( 0f, t.sprite );
			}
		}
		
		//-------------------------------------
		// あたり判定
		
//		if( pos.y >= 600 ) {
//			parent.removeChild( this );
//		}
	}
}
