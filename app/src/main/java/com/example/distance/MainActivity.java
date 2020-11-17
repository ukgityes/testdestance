package com.example.distance;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends AppCompatActivity {

    private Button button_distance;
    private Button button_start;
    //현재 수신중인지
    private TextView text_status;
    //거리계산결과 텍스트
    private TextView text_distance;
    private TextView test;


    private Location firstLocation;
    private Location lastLocation;
    private boolean startbool = false;
    private int timmer = 0;

    private CountDownTimer mCountDown;
    private TextView countdown;

    private TextView text_goal;

    private int goaldistance = 100;
    private int goaltime = 20;

    private int resultdis = 0;

    private LocationManager lm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 목표 거리 시간 넘겨받아야함 인텐트 나중에

        // 목표 텍스트
        text_goal = findViewById(R.id.text_goal);

        text_goal.setText("목표 : " + goaldistance + "m, " + goaltime + "초");

        //카운트다운 변수에 시간간격동안 타이머 동작
        //time : 제한 시간 * 1000 (int타입)
        //countDownInterval : 시간 간격 (1000이 1초입니다)
        countdown = findViewById(R.id.countdown);
        mCountDown = new CountDownTimer(Integer.parseInt((String.valueOf(goaltime) + "000")), 1000) {

            //타이머가 종료될 때까지 동작하는 함수
            @Override
            public void onTick(long millisUntilFinished) {
                countdown.setText(String.valueOf(millisUntilFinished / 1000));

                //5초 이하는 남은 시간이 빨간색으로 표시
                if (millisUntilFinished / 1000 <= 5) {
                    countdown.setTextColor(Color.parseColor("#ff0000"));
                }
            }

            //타이머가 종료될 때 실행 동작하는 함수
            @Override
            public void onFinish() {
                Log.i("test", "stop");

                lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
                //성공 or 실패 다이얼로그
                showCustomDialog(goaldistance, resultdis);
            }
        };

        //권한
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }


        text_distance = findViewById(R.id.text_distance);

        text_status = findViewById(R.id.text_status);
        text_status.setText("위치정보 미수신중");

        // LocationManager 객체를 얻어온다
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // 거리측정 버튼
        button_start = findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    if(startbool==false){
//                        text_status.setText("위치정보 미수신중");
//                    }
                    text_status.setText("수신중..");
                    // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                    lm.requestLocationUpdates(GPS_PROVIDER, // 등록할 위치제공자
                            100, // 통지사이의 최소 시간간격 (miliSecond)
                            1, // 통지사이의 최소 변경거리 (m)
                            mLocationListener);
                } catch (SecurityException ex) {
                }
            }
        });
    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.


            // 처음 실행될때만 카운트 함수 시작
            if (startbool == false) {
                startbool = true;
                mCountDown.start();
            }

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자

            //처음 위치값을 넣는곳
            if (firstLocation != null) {
                //시간 간격
//                deltaTime = (location.getTime() - mLastlocation.getTime()) / 1000.0;
//                tvTimeDif.setText(": " + deltaTime + " sec");  // Time Difference
//                tvDistDif.setText(": " + mLastlocation.distanceTo(location) + " m");  // Time Difference
//                // 속도 계산
//                speed = mLastlocation.distanceTo(location) / deltaTime;
                resultdis = resultdis + (int) firstLocation.distanceTo(location);
                text_distance.setText("움직인 거리 : " + resultdis + " m");
            }
            firstLocation = location;


        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "11111onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "11111onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "1111onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };



    public void showCustomDialog(int goal, int score) {

        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_timeover, null, false);

        //미션 성공 or 실패를 출력하는 텍스트뷰
        TextView tv_gameover = (TextView) view.findViewById(R.id.tv_gameover);
        //목표값을 출력하는 텍스트뷰
        TextView tv_goal_value = (TextView) view.findViewById(R.id.tv_goal_value);
        //점수를 출력하는 텍스트뷰
        TextView tv_score_value = (TextView) view.findViewById(R.id.tv_score_value);

        //미션 성공 or 실패 판별
        //score는 내가 수행한 값
        //goal은 목표값
        if (score >= goal) {
            tv_gameover.setText("미션 성공!");
        } else {
            tv_gameover.setText("미션 실패");
        }

        //목표값
        tv_goal_value.setText(goal+"");

        //점수
        tv_score_value.setText(score+"");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("✓ 확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //메인화면으로 가는 코드

            }
        });

        builder.setNegativeButton("↺ 다시하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //다시 시작하는 코드
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        builder.setView(view);
        builder.show();
    }
}