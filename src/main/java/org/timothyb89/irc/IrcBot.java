
package org.timothyb89.irc;

import com.brewtab.irc.User;
import com.brewtab.irc.client.Channel;
import com.brewtab.irc.client.ChannelListener;
import com.brewtab.irc.client.Client;
import com.brewtab.irc.client.ClientFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tim
 */
public class IrcBot {

	private Logger logger = LoggerFactory.getLogger(IrcBot.class);
	
	private static Pattern YOUTUBE_PATTERN = Pattern.compile(
			"http://(?:www.)?youtu(?:\\.be|be\\.com)/\\S+",
			Pattern.CASE_INSENSITIVE);
	
	private Client client;
	private Channel channel;
	
	public IrcBot() {
		client = ClientFactory.newInstance().connect(
				"irc://youtube-bot@irc.timothyb89.org/");
		
		
		channel = client.join("#robotics");
		channel.addListener(listener);
	}
	
	private ChannelListener listener = new ChannelListener() {

		public void onJoin(Channel channel, User user) {
			
		}

		public void onPart(Channel channel, User user) {
			
		}

		public void onQuit(Channel channel, User user) {
			
		}

		public void onMessage(Channel channel, User from, String message) {
			Matcher m = YOUTUBE_PATTERN.matcher(message);
			while (m.find()) {
				String id = YouTubeClient.getVideoId(m.group());
				try {
					YouTubeVideo v = new YouTubeClient().getVideo(id);
					channel.write(String.format("\"%s\" [%d views] [%d likes / %d dislikes] [%s]",
							v.getTitle(),
							v.getViews(),
							v.getLikes(),
							v.getDislikes(),
							v.getThumbnailHires()));
				} catch (Exception ex) {
					logger.error("Couldn't get video info for URL: " + m.group(), ex);
				}
			}
		}
		
	};
	
}
