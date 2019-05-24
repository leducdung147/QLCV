package com.doan.information.qltg;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.allyants.notifyme.NotifyMe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtDate, txtTime;
    EditText editCv, editNd;
    Button btnDate, btnTime, btnAdd;
    //Khai báo Datasource lưu trữ danh sách công việc
    ArrayList<JobInWeek> arrJob = new ArrayList<JobInWeek>();
    //Khai báo ArrayAdapter cho ListView
    ArrayAdapter<JobInWeek> adapter = null;
    ListView lvCv;
    Calendar cal;
    Date dateFinish;
    Date hourFinish;
    private FirebaseAuth firebaseAuth;

    private TextView txtLogout;

    private TextView textViewEmail;
    DatabaseReference databaseEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getFormWidgets();
        getDefaultInfor();
        addEvent();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Intent i = new Intent(getApplicationContext(), LoginLayer.class);
            startActivity(i);
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewEmail = (TextView) findViewById(R.id.textViewUserEmail);

        //Add dữ liệu vào database
        Intent intent = getIntent();
        String id = user.getUid();


        databaseEvent = FirebaseDatabase.getInstance().getReference("Công việc").child(id);

        textViewEmail.setText("Hi! " + user.getEmail());

        txtLogout = (TextView) findViewById(R.id.txtlogout);

        txtLogout.setOnClickListener(this);

    }

    //Đọc dữ liệu database
    @Override
    protected void onStart() {
        super.onStart();
        databaseEvent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrJob.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    JobInWeek job = postSnapshot.getValue(JobInWeek.class);
                    arrJob.add(job);
                    editCv.setText("");
                    editNd.setText("");
                    getDefaultInfor();
                }

                //gán Adapter vào ListView
                lvCv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getFormWidgets() {
        txtDate = (TextView) findViewById(R.id.txtdate);
        txtTime = (TextView) findViewById(R.id.txttime);
        editCv = (EditText) findViewById(R.id.editcongviec);
        editNd = (EditText) findViewById(R.id.editnoidung);
        btnDate = (Button) findViewById(R.id.btndate);
        btnTime = (Button) findViewById(R.id.btntime);
        btnAdd = (Button) findViewById(R.id.btncongviec);
        lvCv = (ListView) findViewById(R.id.lvcongviec);
        //Gán DataSource vào ArrayAdapter
        adapter = new ArrayAdapter<JobInWeek>
                (this,
                        android.R.layout.simple_list_item_1,
                        arrJob);
        //gán Adapter vào ListView
        lvCv.setAdapter(adapter);
    }

    /**
     * Hàm lấy các thông số mặc định khi lần đầu tiền chạy ứng dụng
     */
    public void getDefaultInfor() {
        //lấy ngày hiện tại của hệ thống
        cal = Calendar.getInstance();
        SimpleDateFormat dft = null;
        //Định dạng ngày / tháng /năm
        dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String strDate = dft.format(cal.getTime());
        //hiển thị lên giao diện
        txtDate.setText(strDate);
        //Định dạng giờ phút am/pm
        dft = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String strTime = dft.format(cal.getTime());
        //đưa lên giao diện
        txtTime.setText(strTime);
        //lấy giờ theo 24 để lập trình theo Tag
        dft = new SimpleDateFormat("HH:mm", Locale.getDefault());
        txtTime.setTag(dft.format(cal.getTime()));

        editCv.requestFocus();
        //gán cal.getTime() cho ngày hoàn thành và giờ hoàn thành
        dateFinish = cal.getTime();
        hourFinish = cal.getTime();
    }

    /**
     * Hàm gán các sự kiện cho các control
     */
    public void addEvent() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();

            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processAddJob();


            }
        });

        lvCv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int position = i;
                Toast.makeText(MainActivity.this,
                        arrJob.get(i).getDescription(),
                        Toast.LENGTH_LONG).show();
                JobInWeek job = arrJob.get(i);
                editCv.setText(job.getTitle());
                editNd.setText(job.getDescription());
                txtTime.setText(job.getHourFormat(job.getHourFinish()));
                txtDate.setText(job.getDateFormat(job.getDateFinish()));
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //lỗi ko lấy được position khi click chọn item chỉnh sửa rồi remove nó đi
                        if (!lvCv.isLongClickable()){
                        JobInWeek job = arrJob.get(position);
                        updateJob(job.getIdKey(), position);}
                        else{
                            processAddJob();
                        }
                    }
                });


            }
        });
        lvCv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                JobInWeek job = arrJob.get(i);
                deleteEvent(user.getUid(), job.getIdKey());

                arrJob.remove(i);

                lvCv.setAdapter(adapter);

                return false;
            }
        });

    }


    /**
     * Hàm hiển thị DatePicker dialog
     */
    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                //Mỗi lần thay đổi ngày tháng năm thì cập nhật lại TextView Date
                txtDate.setText(
                        (dayOfMonth) + "/" + (monthOfYear + 1) + "/" + year);
                //Lưu vết lại biến ngày hoàn thành
                cal.set(year, monthOfYear, dayOfMonth);
                dateFinish = cal.getTime();
            }
        };
        //các lệnh dưới này xử lý ngày giờ trong DatePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s = txtDate.getText() + "";
        String strArrtmp[] = s.split("/");
        int ngay = Integer.parseInt(strArrtmp[0]);
        int thang = Integer.parseInt(strArrtmp[1]) - 1;
        int nam = Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic = new DatePickerDialog(
                MainActivity.this,
                callback, nam, thang, ngay);
        pic.setTitle("Chọn ngày hoàn thành");
        pic.show();
    }

    /**
     * Hàm hiển thị TimePickerDialog
     */
    public void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view,
                                  int hourOfDay, int minute) {
                //Xử lý lưu giờ và AM,PM
                String s = hourOfDay + ":" + minute;
                int hourTam = hourOfDay;
                if (hourTam > 12)
                    hourTam = hourTam - 12;
                txtTime.setText
                        (hourTam + ":" + minute + (hourOfDay > 12 ? " PM" : " AM"));
                //lưu giờ thực vào tag
                txtTime.setTag(s);
                //lưu vết lại giờ vào hourFinish
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                hourFinish = cal.getTime();
            }
        };
        //các lệnh dưới này xử lý ngày giờ trong TimePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s = txtTime.getTag() + "";
        String strArr[] = s.split(":");
        int gio = Integer.parseInt(strArr[0]);
        int phut = Integer.parseInt(strArr[1]);
        TimePickerDialog time = new TimePickerDialog(
                MainActivity.this,
                callback, gio, phut, true);
        time.setTitle("Chọn giờ hoàn thành");
        time.show();
    }

    /**
     * Hàm xử lý đưa công việc vào ListView khi nhấn nút Thêm Công việc
     */
    public void processAddJob() {
        final String title = editCv.getText().toString().trim();

        final String description = editNd.getText().toString().trim();

        final String idKey = databaseEvent.push().getKey();
        JobInWeek job = new JobInWeek(title, description, dateFinish, hourFinish, idKey);
        databaseEvent.child(idKey).setValue(job);

        arrJob.add(job);
        //Thông báo tạo công việc thành công
        NotifyMe success = new NotifyMe.Builder(getApplicationContext())
                .title(" Thêm Công việc thành công!")
                .content("")
                .color(255, 0, 0, 255)
                .led_color(255, 255, 255, 255)


                .key("test")
                .addAction(new Intent(), "Dismiss", true, false)

                .large_icon(R.drawable.firebase2)
                .build();
        //Thông báo công việc khi đến thời gian đã tạo
        NotifyMe popup = new NotifyMe.Builder(getApplicationContext())
                .title("Công việc: " + title)
                .content("Nội dung: " + description)
                .color(255, 0, 0, 255)
                .led_color(255, 255, 255, 255)
                .time(hourFinish)
                .time(dateFinish)
                .key("test")
                .addAction(new Intent(), "Dismiss", true, false)

                .large_icon(R.drawable.ic_launcher_foreground)
                .build();
        adapter.notifyDataSetChanged();

        //sau khi cập nhật thì reset dữ liệu và cho focus tới editCV
        editCv.setText("");
        editNd.setText("");
        editCv.requestFocus();


    }


    @Override
    public void onClick(View view) {
        if (view == txtLogout) {
            firebaseAuth.signOut();
            finish();
            Intent i = new Intent(getApplicationContext(), LoginLayer.class);
            startActivity(i);
            Toast.makeText(this, "Logout is Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deleteEvent(String id, String idKey) {

        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Công việc").child(id).child(idKey);
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();

        return false;
    }

    private boolean updateJob(String idKey, int position) {
        //getting the specified artist reference
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String id = user.getUid();
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Công việc").child(id).child(idKey);
        String cv = editCv.getText().toString().trim();
        String nd = editNd.getText().toString().trim();

        JobInWeek job = arrJob.get(position);

        job = new JobInWeek(cv, nd, dateFinish, hourFinish, job.getIdKey());
        arrJob.set(position, job);

        lvCv.setAdapter(adapter);

        //updating job
        //Thông báo tạo công việc thành công
        NotifyMe success = new NotifyMe.Builder(getApplicationContext())
                .title(" Sửa Công việc thành công!")
                .content("")
                .color(255, 0, 0, 255)
                .led_color(255, 255, 255, 255)
                .key("test")
                .addAction(new Intent(), "Dismiss", true, false)
                .large_icon(R.drawable.firebase2)
                .build();
        //Thông báo công việc khi đến thời gian đã tạo
        NotifyMe popup = new NotifyMe.Builder(getApplicationContext())
                .title("Công việc: " + cv)
                .content("Nội dung: " + nd)
                .color(255, 0, 0, 255)
                .led_color(255, 255, 255, 255)
                .time(hourFinish)

                .key("test")
                .addAction(new Intent(), "Dismiss", true, false)

                .large_icon(R.drawable.ic_launcher_foreground)
                .build();

        dR.setValue(job);
       onStart();

        editCv.requestFocus();
        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();

        return true;
    }
}

