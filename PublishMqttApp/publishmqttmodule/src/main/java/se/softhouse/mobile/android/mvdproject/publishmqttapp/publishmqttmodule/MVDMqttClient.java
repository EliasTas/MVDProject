package se.softhouse.mobile.android.mvdproject.publishmqttapp.publishmqttmodule;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

/**
 * MQTT code.
 * Created by Elias Tas (elias.tas@softhouse.se) on 30/03/15.
 */
class MVDMqttClient implements MqttCallback {

    private static final String TAG = MVDMqttClient.class.getSimpleName();

    private static final String ORG_ID = "1axg2p";
    private static final String TYPE_ID = "mvd";
    private static final String APP_ID = "modcam13";
    private static final String PASSWORD = "t008wVp3bo-ES6b*hL";
    private static final String USERNAME = "use-token-auth";
    private static final String BROKER_URL = String.format("tcp://%s.messaging.internetofthings.ibmcloud.com:1883", ORG_ID);
    private static final String CLIENT_ID = String.format("d:%s:%s:%s", ORG_ID, TYPE_ID, APP_ID);
    private static final String USER_EVENT = "user";
    private static final String PUB_TOPIC = "iot-2/evt/%s/fmt/json";
    private static final String SUB_TOPIC = "iot-2/cmd/%s/fmt/json";
    private static final String DEFAULT_PUB_TOPIC = String.format(PUB_TOPIC, USER_EVENT);
    private static final String DEFAULT_SUB_TOPIC = String.format(SUB_TOPIC, "+");

    private static final String WILL_MESSAGE = "{id:1, message: offline}";

    private MqttClient myClient;
    private MqttConnectOptions connOpt;


    private static final int MVD_QOS = 0; // fire and forget

    // the following two flags control whether this example is a publisher, a subscriber or both
    private static final Boolean subscriber = true;
    private static final Boolean publisher = true;

    /**
     * Create a channel.
     */
    public MVDMqttClient() {

        // Create client
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            myClient = new MqttClient(BROKER_URL, CLIENT_ID, persistence);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // setup topic
        // topics on are in the form <domain>/<topic>
        MqttTopic willTopic = myClient.getTopic(DEFAULT_PUB_TOPIC);

        connOpt = new MqttConnectOptions();
        connOpt.setCleanSession(true);
        connOpt.setKeepAliveInterval(30);
        connOpt.setWill(willTopic, WILL_MESSAGE.getBytes(), MVD_QOS, true); // will(topic,message,qos,retained)
        connOpt.setUserName(USERNAME);
        connOpt.setPassword(PASSWORD.toCharArray());

        // Connect to Broker
        try {
            myClient.setCallback(this);
            myClient.connect(connOpt);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        System.out.println("Connected to " + BROKER_URL);


        // subscribe to topic if subscriber
        if (subscriber) {
            try {
                /* Subscribe in topics. */
                myClient.subscribe(DEFAULT_SUB_TOPIC, MVD_QOS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * connectionLost
     * This callback is invoked upon losing the MQTT connection.
     */
    @Override
    public void connectionLost(Throwable t) {
        System.out.println("Connection lost!");
        // code to reconnect to the broker would go here if desired
    }

    /**
     * deliveryComplete
     * This callback is invoked when a message published by this client
     * is successfully received by the broker.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
        System.out.println("-------------------------------------------------");
        System.out.println("| deliveryComplete callback");
        System.out.println("| Publish completed");
        System.out.println("-------------------------------------------------");
    }

    /**
     * messageArrived
     * This callback is invoked when a message is received on a subscribed topic.
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("-------------------------------------------------");
        System.out.println("| messageArrived callback");
        System.out.println("| Topic:" + topic);
        System.out.println("| Message: " + new String(message.getPayload()));
        System.out.println("-------------------------------------------------");

    }

    /**
     * Send message.
     *
     * @param jsonData to send.
     */
    public void sendMessage(JSONObject jsonData) {

        // publish messages if publisher
        if (publisher) {

            MqttMessage message = new MqttMessage(jsonData.toString().getBytes());
            message.setQos(MVD_QOS);
            message.setRetained(true);

            MqttTopic topic = myClient.getTopic(DEFAULT_PUB_TOPIC);

            // Publish the message
            System.out.println("Publishing to topic \"" + DEFAULT_PUB_TOPIC + "\" qos " + MVD_QOS);
            IMqttDeliveryToken token;
            try {
                // publish message to broker
                token = topic.publish(message);
                // Wait until the message has been delivered to the broker
                token.waitForCompletion();
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // disconnect
        try {
            // wait to ensure subscribed messages are delivered
            if (subscriber) {
                Thread.sleep(2000);
            }

            /* Kill the channel when MVD is not used anymore. */
            if (myClient.isConnected()) {
                myClient.disconnect();
                myClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}