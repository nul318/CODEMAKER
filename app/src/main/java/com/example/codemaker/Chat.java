package com.example.codemaker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Chat extends Activity implements OnClickListener {


	TextView info;
	myAdapter Adapter;
	final ArrayList<Chat_profile> Message_list = new ArrayList<Chat_profile>();

	boolean who = false; //누가 쓰는지 보는거!




	public class myAdapter extends BaseAdapter {
		Context con; // 이미지를 받는곳
		LayoutInflater inflater;
		ArrayList<Chat_profile> message_list;
		int layout;



		myAdapter(Context context, int layout, ArrayList<Chat_profile> message_list) {
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

			}


			 // 비어있을때만 연결!!
			// view를 스크롤올릴때마다 계속 재 생성하는 것을 방지하기위해 최초에 한번만 생성! 근데 새로고침에 오류가나서
			// 일단 이렇게쓰자
			TextView txt = (TextView) convertView.findViewById(R.id.chat_list);
			TextView profile_view = (TextView) convertView.findViewById(R.id.profile_name);
			ImageView profie_image = (ImageView) convertView.findViewById(R.id.profile_image);
			LinearLayout chat_layout = (LinearLayout) convertView.findViewById(R.id.chat_layout);



			if(message_list.get(position).isMine){
				profie_image.setVisibility(View.INVISIBLE);
				profile_view.setVisibility(View.INVISIBLE);

				txt.setBackground(Chat.this.getResources().getDrawable(R.drawable.chat_nine_me));


				chat_layout.setGravity(Gravity.RIGHT);
				chat_layout.setHorizontalGravity(Gravity.RIGHT);










				txt.setText(message_list.get(position).chatmessage);
				profile_view.setText(message_list.get(position).name);

			}
			else{
				profie_image.setVisibility(View.VISIBLE);
				profile_view.setVisibility(View.VISIBLE);
				//			txt.setBackground(Chat.this.getResources().getDrawable( (message_left ? R.drawable.bubble_b : R.drawable.bubble_a )));
				txt.setBackground(Chat.this.getResources().getDrawable(R.drawable.chat_nine));



				chat_layout.setGravity(Gravity.LEFT);
				chat_layout.setHorizontalGravity(Gravity.LEFT);



				txt.setText(message_list.get(position).chatmessage);
				profile_view.setText(message_list.get(position).name);
//			txt.setTextSize(20.0f);
			}



			return convertView;
		}
	}

	final Handler handler1 = new Handler();

	String beforemessage;

	public static String SERVER_IP = "Server IP";
	public static int SERVER_PORT = 19999;

	Button sendButton;
	EditText inputField;
	TextView textView;








	String name;

	Socket socket;
	DataInputStream input;
	DataOutputStream output;


	String user_id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chat);
		this.getActionBar().hide();

		info = (TextView) findViewById(R.id.info);



		Adapter = new myAdapter(this, R.layout.chat_list, Message_list);


		// 어댑터 연결

		ListView list = (ListView) findViewById(R.id.chatlistView);

		list.setAdapter(Adapter);
		// 리스트뷰 속성(없어도 구현 가능)
		// 항목을 선택하는 모드
//		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// 항목 사이의 구분선 지정
		 list.setDivider(null); //xml 에서 직접 지정함
		// 구분선의 높이 지정
//		list.setDividerHeight(4);






		user_id = getIntent().getStringExtra("id");//가져온대화명

		sendButton = (Button) findViewById(R.id.sendButton);
		inputField = (EditText) findViewById(R.id.inputField);
		textView = (TextView) findViewById(R.id.textView);






		sendButton.setOnClickListener(this);
		intro();



		new Thread(new Runnable() {

			public void run() {
				name = user_id;
				connect();

			}
		}
		).start();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			socket.close();
			input.close();
			output.close();
		} catch (IOException e) {
		}
	}

	public void intro() {

		info.append("채팅에 접속하셨습니다! \n");

//		print("대화명을 입력하시고 전송 버튼을 눌러 주세요.");
	}

	public void print(String profile_name, String message, Boolean mine) {




		Message_list.add(new Chat_profile(profile_name,message, mine));

		Adapter.notifyDataSetChanged(); //새로고침
//		textView.append(message + "\n");
//		textView.getText().toString();


	}

	public void onClick(final View view) {


		new Thread(new Runnable() {

			public void run() {
				switch (view.getId()) {
					case R.id.sendButton:
						if (inputField.getText().toString() == "")
							return;

//					if (name == null) {
						if (false){ // 대화명정하는 부분인데 정해진거 가져오기때문에 주석함
//						String text = inputField.getText().toString();
//						
//						handler1.post(new Runnable() {
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								inputField.setText("");
//							}
//						});
//						
//						name = text;
//						connect();
						} else { //여기가 채팅!
							String text = inputField.getText().toString();
							handler1.post(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									inputField.setText("");
								}
							});


							try {
								output.writeUTF("c`" + text);
								output.flush();
							} catch (IOException e) {
								print(profile_name2,"메시지 전송을 실패하였습니다.", false);
							}

						}

						break;
				}

			}
		}).start();

	}

	public void connect() {

		try {
			handler1.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					Toast.makeText(getApplicationContext(), SERVER_IP + ":" + SERVER_PORT + "로 접속 중...", Toast.LENGTH_SHORT).show();
//					print(SERVER_IP + ":" + SERVER_PORT + "로 접속 중...");
				}
			});


			socket = new Socket(SERVER_IP, SERVER_PORT);

			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());

			while (socket != null) {
				if (socket.isConnected()) {
					output.writeUTF("r`1`1`" + name + "`");
					output.flush();
					break;
				}
			}

			MessageReciver messageReceiver = new MessageReciver();
			messageReceiver.start();

		} catch (Exception e) {
			handler1.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
//					Toast.makeText(getApplicationContext(), "서버에 접속할 수 없습니다.", Toast.LENGTH_SHORT).show();
					print(profile_name2,"서버에 접속할 수 없습니다.", false);

				}
			});

			this.finish();

		}

	}

	public String chatMessage = "";
	public String profile_name2 = "";

	public class MessageReciver extends Thread {
		public void run() {
			try {
				String received;

				while ((received = input.readUTF()) != null) {

					final String[] buffer = received.split("`");

					switch (buffer[0].charAt(0)) {
						case 'n':
							chatMessage =  buffer[1]; //입장
							who = false;
							break;
						case 'c':
							chatMessage = buffer[2];
							profile_name2 = buffer[1];
							if(profile_name2.equals(user_id)) {
								who = true;
							}
							else{
								who = false;
							}
							break;
						case 'x':
							chatMessage = buffer[1]; //퇴장
							profile_name2 = buffer[1];
							who=false;

							break;
					}

					Message message = handler.obtainMessage(1, received);

					handler.sendMessage(message);

				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	Handler handler = new Handler() {


		public void handleMessage(Message message) {
			super.handleMessage(message);

			if(!chatMessage.equals("null") && !chatMessage.equals(" ")){
				if(!chatMessage.equals(beforemessage)) { // 처음에 이유는 모르겠으나 입장하셨습니다가 연속 두번떠서 그냥 임시로 연속 2번 뜨는건 전부 차단함
					if(who) {
						print(profile_name2, chatMessage, true);
					}
					else{
						print(profile_name2, chatMessage, false);
					}
				}
			}

			beforemessage=chatMessage;

		}
	};


}
