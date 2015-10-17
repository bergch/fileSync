package da_bubu.de.filesync.file;

import java.sql.Timestamp;


public interface Entry {

	public String getFullPath();
	
	public boolean isFile();
	
	public boolean isDirectory();
	
	public String getFileName();
	
	public Timestamp getLastModificationDate();
}
