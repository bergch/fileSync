package da_bubu.de.filesync.file;

import java.io.File;
import java.sql.Timestamp;

public class LocalFile extends LocalEntry {

	public LocalFile(File f) {
		super(f);
	}

    @Override
    public Timestamp getLastModificationDate() {
        return new java.sql.Timestamp(getFile().lastModified());
    }

}
