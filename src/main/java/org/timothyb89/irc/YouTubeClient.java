package org.timothyb89.irc;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client for YouTube's JSON-C API. Manually parses the json response because
 * the google libs are pretty awful. For some reason this is stuck using the
 * v1.0 json APIs even though we request v2.0, so this may break in the future.
 *
 * This was ripped straight from https://code.google.com/p/frcdb/
 * 
 * @author tim
 */
public class YouTubeClient {

	public static final String YOUTUBE_URL =
			"http://gdata.youtube.com/feeds/api/videos/$ID?"
			+ "v=2&"
			+ "alt=json&"
			+ "prettyprint=true";
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	
	private Logger logger = LoggerFactory.getLogger(YouTubeClient.class);

	public YouTubeClient() {
		mapper = new ObjectMapper();
		writer = mapper.writer(new DefaultPrettyPrinter());
	}

	/**
	 * Creates a new YouTubeVideo by fetching information on the provided video
	 * id from YouTube via a JSON request.
	 *
	 * @param id the video id
	 * @return
	 * @throws IOException
	 */
	public YouTubeVideo getVideo(String id) throws IOException {
		URL url = new URL(getApiUrl(id));
		URLConnection uconn = url.openConnection();
		HttpURLConnection conn = (HttpURLConnection) uconn;

		if (conn.getResponseCode() != 200) {
			throw new IllegalArgumentException("Video not found!");
		}

		InputStream input = conn.getInputStream();
		JsonNode root = mapper.readTree(input);
		input.close();

		JsonNode entry = root.get("entry");

		YouTubeVideo video = new YouTubeVideo();
		video.setVideoId(id);
		video.setTitle(entry.get("title").get("$t").asText());

		JsonNode mediaGroup = entry.get("media$group");
		video.setDescription(mediaGroup
				.get("media$description")
				.get("$t")
				.asText());

		for (JsonNode thumbElement : mediaGroup.get("media$thumbnail")) {
			String type = thumbElement.get("yt$name").asText();
			if (type.equals("default")) {
				video.setThumbnail(thumbElement.get("url").asText());
			} else if (type.equals("hqdefault")) {
				video.setThumbnailHires(thumbElement.get("url").asText());
			}
		}

		JsonNode stats = entry.get("yt$statistics");
		video.setFavorites(stats.get("favoriteCount").asInt());
		video.setViews(stats.get("viewCount").asInt());

		if (entry.has("yt$rating")) {
			JsonNode rating = entry.get("yt$rating");
			if (rating.has("numLikes")) {
				video.setLikes(rating.get("numLikes").asInt());
			}
			if (rating.has("numDislikes")) {
				video.setDislikes(rating.get("numDislikes").asInt());
			}
		}

		return video;
	}

	public static String getApiUrl(String vid) {
		return YOUTUBE_URL.replace("$ID", vid);
	}

	/**
	 * Extracts the YouTube video ID from the given url. The video id is the
	 * text immediately following "v=...". If no id is found (or it is not a
	 * valid YouTube URL) null will be returned. This also attempts to parse
	 * youtu.be links (untested).
	 *
	 * @param url the url to search in
	 * @return the video id, or null
	 */
	public static String getVideoId(String url) {
		try {
			URI uri = new URI(url);

			String host = uri.getHost().toLowerCase();
			if (host.startsWith("www.")) {
				host = host.substring(4);
			}

			// make sure we're looking at a youtube url
			if (host.equals("youtube.com") || host.equals("www.youtube.com")) {
				List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
				for (NameValuePair p : params) {
					if (p.getName().equalsIgnoreCase("v")) {
						return p.getValue();
					}
				}

				// no v= found
				return null;
			} else if (host.equals("youtu.be")) {
				return uri.getPath().substring(1); // in theory
			} else {
				return null;
			}
		} catch (URISyntaxException ex) {
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		printInfo("http://www.youtube.com/watch?v=NWHfY_lvKIQ");
	}

	private static void printInfo(String url) throws IOException {
		System.out.println("Video:       " + url);

		String id = getVideoId(url);
		System.out.println("ID:          " + id);

		YouTubeClient client = new YouTubeClient();
		YouTubeVideo video = client.getVideo(id);

		System.out.println("Title:       " + video.getTitle());
		System.out.println("Description: " + video.getDescription());
		System.out.println("Small Thumb: " + video.getThumbnail());
		System.out.println("Large Thumb: " + video.getThumbnailHires());
		System.out.println("Favorites:   " + video.getFavorites());
		System.out.println("Views:       " + video.getViews());
		System.out.println("Likes:       " + video.getLikes());
		System.out.println("Dislikes:    " + video.getDislikes());

		System.out.println("\n===========\n\n");
	}
}