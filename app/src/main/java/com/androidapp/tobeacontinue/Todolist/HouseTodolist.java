package com.androidapp.tobeacontinue.Todolist;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.tobeacontinue.R;
import com.androidapp.tobeacontinue.database.MemoDBHelper;
import com.androidapp.tobeacontinue.midascon.BeaconListAdapter;
import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.BeaconCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;

import java.util.List;


public class HouseTodolist extends AppCompatActivity implements BeaconCallback, Runnable{
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    private ContextManager contextManager;
    private BeaconListAdapter adapter;

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;             // 비콘 노티 알림


    RecyclerView recyclerView;                  //리사이클러뷰
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;                              //작성 버튼
    Toolbar toolbar;

    MemoDBHelper DBHelper;                      //DB 만들기
    List<Memo> memoList;                        //Memo 리스트 만들기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_todolist);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBHelper = new MemoDBHelper(HouseTodolist.this);
        memoList = DBHelper.selectAll1();       //메모리스트 -> DB 조회


        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HouseTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter=new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);

        btnAdd.setOnClickListener(new View.OnClickListener(){       //작성 버튼 누를시 memowrite.class로 이동!!
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(HouseTodolist.this, MemoWrite.class);
                intent.putExtra("num", "1");                       //requestCode로 4개 액티비티 구분 지어서 resultCode 나눠 받기 위해 num에 1이란 데이터 넣어서 전달
                startActivityForResult(intent,1);
            }
        });

        //비콘
        contextManager = getMidasApplication().getContextManager();
        contextManager.getBeaconSettings().setMidasScanMode(true);

        adapter = new BeaconListAdapter(getBaseContext());
        startService(new Intent(getApplicationContext(), BeaconListAdapter.class));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== resultCode){
            if(resultCode==1) {                             //resultCode 1로 받을 때
                String strMain = data.getStringExtra("main");   //내용 받기
                String strSub = data.getStringExtra("sub");     //날짜 받기

                Memo memo = new Memo(0, strMain, strSub, 0);
                recyclerAdapter.addItem(memo);
                recyclerAdapter.notifyDataSetChanged();

                DBHelper.insertMemo1(memo);                 //memowrite.class에서 데이터 받은 것 DB에 저장
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconCallback(int i, Beacon beacon) {
        if(beacon.getMac().equals("10-78-ce-30-02-fb") && memoList.size() != 0){
            if(beacon.getRssi()+70>0) {             //거리 약 1m 50cm ~ 2m 사이
                if (adapter != null)
                    adapter.addBeacon(beacon);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showNotification(beacon);
                }

                runOnUiThread(this);
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(Beacon beacon) {
        //오레오 (API26)이상부터 채널을 추가해야 notification 사용 가능
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);

        if (beacon == null)
            return;
        int notify = beacon.getId().hashCode();
        Intent intent = new Intent(getApplicationContext(), HouseTodolist.class);        //상단의 노티 클릭하면 menuactivity로 넘어옴.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("title", "비콘 들어옴");
        intent.putExtra("message", beacon.getId());
        intent.putExtra("notify", notify);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID);
        builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), notify, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker(getString(R.string.Beacon));
        builder.setContentTitle(getString(R.string.house_message));
        builder.setContentText(getString(R.string.Beacon_Alarm));

        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
        style.bigText(getString(R.string.Beacon_Alarm));
        style.setBigContentTitle(getString(R.string.house_message));
        style.setSummaryText(getString(R.string.app_name));

        manager.notify(notify, style.build());
    }

    @Override
    public void run() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public MidasApplication getMidasApplication() {
        return (MidasApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                // 콜백 등록
                contextManager.setBeaconCallback(this);
                contextManager.startLeScan();
            } else {
                contextManager.stopLeScan();

                //블루투스 권한 설정 이유 설명, 블루투스 설정창으로 넘어가기
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.bluetooth_access));
                builder.setMessage(getString(R.string.bluetooth_access_alert));
                builder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                                startActivity(settingsIntent);
                            }
                        });
                builder.setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_access_toast), Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        }
    }

    //뒤로 가기 눌렀을 때도 정상 작동
    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                // 콜백 등록
                contextManager.setBeaconCallback(this);
                contextManager.startLeScan();
            } else {
                contextManager.stopLeScan();
            }
        }
    }

    //리사이클러 어댑터 클래스
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
        private List<Memo> listdata;                        //메모리스트

        AlertDialog.Builder builder;

        public RecyclerAdapter(List<Memo> listdata){
            this.listdata=listdata;
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        public List<Memo> getListdata() {
            return listdata;
        }



    @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.memo_item,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            final Memo memo=listdata.get(i);

            itemViewHolder.maintext.setTag(memo.getId());

            itemViewHolder.maintext.setText(memo.getContents());
            itemViewHolder.subtext.setText(memo.getCreateDateStr());

        }

        void addItem(Memo memo){
            listdata.add(memo);
        }

        void removeItem(int position){
            listdata.remove(position);
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            private TextView maintext;
            private TextView subtext;

            public ItemViewHolder(@NonNull View itemView){
                super(itemView);

                maintext=itemView.findViewById(R.id.contentsTextView);
                subtext=itemView.findViewById(R.id.dateTextView);

                builder = new AlertDialog.Builder(HouseTodolist.this);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {             //메모 리스트에서 원하는 아이템 길게 누르면 데이터 삭제

                        final int position = getAdapterPosition();
                        final int id = (int)maintext.getTag();

                        builder.setTitle(getString(R.string.delete_Memo));
                        builder.setMessage("\n");
                        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(position != RecyclerView.NO_POSITION){
                                    DBHelper.deleteMemo1(id);
                                    removeItem(position);
                                    notifyDataSetChanged();
                                }
                            }
                        });

                        builder.setNegativeButton(getString(R.string.no), null);
                        builder.create().show();

                        return true;
                    }

                });

            }
        }
    }

}
