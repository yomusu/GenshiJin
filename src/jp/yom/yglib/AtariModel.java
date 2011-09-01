package jp.yom.yglib;

import jp.yom.yglib.vector.FLine;
import jp.yom.yglib.vector.FMatrix;
import jp.yom.yglib.vector.FSurface;
import jp.yom.yglib.vector.FVector;

/********************************
 * 
 * 
 * 当たり判定を持つ物体を示す
 * 
 * 物体の特性を持つ
 * ・Model
 * ・運動エネルギーを与えた時の挙動
 * 
 * @author Yomusu
 *
 */
public class AtariModel {
	
	
	/** 自身の速度 */
	public FVector	speed;
	
	/** 当たり面(World座標) */
	public FSurface[]	surfaces;
	/** 当たり辺(World座標) */
	public FLine[]		lines;
	
	
	
	/********************************************
	 * 
	 * 当たりデータを読み込む
	 * 
	 */
	public void buildModel() {
		
	}
	
	/********************************************
	 * 
	 * アフィン変換を行う
	 * 
	 * @param mat
	 */
	public void transform( FMatrix mat ) {
		
		for( FSurface s : surfaces )
			s.transform( mat );
		
		for( FLine l : lines )
			l.transform( mat );
		
	}
	
	
	/********************************************
	 * 
	 * 力を加えられる
	 * 
	 */
	public void affectAction( FVector action ) {
		this.speed.add( action );
	}
	
}
