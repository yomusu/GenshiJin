package jp.yom.yglib.gl;

import javax.microedition.khronos.opengles.GL10;

import jp.yom.yglib.vector.FMatrix;



/*****************************************************
 * 
 * 
 * まだボツ
 * 頂点色付きTriangleStrip
 * 
 * 
 * これって複数あったほうがいいけどね
 * 
 * 
 * @author matsumoto
 *
 */
public class ShadeStrip implements YRenderer {

	
	/** 頂点データ:座標 */
	final public float[]	vertices;
	
	/** 頂点データ:色 */
	final public float[]	colors;
	
	final public float[]	matrix = new float[16];
	
	
	public ShadeStrip( int vertexCount ) {
		
		// 頂点バッファ:座標
		vertices = new float[vertexCount*2];
		
		// 頂点バッファ:色
		colors = new float[vertexCount*2];
		
	}
	
	
	/**************************************************
	 * 
	 * アフィン変換するマトリックスをセット
	 * 
	 * @param mat
	 */
	public void setMatrix( FMatrix mat ) {
		
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
		
		// ワールド変換Matrix適用
		gl.glMultMatrixf( matrix, 0 );
		
		g.fvbuf4.put( vertices );
		g.fvbuf4.position(0);
		
		g.fcbuf4.put( colors );
		g.fcbuf4.position(0);
		
		g.ftbuf4.put( coords );
		g.ftbuf4.position(0);
		
		gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, g.fvbuf4 );
		gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
		
		gl.glColorPointer( 4, GL10.GL_FLOAT, 0, g.fcbuf4 );
		gl.glEnableClientState( GL10.GL_COLOR_ARRAY );
		
		gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, 4 );
		
		gl.glPopMatrix();
	}

}
