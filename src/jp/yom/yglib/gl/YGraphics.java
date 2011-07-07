package jp.yom.yglib.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
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
	
	public final FloatBuffer	fvbuf4;
	public final FloatBuffer	fcbuf4;
	public final FloatBuffer	ftbuf4;
	
	/** テクスチャマップ */
	private final HashMap<String,TextureEntry>	texMap = new HashMap<String,TextureEntry>();
	
	
	public YGraphics() {
		
		fvbuf4 = createFloatBuffer(4*2);
		fcbuf4 = createFloatBuffer(4*4);
		ftbuf4 = createFloatBuffer(4*2);
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
	
	/************************************************
	 * 
	 * 4頂点の矩形、色付き
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
