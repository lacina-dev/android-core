/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_vitulus_control;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import sensor_msgs.CompressedImage;
import std_msgs.String;

import org.ros.address.InetAddressFactory;
import org.ros.android.MessageCallable;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.android.view.VirtualJoystickView;
import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;
import org.ros.android.view.visualization.VisualizationView;
import org.ros.android.view.visualization.layer.CameraControlLayer;
import org.ros.android.view.visualization.layer.LaserScanLayer;
import org.ros.android.view.visualization.layer.Layer;
import org.ros.android.view.visualization.layer.OccupancyGridLayer;
import org.ros.android.view.visualization.layer.PathLayer;
import org.ros.android.view.visualization.layer.PosePublisherLayer;
import org.ros.android.view.visualization.layer.PoseSubscriberLayer;
import org.ros.android.view.visualization.layer.RobotLayer;

import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.time.NtpTimeProvider;

import java.util.concurrent.TimeUnit;



/**
 * An app that can be used to control a remote robot. This app also demonstrates
 * how to use some of views from the rosjava android library.
 * 
 * @author munjaldesai@google.com (Munjal Desai)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public class MainActivity extends RosActivity{


  android.widget.EditText mapStringLoadTxtView;
  private RosTextView<std_msgs.String> mapListRosTxtView;
  private RosTextView<std_msgs.String> mapStatusRosTxtView;

  private static final java.lang.String MAP_FRAME = "map";
  private static final java.lang.String ROBOT_FRAME = "t265_pose_frame";

  private VirtualJoystickView virtualJoystickView;
  private VisualizationView visualizationView;
  private RosImageView<sensor_msgs.CompressedImage> image;
  private RosTextView<std_msgs.String> rosTextViewtxtups;
  private RosTextView<std_msgs.String> rosTextViewtxtbat;
  private RosTextView<std_msgs.String> rosTextViewtxtupsinfo;
  private RosIconColorView<std_msgs.String> fontWiewiconcolorups;
  private RosIconColorView<std_msgs.String> fontWiewiconcolorbat;
  private android.widget.EditText mapStringSaveTxtView;


  private Talker talker;
  private TalkerMotor talker_motor;
  private TalkerGoalCancel talker_goal_cancel;
  private TalkerCmdVel talker_cmd_vel;
  private TalkerMapSave talker_save_map;
  private TalkerMapLoad talker_load_map;
  private TalkerMapNew talker_new_map;
  private TalkerMapEdit talker_edit_map;


  private CameraControlLayer cameraControlLayer;
  private final SystemCommands systemCommands;
  private ToggleButton btnMotors;
  private ToggleButton btnJoy;
  private ToggleButton btnNavCam;
  private ToggleButton btnMenu;
  private ToggleButton btnLidar;
  private ToggleButton btnFollow;
  private ToggleButton btnNavi;
  private ToggleButton btnMap;
  private ToggleButton btnSpeed;
  private Button btnLoadMap;
  private Button btnEditMap;
  private Button btnSaveMap;
  private ToggleButton btnUps;
  private Button btnNewMap;

  private SrvsClientMotorStop srvsClientMotorStop;
  private SrvsClientMotorStart srvsClientMotorStart;


  public MainActivity() {

    super("Vitulus", "Vitulus");
    systemCommands = new SystemCommands();
  }

  private Context context = this;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.settings_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.virtual_joystick_snap:
        if (!item.isChecked()) {
          item.setChecked(true);
          virtualJoystickView.EnableSnapping();
        } else {
          item.setChecked(false);
          virtualJoystickView.DisableSnapping();
        }

      case R.id.motor_power_properties:
        if (!item.isChecked()) {
          item.setChecked(true);
          talker.publish("motor on");
        } else {
          item.setChecked(false);
          talker.publish("motor off");
        }

        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.main);

    virtualJoystickView = (VirtualJoystickView) findViewById(R.id.virtual_joystick);
    virtualJoystickView.setTopicName("cmd_vel");


    visualizationView = (VisualizationView) findViewById(R.id.visualization);
//    visualizationView.getCamera().jumpToFrame("map");
//    visualizationView.onCreate(Lists.<Layer>newArrayList(new CameraControlLayer(),
//            new OccupancyGridLayer("map"), new PathLayer("move_base/NavfnROS/plan"), new PathLayer(
//                    "move_base_dynamic/NavfnROS/plan"), new LaserScanLayer("scan"),
//            new PoseSubscriberLayer("simple_waypoints_server/goal_pose"), new PosePublisherLayer(
//                    "simple_waypoints_server/goal_pose"), new RobotLayer("base_footprint")));
    visualizationView.getCamera().jumpToFrame(ROBOT_FRAME);

    visualizationView.onCreate(Lists.<Layer>newArrayList(
            new CameraControlLayer(),
            new OccupancyGridLayer("/rtabmap/grid_map"),
            new CostmapGridLayer("/move_base/local_costmap/costmap"),
            new LaserScanLayer("/scan"),
//            new PointCloud2DLayer("/rtabmap/local_grid_obstacle"),
            new RobotLayer(ROBOT_FRAME),
            new PoseSubscriberLayer("/move_base/current_goal"),
            new PosePublisherLayer("/move_base_simple/goal"),
            new PathLayer("/move_base/TebLocalPlannerROS/global_plan"),
            new PathLayer("/move_base/TebLocalPlannerROS/local_plan")
    ));
    visualizationView.getCamera().jumpToFrame(ROBOT_FRAME);

    image = findViewById(R.id.image);
    image.setTopicName("camera/color/image_raw/compressed");
    image.setMessageType(CompressedImage._TYPE);
    image.setMessageToBitmapCallable(new BitmapFromCompressedImage());
    LayoutParams lp = (LayoutParams) image.getLayoutParams();
    image.setLayoutParams(lp);

    rosTextViewtxtupsinfo = findViewById(R.id.upsinfoview);
    rosTextViewtxtupsinfo.setTopicName("ups_manager/upsinfo");
    rosTextViewtxtupsinfo.setMessageType(String._TYPE);
    rosTextViewtxtupsinfo.setMessageToStringCallable(new MessageCallable<java.lang.String, String>() {
      @Override
      public java.lang.String call(String message) {
        return message.getData();
      }
    });

    // txtups
    rosTextViewtxtups = findViewById(R.id.txtups);
    rosTextViewtxtups.setTopicName("ups_manager/txtups");
    rosTextViewtxtups.setMessageType(String._TYPE);
    rosTextViewtxtups.setMessageToStringCallable(new MessageCallable<java.lang.String, String>() {
      @Override
      public java.lang.String call(String message) {
        return message.getData();
      }
    });

    // txtbat
    rosTextViewtxtbat = findViewById(R.id.txtbat);
    rosTextViewtxtbat.setTopicName("ups_manager/txtbat");
    rosTextViewtxtbat.setMessageType(String._TYPE);
    rosTextViewtxtbat.setMessageToStringCallable(new MessageCallable<java.lang.String, String>() {
      @Override
      public java.lang.String call(String message) {
        return message.getData();
      }
    });

    // colorups
    fontWiewiconcolorups = findViewById(R.id.iconups);
    fontWiewiconcolorups.setTopicName("ups_manager/colorups");
    fontWiewiconcolorups.setMessageType(String._TYPE);
    fontWiewiconcolorups.setMessageToStringCallable(new MessageCallable<java.lang.String, String>() {
      @Override
      public java.lang.String call(String message) {
        fontWiewiconcolorups.setTextColor(Color.parseColor(message.getData()));
        return message.getData();
      }
    });

    // colorbat
    fontWiewiconcolorbat = findViewById(R.id.iconbat);
    fontWiewiconcolorbat.setTopicName("ups_manager/colorbat");
    fontWiewiconcolorbat.setMessageType(String._TYPE);
    fontWiewiconcolorbat.setMessageToStringCallable(new MessageCallable<java.lang.String, String>() {
      @Override
      public java.lang.String call(String message) {
        java.lang.String msgstr = message.getData();
        java.lang.String[] parts = msgstr.split("---");
        java.lang.String part1 = parts[0]; // 004
        java.lang.String part2 = parts[1]; // 034556
        Log.d("mapdebug", "str1: " + part1 + " str2: " + part2);
        fontWiewiconcolorbat.setTextColor(Color.parseColor(part1));
//        Log.d("mapdebug", "yes1");
        if (part2.equals("fa_battery_full_solid")){
          fontWiewiconcolorbat.setText(info.androidhive.fontawesome.R.string.fa_battery_full_solid);
        }
        if (part2.equals("fa_battery_three_quarters_solid")){
          fontWiewiconcolorbat.setText(info.androidhive.fontawesome.R.string.fa_battery_three_quarters_solid);
        }
        if (part2.equals("fa_battery_half_solid")){
          fontWiewiconcolorbat.setText(info.androidhive.fontawesome.R.string.fa_battery_half_solid);
        }
        if (part2.equals("fa_battery_quarter_solid")){
          fontWiewiconcolorbat.setText(info.androidhive.fontawesome.R.string.fa_battery_quarter_solid);
        }
        if (part2.equals("fa_battery_empty_solid")){
          fontWiewiconcolorbat.setText(info.androidhive.fontawesome.R.string.fa_battery_empty_solid);
        }
        return message.getData();
      }
    });

    final ImageView menuBar = (ImageView) findViewById(R.id.menuBar);


    btnLidar = (ToggleButton) findViewById(R.id.btnLidar);
    btnLidar.setChecked(false);
    btnLidar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          srvsClientMotorStart.sendRequest();
        } else {
          srvsClientMotorStop.sendRequest();
        }
      }
    });

    btnFollow = (ToggleButton) findViewById(R.id.btnFixedFollow);
    btnFollow.setChecked(false);
    btnFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          Preconditions.checkNotNull(visualizationView);
          visualizationView.getCamera().jumpToFrame(ROBOT_FRAME);
        } else {
          Preconditions.checkNotNull(visualizationView);
          visualizationView.getCamera().jumpToFrame(MAP_FRAME);
        }
      }
    });


    btnNavCam = (ToggleButton) findViewById(R.id.btnNaviCam);
    btnNavCam.setChecked(true);
    btnNavCam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        LayoutParams lp = (LayoutParams) image.getLayoutParams();
        float factor = context.getResources().getDisplayMetrics().density;

        if (isChecked) {
          visualizationView.setVisibility(View.INVISIBLE);
          lp.topMargin = (int)(0 * factor);
          lp.leftMargin = (int)(0 * factor);
          lp.width = LayoutParams.MATCH_PARENT;
          lp.height = LayoutParams.MATCH_PARENT;
          image.setLayoutParams(lp);
          image.setVisibility(View.VISIBLE);
          virtualJoystickView.setVisibility(View.VISIBLE);
          btnJoy.setChecked(true);
        } else {
          visualizationView.setVisibility(View.VISIBLE);
          virtualJoystickView.setVisibility(View.INVISIBLE);
          image.setVisibility(View.VISIBLE);
          lp.topMargin = (int)(40 * factor);
          lp.leftMargin = (int)(5 * factor);
          lp.width = (int)(150 * factor);
          lp.height = lp.width / 16 * 9;
          image.setLayoutParams(lp);
          btnJoy.setChecked(false);
        }
      }
    });


    btnMotors = (ToggleButton) findViewById(R.id.btnMotors);
    btnMotors.setChecked(true);
    btnMotors.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          talker_motor.publish(true);
          Toast.makeText(getApplicationContext(), "Motors switched on!", Toast.LENGTH_SHORT).show();
        } else {
          talker_motor.publish(false);
          Toast.makeText(getApplicationContext(), "Motors switched off!", Toast.LENGTH_SHORT).show();
        }
      }
    });


    btnJoy = (ToggleButton) findViewById(R.id.btnJoy);
    btnJoy.setChecked(true);
    btnJoy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          virtualJoystickView.setVisibility(View.VISIBLE);
        } else {
          virtualJoystickView.setVisibility(View.INVISIBLE);
        }
      }
    });


    btnUps = (ToggleButton) findViewById(R.id.btnUps);
    btnUps.setChecked(false);
    btnUps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          rosTextViewtxtupsinfo.setVisibility(View.VISIBLE);
        } else {
          rosTextViewtxtupsinfo.setVisibility(View.INVISIBLE);
        }
      }
    });


    btnSpeed = (ToggleButton) findViewById(R.id.btnSpeed);
    btnSpeed.setChecked(false);


    mapStringLoadTxtView = (EditText) findViewById(R.id.mapStringLoadTxtView);

    mapStringSaveTxtView = (EditText) findViewById(R.id.mapStringSaveTxtView);


    mapStatusRosTxtView = findViewById(R.id.mapStatusRosTxtView);
    mapStatusRosTxtView.setTopicName("navi_manager/status");
    mapStatusRosTxtView.setMessageType(String._TYPE);
    mapStatusRosTxtView.setMessageToStringCallable(new MessageCallable<java.lang.String, String>() {
      @Override
      public java.lang.String call(String message) {
        return message.getData();
      }
    });


    mapListRosTxtView = findViewById(R.id.mapListRosTxtView);
    mapListRosTxtView.setTopicName("navi_manager/map_list");
    mapListRosTxtView.setMessageType(String._TYPE);
    mapListRosTxtView.setMessageToStringCallable(new MessageCallable<java.lang.String, String>() {
      @Override
      public java.lang.String call(String message) {
        java.lang.String msgstr = message.getData();
        java.lang.String frmtstr = msgstr.replace("---", "\n");
        Log.d("mapdebug", "frmtstr: " + frmtstr);
        //mapListTxtView.setText(frmtstr);
        return frmtstr;
      }
    });


    btnLoadMap = (Button) findViewById(R.id.btnLoadMap);
    btnLoadMap.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View v) {
        java.lang.String mapString = mapStringLoadTxtView.getText().toString();
        talker_load_map.publish(mapString);
      }
    });


    btnEditMap = (Button) findViewById(R.id.btnEditMap);
    btnEditMap.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View v) {
        talker_edit_map.publish("EDIT");
      }
    });


    btnSaveMap = (Button) findViewById(R.id.btnSaveMap);
    btnSaveMap.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View v) {
        java.lang.String mapString = mapStringSaveTxtView.getText().toString();
        talker_save_map.publish(mapString);
      }
    });


    btnNewMap = (Button) findViewById(R.id.btnNewMap);
    btnNewMap.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View v) {
        talker_new_map.publish("NEW");
      }
    });






    btnNavi = (ToggleButton) findViewById(R.id.btnNavi);
    btnNavi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          btnSpeed.setVisibility(View.INVISIBLE);
        } else {
          btnSpeed.setVisibility(View.VISIBLE);
        }
      }
    });
    btnNavi.setChecked(true);



    btnMap = (ToggleButton) findViewById(R.id.btnMap);
    btnMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ImageView mapPanel;
        mapPanel = (ImageView) findViewById(R.id.mapPanel);
        TextView labelSave;
        labelSave = (TextView) findViewById(R.id.labelSave);
        TextView labeLoad;
        labeLoad = (TextView) findViewById(R.id.labelLoad);

        if (isChecked) {
          btnLoadMap.setVisibility(View.INVISIBLE);
          btnEditMap.setVisibility(View.INVISIBLE);
          btnSaveMap.setVisibility(View.INVISIBLE);
          btnNewMap.setVisibility(View.INVISIBLE);
          mapPanel.setVisibility(View.INVISIBLE);
          mapStringLoadTxtView.setVisibility(View.INVISIBLE);
          mapStringSaveTxtView.setVisibility(View.INVISIBLE);
          mapListRosTxtView.setVisibility(View.INVISIBLE);
          mapStatusRosTxtView.setVisibility(View.INVISIBLE);
//          virtualJoystickView.setVisibility(View.INVISIBLE);
          labelSave.setVisibility(View.INVISIBLE);
          labeLoad.setVisibility(View.INVISIBLE);
          btnJoy.setChecked(true);
        } else {
          btnLoadMap.setVisibility(View.VISIBLE);
          btnEditMap.setVisibility(View.VISIBLE);
          btnSaveMap.setVisibility(View.VISIBLE);
          btnNewMap.setVisibility(View.VISIBLE);
          mapPanel.setVisibility(View.VISIBLE);
          mapListRosTxtView.setVisibility(View.VISIBLE);
          mapStringLoadTxtView.setVisibility(View.VISIBLE);
          mapStringSaveTxtView.setVisibility(View.VISIBLE);
          mapStatusRosTxtView.setText("");
          mapStatusRosTxtView.setVisibility(View.VISIBLE);
//          virtualJoystickView.setVisibility(View.VISIBLE);
          labelSave.setVisibility(View.VISIBLE);
          labeLoad.setVisibility(View.VISIBLE);
          btnJoy.setChecked(false);
        }
      }
    });
    btnMap.setChecked(true);


    btnMenu = (ToggleButton) findViewById(R.id.btnMenu);
    btnMenu.setChecked(true);
    btnMenu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          btnMotors.setVisibility(View.VISIBLE);
          btnLidar.setVisibility(View.VISIBLE);
          btnJoy.setVisibility(View.VISIBLE);
          btnFollow.setVisibility(View.VISIBLE);
          menuBar.setVisibility(View.VISIBLE);
          btnUps.setVisibility(View.VISIBLE);
        } else {
          btnMotors.setVisibility(View.INVISIBLE);
          btnLidar.setVisibility(View.INVISIBLE);
          btnJoy.setVisibility(View.INVISIBLE);
          btnFollow.setVisibility(View.INVISIBLE);
          menuBar.setVisibility(View.INVISIBLE);
          btnUps.setVisibility(View.INVISIBLE);
        }
      }
    });



    Button btn = (Button) findViewById(R.id.buttonStop);
    btn.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View v) {
        talker_goal_cancel.publish("");
        talker_cmd_vel.publish("");
        try {
          //set time in mili
          Thread.sleep(200);

        }catch (Exception e){
          e.printStackTrace();
        }
        talker_motor.publish(false);

//        Toast.makeText(getApplicationContext(), "Goal canceled", Toast.LENGTH_SHORT).show();
        btnMotors.setChecked(false);
        Toast.makeText(getApplicationContext(), "Stopped!", Toast.LENGTH_SHORT).show();
      }
    });

  }

  @Override
  protected void init(NodeMainExecutor nodeMainExecutor) {
    talker = new Talker();
    talker_motor = new TalkerMotor();
    talker_goal_cancel = new TalkerGoalCancel();
    talker_cmd_vel = new TalkerCmdVel();
    talker_save_map = new TalkerMapSave();
    talker_load_map = new TalkerMapLoad();
    talker_new_map = new TalkerMapNew();
    talker_edit_map = new TalkerMapEdit();

    srvsClientMotorStop = new SrvsClientMotorStop();
    srvsClientMotorStart = new SrvsClientMotorStart();




    visualizationView.init(nodeMainExecutor);

    NodeConfiguration nodeConfiguration =
        NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(),
            getMasterUri());

    NtpTimeProvider ntpTimeProvider =
            new NtpTimeProvider(InetAddressFactory.newFromHostString("195.113.144.201"),
                    nodeMainExecutor.getScheduledExecutorService());
    ntpTimeProvider.startPeriodicUpdates(1, TimeUnit.MINUTES);
    nodeConfiguration.setTimeProvider(ntpTimeProvider);

    nodeMainExecutor.execute(virtualJoystickView, nodeConfiguration.setNodeName("android/joy"));
    nodeMainExecutor.execute(visualizationView, nodeConfiguration.setNodeName("android/map_view"));
    nodeMainExecutor.execute(systemCommands, nodeConfiguration.setNodeName("android/syscom"));
    nodeMainExecutor.execute(image, nodeConfiguration.setNodeName("android/video_view"));
    nodeMainExecutor.execute(rosTextViewtxtups, nodeConfiguration.setNodeName("android/txtups"));
    nodeMainExecutor.execute(rosTextViewtxtbat, nodeConfiguration.setNodeName("android/txtbat"));
    nodeMainExecutor.execute(talker, nodeConfiguration.setNodeName("android/talker"));
    nodeMainExecutor.execute(talker_motor, nodeConfiguration.setNodeName("android/motors"));
    nodeMainExecutor.execute(fontWiewiconcolorups, nodeConfiguration.setNodeName("android/iconcolorups"));
    nodeMainExecutor.execute(fontWiewiconcolorbat, nodeConfiguration.setNodeName("android/iconcolorbat"));
    nodeMainExecutor.execute(talker_goal_cancel, nodeConfiguration.setNodeName("android/goalCancel"));
    nodeMainExecutor.execute(talker_cmd_vel, nodeConfiguration.setNodeName("android/cmdVel"));
    nodeMainExecutor.execute(srvsClientMotorStop, nodeConfiguration.setNodeName("android/srvs_cli_motor_stop"));
    nodeMainExecutor.execute(srvsClientMotorStart, nodeConfiguration.setNodeName("android/srvs_cli_motor_start"));
    nodeMainExecutor.execute(rosTextViewtxtupsinfo, nodeConfiguration.setNodeName("android/upsinfo"));
    nodeMainExecutor.execute(mapListRosTxtView, nodeConfiguration.setNodeName("android/maplist"));
    nodeMainExecutor.execute(talker_save_map, nodeConfiguration.setNodeName("android/mapsave"));
    nodeMainExecutor.execute(talker_load_map, nodeConfiguration.setNodeName("android/mapload"));
    nodeMainExecutor.execute(talker_new_map, nodeConfiguration.setNodeName("android/mapnew"));
    nodeMainExecutor.execute(talker_edit_map, nodeConfiguration.setNodeName("android/editmap"));
    nodeMainExecutor.execute(mapStatusRosTxtView, nodeConfiguration.setNodeName("android/mapstatus"));

  }
}
