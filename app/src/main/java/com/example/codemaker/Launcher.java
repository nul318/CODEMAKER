package com.example.codemaker;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class Launcher extends Activity {
	private BroadcastReceiver mRegistrationBroadcastReceiver; //token 받아오는 Broadcast
	Intent intent2;
	String token;



	private void getAppKeyHash() {
	    try {
	        PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md;
	            md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            String something = new String(Base64.encode(md.digest(), 0));
	            Log.d("Hash key", something);
	        }
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        Log.e("name not found", e.toString());
	    }
	}
//
//	@Override
//	protected void onResume() {
//	  super.onResume();
//
//	  // Logs 'install' and 'app activate' App Events.
//	  AppEventsLogger.activateApp(this);
//	}
//
//	@Override
//	protected void onPause() {
//	  super.onPause();
//
//	  // Logs 'app deactivate' App Event.
//	  AppEventsLogger.deactivateApp(this);
//	}


	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "Launcher";

	/**
	 * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	final Handler handler = new Handler();

	private void sendJsonDataToServer(final String token) throws UnsupportedEncodingException {

		new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String URL = "http://Server IP/phpcodemaker/laucher_parse.php";
				// 1
				HttpClient httpClient = new DefaultHttpClient();
				// 2
				HttpPost httpPost = new HttpPost(URL);
				// 3
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);


				try {
					nameValuePairs.add(new BasicNameValuePair("token", URLEncoder.encode(token, "UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}


				try {
					// 4
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// 5
					HttpResponse response = httpClient.execute(httpPost);
					// write response to log
					Log.d("Http Post Response:", response.toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// Log exception
					e.printStackTrace();
				} catch (IOException e) {
					// Log exception
					e.printStackTrace();
				}
			}
		}.start(); //로그인 ㄱㄱ


	}


	public void registBroadcastReceiver(){
		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, final Intent intent) {
				String action = intent.getAction();


				if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
					// 액션이 READY일 경우
					Log.d("TOKEN", "READY");
				} else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
					// 액션이 GENERATING일 경우
					Log.d("TOKEN", "GENERATING");
				} else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
					// 액션이 COMPLETE일 경우

					token = intent.getStringExtra("token");
					Log.d("TOKEN", token);
					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
//							Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
							intent.putExtra("token", token);

							try {
								sendJsonDataToServer(token);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}


						}
					});
//					Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
				}

			}
		};
	}


	public void getInstanceIdToken() {
		if (checkPlayServices()) {
			// Start IntentService to register this application with GCM.
			Intent intent = new Intent(Launcher.this, RegistrationIntentService.class);
			startService(intent);
		}
	}

	/**
	 * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
		LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
				new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

	}

	/**
	 * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
	 */
	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		super.onPause();
	}


	CallbackManager callbackManager; //페이스북에 필요한거


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		callbackManager = CallbackManager.Factory.create(); // callbackManager 생성



		requestWindowFeature(Window.FEATURE_NO_TITLE);
		FacebookSdk.sdkInitialize(this.getApplicationContext());



		setContentView(R.layout.launch);
//		this.getActionBar().hide();
// ;


//		intent2 = new Intent(Launcher.this, Loginpage.class);


		final RelativeLayout layout = (RelativeLayout) findViewById(R.id.launcher_layout);

		getAppKeyHash();

		registBroadcastReceiver();
		getInstanceIdToken();





//		final TextView txt0 = (TextView) findViewById(R.id.TextView09);
		final ImageView txt8 = (ImageView) findViewById(R.id.TextView08);

//		final TextView txt9 = (TextView) findViewById(R.id.author);


		final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button2); //facebook
		final Button resister = (Button) findViewById(R.id.resister2);
//		final CheckBox autocheck = (CheckBox) findViewById(R.id.autocheck2);
//		final Button submit = (Button) findViewById(R.id.submit2);
		final Button login_bt = (Button) findViewById(R.id.login_bt2); // 일반

		loginButton.setVisibility(View.INVISIBLE);
		resister.setVisibility(View.INVISIBLE);
//		autocheck.setVisibility(View.INVISIBLE);
//		submit.setVisibility(View.INVISIBLE);
		login_bt.setVisibility(View.INVISIBLE);






		View.OnClickListener listener2 = new View.OnClickListener() { //회원가입
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.resister_alert, null);
				final EditText id = (EditText) addLayout.findViewById(R.id.alert_id);
				final EditText pw = (EditText) addLayout.findViewById(R.id.alert_pw);
//			final EditText contents = (EditText) addLayout.findViewById(R.id.alert_contents);
				new AlertDialog.Builder(Launcher.this).setTitle("정보를 입력하세요.").setView(addLayout)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								String _id = id.getText().toString();
								String _pw = pw.getText().toString();
								if(!_id.equals("")) {
									try {
										sendJsonDataToServer3(_id, _pw);
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								else{
									Toast toast = Toast.makeText(getApplicationContext(),"id를 입력해주세요.", Toast.LENGTH_SHORT);
									toast.getView().setBackgroundResource(R.drawable.toast_drawable);
									toast.show();
								}
							}
						}).show();
			}


		};


		View.OnClickListener listener3 = new View.OnClickListener() { //로그인
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub




				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.resister_alert, null);
				final EditText id = (EditText) addLayout.findViewById(R.id.alert_id);
				final EditText pw = (EditText) addLayout.findViewById(R.id.alert_pw);
//			final EditText contents = (EditText) addLayout.findViewById(R.id.alert_contents);
				new AlertDialog.Builder(Launcher.this).setTitle("정보를 입력하세요.").setView(addLayout)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								final String _id = id.getText().toString();
								final String _pw = pw.getText().toString();



								if(!_id.equals("")) {

									new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // TODO Auto-generated method stub
                                                    sendPostRequest(_id, _pw);
                                                }
                                            });

                                        }
                                    }.start();


								}
								else{
									Toast toast = Toast.makeText(getApplicationContext(),"id를 입력해주세요.", Toast.LENGTH_SHORT);
									toast.getView().setBackgroundResource(R.drawable.toast_drawable);
									toast.show();
								}
							}
						}).show();
			}


		};

		resister.setOnClickListener(listener2);
		login_bt.setOnClickListener(listener3);






		loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				Log.e("TEST", "Result" + loginResult);
				Toast toast = Toast.makeText(Launcher.this, "Success", Toast.LENGTH_SHORT);

				toast.getView().setBackgroundResource(R.drawable.toast_drawable);
				toast.show();
//				shareDialog = new ShareDialog(act);
//				ShareLinkContent linkContent = new ShareLinkContent.Builder()
//						.setContentTitle("CODE MAKER")
//						.setContentDescription("코드로 음악을 만들자 CODE MAKER")
//						.build();
//				shareDialog.show(linkContent);

				Profile profile = Profile.getCurrentProfile(); //프로필 얻는 부분
				String Name = profile.getName();

				try {
					sendJsonDataToServer4(Name, "1", token);
					sendJsonDataToServer2(Name);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}


				Intent intent = new Intent(Launcher.this, MainActivity.class);
				intent.putExtra("id", Name);
				startActivity(intent);

				LoginManager.getInstance().logOut();


				finish();

//				act.finish();
			}

			@Override
			public void onCancel() {

				Toast toast =  Toast.makeText(Launcher.this, "cancel", Toast.LENGTH_SHORT);
				toast.getView().setBackgroundResource(R.drawable.toast_drawable);
				toast.show();

			}

			@Override
			public void onError(FacebookException error) {
				Log.e("TEST", "Error" + error);
				error.printStackTrace();
				Toast.makeText(Launcher.this, "exception", Toast.LENGTH_SHORT).show();

			}
		});















		final AnimationSet animSet = new AnimationSet(true);
		final AnimationSet animSet2 = new AnimationSet(true);

		Animation scaleZoom = AnimationUtils.loadAnimation(Launcher.this, R.anim.fade2);
		Animation scaleZoom2 = AnimationUtils.loadAnimation(Launcher.this, R.anim.fade);




		animSet.addAnimation(scaleZoom);

		animSet2.addAnimation(scaleZoom2);






		final Handler handler = new Handler();

		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub

//						txt0.startAnimation(animSet);
//						txt0.setVisibility(View.VISIBLE);


						txt8.startAnimation(animSet);
						txt8.setVisibility(View.VISIBLE);

//						txt9.startAnimation(animSet);
//						txt9.setVisibility(View.VISIBLE);
					}
				});
				try {
					Thread.sleep(4000); //700���̷� ��簡��
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub

						login_bt.startAnimation(animSet2);
						resister.startAnimation(animSet2);
//						autocheck.startAnimation(animSet2);
//						submit.startAnimation(animSet2);
						loginButton.startAnimation(animSet2);

						loginButton.setVisibility(View.VISIBLE);
						resister.setVisibility(View.VISIBLE);
//						autocheck.setVisibility(View.VISIBLE);
//						submit.setVisibility(View.VISIBLE);
						login_bt.setVisibility(View.VISIBLE);

					}
				});






//				startActivity(intent2);
//
//				finish();











			}
		}.start();



	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode,resultCode,data);
	} //페이스북






	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}

		return super.onKeyDown(keyCode, event);

	}


	private void sendJsonDataToServer3(final String id, final String pw) throws UnsupportedEncodingException { //회원가입하자

		new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String URL = "http://Server IP/phpcodemaker/resister_proc.php";
				// 1
				HttpClient httpClient = new DefaultHttpClient();
				// 2
				HttpPost httpPost = new HttpPost(URL);
				// 3
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				try {
					nameValuePairs.add(new BasicNameValuePair("id", URLEncoder.encode(id,"UTF-8")));
					nameValuePairs.add(new BasicNameValuePair("pw", URLEncoder.encode(pw,"UTF-8")));
				} catch (UnsupportedEncodingException e)  {
					e.printStackTrace();
				}
				try {
					// 4
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// 5
					HttpResponse response = httpClient.execute(httpPost);
					// write response to log
					Log.d("Http Post Response:", response.toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// Log exception
					e.printStackTrace();
				} catch (IOException e) {
					// Log exception
					e.printStackTrace();
				}
			}
		}.start(); //로그인 ㄱㄱ
	}

	private void sendJsonDataToServer4(final String user, final String login_check , final String token) throws UnsupportedEncodingException {

		new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String URL = "http://Server IP/phpcodemaker/login_session_update.php";
				// 1
				HttpClient httpClient = new DefaultHttpClient();
				// 2
				HttpPost httpPost = new HttpPost(URL);
				// 3
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);


				try {
					nameValuePairs.add(new BasicNameValuePair("user", URLEncoder.encode(user,"UTF-8")));
					nameValuePairs.add(new BasicNameValuePair("login_check", URLEncoder.encode(login_check,"UTF-8")));
					nameValuePairs.add(new BasicNameValuePair("token", URLEncoder.encode(token, "UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}


				try {
					// 4
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// 5
					HttpResponse response = httpClient.execute(httpPost);
					// write response to log
					Log.d("Http Post Response:", response.toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// Log exception
					e.printStackTrace();
				} catch (IOException e) {
					// Log exception
					e.printStackTrace();
				}
			}
		}.start(); //로그인 ㄱㄱ


	}


	private void sendJsonDataToServer2(final String user) throws UnsupportedEncodingException { //페북도 서버에 넣자 아이디

		new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String URL = "http://Server IP/phpcodemaker/add_facebook.php";
				// 1
				HttpClient httpClient = new DefaultHttpClient();
				// 2
				HttpPost httpPost = new HttpPost(URL);
				// 3
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				try {
					nameValuePairs.add(new BasicNameValuePair("user", URLEncoder.encode(user,"UTF-8")));
				} catch (UnsupportedEncodingException e)  {
					e.printStackTrace();
				}
				try {
					// 4
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					// 5
					HttpResponse response = httpClient.execute(httpPost);
					// write response to log
					Log.d("Http Post Response:", response.toString());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// Log exception
					e.printStackTrace();
				} catch (IOException e) {
					// Log exception
					e.printStackTrace();
				}
			}
		}.start(); //로그인 ㄱㄱ


	}



	private void sendPostRequest(final String user_id, final String user_pwd) {
		new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
			@Override
			public void run() {
				class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
					@Override
					protected String doInBackground(String... params){
						final String user_id = params[0];
						String user_pwd = params[1];
						Log.v("TAG", "백그라운드 작업   user_id=" + user_id + " user_pwd=" + user_pwd);
						HttpClient httpClient = new DefaultHttpClient();
						HttpPost httpPost = new HttpPost("http://Server IP/phpcodemaker/login_proc.php"); // 여러분의주소로변경할것
						BasicNameValuePair username = new BasicNameValuePair("id", user_id); // 서버의프로그램에따라달라질수있음
						BasicNameValuePair password = new BasicNameValuePair("pw", user_pwd);// 포스트로보내는변수
						List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
						nameValuePairList.add(username);
						nameValuePairList.add(password);
						try {
							UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList, "UTF-8");
							httpPost.setEntity(urlEncodedFormEntity);
							try {
								HttpResponse httpResponse = httpClient.execute(httpPost);
								InputStream inputStream = httpResponse.getEntity().getContent();
								InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
								BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
								final StringBuilder stringBuilder = new StringBuilder();
								String bufferedStrChunk = null;
								while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
									stringBuilder.append(bufferedStrChunk);
								}
								Log.v("TAG", stringBuilder.toString());///// <== 이곳에서
								///// 서버에서 보내온
								///// 메시지를 확인해 볼
								///// 수 있다..


								handler.post(new Runnable() {
												 @Override
												 public void run() {
													 Toast toast = Toast.makeText(getApplicationContext(), stringBuilder.toString(), Toast.LENGTH_SHORT);
													 toast.getView().setBackgroundResource(R.drawable.toast_drawable);
													 toast.show();

													 if(stringBuilder.toString().equals("true ")){//공백이 왜있는지 모르겠음
														 Intent intent = new Intent(Launcher.this, MainActivity.class);
														 intent.putExtra("id", user_id);
														 try {
															 sendJsonDataToServer4(user_id, "1", token);
														 } catch (UnsupportedEncodingException e) {
															 e.printStackTrace();
														 }
														 startActivity(intent);








														 finish();

													 }
												 }
											 }
								);


								////// 성공/실패 여부에 따라 적절히 대응하자.
								return stringBuilder.toString();

							}

							catch (ClientProtocolException cpe) {

								Log.v("TAG", "First Exception caz of HttpResponese :" + cpe);

								cpe.printStackTrace();

							} catch (IOException ioe) {

								Log.v("TAG", "Second Exception caz of HttpResponse :" + ioe);

								ioe.printStackTrace();

							}

						} catch (UnsupportedEncodingException uee) {

							Log.v("TAG", "An Exception given because of UrlEncodedFormEntity argument :" + uee);

							uee.printStackTrace();

						}

						return null;

					}

					protected void onPostExecute(String result)

					{

						super.onPostExecute(result);

					}/// onPostExecute

				}

				///////////////////////////////////

				// 이곳에서 로그인을 위한 웹문서를 비동기 백그라운드로 요청한다.

				SendPostReqAsyncTask sendTask = new SendPostReqAsyncTask();

				sendTask.execute(user_id, user_pwd); // 비동기 방식 백그라운드로 받아 오기.....

				///////////////////////////////////




			}}.start();

	}


}
