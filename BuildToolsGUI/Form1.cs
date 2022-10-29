using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace BuildToolsGUI
{
    public partial class Form1 : Form
    {
        public static bool BuildToolsDetected = true;
        public Form1()
        {
            string strExeFilePath = System.Reflection.Assembly.GetExecutingAssembly().Location;
            string strWorkPath = Path.GetDirectoryName(strExeFilePath);
            if(!File.Exists(strWorkPath+"\\BuildTools.jar")) { BuildToolsDetected = false; }
            InitializeComponent();
            if(!BuildToolsDetected)
            {
                button1.Enabled = false;
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {

        }

        private void button2_Click(object sender, EventArgs e)
        {
            string buildToolsPath = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location) + "\\BuildTools.jar";
            if(File.Exists(buildToolsPath))
            {
                File.Delete(buildToolsPath);
            }
            try
            {
                using (var client = new WebClient())
                {
                    client.Headers.Add("User-Agent: Other");
                    client.DownloadFile(@"https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar", buildToolsPath);
                }
            } catch(Exception ex)
            {
                MessageBox.Show("Error occurred on download:\n" + ex.Message);
            }
            button2.Text = "Complete!";
            button2.ForeColor = Color.Green;
            BuildToolsDetected = true;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            string path = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
            ProcessStartInfo processStartInfo = new ProcessStartInfo();
            processStartInfo.WorkingDirectory = path;
            processStartInfo.FileName = "cmd.exe";
            processStartInfo.Arguments = "/C java -jar BuildTools.jar --rev " + richTextBox1.Text;
            processStartInfo.RedirectStandardOutput = true;
            processStartInfo.RedirectStandardError = true;
            processStartInfo.UseShellExecute = false;
            processStartInfo.CreateNoWindow = true;
            Process p = new Process();
            p.StartInfo = processStartInfo;
            button1.Text = "BUILDING...";
            button1.ForeColor = Color.Black;
            this.Update();
            button1.Update();
            p.Start();
            string output = p.StandardOutput.ReadToEnd();
            string errorOutput = p.StandardError.ReadToEnd();
            p.WaitForExit();
            if(errorOutput.Contains("Could not get version"))
            {
                MessageBox.Show("Invalid Version Selected!\nValid Version Example: 1.19.2");
                button1.Text = "FAILED";
                button1.ForeColor = Color.Red;
                return;
            }
            button1.Text = "BUILT!";
            if(checkBox1.Checked)
            {
                Process.Start(path);
                Environment.Exit(0);
            }
            button1.ForeColor = Color.Green;
        }
    }
}
