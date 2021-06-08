package com.example.piBuddyCompose.utils

import android.text.Html

object Constants {

    val helpText =  Html.fromHtml("<p><strong>Welcome to PI Buddy</strong></p>\n" +
            "<p><br></br></p>\n" +
            "<p>This is my first app and I hope you found it useful :)</p>\n" +
            "<p><br></br></p>\n" +
            "<p>Please email any queries or questions to <a href=\"mailto:dbtechprojects@gmail.com\">dbtechprojects@gmail.com</a></p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>App Info</strong></p>\n" +
            "<p><br></br></p>\n" +
            "<p>Linux commands used.</p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>CPU Usage</strong></p>\n" +
            "<p>&quot;cat &lt;(grep &apos;cpu &apos; /proc/stat) &lt;(sleep 1 &amp;&amp; grep &apos;cpu &apos; /proc/stat) | awk -v RS=\\&quot;\\&quot; &apos;{print (\\\$13-\\\$2+\\\$15-\\\$4)*100/(\\\$13-\\\$2+\\\$15-\\\$4+\\\$16-\\\$5)}&apos;&quot;</p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>Memory Usage</strong></p>\n" +
            "<p>&quot;awk &apos;/^Mem/ {printf(\\&quot;%u%%\\&quot;, 100*\\\$3/\\\$2);}&apos; &lt;(free -m)&quot;</p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>Root Disk Space Used</strong></p>\n" +
            "<p>&quot;df -hl | grep \\&apos;root\\&apos; | awk \\&apos;BEGIN{print \\&quot;\\&quot;} {percent+=\$5;} END{print percent}\\&apos; | column -t&quot;</p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>Logged In Users</strong></p>\n" +
            "<p>&quot;who | cut -d&apos; &apos; -f1 | sort | uniq\\n&quot;</p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>Power Off</strong></p>\n" +
            "<p>&quot;sudo shutdown -P now&quot;</p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>Restart</strong></p>\n" +
            "<p>&quot;sudo systemctl start reboot.target&quot;</p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>Custom Command</strong></p>\n" +
            "<p><br></br></p>\n" +
            "<p>Any custom command can be used but the output is limited to 1000 characters, only 1 custom command can be used.</p>\n" +
            "<p><br></br></p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>Connecting to your PI</strong></p>\n" +
            "<p><br></br></p>\n" +
            "<p>This app requires the IP address for your Raspberry Pi/ Linux device, and you will need to know your username and password. This app will connect on the default SSH Port 22. The scan mode can also be used to find available devices on your network with port 22 open.</p>\n" +
            "<p><br></br></p>\n" +
            "<p><strong>Saving Connections</strong></p>\n" +
            "<p><br></br></p>\n" +
            "<p>Successful connections are saved in the side draw on the opening page, <strong>the details are stored on your Device</strong> and will be deleted along with the App.</p>\n" +
            "<p><br><p>Ver 1.0.0 </p></br></p>\n" +
            "<p><br></br></p>")
}