package jp.yom.yglib.gl;

import javax.microedition.khronos.opengles.GL10;

import jp.yom.yglib.vector.FPoint;
import jp.yom.yglib.vector.FVector;
import android.opengl.GLU;



/*************************************************
 * 
 * 
 * 3Dのカメラレンダラ
 * 
 * ・透視変換
 * ・カメラ位置
 * 
 * のOpenGL設定を簡単にします
 * 
 * 
 * @author matsumoto
 *
 */
public class Camera3D implements YRenderer {

	/** カメラ位置 */
	FPoint	campos = new FPoint( 0, 50, -50 );
	/** 対象位置 */
	FPoint	objpos = new FPoint( 0, 0, 0 );		// 原点
	/** カメラ向き */
	FVector	camdir = new FVector( 0, 1, 0 );	// Yが上になるように
	
	
	
	/*************************************************
	 * 
	 * 
	 * OpenGLレンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		g.gl.glMatrixMode( GL10.GL_PROJECTION );
		g.gl.glLoadIdentity();
		
		
		// 透視変換…Zの向きが逆というウワサあり
		GLU.gluPerspective( g.gl, 120.0f, 1.0f, 1.0f, 100.0f );
		
		// カメラ位置…MODELVIEWというウワサあり
		GLU.gluLookAt( g.gl,
				campos.x, campos.y, campos.z,
				objpos.x, objpos.y, objpos.z,
				camdir.x, camdir.y, camdir.z
		);
		
		g.gl.glMatrixMode( GL10.GL_MODELVIEW );
		
		
		
		// テストデータ
		float	w = 10;
		float	h = 10;
		float	z = 30;
		
		float[]	vertices = new float[]{
				-w/2, h/2, z,
				 w/2, h/2, z,
				-w/2, -h/2, z,
				 w/2, -h/2, z,
		};
		float[]	colors = {
				1.0f, 1.0f, 1.0f, 1f,
				1.0f, 1.0f, 1.0f, 1f,
				1.0f, 1.0f, 1.0f, 1f,
				1.0f, 1.0f, 1.0f, 1f,
		};

		g.drawPoly4( vertices, colors );
	}
}
