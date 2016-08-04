/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmxgen;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Ram
 */
public class HAR2JMX {

    private static String [] toStringArray(JSONArray array){
        ArrayList<String> list = new ArrayList<String>();              
        for (int i = 0; i < array.size(); i++) {
            list.add(array.get(i).toString());
        }
       return list.toArray(new String[list.size()]);
    }
    public static void main(String[] argv) throws Exception {

        // load config file
        String config_file = "config.json";
        String jmeter_home ="";
        String output_file = "result.jmx";
        String [] exclude_host_name={};
        String [] exclude_file_type={};
        String har_location = "";
        boolean download_resource = false;
        if (argv.length == 1) {
            config_file = argv[0];
        }
        //System.out.println(config_file);
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(config_file));
            JSONObject jsonObject = (JSONObject) obj;
            jmeter_home = (String) jsonObject.get("jmeter_home");
            output_file = (String) jsonObject.get("jmx_output_name");
            download_resource = (boolean) jsonObject.get("download_resource");
            har_location = (String)jsonObject.get("har_location");
             System.out.println(output_file);
            exclude_host_name = toStringArray((JSONArray)jsonObject.get("exclude_host_name"));
            exclude_file_type = toStringArray((JSONArray)jsonObject.get("exclude_file_type"));            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        // Initialize the configuration variables
        String jmeterHome = jmeter_home;
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
        testPlan.setComment("");
        testPlan.setFunctionalMode(false);
        testPlan.setSerialized(false);
        Arguments args = new Arguments();
        args.setProperty(TestElement.TEST_CLASS, Arguments.class.getName());
        args.setProperty(TestElement.GUI_CLASS, ArgumentsPanel.class.getName());
        testPlan.setUserDefinedVariables(args);

        // ThreadGroup controller
        LoopController loopController = new LoopController();
        loopController.setEnabled(true);
        loopController.setLoops(5);
        loopController.setProperty(TestElement.TEST_CLASS,
                LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS,
                LoopControlPanel.class.getName());

        // ThreadGroup
        org.apache.jmeter.threads.ThreadGroup threadGroup = new org.apache.jmeter.threads.ThreadGroup();
        threadGroup.setName("Thread Group");
        threadGroup.setEnabled(true);
        threadGroup.setSamplerController(loopController);
        threadGroup.setNumThreads(5);
        threadGroup.setRampUp(10);
        threadGroup.setProperty(TestElement.TEST_CLASS,
                org.apache.jmeter.threads.ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS,
                ThreadGroupGui.class.getName());

        // Create TestPlan hash tree
        HashTree testPlanHashTree = new ListedHashTree();
        testPlanHashTree.add(testPlan);

        // Add ThreadGroup to TestPlan hash tree
        HashTree threadGroupHashTree = new ListedHashTree();
        threadGroupHashTree = testPlanHashTree.add(testPlan, threadGroup);

        // Add HTTP Sampler to ThreadGroup hash tree
        HashTree httpSamplerHashTree = new HashTree();
        httpSamplerHashTree = new JsonReader().parserHAR(exclude_host_name,exclude_file_type,har_location,download_resource);
        threadGroupHashTree.add(httpSamplerHashTree);

        // Save to jmx file
        SaveService.saveTree(testPlanHashTree, new FileOutputStream(
                output_file));
    }
}
