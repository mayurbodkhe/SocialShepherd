package com.ss.social.media.api;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import facebook4j.Comment;
import facebook4j.Post;
import facebook4j.ResponseList;

public class FacebookExtractor {
	
	private static final Logger logger = Logger.getLogger(FacebookExtractor.class);
	
	public List<PostInfo> getPostByCommentCount(String searchText, int commentCount){
		
		ResponseList<Post> posts = null;
		List<PostInfo> postInfos = null;
		PostInfo postInfo = null;
		List<String> comments = null;
		int counter = 0;

		try {
			
			long postExtractStartTime = System.currentTimeMillis();
			
			posts = FaceBookConnection.getInstance().getConnection().searchPosts(searchText);
			       
			if(posts != null && posts.size()>0){
				postInfos = new LinkedList<PostInfo>();
				for(Post post : posts){
					
					postInfo = new PostInfo();
					postInfo.setUser(post.getName());
					postInfo.setMessage(post.getMessage());
					
				          comments = new LinkedList<String>();
		            
		            
		            for(Comment com: post.getComments()){
		            	if(counter < commentCount)
		            		comments.add(com.getMessage());
				    }
		            
		            postInfo.setComments(comments);
		            postInfos.add(postInfo);
				}
			}
			
			DecimalFormat df = new DecimalFormat("#.##");
			logger.info("Finished Facebook Posts in "+Double.valueOf(df.format((double) (System.currentTimeMillis() - postExtractStartTime)/1000.0))+"(s)");
			
		} catch (Exception ex) {
			logger.info("Error while extracting Posts", ex);
	    }
		 
		return postInfos;
	}
	
	public static void main(String[] args) {
		FacebookExtractor extractor = new FacebookExtractor();
		List<PostInfo> postInfos = extractor.getPostByCommentCount("airtel", 10);
		for(PostInfo postInfo: postInfos){
			System.out.println(postInfo.getMessage());
		}
	}

}
