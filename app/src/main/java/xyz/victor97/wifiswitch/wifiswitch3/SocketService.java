package xyz.victor97.wifiswitch.wifiswitch3;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;


/**
 * 提供Socket服务
 * 功能：
 *      指定 地址，端口 连接到Socket服务器
 *      发送任意文本
 */
public class SocketService {
    private Context mContext;
    private Socket mSocket = null;
    private PrintWriter pw = null;
    private OutputStream os = null;
    private InputStream is = null;

    // 连接与否标志
    private boolean Connected = false;

    private boolean serverflag = false;

    public final static String CONNECTED   = "connected";
    public final static String RECEIVEDATA = "receivedata";

    public SocketService(Context context) {
        mContext = context;
    }

    /**
     * 初始化Socket连接
     * @param address 服务器地址
     * @param port 服务器端口
     */
    public void StartSocket(final String address, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Connected = true;

                    // 初始化连接
                    mSocket = new Socket(address, port);

                    is = mSocket.getInputStream();
                    os = mSocket.getOutputStream();

                    // 得到输出流
                    pw = new PrintWriter(mSocket.getOutputStream(), true);

                    ServerThread.start();

                } catch(Exception e) {
                    Log.i("Tags", "StartSocket Fail!");
                    Connected = false;
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 向服务器发送文本
     * @param Msg 要发送的文本
     * @return 发送成功与否
     */
    public boolean SendMsg(String Msg) {
        if (Connected) {
            try {
                // 输出文本
                pw.println(Msg);

                // 发送完毕标志
                pw.flush();
                return true;
            } catch (Exception e) {
                Log.i("Tags", "SendMsg Fail!");
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean SendMsg(byte[] Msg) {
        if (Connected) {
            try {
                // 输出文本
                os.write(Msg);

                // 发送完毕标志
                os.flush();
                return true;
            } catch (Exception e) {
                Log.i("Tags", "SendMsg Fail!");
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 断开连接
     */
    public void StopSocket() {
        // 如果socket非空才执行
        if (mSocket != null) {
            try {
                serverflag = false;
                if (is != null) is.close();
                if (pw != null) pw.close();
                if (os != null) os.close();
                mSocket.close();
            } catch (Exception e) {
                Log.i("Tags", "StopSocket Fail!");
                e.printStackTrace();
            }
            Connected = false;
        }
    }


    Thread ServerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            serverflag = true;
            byte[] buffer = new byte[1024];
            int len;
            String s;
            while (serverflag) {
                try {
                    if ((len = is.read(buffer)) > 0){
                        s = new String(buffer).substring(0, len);
                        Intent intent = new Intent(SocketService.RECEIVEDATA);
                        intent.putExtra("data", s);
                        mContext.sendBroadcast(intent);
                        Log.i("tags", s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });
}
