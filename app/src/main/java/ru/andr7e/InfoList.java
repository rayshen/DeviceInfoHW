package ru.andr7e;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Pair;

import ru.andr7e.androidshell.ShellExecuter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by andrey on 03.03.16.
 */
public class InfoList
{
    public static ArrayList< Pair<String, String> > platfromDriversList;

    public static void addItem (ArrayList< Pair<String, String> > objList, String key, String value)
    {
        if ( ! value.isEmpty())
        {
            objList.add(new Pair<String, String>(key, value));
        }
    }

    public static ArrayList< Pair<String, String> > buildDriversInfoList()
    {
        if (platfromDriversList == null || platfromDriversList.isEmpty()) {
            ArrayList<Pair<String, String>> objList = new ArrayList<Pair<String, String>>();

            ArrayList<String> driverList = InfoUtils.getPlatformDeviceList();

            Collections.sort(driverList);

            int i = 0;
            for (String driver : driverList) {
                addItem(objList, driver, " ");
                i++;
            }

            platfromDriversList = objList;
        }

        return platfromDriversList;
    }

    public static ArrayList< Pair<String, String> > buildDriversInfoListUpload()
    {
        ArrayList< Pair<String, String> > objList = new ArrayList< Pair<String, String> >();

        ArrayList<String> driverList = InfoUtils.getPlatformDeviceList();

        Collections.sort(driverList);

        addItem(objList, "Drivers", TextUtils.join("\n", driverList));

        return objList;
    }

    public static ArrayList< Pair<String, String> > buildInfoList(boolean isRootMode, boolean isAppendAddress, Context context, ActivityManager.MemoryInfo memoryInfo)
    {
        ShellExecuter exec = new ShellExecuter();

        ArrayList< Pair<String, String> > objList = new ArrayList< Pair<String, String> >();

        String platform = InfoUtils.getPlatform();

        addItem(objList, InfoUtils.MANUFACTURER, InfoUtils.getManufacturer());
        addItem(objList, InfoUtils.MODEL, InfoUtils.getModel());
        addItem(objList, InfoUtils.BRAND, InfoUtils.getBrand());

        addItem(objList, InfoUtils.RESOLUTION, InfoUtils.getResolution());

        addItem(objList, InfoUtils.PLATFORM, platform);

        addItem(objList, "Android Version", InfoUtils.getAndroidVersion());
        addItem(objList, "API", InfoUtils.getAndroidAPI());

        addItem(objList, "Kernel", InfoUtils.getKernelVersion());

        //
        HashMap<String,String> hash = InfoUtils.getDriversHash(exec, isAppendAddress, context);

        //
        String cmdline = "";

        if (isRootMode)
        {
            cmdline = InfoUtils.getCmdline(exec);

            if ( ! cmdline.isEmpty() && InfoUtils.isMtkPlatform(platform))
            {
                String lcmName = InfoUtils.getLcmName(cmdline);

                if ( ! lcmName.isEmpty())
                {
                    hash.put(InfoUtils.LCM, lcmName);
                }
            }
        }

        if (InfoUtils.isQualcomPlatform(platform))
        {
            String hwinfo = InfoUtils.getQcomHwInfo(exec);

            if ( ! hwinfo.isEmpty())
            {
                hash.put(InfoUtils.EXTRA, hwinfo);

                String lcmName = InfoUtils.getQcomLcdName(hwinfo);

                if ( ! lcmName.isEmpty())
                {
                    hash.put(InfoUtils.LCM, lcmName);
                }
            }
        }

        hash.put(InfoUtils.SOUND, InfoUtils.getSoundCard());

        if (InfoUtils.isRkPlatform(platform))
            hash.put(InfoUtils.WIFI,  InfoUtils.getRkWiFi());

        String[] keyList = {
                InfoUtils.PMIC,
                InfoUtils.RTC,
                InfoUtils.LCM,
                InfoUtils.TOUCHPANEL,
                InfoUtils.ACCELEROMETER,
                InfoUtils.ALSPS,
                InfoUtils.MAGNETOMETER,
                InfoUtils.GYROSCOPE,
                InfoUtils.CHARGER,
                InfoUtils.CAMERA,
                InfoUtils.CAMERA_BACK,
                InfoUtils.CAMERA_FRONT,
                InfoUtils.LENS,
                InfoUtils.WIFI,
                InfoUtils.SOUND,
                InfoUtils.MODEM,
                InfoUtils.UNKNOWN,
                InfoUtils.EXTRA
                //InfoUtils.DRIVERS
        };

        for (String key : keyList)
        {
            if (hash.containsKey(key))
            {
                String value = hash.get(key);

                addItem(objList, key, value);
            }
        }

        //
        addItem(objList, InfoUtils.RAM,   MemInfo.getModuleInfo(memoryInfo));
        addItem(objList, InfoUtils.FLASH, MemInfo.getFlashName());

        addItem(objList, "Baseband", Build.getRadioVersion());

        addItem(objList, "cmdline", cmdline);

        //addItem(objList, "Partitions", InfoUtils.getPartitions(platform, exec));

        return objList;
    }

    public static ArrayList< Pair<String, String> > buildProjectConfigList()
    {
        String[] keyList = {
                InfoUtils.PLATFORM,
                InfoUtils.RESOLUTION,
                InfoUtils.PMIC,
                InfoUtils.RTC,
                InfoUtils.LCM,
                InfoUtils.TOUCHPANEL,
                InfoUtils.ACCELEROMETER,
                InfoUtils.ALSPS,
                InfoUtils.MAGNETOMETER,
                InfoUtils.GYROSCOPE,
                InfoUtils.CHARGER,
                InfoUtils.CAMERA,
                InfoUtils.CAMERA_BACK,
                InfoUtils.CAMERA_FRONT,
                InfoUtils.LENS,
                InfoUtils.SOUND,
                InfoUtils.MODEM,
                InfoUtils.VERSION,
                InfoUtils.UNKNOWN
        };

        ArrayList< Pair<String, String> > objList = new ArrayList< Pair<String, String> >();

        HashMap<String,String>  hash = MtkUtil.getProjectDriversHash();

        for (String key : keyList)
        {
            if (hash.containsKey(key))
            {
                String value = hash.get(key);

                addItem(objList, key, value);
            }
        }

        return objList;
    }

    public static ArrayList< Pair<String, String> > buildFeatureInfoList(Context context, ActivityManager.MemoryInfo memoryInfo)
    {
        ArrayList<Pair<String, String>> objList = new ArrayList<Pair<String, String>>();

        HashMap<String,String> hash = new HashMap<String,String>();

        hash.put(InfoUtils.BUILD,  Build.DISPLAY);

        String patchLevel = InfoUtils.getPatchLevel();

        if ( ! patchLevel.isEmpty())
        {
            hash.put(InfoUtils.PATCH,  patchLevel);
        }

        String platform = InfoUtils.getPlatform();

        CpuInfo cpuInfo = new CpuInfo();

        String cpuModel = cpuInfo.getHardware();

        if (InfoUtils.isRkPlatform(platform))
        {
            String soc = CpuFreq.getCpuSoc();

            if ( ! soc.isEmpty())
            {
                cpuModel = soc;
            }
        }

        hash.put(InfoUtils.CPU,      cpuModel);
        hash.put(InfoUtils.CORES,    cpuInfo.getCores());
        hash.put(InfoUtils.REVISION, cpuInfo.getRevision());
        hash.put(InfoUtils.FAMILY,   cpuInfo.getArmFamily());
        hash.put(InfoUtils.ABI,      cpuInfo.getCpuABI());

        hash.put(InfoUtils.CLOCK_SPEED, Util.formatUnit(CpuFreq.getClockSpeed(), "MHz"));
        hash.put(InfoUtils.GOVERNOR, CpuFreq.getGovernor());

        if (InfoUtils.isQualcomPlatform(platform))
        {
            hash.put(InfoUtils.GPU, "Adreno");
            hash.put(InfoUtils.GPU_CLOCK, Util.formatUnit(GpuFreq.getClockSpeed(), "MHz"));
        }
        else if (InfoUtils.isMtkPlatform(platform))
        {
            hash.put(InfoUtils.GPU_CLOCK, Util.formatUnit(GpuMtkFreq.getClockSpeed(), "MHz"));
        }

        hash.put(InfoUtils.MEMORY, MemInfo.getTotalSize(memoryInfo));
        hash.put(InfoUtils.DISK, StorageInfo.getTotalInternalSize());

        String[] keyList = {
                InfoUtils.CPU,
                InfoUtils.CORES,
                InfoUtils.FAMILY,
                InfoUtils.ABI,
                InfoUtils.REVISION,
                InfoUtils.CLOCK_SPEED,
                InfoUtils.GOVERNOR,
                InfoUtils.GPU,
                InfoUtils.GPU_CLOCK,
                InfoUtils.MEMORY,
                InfoUtils.DISK,
                InfoUtils.BUILD,
                InfoUtils.PATCH,
        };

        for (String key : keyList)
        {
            if (hash.containsKey(key))
            {
                String value = hash.get(key);

                addItem(objList, key, value);
            }
        }

        return objList;
    }
}