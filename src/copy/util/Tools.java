package copy.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 工具类
 * 
 * @author yzx
 * 
 */
public class Tools {
	private static Context context;
	private static Toast to;
	private static AlertDialog ad;
	/**
	 * 线程池最大并发数
	 */
	public static final int THREAD_POOL_MAX = 15;

	public static final int CAMERA = 1;
	public static final int PICLIB = 2;
	public static final int CUT = 3;

	/**
	 * 初始化注入Context对象
	 */
	public static void setContext(Context context) {
		Tools.context = context;
	}

	/**
	 * 显示Toast
	 * 
	 * @param str
	 *            要显示的内容
	 */
	public static void showToast(final String str) {
		if (str == null)
			return;
		if (to == null)
			to = Toast.makeText(context, str, Toast.LENGTH_SHORT);
		else
			to.setText(str);
		to.show();
	}

	/**
	 * log (user error)
	 * 
	 * @param str
	 */
	public static void log(String str) {
		if (str != null)
			Log.e("log--->", str);
	}

	/**
	 * 启动一个新的Activity
	 * 
	 * @param from
	 *            起始Activity
	 * @param to
	 *            跳转Activity
	 */
	public static void goActivity(Context from, Class<? extends Activity> to, String[] keys, String[] values) {
		Intent in = new Intent(from, to);
		if (keys != null && values != null)
			for (int i = 0; i < keys.length; i++)
				in.putExtra(keys[i], values[i]);
		from.startActivity(in);
	}

	/**
	 * 显示一个dialog
	 * 
	 * @param view
	 *            dialog的contentView
	 * @param x
	 *            显示的x坐标
	 * @param y
	 *            显示的y坐标
	 * @param width
	 *            dialog的宽度
	 * @param height
	 *            dialog的高度
	 * @param animation
	 *            显示和消失的动画效果
	 * @param cancleAble
	 *            是否可以cancle掉
	 * @return Dialog本身,返回之前已经show()了
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
/*	public static AlertDialog showDialog(Context context, View view, int x, int y, int width, int height, int animation, boolean cancleAble, boolean isAnimation) {
		AlertDialog ad;
		if (SystemTool.getSDKVersion() >= 11 && x == ConstantValues.screenX)
			ad = new AlertDialog.Builder(context, R.style.no_dark).create();
		else
			ad = new AlertDialog.Builder(context).create();
		ad.setCancelable(cancleAble);
		LayoutParams lp = ad.getWindow().getAttributes();
		lp.x = x;
		lp.y = y;
		ad.show();
		ad.setContentView(view);
		ad.getWindow().setLayout(width, height);
		if (isAnimation) {
			if (animation != 0)
				ad.getWindow().setWindowAnimations(animation);
			else
				ad.getWindow().setWindowAnimations(R.style.head_in_out);
		}
		ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		return ad;
	}*/

	/**
	 * 使用SharedPreferences存储值
	 * 
	 * @param key
	 *            存储值的key
	 * @param value
	 *            存储值的value
	 * @param fileName
	 *            文件名称
	 */
	public static void saveValues(String key, String value) {
		context.getSharedPreferences("default_golfer_sp", Context.MODE_PRIVATE).edit().putString(key, value).commit();
	}

	/**
	 * 使用SharedPreferences取值
	 * 
	 * @param key
	 *            key
	 * @param fileName
	 *            xml文件名称
	 * @return 如果值不存在 ,返回空串
	 */
	public static String getValues(String key) {
		return context.getSharedPreferences("default_golfer_sp", Context.MODE_PRIVATE).getString(key, "");
	}

	private static ScheduledThreadPoolExecutor threadPool;

	public static ScheduledThreadPoolExecutor getThreadPool() {
		if (threadPool == null)
			threadPool = new ScheduledThreadPoolExecutor(10);
		return threadPool;
	}

	/**
	 * 线程池
	 * 
	 * @param run
	 */
	public static void exec(Runnable run) {
		if (threadPool == null)
			threadPool = new ScheduledThreadPoolExecutor(THREAD_POOL_MAX);
		threadPool.execute(run);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

/*	*//**
	 * 显示一个提示性的dialog
	 * 
	 * @param context
	 * @param titleImg
	 *            左上角的图标
	 * @param title
	 *            title文字内容
	 * @param message
	 *            提示内容
	 * @param buttonclick
	 *            点击确定按钮要做的事情
	 * @return
	 *//*
	public static void showNoticeDialog(Context context, int titleImg, String title, String message, final Runnable buttonclick) {
		CustomDialog.Builder ibuilder;
		ibuilder = new CustomDialog.Builder(context);
		ibuilder.setTitle(title);
		ibuilder.setMessage(message);
		ibuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				buttonclick.run();
				dialog.dismiss();
			}
		});
		ibuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ibuilder.create().show();
	}

	public static void showNoticeDialog(Context context, int titleImg, String title, String message, final Runnable buttonclick1, final Runnable buttonclick2, String b1Str,
			String b2Str) {
		CustomDialog.Builder ibuilder;
		ibuilder = new CustomDialog.Builder(context);
		ibuilder.setTitle(title);
		ibuilder.setMessage(message);
		ibuilder.setPositiveButton(R.string.disagree, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				buttonclick1.run();
				dialog.dismiss();
			}
		});
		ibuilder.setNegativeButton(R.string.agree, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				buttonclick2.run();
				dialog.dismiss();
			}
		});
		ibuilder.create().show();
	}*/

	/**
	 * 检测网络是否可用
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null)
				return mNetworkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * md5算法
	 * 
	 * @param text
	 *            一个字符串
	 * @return md5值 失败了返回null
	 */
	public static String MD5(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] bs = md.digest();
			int temp;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < bs.length; i++) {
				temp = bs[i];
				if (temp < 0)
					temp += 256;
				if (temp < 16) {
					sb.append("0");
				}
				sb.append(Integer.toHexString(temp) + "");
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

/*	*//**
	 * 弹出提示用户等待的dialog
	 *//*
	public static void showWaitDialog(final Context context) {
		if (ad != null && ad.isShowing())
			ad.dismiss();
		ad = showDialog(context, View.inflate(context, R.layout.dialog_wait, null), 0, 0, ConstantValues.screenX / 2, ConstantValues.ScreenY / 7, 0, true, false);
		ad.setCanceledOnTouchOutside(false);
		ad.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				Client.getInstance().cancelRequests(context, true);
			}
		});
	}

	*//**
	 * 弹出提示用户等待的dialog
	 *//*
	public static Dialog showDialog(final Context context) {
		if (ad != null && ad.isShowing())
			ad.dismiss();
		ad = showDialog(context, View.inflate(context, R.layout.dialog_wait, null), 0, 0, ConstantValues.screenX / 2, ConstantValues.ScreenY / 7, 0, true, false);
		ad.setCanceledOnTouchOutside(false);
		ad.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				Client.getInstance().cancelRequests(context, true);
			}
		});
		return ad;
	}*/

	/**
	 * 取消等待的dialog
	 */
	public static void dismissWaitDialog() {
		if (ad != null)
			ad.dismiss();
	}

	public static boolean isEmptyStr(String str) {
		return (str == null || str.trim().length() < 1);
	}

	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.obj != null) {
				Runnable r = (Runnable) msg.obj;
				r.run();
			}
		};
	};

	/**
	 * 主线程回到使用
	 */
	public static void callBack(Runnable run) {
		Message me = handler.obtainMessage();
		me.obj = run;
		handler.sendMessage(me);
	}

	/**
	 * 在主线程中执行
	 */
	public static void runOnUiThread(Runnable run, Activity a) {
		a.runOnUiThread(run);
	}


	/**
	 * 弹出输入法
	 */
	public static void showSoftInput(EditText et) {
		SystemClock.sleep(100);
		InputMethodManager is = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		is.showSoftInput(et, 0);
	}

	/**
	 * 隐藏输入法
	 */
	public static void hideSoftInput(EditText view) {
		InputMethodManager is = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		is.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static boolean isSoftInputShowing() {
		InputMethodManager is = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		return is.isActive();
	}


	/**
	 * 跳转到系统图库
	 *//*
	public static void goPicLib(Activity a, int requestCode) {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		a.startActivityForResult(i, requestCode);
	}

	public static String getAutoName() {
		return MD5(System.currentTimeMillis() + "");
	}

	*//**
	 * 将图片转化成base64编码
	 *//*
	public static String bitmapToBase64Str(Bitmap b) {
		if (b == null)
			return null;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		b.compress(CompressFormat.PNG, 100, bao);
		try {
			bao.flush();
			return Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT);
		} catch (IOException e) {
			return null;
		} finally {
			b.recycle();
		}
	}

	*//**
	 * 将file转化成base64编码
	 *//*
	public static String fileToBase64Str(String path) {
		if (path == null)
			return null;
		try {
			File file = new File(path);
			FileInputStream inputFile = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return Base64.encodeToString(buffer, Base64.DEFAULT);
		} catch (IOException e) {
			return null;
		}
	}

	*//**
	 * 跳转到系统裁剪图片的界面
	 * 
	 * @param uri
	 *            图片资源的uri
	 *//*
	public static void goCut(File file, Activity a, int outPutX, int outPutY) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outPutX);
		intent.putExtra("outputY", outPutY);
		intent.putExtra("return-data", true);
		a.startActivityForResult(intent, CUT);
	}

	public static String fillURL(String url) {
		return url.startsWith("/") ? (MyURL.HOME + url) : url;
	}

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String yyyymmddhhmmssToTimeAgo(String time) {
		try {
			Date dd = ss.parse(time);
			long c = System.currentTimeMillis() - dd.getTime();
			long chafen = c / 1000 / 60;// minute

			if (chafen <= 1)
				return "刚刚";

			if (chafen < 60)
				for (int i = 3; i < 60; i++) {
					if (chafen < i)
						return new StringBuffer().append(i).append("分钟前").toString();
				}

			long chaxiaoshi = chafen / 60;
			if (chaxiaoshi <= 23)
				for (int i = 1; i <= 23; i++) {
					if (chaxiaoshi <= i)
						return new StringBuffer().append(i).append("小时前").toString();
				}

			long chaday = chaxiaoshi / 24;
			if (chaday < 30)
				for (int i = 1; i < 30; i++) {
					if (chaday <= i)
						return new StringBuffer().append(i).append("天前").toString();
				}

			long chayue = chaday / 30;
			if (chayue < 12)
				for (int i = 1; i < 12; i++) {
					if (chayue <= i)
						return new StringBuffer().append(i).append("个月前").toString();
				}

			long chanian = chayue / 12;
			if (chanian <= 1)
				return new StringBuffer().append(1).append("年以前").toString();
			else
				return new StringBuffer().append(chanian).append("年以前").toString();

		} catch (ParseException e) {
			return "";
		}
	}

	private static AlertDialog proAd;
	private static ProgressBar pb;
	private static TextView ptv;

	public static void showProgressWaitDialog(String pro, Activity activity) {
		if (proAd == null) {
			View vv = View.inflate(activity, R.layout.progress_dialog, null);
			pb = (ProgressBar) vv.findViewById(R.id.pb);
			ptv = (TextView) vv.findViewById(R.id.tv);
			proAd = Tools.showDialog(activity, vv, 0, 0, ConstantValues.screenX - 32, ConstantValues.ScreenY / 7, 0, false, true);
		}
		pb.setProgress(Integer.valueOf(pro));
		ptv.setText(pro + "%");
	}

	public static void dismissProgressWaitDialog() {
		if (proAd != null)
			proAd.dismiss();
		proAd = null;
		pb = null;
		ptv = null;
	}*/



}
