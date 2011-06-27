package jp.yom;

import java.util.ArrayList;
import java.util.Iterator;

import jp.yom.yglib.AppToolkit;
import jp.yom.yglib.gl.Sprite;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FVector;


/******************************************************
 * 
 * 
 * 落下弾クラス
 * 
 * @author Yomusu
 *
 */
public class RakkaDan  extends YNode {

	
	class Tama {
		
		/** 現在地 */
		FPoint	pos;
		
		/** 回転角 */
		float	rotate;
		
		/** 進行ベクトル */
		FVector	speed;
		
		float	rspeed;
		
		Sprite	sprite;
	}
	
	
	final ArrayList<Tama>	tamaList = new ArrayList<Tama>();
	
	
	/************************************
	 * 
	 * 
	 * 落下する
	 * 
	 */
	public void rakka() {
		
		Tama	tama = new Tama();
		
		// 弾数
		
		// 方向
		double	angle = rangeRandom( -15.0, 15.0 ) + 180.0;
		angle = (angle * Math.PI) / 180.0;
		
		// スピード
		double	speed = rangeRandom( 5.0, 10.0 );
		FPoint	pos = new FPoint();
		
		FMatrix	mat = new FMatrix();
		mat.unit();
		mat.rotateZ( (float)angle );
		mat.transform( 0f,1.0f,0f, pos );
		
		tama.speed = new FVector( pos.x, pos.y, pos.z ).scale( (float)speed );
		
		// 回転スピード
		tama.rotate = (float)rangeRandom( 0, 180.0 );
		tama.rspeed = 0f;
		
		
		// 位置
		tama.pos = new FPoint( (float)rangeRandom( -320.0, 320.0 ), 300f );
		
		
		tama.sprite = new Sprite();
		tama.sprite.setSize( 32f, 32f );
		tama.sprite.setCenter( 16f, 16f );
		tama.sprite.setScale( 1.5f, 1.5f );
		tama.sprite.texkey = "iwa";
		
		
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
	public void process( YNode parent, AppToolkit fh, YRendererList renderList  ) {
		
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
			if( t.pos.y < 0 ) {
				
				// 破裂Node設定
				
				it.remove();
				
			} else {

				//----------------------------
				// 描画設定
				t.sprite.x = t.pos.x;
				t.sprite.y = t.pos.y;

				t.sprite.rz = t.rotate;

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
