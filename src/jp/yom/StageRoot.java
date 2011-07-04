package jp.yom;

import jp.yom.yglib.GameActivity;
import jp.yom.yglib.gl.YGraphics;
import jp.yom.yglib.gl.YRenderer;
import jp.yom.yglib.gl.YRendererList;
import jp.yom.yglib.node.YNode;


/*****************************************************
 * 
 * 
 * ステージのルートとなるノード
 * 
 * ・カメラ位置をコントロールします
 * ・背景の描画
 * 
 * 
 * @author Yomusu
 *
 */
public class StageRoot extends YNode {
	
	
	/** センター位置 */
	float	cx = 0;
	/** センター位置(Y=0を地面とする) */
	float	cy = -100;
	
	/** スクロール量 */
	float	sx = 0;
	float	sy = 0;
	
	/** 揺れの位置 */
	float	eqx = 0;
	float	eqy = 0;
	
	
	/** カメラのレンダラ */
	protected final CameraRender	cameraRender = new CameraRender();
	/** 背景のレンダラ */
	protected final BackGroundRender	bgRender = new BackGroundRender();
	
	
	@Override
	public void process(YNode parent, GameActivity h, YRendererList renderList) {
		super.process(parent, h, renderList);
		
		// カメラ位置の計算
		cameraRender.setPosition( cx+sx+eqx, cy+sy+eqy );
		
		
		renderList.add( 10001, cameraRender );
		
		//------------------------------------
		// 背景のレンダラをセット
		renderList.add( 10000, bgRender );
	}
	
	
	/****************************************************
	 * 
	 * カメラをレンダラ
	 * 
	 */
	class CameraRender implements YRenderer {
		
		private float x;
		private float y;
		
		public void setPosition( float x, float y ) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public void render(YGraphics g) {
			g.gl.glTranslatef( this.x, this.y, 0f );
		}
	}


	/****************************************************
	 * 
	 * 背景のレンダラ
	 * 
	 * @author Yomusu
	 *
	 */
	class BackGroundRender implements YRenderer {
		
		float[]	v1 = new float[]{
				-320f, 300f,	320f, 300f,
				-320f, 0f,		320f, 0f,
		};
		
		float[]	v2 = new float[]{
				-320f, 0f,		320f, 0f,
				-320f, -100f,	320f, -100f,
		};
		
		float[]	c1 = new float[]{
				0f,0f,0f, 1f,
				0f,0f,0f, 1f,
				0f,0f,0f, 1f,
				0f,0f,0f, 1f,
		};
		
		float[]	c2 = new float[]{
				0.6f,0.4f,0.4f, 1f,
				0.6f,0.4f,0.4f, 1f,
				0.6f,0.4f,0.4f, 1f,
				0.6f,0.4f,0.4f, 1f,
		};
		
		@Override
		public void render(YGraphics g) {
			
			g.drawRect( v1, c1 );
			g.drawRect( v2, c2 );
		}
	}
}
