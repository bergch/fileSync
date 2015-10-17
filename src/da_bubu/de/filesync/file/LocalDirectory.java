package da_bubu.de.filesync.file;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LocalDirectory extends LocalEntry {

	public LocalDirectory(String dir) {
		super(dir);
	}
	
	public LocalDirectory(File file) {
		super(file);
	}

	ArrayList<LocalEntry> content = new ArrayList<>();

	public void add(LocalEntry localDirectory) {
		content.add(localDirectory);
		
	}

	public void add(List<LocalEntry> dirContent) {
		if(dirContent!=null) {
			content.addAll(dirContent);
		}
	}

	public ArrayList<LocalEntry> getContent() {
		return content;
	}

	public List<LocalDirectory> getSubDirs() {
		List<LocalDirectory> res = new ArrayList<LocalDirectory>();
		for (LocalEntry localEntry : content) {
			if(localEntry.isDirectory()) {
				res.add((LocalDirectory)localEntry);
			}
		}
		return res;
	}

    @Override
    public Timestamp getLastModificationDate() {
        return null;
    }

}
