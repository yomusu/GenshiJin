package jp.yom.yglib.gl;

import javax.microedition.khronos.opengles.GL10;


/*****************************************************
 * 
 * ライティング
 * 光源の設定保持
 * OpenGLコマンドの作成
 * 
 * 
 * Lightを継承した各種光源を使用します
 * 
 * 
 * @author Yomusu
 *
 */
public class Light implements YRenderer {
	
	
	public int	glLightNumber = GL10.GL_LIGHT0;
	
	/** アンビエント(環境光)の色(RGB) */
	public float[]	ambient = null;
	
	/** ディフューズの色(RGB) */
	public float[]	diffuse = null;
	
	/** スペキュラーの色(RGB) */
	public float[]	specular = null;
	
	/** 光源の位置 */
	public float[]	pos = new float[]{1.0f, 1.0f, 1.0f, 0.0f};
	
	/** 減衰率（一定、線形、2次） */
	public float[]	attenuation = null;
	
	
	protected Light(){}
	
	
	/**************************************************
	 * 
	 * 使用するGLのLIGHTを設定する
	 * 
	 * @param index	GL10.GL_LIGHT0 ～ GL10.GL_LIGHT9
	 */
	public void setGLLightNumber( int num ) {
		
		glLightNumber = num;
	}
	
	/**************************************************
	 * 
	 * アンビエントの色をセットする
	 * 
	 * @param dir
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
	 * @param dir
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
	 * @param dir
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
	 * 一定減衰率
	 * 
	 */
	public void setConstantAttenuation( float a ) {
		
		if( attenuation==null )
			attenuation = new float[3];
		
		attenuation[0] = a;
		attenuation[1] = 0;
		attenuation[2] = 0;
	}
	
	/**************************************************
	 * 
	 * 線形減衰率
	 * 
	 */
	public void setLinearAttenuation( float a ) {
		
		if( attenuation==null )
			attenuation = new float[3];
		
		attenuation[0] = 0;
		attenuation[1] = a;
		attenuation[2] = 0;
	}
	
	/**************************************************
	 * 
	 * 2次減衰率
	 * 
	 */
	public void setQuadraticAttenuation( float a ) {
		
		if( attenuation==null )
			attenuation = new float[3];
		
		attenuation[0] = 0;
		attenuation[1] = 0;
		attenuation[2] = a;
	}
	
	/**************************************************
	 * 
	 * レンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		// ライティング
		if( ambient!=null )
			g.gl.glLightfv(glLightNumber, GL10.GL_AMBIENT, ambient,0);
		if( diffuse!=null )
			g.gl.glLightfv(glLightNumber, GL10.GL_DIFFUSE, diffuse,0);
		if( specular!=null )
			g.gl.glLightfv(glLightNumber, GL10.GL_SPECULAR, specular,0);
		
		// 位置
		if( pos!=null )
			g.gl.glLightfv(glLightNumber, GL10.GL_POSITION, pos,0);
		
		// 減衰率
		if( attenuation!=null ) {
			g.gl.glLightf(glLightNumber, GL10.GL_CONSTANT_ATTENUATION, attenuation[0] );
			g.gl.glLightf(glLightNumber, GL10.GL_LINEAR_ATTENUATION, attenuation[1] );
			g.gl.glLightf(glLightNumber, GL10.GL_QUADRATIC_ATTENUATION, attenuation[2] );
		}
		
		g.gl.glEnable( glLightNumber );
	}
	
	
	
	//==================================================================================
	// 実際に使用する光源の実装
	//==================================================================================
	
	/**************************************************
	 * 
	 * 平行光源
	 * 
	 * @author Yomusu
	 *
	 */
	static public class DirectionalLight extends Light {
		
		/** 光源の向きをセットする */
		public void setDirection( float vx, float vy, float vz ) {
			
			if( pos==null )
				pos = new float[4];
			
			pos[0] = vx;
			pos[1] = vy;
			pos[2] = vz;
			pos[3] = 0f;
		}
	}


	/**************************************************
	 * 
	 * 点光源
	 * 
	 * @author Yomusu
	 *
	 */
	static public class PointLight extends Light {
		
		/** 光源の位置をセットする */
		public void setPosition( float x, float y, float z ) {
			
			pos[0] = x;
			pos[1] = y;
			pos[2] = z;
			pos[3] = 1f;
		}
		
	}

	/**************************************************
	 * 
	 * スポット光源
	 * 
	 * @author Yomusu
	 *
	 */
	static public class SpotLight extends Light {
		
		/** 向き */
		float[]	dir = new float[]{ 0f, 0f, -1f };
		
		/** 輝度の分布(0～128) */
		float	exponent = 0f;
		
		/** 最大放射角度(0～90、もしくは180) */
		float	cutoff =180f;
		
		/** 光源の位置をセットする */
		public void setPosition( float x, float y, float z ) {
			
			pos[0] = x;
			pos[1] = y;
			pos[2] = z;
			pos[3] = 1f;
		}
		
		/** 光源の向きをセットする */
		public void setDirection( float vx, float vy, float vz ) {
			
			dir[0] = vx;
			dir[1] = vy;
			dir[2] = vz;
		}
		
		/** レンダリング */
		@Override
		public void render(YGraphics g) {
			
			super.render(g);
			
			g.gl.glLightfv(glLightNumber, GL10.GL_SPOT_DIRECTION, pos,0);
			g.gl.glLightf(glLightNumber, GL10.GL_SPOT_EXPONENT, exponent );
			g.gl.glLightf(glLightNumber, GL10.GL_SPOT_CUTOFF, cutoff );
		}
	}

}



