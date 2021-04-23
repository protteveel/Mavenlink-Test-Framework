import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class GenererateCode {

	// Text file extension
	private static final String TXT_FILE_EXTENSION = ".txt";
	
	// Index file name
	private static final String INDEX_FILE_NAME = "index.html";
	
	// Index file encoding
	private static final String INDEX_FILE_ENCODING = "UTF-8";
	
	// Function title to fetch a list
	private static final String FETCH_LIST_TEXT = "Fetching a list";
	
	// Function title to fetch a record
	private static final String FETCH_RECORD_TEXT = "Fetching a single";
	
	// Path to the section text files
	private String sourcePath;

	// Path to the destination directory
	private String destinationPath;
	
	// The file separator
	private String fileSeparator;
	
	// The index file
	PrintWriter indexFile;

	// Default constructor
	GenererateCode( String srcDir, String dstDir) {
		// Copy the path to the section text files
		sourcePath = srcDir;
		// Copy the path to the destination directory
		destinationPath = dstDir;
	}
	
	// Initialize the object
	private boolean init() {
		// Set the default return value
		boolean retVal = true;
		// Get the file separator
		fileSeparator = System.getProperty("file.separator");
		// Does the source path ends with a file separator
		if(sourcePath.endsWith(fileSeparator)) {
			// Remove the file separator
			sourcePath = sourcePath.substring(0, sourcePath.length() - 1);
		}
		// Does the destination path ends with a file separator
		if(destinationPath.endsWith(fileSeparator)) {
			// Remove the file separator
			destinationPath = destinationPath.substring(0, destinationPath.length() - 1);
		}
		// Compose the index file path
		String indexFilePath = destinationPath + fileSeparator + INDEX_FILE_NAME;
		// Try to create/overwrite the index file
		try {
			// Create the index file
			indexFile = new PrintWriter(indexFilePath, INDEX_FILE_ENCODING);
			// Write the beginning of the index file
			indexFile.println("<html>");
			indexFile.println("	<head>");
			indexFile.println("		<title>Mavenlink-Integration</title>");
			indexFile.println("		<link rel=\"stylesheet\" href=\"/styles/stylesheet.css\" type=\"text/css\">");
			indexFile.println("	</head>");
			indexFile.println("	<body>");
		} catch (UnsupportedEncodingException exception ) {
			// Return the error
			retVal = false;
			// Inform the user of the error
			informUser("ERROR: Text encoding (" + INDEX_FILE_ENCODING + ") for " + INDEX_FILE_NAME + " is not supported");
		} catch (FileNotFoundException exception ) {
			// Return the error
			retVal = false;
			// Inform the user of the error
			informUser("ERROR: Could not create " + indexFilePath);
		}
		// Return the result
		return retVal;
	}
	
	// The list of function types
	private enum functionTypes {
		GET,
		POST,
		PUT,
		DELETE,
		unknown
	};
	
	//Convert the function type string to an enumerate
	private functionTypes functionTypeToEnum(String functionTypeStr ) {
		// Set the default return value
		functionTypes retVal = functionTypes.unknown;
		// Try to convert the function type to an enumerate
		try {
			// Convert the string to an enumerate
			retVal = functionTypes.valueOf(functionTypeStr);
		}
		catch (java.lang.IllegalArgumentException e ) {
			// Could not convert the function type string to an enumerate
			retVal = functionTypes.unknown;
		}
		// Return the result
		return retVal;
	}
	
	// Create a directory
	private void createDirectory(String dirName ) {
		// Create a new file object
	    File directory = new File(dirName);
	    // If the directory does not exist
	    if(!directory.exists()) {
		    // Create the directory
		    if(directory.mkdir()) {
		    	// Inform the user
		       	informUser("Created directory: " + dirName);
		    } else {
		    	// Inform the user of the error
		       	informUser("ERROR: Could not create directory " + dirName);
		    }
	    } else {
	    	// Inform the user
	       	informUser("Directory exists: " + dirName);
	    }
	}
	
	// Converts a name to a directory name
	private String getDirName(String name) {
		// Does it have " (" in the name?
		if(name.contains(" (")) {
			// Remove everything after " ("
			name = name.substring(0, name.indexOf(" ("));
		}
		// Replace all " & " with "_"
		name = name.replaceAll(" & ", "_");
		// Replace all " " with "_"
		name = name.replaceAll(" ", "_");
		// Return the results
		return name;
	}
	
	// Converts a name to a file name
	private String getFileName(String name) {
		// Replace all " " with "_"
		name = name.replaceAll(" ", "_");
		// Return the results
		return name;
	}
	
	// Create a generic file
	private void createGenericFile(String path, String function, String url) {
		try {
			// Create the generic file
			PrintWriter genericFile = new PrintWriter(path, INDEX_FILE_ENCODING);
			// Write the generic file
			genericFile.println("<?php ");
			genericFile.println("    print \"<html>\\n\";");
			genericFile.println("    print \"<head>\\n\";");
			genericFile.println("    print \"<link rel=\\\"stylesheet\\\" href=\\\"/styles/stylesheet.css\\\" type=\\\"text/css\\\">\";");
			genericFile.println("    print \"</head>\\n\";");
			genericFile.println("    print \"<body>\\n\";");
			genericFile.println("    print \"<h1>" + function + "</h1>\\n\";");
			genericFile.println("    print \"<h2>To be implemented</h2>\\n\";");
			genericFile.println("    print \"</body>\\n\";");
			genericFile.println("    print \"</html>\\n\";");
			genericFile.println("?>");
			// Close the generic file
			genericFile.close();
			// Inform the user
			informUser("Created file: " + path);
		} catch (UnsupportedEncodingException exception ) {
			// Inform the user of the error
			informUser("ERROR: Text encoding (" + INDEX_FILE_ENCODING + ") for " + path + " is not supported");
		} catch (FileNotFoundException exception ) {
			// Inform the user of the error
			informUser("ERROR: Could not create " + path);
		}
	}
	
	// Create the file to fetch a list
	private void createFetchListFile(String path, String function, String url, String object) {
		try {
			// Create the generic file
			PrintWriter fetchListFile = new PrintWriter(path, INDEX_FILE_ENCODING);
			// Write the generic file
			fetchListFile.println("<?php");
			fetchListFile.println("    // Set the page name");
			fetchListFile.println("    $page_name = \"" + function + "\";");
			fetchListFile.println("    // Set the page URL");
			fetchListFile.println("    $page_url = '" + url + ".json';");
			fetchListFile.println("");
			fetchListFile.println("    // Function to display the results");
			fetchListFile.println("    function print_results_as_html($data) {");
			fetchListFile.println("        // Print all the results");
			fetchListFile.println("        foreach ($data->results as &$result) {");
			fetchListFile.println("            // Get the id");
			fetchListFile.println("            $id = $result->id;");
			fetchListFile.println("            // Get the record");
			fetchListFile.println("            $record = $data->" + object + "->$id;");
			fetchListFile.println("            // Print the record");
			fetchListFile.println("            print \"<tr><th>record</th><td class=\\\"code\\\">\";");
			fetchListFile.println("            print_r($record);");
			fetchListFile.println("            print \"</td></tr>\\n\";");
			fetchListFile.println("            // Print a record separator");
			fetchListFile.println("            print \"<tr><td colspan=\\\"2\\\">&nbsp;</td>\";");
			fetchListFile.println("        }");
			fetchListFile.println("    }");
			fetchListFile.println("");
			fetchListFile.println("    include \"../../get_list_page.php\";");
			fetchListFile.println("?>");
			// Close the generic file
			fetchListFile.close();
			// Inform the user
			informUser("Created file: " + path);
		} catch (UnsupportedEncodingException exception ) {
			// Inform the user
			informUser("ERROR: Text encoding (" + INDEX_FILE_ENCODING + ") for " + path + " is not supported");
		} catch (FileNotFoundException exception ) {
			// Inform the user
			informUser("ERROR: Could not create " + path);
		}
	}

	// Create the file to fetch a record
	private void createFetchRecordFile(String path, String function, String url, String object) {
		try {
			// Create the generic file
			PrintWriter fetchRecordFile = new PrintWriter(path, INDEX_FILE_ENCODING);
			// Write the generic file
			fetchRecordFile.println("<?php");
			fetchRecordFile.println("    // Set the page name");
			fetchRecordFile.println("    $page_name = \"" + function + "\";");
			fetchRecordFile.println("    // Set the page URL");
			fetchRecordFile.println("    $page_url = '" + url + "';");
			fetchRecordFile.println("");
			fetchRecordFile.println("    // Function to display the results");
			fetchRecordFile.println("    function print_results_as_html($data) {");
			fetchRecordFile.println("        // Print all the results");
			fetchRecordFile.println("        foreach ($data->results as &$result) {");
			fetchRecordFile.println("            // Get the id");
			fetchRecordFile.println("            $id = $result->id;");
			fetchRecordFile.println("            // Get the record");
			fetchRecordFile.println("            $record = $data->" + object + "->$id;");
			fetchRecordFile.println("            // Print the record");
			fetchRecordFile.println("            print \"<tr><th>record</th><td class=\\\"code\\\">\";");
			fetchRecordFile.println("            print_r($record);");
			fetchRecordFile.println("            print \"</td></tr>\\n\";");
			fetchRecordFile.println("            // Print a record separator");
			fetchRecordFile.println("            print \"<tr><td colspan=\\\"2\\\">&nbsp;</td>\";");
			fetchRecordFile.println("        }");
			fetchRecordFile.println("    }");
			fetchRecordFile.println("");
			fetchRecordFile.println("    include \"../../get_record_page.php\";");
			fetchRecordFile.println("?>");
			// Close the generic file
			fetchRecordFile.close();
			// Inform the user
			informUser("Created file: " + path);
		} catch (UnsupportedEncodingException exception ) {
			// Inform the user
			informUser("ERROR: Text encoding (" + INDEX_FILE_ENCODING + ") for " + path + " is not supported");
		} catch (FileNotFoundException exception ) {
			// Inform the user
			informUser("ERROR: Could not create " + path);
		}
	}

	// Create a file to fetch a single record
	private void createFetchRecordFile(String path, String function, String url) {
		// For now, create the generic file
		createGenericFile(path, function, url);
	}

	// Create a file to create a record
	private void createCreateRecordFile(String path, String function, String url) {
		// For now, create the generic file
		createGenericFile(path, function, url);
	}

	// Create a file to update a record
	private void createUpdateRecordFile(String path, String function, String url) {
		// For now, create the generic file
		createGenericFile(path, function, url);
	}

	// Create a file to delete a record
	private void createDeleteRecordFile(String path, String function, String url) {
		// For now, create the generic file
		createGenericFile(path, function, url);
	}

	private void processSectionFile(String srcDir, String fileName, String dstDir ) {
		// Are we in a section
		boolean inSection = false;
		// The name of the section
		String sectionName = "";
		// The name of the section directory
		String sectionNameDir = "";
		// Are we in an object = false;
		boolean inObject = false;
		// The name of the object
		String objectName = "";
		// The name of the object directory
		String objectNameDir = "";
		// The URL of the object
		String objectUrl = "";
		// The function title
		String functionTitle = "";
		// The function title file name
		String functionTitleFileName = "";
		// The function title path
		String functionTitlePath = "";
		// The function type
		functionTypes functionType = functionTypes.unknown;
		// Create the section file name
		String sectionFileName = srcDir + fileSeparator + fileName;
		// Read the section text file, line by line
		try  
		{
			// Open the section file name
			File sectionFile=new File(sectionFileName);
			// Create a file reader
			FileReader fileReader = new FileReader(sectionFile);
			// Create a buffered reader
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			// The line of text to read
			String line;
			// Keep track of the line count
			int lineCount =0;
			// Loop through the text file
			while((line=bufferedReader.readLine())!=null)  
			{
				// Increase the line count
				lineCount++;
				// Split the line by tabs
				String parts[] = line.split("\t");
				// Is it a section?
				if((parts.length >= 1) && (parts[0] != null) && (parts[0].length()>0)) {
					// Get the section name
					sectionName = parts[0];
					// Get the section directory name
					sectionNameDir = getDirName(sectionName);
					// Are we already in a section?
					if(inSection) {
						// Close the section
						indexFile.println("				</ul>");
						indexFile.println("			</ul>");
						indexFile.println("		</ul>");
					}
					// Create a section directory
					createDirectory(dstDir + fileSeparator + sectionNameDir );
					// Create a section in the index file
					indexFile.println("		<ul>");
					indexFile.println("			<li class=\"level-1\">" + sectionName + "</li>");
					indexFile.println("			<ul>");
					// Set we are in a section now
					inSection = true;
					// Create a new section
				} else {
					// Is it an object?
					if((parts.length >= 3) && (parts[1] != null) && (parts[1].length()>0) && (parts[2] != null) && (parts[2].length()>0)) {
						// Get the object name
						objectName = parts[1];
						// Get the object directory name
						objectNameDir = getDirName(objectName);
						// Get the object URL
						objectUrl = parts[2];
						// Are we already in an object?
						if(inObject) {
							// Create the end of a function and object list in the index file
							indexFile.println("				</ul>");
						}
						// Set we are in an object
						inObject = true;
						// Create an object directory
						createDirectory(dstDir + fileSeparator + sectionNameDir + fileSeparator + objectNameDir);
						// Create an object in the index file
						indexFile.println("				<li class=\"level-2\">" + objectName + "</li>");
						indexFile.println("				<ul>");
					} else {
						if((parts.length >= 4) && (parts[2] != null) && (parts[2].length()>0) && (parts[3] != null) && (parts[3].length()>0)) {
							// Get the function title
							functionTitle = parts[2];
							// Get the function title file name
							functionTitleFileName = getFileName(functionTitle);
							// Get the function title path
							functionTitlePath = dstDir + fileSeparator + sectionNameDir + fileSeparator + objectNameDir + fileSeparator + functionTitleFileName + ".php";
							// Get the function type
							functionType = functionTypeToEnum(parts[3]);
							// Create the end of the link
							String linkEnd = "</a></li>";
							// Is it the "Fetching a list" function?
							if(functionTitle.startsWith(FETCH_LIST_TEXT) || functionTitle.startsWith(FETCH_RECORD_TEXT)) {
								// End an asterisk to the end of the link
								linkEnd = "</a> (*)</li>";
							}
							// Create function list in the index file
							indexFile.println("					<li class=\"level-3\"><a href=\"." + fileSeparator + sectionNameDir + fileSeparator + objectNameDir + fileSeparator + functionTitleFileName + ".php\" target=\"_blanc\">" + functionTitle + linkEnd);
							// Create function file
							switch(functionType) {
							case GET:
								// Is it the "Fetching a list" function?
								if(functionTitle.startsWith(FETCH_LIST_TEXT)) {
									// Create the fetch list file
									createFetchListFile(functionTitlePath, functionTitle, objectUrl, objectNameDir.toLowerCase());
								} else {
									if(functionTitle.startsWith(FETCH_RECORD_TEXT)) {
										createFetchRecordFile(functionTitlePath, functionTitle, objectUrl, objectNameDir.toLowerCase());
									} else {
										// Create the fetch record file
										createFetchRecordFile(functionTitlePath, functionTitle, objectUrl);
									}
								}
								break;
							case POST:
								// Create the post record file
								createCreateRecordFile(functionTitlePath, functionTitle, objectUrl);
								break;
							case PUT:
								// Create the put record file
								createUpdateRecordFile(functionTitlePath, functionTitle, objectUrl);
								break;
							case DELETE:
								// Create the delete record file
								createDeleteRecordFile(functionTitlePath, functionTitle, objectUrl);
								break;
							default: 
								// Inform the user of the error
								informUser("ERROR: Unknown function type (" + parts[3] + ") in the section text file " + sectionFileName + " (line: " + lineCount + ")");
								break;
							}
		
						} else {
							// Inform the use of the error
							informUser("ERROR: Malformed line (line: " + lineCount + ") in the section text file " + sectionFileName);
						}
					}
				}
			}
			// End the section list in the index file
			indexFile.println("				</ul>");
			indexFile.println("			</ul>");
			indexFile.println("		</ul>");
			// Close the file reader
			fileReader.close();  
		}  
		catch(IOException e)  
		{
			// Inform the user of the error
			informUser("ERROR: Could not read the section text file " + sectionFileName);
		}
	}
	
	// Convert each text file to a directory, entry to an index file, and a PHP script
	private void run() {
		// Create the search path
		String searchPath = sourcePath;
		// Get the file separator
		String fileSeparator = System.getProperty("file.separator");
		// Does the search path ends with a file separator
		if(searchPath.endsWith(fileSeparator)) {
			// Remove the file separator
			searchPath = searchPath.substring(0, searchPath.length() - 1);
		}
		// Get the files in the directory
		File txtHandle = new File( searchPath );
		// We have a text handle?
		if ( txtHandle != null) {
			// Get the list of files in
			File fileList[] = txtHandle.listFiles();
			// List of section file name
			ArrayList<String> fileNames = new ArrayList<String>();
			// Loop through the list of files
			for ( int index = 0; index < fileList.length; index++ ) {
				// Get the file name
				String fileName = fileList[index].getName();
				// Is it a text file we can read?
				if ((fileName != null) && (fileList[index].isFile()) && (fileList[index].canRead()) && (fileName.endsWith(TXT_FILE_EXTENSION))) {
					// Add the file name to the list
					fileNames.add(fileName);
				}
			}
			// Sort the list of file names
			Collections.sort(fileNames);
			// Create iterator
			Iterator<String> iterator = fileNames.iterator();
			// Loop through the list of files
			while(iterator.hasNext()) {
				// Process the section file
				processSectionFile(searchPath, iterator.next(), destinationPath);
			}
		}
	}

	// Clean up the object
	private void done() {
		// Write the end of the index file
		indexFile.println("	</body>");
		indexFile.println("</html>");
		// Close the index file
		indexFile.close();
	}

	// Display help message
	public static final void displayHelp() {
		// Display the default help message
		System.out.println("GenMvnLnkTstFrmWrk - Generate Mavenlink Test Framework.");
		System.out.println("Usage:   java GenMvnLnkTstFrmWrk <path section text files> <path to the destination directory>");
		System.out.println("Example: java GenMvnLnkTstFrmWrk /Users/percy.rotteveel/Documents/IncludeSec/Mavenlink-Integration/Sections /Users/percy.rotteveel/Documents/IncludeSec/Mavenlink-Integration/mavenlink");
		System.out.println("Notes:   1) Everyting in the destination directory, it will be overwritten.");
		System.out.println("         2) It is expected the application can at least read one section text file.");
		System.out.println("Version: 1.0 - 2020/04/18 - PWAR - Created.");
	}

	// Display help message
	public static final void informUser(String errorMsg) {
		// Is there an error message?
		if((errorMsg != null) && (errorMsg.length() > 0 )) {
			// Display the error message
			System.out.println(errorMsg);
		}
	}

	public static void main(String[] args) {
		// Are source and destination directories specified?
		if (( args != null ) && ( args.length > 1 )) {
			// Get the source directory
			String srcDir = args[0];
			// Get the destination directory
			String dstDir = args[1];
			// Create the object
			GenererateCode genertor = new GenererateCode( srcDir, dstDir );
			// Object created?
			if ( genertor != null )
			{
				// Object initialized?
				if ( genertor.init()) {
					// Search for files
					genertor.run();
					// Terminate the object
					genertor.done();
				}
			}
		}
		else
		{
			// Inform the user
			GenererateCode.displayHelp();
		}
	}
}
