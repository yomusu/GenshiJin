package jp.yom;

import jp.yom.yglib.AppToolkit;
import jp.yom.yglib.StopWatch;
import jp.yom.yglib.gl.Sprite;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;


/***************************************************
 * 
 * 
 * ペンギンNode
 * 
 * 
 * @author Yomusu
 *
 */
public class Penguin extends YNode {
	
	
	static final String[]	right = new String[] {
			"penguinR01", "penguinR02"
	};
	static final String[]	left = new String[] {
			"penguinL01", "penguinL02"
	};
	
	Sprite	spr = new Sprite();
	
	StopWatch	watch = new StopWatch();
	
	
	String[]	dir = right;
	
	
	public Penguin() {
		
		spr.texkey = "penguinR01";
		spr.setPosition( 0f, 0f );
		spr.setSize( 100f, 100f );
		spr.setCenter( 50f, 95f );
		spr.setAlpha( 1f );
		spr.setScale( 1f, 1f );
		spr.rz = 0f;
		
		watch.reset();
	}
	
	
	
	@Override
	public void process(YNode parent, AppToolkit app, YRendererList renderList) {
		super.process(parent, app, renderList);
		
		
		// 加速度センサで移動処理
		float	gx = app.getGravityX();
		
		// 目標ポジション
		float	targetx = (gx / 9.8f) * 320f;
		
		// 移動量
		float	dx = targetx - spr.x;
		
		// 
		if( Math.abs(dx) > 5 ) {
			
			spr.x += dx;
			
			// 向き
			this.dir = ( dx>=0 ) ? right : left;
			
			// 当たり判定
			atari();
		}
		
		// アニメ処理
		long	t = watch.watch();
		if( t < 200 ) {
			spr.texkey = dir[0];
		} else if( t < 400 ) {
			spr.texkey = dir[1];
		} else {
			spr.texkey = dir[0];
			watch.reset();
		}

		renderList.add( 0, spr );
	}
	
	
	/*************************************************
	 * 
	 * 当たり判定
	 * 
	 */
	public void atari() {
		
		// 落下弾の座標をすべて取得し
		
		
		// その距離が落下弾の半径＋ペンギンの半径
		
		
		// あたりなら自信の動作モードを変更する
		
	}
}
