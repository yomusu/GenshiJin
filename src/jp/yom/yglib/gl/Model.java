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
public class Model implements YRenderer {

	// 当たり面(World座標)
	FSurface[]	surfaces;
	
	/** マテリアル */
	public Material	material = null;
	
	
	/** レンダリング用頂点座標バッファ */
	float[]	vertices = new float[4*3];
	float[]	normals  = new float[4*3];
	
	
	public Model( FSurface[] surfaces ) {
		
		this.surfaces = surfaces;
		
	}
	
	
	
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
		for( FSurface s : surfaces ) {
			
			normals[0] = s.normal.x;
			normals[1] = s.normal.y;
			normals[2] = s.normal.z;
			
			vertices[0] = s.p0.x;
			vertices[1] = s.p0.y;
			vertices[2] = s.p0.z;
			
			normals[3] = s.normal.x;
			normals[4] = s.normal.y;
			normals[5] = s.normal.z;
			
			vertices[3] = s.p1.x;
			vertices[4] = s.p1.y;
			vertices[5] = s.p1.z;
			
			normals[6] = s.normal.x;
			normals[7] = s.normal.y;
			normals[8] = s.normal.z;
			
			vertices[6] = s.p2.x;
			vertices[7] = s.p2.y;
			vertices[8] = s.p2.z;
			
			normals[9] = s.normal.x;
			normals[10] = s.normal.y;
			normals[11] = s.normal.z;
			
			vertices[9] = s.p3.x;
			vertices[10] = s.p3.y;
			vertices[11] = s.p3.z;
			
			g.drawPoly4( vertices, normals, null );
		}
	}
	
}
