package cn.nstreet.baijie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import cn.nstreet.baijie.util.StringUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

@SuppressLint("NewApi")
public class AppConfig {

	private final static String APP_CONFIG = "config";

	public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";
	public final static String CONF_COOKIE = "cookie";
	public final static String CONF_ACCESSTOKEN = "accessToken";
	public final static String CONF_ACCESSSECRET = "accessSecret";
	public final static String CONF_EXPIRESIN = "expiresIn";
	public final static String CONF_LOAD_IMAGE = "perf_loadimage";
	public final static String CONF_SCROLL = "perf_scroll";
	public final static String CONF_HTTPS_LOGIN = "perf_httpslogin";
	public final static String CONF_VOICE = "perf_voice";
	public final static String CONF_CHECKUP = "perf_checkup";

	public final static String SAVE_IMAGE_PATH = "save_image_path";
	@SuppressLint("NewApi")
	public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory()+ File.separator+ "BaiJieLife"+ File.separator;
			
	private Context mContext;
	private static AppConfig appConfig;

	public static AppConfig getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfig();
			appConfig.mContext = context;
		}
		return appConfig;
	}

	/**
	 * 获取Preference设置
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * 是否加载显示文章图片
	 */
	public static boolean isLoadImage(Context context) {
		return getSharedPreferences(context).getBoolean(CONF_LOAD_IMAGE, true);
	}

	public String getCookie() {
		return getByKey(CONF_COOKIE);
	}

	public void setAccessToken(String accessToken) {
		setKeyValue(CONF_ACCESSTOKEN, accessToken);
	}

	public String getAccessToken() {
		return getByKey(CONF_ACCESSTOKEN);
	}

	public void setAccessSecret(String accessSecret) {
		setKeyValue(CONF_ACCESSSECRET, accessSecret);
	}

	public String getAccessSecret() {
		return getByKey(CONF_ACCESSSECRET);
	}

	public void setExpiresIn(long expiresIn) {
		setKeyValue(CONF_EXPIRESIN, String.valueOf(expiresIn));
	}

	public long getExpiresIn() {
		return StringUtils.toLong(getByKey(CONF_EXPIRESIN));
	}
	
	public void setKeyValue(String key, String value) {
		Properties props = getProperties();
		props.setProperty(key, value);
		setProperties(props);
	}

	public String getByKey(String key) {
		Properties props = getProperties();
		return (props != null) ? props.getProperty(key) : null;
	}

	public Properties getProperties() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			// 读取files目录下的config
			// fis = activity.openFileInput(APP_CONFIG);

			// 读取app_config目录下的config
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			fis = new FileInputStream(dirConf.getPath() + File.separator
					+ APP_CONFIG);

			props.load(fis);
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	public void setProperties(Properties p) {
		FileOutputStream fos = null;
		try {
			// 把config建在files目录下
			// fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

			// 把config建在(自定义)app_config的目录下
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			File conf = new File(dirConf, APP_CONFIG);
			fos = new FileOutputStream(conf);

			p.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

//	public void set(Properties ps) {
//		Properties props = getProperties();
//		props.putAll(ps);
//		setProperties(props);
//	}

	public void removeByKeys(String... key) {
		Properties props = getProperties();
		for (String k : key) {
			props.remove(k);
		}
		setProperties(props);
	}
}
