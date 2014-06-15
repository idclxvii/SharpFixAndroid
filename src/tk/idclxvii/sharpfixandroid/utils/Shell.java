package tk.idclxvii.sharpfixandroid.utils;

/*
 * Copyright (c) 2011 Adam Outler
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;

import tk.idclxvii.sharpfixandroid.SharpFixApplicationClass;

/**
 *
 * @author adam
 */
//define <output and input> to this abstract class
public abstract class Shell {

    //for internal access
    //for external access
 
    public static String sendShellCommandTest(String[] cmd) {
        System.out.println("\n###executing: " + cmd[0] + "###");
        String AllText = "";
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
            }
            while ((line = STDERR.readLine()) != null) {
                AllText = AllText + "\n" + line;
            }
            while ((line = STDOUT.readLine()) != null) {
                AllText = AllText + "\n" + line;
                while ((line = STDERR.readLine()) != null) {
                    AllText = AllText + "\n" + line;
                }
            }
            //log.level0(cmd[0]+"\":"+AllText);
            return AllText;
        } catch (IOException ex) {
            System.out.println("Problem while executing in Shell.sendShellCommand() Received " + AllText);
            return "CritERROR!!!";
        }

    }
    
    
    public static String[] sendShellCommand(String[] cmd, Context c) {
        System.out.println("\n###executing: " + cmd[0] + "###");
        
        // we first assume that permission has been granted, if anything goes wrong, it becomes false immediately
        ((SharpFixApplicationClass)c.getApplicationContext()).setRootPermission(true);
        
        
        //String AllText = "";
        List<String> AllText = new ArrayList<String>();
        try {
            String line;
            Process process = new ProcessBuilder(cmd).start();
            BufferedReader STDOUT = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader STDERR = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(Shell.class.getName()).log(Level.SEVERE, null, ex);
                ((SharpFixApplicationClass)c.getApplicationContext()).setRootPermission(false);
            }
            while ((line = STDERR.readLine()) != null) {
                // AllText = AllText + "\n" + line;
            	((SharpFixApplicationClass)c.getApplicationContext()).setRootPermission(false);
            	
               
            }
            while ((line = STDOUT.readLine()) != null) {
               //  AllText = AllText + "\n" + line;
                AllText.add(line);
                /*
                while ((line = STDERR.readLine()) != null) {
                    AllText = AllText + "\n" + line;
                }
                */
            }
           
            //log.level0(cmd[0]+"\":"+AllText);
            return AllText.toArray(new String[AllText.size()]);
        } catch (IOException ex) {
            System.out.println("Problem while executing in Shell.sendShellCommand() Received " + AllText);
            ((SharpFixApplicationClass)c.getApplicationContext()).setRootPermission(false);
            return null;
        }

    }

    
}
