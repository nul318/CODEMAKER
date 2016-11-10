package com.example.codemaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Message extends Activity {
	String user_id = "";
//	final ArrayList<MyFile> File_list = new ArrayList<MyFile>();
	final ArrayList<MyMessage> Message_list = new ArrayList<MyMessage>();

	myAdapter Adapter;
	Handler handler = new Handler();

	private void sendJsonDataToServer(String title) throws UnsupportedEncodingException {

		String URL = "http://Server IP/phpcodemaker/message_del_proc.php";
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
		setContentView(R.layout.message);
		 this.getActionBar().hide();
		user_id = this.getIntent().getStringExtra("id");

		final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout2); //새로고침api
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
													 @Override
													 public void onRefresh() {
														 new Handler().postDelayed(new Runnable() {
															 @Override
															 public void run() {
																 mSwipeRefreshLayout.setRefreshing(false);
																 Intent intent = new Intent(Message.this, Message.class);
																 intent.putExtra("id", user_id);
																 //startActivity(intent);
																 startActivity(intent); // 액티비티실행하고 결과받아오는
																 // 함수!! 미친 개꿀
																 finish();
															 }
														 }, 1000);
													 }
												 }
		);


		ImageView send_message = (ImageView) findViewById(R.id.send_message);
		send_message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.message_alert, null);

				final EditText title = (EditText) addLayout.findViewById(R.id.alert_title);
				final EditText to = (EditText) addLayout.findViewById(R.id.alert_to);
//			final EditText contents = (EditText) addLayout.findViewById(R.id.alert_contents);

				new AlertDialog.Builder(Message.this).setTitle("정보를 입력하R세요.").setView(addLayout)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								String _title = title.getText().toString();
								String _to = to.getText().toString();
//							String _contents = contents.getText().toString();

								try {
									sendJsonDataToServer(_title, _to, user_id, "", "");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								return;
							}
						}).show();
			}
		});





		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);//네트워크 에러났는데 이렇게하면 댄대 ㅇㅅㅇ 권한문제인듯

		final phpDown task = new phpDown();
		task.execute("http://Server IP/phpcodemaker/message_index.php");
//
		// 어댑터 준비

		ListView list = (ListView) findViewById(R.id.messagelist);



		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//클릭하는부분


			}

        });//값을 받아올땐 Resume 에서 다시 시작해야하므로 ㅇㅇ resume 에다 작성






		list.setOnItemLongClickListener(new OnItemLongClickListener(){ // 길게누르면 수정, 삭제 가 나와야겠지! 해해해해
			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Message.this)
				//.setTitle("alert")
						// TODO Auto-generated method stub						})
				.setPositiveButton("삭제", new DialogInterface.OnClickListener() { //삭제버튼

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						new Thread() {// 네트워크 통신은 서브 쓰레드에서 돌려야한다.
							@Override
							public void run() {
								// TODO Auto-generated method stub

								handler.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method
										// stub
										try {
											sendJsonDataToServer(Message_list.get(position).Title.toString());
											Intent intent = new Intent(Message.this, Message.class); // 일단
																										// 새로고침을
																										// 이런식으로
																										// ㅎㅎㅎㅎ...
											startActivity(intent);
											finish();

										} catch (UnsupportedEncodingException e) {
											// TODO Auto-generated catch
											// block
											e.printStackTrace();
										}

									}
								});

							}
						}.start();
					}
				})
			.show();

				return false;
			}

        });


		Adapter = new myAdapter(this, R.layout.mymessage, Message_list);

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
		Animation fade = AnimationUtils.loadAnimation(Message.this, R.anim.fade);
		animSet.addAnimation(fade); //애니메이션 추가
		list.startAnimation(animSet);


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
			String to = "To. ";
			String from = "By. ";
			String contents;
			String time;

			try {
				JSONObject root = new JSONObject(str);
				JSONArray ja = root.getJSONArray("results");
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					title = jo.getString("TITLE");
					to += jo.getString("To");
					from += jo.getString("From");
					contents = jo.getString("Contents");
					time = jo.getString("Time");

					if(jo.getString("To").equals(user_id)) //받는사람이 일치하면 ㅇㅇ
						Message_list.add(new MyMessage(title, to, from, contents, time));

					to = "To. ";
					from = "By. ";
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
		menu.add(0, 1, 0, "쪽지보내기");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == 1) {
//			Toast.makeText(getApplicationContext(), "업데이트중", Toast.LENGTH_SHORT).show();

			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.message_alert, null);

			final EditText title = (EditText) addLayout.findViewById(R.id.alert_title);
			final EditText to = (EditText) addLayout.findViewById(R.id.alert_to);
//			final EditText contents = (EditText) addLayout.findViewById(R.id.alert_contents);


			new AlertDialog.Builder(Message.this).setTitle("정보를 입력하세요.").setView(addLayout)
					.setNeutralButton("확인", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String _title = title.getText().toString();
							String _to = to.getText().toString();
//							String _contents = contents.getText().toString();




							try {
								sendJsonDataToServer(_title, _to, user_id, "", "");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}





							return;
						}
					}).show();
		}




		return super.onOptionsItemSelected(item);
	}





	public class myAdapter extends BaseAdapter {

		Context con; // 이미지를 받는곳
		LayoutInflater inflater;
		ArrayList<MyMessage> message_list;
		int layout;

		myAdapter(Context context, int layout, ArrayList<MyMessage> message_list) {
			con = context;
			this.layout = layout;
			this.message_list = message_list;
			inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			// 멤버변수 초기화
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return message_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return message_list.get(position);
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
//			TextView txt2 = (TextView) convertView.findViewById(R.id.to);
			TextView txt3 = (TextView) convertView.findViewById(R.id.from);
//			TextView txt4 = (TextView) convertView.findViewById(R.id.contents);

			txt.setText(message_list.get(position).Title);
			txt.setTextSize(20.0f);
//			txt2.setText(message_list.get(position).To);
			txt3.setText(message_list.get(position).From);
//			txt4.setText(message_list.get(position).Contents);

			return convertView;
		}
	}









	private void sendJsonDataToServer(String title, String to , String from, String contents, String time) throws UnsupportedEncodingException {

		String URL = "http://Server IP/phpcodemaker/add_message_proc.php";
		// 1
		HttpClient httpClient = new DefaultHttpClient();
		// 2
		HttpPost httpPost = new HttpPost(URL);
		// 3
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

		nameValuePairs.add(new BasicNameValuePair("title", URLEncoder.encode(title,"UTF-8")));
		nameValuePairs.add(new BasicNameValuePair("to", URLEncoder.encode(to,"UTF-8")));
		nameValuePairs.add(new BasicNameValuePair("contents", URLEncoder.encode(contents,"UTF-8")));
		nameValuePairs.add(new BasicNameValuePair("from", URLEncoder.encode(from,"UTF-8")));// 일단 널값
		nameValuePairs.add(new BasicNameValuePair("time", URLEncoder.encode(contents,"UTF-8")));

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

	
	
}
