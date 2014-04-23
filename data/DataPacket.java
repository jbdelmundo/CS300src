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
