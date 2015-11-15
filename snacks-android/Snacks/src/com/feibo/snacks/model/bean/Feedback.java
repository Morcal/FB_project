package com.feibo.snacks.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 用户反馈
 * @author fuJun
 *
 */
public class Feedback {

	/**
	 * 1:用户 2、零食小喵
	 */
	@SerializedName("type")
	public int type;

	@SerializedName("content")
	public String content;

	@SerializedName("author")
	public FeedbackAuthor author;

	public Feedback(boolean isOffical, String content) {
		this.type = isOffical ? 0 : 1;
		this.content = content;
	}

	public boolean isOfficial() {
		return type == 0;
	}

	public String icon() {
		return author == null ? null : author.icon;
	}

	public void setIcon(String icon) {
		if (author == null) {
			author = new FeedbackAuthor();
		}
		author.icon = icon;
	}

	public static class FeedbackAuthor {

		@SerializedName("uid")
		public int uid;

		@SerializedName("nickname")
		public String nickname;

		@SerializedName("icon")
		public String icon;
	}
}
