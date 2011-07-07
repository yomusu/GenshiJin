package jp.yom.yglib.gl;

import javax.microedition.khronos.opengles.GL10;



/**************************************************
 * 
 * 
 * スプライト
 * 
 * @author Yomusu
 *
 */
public class Sprite implements YRenderer {

	
	public float	x,y;
	
	/** サイズ */
	protected float	w,h;
	
	/** 中心座標 */
	protected float	cx = 0f;
	protected float	cy = 0f;
	
	/** 拡大 */
	protected float	sw = 1.0f;
	protected float	sh = 1.0f;
	
	/** 回転(DEG) */
	public float	rz;
	
	/** テクスチャキー */
	public String	texkey;
	
	/** U,V */
	float	u,v;
	
	
	/** 頂点データ */
	float[]	vertices = { 0,0, 0,0, 0,0, 0,0 };
	
	/** 頂点データ */
	float[]	colors = {
			1.0f, 1.0f, 1.0f, 1f,
			1.0f, 1.0f, 1.0f, 1f,
			1.0f, 1.0f, 1.0f, 1f,
			1.0f, 1.0f, 1.0f, 1f,
	};
	
	/***********************************************
	 * 
	 * @param x
	 * @param y
	 */
	public void setPosition( float x, float y ) {
		this.x = x;
		this.y = y;
	}
	
	/***********************************************
	 * 
	 * 中心位置を設定する
	 * 
	 * @param cx
	 * @param cy
	 */
	public void setCenter( float cx, float cy ) {
		this.cx = cx;
		this.cy = cy;
		refreshVertices();
	}
	
	/***********************************************
	 * 
	 * サイズを設定する
	 * 
	 * @param w
	 * @param h
	 */
	public void setSize( float w, float h ) {
		this.w = w;
		this.h = h;
		refreshVertices();
	}
	
	/************************************************
	 * 
	 * 拡大・縮小率を設定する
	 * 
	 * @param sw
	 * @param sh
	 */
	public void setScale( float sw, float sh ) {
		this.sw = sw;
		this.sh = sh;
	}
	
	
	/*************************************************
	 * 
	 * アルファ値を設定する
	 * 
	 * @param a
	 */
	public void setAlpha( float a ) {
		
		colors[3] = a;
		colors[7] = a;
		colors[11] = a;
		colors[15] = a;
	}
	
	public void refreshVertices() {
		vertices[0] = -cx;	vertices[1] = cy;
		vertices[2] = -cx+w; vertices[3] = cy;
		vertices[4] = -cx;   vertices[5] = cy-h;
		vertices[6] = -cx+w; vertices[7] = cy-h;
	}
	
	
	
	/****************************************************
	 * 
	 * 
	 * レンダリング処理
	 * 
	 * @param g
	 */
	@Override
	public void render( YGraphics g ) {
		
		GL10	gl = g.gl;
		
		float[] coords = {
				0f, 0f,
				1f, 0f,
				0f, 1f,
				1f, 1f,
		};
		
		gl.glPushMatrix();
		
		// 移動
		gl.glTranslatef( x, y, 0f );
		// 回転
		gl.glRotatef( rz, 0f, 0f, 1.0f );
		// 拡大縮小
		gl.glScalef( sw, sh, 1.0f );
		
		g.fvbuf4.put( vertices );
		g.fvbuf4.position(0);
		
		g.fcbuf4.put( colors );
		g.fcbuf4.position(0);
		
		g.ftbuf4.put( coords );
		g.ftbuf4.position(0);
		
		gl.glEnable( GL10.GL_TEXTURE_2D );
		gl.glEnable( GL10.GL_BLEND );
		gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		
		g.bindTexture( texkey );
        
		gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, g.fvbuf4 );
		gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
		
		gl.glColorPointer( 4, GL10.GL_FLOAT, 0, g.fcbuf4 );
		gl.glEnableClientState( GL10.GL_COLOR_ARRAY );
		
		gl.glTexCoordPointer( 2, GL10.GL_FLOAT, 0, g.ftbuf4 );
		gl.glEnableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
		
		gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, 4 );
		
		gl.glDisableClientState( GL10.GL_TEXTURE_COORD_ARRAY );
		gl.glDisable( GL10.GL_BLEND );
		gl.glDisable( GL10.GL_TEXTURE_2D );
		
		gl.glPopMatrix();
	}

}
