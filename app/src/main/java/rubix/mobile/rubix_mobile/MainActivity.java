package rubix.mobile.rubix_mobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import rubix.mobile.rubix_mobile.Fragment.MenuChangeFragment;
import rubix.mobile.rubix_mobile.Fragment.MenuCountingFragment;
import rubix.mobile.rubix_mobile.Fragment.MenuHomeFragment;
import rubix.mobile.rubix_mobile.Fragment.MenuPickingFragment;
import rubix.mobile.rubix_mobile.Fragment.MenuProfileFragment;
import rubix.mobile.rubix_mobile.Fragment.MenuReceivingFragment;
import rubix.mobile.rubix_mobile.Fragment.MenuTransitFragment;
import rubix.mobile.rubix_mobile.Fragment.PickingDetailStickerFragment;
import rubix.mobile.rubix_mobile.Fragment.PickingSerialScanFragment;
import rubix.mobile.rubix_mobile.Fragment.ReceiveDetailsNoStickerFragment;
import rubix.mobile.rubix_mobile.Fragment.ReceiveDetailsStickerFragment;
import rubix.mobile.rubix_mobile.Fragment.ReceiveSerialScanFragment;
import rubix.mobile.rubix_mobile.Fragment.SettingFragment;

import static rubix.mobile.rubix_mobile.Fragment.MenuReceivingFragment.json_receive;
import static rubix.mobile.rubix_mobile.Fragment.MenuReceivingFragment.json_serial;
import static rubix.mobile.rubix_mobile.Fragment.MenuPickingFragment.DtItem;
import static rubix.mobile.rubix_mobile.Fragment.MenuPickingFragment.DtSerial;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MenuHomeFragment.OnClickMenu,
        ReceiveSerialScanFragment.getDataListener,
        PickingSerialScanFragment.sendData {
    private Toolbar toolbar;
    private Window window ;
    public static String FragmentName = "Start";
    private Date TimePause;
    private NavigationView navigationView;
    Bundle config;
    Fragment tmp;
    int resetReceiveFragment = 1;
    int resetPickFragment = 1;
    public static int checkBack_receive = 0;
    public static int checkBack_pick = 0;
    public static boolean resetTransitFragment = false;
    public static boolean typingTransitFragment = false;
    public static boolean resetChangeFragment = false;
    public static boolean typingChangeFragment = false;
    public static boolean typingCountingFragment = false;
    public static boolean dialogTransit = false;
    public static ActionBarDrawerToggle toggle;
    public static DrawerLayout drawer;
    public static JSONArray dt_serial_receive = new JSONArray();
    public static JSONArray dt_serial_pick = new JSONArray();
    private Appconfig appconfig;
    private FragmentTransaction transaction;
    private int Time = 1;
    private String Unit = "Hour";
    public static void checkBackReceive(int num) {
        checkBack_receive = num;
    }
    public static int getCheckBackReceive() {
        return checkBack_receive;
    }
    public static void checkBackPick(int num) {
        checkBack_pick = num;
    }
    public static int getCheckBackPick() {
        return checkBack_pick;
    }
    public static void checkBackTransit(boolean st) {
        resetTransitFragment = st;
    }
    public static void setDialogTransit(boolean st) {
        dialogTransit = st;
    }
    public static void checkBackChange(boolean st) {
        resetChangeFragment = st;
    }
    public static void typingTransit(boolean st){typingTransitFragment=st;}
    public static void typingCounting(boolean st) {typingCountingFragment = st;}
    public static void typingChange(boolean st) {typingChangeFragment =st;}
    private TextView navName;
    private TextView navSurename;
    private TextView navUsername;
    private de.hdodenhof.circleimageview.CircleImageView navPic;
//    private LinearLayout mHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        config = new Bundle();
        config.putSerializable("Appconfig", getIntent().getSerializableExtra("Appconfig"));
        config.putString("Check", " ");
        appconfig =(Appconfig) getIntent().getSerializableExtra("Appconfig");
        if (!appconfig.checkstate()) restartApp();
        appconfig.setContext(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
        SetTime();

        View mHeader = navigationView.getHeaderView(0);
        navUsername = (TextView) mHeader.findViewById(R.id.nav_username);
        navName = (TextView) mHeader.findViewById(R.id.nav_name);
        navSurename = (TextView) mHeader.findViewById(R.id.nav_lastname);
        navPic = (de.hdodenhof.circleimageview.CircleImageView) mHeader.findViewById(R.id.nav_pic);

        AsyncTaskAdapter SR = new AsyncTaskAdapter(new Gson().toJson(appconfig.getUser().toString()) , appconfig);
        SR.execute("api/MobileUserProfile/LoadUserProfile");

        try {
            String data = SR.get().toString();
            JSONObject UserData = new JSONObject(data.substring(2, data.length()-1));

            String img_src = UserData.getString("ImageUrl");
            String img_url = appconfig.getURL() + img_src.substring(2,img_src.length()-1);

            navName.setText(UserData.getString("FirstName"));
            navSurename.setText(UserData.getString("LastName"));
            navUsername.setText(UserData.getString("Username"));
            Picasso.with(this)
                    .load(img_url)
                    .placeholder(R.drawable.default_user)
                    .error(R.drawable.default_user)
                    .into(navPic);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        Calendar timer = Calendar.getInstance();
        if(Unit.equals("Hour"))   timer.add(Calendar.HOUR,Time);
        else if(Unit.equals("Minute"))  timer.add(Calendar.MINUTE,Time);
        TimePause = timer.getTime();
        appconfig.setContext(null);
    }
    @Override
    protected void onStart() {
        super.onStart();
        appconfig.setContext(this);
        if(TimePause!=null){
            if(Calendar.getInstance().getTime().after(TimePause)) restartApp();
        }
        if (!checkinternet()) Toast.makeText(this,"Please connect internet",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Warning!");
        builder.setMessage("Are you sure you want to cancel?");

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!FragmentName.equals("Home")) {
            if (FragmentName.equals("Complete Process")) {
                checkBack_receive = 0;
                checkBack_pick = 0;

                config.putString("Check", "");
                resetReceiveFragment = 1;
                resetPickFragment = 1;

                ReceiveDetailsStickerFragment.Po = "!";
                ReceiveDetailsStickerFragment.Lot = "!";
                ReceiveDetailsNoStickerFragment.Po = "!";
                ReceiveDetailsNoStickerFragment.Lot = "!";
                PickingDetailStickerFragment.Po = "!";
                PickingDetailStickerFragment.Lot = "!";

                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));

            } else if (FragmentName.equals("Receive Details")) {

                MenuReceivingFragment.arr_supplier = new ArrayList<String>();
                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));

            } else if (FragmentName.equals("Receive Details press ok")) {

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkBack_receive = 0;
                        resetReceiveFragment = 1;
                        dt_serial_receive = new JSONArray();
                        json_receive = new JSONArray();
                        json_serial = new JSONArray();
                        MenuReceivingFragment.arr_supplier = new ArrayList<String>();

                        ReceiveDetailsStickerFragment.Po = "!";
                        ReceiveDetailsStickerFragment.Lot = "!";
                        ReceiveDetailsNoStickerFragment.Po = "!";
                        ReceiveDetailsNoStickerFragment.Lot = "!";

                        config.putString("Check", "");

                        navigationView.getMenu().getItem(0).setChecked(true);
                        onNavigationItemSelected(navigationView.getMenu().getItem(0));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else if (FragmentName.equals("Receive sticker info")){

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        config.putString("Check", "");

                        FragmentManager fm = getSupportFragmentManager();
                        Fragment tmp = getSupportFragmentManager().findFragmentByTag("tag_receiving");
                        tmp.setArguments(config);

                        fm.popBackStack("ReceiveSticker", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction()
                                .replace(R.id.FragmentMain, tmp, "tag_receiving")
                                .addToBackStack("MenuReceive")
                                .commit();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else if (FragmentName.equals("Receive no sticker info")) {

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        config.putString("Check", "");

                        FragmentManager fm = getSupportFragmentManager();
                        Fragment tmp = getSupportFragmentManager().findFragmentByTag("tag_receiving");
                        tmp.setArguments(config);

                        fm.popBackStack("ReceiveNoSticker",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction()
                                .replace(R.id.FragmentMain, tmp, "tag_receiving")
                                .addToBackStack("MenuReceive")
                                .commit();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else if (FragmentName.equals("Receive Serial")){

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FragmentManager fm = getSupportFragmentManager();
                        fm.popBackStack("ReceiveSerial", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else if (FragmentName.equals("Add New Item")) {
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        config.putString("Check", "");

                        FragmentManager fm = getSupportFragmentManager();
                        Fragment tmp = getSupportFragmentManager().findFragmentByTag("tag_receiving");
                        tmp.setArguments(config);

                        fm.popBackStack("MappingItem", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction()
                                .replace(R.id.FragmentMain, tmp, "tag_receiving")
                                .addToBackStack("MenuReceive")
                                .commit();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else if (FragmentName.equals("Add New Item press ok")) {

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        config.putString("Check", "");

                        FragmentManager fm = getSupportFragmentManager();
                        Fragment tmp = getSupportFragmentManager().findFragmentByTag("tag_receiving");
                        tmp.setArguments(config);

                        fm.popBackStack("MappingItem", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction()
                                .replace(R.id.FragmentMain, tmp, "tag_receiving")
                                .addToBackStack("MenuReceive")
                                .commit();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else if (FragmentName.equals("Picking Details")) {

                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));

            } else if (FragmentName.equals("Picking Details press ok")) {
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkBack_pick = 0;
                        resetPickFragment = 1;
                        dt_serial_pick = new JSONArray();
                        DtItem = new JSONArray();
                        DtSerial = new JSONArray();

                        PickingDetailStickerFragment.Po = "!";
                        PickingDetailStickerFragment.Lot = "!";

                        config.putString("CheckPick", "");

                        navigationView.getMenu().getItem(0).setChecked(true);
                        onNavigationItemSelected(navigationView.getMenu().getItem(0));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else if (FragmentName.equals("Picking info")) {

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        config.putString("CheckPick", "");

                        FragmentManager fm = getSupportFragmentManager();
                        Fragment tmp = getSupportFragmentManager().findFragmentByTag("tag_picking");
                        tmp.setArguments(config);

                        fm.popBackStack("PickBySticker",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction()
                                .replace(R.id.FragmentMain, tmp, "tag_picking")
                                .addToBackStack("MenuPick")
                                .commit();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else if (FragmentName.equals("Picking Serial")) {

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FragmentManager fm = getSupportFragmentManager();
                        fm.popBackStack("PickingSerial", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else if (FragmentName.equals("ScanCodeFragment")){
                Fragment tmp_serial = getSupportFragmentManager().findFragmentByTag("tag_barcode");
                Fragment tmp_receive = getSupportFragmentManager().findFragmentByTag("tag_receiving");
                Fragment tmp_receive_serial = getSupportFragmentManager().findFragmentByTag("tag_detail_receive_serial");
                Fragment tmp_pick = getSupportFragmentManager().findFragmentByTag("tag_picking");
                Fragment tmp_pick_serial = getSupportFragmentManager().findFragmentByTag("tag_detail_pick_serial");
                Fragment tmp_add = getSupportFragmentManager().findFragmentByTag("tag_mapping_item");
                if (tmp_receive != null) {
                    if (tmp_receive.getArguments().getString("Check")!= null)
                    {
                        config.putString("Check", "");
                        tmp_receive.setArguments(config);
                    }
                }
                if (tmp_pick != null) {
                    if (tmp_pick.getArguments().getString("CheckPick") != null) {
                        config.putString("CheckPick", "");
                        tmp_pick.setArguments(config);
                    }
                }
                if (tmp_serial != null) {
                    if (tmp_serial.getArguments().getString("WhereScan") == null){
                        config.putString("WhereScan", "");
                        if (tmp_receive_serial != null)
                            tmp_receive_serial.setArguments(config);
                        if (tmp_pick_serial != null)
                            tmp_pick_serial.setArguments(config);
                        if (tmp_add != null)
                            tmp_add.setArguments(config);
                    }
                }
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStack("ScanCode Fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else if (FragmentName.equals("Transit") &&(resetTransitFragment == true || typingTransitFragment==true)) {
                new AlertDialog.Builder(MainActivity.this).setMessage("Are you sure you want to exit process ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkBackTransit(false);
                                typingTransit(false);
                                getSupportFragmentManager().popBackStack("MenuTransit", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                navigationView.getMenu().getItem(0).setChecked(true);
                                onNavigationItemSelected(navigationView.getMenu().getItem(0));
                            }
                        }).setNegativeButton("No", null).show();

            }
            else if(FragmentName.equals("Counting") &&typingCountingFragment==true){
                new AlertDialog.Builder(MainActivity.this).setMessage("Are you sure you want to exit Stock Counting ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                typingCounting(false);
                                getSupportFragmentManager().popBackStack("MenuCounting", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                navigationView.getMenu().getItem(0).setChecked(true);
                                onNavigationItemSelected(navigationView.getMenu().getItem(0));
                            }
                        }).setNegativeButton("No", null).show();
            }
            else if(FragmentName.equals("Change Location") &&( resetChangeFragment == true || typingChangeFragment == true)){
                new AlertDialog.Builder(MainActivity.this).setMessage("Are you sure you want to exit Change Location ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                typingChange(false);
                                checkBackChange(false);
                                getSupportFragmentManager().popBackStack("MenuChange", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                navigationView.getMenu().getItem(0).setChecked(true);
                                onNavigationItemSelected(navigationView.getMenu().getItem(0));
                            }
                        }).setNegativeButton("No", null).show();
            }
            else if(FragmentName.equals("Setting Page")){
                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
            } else if(FragmentName.equals("Profile")){
                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
            }else {
                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
            }


        } else {
            AlertDialog.Builder builder_login = new AlertDialog.Builder(MainActivity.this);
            builder_login.setMessage("Are you sure you want to logout?");
            builder_login.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    checkBack_receive = 0;
                    checkBack_pick = 0;
                    appconfig.clearstate();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder_login.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder_login.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //set time on setting logout
    public void SetTime(){
        try {
            FileInputStream fileIn=openFileInput("setting.xml");
            InputStreamReader InputRead= new InputStreamReader(fileIn);
            char[] inputBuffer= new char[150];
            String s="";
            int charRead;
            while ((charRead=InputRead.read(inputBuffer))>0) {
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            Unit = LoginActivity.getXmlTag("Unit",s.toString().trim());
            Time = Integer.parseInt(LoginActivity.getXmlTag("Time",s.toString().trim()));;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            toolbar.setTitle("Setting");
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, SettingFragment.newInstance(Time,Unit), "tag_setting").addToBackStack(null).commit();
            return true;
        }
        else if (id == R.id.action_logout){
            Toast.makeText(this,appconfig.getUser()+ " was logout :D",Toast.LENGTH_LONG).show();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(i);
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_menu_home) {

            toolbar.setTitle("RUBIX-LITE");
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
            navigationView.getHeaderView(0).setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            setNavMenuItemThemeColors(this.getResources().getColor(R.color.colorPrimary));
//            navigationView.setItemTextColor(navMenuTextList);
//            navigationView.setItemIconTintList(navMenuIconList);

            transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, new MenuHomeFragment(), "tag_home").addToBackStack(null).commit();
            FragmentName = "Home";
        } else if (id == R.id.nav_menu_receivng) {
            toolbar.setTitle("Receiving");
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
            navigationView.getHeaderView(0).setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            setNavMenuItemThemeColors(this.getResources().getColor(R.color.colorPrimary));
//            navigationView.setItemTextColor(navMenuTextList);
//            navigationView.setItemIconTintList(navMenuIconList);

            tmp = getSupportFragmentManager().findFragmentByTag("tag_receiving");
            if (tmp == null || resetReceiveFragment == 1) {
                Fragment s = new MenuReceivingFragment();
                s.setArguments(config);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, s, "tag_receiving").addToBackStack("MenuReceive").commit();
            } else {
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, tmp, "tag_receiving").addToBackStack("MenuReceive").commit();
            }
            FragmentName = "Receive Details";
        } else if (id == R.id.nav_menu_transit) {
            toolbar.setTitle("Transit");
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
            navigationView.getHeaderView(0).setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            setNavMenuItemThemeColors(this.getResources().getColor(R.color.colorPrimary));
//            navigationView.setItemTextColor(navMenuTextList);
//            navigationView.setItemIconTintList(navMenuIconList);

            tmp = getSupportFragmentManager().findFragmentByTag("tag_transit");
            if (tmp == null || (resetTransitFragment == false && typingTransitFragment == false) ) {
                Fragment s = new MenuTransitFragment();
                s.setArguments(config);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, s, "tag_transit").addToBackStack("MenuTransit").commit();
            } else {
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, tmp, "tag_transit").addToBackStack("MenuTransit").commit();
            }
            FragmentName = "Transit";
        } else if (id == R.id.nav_menu_picking) {
            toolbar.setTitle("Picking");
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
            navigationView.getHeaderView(0).setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            setNavMenuItemThemeColors(this.getResources().getColor(R.color.colorPrimary));
//            navigationView.setItemTextColor(navMenuTextList);
//            navigationView.setItemIconTintList(navMenuIconList);

            tmp = getSupportFragmentManager().findFragmentByTag("tag_picking");
            if (tmp == null  || resetPickFragment == 1) {
                Fragment s = new MenuPickingFragment();
                s.setArguments(config);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, s, "tag_picking").addToBackStack("MenuPick").commit();
            } else {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, tmp, "tag_picking").addToBackStack("MenuPick").commit();
            }
            FragmentName = "Picking Details";

        } else if (id == R.id.nav_menu_counting) {
            toolbar.setTitle("Counting");
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
            navigationView.getHeaderView(0).setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            setNavMenuItemThemeColors(this.getResources().getColor(R.color.colorPrimary));
//            navigationView.setItemTextColor(navMenuTextList);
//            navigationView.setItemIconTintList(navMenuIconList);

            tmp = getSupportFragmentManager().findFragmentByTag("tag_counting");
            if (tmp == null || typingCountingFragment == false) {
                Fragment s = new MenuCountingFragment();
                s.setArguments(config);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, s, "tag_counting").addToBackStack("MenuCounting").commit();
            } else {
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, tmp, "tag_counting").addToBackStack("MenuCounting").commit();
            }
            FragmentName = "Counting";
        } else if (id == R.id.nav_menu_change) {
            toolbar.setTitle("Change Location");
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
            navigationView.getHeaderView(0).setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            setNavMenuItemThemeColors(this.getResources().getColor(R.color.colorPrimary));
//            navigationView.setItemTextColor(navMenuTextList);
//            navigationView.setItemIconTintList(navMenuIconList);

            tmp = getSupportFragmentManager().findFragmentByTag("tag_change");
            if (tmp == null || ( resetChangeFragment == false && typingChangeFragment == false)) {
                Fragment s = new MenuChangeFragment();
                s.setArguments(config);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, s, "tag_change").addToBackStack("MenuChange").commit();
            } else {
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, tmp, "tag_change").addToBackStack("MenuChange").commit();
            }
            FragmentName = "Change Location";
        } else if (id == R.id.nav_menu_profile) {
            toolbar.setTitle("Profile");
            toolbar.setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
            window.setNavigationBarColor(this.getResources().getColor(R.color.colorPrimary));
            navigationView.getHeaderView(0).setBackgroundColor(this.getResources().getColor(R.color.colorPrimary));
            setNavMenuItemThemeColors(this.getResources().getColor(R.color.colorPrimary));
//            navigationView.setItemTextColor(navMenuTextListProfile);
//            navigationView.setItemIconTintList(navMenuIconListProfile);

            tmp = getSupportFragmentManager().findFragmentByTag("tag_profile");
            if (tmp == null) {
                Fragment s = new MenuProfileFragment();
                s.setArguments(config);
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, s, "tag_profile").addToBackStack(null).commit();
            } else {
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.FragmentMain, tmp, "tag_profile").addToBackStack(null).commit();
            }
            FragmentName = "Profile";
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void Update(int i) {
        navigationView.getMenu().getItem(i).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(i));
    }

    public void restartApp() {
        Toast.makeText(this, "Session expire", Toast.LENGTH_SHORT).show();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

    @Override
    public void setPickDataSerial(JSONArray data) {
        if (data.length() != 0) {
            for (int i = 0 ; i < data.length() ; i++) {
                try {
                    dt_serial_pick.put(data.get(i));
                } catch (JSONException e) {
                }
            }
        }
    }

    @Override
    public void setReciveDataSerial(JSONArray data) {
        if (data.length() != 0) {
            for (int i = 0 ; i < data.length() ; i++) {
                try {
                    dt_serial_receive.put(data.get(i));
                } catch (JSONException e) {
                }
            }
        }
    }

    public JSONObject LoadUserData (String username) {
        JSONObject user_data = new JSONObject();

        return user_data;
    }

    public boolean checkinternet(){
        if(((ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo()==null)
        return false;
        return true;
    }

    public void setNavMenuItemThemeColors(int color){
        //Setting default colors for menu item Text and Icon
        int navDefaultTextColor = Color.parseColor("#202020");
        int navDefaultIconColor = Color.parseColor("#737373");

        //Defining ColorStateList for menu item Text
        ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        color,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );

        //Defining ColorStateList for menu item Icon
        ColorStateList navMenuIconList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        color,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor
                }
        );

        navigationView.setItemTextColor(navMenuTextList);
        navigationView.setItemIconTintList(navMenuIconList);

    }

}
