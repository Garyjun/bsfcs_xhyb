package com.brainsoon.common.util.dofile.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamDrainer implements Runnable {
    private InputStream ins;

    public StreamDrainer(InputStream ins) {
        this.ins = ins;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(ins));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String[] cmd = new String[] { "cmd.exe", "/C", "d:\\1.bat D:\\11\\lib\\" };
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            
            new Thread(new StreamDrainer(process.getInputStream())).start();
            new Thread(new StreamDrainer(process.getErrorStream())).start();
            
            process.getOutputStream().close();

            int exitValue = process.waitFor();
            System.out.println("返回值：" + exitValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
