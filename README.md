# 목차

## 1. 앱 소개
1-1. 주제 선정이유   
1-2. 비콘

## 2. 기능 구현
#### [DATABASE]
2-1. SQLite   
2-2. 메모, 사진 저장   
2-3. 날짜, 시간 저장     

#### [앱 연동]
2-4. everytime앱과 연동    

#### [Alarm]
2-5. Alarm Service   
2-6. DatePicker   
2-7. TimePicker   

#### [BEACON]
2-8. BEACON Notification   
2-9. 거리 인식   

#### [Geofence & Geocoder]
2-10. Google Map API   
2-11. Geocoder   
2-12. Geofence  
2-13. Geofence Notification   

#### [환경설정]
2-14. 소리 설정   
2-15. 언어 설정   

#### [디자인]
2-16. Splash 화면   
2-17. Nevigation Drawer   
2-18. Tool Bar   
2-19. 기종에 상관없는 디자인   

## 3. 시장성, 수익화 방안

## 4. 참고자료

## 5. 동작 영상

<hr>

# **1. 앱 소개**   

## 주제 선정 이유

<img src="https://user-images.githubusercontent.com/62587097/84929189-73465580-b10a-11ea-97ca-e5ef814fb486.jpg" width="55%">    

* 위의 그림과 같이 시간의 경과에 따라서 기억력은 점점 감소하게 된다. 이로 인한 부주의로(가스불 화재, 현관문 열고 외출 등) 사고로 이어지는 위험한 상황이 벌어질 수 있다. 따라서, 할 일를 기록하고 할 일이 해야하는 장소에 따라 시각적으로 보여주는 것이 큰 도움이 될 것이다.    
이 프로젝트는 평소에 메모를 해도 자주 잊어버려 할 일을 제대로 못한 경우를 대비하기 위해 정해진 장소에 도착했을 때 알림이 울리면 할 일을 까먹지 않게 하는 목적으로 ‘To Beacontinued’앱의 주제를 선정하였다.

* To Beacontinued앱에서는 현재 학생들이 자주 이용하는 한누리관과 편의점, 서점, 미용실 등 편의시설이 있는 학생회관, 가장 오래 머물고 할 일을 자주 잊는 집 안과 집 밖을 비콘의 설치 장소로 정하였다.   
비콘이 없는 장소일 경우 GPS(Geocoder, Geofence)를 이용해 알림을 받을 수 있다.   
알림을 받는 또 다른 방법으로는 사용자가 원하는 날짜와 시간을 지정하여 알람을 통해 그에 맞게 알림을 받을 수도 있다.   

## 비콘

* 아래 사진은 To Beacontinued앱에서 사용한 비콘이다.

<img src = "https://user-images.githubusercontent.com/62587097/85034161-beb93c00-b1bc-11ea-8124-e3a159a95fae.png" width = 30%>     

* 비콘(Beacon)이란 BLE(Bluetooth Low Energy)를 활용한 근거리 데이터 통신 기술이다.  
    
    ▷ 넓은 송수신 범위
    
    ▷ 일방향 통신이며 페어링 과정이 필요 없음
    
    ▷ 5~10cm 단위의 구별이 가능할 정도로 정확성이 우수함
    
    ▷ 전력소모가 작음

* 이에 따라, 광범위한 범위로 정확성이 떨어지고 높은 빌딩에 둘러싸인 곳이나 지하, 실내 등에서 위치가 잘 파악되지 않는 GPS가 아닌 건물이나 지하에서도 알림을 받을 수 있고 정확한 거리를 파악할 수 있는 비콘(Beacon)이 적합하여 이 프로젝트에 사용하였다.   

<hr>

# 2. 기능구현

## SQLite

<img src = "https://user-images.githubusercontent.com/62948547/84872680-28522100-b0bd-11ea-8479-225b8c9637e8.png" width = 30%>   

* 구글 안드로이드 운영 체제에 기본 탑재된 데이터베이스이며, 경량하다는 것이 특징이다.<br>MySQL이나 PostgreSQL 등의 다른 데이터베이스와 다르게, 서버가 아닌 응용 프로그램에서 동작하기 때문에 복잡하거나 큰 데이터를 보관하는 데에는 적절하지 않다.   

* To beacontinued앱에서는 메모의 저장과 같이 간단한 동작이기 때문에 SQLite를 사용하였다.

## 메모, 사진 저장

#### 메모 저장

###### 메모 저장 예시   
<img src= "https://user-images.githubusercontent.com/62948547/84875845-7bc66e00-b0c1-11ea-9099-4317fd6fe84d.PNG">
                  
* 각각 액티비티에서 SQLiteOpenHelper를 상속받아 Beacon Fragment에서 필요한 장소 <집, 밖, 한누리관(학교), 학생회관(학생식당)> 4개의 장소에 해당하는 DB Table을 만들었다.   
<pre><code>
public class MemoDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "memo_reminder";    //Database Name
    private static final String TABLE1 = "HouseMemo";               //House Table Name
    private static final String TABLE2 = "OutsideMemo";             //Outside Table Name
    private static final String TABLE3 = "SchoolMemo";              //School Table Name
    private static final String TABLE4 = "CafeteriaMemo";           //Cafeteria Table Name
    
    ```
}
</code></pre>

* 각 테이블에는 <id값, 내용, 날짜, 완료여부> 4개의 속성 값을 추가하였다.      
그 이후에 인스턴스(instance)를 관리하는 조회(SELECT), 삽입(INSERT), 삭제(DELETE) 함수를 추가하였다. 
<pre><code>
//Table 만들기
@Override
public void onCreate(SQLiteDatabase db) {

    String create1 = "CREATE TABLE "+TABLE1+" (id INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, isdone INTEGER) ";
    String create2 = "CREATE TABLE "+TABLE2+" (id INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, isdone INTEGER) ";
    String create3 = "CREATE TABLE "+TABLE3+" (id INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, isdone INTEGER) ";
    String create4 = "CREATE TABLE "+TABLE4+" (id INTEGER PRIMARY KEY AUTOINCREMENT, maintext TEXT, subtext TEXT, isdone INTEGER) ";
    //각 장소 별로 create table -> id, 내용, 날짜, 완료 여부

    db.execSQL(create1);
    db.execSQL(create2);
    db.execSQL(create3);
    db.execSQL(create4);
    //db 실행
}
</code></pre>

###### db에 data 삽입 구조
<pre><code>
//INSERT INTO MemoTable VALUES(NULL,'MAINTEXT','SUBTEXT',0);
public void insertMemo1(Memo memo){

    String sql1 = "INSERT INTO "+TABLE1+" VALUES(NULL, '"+memo.contents+"', '"+memo.createDateStr+"', "+ memo.getIsdone()+");";
    db.execSQL(sql1);

}
</code></pre>

###### db에 data 삭제 구조
<pre><code>
//DELETE FROM MemoTable WHERE id = 0;
public void deleteMemo1(int position){

    String sql1 = "DELETE FROM "+TABLE1+" WHERE id = "+position+";";
    db.execSQL(sql1);

}
</code></pre>

###### db data조회 구조
<pre><code>
//SELECT * FROM MemoTable;
public ArrayList<Memo> selectAll1(){

    db = this.getReadableDatabase();
    String sql = "SELECT * FROM "+TABLE1;

    ArrayList<Memo> list = new ArrayList<>();

    Cursor results = db.rawQuery(sql,null);
    results.moveToFirst();

    while(!results.isAfterLast()){                  //DB 마지막까지 반복문 돌기
        Memo memo = new Memo(results.getInt(0),results.getString(1),results.getString(2),results.getInt(3));
        list.add(memo);
        results.moveToNext();
    }
    
    return list;
}
</code></pre>

* 각 비콘 액티비티는 recyclerView를 이용하여 메모 DB에 저장된 data값들을 List<>형식으로 보여준다.
<img src="https://user-images.githubusercontent.com/62948547/85099849-8277f100-b239-11ea-8437-36f0f7fb473b.PNG" width="60%">
<pre><code>
< androidx.recyclerview.widget.RecyclerView
     android:id="@+id/recyclerview"
     android:layout_width="match_parent"
     android:layout_height="match_parent" />
</code></pre>

* 먼저 각 액티비티에 MemoDBHelper 객체를 정의한 후, selectAll()이라는 조회 함수를 이용해서 DB에 저장되어 있는 data값들을 recyclerView에 보여준다.       

* 작성버튼을 클릭하면 메모 작성 창으로 이동한다. 메모 작성 창은 4개의 액티비티가 공유하고 있기 때문에, 각 액티비티를 구분하기 위해서 requestCode를 intent 값으로 넘겨주어 그 값에 따라 처리할 수 있도록 하였다.

* 다른 액티비티로부터 결과 값을 받아야 하기 때문에 startActivityForResult()함수를 이용하였다.

<pre><code>
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_house_todolist);
    
    ```
    
    DBHelper = new MemoDBHelper(HouseTodolist.this);
    memoList = DBHelper.selectAll1();       //메모리스트 -> DB 조회
    
    ```
    recyclerAdapter=new RecyclerAdapter(memoList);
    recyclerView.setAdapter(recyclerAdapter);               //recyclerView에 메모리스트 보여줌
           
           
    btnAdd.setOnClickListener(new View.OnClickListener(){       //작성 버튼 누를시 memowrite.class로 이동!!
    @Override
    public void onClick(View view) {
        //새로운 메모작성
        Intent intent=new Intent(HouseTodolist.this, MemoWrite.class);
        intent.putExtra("num", "1");                       
        //requestCode로 4개 액티비티 구분 지어서 resultCode 나눠 받기 위해 num에 1이란 데이터 넣어서 전달
        startActivityForResult(intent,1);
    }
});
</code></pre>

* 메모 작성 창에서는 받은 requestCode값으로 나눠준 후, 내용과 날짜 값을 intent로 다시 넘겨준다. 그 때 resultCode값도 각 액티비티마다 바꿔서 전달해준다. 날짜 값은 Date객체를 이용하여 자동적으로 format되므로 작성한 날짜 값이 넘겨진다.

* 완료 버튼이나 취소 버튼을 클릭하면 그 창이 자동으로 닫힐 수 있게 finish() 함수를 넣어주었다.

<pre><code>
public class MemoWrite extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write);
        ```

        if(requestCode==1){                                         //각각의 경우에 따라 setResult(resultCode, intent) 나눠줌
            findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        Intent intent = new Intent();
                        intent.putExtra("main", str);
                        intent.putExtra("sub", substr);
                        setResult(1, intent);                   //resultCode == 1
                        finish();

                    }
                }
            });

            findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
</code></pre>

* 각 액티비티에서 메모 작성 창에서 넘겨 받은 intent값들을 Memo 객체에 저장한 후 DBHelper에 insertMemo()함수로 저장해준다.

<pre><code>
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
</code></pre>

* recyclerView에 보여줄 수 있도록 만들어 주는 객체인 recyclerAdapter 클래스에 longClickListener()함수를 넣어 메모를 길게 누를 시 삭제가 되도록 하였다.   
메모를 길게 누를 시 alertbuilder를 통해 확인 메세지가 뜨고 '예'버튼을 클릭할 시 DBHelper에 deleteMemo()함수를 이용하여 DB에서 삭제된다.

<pre><code>
itemView.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View view) {             //메모 리스트에서 원하는 아이템 길게 누르면 데이터 삭제

    builder.setTitle(getString(R.string.delete_Memo));
    
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
</code></pre>
<img src = "https://user-images.githubusercontent.com/62948547/85100656-a2a8af80-b23b-11ea-9882-3865948d3b86.PNG" width = "60%">

* 다음 사진은 메모 데이터베이스의 최종 모습이다.
<img src = "https://user-images.githubusercontent.com/62948547/84997758-ef33b280-b189-11ea-9503-76447f7de25d.png">

#### 사진 저장

* 사진을 저장하는 DB는 SchoolTodolist.java에 이미지를 로드한 뒤 저장하기 위해 사용하였다.
밑의 코드와 같이 데이터를 직접 읽고 쓸 수 있게 하여 table_image 생성을 SchoolTodolist.java에 추가하였다.

<pre><code>
public class ImageDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "imageDB";      //Database Name
    private static final String DB_TABLE = "table_image";       //Table Name

    public void queryData(String sql){                          //데이터 쓰기
        SQLiteDatabase db= getWritableDatabase();
        db.execSQL(sql);
    }

    public void insertImage(byte[] image){                      //이미지 삽입
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO " + DB_TABLE + " VALUES(?);"; //INSERT INTO imageDB VALUES(image);
        SQLiteStatement statement = db.compileStatement(sql);
        statement.bindBlob(1,image);                            //image는 blob 타입으로 저장

        statement.executeInsert();
    }

    public Cursor getData(String sql){                          //데이터 읽기
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }

}
</code></pre>

* 이미지는 '이미지 넣기' 버튼을 클릭하면 갤러리를 통해 사진을 선택할 수 있게 하였다. 갤러리에서 선택한 사진을 Uri 객체를 통해 받은 뒤 Bitmap 객체로 바꿔주어야 이미지를 볼 수 있다.   
크기를 지정해 준 후 imageView에 로드하였다.
<img src = "https://user-images.githubusercontent.com/62948547/85101962-93773100-b23e-11ea-9045-e7c5913cd68e.png">
<pre><code>
    //갤러리로부터 이미지 불러오기
    public void loadImagefromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);

        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }
    
    ```
    
    //불러온 뒤 이미지 로드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    
    ```
    try {                                                       // 갤러리에서 이미지 로드할 때 오는 resultcode
            if(requestCode == PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && null!=data) {
                Uri uri = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                imgView = findViewById(R.id.imageView);
                imgView.setImageBitmap(scaled);                     //이미지 불러온 후
                imageDBHelper.insertImage(imgViewToByte(imgView));  //DB에 img를 byte로 변환 후 저장

            }
        } catch (Exception e) {
            Toast.makeText(this,"로딩에 오류가 있습니다.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    
 </code></pre>

* 이미지는 BLOB 타입으로 DB에 저장해야 하는데, 다음의 함수를 이용해서 bitmap byte 변환을 통해 저장하고 byte bitmap 변환을 통해 사진을 불러온다.   

<pre><code>
private byte[] imgViewToByte(ImageView imageView) {             //이미지 바이트 변환 함수
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public Bitmap byteArrayToBitmap( byte[] $byteArray ) {          //바이트 이미지 변환 함수
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
    }
</code></pre>

## 날짜, 시간 저장

* 날짜, 시간 저장도 메모 저장과 같은 형식으로 insertMemo, deleteMemo를 처리해주었다. 데이터 베이스 테이블에 <id값, 메모 내용, 날짜, 시간, 완료여부> 5개의 속성 값을 추가하게 하였다.
<pre><code>
//INSERT INTO MemoTable VALUES(NULL,'MAINTEXT','SUBTEXT','TIMETEXT',0);
    public void insertMemo(CalendarMemo memo){
        String sql="INSERT INTO "+table+" VALUES(NULL,'"+memo.maintext+"','"+memo.subtext+"','"+memo.timetext+"',"+memo.getIsdone()+");";
        db.execSQL(sql);
}
</code></pre>
<img src = "https://user-images.githubusercontent.com/62948547/85102121-f072e700-b23e-11ea-856d-6861d77d605c.png" width = "30%">

<hr>

## Everytime앱과 연동

* 사용자를 한 활동에서 다른 활동으로 안내하려면 앱은 Intent를 사용하여 앱이 실행하려는 작업, 즉 '인텐트'를 정의해야 한다. 또, 연동할 앱의 패키지이름을 통해 앱과 연동을 시킨다.

* CafeteriaTodolist.java에 다음과 같이 코드를 추가한다.    
해당 패키명으로 등록된 Launcher를 찾아 실행시켜 값을 전달하는 방식이다.  
getPackageList()를 통하여 사용자의 핸드폰에 해당 앱이 설치되어 있는지 확인한다.   

* Everytime 앱이 없다면 해당 패키명으로 등록된 Launcher를 찾지 못하기 때문에 실행되지 않는다.

<pre><code>
private Intent intent;
private final String packageName = "com.everytime.v2"; 

  //생략...
  
  intent = this.getPackageManager().getLaunchIntentForPackage(packageName);
  Button etabutton =(Button)findViewById(R.id.etabutton);

   etabutton.setOnClickListener(new View.OnClickListener(){
        @Override
            public void onClick(View view) {

                //에브리타임 앱이 있다면
                if(getPackageList())
                {
                    CafeteriaTodolist.this.startActivity(intent);       //이미지 버튼을 클릭하면 패키지이름인 에브리타임 앱으로 연동됨
                } else //에브리타입 앱이 없다면
                {
                    Toast.makeText(CafeteriaTodolist.this, getString(R.string.eveytime_not_exist), Toast.LENGTH_SHORT).show();
                }   
     });   
 
  //생략...
  
  //에브리타임 앱이 있는지 확인
    public boolean getPackageList(){
        boolean isExist = false;

        //queryIntentActivities()를 호출하여 Intent를 처리할 수 있는 활동 목록을 가져와야 함

        PackageManager pkgMgr = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        //List가 비어있지 않다면 안전하게 인텐트를 사용할 수 있음
        try {
            for (int i = 0; i < mApps.size(); i++) {
                if (mApps.get(i).activityInfo.packageName.startsWith(packageName)) {
                    isExist = true;
                    break;
                }
            }
        } catch (Exception e) {
            isExist = false;
        }

        // isExist가 true면 최소한 하나의 앱이 인텐트에 응답. false면 인텐트를 처리할 앱이 없음
        return isExist;
    }
    
</code></pre>

* 구현화면

<img src="https://user-images.githubusercontent.com/62587097/84897223-3a918680-b0e0-11ea-9f14-9fa6ccea42c8.png" width="30%"><img src="https://user-images.githubusercontent.com/62587097/84921174-185b3100-b0ff-11ea-94d1-cad83a04c45a.png" width="30%">

<hr>

## Alarm Service

* 알람은 foreground, background에서의 서비스가 필요한데 이를 하기 위해서 AndroidManifest.java파일에 foreground에서 작동될 수 있는 퍼미션과 BroadcasReceiver를 추가해주고 background동작을 위해 service(AlarmService.java 파일에서 구현)를 사용한다. 

 <pre><code>
   < uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
  
    //receiver와 service 추가 
 
     < receiver
            android:name=".Calendar.AlarmReceiver"
            android:enabled="true"
            android:exported="false" />

        < service
            android:name=".Calendar.AlarmService"
            android:enabled="true" />
 </code></pre>
 
* 오레오(Oreo API 26) 이상부터는 채널을 추가해야 Notification을 사용할 수 있다. AlarmService.java파일에 CHANNEL ID를 등록하고 알람에 대한 서비스를 구현한다.    
service는 background에서 동작하도록 도와주며 createNotification을 사용해 특정 시간에 알림을 해줄 수 있다.
 <pre><code>
     private String createNotificationChannel(){
        String channelid = "Alarm";
        String channelName = getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(channelid,channelName,
        NotificationManager.IMPORTANCE_NONE);
        
        channel.setSound(null,null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        return channelid;
    }
 </code></pre>
  
* foreground에서 실행되기 위해 notification을 보여준다. 
  <pre><code>
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelid = createNotificationChannel();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelid);
            Notification notification = builder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            startForeground(1, notification);

        }
  </code></pre>
  
* 알람이 울리면 알람종료페이지가 뜨는 AlarmRingActivity.java를 호출한다.
  
 <pre><code>
   Intent intent1=new Intent(this,AlarmRingActivity.class);
 </code></pre>
 
 * 새로운 Task를 생성해 종료페이지activity를 최상위로 올린다.
 <pre><code>
    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);

        Log.d("AlarmService","Alarm");
        
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
           stopForeground(true);
        }

        stopSelf();
       return START_NOT_STICKY;
 </code></pre>

* 다음으로 AlarmActivity.java파일에 알람동작 setAlarm()를 추가한다.    
receiver를 설정하고 AlarmManager를 통해 getSystemService(Alarm_service)를 동작한다. 
<pre><code>

   //알람등록
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(){
    
        //Receiver 설정
        Intent intent =new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //알람설정
        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, this.calendar.getTimeInMillis(),pendingIntent);


        //메모 디비로 넘기기위한 sumpledateformat 사용
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm",Locale.getDefault());

        //메모로 넘기기
        String context = edtText.getText().toString();
        String date = format.format(calendar.getTime());
        String time= format1.format(calendar.getTime());

        Toast.makeText(AlarmActivity.this, context + ", " + date+", " + time, Toast.LENGTH_SHORT).show();

        if (context.length() > 0) {

            Intent intent1 = new Intent();
            intent1.putExtra("context", context);
            intent1.putExtra("date", date);
            intent1.putExtra("time", time);

            setResult(5, intent1);

            
            finish();
          }
     }
          
</code></pre>
 * 일정등록 버튼을 누르면 timepicker, datepicker에서 설정한 시간, 날짜 값을 받아 메모 내용과 함께 DB에 저장되면서 알람이 등록된다. 시간에 맞춰 알람이 울릴 때 나타나는 알람종료 창의 알람종료 버튼을 누르면 알람이 꺼진다.
 
<img src="https://user-images.githubusercontent.com/62587097/84996509-5486a400-b188-11ea-857b-30d61be2df80.jpg" width="30%" height="487"><img src="https://user-images.githubusercontent.com/62587097/84996787-b2b38700-b188-11ea-8e49-2dcd7a04463f.png" width="30%">

## DatePicker

* Calendar는 날짜와 시간을 객체 모델링화한 클래스이다. 년, 월, 일, 요일, 시간, 분 등과 같은 날짜, 시간 정보를 제공한다.
<img src="https://user-images.githubusercontent.com/62587484/85038592-b9aabb80-b1c1-11ea-8adc-96cc898f96ad.png" width="30%">

* AlarmActivity.java 파일에 다음과 같이 코드를 추가한다. 날짜는 Calendar, DatePickerDialog를 사용해 구현하였다.   

▷ 시스템의 현재 날짜와 시간정보를 얻기 위해 getInstance()메서드를 사용한다.   

▷ 객체가 갖고 있는 날짜 시간 정보는 get()메서드를 이용하여 사용한다.   

▷ DatePickerDialog는 날짜를 선택하는 뷰를 만들 때 사용한다.   

▷ dialog.show()를 통해 팝업창을 띄어 사용한다.   
   
<pre><code>
      private Calendar calendar;  
      
      //생략...
      
      @Override
    protected void onCreate(Bundle savedInstanceState) {
    
      //생략...
      this.calendar=Calendar.getInstance();
      
      //현재 날짜 표시
      displayDate();
      }
     
     //생략...
     
     private void displayDate(){
          SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
          edtDate = findViewById(R.id.txtDate);
          edtDate.setText(format.format(this.calendar.getTime()));
      
      //생략...
      
      private  void showDatePicker(){
            DatePickerDialog dialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    //알람날짜 설정
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                    //날짜 표시
                    displayDate();
                }
            } ,this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH),this.calendar.get(Calendar.DAY_OF_MONTH));

            dialog.show();
        }
        
  </code></pre>
   
* AlarmActivity.java에서 switch case문으로 달력 버튼 클릭 시에 DatePickerDialog, 즉 달력창을 보여준다.
  
     <pre><code>
      View.OnClickListener mClickListener=new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btnCalendar:
                    showDatePicker();
                    break;

              //생략...

            }
        }
    };
    
  </code></pre>

## TimePicker

* calendar객체를 사용해서 시간 값을 받아온다. 시간과 분이 과거보다 이전으로의 설정은 허용되지 않게 하였다.

<pre><code>
   private TimePicker timePicker;      //timepicker 선언 (시간 설정)

//생략...

 @RequiresApi(api = Build.VERSION_CODES.M)
   private void setAlarm(){
   this.calendar.set(Calendar.HOUR_OF_DAY, this.timePicker.getHour());
   this.calendar.set(Calendar.MINUTE, this.timePicker.getMinute());
   this.calendar.set(Calendar.SECOND,0);
 
 //생략...

  if(this.calendar.before(Calendar.getInstance())){
            Toast.makeText(this,getString(R.string.alarm_toast),Toast.LENGTH_LONG).show();
            return;
        }
        
 </pre></code>
 
 #### TimePicker_clock버전과 TimpePicker_spinner버전
  <img src="https://user-images.githubusercontent.com/62587097/85008127-7fc4bf80-b197-11ea-854d-eae0d7f3a678.jpg" width="30%"><img src="https://user-images.githubusercontent.com/62587097/85008364-d92cee80-b197-11ea-922b-c892ebaa1397.jpg" width="30%">
 
<hr>

## BEACON Notification

* 현재 midascon 비콘을 사용하기 때문에 비콘에서 제공하는 sdk를 library에 추가하였다.
<img src = "https://user-images.githubusercontent.com/62948547/85040978-566e5880-b1c4-11ea-9015-6995b5586702.PNG">

* 비콘에서 필요한 permission과 service를 manifest에 넣어주고 각 액티비티에서 midascon 모듈을 import하여 인터페이스인 beaconCallback과 Runnable을 구현하였다. 
<pre><code>
< uses-permission android:name="android.permission.WAKE_LOCK" />
< uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
< uses-permission android:name="android.permission.BLUETOOTH" />
< uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
< uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
< uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

< service android:name="com.hanvitsi.midascon.MidasService" />
</code></pre>
<img src = "https://user-images.githubusercontent.com/62948547/85041812-63d81280-b1c5-11ea-850e-9f100baf1079.PNG">

* 다음 CHANNEL 변수들은 AlarmService에서 언급한 것과 같이 오레오 (API26)이상부터 채널을 추가해야 notification을 사용 가능하기 때문에 필요한 변수들이다. contextMangager를 통해 비콘 스캔을 활성화하고 OnCreate()에 startService()로 beaconListAdapter에 스캔된 비콘들을 추가할 수 있게 된다.

<pre><code>
public class HouseTodolist extends AppCompatActivity implements BeaconCallback, Runnable{
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    private ContextManager contextManager;
    private BeaconListAdapter adapter;

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;             // 비콘 노티 알림
    
    ```
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_todolist);
        ```
        //비콘
        contextManager = getMidasApplication().getContextManager();
        contextManager.getBeaconSettings().setMidasScanMode(true);          //비콘 스캔모드 활성화

        adapter = new BeaconListAdapter(getBaseContext());
        startService(new Intent(getApplicationContext(), BeaconListAdapter.class));            
    }
    
</code></pre>

* BeaconCallback과 Runnable 인터페이스를 구현하면, run()함수와 OnBeaconCallback()함수를 구현해야 한다. OnBeaconCallback() 함수로 먼저 각 장소에 비콘의 맥주소(beacon.getMac())를 다르게 설정하여 구분지었고 DB에 메모가 없을 때는 알림이 오지 않도록 설정하였다.<br> 그리고 비콘이 인식되는 거리를 약 1m로 제한시키고 notification을 설정해주었다. <br>

* run()함수로는 BeaconAdapter를 재설정해주었다. BeaconListAdapter.java에서는 비콘을 ArrayMap<>을 통해 배열하고 비콘의 맥 주소로 비콘을 추가하거나 삭제해주는 함수를 정의한다.<br>

<pre><code>    
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
    
    ```
    
    @Override
     public void run() {
      if (adapter != null)
         adapter.notifyDataSetChanged();
     }
    
</code></pre>

* 다음은 비콘 notification 함수이다. 휴대폰 상단에 뜨는 알림 메세지를 클릭할 경우, 비콘과 연결된 액티비티(장소에 따라 메모를 저장한 화면)로 들어가게 된다. NotificationCompat.Builder를 이용하여 title에 장소가 나오고 content에는 '할 일을 확인하세요'가 뜨도록 설정하였다.

* 이 코드는 Geofence에서 알림을 받아오는 코드와 동일하다. (Geofence에서 알림을 받아오는 설명은 생략)

<pre><code>
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
        Intent intent = new Intent(getApplicationContext(), HouseTodolist.class);        //상단의 노티 클릭하면 HouseTodolist로 넘어옴.
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

        ```
    }
</code></pre>

* 각 액티비티 페이지에서 뒤로 가기로 나갔을 때도 비콘이 인식될 수 있도록 onPause()함수와 onResume()함수에 BeaconCallback을 등록하였다.

* 사용자의 휴대폰 내 블루투스가 켜져 있을 경우 비콘 activitiy(집 안, 집 밖, 한누리관, 학생회관)에 들어갈 수 있도록 설정하였고 블루투스가 꺼져 있을 경우, 블루투스의 권한 설정 이유에 대해 설명한 후 휴대폰 시스템 설정 내 블루투스 설정으로 들어가 블루투스를 킨 후에 비콘 activitiy에 들어갈 수 있게 된다. 비콘 activitiy에 들어가게 되면 그 때 비콘이 scan되기 시작한다.  
<pre><code>
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
    //onResume함수와 동일 (블루투스 설정 창 이동만 제외)
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
</code></pre>
<img src = "https://user-images.githubusercontent.com/62948547/85103363-7a23b400-b241-11ea-8ff6-b4581ac63c38.png" width = "30%">

## 거리 인식

* 다음은 Midascon에서 비콘을 관리할 수 있게 해놓은 BeaconManager 앱을 통해서 비콘을 조회한 화면이다. 
<img src = "https://user-images.githubusercontent.com/62948547/85053808-08fae700-b1d6-11ea-820d-11d5ef6e6500.png" width = "30%">

* Rssi로 거리가 측정되는데 음수 값이고, 거리가 가까울수록 값이 커지며(0에 가까워짐) 거리가 멀어질 수록 값이 작아진다. 실험을 통해 아래 코드의 범위가 대략 1m 정도로 이를 비콘의 반경으로 설정하였다.

<pre><code>
 if(beacon.getRssi()+70>0)
 </code></pre>

<hr>

## Google Map API

* Google Map을 띄우기 위해 Google Map API를 사용하는 방법이다.   
프로젝트 우클릭 > New> Google > Google Maps Activity를 클릭하여 Google Maps Activity를 생성한다.

<img src="https://user-images.githubusercontent.com/62587484/84872753-3bfd8780-b0bd-11ea-9efb-96b9b0786b1c.png" width="70%">

* res > values > google_maps_api.xml 에 들어가 아래 보이는 링크에 들어간다.   

<pre>
<code>
    //생략...
    
    https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=FA:32:8D:A5:DB:85:6A:1D:0F:62:5B:F2:75:B7:8A:29:55:9A:F5:7A%3Bcom.androidapp.tobeacontinue
   
   //생략...
   
    < string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">YOUR_KEY_HERE< /string>
© 2020 GitHub, Inc.
</code>
</pre>

* 다음 사진에 보이는 것과 같은 화면이 뜨면 프로젝트를 생성한다.   
<img src="https://user-images.githubusercontent.com/62587484/84872654-225c4000-b0bd-11ea-97c4-00de47fb995d.jpg" width="50%">

* API키 만들기를 클릭한다.   
<img src="https://user-images.githubusercontent.com/62587484/84872917-70714380-b0bd-11ea-95c1-ee0847c03cc5.jpg" width="50%">

* 생성된 키 값을 복사하여 google_maps_api.xml에 YOUR_KEY_HERE 부분에 넣어준다.   
<img src="https://user-images.githubusercontent.com/62587484/84872971-8121b980-b0bd-11ea-8224-0bcedbfd6963.jpg" width="100%">

* Gradle Scripts > build.gradle(Module: app)에 들어가 dependencies를 다음과 같이 수정한다.   
<pre>
<code>
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation files("libs/MidasconSDK_android_1.0.0.jar")

     ```
    implementation 'com.google.android.gms:play-services-maps:17.0.0'
    implementation "com.google.android.gms:play-services-location:17.0.0"
}
</code>
</pre>

* AndroidManifest.xml에서 <meta-data>부분을 다음과 같이 수정한다.
<pre><code>
  < meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="API 키"/>
  </code>
</pre>

* 마커를 추가하고 title, snippet과 마커의 반경을 정의하는 아래의 코드를 MapsAcitivity.java에 추가한다.   
지오펜싱에서 최상의 결과를 얻는 또 다른 전략은 최소 반경을 100m로 설정하는 것이기 때문에 마커의 반경은 100m로 설정한다.

<pre><code>
final MarkerOptions markerOptions = new MarkerOptions();
private float GEOFENCE_RADIUS = 100;    //Geofence 반경

  //생략...

  //마커세팅
    private void setMarker(LatLng latLng){
        mMap.clear();
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
    }
    
 //마커 title,snippet 추가
    private void addMarker(final LatLng latLng){

        //주소 입력 창에 입력한 text를 marker의 title로 사용
        //할 일 입력 창에 입력학 text를 marker의 snippet으로 사용
        String location_title = address_editText.getText().toString();
        String todo_snippet = todo_editText.getText().toString();

        markerOptions.position(latLng);
        markerOptions.title(location_title);
        markerOptions.snippet(todo_snippet);

        mMap.addMarker(markerOptions);
    }

    //반경설정
    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);       //원의 중앙을 받아온 위도, 경도로 설정
        circleOptions.radius(radius);       //반경설정
        circleOptions.strokeColor(Color.argb(255,255,0,0)); //색상
        circleOptions.fillColor(Color.argb(64,255,0,0));    //색상
        circleOptions.strokeWidth(4);   //두께
        mMap.addCircle(circleOptions);
    }
</code>
</pre>   

* 다음은 Map의 시작을 서울로 지정한 코드이다. 

<pre><code>
//맵 시작시 서울이 default
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,16));

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
    }
</code></pre>    

* 협업이나 앱의 배포 시 GoogleMap이 로드되지 않는 현상이 발생하게 된다.   
이는 release용 key를 받아 (debug)google_maps_api.xml이 아닌 (release)google_maps_api.xml에 추가하면 해결된다.   

* release용 key를 발급 받는 것은 다음의 link를 참고한다.   
https://developers.google.com/maps/documentation/android-sdk/get-api-key

    * 이 프로젝트에선 release용 key를 받아도 GoogleMap이 로드되지 않는 현상이 발생해 이를 새로운 key를 생성하고 적용하여 해결하였다.   
    * API를 발급받는 프로젝트를 새로 만드는 것도 방법이 될 수 있다.   

## Geocoder

* AndroidManifest.xml에서 INTERNET과 ACCESS_FINE_LOCATION 권한을 받아온다.
<pre>
<code>
    < uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    < uses-permission android:name="android.permission.INTERNET" />
</code>
</pre>

* MapsActivity.java에 다음과 같은 코드를 추가한다.   

* Geocoder 객체를 만들고 getFromLocationName()를 통해 주소를 얻어오게 된다.   
얻어 온 주소에서 위도와 경도만 가져와 지도 앱으로 묵시적인텐트를 날려서 해당 지도앱이 넘겨받은 위도, 경도 값으로 지도 표시를 한다. 

<pre>
<code>
final Geocoder geocoder = new Geocoder(this);

private GoogleMap mMap;

```

AddressMap_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소입력 후 지도버튼 클릭시 해당 위도경도값의 지도화면으로 이동
                List<Address> list = null;

                String str = address_editText.getText().toString();

                try {
                    list = geocoder.getFromLocationName
                            (str, // 지역 이름
                                    10); // 읽을 개수

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
                }

                if (list != null) {
                    if (list.size() == 0) {
                        result_textView.setText(getString(R.string.no_address));
                    } else {

                        //입력한 주소 결과창에 표시
                        result_textView.setText(list.get(0).getAddressLine(0).toString());

                        // 해당되는 주소로 카메라이동
                        Address addr = list.get(0);
                        double lat = addr.getLatitude();
                        double lon = addr.getLongitude();
                        LatLng searchLocation = new LatLng(lat, lon);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLocation,16));
                    }
                }
</code>
</pre>

참고 : https://bitsoul.tistory.com/135 

## Geofence

#### 지오펜스에 대하여   

▷ Geographic(지리적)과 Fencing(울타리)의 합성어로 특정 구역에 대한 사용자 출입 현황을 알려주는 API    
이는 위치 추적 기술(GPS 등)을 활용하여 실행됨

▷ 지오 펜싱은 현재 위치에 관한 사용자의 인식과 관심 위치에 관한 사용자의 근접성 인식을 결합함

▷ 각 지오 펜싱에서 위치 서비스에 방문 및 이탈 이벤트를 전송하도록 요청 가능

▷ 백그라운드에서도 동작

#### 지오펜싱 모니터링 설정
* AndroidManifest.xml에서 ACCESS_BACKGROUND_LOCATION과 ACCESS_FINE_LOCATION 권한을 받아온다.
<pre>
<code>
    < uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    < uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
</code>
</pre>

* BroadcastReceiver를 사용하여 지오펜싱 전환을 수신 대기하기 위해 application태그 안에 추가한다.   

<pre><code>
   < receiver
            android:name=".GeofenceBroadcastReceiver"
            android:enabled="true"
            android:exported="true" />
</code>
</pre>

* 위치 API에 액세스하려면 지오펜싱 클라이언트의 인스턴스를 만들어야 한다.   
MapsActivity.java에 아래와 같은 코드를 추가한다.

<pre>
<code>
private GeofencingClient geofencingClient;

@Override
    public void onCreate(Bundle savedInstanceState) {
        // ...
        //Location API 사용을 위하여 Geofencing Client 인스턴스를 생성
        geofencingClient = LocationServices.getGeofencingClient(this);
    }
</code>
</pre>
#### 지오펜싱 개체 만들기

* GeoFenceHelper.java에서 Geofence.Builder를 사용하여 지오펜싱을 만들어 원하는 지오펜싱의 반경과 지속 시간, 전환 유형을 설정한다.

<pre> <code>
public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes){

        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude,latLng.longitude,radius)  // 위치 및 반경(m)
                .setRequestId(ID)        // 이벤트 발생시 BroadcastReceiver에서 구분할 id
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(5000)        //Geofence 진입 후 머물기로 체크할 시간을 millisecond로 지정
                .setExpirationDuration(Geofence.NEVER_EXPIRE)   // Geofence 만료 시간
                .build();
    }
</code>
</pre>

#### 지오펜싱 및 초기 트리거 지정

* 모니터링할 지오펜싱을 지정하고 관련 지오펜싱 이벤트가 트리거되는 방법을 설정해준다.

<pre><code>
public GeofencingRequest getGeofencingRequest(Geofence geofence){
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }
</code>
</pre>

▷ 기기가 지오펜싱에 들어갈 때 GEOFENCE_TRANSITION_ENTER 전환이 트리거되고 기기가 지오펜싱에서 나갈 때 GEOFENCE_TRANSITION_EXIT 전환이 트리거된다. 
 GEOFENCE_TRANSITION_DWELL는 사용자가 지오펜싱 내에서 정의된 시간 동안 정지할 때만 이벤트를 트리거한다.   
 To beacontinued앱에서는 설정한 장소에 들어갈 때 알림을 띄워주기 위하여 GEOFENCE_TRANSITION_ENTER만 사용한다.
 
 * 다음은 Geofence error를 정의한 코드이다.   
 Geofence에서 거부되거나, 등록 개수(최대 100개)를 넘어서거나 PendingIntent를 너무 많이 등록했을 경우로 나뉜다.   

<pre><code>
//지오펜싱 에러메세지
    public String getErrorString(Exception e){
        if(e instanceof ApiException){
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()){
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFNECE_NOT_AVAILABLE";    //지오펜스에서 거부됨
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFNECE_TOO_MANY_GEOFENCE";    //지오펜스를 너무 많이 등록함
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFNECE_TOO_MANY_PENDING_INTENTS";     //펜딩인텐드를 너무 많이 등록함
            }
        }
        return e.getLocalizedMessage();
    }
    
</code></pre> 
 
#### 지오펜싱 전환을 위한 Broadcast Receiver 정의

* BroadcastReceiver는 지오펜싱으로 들어가는 또는 지오펜싱에서 나오는 전환과 같은 이벤트가 발생할 때 업데이트되어 장기 실행 백그라운드 작업을 시작할 수 있다.
아래 코드를 추가하여 BroadcastReceiver를 시작하는 PendingIntent 정의한다.   
GeoFenceHelper.java에 추가한다. 

<pre><code>
//PendingIntent 정의
PendingIntent pendingIntent;

public PendingIntent getPendingIntent(){
        if(pendingIntent != null){
            return pendingIntent;
        }

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,2607,intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
</code>
</pre>
<hr>

#### 지오펜싱 추가

* MapActivity.java에서 지오펜싱을 추가할 때 background permission을 요청하는 코드를 추가한다.
<pre> <code>
public void permission(LatLng latLng){
        if(address_editText.length() != 0 && todo_editText.length() != 0) {
            if (Build.VERSION.SDK_INT >= 29) {
                //background permission 필요함
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    setMarker(latLng);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                    }
                }
            } else {
            //지도에 마커 추가
                setMarker(latLng);
            }
        }
        else{
        //생략...
        }
    }
</code>
</pre>

* Map을 길게 눌렀을 경우에도 위에 언급한 permission()을 사용하여 permission확인 후 마커를 추가하도록 하였다.   

<pre><code>
//구글맵 길게 클릭 시 퍼미션 확인 후 마커표시
    @Override
    public void onMapLongClick(LatLng latLng){
        permission(latLng);
    }
</code></pre>

* ACCESS_FINE_LOCATION과 ACCESS_BACKGROUND_LOCATION에 대한 권한 요청 결과를 가져온다.   
MapsActivity.java에 아래 코드를 추가한다.   
<pre> <code>
private int FINE_LOCATION_ACCESS_REQUEST_CODE = 1001;
private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1002;

//생략...

//ACCESS_FINE_LOCATION과 ACCESS_BACKGROUND_LOCATION에 대한 권한 요청 결과를 가져옴
@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //ACCESS_FINE_LOCATION에 대한 권한 요청 결과
        if(requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission 있을 때
                mMap.setMyLocationEnabled(true);
            }else{
                //permission 없을 때
                Toast.makeText(this,getString(R.string.maps_toast2)
                        ,Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE){
        //ACCESS_BACKGROUND_LOCATION에 대한 권한 요청 결과
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission 있을 때
                Toast.makeText(this, getString(R.string.maps_toast3),Toast.LENGTH_SHORT).show();
            }else{
                //permission 없을 때
                Toast.makeText(this,getString(R.string.maps_toast4)
                        ,Toast.LENGTH_SHORT).show();
            }
        }
    }
</code>
</pre>

* GeofencingClient.addGeofence() 메서드를 사용하여 다음과 같이 지오펜싱을 추가한다.   
MapsActivity.java에 아래의 코드를 추가한다.

<pre><code>
private GeoFenceHelper geoFenceHelper;

private String GEOFENCE_ID = "JC_GEOFENCE_ID";    //Geofence ID

//생략...

//지오펜스 개체 만들기
    public void addGeofence(LatLng latLng, float radius){

        Geofence geofence = geoFenceHelper.getGeofence(GEOFENCE_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER);

        GeofencingRequest geofencingRequest = geoFenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geoFenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest,pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    //Geofence 추가 성공
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    //Geofence 추가 실패
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geoFenceHelper.getErrorString(e);
                        Log.d(TAG,"onFailure: " + errorMessage);
                    }
                });

    }
</code>
</pre>

#### 지오펜싱 전환 처리

* 위치 서비스에서 사용자가 지오펜싱에 들어가거나 나오는 것을 감지하면 지오펜싱 추가 요청에서 포함한 PendingIntent에 포함된 Intent를 전송한다. GeofenceBroadcastReceiver는 Intent가 호출된 것을 알아채고 나면 인텐트에서 지오펜싱 이벤트를 얻고 지오펜싱 전환 유형을 결정하며 정의된 지오펜싱 중에 트리거된 것을 판단할 수 있다. broadcast receiver는 앱에서 백그라운드 작업 실행을 시작하도록 하거나 원하는 경우 알림을 출력으로 전송할 수 있다.   

* 다음은 지오펜싱 전환이 발생할 때 알림을 게시하는 BroadcastReceiver 정의한 코드이다.   
GeofenceBroadcastReceiver.java에 다음 코드를 추가한다.

<pre><code>

private static final String TAG = "GeoBroadcastReceive";

    //지오펜싱 전환이 발생할 때 알림 게시 정의
    @Override
    public void onReceive(Context context, Intent intent) {
  
        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving geofence event");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        for(Geofence geofence: geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        // 발생 이벤트 타입
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "지오펜스 들어옴");
                notificationHelper.sendHighPriorityNotification("설정한 장소에 들어옴","할 일을 확인하세요", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("설정한 장소에 위치하는 중", "할 일을 확인하세요", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("설정한 장소에서 나옴", "", MapsActivity.class);
                break;
        }
</code>
</pre>

앞에서 말했듯이 To beacontinued 앱에서는 GEOFENCE_TRANSITION_ENTER만 사용하기 때문에 case Geofence.GEOFENCE_TRANSITION_DWELL/Geofence.GEOFENCE_TRANSITION_EXIT는 사용하지 않아도 된다.   

## Geofence Notification

* 아래의 코드는 Geofence에서 Notification을 받기 위한 코드이다. NotificationHelper.java에 추가한다.   
Notification에 대한 설명은 위에서 언급했으므로 생략한다.   

<pre><code>
    // 지오펜스 notification
    private static final String TAG = "NotificationHelper";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;


    //오레오 (API26)이상부터 채널을 추가해야 notification 사용 가능
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    public void sendHighPriorityNotification(String title, String body, Class activityName) {

        //intent에서 notification클릭 시 보여줄 엑티비티 설정
        Intent intent = new Intent(this, ExtraTodolist.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().setSummaryText("summary").setBigContentTitle(title).bigText(body))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat.from(this).notify(new Random().nextInt(), notification);

    }
</code></pre>

* 비콘으로 받는 알림이 아닌 Geofence를 통해 받는 알림은 늦을 수 있는데 그 이유는 다음과 같다.
    * 지속적으로 위치 쿼리하는 형식이 아니라 대기 시간이 필요하다.   
    * 일반적인 대기 시간은 2분 미만이며 장치가 움직일 때는 훨씬 적다.
    * 백그라운드 위치 제한 적용시 대기 시간은 평균 약 2~3분 정도이다.
    * 단말이 장시간 정지된 경우 대기 시간이 증가할 수 있다. (최대 6분)
    
참고 : https://developer.android.com/training/location/geofencing?hl=ko#java

## 소리 설정

* #### Preference
Preference 라이브러리는 안드로이드 내에서 쉽게 설정 화면을 구축을 할 수 있도록 한다. 이 계층 구조를 XML 리소스로 정의하거나 코드로 계층 구조를 빌드할 수 있다. 또한, 계층구조를 여러 개의 화면으로 분할할 수 있다. Preference를 유지하도록 설정되면 애플리케이션의 다른 곳에서 액세스할 수 있는 설정에 대한 해당 키-값 쌍이 포함된다. 

* #### Intent 설정
Preference에 Intent를 설정하면 Preference를 탭할 때마다 새 Fragment, Activity 또는 별도의 어플리케이션을 실행할 수 있다. 

* activity_setting.xml 파일에 다음과 같이 코드를 추가한다.    
핸드폰의 내장되어 있는 설정 창으로 이동하여 소리 설정 창이 실행된다. 
<pre><code>
< PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android"
    android:title="환경 설정"
    android:layout_height="match_parent"
    android:layout_width="match_parent" >

    < PreferenceCategory
        android:title="@string/settings" / >

    < PreferenceScreen
        android:icon="@drawable/ic_vibration"
        android:title="@string/settings_alarm"
        android:summary="@string/settings_alarm_explain" >
        < intent
            android:action="android.intent.action.MAIN"
            android:targetPackage="com.android.settings"
            android:targetClass="com.android.settings.SoundSettings"/ >

    < /PreferenceScreen >
</code></pre>

* SettingActivity.java 파일에 PreferenceActivity를 extends 후, 코드를 추가한다.

<pre><code>
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_setting);
        //Settingfragment를 이용할 때는 onCreateView가 아닌 addPreferencesFromResource를 이용해야 함
    }

</code></pre>

참고 : [Google Developers][googlelink]

[googlelink]: https://developer.android.com/guide/topics/ui/settings/components-and-attributes?hl=ko"

## 언어 설정

* To Beacontinued앱에서 언어 설정은 영어와 한국어를 지원한다. 앱에서 제공해야 하는 부분을 하드코딩 방식으로 < string >객체로 바꾸어주었다.
strings.xml 파일에서 open editor를 통해 영어를 추가해 주면 다음 그림과 같이 strings 폴더 안에 두 개의 xml 파일이 생성된다.
<img src = "https://user-images.githubusercontent.com/62948547/85103843-6c226300-b242-11ea-8d0a-df85fcd09908.png">

* 여기에서 지원해야 하는 부분들을 한국어와 영어 두 언어로 모두 넣어주면 시스템 설정 내에서 영어로 변환할 경우 앱의 언어가 변경되어 보여진다.
<img src = "https://user-images.githubusercontent.com/62948547/85104295-506b8c80-b243-11ea-960f-f0c37f9e68c1.png">

* activity_setting.xml은 소리 설정과 같은 형식이다.
<pre><code>
    < PreferenceScreen
        android:icon="@drawable/ic_language"
        android:title = "@string/settings_lang"
        android:summary="@string/settings_language" >
        < intent
            android:action="android.intent.action.MAIN"
            android:targetPackage="com.android.settings"
            android:targetClass="com.android.settings.LanguageSettings"/ >
    < /PreferenceScreen >
    
</code></pre>

* 구현화면

<img src="https://user-images.githubusercontent.com/62587097/84918314-68d08f80-b0fb-11ea-903c-2183466528b5.png" width="30%"><img src="https://user-images.githubusercontent.com/62587097/84918854-2065a180-b0fc-11ea-8657-d23fd93fb738.png" width="30%"><img src="https://user-images.githubusercontent.com/62587097/84918935-3bd0ac80-b0fc-11ea-95b6-01db636a73a7.png" width="30%">

<hr>

## Splash 화면

* 스플래시 스크린(Splash screen) 이란 프로그램(앱)이 실행될 때 잠시 나타나는 화면을 말한다.   

* MainActivity   
처음 화면을 구현하기 위해서 Handler를 이용하여 delay를 주어 2초 뒤에 화면 전환을 한다. 
<pre><code>
public class MainActivity extends AppCompatActivity {
    //스플래쉬 화면 첫번째 켜질때 뜨는 화면
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 2000);  //2초 뒤 화면 변함

    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MenuActivity.class));
            MainActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }
}
</code></pre>

* activity_main    
background화면으로 drawable 폴더 내에서 배경을 만든 이미지 파일을 설정한다.   
<pre><code>
  < ?xml version="1.0" encoding="utf-8"? >
< androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:background="@drawable/start" />
    
</code></pre>

* AndroidManifest    
구현한 activity 파일을 정의해주고 style을 적용한다. MainActivity가 아닌 새로 구성한 activity가 배치되어야 한다.
<pre><code>
```
< activity android:name=".MainActivity ">
            < intent-filter >
                < action android:name="android.intent.action.MAIN" / >

                < category android:name="android.intent.category.LAUNCHER" / >
            < /intent-filter >
 < /activity >
 ```
 </pre></code>
 
* 구현화면(시작 화면과 2초 뒤 화면)

<img src="https://user-images.githubusercontent.com/62587097/84872825-56cffc00-b0bd-11ea-9916-f687e442c336.png" width="30%"><img src="https://user-images.githubusercontent.com/66317772/85261542-3f22ba00-b4a7-11ea-91b2-73b39deda1d9.png" width="30%">

## Nevigation Drawer
* 메인 화면 Menuactivity는 Navigation Drawer Activity를 활용하였고 2개의 Fragment를 추가한다.
<img src = "https://user-images.githubusercontent.com/62948547/85104877-5f067380-b244-11ea-8c59-2bd7cc8d4a91.png" width = "60%">

* 기본 프래그먼트를 beaconFragment로 만들어주었고 첫 번째 인터페이스를 선언하면 아래의 onNavigationItemSelected()함수를 구현해야 한다.
첫 번째 메뉴가 선택될 때는 beaconFragment, 두 번째 메뉴가 선택될 때는 calendarFragment이 보여지도록 설정해 주었다.
<pre><code>
public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback{
    //메인화면
    //메인화면은 두 부분으로 나누었다 -> 비콘을 활용한 비콘 프레그먼트 & 날짜로 저장하는 캘린더 프레그먼트

    BeaconFragment beaconFragment;          //비콘 프레그먼트 선언
    CalendarFragment calendarFragment;      //캘린더 프레그먼트 선언

    DrawerLayout drawer;                    //메뉴액티비티는 왼쪽 상단에 네비게이션 드로어를 이용했기 때문에 선언
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ```
        
        getSupportFragmentManager().beginTransaction().add(R.id.container, beaconFragment).commit();        
        //기본 프레그먼트는 비콘프레그먼트임
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {           
    //네비게이션 드로어 열었을 때 두 가지 메뉴
        int id = item.getItemId();

        if(id==R.id.menu1){
            OnFragmentSelected(0, null);
        }
        else if(id ==R.id.menu2){
            OnFragmentSelected(1, null);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
</code></pre>

* drawer부분은 다음 사진과 같이 header부분과 menu부분으로 나뉘어 변경하였다.
<pre><code>
< menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    tools:showIn="navigation_view" >

    < group android:checkableBehavior="single" >
        < item
            android:id="@+id/menu1"
            android:icon="@android:drawable/ic_menu_mylocation"
            android:title="@string/add_Beacon" />
        < item
            android:id="@+id/menu2"
            android:icon="@android:drawable/ic_menu_today"
            android:title="@string/add_calendar" />
    < /group>
< /menu>
</code></pre>

<img src = "https://user-images.githubusercontent.com/62948547/85105794-0afc8e80-b246-11ea-8b63-58c5f214724f.png" width = "30%">

## Tool Bar

* 툴바는 메뉴 activity(메인)와 각 장소(비콘) activity에 설정한다. 메뉴 액티비티에서는 Navigation drawer와 설정으로 들어갈 수 있는 버튼이 있고,
각 장소 activity에서는 뒤로 가기 버튼을 추가한다.

* 툴바를 사용하는 액티비티는 manifest파일에서 다음의 부분을 추가한다.
<pre><code>
android:theme="@style/AppTheme.NoActionBar"
</code></pre>

* 앱 실행 화면과 Menuactivity.java
<img src = "https://user-images.githubusercontent.com/62948547/85106959-13ee5f80-b248-11ea-8353-e7177ce8b697.png" width = "40%">

<pre><code>
public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback{
    
    Toolbar toolbar;      
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        drawer = findViewById(R.id.drawer_layout);            //drawer toolbar에 추가
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        
        ```
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {             //메뉴바 생성
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {      //메뉴바에 메뉴 아이템 선택
        int curId = item.getItemId();
        switch (curId){
            case R.id.menu_settings:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivity(settingIntent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
</code></pre>    

* 다른 장소 페이지화면(툴바) 
<img src = "https://user-images.githubusercontent.com/62948547/85107110-5d3eaf00-b248-11ea-972c-154e4d5475b6.png" width="40%">

* 각각 activity.java 파일에 다음과 같은 코드로 뒤로 가기를 추가한다.
<pre><code>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    ```
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);      //toolbar 설정 , 뒤로가기 넣음
    ```
    }
        
    //Toolbar에 뒤로가기 눌렀을 때 실행
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
</code></pre>

## 기종에 상관없는 디자인

* 기종에 따라 디자인이 영향을 받지 않도록 layout을 변경하였다.     

* Layout은 ConstraintLayout을 사용하며 아래의 순서대로 xml파일을 수정한다.   

▷ Component Tree 우클릭 > Helpers > Add Vertical Guidline 또는 Add Horizontal Guidline을 추가한다.    

▷ 각 Guidline의 constraintguide_percent를 원하는 percent로 정해준다.   

▷ 버튼, 텍스트 등의 Start_toStartOf / End_toEndOf / Bottom_toBottomOf / Top_toTopOf를 정해준다. (상하좌우)   

▷ 버튼, 텍스트 등이 정해진 영역을 다 차지할 경우 width와 height를 0dp로 지정한다.   

<img src="https://user-images.githubusercontent.com/62587484/84990680-7bd97300-b180-11ea-93e9-0cffe035adc5.png" width="40%"><img src="https://user-images.githubusercontent.com/62587484/84990735-8c89e900-b180-11ea-97c0-4f43b6967961.png" width="20.5%">

#### AppCompatTextView를 이용하여 핸드폰 기종에 영향을 받지 않는 TextView

* 기종에 따라 TextView가 변하지 않도록 Platte > Containers > < view > > AppCompatTextView를 사용한다.   
Text의 최소,최대 크기를 지정하고 Text가 차지하는 최소,최대 line 등을 지정한다.   
다음 코드는 activitiy_maps.xml에서 사용한 코드이다.

<pre> <code>
< view
        android:id="@+id/result_textView"
        class="androidx.appcompat.widget.AppCompatTextView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginTop="5dp"
        android:layout_marginEnd="5dp"
        android:layout_marginBottom="8dp"
        android:maxLines="3"
        android:minLines="2"
        android:text="@string/to_do_text"
        android:textSize="18sp"
        map:autoSizeMaxTextSize="20sp"
        map:autoSizeMinTextSize="15sp"
        map:autoSizeStepGranularity="1sp"
        map:autoSizeTextType="uniform"
        map:layout_constraintBottom_toBottomOf="@id/guideline_map_top_ho"
        map:layout_constraintEnd_toEndOf="@id/guideline_center_ver"
        map:layout_constraintStart_toStartOf="@id/guideline_ver"
        map:layout_constraintTop_toTopOf="@id/guideline_bottom_ho" />
</code></pre>

* 다음 사진은 activitiy_maps.xml에서 AppCompatTextView를 이용한 실행화면이다.
<img src="https://user-images.githubusercontent.com/62587484/85009016-bb13be00-b198-11ea-8482-1aed0f4edfcc.png" width="30%">       

<hr>

* To beacontinued에서 사용한 버튼과 배경 이미지, 앱 아이콘은 모두 직접 그려 사용한 것이다.

# 3. 시장성, 수익화 방안

### 비콘의 시장 규모 
<img src="https://user-images.githubusercontent.com/62588817/85100759-e00d3d00-b23b-11ea-94cc-a8afc1da86aa.png" width="40%">

* 위 그래프는 비콘 기반 지역별 위치서비스에 대한 시장 규모를 보여주고 있다. 
비콘이란, 스마트폰과 같은 허브기기와 주변 장치, 그리고 액세서리들 간의 다리(Bridge)역할을 한다. 
따라서 비콘 기술은 차세대 IoT 유망 기술이 될 수 있으며, 생활편의 뿐 아니라 회사의 마케팅 분야까지 다양하게 쓰일 수 있다.
이처럼, 비콘 기술은 향후 4차 산업에서 mainstream의 역할을 할 것으로 기대된다. 

### 비콘서비스를 활용 중인 예시

  #### 1. 스타벅스_사이렌오더

<img src="https://user-images.githubusercontent.com/62587097/85141104-381a6280-b281-11ea-956a-de76136b7ed1.png" width="20%">

  * 세계 1위 커피전문점인 ‘STARBUCKS’는 주문 서비스 기술로 ‘비콘 서비스’를 활용하고 있다.   
  매장 근처에서 고객들의 주문과 결제가 용이하며, 고객들이 커피를 모바일로 주문과 결제를 할 수 있다는 점에 매우 인기가 높은 앱 서비스이다.
  
  * 많은 고객들과 주문들 사이에서, 주문한 고객과 주문된 음료를 정확하게 매칭시키는 것 또한 비콘(beacon)을 통해 활용하고 있다. 
  
  #### 2. SK플래닛_시럽(syrup)

<img src="https://user-images.githubusercontent.com/62587097/85141197-5bdda880-b281-11ea-9145-dda09394d18c.png" width="30%"><img src="https://user-images.githubusercontent.com/62588817/85103061-ea7e0580-b240-11ea-87f2-d0534bb43a34.png" width="30%">

  * 국내 기업 활용한 예를 들자면, SK planet의 SYRUP은 매장 안과 주위에 설치된 비콘을 통해 SYRUP이용자들의 스마트 폰을 인식하고 각종 마케팅     정보들을 전송하는 기술로 활용하고 있다.      
    이러한 정보들을 활용하기 위해서는 블루투스(Bluetooth)와 GPS기능을 켜야 한다. 이를 통해 이용자의 위치를 확인하여 SYRUP service zone에       들어온 이용자에게 정보를 전송할 수 있기 때문이다.    

  * 현재 SK planet은 차세대 커머스인 SYRUP service에 전사 역량을 집중하며 이와 동시에 O2O(Online to Offline) service를 선보이며 향후        SYRUP service 확대를 기대하고 있다. 

### To Becontinued 앱의 전망과 기대효과

* 앱 사용자들로부터 10억 다운로드 및 129만개의 리뷰를 받은 구글 캘린더(Google Calender)는 매우 앱 서비스 기반 산업에서도 성공적인 사례이다. 특히, 일정 알림을 통해 팝업과 이메일 등으로 정보를 제공하며 또한 일정에 반영할 수 있도록 유용한 날씨 정보도 함께 제공하고 있다. 

* 또한, 위치기반서비스에서 장소를 입력하면 해당 장소 사진도 함께 제공받을 수 있는 장점으로 인해 고객별점 4.5점 만점에 4.4점의 별점을 받았다. 이처럼 To Becontinued 앱과 유사한 기능을 하는 ‘구글캘린더’와 비교했을 때, To Becontinued의 차별성은 ‘Specific Alarm Service’이다. 특정 장소에 방문 시, 해당장소에서 사용자가 해야 하는 목록을 즉시 알림 해주는 서비스이기 때문이다. 

 * 향후, To Becontinued 기술개발의 경우 구글캘린더의 날씨 정보 뿐 아니라 이 프로젝트에서 사용한 비콘에 있는 응급 상황 시 EMERGENCY 버튼을 통해 SMS 및 구조 요청을 할 수 있는 기술개발도 가능하다. 현재 비콘 시장가격보다 더 저렴한 비콘을 사용하게 된다면 현재 비콘을 설치 한 4개의 장소보다 더 많은 장소에서 비콘 서비스를 고객들이 이용할 수 있는 산업적 기대효과도 있다.   
 
### 수익화 방안

#### 광고

* 앞서 언급한 비콘서비스 기반의 예들(STARBUCKS / SYRUP)의 수익성의 경우와 마찬가지로, To Beacontinued 앱 또한 수익의 상당 부분을 광고 및 홍보 대행의 서비스를 통해 얻을 수 있다.   
특히 문화 및 예술 관련 사업체나 뷰티/패션 등과 같이 홍보가 중요한 개인 및 기업체들은 모바일을 통한 홍보가 필수적이다. 이러한 조건은 To Beacontinued 앱의 경우, 앱 내에서의 알림을 통해 사용자들에게 홍보에 있어 거부감이 들지 않으면서 자연스러운 홍보 효과를 통해 깊게 사용자들의 일상 속에 침투할 수 있는 차별성을 가지고 있다.   
구체적으로, 홍보 업체의 장소 및 이벤트들을 앱 사용 빈도와 함께 조정하며 홍보 횟수에 따라 업체 별 유료 금액을 조정 및 활용하여 수익 창출이 가능하다.   
예를 들어, 비콘을 설치한 장소 중 하나인 집 밖의 메모를 "올리브○에서 ○○○사기"나 "○마트에서 장보기"라고 작성 시 특정 단어에 따라 알고리즘을 통해 해당 업체의 광고 영상이나 각종 이벤트들을 알림으로 보여줌으로써 수익을 얻을 수 있다.   

    * 수익창출 기대효과를 가진 대표적인 4가지 사업체 별 광고 및 홍보 금액은 다음과 같다.   
뷰티 산업체 : 10원/건 (광고시간 5초 이하 무료 / 5초 초과 시 5금액)   
패션 산업체 : 10원/건 (광고시간 5초 이하 무료 / 5초 초과 시 5금액)   
영화 및 문화 산업체 : 30원/건 (광고시간 15초 이하 무료 / 10초 초과 시 15금액)   
게임 및 컨텐츠 산업체 : 30원/건 (광고시간 15초 이하 무료 / 10초 초과 시 15금액)    
기타 산업체 : 20원/건 (광고시간 10초 이하 무료 / 10초 초과 시 10금액)

#### 유료 서비스

* To Beacontinued앱 내에서도 수익을 얻을 수 있는 방안이 있다.   
예를 들어, 추가 장소(사용자가 설정한 장소에 할 일을 알림으로 받기)에 메모를 추가 할 경우 메모 추가를 특정 개수만큼은 무료로 사용할 수 있고 특정 개수를 초과하는 경우 유료로 변환하는 방식이다.    
    
이를 통해, To Beacontinued 앱을 통한 수익창출은 매우 증가할 것이며 각 사업체 별로 광고 및 홍보를 해주며 동시에 저희 앱 또한 홍보를 할 수 있는 효과가 있기에 향후 시장성 및 수익성에 대한 기대효과가 클 것으로 예상된다.    
 
 # 4. 참고자료

* **참고 사이트**
 
   ▷ Geocoder :  https://bitsoul.tistory.com/135 
 
   ▷ Geofencing : https://developer.android.com/training/location/geofencing?hl=ko#java  
   
   ▷ 언어 설정 및 소리 설정 : https://developer.android.com/guide/topics/ui/settings/components-and-attributes?hl=ko"     

 * **그림 출처**
 
   ▷ 시간경과에 따른 기억력      
   
    https://brunch.co.kr/@visualvision/27
   
   ▷ 비콘 기반 지역별 위치서비스에 대한 시장규모
   
    산업시장분석실 책임연구원 서진이, Vol 4 Issue KISTI MARKET REPORT(2014)
    
# 5. 동작 영상

#### Beacon이 없는 경우 실행의 제한이 많아 동작 영상을 추가하였다.
* 전체적인 To Beacontinued 앱의 동작 영상이다. 아래의 사진을 클릭하여 재생할 수 있다.

[![Video Label](http://img.youtube.com/vi/Ac5DqnVF6M8/0.jpg)](https://youtu.be/Ac5DqnVF6M8)

###### 0.75배속으로 재생을 권장한다.   (3:53 앱은 -> 앱을)   
###### 알람이 울리고 클릭하여 해당 메모 페이지로 넘어간 후 Toolbar의 뒤로가기 버튼을 눌렀을 때 앱의 전 화면이 나타나는 것이 아니고 앱을 나가게 한 것은 사용자가 다른 실행을 하고 있을 시 빠르게 메모만 확인하기 위함이다.
