package org.timothyb89.irc;

/**
 *
 * @author tim
 */
public class YouTubeVideo {

	private String id;
	private String videoId;
	private String title;
	private String description;
	private String thumbnail;
	private String thumbnailHires;
	private int views;
	private int favorites;
	private int likes;
	private int dislikes;

	public YouTubeVideo() {
	}

	public String getId() {
		return id;
	}

	public String getContentType(String extra) {
		if (extra != null && extra.equalsIgnoreCase("thumbnail")) {
			return "image/jpeg";
		} else {
			return "text/html";
		}
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getThumbnailHires() {
		return thumbnailHires;
	}

	public void setThumbnailHires(String thumbnailHires) {
		this.thumbnailHires = thumbnailHires;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getFavorites() {
		return favorites;
	}

	public void setFavorites(int favorites) {
		this.favorites = favorites;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}
	
}