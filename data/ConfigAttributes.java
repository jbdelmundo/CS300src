package data;

import java.util.Arrays;
import java.util.Iterator;



public class ConfigAttributes {

	public static boolean isBasicClassification = false;	//true if summarized labels 
	public final static int BasicClassificationCount = 5;
	public static boolean willClassify = false;
	
	public static int frequencyAccuracyMultiplier = 1;//1000000;
	public static int rangeAccuracyMultiplier = 1000000000;//1000;
	
	public static int MinPts = 5;
	public static boolean useDB = false;

	public static boolean INCLUDEALL = true;
//	public static boolean ISINCLUDED[] = Config.ISINCLUDED;
	

	public final static char TEXTDATAFIELDTYPES[] = { 'I', 'C', 'C', 'C', 'I',
			'I', 'C', 'I', 'I', 'I', 'I', 'C', 'I', 'I', 'I', 'I', 'I', 'I',
			'I', 'I', 'C', 'C', 'I', 'I', 'D', 'D', 'D', 'D', 'D', 'D', 'D',
			'I', 'I', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D' }; // excluding
																// label
	public final static String[] DATAFIELDNAMESTEXT = { "duration", "protocol_type",
		"service", "flag", "src_bytes", "dst_bytes", "land",
		"wrong_fragment", "urgent", "hot", "num_failed_logins",
		"logged_in", "num_compromised", "root_shell", "su_attempted",
		"num_root", "num_file_creations", "num_shells", "num_access_files",
		"num_outbound_cmds", "is_host_login", "is_guest_login", "count",
		"srv_count", "serror_rate", "srv_serror_rate", "rerror_rate",
		"srv_rerror_rate", "same_srv_rate", "diff_srv_rate",
		"srv_diff_host_rate", "dst_host_count", "dst_host_srv_count",
		"dst_host_same_srv_rate", "dst_host_diff_srv_rate",
		"dst_host_same_src_port_rate", "dst_host_srv_diff_host_rate",
		"dst_host_serror_rate", "dst_host_srv_serror_rate",
		"dst_host_rerror_rate", "dst_host_srv_rerror_rate" };
	
	public final static String[] SYMBOLIC_ATTRIBUTES_NAMES = { "protocol_type",
			"service", "flag", "land", "logged_in", "is_host_login",
			"is_guest_login" };
	final static int[] SYMBOLIC_ATTRIBUTES_INDICES = { 1, 2, 3, 6, 11, 20, 21 };

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
	
	final static int[] CONTINUOUS_ATTRIBUTES_INDICES = { 0, 4, 5, 7, 8, 9, 10,
			12, 13, 14, 15, 16, 17, 18, 19, 22, 23, 24, 25, 26, 27, 28, 29, 30,
			31, 32, 33, 34, 35, 36, 37, 38, 39, 40 };

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

	public final static String BASICLABELS[] = { "normal", "DOS", "U2R", "R2l",	"SCAN" };
	
	
//	public static int IncludedContinuousIndices[] = setIncludedAttributes();
//	public static int IncludedSymbolicIndices[];	
//	public static String IncludedContinuousNames[];
//	public static String IncludedSymbolicNames[];
//	public static double IncludedContinuousWeights[];
//	public static double IncludedSymbolicWeights[];
	

//	private static int[] setIncludedAttributes(){
//		
//		int symbolic_count = 0, continuous_count = 0;
//		//count included first
//		for(int i = 0 ; i< TEXTDATAFIELDTYPES.length; i++){			
//			if(ISINCLUDED[i] || INCLUDEALL)
//				if(TEXTDATAFIELDTYPES[i] == 'C'){
//					symbolic_count++;
//				}else{
//					continuous_count++;				
//				}					
//		}
//		
//		IncludedContinuousIndices = new int[continuous_count];
//		IncludedContinuousNames = new String[continuous_count];
//		IncludedSymbolicIndices = new int[symbolic_count];
//		IncludedSymbolicNames = new String[symbolic_count];
//		IncludedContinuousWeights = new double[continuous_count];
//		IncludedSymbolicWeights = new double[symbolic_count];
//		
//		int symbolic_ctr = 0, continuous_ctr = 0;
//		
//		
//		for(int i = 0 ; i< TEXTDATAFIELDTYPES.length; i++){	
//			if(ISINCLUDED[i] || INCLUDEALL)
//				if(TEXTDATAFIELDTYPES[i] == 'C'){
//					IncludedSymbolicIndices[symbolic_ctr] = i;
//					IncludedSymbolicNames[symbolic_ctr] = DATAFIELDNAMESTEXT[i];
//					IncludedSymbolicWeights[symbolic_ctr] = WEGHTS[i];				
//					symbolic_ctr++;
//				}else{
//					IncludedContinuousIndices[continuous_ctr] = i;
//					IncludedContinuousNames[continuous_ctr]= DATAFIELDNAMESTEXT[i];
//					IncludedContinuousWeights[continuous_ctr] = WEGHTS[i];
//					continuous_ctr++;				
//				}					
//		}
//		
//		return IncludedContinuousIndices;
//	}
//			
//	
//
//	public static void printAttributeNames() {
//		System.out.println("Continuous Attr:");
//		System.out.println(Arrays.toString(IncludedContinuousNames));
//		
//		System.out.println("Symbollic Attr:");
//		System.out.println(Arrays.toString(IncludedSymbolicNames));
//	}
}
