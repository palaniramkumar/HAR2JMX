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
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

/**
 * @author
 */
public class HarTimeStamps
{
  private List<HarTimeStamp> pageTimings;

  /**
   * Creates a new <code>HarTimeStamps</code> object
   */
  public HarTimeStamps()
  {
    pageTimings = new ArrayList<HarTimeStamp>();
  }

  /**
   * Creates a new <code>HarTimeStamps</code> objectfrom a JsonParser already
   * positioned at the beginning of the element content
   * 
   * @param jp a JsonParser already positioned at the beginning of the element
   *          content
   * @param warnings null if parser should fail on first error, pointer to
   *          warning list if warnings can be issued for missing fields
   * @throws JsonParseException
   * @throws IOException
   */
  public HarTimeStamps(JsonParser jp, List<HarWarning> warnings) throws JsonParseException, IOException
  {
    pageTimings = new ArrayList<HarTimeStamp>();

    // Read the content of the pages element
    if (jp.nextToken() != JsonToken.START_ARRAY)
    {
      throw new JsonParseException("[ missing after \"pages\" element " + jp.getCurrentName(), jp.getCurrentLocation());
    }

    while (jp.nextToken() != JsonToken.END_ARRAY)
    {
      addTimeStamp(new HarTimeStamp(jp, warnings));
    }
  }

  /**
   * Add a new page to the list
   * 
   * @param pageTiming the page to add
   */
  public void addTimeStamp(HarTimeStamp pageTiming)
  {
    pageTimings.add(pageTiming);
  }

  /**
   * Remove a page from the list
   * 
   * @param pageTiming the page to remove
   */
  public void removeTimeStamp(HarTimeStamp pageTiming)
  {
    pageTimings.remove(pageTiming);
  }

  /**
   * Returns the pages value.
   * 
   * @return Returns the pages.
   */
  public List<HarTimeStamp> getPages()
  {
    return pageTimings;
  }

  /**
   * Sets the pages value.
   * 
   * @param pageTimings The pages to set.
   */
  public void setPages(List<HarTimeStamp> pageTimings)
  {
    this.pageTimings = pageTimings;
  }

}
