/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmxgen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;

import edu.umass.cs.benchlab.har.*;
import edu.umass.cs.benchlab.har.tools.*;
import java.util.ArrayList;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.HashTree;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.jorphan.collections.ListedHashTree;


/**
 *
 * @author Ram
 */
public class JsonReader {

      public HashTree parserHAR(final String [] exceptionHostName,final String [] exceptionFileType,String har_location, boolean download_resource) {

        //final String[] exceptionFileType =  {"png","jpg","css","js","svg","gif","woff"};      
        
        //Dropbox
        /*final String[] exceptionHostName =  {"gcm.netmng.com", "cs.adingo.jp", "ums.adtechus.com", "www.google.co.in", "marketing.dropbox.com", "epiv.cardlytics.com", "secure.adnxs.com",
                                            "ads.stickyadstv.com", "stats.g.doubleclick.net", "ak1s.abmr.net", "t.co", "api.demandbase.com", "cm.g.doubleclick.net", "dis.criteo.com",
                                            "www.google.com", "www.google-analytics.com", "fonts.googleapis.com", "bat.r.msn.com", "us-u.openx.net", "www.bizographics.com", 
                                            "ad.sxp.smartclip.net", "pixel.rubiconproject.com", "a.company-target.com", "t4.liverail.com", "com-dbox.netmng.com", "dps.bing.com",
                                            "simage2.pubmatic.com", "googleads.g.doubleclick.net", "ssp.adskom.com", "4727608.fls.doubleclick.net", "load.s3.amazonaws.com", "stags.bluekai.com",
                                            "sync.mathtag.com", "ap-southeast-1.dc.ads.linkedin.com", "dc.ads.linkedin.com", "pixel.mathtag.com", "sp.analytics.yahoo.com", 
                                            "www.googleadservices.com", "sync.adap.tv", "imp2.ads.linkedin.com", "bat.bing.com","ad.360yield.com", "sync.adaptv.advertising.com",
                                            "jp-u.openx.net", "sync.rhythmxchange.com", "b92.yahoo.co.jp", "x.bidswitch.net", "www.facebook.com", "fonts.gstatic.com", "analytics.twitter.com",
                                            "cdn-static-secure.liverail.com", "tags.bluekai.com", "dsum-sec.casalemedia.com","ib.adnxs.com", "4212862.fls.doubleclick.net", "loadm.exelator.com",
                                            "2.realtime.services.box.net","edit.boxlocalhost.com","s1464.t.eloqua.com"};

        */
        HashSet<String> uniqueDomain = new HashSet<>();
        // Add HTTP Sampler to ThreadGroup hash tree
        HashTree httpSamplerHashTree = new ListedHashTree();

        //String filename = new String("O365_drive.har");
        File f = new File(har_location);
        HarFileReader r = new HarFileReader();
        try {
            // All violations of the specification generate warnings
            List<HarWarning> warnings = new ArrayList<HarWarning>();
            System.out.println("Reading " + har_location);
            HarLog log = r.readHarFile(f, warnings);

            // Access all elements as objects
            HarEntries entries = log.getEntries();

            // Used for loops
            List<HarPage> pages = log.getPages().getPages();
            List<HarEntry> hentry = entries.getEntries();

            for (HarPage page : pages) {
                System.out.println("page start time: "
                        + ISO8601DateFormatter.format(page.getStartedDateTime()));
                System.out.println("page id: " + page.getId());
                System.out.println("page title: " + page.getTitle());
            }

            int sample_number =1;
            //Output "response" code of entries.
            for (HarEntry entry : hentry) {
                
                URL url = new URL(entry.getRequest().getUrl());               
                String ext = FilenameUtils.getExtension(url.getPath());
               
                if(ArrayUtils.contains(exceptionFileType,ext)){
                        //System.out.println("*filtered ext: "+ext);
                        continue;
                }
                if(ArrayUtils.contains(exceptionHostName,url.getHost())){
                        //System.out.println("*filtered ext: "+ext);
                        continue;
                }
                //System.out.println("Non filtered ext: "+ext);
                HeaderManager mgr = new HeaderManager();
                mgr.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
                mgr.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
                mgr.setEnabled(true);
                mgr.setName("HTTP Manager");

                //httpSampler.setHeaderManager(mgr);
                List<HarHeader> har_header = entry.getRequest().getHeaders().getHeaders();
                for (HarHeader har_header1 : har_header) {
                    
                    Header hdr = new Header(har_header1.getName(), har_header1.getValue());
                    mgr.add(hdr);
                }

               

                HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
                //setting up jmeter header
                httpSampler.setDomain(url.getHost());
                httpSampler.setMethod(entry.getRequest().getMethod());
                
                //String q = url.getQuery()!=null ? url.getQuery() : "";
                if(url.getQuery()!=null)
                    httpSampler.setPath(url.getPath() + "?" + url.getQuery());
                else
                     httpSampler.setPath(url.getPath());
                    
                httpSampler.setName(sample_number +". "+url.getPath());
                httpSampler.setEnabled(true);
                httpSampler.setUseKeepAlive(true);
                httpSampler.setFollowRedirects(true);
                if(download_resource){
                    httpSampler.setImageParser(true);
                }
                
                httpSampler.setProperty(TestElement.GUI_CLASS,
                        HttpTestSampleGui.class.getName());
                httpSampler.setProperty(TestElement.TEST_CLASS,
                        HTTPSamplerProxy.class.getName());

                
                //seeting up Payload
                if(entry.getRequest().getPostData()!=null){
                    List <HarPostDataParam> post_data= entry.getRequest().getPostData().getParams().getPostDataParams();

                    for (HarPostDataParam har_postdata_param : post_data) {
                        if(har_postdata_param.getName()!=null)
                            httpSampler.addArgument(har_postdata_param.getName(), har_postdata_param.getValue());                    
                    }
                }
                
                httpSamplerHashTree.add(httpSampler, mgr);
                
                /*System.out.println("request Method: " + entry.getRequest().getMethod()); //Output request type
                System.out.println("    response URL: " + entry.getRequest().getUrl()); //Output url of request
                System.out.println("    response code: " + entry.getResponse().getStatus()); // Output the 
                System.out.println("    response code: " + entry.getRequest().getHeaders());*/
                System.out.println("Added as " + url.getPath() +" for "+ entry.getRequest().getUrl()); //Output request type
                uniqueDomain.add(url.getHost());
                sample_number++;
            }

            /*
             // Once you are done manipulating the objects, write back to a file
             System.out.println("Writing " + "fileName" + ".test");
             File f2 = new File("fileName" + ".test");
             w.writeHarFile(log, f2);
             */
        } catch (JsonParseException e) {
            e.printStackTrace();
            //fail("Parsing error during test");
        } catch (IOException e) {
            e.printStackTrace();
            //fail("IO exception during test");
        }
        System.out.println("SKIPPED RESOURCE TYPE");
        System.out.println("======================");
        System.out.println(Arrays.toString(exceptionFileType));
        System.out.println("");
         System.out.println("SKIPPED DOMAINS");
        System.out.println("======================");
        System.out.println(Arrays.toString(exceptionHostName));
        System.out.println("");
        System.out.println("UNIQUE HOST NAMES");
        System.out.println("======================");        
        System.out.println(uniqueDomain.toString());
        System.out.println("");
        return httpSamplerHashTree;
    }
}
