/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 http://stackoverflow.com/questions/25511949/trying-to-generate-jmeter-test-plan-jmx-with-jmeter-api-mismatch-between-jme
 */
package jmxgen;

import java.io.FileOutputStream;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;

import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;

/**
 *
 * @author Ram
 */
public class JMXGen {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] argv) throws Exception {

       // load har file
        // Initialize the configuration variables
        String jmeterHome = "C:\\Users\\Ram\\Documents\\GitHub\\qa-auto\\Performace-Auto\\apache-jmeter-2.12";
        JMeterUtils.setJMeterHome(jmeterHome);
        JMeterUtils.loadJMeterProperties(JMeterUtils.getJMeterBinDir()
                + "\\jmeter.properties");
        JMeterUtils.initLogging();
        JMeterUtils.initLocale();

        // TestPlan
        TestPlan testPlan = new TestPlan();
        testPlan.setName("Test Plan");
        testPlan.setEnabled(true);
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());

        // ThreadGroup controller
        LoopController loopController = new LoopController();
        loopController.setEnabled(true);
        loopController.setLoops(5);
        loopController.setProperty(TestElement.TEST_CLASS,
                LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS,
                LoopControlPanel.class.getName());

        // ThreadGroup
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Thread Group");
        threadGroup.setEnabled(true);
        threadGroup.setSamplerController(loopController);
        threadGroup.setNumThreads(5);
        threadGroup.setRampUp(10);
        threadGroup.setProperty(TestElement.TEST_CLASS,
                ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS,
                ThreadGroupGui.class.getName());
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        //guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="HTTP Request2" enabled="true"
        httpSampler.setDomain("box.com");
        httpSampler.setMethod("POST");
        httpSampler.setPath("/home");
        httpSampler.setName("demo");
        httpSampler.setEnabled(true);
        httpSampler.setProperty(TestElement.GUI_CLASS,
                HttpTestSampleGui.class.getName());
        httpSampler.setProperty(TestElement.TEST_CLASS,
                HTTPSamplerProxy.class.getName());

        //guiclass="HeaderPanel" testclass="HeaderManager" testname="HTTP Header Manager" enabled="true"
        // name="HeaderManager.headers"
        HeaderManager mgr = new HeaderManager();
        mgr.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
        mgr.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        mgr.setEnabled(true);
        mgr.setName("HTTP Manager");
        Header hdr = new Header("demo", "demo1");
        mgr.add(hdr);
        //httpSampler.setHeaderManager(mgr);

        HTTPSamplerProxy httpSampler1 = new HTTPSamplerProxy();
        //guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="HTTP Request2" enabled="true"
        httpSampler1.setDomain("box.com");
        httpSampler1.setMethod("POST");
        httpSampler1.setPath("/home");
        httpSampler1.setName("demo1");
        httpSampler1.setEnabled(true);
        httpSampler1.setProperty(TestElement.GUI_CLASS,
                HttpTestSampleGui.class.getName());
        httpSampler1.setProperty(TestElement.TEST_CLASS,
                HTTPSamplerProxy.class.getName());

        HTTPSamplerProxy httpSampler2 = new HTTPSamplerProxy();
        //guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="HTTP Request2" enabled="true"
        httpSampler2.setDomain("box.com");
        httpSampler2.setMethod("POST");
        httpSampler2.setPath("/home");
        httpSampler2.setName("demo2");
        httpSampler2.setEnabled(true);
        httpSampler2.setProperty(TestElement.GUI_CLASS,
                HttpTestSampleGui.class.getName());
        httpSampler2.setProperty(TestElement.TEST_CLASS,
                HTTPSamplerProxy.class.getName());

        HTTPSamplerProxy httpSampler3 = new HTTPSamplerProxy();
        //guiclass="HttpTestSampleGui" testclass="HTTPSamplerProxy" testname="HTTP Request2" enabled="true"
        httpSampler3.setDomain("box.com");
        httpSampler3.setMethod("POST");
        httpSampler3.setPath("/home");
        httpSampler3.setName("demo2");
        httpSampler3.setEnabled(true);
        httpSampler3.setProperty(TestElement.GUI_CLASS,
                HttpTestSampleGui.class.getName());
        httpSampler2.setProperty(TestElement.TEST_CLASS,
                HTTPSamplerProxy.class.getName());

        // Create TestPlan hash tree
        ListedHashTree testPlanHashTree = new ListedHashTree();
        testPlanHashTree.add(testPlan);

        // Add ThreadGroup to TestPlan hash tree
        HashTree threadGroupHashTree = new ListedHashTree();
        threadGroupHashTree = testPlanHashTree.add(testPlan, threadGroup);

        // Add HTTP Sampler to ThreadGroup hash tree
        HashTree httpSamplerHashTree = new ListedHashTree();
        httpSamplerHashTree.add(httpSampler, mgr);
        httpSamplerHashTree.add(httpSampler1, mgr);
        httpSamplerHashTree.add(httpSampler2, mgr);
        httpSamplerHashTree.add(httpSampler3, mgr);
        
        threadGroupHashTree.add(httpSamplerHashTree);

        /*httpSamplerHashTree = threadGroupHashTree.add(httpSampler);
        
         threadGroupHashTree.add(mgr);
         threadGroupHashTree = threadGroupHashTree.add(httpSampler1);*/
        // Save to jmx file
        SaveService.saveTree(testPlanHashTree, new FileOutputStream(
                "d:\\test.jmx"));
    }

}
