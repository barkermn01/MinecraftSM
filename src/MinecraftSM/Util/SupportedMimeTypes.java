package MinecraftSM.Util;

import java.io.Serializable;
import java.util.HashMap;

import MinecraftSM.Util.Exceptions.MimeInvalid;

public class SupportedMimeTypes implements Serializable {
	private static SupportedMimeTypes _inst;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// holds the supported mimeTypes;
	private HashMap<String, String> mimeTypes;
	
	// holds the default mime type
	private String defaultMime = "text/plain";
	
	private SupportedMimeTypes()
	{
		// check if this is first load if so fill it with standard data
		if(mimeTypes == null){
			// build the supported mimeTypes
			mimeTypes = new HashMap<>();
			mimeTypes.put(".css", 	"text/css");
			mimeTypes.put(".js", 	"text/javascript");
			mimeTypes.put(".json", 	"application/json");
			mimeTypes.put(".html", 	"text/html");
			mimeTypes.put(".gif", 	"image/gif");
			mimeTypes.put(".jpeg", 	"image/jpeg");
			mimeTypes.put(".jpg", 	"image/jpeg");
			mimeTypes.put(".png", 	"image/png");
			mimeTypes.put(".tiff", 	"image/tiff");
			mimeTypes.put(".tif", 	"image/tiff");
			mimeTypes.put(".svg", 	"image/svg+xml");
			mimeTypes.put(".ico", 	"image/icon");
		}
	}
	
	public void addMimeType(String ext, String mime) throws MimeInvalid{
		if(ext.charAt(0) != '.'){
			throw new MimeInvalid(ext);
		}
		mimeTypes.put(ext, mime);
	}
	
	public String getMimeType(String ext)
	{
		if(mimeTypes.containsKey(ext)){
			return mimeTypes.get(ext);
		}
		return defaultMime;
	}

	public static SupportedMimeTypes GetInstance() {
		if(_inst == null){
			_inst = new SupportedMimeTypes();
		}
		return _inst;
	}
}
