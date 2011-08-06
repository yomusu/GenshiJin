package jp.yom.yglib.gl;

import javax.microedition.khronos.opengles.GL10;


/************************************************************
 * 
 * 
 * GLのMaterialの設定を行うクラス
 * 
 * 
 * @author Yomusu
 *
 */
public class Material implements YRenderer {
	
	/** 環境光 */
	public float[]	ambient = null;
	
	/** ディフューズ */
	public float[]	diffuse = null;
	
	/** スペキュラー */
	public float[]	specular= null;
	
	/** エミッション(放射輝度) */
	public float[]	emission= null;
	
	/** 鏡面光の指数 */
	public float	shinness = 0f;
	
	
	
	/**************************************************
	 * 
	 * アンビエントの色をセットする
	 * 
	 */
	public void setAmbientColor( float r, float g, float b ) {
		
		if( ambient==null ) {
			ambient = new float[4];
			ambient[3] = 1.0f;
		}
		
		ambient[0] = r;
		ambient[1] = g;
		ambient[2] = b;
	}
	
	/**************************************************
	 * 
	 * ディフューズの色をセットする
	 * 
	 */
	public void setDiffuseColor( float r, float g, float b ) {
		
		if( diffuse==null ) {
			diffuse = new float[4];
			diffuse[3] = 1.0f;
		}
		
		diffuse[0] = r;
		diffuse[1] = g;
		diffuse[2] = b;
	}
	
	/**************************************************
	 * 
	 * スペキュラーの色をセットする
	 * 
	 */
	public void setSpecularColor( float r, float g, float b ) {
		
		if( specular==null ) {
			specular = new float[4];
			specular[3] = 1.0f;
		}
		
		specular[0] = r;
		specular[1] = g;
		specular[2] = b;
	}
	
	/**************************************************
	 * 
	 * エミッション(放射輝度)の色をセットする
	 * 
	 */
	public void setEmissionColor( float r, float g, float b ) {
		
		if( emission==null ) {
			emission = new float[4];
			emission[3] = 1.0f;
		}
		
		emission[0] = r;
		emission[1] = g;
		emission[2] = b;
	}
	
	
	/**************************************************
	 * 
	 * 鏡面光の指数
	 * 
	 * @param f
	 */
	public void setShinness( float f ) {
		
		shinness = f;
	}
	
	/***********************
	 * 
	 * レンダリング
	 */
	@Override
	public void render(YGraphics g) {
		
		if( ambient!=null )
			g.gl.glMaterialfv( GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambient, 0 );
		
		if( diffuse!=null )
			g.gl.glMaterialfv( GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuse, 0 );
		
		if( specular!=null )
			g.gl.glMaterialfv( GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specular, 0 );
		
		
		g.gl.glMaterialf( GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shinness );
		
		if( emission!=null )
			g.gl.glMaterialfv( GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, emission, 0 );
		
	}
	
}
