package jp.yom.yglib.vector;



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
public class AtariObject {
	
	/** 自身の速度 */
	final public FVector	speed = new FVector(0,0,0);
	
	/** ボールの座標 */
	final public FPoint	pos = new FPoint();
	/** ひとつ前のボールの座標 */
	final public FPoint	p0 = new FPoint();
	
	
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
	
	
	public String toString() {
		
		StringBuilder	buf = new StringBuilder();
		buf.append("pos=").append(p0).append("(").append(pos).append(")");
		buf.append(" speed=").append(speed);
		
		return buf.toString();
	}
}
