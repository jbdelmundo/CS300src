package config;

public class Constants {

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
	
	public final static String LABELS[] = { "normal", "back",
		"buffer_overflow", "ftp_write", "guess_passwd", "imap", "ipsweep",
		"land", "loadmodule", "multihop", "neptune", "nmap", "perl", "phf",
		"pod", "portsweep", "rootkit", "satan", "smurf", "spy", "teardrop",
		"warezclient", "warezmaster" };
}
