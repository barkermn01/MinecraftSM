package MinecraftSM.WebServer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class ServerRequest
{
	  public String requestPage = "";
	  public String requestPath = "";
	  public String hostname = "";
	  public Map<String, String> headers = new HashMap<>();
	  public String rawGet = "";
	  public Map<String, String> get = new HashMap<>();
	  public String rawPost = "";
	  public Map<String, String> post = new HashMap<>();
	  public Map<String, String> cookies = new HashMap<>();
	  public BufferedOutputStream out;
	  
	  public void parseInput(BufferedReader in){
		  	boolean gotFirst = false;
		  	String input = "";
		  	String request = "";
			
		  	try {
		  		input = in.readLine();
				while (!"".equals(input))
				{
					if (gotFirst)
					{
						request = request + input + "\n";
					}
					else
					{
						try{
						requestPage = input.split(" ")[1];
						gotFirst = true;
						}catch(Exception e){}
					}

			  		input = in.readLine();
				}
				String[] requestData = request.split("\n");
				String headersBlock = requestData[0];
				String[] headersArray = headersBlock.split("\n");
				
				headers = new HashMap<>();
				for (int i = 0; i < headersArray.length; i++)
				{
					String key = headersArray[i].split(":", 2)[0].trim();
					String value = headersArray[i].split(":", 2)[1].trim();
					headers.put(key, value);
				}
				
				String postData = "";
				if (headers.containsKey("Content-Length"))
				{
					int lenght = Integer.parseInt(headers.get("Content-Length"));
					int currentCount = 1;
					while (currentCount <= lenght)
					{
						postData = postData + (char)in.read();
						currentCount++;
					}
				}
				hostname = headers.get("Host");
				String[] QueryStringPart = requestPage.split("\\?");
				if (QueryStringPart.length > 1)
				{
					requestPage = QueryStringPart[0];
					rawGet = QueryStringPart[1];
					String get = rawGet;
					String[] parts = get.split("([\\?|\\&|\\;])");
					for (String part : parts)
					{
						String[] getParam = part.split("=");
						if (getParam.length > 1) {
							this.get.put(getParam[0], getParam[1]);
						} else if (getParam.length > 0) {
							this.get.put(getParam[0], null);
						}
					}
				}
				if (!"".equals(postData))
				{
					String[] parts = postData.split("([\\?|\\&|\\;])");
					for (String part : parts)
					{
						String[] getParam = part.split("=");
						if (getParam.length > 1) {
							post.put(getParam[0], getParam[1]);
						} else if (getParam.length > 0) {
							post.put(getParam[0], null);
						}
					}
				}
				if (headers.containsKey("Cookie"))
				{
					String[] parts = headers.get("Cookie").split("([\\?|\\&|\\;])");
					for (String part : parts)
					{
						String[] getParam = part.split("=");
						if (getParam.length > 1) {
							cookies.put(getParam[0], getParam[1]);
						} else if (getParam.length > 0) {
							cookies.put(getParam[0], null);
						}
					}
				}
				
				int p = requestPage.lastIndexOf("/");
				requestPath = requestPage.substring(0, p)+"/";
				requestPage = requestPage.substring(p+1);
				if("".equals(requestPage)){
					requestPage = "index.html";
				}
				rawPost = postData;
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
}
