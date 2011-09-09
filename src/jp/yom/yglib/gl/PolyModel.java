package jp.yom.yglib.gl;

import javax.microedition.khronos.opengles.GL10;

import jp.yom.yglib.vector.FSurface;




/**************************************************
 * 
 * 
 * モデルひとつを表すクラス
 * 
 * モデルひとつとは、
 * 
 * ・同じアフィン変換を使用する
 * ・同じ頂点リストを共有する
 * 
 * ・複数の面を使用する
 * ・複数のマテリアルを使用する
 * ・複数のテクスチャを使用する
 * 
 * 
 * 頂点リスト
 * マテリアルリスト
 * 
 * 【面の構成】
 * ・複数の頂点（3点もしくは4点をインデックスで）
 * ・マテリアル（インデックス）
 * ・テクスチャ（キー）
 * 
 * 【頂点の構成】
 * ・座標
 * ・法線
 * ・UV
 * 
 * @author Yomusu
 *
 */
public class PolyModel implements YRenderer {
	
	
	public static class Polygon {
		
		int		type;
		
		int[]	indexies;
		
		int		materialIndex;
		String	textureKey;
		
		public Polygon( int type, int[] ind ) {
			
			this.type = type;
			this.indexies = ind;
			materialIndex = 0;
		}
	}
	
	public Polygon[]	polys;
	
	public float[][]	vertices;
	
	
	/** マテリアル */
	public Material	material = null;
	
	
	/** レンダリング用頂点座標バッファ */
	float[]	bufVertices = new float[4*3];
	float[]	bufNormals  = new float[4*3];
	
	
	
	
	/**************************************************
	 * 
	 * レンダリング
	 * 
	 */
	@Override
	public void render(YGraphics g) {
		
		// マテリアル
		if( material!=null ) {
			material.render( g );
		}
		
		// サーフェスから
		for( Polygon poly : polys ) {
			
			int	cnt = 0;
			for( int i : poly.indexies ) {
				
				float[]	data = vertices[i];
				
				// 頂点
				g.fvbuf4.put( data, 0, 3 );
				// 法線
				g.fnbuf4.put( data, 3, 3 );
				
				cnt += 3;
			}
			
			// バッファの位置リセット
			g.fnbuf4.position(0);
			g.fvbuf4.position(0);
			
			
			g.gl.glEnableClientState( GL10.GL_NORMAL_ARRAY );
			g.gl.glNormalPointer( GL10.GL_FLOAT, 0, g.fnbuf4 );
			
			g.gl.glEnableClientState( GL10.GL_VERTEX_ARRAY );
			g.gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, g.fvbuf4 );
			
			
			if( poly.type==0 )
				g.gl.glDrawArrays( GL10.GL_TRIANGLE_STRIP, 0, poly.indexies.length );
			else
				g.gl.glDrawArrays( GL10.GL_TRIANGLE_FAN, 0, poly.indexies.length );
			
			g.gl.glDisableClientState( GL10.GL_COLOR_ARRAY );
			g.gl.glDisableClientState( GL10.GL_NORMAL_ARRAY );
			g.gl.glDisableClientState( GL10.GL_VERTEX_ARRAY );
			
		}
	}
}
