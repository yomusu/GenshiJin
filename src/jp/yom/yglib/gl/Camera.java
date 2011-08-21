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
public class Camera implements YRenderer {

	/** カメラ位置 */
	public FPoint	campos = new FPoint( 0, 50, -50 );
	/** 対象位置 */
	public FPoint	objpos = new FPoint( 0, 0, 0 );		// 原点
	/** カメラ向き */
	public FVector	camdir = new FVector( 0, 1, 0 );	// Yが上になるように
	
	
	/** Farクリップ */
	public float	far = 1000f;
	
	/** Nearクリップ */
	public float	near = 1;
	
	/** 透視角Y(deg) */
	public float	fovy = 60f;
	
	
	/*************************************************
	 * 
	 * 
	 * OpenGLレンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		g.gl.glMatrixMode( GL10.GL_PROJECTION );
		
		// 透視変換…Zの向きが逆というウワサあり
		GLU.gluPerspective( g.gl, fovy, 4f/3f, near, far );
		
		// カメラ位置…MODELVIEWというウワサあり
		GLU.gluLookAt( g.gl,
				campos.x, campos.y, campos.z,
				objpos.x, objpos.y, objpos.z,
				camdir.x, camdir.y, camdir.z
		);
		
		g.gl.glMatrixMode( GL10.GL_MODELVIEW );
	}
}
