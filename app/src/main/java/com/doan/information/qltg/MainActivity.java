package com.doan.information.qltg;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.*;
import com.allyants.notifyme.NotifyMe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView txtDate, txtTime, user, userEmail;
    EditText editCv, editNd;
    Button btnDate, btnTime, btnAdd, btnSua;
    //Khai báo Datasource lưu trữ danh sách công việc
    ArrayList<JobInWeek> arrJob = new ArrayList<JobInWeek>();
    //Khai báo ArrayAdapter cho ListView
    ArrayAdapter<JobInWeek> adapter = null;
    ListView lvCv;
    Calendar cal;
    Date dateFinish;
    Date hourFinish;
    private FirebaseAuth firebaseAuth;

     TextView txtLogout;

    private TextView textViewEmail;
    DatabaseReference databaseEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getFormWidgets();
        getDefaultInfor();
        addEvent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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




        btnSua.setEnabled(false);
    }

    private void sendMail() {
        String email = "leducdung147@gmail.com";
        String[] convert = email.split(",");
        String content = "Mọi góp ý gửi về leducdung147@gmail.com";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, convert);
        intent.putExtra(Intent.EXTRA_SUBJECT, email);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent(MainActivity.this, Infomation.class);
        startActivity(intent);
        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
         intent = new Intent(MainActivity.this, Infomation.class);
          startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            intent = new Intent(MainActivity.this, Infomation.class);
            startActivity(intent);
        } else if (id == R.id.nav_tools) {
            intent = new Intent(MainActivity.this, Infomation.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            sendMail();
        } else if (id == R.id.nav_send) {
            sendMail();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
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
        btnSua = (Button) findViewById(R.id.btnsua);
        lvCv = (ListView) findViewById(R.id.lvcongviec);
        txtLogout = (TextView) findViewById(R.id.txtlogout);
        user = (TextView) findViewById(R.id.user);
        userEmail = (TextView) findViewById(R.id.userEmail);
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

                btnSua.setEnabled(true);
                final int position = i;
                Toast.makeText(MainActivity.this,
                        arrJob.get(i).getDescription(),
                        Toast.LENGTH_LONG).show();
                JobInWeek job = arrJob.get(i);
                editCv.setText(job.getTitle());
                editNd.setText(job.getDescription());
                txtTime.setText(job.getHourFormat(job.getHourFinish()));
                txtDate.setText(job.getDateFormat(job.getDateFinish()));
                btnSua.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //lỗi ko lấy được position khi click chọn item chỉnh sửa rồi remove nó đi

                        JobInWeek job = arrJob.get(position);
                        updateJob(job.getIdKey(), position);
                        btnSua.setEnabled(false);



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
                btnSua.setEnabled(false);
                return false;
            }
        });
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (v == txtLogout) {
                        firebaseAuth.signOut();
                        finish();
                        Intent i = new Intent(getApplicationContext(), LoginLayer.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(), "Logout is Successfully", Toast.LENGTH_SHORT).show();
                    }
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
        dR.setValue(job);
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


        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();

        return true;
    }
}
