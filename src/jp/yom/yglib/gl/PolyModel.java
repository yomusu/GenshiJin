package jp.yom.yglib.gl;

import javax.microedition.khronos.opengles.GL10;




/**************************************************
 * 
 * 
 * モデルひとつを表すクラス
 * 
 * モデルひとつとは、
 * 
 * ・同じアフィン変換を使用する
 * ・同じ頂点座標リストを共有する
 * ・同じ法線リストを共有する
 * ・同じUVリストを共有する
 * 
 * ・複数のポリゴンを使用できる
 * 
 * 
 * ポリゴンひとつは、
 * ・TriangleFanかTriangleStripを指定できる
 * ・頂点座標、法線、UVをインデックスで指定する
 * ・マテリアルをひとつ使用できる
 * ・テクスチャをひとつ指定できる
 * 
 * 
 * @author Yomusu
 *
 */
public class PolyModel implements YRenderer {
	
	
	//=======================================================
	// ポリゴンクラスの宣言
	//=======================================================
	
	public static class Polygon {
		
		int		type;
		
		int[]	posIndexies;
		int[]	normIndexies;
		
		int		materialIndex;
		String	textureKey;
		
		protected Polygon() {}
	}
	
	//=======================================================
	// スタティックメソッドの宣言
	//=======================================================
	
	/******************
	 * 
	 * create a triangle fan
	 * without texture
	 * 
	 */
	public static Polygon createTriFan( int[] pos, int[] norm, int material ) {
		
		Polygon	p = new Polygon();
		
		p.type = GL10.GL_TRIANGLE_FAN;
		p.posIndexies = pos;
		p.normIndexies = norm;
		p.materialIndex = material;
		
		return p;
	}
	
	/******************
	 * 
	 * create a triangle strip
	 * without texture
	 * 
	 */
	public static Polygon createTriStrip( int[] pos, int[] norm, int material ) {
		
		Polygon	p = new Polygon();
		
		p.type = GL10.GL_TRIANGLE_STRIP;
		p.posIndexies = pos;
		p.normIndexies = norm;
		p.materialIndex = material;
		
		return p;
	}
	
	//=======================================================
	// メンバ変数の宣言
	//=======================================================
	
	/** ポリゴン配列 */
	public Polygon[]	polys;
	
	/** 頂点座標配列 */
	public float[]	positions;
	/** 法線配列 */
	public float[]	normals;
	
	
	/** マテリアル配列 */
	public Material[]	materials = null;
	
	
	
	//=======================================================
	// メソッドの宣言
	//=======================================================
	
	/**************************************************
	 * 
	 * レンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		// サーフェスから
		for( Polygon poly : polys ) {
			
			// マテリアル
			materials[poly.materialIndex].render( g );
			
			// 頂点
			for( int n : poly.posIndexies )
				g.fvbuf4.put( positions, n*3, 3 );
			
			// 法線
			for( int n : poly.normIndexies )
				g.fnbuf4.put( normals, n*3, 3 );
			
			// バッファの位置リセット
			g.fnbuf4.position(0);
			g.fvbuf4.position(0);
			
			g.gl.glEnableClientState( GL10.GL_NORMAL_ARRAY );
			g.gl.glNormalPointer( GL10.GL_FLOAT, 0, g.fnbuf4 );
			
			g.gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
			g.gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, g.fvbuf4 );
			
			
			g.gl.glDrawArrays( poly.type, 0, poly.posIndexies.length );
			
			g.gl.glDisableClientState( GL10.GL_COLOR_ARRAY );
			g.gl.glDisableClientState( GL10.GL_NORMAL_ARRAY );
			g.gl.glDisableClientState( GL10.GL_VERTEX_ARRAY );
			
		}
	}
}
