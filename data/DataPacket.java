package data;

import java.util.Arrays;

public class DataPacket extends OPTICSPoint implements ClusterPoint {

	public char SymbolicAttr[];
	public int ContinuousAttr[];
	public int DataPacketID;
	public int label = -1;
	public boolean hasLabel = true; // set false to make the label as
									// null/undefined
	
	public DataPacket(int symbolicSize, int continuousSize) {
		this.SymbolicAttr = new char[symbolicSize];
		this.ContinuousAttr = new int[continuousSize];
	}
	
	public DataPacket(DataPacket source){
		this.SymbolicAttr = Arrays.copyOf(source.SymbolicAttr, source.SymbolicAttr.length);
		this.ContinuousAttr = Arrays.copyOf(source.ContinuousAttr, source.ContinuousAttr.length);
		this.DataPacketID = source.DataPacketID;
		this.label = source.label;
		this.hasLabel = source.hasLabel;		
	}

	public void printDataPacketInfo() {
		System.out.println("ID: " + DataPacketID + "\tLabel:" + label);
		System.out.println("Symbolic Attributes:"
				+ Arrays.toString(SymbolicAttr));
		System.out.println("Continuous Attributes:"
				+ Arrays.toString(ContinuousAttr));

		System.out.println("");
	}

//	public void printDataPacketInfo(boolean complete) {
//		System.out.println("ID: " + DataPacketID + "\tLabel:" + label);
//		System.out.println("Symbolic Attributes:");
//		for (int i = 0; i < SymbolicAttr.length; i++) {
//			System.out.println(ConfigAttributes.IncludedSymbolicNames[i] + ": "
//					+ SymbolicAttr[i]);
//		}
//		System.out.println("Continuous Attributes:");
//		for (int i = 0; i < ContinuousAttr.length; i++) {
//			System.out.println(ConfigAttributes.IncludedContinuousNames[i]
//					+ ": " + ContinuousAttr[i]);
//		}
//		System.out.println("");
//	}
	
	public static int getLabelCategory(int label){
		//based on Relative Entropy based Clustering
		switch (label) {
		case NORMAL:
			return NORMAL;
			
		case BACK:
		case LAND:
		case NEPTUNE:
		case POD:
		case SMURF:
		case TEARDROP:
			return DOS;
			
		case BUFFER_OVERFLOW:
		case LOADMODULE:
		case PERL:
		case ROOTKIT:
			return U2R;
			
		case FTP_WRITE:
		case GUESS_PASSWD:
		case IMAP:
		case MULTIHOP:
		case PHF:
		case SPY:
		case WAREZCLIENT:
		case WAREZMASTER:
			return R2L;
			
		case IPSWEEP:
		case NMAP:
		case PORTSWEEP:
		case SATAN:
			return PROBE;
				

		default:
			System.err.println("Returning -1 category:"+label);
			return -1;
		}
	}
	

	public static final int DOS = 1;
	public static final int U2R = 2;
	public static final int R2L = 3;
	public static final int PROBE = 4;
	
	
	public static final int NORMAL = 0;
	public static final int BACK = 1;
	public static final int BUFFER_OVERFLOW = 2;
	public static final int FTP_WRITE = 3;
	public static final int GUESS_PASSWD = 4;
	public static final int IMAP = 5;
	public static final int IPSWEEP = 6;
	public static final int LAND = 7;
	public static final int LOADMODULE = 8;
	public static final int MULTIHOP = 9;
	public static final int NEPTUNE = 10;
	public static final int NMAP = 11;
	public static final int PERL = 12;
	public static final int PHF = 13;
	public static final int POD = 14;
	public static final int PORTSWEEP = 15;
	public static final int ROOTKIT = 16;
	public static final int SATAN = 17;
	public static final int SMURF = 18;
	public static final int SPY = 19;
	public static final int TEARDROP = 20;
	public static final int WAREZCLIENT = 21;
	public static final int WAREZMASTER = 22;

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
