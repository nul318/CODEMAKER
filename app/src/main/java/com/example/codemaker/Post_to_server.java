package com.example.codemaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Post_to_server extends Activity {
	String key = ""; //파일 path
	String pw = ""; //파일 pw
	String time = "FileName"; //서버에 저장될 파일명
	
	private void sendJsonDataToServer(String title, String fileurl , String author, String contents, String pw) throws UnsupportedEncodingException {

		String URL = "http://Server IP/phpcodemaker/add_proc.php";
		// 1
		HttpClient httpClient = new DefaultHttpClient();
		// 2
		HttpPost httpPost = new HttpPost(URL);
		// 3
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

		nameValuePairs.add(new BasicNameValuePair("title", URLEncoder.encode(title,"UTF-8")));
		nameValuePairs.add(new BasicNameValuePair("author", URLEncoder.encode(author,"UTF-8")));
		nameValuePairs.add(new BasicNameValuePair("contents", URLEncoder.encode(contents,"UTF-8")));
		nameValuePairs.add(new BasicNameValuePair("fileurl", URLEncoder.encode(fileurl,"UTF-8")));// 일단 널값
		nameValuePairs.add(new BasicNameValuePair("password", URLEncoder.encode(pw,"UTF-8")));
 
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
		setContentView(R.layout.post_to_server);
		this.getActionBar().hide();
		
		



		
		
		
		
		final EditText title = (EditText) findViewById(R.id.title_edit);
		final TextView author = (TextView) findViewById(R.id.author_edit);
		final EditText contents = (EditText) findViewById(R.id.contents_edit);
		final Handler handler = new Handler();
		final TextView fileurl = (TextView) findViewById(R.id.fileurl_view);
		
		Intent intent = getIntent();
		String id=intent.getStringExtra("id");
		author.setText(id);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);//네트워크 에러났는데 이렇게하면 댄대 ㅇㅅㅇ 권한문제인듯
		


		View.OnClickListener listener = new View.OnClickListener() { // 전송버튼
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				final FTPClient mFTP = new FTPClient();
				
				
				
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.post_alert, null);

				final EditText password = (EditText) addLayout.findViewById(R.id.password);

				new AlertDialog.Builder(Post_to_server.this).setTitle("패스워드를 입력하세요.").setView(addLayout)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						Calendar now = Calendar.getInstance();
						
						
						String filename = "FileName";//디폴트	
						
						if (!fileurl.getText().toString().equals("FileName")) {
							
							time = now.get(now.YEAR) % 100 + "" + (now.get(now.MONTH) + 1) + ""
									+ now.get(now.DATE) + "" + now.get(now.HOUR) + "" + now.get(now.MINUTE) + ""
									+ now.get(now.SECOND) + ".mp3"; // 시간으로
																			// 서버에
																			// 저장
							filename = "/home/hanul/upload/"+time;
							
						}
						final String files = filename;
						
						
						
						
						new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								
								try {
									
									
									if (!fileurl.getText().toString().equals("FileName")) {

										File path = new File(key);
										HttpClient client = new DefaultHttpClient();
										HttpPost post = new HttpPost("http://Server IP/phpcodemaker/addfile.php");

										MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
										entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

										FileBody bin1 = new FileBody(path);
										entityBuilder.addPart("file", bin1);
										
										HttpEntity entity = entityBuilder.build();
										
										post.setEntity(entity);
										
										//----------------------------------------------------------------------------post 하는 부분
//										List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//										nameValuePairs.add(new BasicNameValuePair("time", URLEncoder.encode(time,"UTF-8")));								 
//										post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
										//----------------------------------------------------------------------------
										
										
										
										HttpResponse response = client.execute(post);

										HttpEntity httpEntity = response.getEntity();
										String result = EntityUtils.toString(httpEntity);
										
										Log.i("result", result);
								
										
//										mFTP.storeFile(files, ifile);
//										mFTP.disconnect();
									}
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}				
								
																
								handler.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										
										
										String _title = title.getText().toString();
										String _author = author.getText().toString();
										String _contents = contents.getText().toString();
										pw = password.getText().toString();

										try {
											sendJsonDataToServer(_title, time , _author, _contents, pw);//인코딩떔시 쓸데없는 트라이캐치문 생김
										} catch (UnsupportedEncodingException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}																				
										Intent intent = new Intent(Post_to_server.this, Board.class);
										startActivity(intent);
										finish();
									}
								});

							}
						}.start();

						
					}
				}).show();
			
	// set dialog message

			}
		};

		
		
		View.OnClickListener listener2 = new View.OnClickListener() { // 꿀 형식 모든 //파일첨부기능
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), "구현중", Toast.LENGTH_SHORT).show();
				new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
					@Override
					public void run() {
						// TODO Auto-generated method stub
//						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("*/*");
						
						
						startActivityForResult(intent, 0);
						

					}
				}.start();
				// set dialog message
			}
		};

		Button post = (Button) findViewById(R.id.post);
		Button post_file = (Button) findViewById(R.id.file_bt);
		
		post.setOnClickListener(listener);
		post_file.setOnClickListener(listener2);

	}

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
	
	

	public String getRealImagePath(Uri uriPath) { // uri에서 실제경로 찾아오기
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uriPath, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(index);
//		path = path.substring(5);
		return path;

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub
		
		switch (requestCode) {
		case 0:
			if (resultCode == RESULT_OK) {
				

				Uri mDataUri = data.getData();
				
				String realpath = getRealImagePath(mDataUri);
				TextView fileurlview = (TextView) findViewById(R.id.fileurl_view);
				
				try {
//					key = URLDecoder.decode(mDataUri.toString(), "UTF-8");
					key = URLDecoder.decode(realpath, "UTF-8");
					fileurlview.setText(key);
					
//					Toast.makeText(getApplicationContext(), key, Toast.LENGTH_SHORT).show();
//					Toast.makeText(getApplicationContext(), key, Toast.LENGTH_SHORT).show();
					
					

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
	
				 				

			}

			break;

		}

	}
	





}
	
