package FilleSystem;

import java.util.HashSet;
import java.util.List;
import java.io.Serializable;
public class FileList implements Serializable {
	private String _filename;
	private HashSet<String> storeAddress; // = new ArrayList<String>();
	private int counter =0 ;
	


	public FileList(String fileName, HashSet<String> storeAddress,int count)
	{
		this._filename = fileName;
		this.storeAddress = storeAddress;
		this.counter =this.counter + count;
	}


	public String get_filename() {
		return _filename;
	}
	public void set_filename(String _filename) {
		this._filename = _filename;
	}
	public HashSet<String> getStoreAddress() {
		return storeAddress;
	}
	public void setStoreAddress(HashSet<String> storeAddress) {
		this.storeAddress = storeAddress;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_filename == null) ? 0 : _filename.hashCode());
		result = prime * result
				+ ((storeAddress == null) ? 0 : storeAddress.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileList other = (FileList) obj;
		if (_filename == null) {
			if (other._filename != null)
				return false;
		} else if (!_filename.equals(other._filename))
			return false;
		if (storeAddress == null) {
			if (other.storeAddress != null)
				return false;
		} else if (!storeAddress.equals(other.storeAddress))
			return false;
		return true;
	}
	
	
}
