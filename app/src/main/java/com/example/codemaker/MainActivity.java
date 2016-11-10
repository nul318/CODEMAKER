package com.example.codemaker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Releasable;

public class MainActivity extends Activity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		this.getActionBar().hide();

        Intent intent = getIntent();
        final String user_id = intent.getStringExtra("id");

//        final AnimationSet animSet = new AnimationSet(true);
//        Animation fade = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade);
//        animSet.addAnimation(fade); // 애니메이션 추가

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_layout);
//        layout.startAnimation(animSet);

        //
        // Button making = (Button) findViewById(R.id.making_bt); // 硫��댄�� 踰���
        // Button download = (Button) findViewById(R.id.downlod_bt); // �ㅼ�대���
        // 踰��� ; 鍮����깊��
        // Button chat = (Button) findViewById(R.id.chat_bt); // 而ㅻ�ㅻ���� 踰��� ;
        // 鍮����깊��
        //
        RelativeLayout making = (RelativeLayout) findViewById(R.id.making);
        RelativeLayout board = (RelativeLayout) findViewById(R.id.Board);
        RelativeLayout chat = (RelativeLayout) findViewById(R.id.chat);
        RelativeLayout message = (RelativeLayout) findViewById(R.id.message);
        RelativeLayout logout = (RelativeLayout) findViewById(R.id.logout2);

        View.OnClickListener listener_logout = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub
//                v.startAnimation(animSet);

//                SharedPreferences setting; //자동로그인을 위한 셋팅
//                final SharedPreferences.Editor editor;
//
//                setting = getSharedPreferences("setting", 0);
//                editor = setting.edit();//에디터 ㅇㅅㅇ
//
//                editor.clear();
//                editor.apply();


                Intent intent = new Intent(MainActivity.this, Launcher.class);
                startActivity(intent);
                finish();


            }

            ;
        };




        View.OnClickListener listener1 = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub
//                v.startAnimation(animSet);

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(MainActivity.this, MakingMusic.class);
                                startActivity(intent);

                            }
                        });

                    }
                }).start();

            }

            ;
        };

        View.OnClickListener listener2 = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub
//                v.startAnimation(animSet);

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(MainActivity.this, Board.class);

                                intent.putExtra("id", user_id);

                                startActivity(intent);
                            }
                        });

                    }
                }).start();

            }

            ;
        };
        View.OnClickListener listener3 = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub
//                v.startAnimation(animSet);

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(MainActivity.this, Chat.class);

                                intent.putExtra("id", user_id);

                                startActivity(intent);

                            }
                        });

                    }
                }).start();

            }

            ;
        };
        View.OnClickListener listener4 = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub
//                v.startAnimation(animSet);

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(MainActivity.this, Message.class);

                                intent.putExtra("id", user_id);

                                startActivity(intent);

                            }
                        });

                    }
                }).start();
            }

            ;
        };
        //
        making.setOnClickListener(listener1);
        board.setOnClickListener(listener2);
        chat.setOnClickListener(listener3);
        message.setOnClickListener(listener4);
        logout.setOnClickListener(listener_logout);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        menu.clear();
//        menu.add(0, 1, 0, "로그아웃");
//
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == 1) {
//
//            SharedPreferences setting; //자동로그인을 위한 셋팅
//            final SharedPreferences.Editor editor;
//
//            setting = getSharedPreferences("setting", 0);
//            editor = setting.edit();//에디터 ㅇㅅㅇ
//
//            editor.clear();
//            editor.apply();
//
//
//
//
//            Intent intent = new Intent(MainActivity.this, Launcher.class);
//            startActivity(intent);
//            finish();
//
//
//
//
//
//
//
//
//
//
//
//
//
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
