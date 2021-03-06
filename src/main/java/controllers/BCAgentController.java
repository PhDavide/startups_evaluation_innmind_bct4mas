package controllers;

import agents.BCAgent;
import fabric.SdkIntegration;
import model.FeatureView;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.w3c.dom.Document;
import start.HFJson2Pojo;
import start.StartClass;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;


public class BCAgentController {

    private static final Logger log = Logger.getLogger(BCAgentController.class);

    /**
     * Set all the values of the bcAgent
     *
     * @param bcAgent
     * @param documentXML
     * @param bcFeatureList
     * @throws CryptoException
     * @throws InvalidArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void setBCAgentValuesFromXml(BCAgent bcAgent, Document documentXML,
        ArrayList<FeatureView> bcFeatureList)
        throws CryptoException, InvalidArgumentException, IllegalAccessException,
        InstantiationException, ClassNotFoundException, NoSuchMethodException,
        InvocationTargetException {
        // TODO:Spostare configType in una classe a parte come fatto per Heuristic
        String configType = documentXML.getElementsByTagName("configType").item(0).getTextContent();
        bcAgent.setConfigurationType(configType);
        bcAgent.setMyName(bcAgent.getLocalName());
        // This is the local address
        bcAgent.setMyAddress(bcAgent.getName());
        bcAgent.setHfClient(HFClient.createNewInstance());
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        bcAgent.getHfClient().setCryptoSuite(cryptoSuite);
        bcAgent.setFeaturesList(bcFeatureList);
    }

    /**
     * Set all the values of the bcAgent
     *
     * @param bcAgent
     * @throws CryptoException
     * @throws InvalidArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void setBCAgentValues(BCAgent bcAgent)
        throws CryptoException, InvalidArgumentException, IllegalAccessException,
        InstantiationException, ClassNotFoundException, NoSuchMethodException,
        InvocationTargetException {
        // TODO:Spostare configType in una classe a parte come fatto per Heuristic
        HFClient hfClient = HFClient.createNewInstance();
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();



        bcAgent.setMyName(bcAgent.getLocalName());
        // This is the local address
        bcAgent.setMyAddress(bcAgent.getName());
        bcAgent.setHfClient(hfClient);
        bcAgent.getHfClient().setCryptoSuite(cryptoSuite);
        // bcAgent.setFeaturesList(bcFeatureList);
    }


    /**
     * Add the newStructFeature to the BCAgent.featuresList
     *
     * @param newFeatureViewList
     * @return
     */
    public static BCAgent refreshStructFeatureListInAgent(ArrayList<FeatureView> newFeatureViewList,
        BCAgent bcAgent) {
      bcAgent.setFeaturesList(newFeatureViewList);
        return bcAgent;
    }

    /**
     * Add the newStructFeature to the BCAgent.featuresList
     *
     * @param newFeatureView
     * @return
     */
    public static BCAgent loadStructFeatureInAgent(FeatureView newFeatureView,
                                                   BCAgent bcAgent) {
      bcAgent.featuresList.add(newFeatureView);
        return bcAgent;
    }

  public static BCAgent deleteStructFeatureInAgent(String serviceId, BCAgent bcAgent) {

    for (int i = 0; i < bcAgent.featuresList.size(); i++) {
      if (bcAgent.featuresList.get(i).getFeatureId() == serviceId) {
        bcAgent.featuresList.remove(i);
      }
    }
    return bcAgent;
  }

  public static BCAgent updateStructFeatureCostInAgent(String serviceId, BCAgent bcAgent,
      String cost) {

    for (int i = 0; i < bcAgent.featuresList.size(); i++) {
      if (bcAgent.featuresList.get(i).getFeatureId() == serviceId) {
        bcAgent.featuresList.get(i).setCost(cost);
      }
    }
    return bcAgent;
  }

  public static BCAgent updateStructFeatureTimeInAgent(String serviceId, BCAgent bcAgent,
      String time) {

    for (int i = 0; i < bcAgent.featuresList.size(); i++) {
      if (bcAgent.featuresList.get(i).getFeatureId() == serviceId) {
        bcAgent.featuresList.get(i).setTime(time);
      }
    }
    return bcAgent;
  }

  public static BCAgent updateStructFeatureDescriptionInAgent(String serviceId, BCAgent bcAgent,
      String description) {

    for (int i = 0; i < bcAgent.featuresList.size(); i++) {
      if (bcAgent.featuresList.get(i).getFeatureId() == serviceId) {
        bcAgent.featuresList.get(i).setDescription(description);
      }
    }
    return bcAgent;
  }

    /**
     * @param bcAgent
     * @param name
     */
    public static void registerUserAndSetInBcAgent(BCAgent bcAgent, String name) {
        try {

            User fabricUser = SdkIntegration.registerOrGetUserInHF(name);

            HFClient bcHfClient = bcAgent.getHfClient();
            if (bcHfClient == null) {
                throw new NullPointerException(
                    "HFClient of BCAgent object " + bcAgent.getName() + " is null");
            }
            //      Channel hfFeatureChannel, hfTransactionChannel;

            log.info("LoadCert Begin of agent: " + name);

            bcAgent.setUser(fabricUser);
            bcHfClient.setUserContext(bcAgent.getUser());

            // TODO: Spostare questa chiamata
            //            ChannelController.initializeChannels(bcAgent, hfJson2Pojo, bcHfClient);

            log.info("Cert Loaded relative of agent: " + name);

        } catch (Exception e) {
          log.info(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * @param bcAgent
     */
    public static void initializeChannelsAndSetInBcAgent(BCAgent bcAgent) {
        HFClient bcHfClient = bcAgent.getHfClient();
        HFJson2Pojo hfJson2Pojo;
        try {
            hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
            SdkIntegration.initializeChannels(bcAgent, hfJson2Pojo, bcHfClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
