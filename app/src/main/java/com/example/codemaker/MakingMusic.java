package com.example.codemaker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressLint("NewApi")
public class MakingMusic extends Activity {


	public void play(final float speed, final float volume, final String key) {
		new Thread(new Runnable() {
			public void run() {
				AndroidAudioDevice device = new AndroidAudioDevice(44100, 1);
				Sonic sonic = new Sonic(44100, 1);
				byte samples[] = new byte[4096];
				byte modifiedSamples[] = new byte[2048];

				InputStream soundFile = null;

				if (key.equals("C")) {
					soundFile = getResources().openRawResource(R.raw.c);
				} else if (key.equals("Dm")) {
					soundFile = getResources().openRawResource(R.raw.dm);
				} else if (key.equals("Em")) {
					soundFile = getResources().openRawResource(R.raw.em);
				} else if (key.equals("F")) {
					soundFile = getResources().openRawResource(R.raw.f);
				} else if (key.equals("G")) {
					soundFile = getResources().openRawResource(R.raw.g);
				} else if (key.equals("Am")) {
					soundFile = getResources().openRawResource(R.raw.am);
				} else if (key.equals("drum001")) {
					soundFile = getResources().openRawResource(R.raw.drum001);
				}  else {
					// 만약 이상한 값을 넣었을 시 무반응
					Toast toast = Toast.makeText(MakingMusic.this, "error", Toast.LENGTH_SHORT);
					toast.getView().setBackgroundResource(R.drawable.toast_drawable);
					toast.show();
				}


				int bytesRead;

				if(soundFile != null) {
					sonic.setSpeed(speed);
//				 sonic.setPitch(1.5f);
					sonic.setVolume(volume);
					sonic.setRate(2);

					do {
						try {
							bytesRead = soundFile.read(samples, 0, samples.length);

						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
						if(bytesRead > 0) {
							sonic.putBytes(samples, bytesRead);
						} else {
							sonic.flush();
						}
						int available = sonic.availableBytes();
						if(available > 0) {
							if(modifiedSamples.length < available) {
								modifiedSamples = new byte[available*2];
							}
							sonic.receiveBytes(modifiedSamples, available);
							device.writeSamples(modifiedSamples, available);
						}
					} while(bytesRead > 0);
					device.flush();
				}



			}
		}).start();

	}

	private SoundPool mSoundPool; // 기타용
	private HashMap<Integer, Integer> mSoundPoolMap;
	// private HashMap<Integer, MediaPlayer> mSoundPoolMap;

	private SoundPool mSoundPool2; // 드럼용
	private HashMap<Integer, Integer> mSoundPoolMap2;
	// private HashMap<Integer, MediaPlayer> mSoundPoolMap2;

	// MediaPlayer c = null;
	// MediaPlayer dm = null;
	// MediaPlayer em = null;
	// MediaPlayer f = null;
	// MediaPlayer g = null;
	// MediaPlayer am = null;
	// MediaPlayer drum = null;

	String _bpm = "90";
	String _rhythm = null;

	float g_volume = 0; //기타파일 볼륨 설정
	float d_volume = 0; //드럼파일 볼륨 설정

	int codesize = 1;
	static int idx = 0;

	boolean Reccheck = false; // 녹음 정지 기능을 위해 만듬
	MediaRecorder mediaRecorder = new MediaRecorder(); // 녹음 기능을 위한 객체
	String diraddress = ""; // 녹음파일이 저장되는 디렉터리주소

	int chk = 0; // 지나간 idx를 체크하기위한 변수

	double time = 2620;
	boolean stop = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.makingmusic);
		this.getActionBar().hide();
		final myAdapter Adapter;
		// 어댑터 준비
		final ArrayList<Code> code_list = new ArrayList<Code>();

		final TextView codeview = (TextView) findViewById(R.id.viewcode);

		final AnimationSet animSet = new AnimationSet(true);
		Animation fade = AnimationUtils.loadAnimation(MakingMusic.this, R.anim.fade);
		animSet.addAnimation(fade); //애니메이션 추가

//		
//		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//		mSoundPoolMap = new HashMap<Integer, Integer>();
//
//		mSoundPool2 = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//		mSoundPoolMap2 = new HashMap<Integer, Integer>();
//
//		final int c = mSoundPool.load(this, R.raw.c, 1);
//		final int dm = mSoundPool.load(this, R.raw.dm, 1);
//		final int em = mSoundPool.load(this, R.raw.em, 1);
//		final int f = mSoundPool.load(this, R.raw.f, 1);
//		final int g = mSoundPool.load(this, R.raw.g, 1);
//		final int am = mSoundPool.load(this, R.raw.am, 1);
//
//		final int drum = mSoundPool2.load(this, R.raw.drum001, 1);

		// Adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_expandable_list_item_1, arDessert);
		Adapter = new myAdapter(this, R.layout.code, code_list);
		// 어댑터 연결
		final ListView list = (ListView) findViewById(R.id.Codelist);
		list.setAdapter(Adapter);

		// 리스트뷰 속성(없어도 구현 가능)
		// 항목을 선택하는 모드
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// 항목 사이의 구분선 지정
//		list.setDivider(new ColorDrawable(Color.WHITE)); //xml 에서 직접 지정함
		// 구분선의 높이 지정
		list.setDividerHeight(4);

		final Handler handler = new Handler();

		final Button StartBt = (Button) findViewById(R.id.music_start); // 시작
		final Button demoBt = (Button) findViewById(R.id.demo); // 시작





		final ToggleButton Guitartog = (ToggleButton) findViewById(R.id.guitartoggle);
		final ToggleButton Drumtog = (ToggleButton) findViewById(R.id.drumtoggle);



		final String demo_list[] = new String[4];
		demo_list[0]="C-Am-Dm-G-C-Am-Dm-G-C";
		demo_list[1]="C-G-Am-Em-F-C-F-G-C";
		demo_list[2]="C-G-F-G-C-G-F-G-C";
		demo_list[3]="C-G-Em-Am-Dm-G-C-G-Em-Am-Dm-G-C";

		final DialogInterface.OnClickListener listener6= new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				idx=which;

			}
		};

		demoBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				new AlertDialog.Builder(MakingMusic.this)
						.setTitle("코드진행을 선택하시오.")
						.setSingleChoiceItems(demo_list, 0, listener6)

						.setNeutralButton("확인", new DialogInterface.OnClickListener() { //확인버튼

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								codesize = 1;
								code_list.clear();

								//arDessert.remove(idx);
								if (idx == 0) {
									code_list.add(new Code("C"));
									codesize++;
									code_list.add(new Code("Am"));
									codesize++;
									code_list.add(new Code("Dm"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("C"));
									codesize++;
									code_list.add(new Code("Am"));
									codesize++;
									code_list.add(new Code("Dm"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("C"));
									codesize++;
								}
								if (idx == 1) { //"C-G-Am-Em-F-C-F-G-C";
									code_list.add(new Code("C"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("Am"));
									codesize++;
									code_list.add(new Code("Em"));
									codesize++;
									code_list.add(new Code("F"));
									codesize++;
									code_list.add(new Code("C"));
									codesize++;
									code_list.add(new Code("F"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("C"));
									codesize++;
								}
								if (idx == 2) { //"C-G-F-G-C-G-F-G-C"
									code_list.add(new Code("C"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("F"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("C"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("F"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("C"));
									codesize++;
								}
								if (idx == 3) { //"C-G-Em-Am-Dm-G-C-G-Em-Am-Dm-G-C";
									code_list.add(new Code("C"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("Em"));
									codesize++;
									code_list.add(new Code("Am"));
									codesize++;
									code_list.add(new Code("Dm"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("C"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("Em"));
									codesize++;
									code_list.add(new Code("Am"));
									codesize++;
									code_list.add(new Code("Dm"));
									codesize++;
									code_list.add(new Code("G"));
									codesize++;
									code_list.add(new Code("C"));
									codesize++;
								}


								Adapter.notifyDataSetChanged(); //새로고침

							}
						})
						.show();




			}
		});



		// default 값
//		mSoundPoolMap.put(codesize, c);
//		mSoundPoolMap2.put(codesize, drum);
//		code_list.add(new Code("C"));
//		codesize++;
//
//		mSoundPoolMap.put(codesize, g);
//		mSoundPoolMap2.put(codesize, drum);
//		code_list.add(new Code("G"));
//		codesize++;
//
//		mSoundPoolMap.put(codesize, f);
//		mSoundPoolMap2.put(codesize, drum);
//		code_list.add(new Code("F"));
//		codesize++;
//
//		mSoundPoolMap.put(codesize, g);
//		mSoundPoolMap2.put(codesize, drum);
//		code_list.add(new Code("G"));
//		codesize++;
//
//		mSoundPoolMap.put(codesize, c);
//		mSoundPoolMap2.put(codesize, drum);
//		code_list.add(new Code("C"));
//		codesize++;



		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (Guitartog.isChecked()) {
								Guitartog.setBackgroundResource(R.drawable.guitar_after);
								if (idx >= 0) {
//									mSoundPoolMap.get(idx).setVolume(1f, 1f);
									g_volume=1f;
								}
							} else if (!(Guitartog.isChecked())) {
								Guitartog.setBackgroundResource(R.drawable.guitar);
								if (idx >= 0) {
//									mSoundPoolMap.get(idx).setVolume(0, 0);
									g_volume=0;
								}
							}

							if (Drumtog.isChecked()) {
								Drumtog.setBackgroundResource(R.drawable.drum_after);
								if (idx >= 0) {
//									mSoundPoolMap2.get(idx).setVolume(1f, 1f);
									d_volume=1f;
								}

							} else if (!(Drumtog.isChecked())) {
								Drumtog.setBackgroundResource(R.drawable.drum);
								if (idx >= 0) {
//									mSoundPoolMap2.get(idx).setVolume(0, 0);
									d_volume=0;
								}

							}

						}
					});

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}.start();

		StartBt.setOnClickListener(new OnClickListener() {

			@Override // 음악재생
			public void onClick(View v) {
				// TODO Auto-generated method stub


				if (StartBt.getText().equals("   ")) {
					stop=true; //정지하는 기능
					StartBt.setText("  "); //공백 2번 : 실행
				} else {
//					stop=false;

					StartBt.setText("   "); // 공백 3번 == ㅁ
					StartBt.setBackgroundResource(R.drawable.stop);

					new Thread() {
						@Override
						public void run() {
							// TODO Auto-generated method stub

							for (idx = 1; idx <= codesize-1; idx++) {
								if(stop==true){
									stop=false;
									break;
//									idx=mSoundPoolMap.size()+1; //for문을 빠져나가기위함
								}
								// 원래 그냥 handler 안에 쓰려고 했으나 handler 안에 sleep을
								// 쓰면안돼서이렇게 씀
								handler.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub
										code_list.get(idx - 1).emphasis = true;

										Adapter.notifyDataSetChanged(); //새로고침 
										list.smoothScrollToPosition(idx);


//										Toast.makeText(getApplicationContext(), idx+"", Toast.LENGTH_SHORT).show();

//										mSoundPoolMap.get(idx).start(); // SoundPool
//																		// 은
//																		// 배속기능을
//																		// 지원하는데
//																		// 음소거기능이
//																		// 힘들다.
//										mSoundPoolMap2.get(idx).start();


										codeview.setText("   "+code_list.get(idx-1).CodeName);

										codeview.startAnimation(animSet);
//										mSoundPool.play(mSoundPoolMap.get(idx), g_volume, g_volume, 1, 0, Float.valueOf(_bpm)/90);
//										mSoundPool2.play(mSoundPoolMap2.get(idx), d_volume, d_volume, 1, 0, Float.valueOf(_bpm)/90);
										//time도 같이 조정. 속도를 몇으로 나누어야지 밸런스가맞는지 조정

										play(Float.valueOf(_bpm)/90, g_volume, code_list.get(idx-1).CodeName.toString() );
										play(Float.valueOf(_bpm)/90, d_volume, "drum001" );

									}
								});
								try {
									Thread.sleep((int)time);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								handler.post(new Runnable() {
									@Override
									public void run() {
										// TODO Auto-generated method stub

										// 이유가뭔지는모르겠으나thread다음에idx가자동으로상승하는듯
										code_list.get(idx - 2).emphasis = false;
										code_list.get(idx - 2).emphasis = false;
										code_list.get(idx - 2).emphasis = false;
//										list.setAdapter(Adapter);

									}
								});

							}
							handler.post(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									list.setAdapter(Adapter);
									idx = 0;
									StartBt.setBackgroundResource(R.drawable.play);
//									StartBt.setText("▶");
									StartBt.setText("  "); //공백 2번 실행

								}
							});

						}

					}.start();

				}
			}
		});

		Button AddBt = (Button) findViewById(R.id.AddCode);

		AddBt.setOnClickListener(new OnClickListener() { // 코드 추가

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.add_alert, null);

				final EditText code = (EditText) addLayout.findViewById(R.id.codeEdit);

				new AlertDialog.Builder(MakingMusic.this).setTitle("코드를 입력하세요.").setView(addLayout)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

								String key = code.getText().toString(); // 입력받은 값과 파일을
								// 대응시킨다.

								list.setAdapter(Adapter);// 새로고침

								if (key.equals("C")) {
//							mSoundPoolMap.put(codesize, c);
//							mSoundPoolMap2.put(codesize, drum);
									code_list.add(new Code(key));
									codesize++;
								} else if (key.equals("Dm")) {
//							mSoundPoolMap.put(codesize, dm);
//							mSoundPoolMap2.put(codesize, drum);
									code_list.add(new Code(key));
									codesize++;
								} else if (key.equals("Em")) {
//							mSoundPoolMap.put(codesize, em);
//							mSoundPoolMap2.put(codesize, drum);
									code_list.add(new Code(key));
									codesize++;
								} else if (key.equals("F")) {
//							mSoundPoolMap.put(codesize, f);
//							mSoundPoolMap2.put(codesize, drum);
									code_list.add(new Code(key));
									codesize++;
								} else if (key.equals("G")) {
//							mSoundPoolMap.put(codesize, g);
//							mSoundPoolMap2.put(codesize, drum);
									code_list.add(new Code(key));
									codesize++;
								} else if (key.equals("Am")) {
//							mSoundPoolMap.put(codesize, am);
//							mSoundPoolMap2.put(codesize, drum);
									code_list.add(new Code(key));
									codesize++;
								} else {
									// 만약 이상한 값을 넣었을 시 무반응
									Toast toast = Toast.makeText(MakingMusic.this, "등록되지 않은 코드입니다.", Toast.LENGTH_SHORT);
									toast.getView().setBackgroundResource(R.drawable.toast_drawable);
									toast.show();
									;
								}

							}
						}).show();
			}
		});

		Button SubBt = (Button) findViewById(R.id.SubCode);

		SubBt.setOnClickListener(new OnClickListener() { // 코드 삭제

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (code_list.size() > 0) {
//					mSoundPoolMap.remove(mSoundPoolMap.size());
					code_list.remove(code_list.remove(code_list.size() - 1));
					list.setAdapter(Adapter);// 새로고침
					codesize--;
				} else {
					Toast toast = Toast.makeText(MakingMusic.this, "삭제할 코드가 없습니다.", Toast.LENGTH_SHORT);

					toast.getView().setBackgroundResource(R.drawable.toast_drawable);
					toast.show();
					;
				}
			}
		});

		Button SetBt = (Button) findViewById(R.id.set_up);

		SetBt.setOnClickListener(new OnClickListener() { // 박자 리듬설정

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				LinearLayout addLayout = (LinearLayout) vi.inflate(R.layout.bpm, null);

				final EditText bpm = (EditText) addLayout.findViewById(R.id.bpm);
//				final EditText rhythm = (EditText) addLayout.findViewById(R.id.Rhythm);

				final TextView v_bpm = (TextView) findViewById(R.id.BpmView);
				final TextView v_rhythm = (TextView) findViewById(R.id.RhythmView);



				new AlertDialog.Builder(MakingMusic.this).setTitle("정보를 입력하세요.").setView(addLayout)
						.setNeutralButton("확인", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

								try {
									if (Integer.valueOf(bpm.getText().toString()) >= 45 && Integer.valueOf(bpm.getText().toString()) <= 180) {
										_bpm = bpm.getText().toString();
//							_rhythm = rhythm.getText().toString();
										v_bpm.setText("bpm " + Integer.valueOf(_bpm));
										// v_rhythm.setText("Rhythm " + _rhythm);
										v_rhythm.setText("Rhythm 4/4");

										time = 2620 / ((Double.valueOf(_bpm) / 90));
									} else {
										Toast toast = Toast.makeText(getApplicationContext(), "45와 180 사이의 값을 입력해 주세요", Toast.LENGTH_SHORT);

										toast.getView().setBackgroundResource(R.drawable.toast_drawable);
										toast.show();
									}
								}catch(Exception e){
									Toast toast = Toast.makeText(getApplicationContext(), "45와 180 사이의 값을 입력해 주세요", Toast.LENGTH_SHORT);

									toast.getView().setBackgroundResource(R.drawable.toast_drawable);
									toast.show();
								}
								//Bpm 선택을 select옵션으로 하자
							}
						}).show();
			}
		});

		File directory = new File(Environment.getExternalStorageDirectory().toString() + "/CODEMAKER/rec/");
		if (!directory.exists())
			directory.mkdirs();

		final Button RecBt = (Button) findViewById(R.id.music_record);

		RecBt.setOnClickListener(new OnClickListener() { // 녹음기능추가

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (Reccheck == false) {
					mediaRecorder = new MediaRecorder(); // 녹음 기능을 위한 객체
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);




					long currentTime = System.currentTimeMillis();
					diraddress = Environment.getExternalStorageDirectory().toString() + "/CODEMAKER/rec/" + currentTime
							+ ".mp3";

					mediaRecorder.setOutputFile(diraddress);

//					RecBt.setText("■");
					RecBt.setText("   "); //공백 3번 = 정지
					RecBt.setBackgroundResource(R.drawable.stop);

					RecBt.setTextColor(Color.BLACK);

					Reccheck = true;
					try {

						mediaRecorder.prepare();
						mediaRecorder.start();
						Toast toast = Toast.makeText(MakingMusic.this, diraddress + "녹음 시작", Toast.LENGTH_SHORT);

						toast.getView().setBackgroundResource(R.drawable.toast_drawable);
						toast.show();
						;

					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {

					try {
//						RecBt.setText("●");
						RecBt.setText("  "); //공백 2번 == 녹음
						RecBt.setBackgroundResource(R.drawable.rec_selector);

						RecBt.setTextColor(Color.RED);
						Reccheck = false;
						mediaRecorder.stop();
						mediaRecorder.reset();
						mediaRecorder.release();
						mediaRecorder = null;

						Toast toast = Toast.makeText(MakingMusic.this, diraddress + "녹음 정지", Toast.LENGTH_SHORT);
						toast.getView().setBackgroundResource(R.drawable.toast_drawable);
						toast.show();
						;
						Toast toast2 = Toast.makeText(MakingMusic.this, diraddress + " 에 저장되었습니다.", Toast.LENGTH_SHORT);

						toast2.getView().setBackgroundResource(R.drawable.toast_drawable);
						toast2.show();
						;
						File f = new File(diraddress);



						;
					}

					catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});
	}

	public class myAdapter extends BaseAdapter {

		Context con; // 이미지를 받는곳
		LayoutInflater inflater;
		ArrayList<Code> code_list;
		int layout;

		myAdapter(Context context, int layout, ArrayList<Code> code_list) {
			con = context;
			this.layout = layout;
			this.code_list = code_list;
			inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			// 멤버변수 초기화
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return code_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return code_list.get(position);
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

			TextView txt = (TextView) convertView.findViewById(R.id.CodeName);
			txt.setText(code_list.get(position).CodeName);
			txt.setTextSize(20.0f);

			if (code_list.get(position).emphasis == true) { // 색깔 입히는 부분
				txt.setTextColor(Color.RED);
			} else {
				txt.setTextColor(Color.BLACK);
			}
			return convertView;
		}
	}
}
