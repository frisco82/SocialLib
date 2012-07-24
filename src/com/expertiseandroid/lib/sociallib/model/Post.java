
/** 
 * Copyright (C) 2010  Expertise Android
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.expertiseandroid.lib.sociallib.model;

import java.util.Date;

/**
 * A abstract class that represents posts
 * The Id is not an attribute because its type may differ from a social network to another
 * @author Expertise Android
 *
 */
public abstract class Post {
  
  public enum PostType{status, link, video, photo, other}
  
  public PostType type;
  public User author;
  public Date date;
  
  public String message;
  public String link;
  public String imagePath;
  public String title;
  
  
  protected void construct(PostType type, User author, Date date){
    this.type = type;
    this.author = author;
    this.date = date;
  }
  
  /**
   * Access to the contents of the post
   * @return a string representation of the contents
   */
  public String getContents() {
	  return this.message;
  }
  
  public String getLink() {
	  return this.link;
  }
  
  public String getImage() {
	  return this.imagePath;
  }
  
  public String getTitle() {
	  return this.title;
  }
  
  /**
   * Access the post's id
   * @return a string representation of the id
   */
  public abstract String getId();
  
  /**
   * Sets the post's id
   * @param id a string representation of the id
   */
  public abstract void setId(String id);
  
  /**
   * Sets the post's contents
   * @param content a string representation of the contents
   */
  public void setContents(String content) {
	  this.message = content;
  }

  public void setLink(String link) {
	  this.link = link;
  }
  
  public void setImage(String path) {
	  this.imagePath = path;
  }

  public void setTitle(String title) {
	  this.title = title;
  }

}