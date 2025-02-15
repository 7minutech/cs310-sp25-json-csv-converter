package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Hashtable;
import org.apache.commons.lang3.text.StrBuilder;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            StringReader reader = new StringReader(csvString);
            CSVReader csvReader = new CSVReader(reader);
            String[] nextRow;
            JsonObject jsonObject = new JsonObject();
            JsonArray prodNums = new JsonArray();
            JsonArray colHeaders = new JsonArray();
            JsonArray episodeData = new JsonArray();
            JsonArray episode = new JsonArray();
            
            boolean header = true;
            int rowCount = 0;
            //read through each row
            while((nextRow = csvReader.readNext()) != null){
                //read each col in row
                for(int col = 0; col < nextRow.length; col++) {
                    if(header){
                        //add header col to obj
                        colHeaders.add(nextRow[col]);  
                    }
                    else if (col > 0){
                        //if b/w cols 2 and 3 
                        //must parse int for json string
                        if (col == 2 || col == 3){
                            int value = Integer.parseInt(nextRow[col]);
                            episode.add(value);
                            continue;
                        }
                        //add col info to episode obj
                        episode.add(nextRow[col]);
                    }
                }
                if(!header){
                    //add number to prodNums
                    prodNums.add(nextRow[0]);
                    episodeData.add(episode);
                    // need to clear episode
                    episode = new JsonArray();  
                }
                rowCount++;
                header = (rowCount == 0);             
            }
            jsonObject.put("ProdNums", prodNums);
            jsonObject.put("ColHeadings", colHeaders);
            jsonObject.put("Data", episodeData);
            result = Jsoner.serialize(jsonObject);
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        final int EPISODE_DATA_LENGTH = 6;
        final int EPISODE_RECORD_LENGTH = 7;
        final int FIRST_COL = 0;
          
        try {
            
            // INSERT YOUR CODE HERE
            StringReader reader = new StringReader(jsonString);
            //deserialize json to obj
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(reader);
            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray data = (JsonArray) jsonObject.get("Data");
            StringWriter stringWriter = new StringWriter();
            CSVWriter writer = new CSVWriter(stringWriter);
            String[] headerRow = new String[colHeadings.size()];

            for (int i = 0; i < colHeadings.size(); i++) {
                //get header string arr
                headerRow[i] = (colHeadings.get(i)).toString();
            }
            writer.writeNext(headerRow);
            for (int i = 0; i < data.size(); i++) {
                //clear episode record datta
                String[] episodeRecord = new String[EPISODE_RECORD_LENGTH];
                //prod num is first col in episode data
                episodeRecord[FIRST_COL] = (prodNums.get(i)).toString();
                JsonArray episdoeData = (JsonArray) data.get(i);
                //loop through episode data
                for (int j = 0; j < EPISODE_DATA_LENGTH; j++) {
                    String dataField = (episdoeData.get(j)).toString();  
                    if(j == 2 && dataField.length() == 1){
                        //must prepend with 0 for nums if length 1
                        episodeRecord[j+1] = Converter.prependString(dataField, "0");
                    }
                    else{
                      //first col must be excluded
                      episodeRecord[j+1] = dataField;  
                    }
                    
                }
                writer.writeNext(episodeRecord);   
            }
            result = stringWriter.toString();   
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    public static String prependString(String str, String desired){
        StringBuilder episodeNumber = new StringBuilder(str);
        episodeNumber.insert(0, desired);
        return episodeNumber.toString();
    }
    
}
