package com.example.codemaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class Board extends Activity {



//	public void showSTORAGE() {
////		         Log.i(TAG, "Show camera button pressed. Checking permission.");
//		         // BEGIN_INCLUDE(camera_permission)
//		         // Check if the Camera permission is already available.
//		         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//		                 != PackageManager.PERMISSION_GRANTED) {
//			             // Camera permission has not been granted.
//			             requestSTORAGEPermission();
//			         } else {
//			             // Camera permissions is already available, show the camera preview.
////			             Log.i("TAG", "CAMERA permission has already been granted. Displaying camera preview.");
////			             showCameraPreview();
//					 	requestSTORAGEPermission();
//			        }
//		         // END_INCLUDE(camera_permission)
//
//
//		  }
//	@Override
//	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//										   @NonNull int[] grantResults) {
//		if (requestCode == 0) {
//			// BEGIN_INCLUDE(permission_result)
//			// Received permission result for camera permission.
//			Log.i("TAG : ", "Received response for Camera permission request.");
//
//
//			// Check if the only required permission has been granted
//			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//				// Camera permission has been granted, preview can be displayed
//				Log.i("TAG : ", "CAMERA permission has now been granted. Showing preview.");
////				Snackbar.make(mLayout, R.string.permision_available_camera,
////						Snackbar.LENGTH_SHORT).show();
//			} else {
////				Log.i(TAG, "CAMERA permission was NOT granted.");
////				Snackbar.make(mLayout, R.string.permissions_not_granted,
////						Snackbar.LENGTH_SHORT).show();
//
//
//			}
//			// END_INCLUDE(permission_result)
//
//
//		} else {
//			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//		}
//	}
//
//
//
//	private void requestSTORAGEPermission() {
////		         Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");
//		         // BEGIN_INCLUDE(camera_permission_request)
//		         if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//				                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//			             // Provide an additional rationale to the user if the permission was not granted
//			             // and the user would benefit from additional context for the use of the permission.
//			// For example if the user has previously denied the permission.
////			             Log.i(TAG, "Displaying camera permission rationale to provide additional context.");
//					 ActivityCompat.requestPermissions(Board.this,
//							 new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//			         } else {
//
//			             // Camera permission has not been granted yet. Request it directly.
//			             ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},	 0);
//			         }
//		        // END_INCLUDE(camera_permission_request)
//		     }
//
//



	SwipeRefreshLayout mSwipeRefreshLayout;









	final ArrayList<MyFile> File_list = new ArrayList<MyFile>();

	myAdapter Adapter;
	Handler handler = new Handler();

	private void sendJsonDataToServer(String title) throws UnsupportedEncodingException {

		String URL = "http://Server IP/phpcodemaker/del_proc.php";
		// 1
		HttpClient httpClient = new DefaultHttpClient();
		// 2
		HttpPost httpPost = new HttpPost(URL);
		// 3
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("title", URLEncoder.encode(title,"UTF-8")));



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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		 this.getActionBar().hide();


		final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
													 @Override
													 public void onRefresh() {
														 new Handler().postDelayed(new Runnable() {
															 @Override
															 public void run() {
																 mSwipeRefreshLayout.setRefreshing(false);
																 Intent intent = new Intent(Board.this, Board.class);
																 //startActivity(intent);
																 startActivity(intent); // 액티비티실행하고 결과받아오는
																 // 함수!! 미친 개꿀
																 finish();
															 }
														 }, 1000);
													 }
												 }
			);





		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);//네트워크 에러났는데 이렇게하면 댄대 ㅇㅅㅇ 권한문제인듯

		final phpDown task = new phpDown();
		task.execute("http://Server IP/phpcodemaker/index.php");
//		
		// 어댑터 준비


		ImageView send_server = (ImageView) findViewById(R.id.send_server);

		send_server.setOnClickListener(new View.OnClickListener() {
										   @Override
										   public void onClick(View v) {
											   String user_id = getIntent().getStringExtra("id");

											   Intent intent = new Intent(Board.this, Post_to_server.class);
											   intent.putExtra("id", user_id);

											   startActivity(intent);
											   finish();

										   }
									   }
		);


				ListView list = (ListView) findViewById(R.id.filelist);



		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				new FileDownloadTask().execute("http://Server IP/phpcodemaker/upload/"+File_list.get(position).Fileurl);
				//http방식 너무 힘듬 시발 되긴되는데 안되는 경우가 많음


//				showSTORAGE();



//
//				//---------------------아래로는 ftp 다운로드 방식----------------------
//				try {
//					FTPGetFile(File_list.get(position).Fileurl);
//				} catch (SocketException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

			}

		});//값을 받아올땐 Resume 에서 다시 시작해야하므로 ㅇㅇ resume 에다 작성






		list.setOnItemLongClickListener(new OnItemLongClickListener(){ // 길게누르면 수정, 삭제 가 나와야겠지! 해해해해
			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Board.this)
						//.setTitle("alert")

						.setNegativeButton("수정", new DialogInterface.OnClickListener() { //수정버튼

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),"수정",Toast.LENGTH_SHORT).show();


//
//								LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//								LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.post_alert, null);
//
//								final EditText password = (EditText) addLayout.findViewById(R.id.password);
//
//								new AlertDialog.Builder(Board.this).setTitle("패스워드를 입력하세요.").setView(addLayout)
//										.setNeutralButton("확인", new DialogInterface.OnClickListener() {
//
//											@Override
//											public void onClick(DialogInterface dialog, int which) {
//												// TODO Auto-generated method stub
//
//												//수정 삭제할때 패스워드 입력
//												if(password.getText().toString().equals(File_list.get(position).Password)){
//
//													Intent intent = new Intent(Board.this, Post_to_server_modify.class);
//													//startActivity(intent);
//													intent.putExtra("title", File_list.get(position).Title.toString());
//													intent.putExtra("author", File_list.get(position).SubTitle.toString());
//													intent.putExtra("contents", File_list.get(position).Contents.toString());
//													intent.putExtra("fileurl", File_list.get(position).Fileurl.toString());
//
//													startActivity(intent); // 액티비티실행하고 결과받아오는
//													// 함수!! 미친 개꿀
//													finish();
//												}
//											}
//										}).show();

							}
						})

						.setNeutralButton("쪽지", new DialogInterface.OnClickListener() {


							@Override
							public void onClick(DialogInterface dialog, int which) {
//								Toast.makeText(getApplicationContext(), "구현중", Toast.LENGTH_SHORT);


								String user_id = getIntent().getStringExtra("id");
								Intent intent = new Intent(Board.this, Message.class); //일단 새로고침을 이런식으로 ㅎㅎㅎㅎ...
								intent.putExtra("id", user_id);
								startActivity(intent);
								finish();


							}
						})

								// TODO Auto-generated method stub						})
						.setPositiveButton("삭제", new DialogInterface.OnClickListener() { //삭제버튼

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

								LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.post_alert, null);

								final EditText password = (EditText) addLayout.findViewById(R.id.password);

								new AlertDialog.Builder(Board.this).setTitle("패스워드를 입력하세요.").setView(addLayout)
										.setNeutralButton("확인", new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog, int which) {
												// TODO Auto-generated method stub

												//수정 삭제할때 패스워드 입력
												if(password.getText().toString().equals(File_list.get(position).Password)){


													new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
														@Override
														public void run() {
															// TODO Auto-generated method stub


															handler.post(new Runnable() {
																@Override
																public void run() {
																	// TODO Auto-generated method stub
																	try {
																		sendJsonDataToServer(File_list.get(position).Title.toString());
																		Intent intent = new Intent(Board.this, Board.class); //일단 새로고침을 이런식으로 ㅎㅎㅎㅎ...
																		startActivity(intent);
																		finish();

																	} catch (UnsupportedEncodingException e) {
																		// TODO Auto-generated catch block
																		e.printStackTrace();
																	}

																}
															});

														}
													}.start();

												}
											}
										}).show();





							}
						})
						.show();

				return false;
			}

		});


		Adapter = new myAdapter(this, R.layout.file, File_list);

		// 어댑터 연결


		list.setAdapter(Adapter);

		// 리스트뷰 속성(없어도 구현 가능)
		// 항목을 선택하는 모드
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// 항목 사이의 구분선 지정
		// list.setDivider(new ColorDrawable(Color.WHITE)); //xml 에서 직접 지정함
		// 구분선의 높이 지정
		list.setDividerHeight(4);

		final AnimationSet animSet = new AnimationSet(true);
		Animation fade = AnimationUtils.loadAnimation(Board.this, R.anim.fade);
		animSet.addAnimation(fade); //애니메이션 추가
		list.startAnimation(animSet);




	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { //받아오면 여기로 이동!!
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);


	}


	private class phpDown extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... urls) {
			StringBuilder jsonHtml = new StringBuilder();
			try {
				// 연결 url 설정
				URL url = new URL(urls[0]);
				// 커넥션 객체 생성
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				// 연결되었으면.
				if (conn != null) {
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					// 연결되었음 코드가 리턴되면.
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						for (;;) {
							// 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
							String line = br.readLine();
							if (line == null)
								break;
							// 저장된 텍스트 라인을 jsonHtml에 붙여넣음
							jsonHtml.append(line + "\n");
						}
						br.close();
					}
					conn.disconnect();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return jsonHtml.toString();

		}

		protected void onPostExecute(String str) {


			String title;
			String author;
			String fileurl;
			String contents;
			String password;
			try {
				JSONObject root = new JSONObject(str);
				JSONArray ja = root.getJSONArray("results");
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					title = jo.getString("TITLE");
					author = jo.getString("Author");
					fileurl = jo.getString("Fileurl");
					contents = jo.getString("Contents");
					password = jo.getString("Password");

					File_list.add(new MyFile(title, author, fileurl, contents, password));

				}
				final Handler handler = new Handler();
				handler.post(new Runnable() {
					@Override
					public void run() {
						Adapter.notifyDataSetChanged();




					}
				});

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.clear();
		menu.add(0, 1, 0, "자랑하기");


		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == 1) {
			String user_id = getIntent().getStringExtra("id");

			Intent intent = new Intent(Board.this, Post_to_server.class);
			intent.putExtra("id", user_id);

			startActivity(intent);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class myAdapter extends BaseAdapter {

		Context con; // 이미지를 받는곳
		LayoutInflater inflater;
		ArrayList<MyFile> file_list;
		int layout;

		myAdapter(Context context, int layout, ArrayList<MyFile> file_list) {
			con = context;
			this.layout = layout;
			this.file_list = file_list;
			inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			// 멤버변수 초기화
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return file_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return file_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (null == convertView) {
				convertView = inflater.inflate(layout, parent, false); // cells
				// 를 뷰화
				// 시켜서
				// 아이템목록으

			} // 비어있을때만 연결!!
			// view를 스크롤올릴때마다 계속 재 생성하는 것을 방지하기위해 최초에 한번만 생성! 근데 새로고침에 오류가나서
			// 일단 이렇게쓰자
			TextView txt = (TextView) convertView.findViewById(R.id.title);
			TextView txt2 = (TextView) convertView.findViewById(R.id.subtitle);
			TextView txt3 = (TextView) convertView.findViewById(R.id.imgurl);
			TextView txt4 = (TextView) convertView.findViewById(R.id.contents);

			txt.setText(file_list.get(position).Title);
			txt.setTextSize(20.0f);
			txt2.setText(file_list.get(position).SubTitle);
			txt3.setText(file_list.get(position).Fileurl);
			txt4.setText(file_list.get(position).Contents);

			return convertView;
		}
	}




//	서버에서 파일 다운로드하기
//	ftp에서 파일을 가져오는 부분이다.

	public boolean FTPGetFile(String fileName) throws SocketException, IOException{ //다운로드ftp!!

		FileOutputStream os;
		FTPClient mFtp = new FTPClient();


		File f;


		mFtp.connect("Server IP", 21);
		mFtp.login("root", "password");
		mFtp.setFileType(FTP.BINARY_FILE_TYPE);
		mFtp.enterLocalPassiveMode();



		try {

			mFtp.changeWorkingDirectory("/var/www/html/phpcodemaker/upload/");

			// 일단 거기에있는파일들을 가져온다.

			FTPFile files[] = mFtp.listFiles();

			// 모든파일가져오는 과정에서 fileName이있으면 그것을가져와 내꺼에저장한다.

			for (int i = 0; i < files.length; i++) {

				if (files[i].getName().equals(fileName)) {

					// 내가 저장할 폴더의 루트로 파일가져올것이다.
					File DIR = new File(Environment.getExternalStorageDirectory().toString() + "/CODEMAKER/download/");
					if (!DIR.exists())
						DIR.mkdirs();

					f = new File(DIR +"/"+ files[i].getName());

					// 값을 써준다.

					os = new FileOutputStream(f);

					// 이것이 핵심적으로 파일을 다운로드하는 로직

					mFtp.retrieveFile(files[i].getName(), os);

					os.close();
					mFtp.disconnect();
					Toast toast = Toast.makeText(getApplicationContext(), DIR +"/"+ files[i].getName() + " 에 저장 완료", Toast.LENGTH_SHORT);


					toast.getView().setBackgroundResource(R.drawable.toast_drawable);
					toast.show();

					//음악파일 실행
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
					intent.addCategory(intent.CATEGORY_DEFAULT);
					File file = new File(DIR +"/"+ files[i].getName());
					intent.setDataAndType(Uri.fromFile(file), "audio/*");
					startActivity(intent);


				}

			}

		} catch (IOException e) {


			return false;

		}

		return true;

	}








	private class FileDownloadTask extends AsyncTask<String, Void, File> {

		private boolean isDownloadCompleted = false;
		URL url = null;

		@Override
		protected void onPreExecute() {
			Toast toast = Toast.makeText(Board.this, "다운로드 중입니다.", Toast.LENGTH_SHORT);

			toast.getView().setBackgroundResource(R.drawable.toast_drawable);
			toast.show();
		}

		@Override
		protected File doInBackground(String... params) {

			HttpURLConnection connection = null;
			File downloadedFile = null;

			try {
				// 주소로부터 URL 객체 생성하고, 서버와 연결 고리 생성
				url = new URL(params[0]);
				connection = (HttpURLConnection)url.openConnection();

				// 연결 관련 설정
				connection.setRequestMethod("GET");
				connection.setUseCaches(false);
				connection.setConnectTimeout(10000);

				// 서버에 텍스트 다운로드 요청
				int resCode = connection.getResponseCode();


				Thread.sleep(5000); //여기서 잠깐 멈춰야 하는듯 이해불가
				if (HttpURLConnection.HTTP_OK == resCode) {
					downloadedFile = getFileFrom(
							connection,
							Environment.getExternalStorageDirectory().toString() + "/CODEMAKER/download/",
							FileManager.getFileName(url)
					);
//                    Thread.sleep(5000);

				} else {
					downloadedFile = null;
					this.isDownloadCompleted = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				downloadedFile = null;
				this.isDownloadCompleted = false;
			}

			if (null != connection) {
				connection.disconnect();
			}






			return downloadedFile;    // null일 수 있습니다.
		}

		@Override
		protected void onPostExecute(File result) {
			if (null == result) {
				Toast toast = Toast.makeText(Board.this, "다운로드할 수 없습니다.", Toast.LENGTH_SHORT);

				toast.getView().setBackgroundResource(R.drawable.toast_drawable);
				toast.show();
				return;
			}
			else if (!this.isDownloadCompleted) {
				Toast toast = Toast.makeText(Board.this, "다운로드가 완료되지 못 했습니다. 네트워크를 확인해 주세요.", Toast.LENGTH_SHORT);

				toast.getView().setBackgroundResource(R.drawable.toast_drawable);
				toast.show();
				return;
			}

			// -----------------------------------------------------------------
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			intent.addCategory(intent.CATEGORY_DEFAULT);
			File file = new File(Environment.getExternalStorageDirectory().toString() + "/CODEMAKER/download/" + FileManager.getFileName(url));
			intent.setDataAndType(Uri.fromFile(file), "audio/*");
			startActivity(intent);
			// ----------------------------------------------------------------- mp3 파일 열기

			Toast toast = Toast.makeText(Board.this, "다운로드를 완료했습니다.", Toast.LENGTH_SHORT);

			toast.getView().setBackgroundResource(R.drawable.toast_drawable);
			toast.show();
		}

		/**
		 * 연결된 서버로부터 파일을 다운로드 받습니다.
		 * @param connection 서버와 연결을 담당. 서버로부터 파일의 내용을 받기 위한 입력 스트림을 얻을 수 있습니다.
		 * @param path 다운로드 받을 파일의 경로
		 * @param fileName 다운로드 받을 파일의 이름
		 * @return 다운로드 받을 파일 객체
		 */
		private File getFileFrom(HttpURLConnection connection, String path, String fileName) {
			InputStream inputFromServer = null;

			// 경로가 존재하지 않으면, 생성합니다.
			File fileAsDownloadPath = new File(path);
			if (!fileAsDownloadPath.exists()) {
				fileAsDownloadPath.mkdirs();
			}

			File downloadedFile = new File(path + '/' + fileName);
			FileOutputStream outputToFile = null;

			try {
				// 다운로드 파일을 생성합니다.
				if (!downloadedFile.exists()) {
					downloadedFile.createNewFile();
				}

				inputFromServer = connection.getInputStream();
				outputToFile = new FileOutputStream(downloadedFile);

				// 서버로부터 파일의 내용을 읽고 그것을 새 파일에 기록합니다.
//				final int BUFFER_LENGTH = 4096;
				final int BUFFER_LENGTH = 1024;

				int count; //새로운방법

				byte byteBuffer[] = new byte[BUFFER_LENGTH];
//				while (-1 != inputFromServer.read(byteBuffer)) {
//					outputToFile.write(byteBuffer);
//				}
				while ((count = inputFromServer.read(byteBuffer))>0) {
					outputToFile.write(byteBuffer, 0 ,count);
				}

				this.isDownloadCompleted = true;
			}
			catch (IOException e) {
				e.printStackTrace();
				this.isDownloadCompleted = false;
			}
			finally {
				Closer.close(inputFromServer);
				Closer.close(outputToFile);
			}

			return downloadedFile;
		}

	}






}
