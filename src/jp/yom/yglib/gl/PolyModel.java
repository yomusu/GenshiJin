package jp.yom.yglib.gl;

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
	
	
	public static class PolyTriangleStrip {
		
		int[]	indexies;
		
		int		materialIndex;
		String	textureKey;
		
		public PolyTriangleStrip( int[] ind ) {
			
			this.indexies = ind;
			materialIndex = 0;
		}
	}
	
	public PolyTriangleStrip[]	polys;
	
	public float[][]	vertices;
	
	
	// 当たり面(World座標)
	FSurface[]	surfaces;
	
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
		for( PolyTriangleStrip s : polys ) {
			
			int	cnt = 0;
			for( int i : s.indexies ) {
				
				float[]	data = vertices[i];
				
				bufVertices[cnt] = data[0];
				bufVertices[cnt+1] = data[1];
				bufVertices[cnt+2] = data[2];
				
				bufNormals[cnt] = data[3];
				bufNormals[cnt+1] = data[4];
				bufNormals[cnt+2] = data[5];
				
				cnt += 3;
			}
			
			g.drawPoly4( bufVertices, bufNormals, null );
		}
	}
}
