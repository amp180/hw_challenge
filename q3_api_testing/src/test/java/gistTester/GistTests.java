package gitTester;


import org.junit.*;
import static org.junit.Assert.*;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.Jsoner;
import java.net.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import gitTester.GithubConfig;

public class GistTests {
    final static String baseUrl= "https://api.github.com/";
    final static String gistsUrl = baseUrl + "gists";
    final static String sampleGist =
    "{"+
    "    \"description\": \"Test Gist\","+
    "    \"public\": false,"+
    "    \"files\": {"+
    "      \"test_gist.txt\": {"+
    "        \"content\": \"This is a test gist.\""+
    "      }"+
    "   }"+
    "}";  // Sample gist to create.
    static GithubConfig config;
    JsonObject createdGist; // gist created by test setup.

    @BeforeClass
    public static void getConfig() {
        // Load the config from props file.
        config = new GithubConfig();
    }

    @Before
    public void createGist() throws ProtocolException, IOException {
        final URL url = new URL(gistsUrl);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        setAuthHeader(conn);
        OutputStreamWriter writer = getWriter(conn);
        writer.write(sampleGist);
        writer.flush();
        writer.close();
        final int responseCode = conn.getResponseCode();
        assertEquals("Creating a gist should return 201 created.", responseCode, 201);
        JsonObject responseObject = readResponseObject(conn);
        // Set the createdGist variable for the other tests.
        createdGist = responseObject;
        assertNotNull("Should be able to parse response", createdGist);
    }

    @Test
    public void testCreateGistResponse(){
        assertGistHasFields(createdGist);
   }

   @Test
   public void testGistWasCreated() throws IOException {
        HttpURLConnection conn = getGistById((String) createdGist.get("id"));
        assertTrue("Getting the created gist by id should return 200.", conn.getResponseCode() == 200);
    }

    @Test
    public void testGistUserListing() throws ProtocolException, IOException{
        final URL url = new URL(gistsUrl);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        setAuthHeader(conn);
        conn.connect();
        final int responseCode = conn.getResponseCode();
        assertEquals("Listing gists should return 200.", responseCode, 200);
        JsonArray responseArray = readResponseArray(conn);
        assertNotNull("Should be able to parse listing response", responseArray);

        ArrayList<JsonObject> listingArrayList = new ArrayList<>();
        responseArray.asCollection(listingArrayList);
        JsonObject createdGistFromListing = null;
        for (JsonObject gistListing : listingArrayList) {
            if (createdGist.get("id").equals(gistListing.get("id"))) {
                assertGistHasFields(gistListing);
                createdGistFromListing = gistListing;
            }
        }
        assertNotNull("Created gist should be in listing.", createdGistFromListing);
    }

    @After
    public void destroyGist() throws ProtocolException, IOException {
        final String id = (String)createdGist.get("id");
        final URL url = new URL(gistsUrl+"/"+id);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        setAuthHeader(conn);
        conn.connect();
        assertEquals("Deletion should return a 204 response code.", conn.getResponseCode(), 204);
        final HttpURLConnection delConn = getGistById((String) createdGist.get("id"));
        assertEquals("Getting the deleted gist by id should return 404.", delConn.getResponseCode(), 404);
    }

    static void setAuthHeader(HttpURLConnection conn){
        conn.setRequestProperty("Authorization", "token " + config.token);
    }

    static OutputStreamWriter getWriter(HttpURLConnection conn) throws ProtocolException, IOException {
        conn.setDoOutput(true);
        conn.connect();
        OutputStream outputStream = conn.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        return writer;
    }

    static String readResponseString(HttpURLConnection conn) throws IOException {
        InputStream inputStream = conn.getInputStream();
        final String str = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        inputStream.close();
        return str;
    }

    static JsonObject readResponseObject(HttpURLConnection conn) throws IOException {
        final String responseString = readResponseString(conn);
        return Jsoner.deserialize(responseString, new JsonObject());
    }

    static JsonArray readResponseArray(HttpURLConnection conn) throws IOException {
        final String responseString = readResponseString(conn);
        return Jsoner.deserialize(responseString, new JsonArray());
    }

    static HttpURLConnection getGistById(final String id) throws IOException {
        final URL url = new URL(gistsUrl+"/"+id);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        setAuthHeader(conn);
        conn.connect();
        return conn;
    }

    static void assertGistHasFields(final JsonObject gist) {
        assertTrue("Field url should be present", gist.containsKey("url"));
        assertTrue("Field forks_url should be present", gist.containsKey("forks_url"));
        assertTrue("Field commits_url should be present", gist.containsKey("commits_url"));
        assertTrue("Field id should be present", gist.containsKey("id"));
        assertTrue("Field node_id should be present", gist.containsKey("node_id"));
        assertTrue("Field git_pull_url should be present", gist.containsKey("git_pull_url"));
        assertTrue("Field git_push_url should be present", gist.containsKey("git_push_url"));
        assertTrue("Field html_url should be present", gist.containsKey("html_url"));
        assertTrue("Field files should be present", gist.containsKey("files"));
        assertTrue("Field public should be present", gist.containsKey("public"));
        assertTrue("Field created_at should be present", gist.containsKey("created_at"));
        assertTrue("Field updated_at should be present", gist.containsKey("updated_at"));
        assertTrue("Field description should be present", gist.containsKey("description"));
        assertTrue("Field comments should be present", gist.containsKey("comments"));
        assertTrue("Field user should be present", gist.containsKey("user"));
        assertTrue("Field comments_url should be present", gist.containsKey("comments_url"));
        assertTrue("Field owner should be present", gist.containsKey("owner"));
        assertTrue("Field truncated should be present", gist.containsKey("truncated"));
    }
}