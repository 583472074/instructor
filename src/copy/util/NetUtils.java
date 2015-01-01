package copy.util;

import itstudio.instructor.config.Config;
import itstudio.instructor.http.TwitterRestClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.kjframe.KJHttp;
import org.kymjs.kjframe.http.HttpCallBack;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetUtils {

	public static final String RESULT_OK = "ok";
	public static final String RESULT_FAILED = "no";

	public static final String USER_AGENT = "golf data center";

	private static final String KEY_USERNAME = "kusername";
	private static final String KEY_PASSWORD = "kpassword";
	private static final String KEY_COUNTY = "kcountry";
	private static final String KEY_NICKNAME = "knickname";
	private static final String KEY_FRIENDS_LIST = "kflist";
	private static final String KEY_UPLOAD_INFO = "kupinfo";
	private static final String KEY_SEX = "ksex";
	private static final String KEY_GET_NAME_URL = "ksearchperson";
	private static final String KEY_UP_PHOTO = "file";
	private static final String KEY_CONTACTS = "contacts";
	private static final String KEY_PROVINCE = "kprovince";
	private static final String KEY_REGION = "kregion";
	private static final String KEY_CHADIAN = "kunder";



	/**
	 * 联网,获取phone对应的昵称和头像url,存内存
	 */
	public static void getNameAndUrl(final String phone, final ObjectRunnable success, final ExceptionRunnable failed) {

		RequestParams rr = new RequestParams(KEY_GET_NAME_URL, phone);
		rr.put("id", phone);
		System.out.println("phone"+phone);
		TwitterRestClient.post(Config.AC_FINDUSER, rr, new AsyncHttpResponseHandler() {
			public void onSuccess(int arg0, Header[] arg1, byte[] bs) {
				try {

					String json = new String(bs).trim();
					System.out.println("json"+json);
					Tools.log("获取头像url和姓名:" + json);
					JSONObject jo = new JSONObject(json);// 成功了,将json转化成Name_HeadUrl对象,传入到接口中
					String id = jo.getString("id");
					String name = jo.getString("name");
					String headUrl = jo.getString("headUrl");
					NameUrl nu = new NameUrl();
					nu.setName(name);
					nu.setHeadUrl(headUrl);
					nu.setId(phone);
					if (!Tools.isEmptyStr(name)) {
						NameUrlUtils.saveNameUrlToLocal(nu);
						success.run(nu);// 调用接口
					} else
						onFailure(0, null, new byte[] {}, null);

				} catch (JSONException e) {
					//failed(null, "获取数据失败", failed);
				}
			}

			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				//failed(null, null, failed);
			}
		});
	}





}
