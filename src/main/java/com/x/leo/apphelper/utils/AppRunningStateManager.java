package com.x.leo.apphelper.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.x.leo.apphelper.log.XLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @作者:My
 * @创建日期: 2017/5/16 9:28
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class AppRunningStateManager {

    public static final int AID_APP  = 10000;
    public static final int AID_USER = 100000;

    public static void  isRunForeground(final Context context, final StateObtainListener l) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String foregroundApp = getForegroundApp(context);
                    XLog.INSTANCE.d("AppRunningStateManager" + foregroundApp,10);
                    l.onSuccess(context.getPackageName().equals(foregroundApp));
                }catch (Exception e){
                    l.onFailed(e);
                }
            }
        }).start();
    }


    public static boolean isRunBackground(Context context,StateObtainListener l) {
        return false;
    }

    /**
     * 1.proc下以数字命名的文件夹，文件夹名即是一个进程的pid，该文件夹下的文件包含这个进程的信息；
     * <p>
     * 2.cgroup，控制组群（control groups）的简写，是Linux内核的一个功能，用来限制，控制与分离一个进程组群的资源（如CPU、内存、磁盘输入输出等）。cpu:设置cpu的使用率；cpuacct：记录cpu的统计信息。
     * <p>
     * 3.bg_non_interactive，运行cpu的一个分组，另一分组是apps，当一个应用（进程）即可从apps分组切换到bg_non_interactive，也可以切换回来。apps分组可以利用95%的cpu，而bg_non_interactive只能使用大约5%。
     * <p>
     * 4.cmdline，显示内核启动的命令行。
     * <p>
     * 5.oom_score_adj,这个文件的数值用来标记在内存不足的情况下，启发式的。选择哪个进程被杀掉,值从0（从不被杀掉）到1000（总是被杀掉）。
     *
     * @return
     */
    public static String getForegroundApp(Context context) {
        if (Build.VERSION.SDK_INT >= 22) {


            File[] files = new File("/proc").listFiles();
            int lowestOomScore = Integer.MAX_VALUE;
            String foregroundProcess = null;
            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                int pid;

                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException e) {
                    continue;
                }

                try {
                    String cgroup = read(String.format("/proc/%d/cgroup", pid));
                    String[] lines = cgroup.split("\n");
                    String cpuSubsystem;
                    String cpuaccctSubsystem;

                    if (lines.length == 2) {// 有的手机里cgroup包含2行或者3行，我们取cpu和cpuacct两行数据,xiaomi 5hang
                        cpuSubsystem = lines[0];
                        cpuaccctSubsystem = lines[1];
                    } else if (lines.length == 3) {
                        cpuSubsystem = lines[0];
                        cpuaccctSubsystem = lines[2];
                    } else if (lines.length == 5) {
                        cpuSubsystem = lines[2];
                        cpuaccctSubsystem = lines[4];
                    } else {
                        continue;
                    }
                    //Integer.toString(pid)
                    if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
                        // not an application process
                        continue;
                    }
                    if (cpuSubsystem.endsWith("bg_non_interactive")) {
                        // TODO:background policy
                        continue;
                    }

                    String cmdline = read(String.format("/proc/%d/cmdline", pid));
                    if (cmdline.contains("com.android.systemui")) {
                        continue;
                    }
                    int uid = Integer.parseInt(cpuaccctSubsystem.split(":")[2]
                            .split("/")[1].replace("uid_", ""));
                    if (uid >= 1000 && uid <= 1038) {
                        // system process
                        continue;
                    }
                    int appId = uid - AID_APP;
                    int userId = 0;
                    // loop until we get the correct user id.
                    // 100000 is the offset for each user.

                    while (appId > AID_USER) {
                        appId -= AID_USER;
                        userId++;
                    }

                    if (appId < 0) {
                        continue;
                    }
                    // u{user_id}_a{app_id} is used on API 17+ for multiple user
                    // account support.
                    // String uidName = String.format("u%d_a%d", userId, appId);
                    File oomScoreAdj = new File(String.format(
                            "/proc/%d/oom_score_adj", pid));
                    if (oomScoreAdj.canRead()) {
                        int oomAdj = Integer.parseInt(read(oomScoreAdj
                                .getAbsolutePath()));

                        if (cmdline.contains("com.miui") || cmdline.contains("com.android")||cmdline.contains("com.amap.android")
                                || cmdline.contains("android.process")) {
                            continue;
                        } else {
                            if (oomAdj != 0) {
                                continue;
                            }
                        }
                    }else{
                        continue;
                    }
                    int oomscore = Integer.parseInt(read(String.format(
                            "/proc/%d/oom_score", pid)));
                    XLog.INSTANCE.d("appinfo:" + cmdline + ":" + oomscore,10);
//                    if ("Xiaomi".equals(Build.BRAND)) {
//                        if (lowestOomScore == 0) {
//                            foregroundProcess = cmdline;
//                        }else{
//                            if (oomscore < lowestOomScore) {
//                                lowestOomScore = oomscore;
//                                foregroundProcess = cmdline;
//                            }
//                        }
//                    }
                    if (oomscore < lowestOomScore) {
                        lowestOomScore = oomscore;
                        foregroundProcess = cmdline;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return foregroundProcess;
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                final int PROCESS_STATE_TOP = 2;
                ActivityManager.RunningAppProcessInfo currentInfo = null;
                Field field = null;
                try {
                    field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
                } catch (Exception ignored) {
                }
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo app : runningAppProcesses) {
                    if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                            app.importanceReasonCode == 0) {
                        Integer state = null;
                        try {
                            state = field.getInt(app);
                        } catch (Exception ignored) {
                        }
                        if (state != null && state == PROCESS_STATE_TOP) {
                            currentInfo = app;
                            break;
                        }
                    }
                }
                if (currentInfo != null) {
                    String processName = currentInfo.processName;
                    String[] pkgList = currentInfo.pkgList;
                    if (pkgList == null || pkgList.length <= 0) {
                        return null;
                    }
                    return pkgList[0];
                }
                return null;
            } else {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1000);
                if (runningTasks != null && runningTasks.size() >= 1) {
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    ComponentName baseActivity = runningTaskInfo.baseActivity;
                    return baseActivity.getPackageName();
                } else {
                    return null;
                }
            }
        }

    }

    private static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());

        for (String line = reader.readLine(); line != null; line = reader
                .readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString().trim();// 不调用trim()，包名后会带有乱码
    }

    public  static interface StateObtainListener {
        void onSuccess(boolean result);
        void onFailed(Exception e);
    }
}
