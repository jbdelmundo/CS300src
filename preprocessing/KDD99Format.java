package preprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Changes the input file KDD99 to Experiment Specific format. (ids file)
 * ids data format
 * 
 * 7 integers representing symbolic, followed by continuous
 * 
 * */

public class KDD99Format {

	File datafile;
	File namefile;
	boolean debug = true;
	
	

	
	

	public static void main(String[] args) throws FileNotFoundException {

		int[] SYMBOLIC_ATTRIBUTES_INDICES = { 1, 2, 3, 6, 11, 20, 21 };
		int[] CONTINUOUS_ATTRIBUTES_INDICES = { 0, 4, 5, 7, 8, 9, 10, 12, 13,
				14, 15, 16, 17, 18, 19, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
				32, 33, 34, 35, 36, 37, 38, 39, 40 };
		
		boolean names = true;
		if(names){
			PrintWriter pwnames = new PrintWriter(new File("data"+ File.separatorChar + "ids.names"));
		
			for (int i = 0; i < SYMBOLIC_ATTRIBUTES_NAMES.length; i++) {
				pwnames.println(SYMBOLIC_ATTRIBUTES_NAMES[i]);
			}
			
			for (int i = 0; i < CONTINUOUS_ATTRIBUTES_NAMES.length; i++) {
				pwnames.println(CONTINUOUS_ATTRIBUTES_NAMES[i]);
			}
			pwnames.close();
			
			return;
		}
		Scanner scan = new Scanner(new File("data" + File.separatorChar
				+ "kddcup.data"));
		PrintWriter pwdata = new PrintWriter(new File("data"
				+ File.separatorChar + "ids.data"));
		PrintWriter pwnames = new PrintWriter(new File("data"
				+ File.separatorChar + "ids.names"));
		int count = 0;
		while (scan.hasNext()) {
			String inLine[] = scan.nextLine().split(",");

			int symbolic[] = new int[SYMBOLIC_ATTRIBUTES_INDICES.length];
			int continuous[] = new int[CONTINUOUS_ATTRIBUTES_INDICES.length];
			int s = 0, c = 0;

			// read attributes
			for (int i = 0; i < inLine.length - 1; i++) {
				char type = TEXTDATAFIELDTYPES[i];

				switch (type) {
				case 'C': // character
					switch (i) {
					case 1:
						symbolic[s] = findIndexProtocol(inLine[i]);
						break;

					case 2:
						symbolic[s] = findIndexServices(inLine[i]);
						break;

					case 3:
						symbolic[s] = findIndexFlags(inLine[i]);
						break;

					default:
						symbolic[s] = Integer.parseInt(inLine[i]);
					}
					s++;
					break;

				case 'I': // integer
					continuous[c] = Integer.parseInt(inLine[i]);
					c++;
					break;

				case 'D':
					double d = Double.parseDouble(inLine[i]);
					int integer = (int) (d * 100);
					continuous[c] = integer;
					c++;
					break;

				default:
					break;
				}

			}
			int label = findIndexLabel(inLine[inLine.length-1]);
			count++;
			if(count % 10000 == 0) System.out.println("Count "+ count);
			
			
			//write packet
			for (int i = 0; i < symbolic.length; i++) {
				pwdata.print( symbolic[i] + ",");
			}
			
			for (int i = 0; i < continuous.length; i++) {
				pwdata.print( continuous[i] + ",");
			}
			pwdata.println(label);	
			
			if(label == -1){
				System.err.println("NOOO");
				return ;
			}
		}
		pwdata.close();
		
		for (int i = 0; i < SYMBOLIC_ATTRIBUTES_NAMES.length; i++) {
			pwnames.println("S:"+SYMBOLIC_ATTRIBUTES_NAMES[i]);
		}
		for (int i = 0; i < CONTINUOUS_ATTRIBUTES_NAMES.length; i++) {
			pwnames.println("C:"+CONTINUOUS_ATTRIBUTES_NAMES[i]);
		}
		
		pwnames.close();
		pwdata.close();
		scan.close();
		System.out.println("Done");
	}

	public static int findIndex(String key, String arr[]) {
		for (int i = 0; i < arr.length; i++) {			
			if (arr[i].equals(key)){
				return i;
			}
		}
		return -1;
	}

	public static int findIndexLabel(String key) {
		return findIndex(key.substring(0, key.length()-1), LABELS);
	}
	
	public static int findIndexProtocol(String key) {
		return findIndex(key, PROTOCOLS);
	}

	public static int findIndexServices(String key) {
		return findIndex(key, SERVICES);
	}

	public static int findIndexFlags(String key) {
		return findIndex(key, FLAGS);
	}

	public final static char TEXTDATAFIELDTYPES[] = { 'I', 'C', 'C', 'C', 'I',
			'I', 'C', 'I', 'I', 'I', 'I', 'C', 'I', 'I', 'I', 'I', 'I', 'I',
			'I', 'I', 'C', 'C', 'I', 'I', 'D', 'D', 'D', 'D', 'D', 'D', 'D',
			'I', 'I', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D' }; // excluding
																// label

	public final static String[] SYMBOLIC_ATTRIBUTES_NAMES = { "protocol_type",
			"service", "flag", "land", "logged_in", "is_host_login",
			"is_guest_login" };

	public final static String[] CONTINUOUS_ATTRIBUTES_NAMES = { "duration",
			"src_bytes", "dst_bytes", "wrong_fragment", "urgent", "hot",
			"num_failed_logins", "num_compromised", "root_shell",
			"su_attempted", "num_root", "num_file_creations", "num_shells",
			"num_access_files", "num_outbound_cmds", "count", "srv_count",
			"serror_rate", "srv_serror_rate", "rerror_rate", "srv_rerror_rate",
			"same_srv_rate", "diff_srv_rate", "srv_diff_host_rate",
			"dst_host_count", "dst_host_srv_count", "dst_host_same_srv_rate",
			"dst_host_diff_srv_rate", "dst_host_same_src_port_rate",
			"dst_host_srv_diff_host_rate", "dst_host_serror_rate",
			"dst_host_srv_serror_rate", "dst_host_rerror_rate",
			"dst_host_srv_rerror_rate" };

	public final static String PROTOCOLS[] = { "icmp", "udp", "tcp" };

	public final static String SERVICES[] = { "vmnet", "smtp", "ntp_u",
			"shell", "kshell", "imap4", "urh_i", "netbios_ssn", "tftp_u",
			"mtp", "uucp", "nnsp", "echo", "tim_i", "ssh", "iso_tsap", "time",
			"netbios_ns", "systat", "login", "hostnames", "efs", "supdup",
			"courier", "ctf", "finger", "nntp", "ftp_data", "red_i", "ldap",
			"http", "pm_dump", "ftp", "exec", "klogin", "netbios_dgm", "auth",
			"other", "link", "X11", "discard", "remote_job", "private", "IRC",
			"pop_3", "daytime", "gopher", "pop_2", "sunrpc", "name", "rje",
			"domain", "uucp_path", "Z39_50", "domain_u", "csnet_ns", "whois",
			"eco_i", "bgp", "sql_net", "telnet", "ecr_i", "printer", "urp_i",
			"netstat", "http_443", "harvest", "aol", "http_8001", "http_2784",
			"icmp" };

	public final static String FLAGS[] = { "RSTR", "S3", "SF", "RSTO", "SH",
			"OTH", "S2", "RSTOS0", "S1", "S0", "REJ" };

	public final static String LABELS[] = { "normal", "back",
			"buffer_overflow", "ftp_write", "guess_passwd", "imap", "ipsweep",
			"land", "loadmodule", "multihop", "neptune", "nmap", "perl", "phf",
			"pod", "portsweep", "rootkit", "satan", "smurf", "spy", "teardrop",
			"warezclient", "warezmaster" };

}
