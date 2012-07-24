/** Copyright (C) 2010  Expertise Android

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    */

package com.expertiseandroid.lib.sociallib.messages;

/**
 * An interface for every response that readers are able to read
 * @author Expertise Android
 *
 */
public interface ReadableResponse {

  /**
   * Gets the contents (body) of the response
   * @return a string representation of the response
   */
  public String getContents();
  public int getCode();
}