package jp.yom.yglib.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import jp.yom.yglib.vector.FMatrix;

import android.opengl.GLUtils;




/*****************************************************************
 * 
 * 
 * 
 * ・GLコンテキスト
 * ・nioバッファ
 * ・テクスチャマネジメント
 * ・easyメソッド
 * 
 * 
 * @author Yomusu
 *
 */
public class YGraphics {

	public GL10	gl;
	
	public final FloatBuffer	fnbuf4;
	public final FloatBuffer	fvbuf4;
	public final FloatBuffer	fcbuf4;
	public final FloatBuffer	ftbuf4;
	
	/** テクスチャマップ */
	private final HashMap<String,TextureEntry>	texMap = new HashMap<String,TextureEntry>();
	
	
	public YGraphics() {
		
		fnbuf4 = createFloatBuffer(32*3);
		fvbuf4 = createFloatBuffer(32*3);
		fcbuf4 = createFloatBuffer(32*4);
		ftbuf4 = createFloatBuffer(32*2);
	}
	
	
	/************************************************
	 * 
	 * テクスチャを管理に追加します。
	 * この後、loadTextureを行って始めてテクスチャが使えるようになります
	 * 
	 * @param tex
	 */
	public void addTexture( TextureEntry tex ) {
		
		texMap.put( tex.key, tex );
	}
	
	/************************************************
	 * 
	 * 指定されたビットマップをBindします
	 * TextureIDは新たに作ります。
	 * 
	 * @param bmp
	 * 
	 * @return	テクスチャID
	 */
	public void loadTexture() {
		
		for( TextureEntry tex : texMap.values() ) {
			if( tex.bindID == null ) {
				
				int[]	textures = new int[1];

				gl.glGenTextures( 1, textures, 0 );
				gl.glBindTexture( GL10.GL_TEXTURE_2D, textures[0] );
				GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, tex.getBitmap(), 0 );
				gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST );
				gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );
				gl.glBindTexture( GL10.GL_TEXTURE_2D, 0 );


				// 管理に追加
				tex.bindID = new Integer( textures[0] );
			}
		}
	}
	
	
	/*************************************************
	 * 
	 * 使用テクスチャを指定
	 * 
	 * @param texKey
	 */
	public void bindTexture( String texKey ) {
		TextureEntry	tex = texMap.get(texKey);
		if( tex!=null && tex.bindID!=null )
			gl.glBindTexture( GL10.GL_TEXTURE_2D, tex.bindID.intValue() );
	}
	
	
	/*************************************************
	 * 
	 * ロードしたテクスチャを削除
	 * 
	 * @param texKey
	 */
	public void deleteAllTexture() {
		for( TextureEntry tex : texMap.values() ) {
			if( tex.bindID!=null ) {
				gl.glDeleteTextures( 1, new int[]{ tex.bindID.intValue() }, 0 );
				tex.bindID = null;
			}
		}
	}
	
	
	/**************************************************
	 * 
	 * GLの毎フレームの初期化処理
	 * 
	 */
	public void initializeFor2D( int screenWidth, int screenHeight ) {
		
		// 描画準備
        gl.glDisable(GL10.GL_DITHER);
		gl.glDisable( GL10.GL_TEXTURE_2D );
		gl.glDisable( GL10.GL_BLEND );

		gl.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST );
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_LIGHTING );
		
		// 透視変換の設定
		gl.glMatrixMode( GL10.GL_PROJECTION );
		gl.glLoadIdentity();
		gl.glOrthof( -screenWidth/2f,screenWidth/2f, -screenHeight/2f,screenHeight/2f, -100f,100f );
		
		gl.glMatrixMode( GL10.GL_MODELVIEW );
		gl.glLoadIdentity();
		
		// 画面のクリア
		gl.glClearColor( 0.3f, 0.3f, 0.3f, 1.0f );
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}
	
	/**************************************************
	 * 
	 * GLの毎フレームの初期化処理
	 * 
	 */
	public void initializeFor3D( Camera camera ) {
		
		// 描画準備
        gl.glDisable(GL10.GL_DITHER);
		gl.glDisable( GL10.GL_TEXTURE_2D );
		gl.glDisable( GL10.GL_BLEND );

		gl.glHint( GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST );
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		// 隠面消去はデフォルトで行う
		depthTest( true );
		// ライティングはデフォルトで行う
		lighting( true );
		
		// 透視変換の設定
		// 左手座標系
		gl.glMatrixMode( GL10.GL_PROJECTION );
		gl.glLoadIdentity();
		gl.glScalef( -1f, 1f, 1f );
		
		camera.render( this );
		
		gl.glMatrixMode( GL10.GL_MODELVIEW );
		gl.glLoadIdentity();
		gl.glScalef( -1f, 1f, 1f );
		
		// 画面のクリア
		gl.glClearColor( 0.3f, 0.3f, 0.3f, 1.0f );
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}
	
	
	
	/*************************************************
	 * 
	 * ライティングを行うかどうか
	 * Zバッファ
	 * 
	 * @param isEnable
	 */
	public void lighting( boolean isEnable ) {
		
		if( isEnable )
			gl.glEnable( GL10.GL_LIGHTING );
		else
			gl.glDisable( GL10.GL_LIGHTING );

	}
	
	/*************************************************
	 * 
	 * 隠面消去を使うかどうか
	 * Zバッファ
	 * 
	 * @param isEnable
	 */
	public void depthTest( boolean isEnable ) {
		
		if( isEnable )
			gl.glEnable( GL10.GL_DEPTH_TEST );
		else
			gl.glDisable( GL10.GL_DEPTH_TEST );

	}
	
	
	/************************************************
	 * 
	 * 背面消去を使うかどうか
	 * 
	 * @param isEnable
	 */
	public void cullFace( boolean isEnable ) {
		
		if( isEnable )
			gl.glEnable( GL10.GL_CULL_FACE );
		else
			gl.glDisable( GL10.GL_CULL_FACE );
	}
	
	
	/**********************************************
	 * 
	 * マトリックスを適用する
	 * 
	 * 現在のマトリックスに乗算する
	 * 
	 * @param mat
	 */
	public void mulMatrix( FMatrix mat ) {
		
		matrix[0] = mat.m00;
		matrix[1] = mat.m01;
		matrix[2] = mat.m02;
		matrix[3] = mat.m03;
		
		matrix[4] = mat.m10;
		matrix[5] = mat.m11;
		matrix[6] = mat.m12;
		matrix[7] = mat.m13;
		
		matrix[8] = mat.m20;
		matrix[9] = mat.m21;
		matrix[10] = mat.m22;
		matrix[11] = mat.m23;
		
		matrix[12] = mat.m30;
		matrix[13] = mat.m31;
		matrix[14] = mat.m32;
		matrix[15] = mat.m33;
		
		gl.glMultMatrixf( matrix, 0 );
	}
	
	final private float[]	matrix = new float[16];
	
	
	/************************************************
	 * 
	 * 3次元の4頂点の矩形、法線、色付き
	 * 
	 * @param vertices	頂点。省略不可
	 * @param normals	法線。省略可能
	 * @param colors	頂点色。省略可能
	 * 
	 */
	public void drawPoly4( float[] vertices, float[] normals, float[] colors ) {
		
		if( normals!=null ) {
			fnbuf4.put( normals );
			fnbuf4.position(0);
			gl.glEnableClientState( GL10.GL_NORMAL_ARRAY );
			gl.glNormalPointer( GL10.GL_FLOAT, 0, fnbuf4 );
		}
		
		if( colors!=null ) {
			fcbuf4.put( colors );
			fcbuf4.position(0);
			gl.glEnableClientState( GL10.GL_COLOR_ARRAY );
			gl.glColorPointer( 4, GL10.GL_FLOAT, 0, fcbuf4 );
		}
		
		fvbuf4.put( vertices );
		fvbuf4.position(0);
		
		gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
		gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, fvbuf4 );
		
		
		gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, 4 );
		
		gl.glDisableClientState( GL10.GL_COLOR_ARRAY );
		gl.glDisableClientState( GL10.GL_NORMAL_ARRAY );
		gl.glDisableClientState( GL10.GL_VERTEX_ARRAY );
		
	}
	
	/************************************************
	 * 
	 * 2次元の4頂点の矩形、色付き
	 * 
	 * @param vertices
	 * @param colors
	 */
	public void drawRect( float[] vertices, float[] colors ) {
		
		fvbuf4.put( vertices );
		fvbuf4.position(0);
		
		fcbuf4.put( colors );
		fcbuf4.position(0);
		
		gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, fvbuf4 );
		gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
		gl.glColorPointer( 4, GL10.GL_FLOAT, 0, fcbuf4 );
		gl.glEnableClientState( GL10.GL_COLOR_ARRAY );
		gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, 4 );
	}
	
	/**************************************************
	 * 
	 * 4頂点の矩形、テクスチャ
	 * 
	 * 
	 * @param vertices
	 * @param texkey
	 */
	public void drawImage( float[] vertices, String texkey ) {
		
		float[] colors = {
				0.5f, 0.5f, 0.5f, 1f,
				0.5f, 0.5f, 0.5f, 1f,
				0.5f, 0.5f, 0.5f, 1f,
				0.5f, 0.5f, 0.5f, 1f,
		};

		float[] coords = {
				0f, 0f,
				1f, 0f,
				0f, 1f,
				1f, 1f,
		};
		
		fvbuf4.put( vertices );
		fvbuf4.position(0);
		
		fcbuf4.put( colors );
		fcbuf4.position(0);
		
		ftbuf4.put( coords );
		ftbuf4.position(0);
		
		gl.glEnable( GL10.GL_TEXTURE_2D );
		gl.glEnable( GL10.GL_BLEND );
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		
		bindTexture( texkey );
        
		gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, fvbuf4 );
		gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
		gl.glColorPointer( 4, GL10.GL_FLOAT, 0, fcbuf4 );
		gl.glEnableClientState( GL10.GL_COLOR_ARRAY );
		gl.glTexCoordPointer( 2, GL10.GL_FLOAT, 0, ftbuf4 );
		gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
		
		gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, 4 );
		
		gl.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
		gl.glDisable( GL10.GL_BLEND );
		gl.glDisable( GL10.GL_TEXTURE_2D );
	}
	
	public FloatBuffer createFloatBuffer( int length ) {
		ByteBuffer	buf = ByteBuffer.allocateDirect( length*4 );
		buf.order( ByteOrder.nativeOrder() );
		return buf.asFloatBuffer();
	}
	
}
