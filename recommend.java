package museDB;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class recommend {
  
  private static String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
        URL url = new URL(urlString);
        reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer buffer = new StringBuffer();
        int read;
        char[] chars = new char[1024];
        while ((read = reader.read(chars)) != -1)
            buffer.append(chars, 0, read); 

        return buffer.toString();
    } finally {
        if (reader != null)
            reader.close();
    }
}
 
  public ArrayList<String> getRecsGenre(String genre) throws Exception{
    String json = readUrl("http://api.musicgraph.com/api/v2/artist/search?api_key=1952901676a1f77d5376a71b98308ca4&genre="+genre);
    Gson gson = new Gson();
    MusicGraphSearchResult result = gson.fromJson(json, MusicGraphSearchResult.class);
    ArrayList<String> recomendations = new ArrayList<String>();
    for(SearchEntry entry : result.data) {
        recomendations.add(entry.sort_name);
    }
    
    return recomendations;
  }
  
  private static ArrayList getRecsArtist(String artist) throws Exception{
    String json = readUrl("http://api.musicgraph.com/api/v2/artist/search?api_key=1952901676a1f77d5376a71b98308ca4&similar_to="+artist);
    Gson gson = new Gson();
    MusicGraphSearchResult result = gson.fromJson(json, MusicGraphSearchResult.class);
    ArrayList recomendations =new ArrayList();
    for(SearchEntry entry : result.data) {
        recomendations.add(entry.sort_name);
    }
    
    return recomendations;
  }
  
  public static class MusicGraphSearchResult {
    SearchEntry[] data;
  }

  public static class SearchEntry {
    String sort_name;
  }
  
  /** 
 public static void main(String[] args) throws Exception  {
    ArrayList recs = getRecsGenre("Rock");
    Iterator itr=recs.iterator();  
    while(itr.hasNext()){  
     System.out.println(itr.next());  
    }  
    }
**/

}
