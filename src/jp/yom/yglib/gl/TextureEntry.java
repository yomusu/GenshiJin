package jp.yom.yglib.gl;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

/***********************************************
 * 
 * 
 * テクスチャの準備を行うクラス
 * 
 * @author matsumoto
 *
 */
public class TextureEntry {
	
	public final String	key;
	private	Bitmap	bmp = null;
	
	/** リソースID */
	public final int	resID;
	
	/** バインドされたID */
	public Integer	bindID = null;
	
	
	public TextureEntry( String key, int resID ) {
		
		this.key = key;
		this.resID = resID;
	}
	
	/***************************************************
	 * 
	 * ビットマップを取得する
	 * 
	 * @return	loadされてないときnull
	 */
	public Bitmap getBitmap() {
		return bmp;
	}
	
	/***************************************************
	 * 
	 * ビットマップデータを読み込みます
	 * 
	 * @param res
	 * @param resID
	 */
	public void loadBitmap( Resources res ) {
		
		BitmapFactory.Options	options = new BitmapFactory.Options();
		options.inScaled = false;
		options.inPreferredConfig = Config.ARGB_8888;
		
		bmp = BitmapFactory.decodeResource( res, resID, options );

	}
	
	
	/***************************************************
	 * 
	 * 読み込んだビットマップデータを解放します
	 * 
	 */
	public void disposeBitmap() {
		if( bmp!=null ) {
			bmp.recycle();
			bmp = null;
		}
	}
}
