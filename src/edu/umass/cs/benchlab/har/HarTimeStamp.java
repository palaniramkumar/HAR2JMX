/**
 * BenchLab: Internet Scale Benchmarking.
 * Copyright (C) 2010-2011 Emmanuel Cecchet.
 * Contact: cecchet@cs.umass.edu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): karlholl (sf.net)
 * Contributor(s): Emmanuel Cecchet.
 */

package edu.umass.cs.benchlab.har;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

/**
 * @author
 */
public class HarTimeStamp
{
  private Long            time;
  private String          lable;
  private HarCustomFields customFields = new HarCustomFields();

  /**
   * Creates a new <code>HarPageTiming</code> object
   * 
   * @param time onContentLoad (DOMContentLoaded) or onLoad (load) Event
   * @param lable description of time DOMContentLoaded or load
   */
  public HarTimeStamp(Long time, String lable)
  {
    this.time = time;
    this.lable = lable;
  }

  /**
   * Creates a new <code>HarPageTiming</code> object from a JsonParser already
   * positioned at the beginning of the element content
   * 
   * @param jp a JsonParser already positioned at the beginning of the element
   *          content
   * @param warnings null if parser should fail on first error, pointer to
   *          warning list if warnings can be issued for missing fields
   * @throws JsonParseException
   * @throws IOException
   */
  public HarTimeStamp(JsonParser jp, List<HarWarning> warnings) throws JsonParseException, IOException
  {
    // Read the content of the log element
    if (jp.getCurrentToken() != JsonToken.START_OBJECT)
    {
      throw new JsonParseException("{ missing after \"pages\" element", jp.getCurrentLocation());
    }

    while (jp.nextToken() != JsonToken.END_OBJECT)
    {
      String name = jp.getCurrentName();
      if ("time".equals(name))
        setTime(jp.getValueAsLong());
      else if ("label".equals(name))
        setLable(jp.getText());
      else if (name != null && name.startsWith("_"))
        this.customFields.addHarCustomFields(name, jp);
      else
      {
        throw new JsonParseException("Unrecognized field '" + name + "' in page element", jp.getCurrentLocation());
      }
    }
  }

  /**
   * Returns the time value.
   * 
   * @return Returns the time.
   */
  public Long getTime()
  {
    return time;
  }

  /**
   * Sets the time value.
   * 
   * @param time The time to set.
   */
  public void setTime(Long time)
  {
    this.time = time;
  }

  /**
   * Returns the lable value.
   * 
   * @return Returns the lable.
   */
  public String getLable()
  {
    return lable;
  }

  /**
   * Sets the lable value.
   * 
   * @param lable The lable to set.
   */
  public void setLable(String lable)
  {
    this.lable = lable;
  }

}
