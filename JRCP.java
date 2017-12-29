import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;

/**
 * Created by sdk on 4/3/2017.
 */

public class XboxConsole {
    BufferedReader fromConsole;
    DataOutputStream toConsole;
    Socket xConsole;
    public XboxConsole(String ip, int port) throws IOException {
        try {
            xConsole = new Socket(ip, port);
            toConsole = new DataOutputStream(xConsole.getOutputStream());
            fromConsole = new BufferedReader(new InputStreamReader(xConsole.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public XboxConsole(String ip)  {
        try {
            xConsole = new Socket(ip, 730);
            toConsole = new DataOutputStream(xConsole.getOutputStream());
            fromConsole = new BufferedReader(new InputStreamReader(xConsole.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void SendCommand(String command) {
        try {
            toConsole.writeBytes(command + "\r\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set some candy down for the game to eat
     * @param address Address you wanna send the candy to.
     * @param data Sweet sweet candy
     */
    public void SetMemory(String address, String data) {
        this.SendCommand(String.format("setmem addr=0x%s data=%s", address, data));
    }

    /**
     * A function to show a sexy notification
     * @param message Message of the notification, if using a type other than PLAIN should be a name to make sense.
     * @param Type XType of the notification
     */
    public void XNotify(String message, XType Type) {
        int String = 2;
        int Int = 1;
        String command = "consolefeatures ver=2" +
                " type=12 params=\"A\\0\\A\\2\\" +
                String +
                "/" +
                message.length() +
                "\\" +
                toHex(message) +
                "\\" +
                Int+
                "\\";
        switch (Type) {
            case PLAIN:
                command += "0\\\"";
                break;
            case INVITE:
                command += "1\\\"";
                break;
            case FRIEND:
                command += "2\\\"";
                break;
            default:
                command += "0\\\"";
                break;
        }
        this.SendCommand(command);
    }

    /**
     * A function to get some of that good good candy, please exclude 0x from the offests and bytes!
     * @param address Address where the candy is
     * @param rlength Length of data to get.
     * @return a string containing sweet candy
     */
    public String GetMemory(String address, String rlength) {
        try {
            this.SendCommand(String.format("getmemex addr=0x%s length=%s", address, rlength));
            while(true) {
                String x = this.fromConsole.readLine();
                if(x.endsWith("203- binary response follows")) {
                    int lng = Integer.parseInt(rlength) + 2;
                    char[] cbuff = new char[lng];
                    this.fromConsole.read(cbuff, 0, lng);
                    String text = "";
                    for(int i = 2, n = lng; i < n; i++) {
                       text += cbuff[i];
                    }
                    return text;
                }
            }
            //return  this.fromConsole.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
            return "error";
        }
    }

    public enum XType {
        PLAIN,
        INVITE,
        FRIEND
    }

    public String toHex(String arg) {
        String text = "";
        for (int i = 0; i < arg.length(); i++)
        {
            text += Integer.toHexString(arg.charAt(i));
        }
        try {
            return text;
            //return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
        } catch (Exception ex) {
            return "";
        }
    }
}
