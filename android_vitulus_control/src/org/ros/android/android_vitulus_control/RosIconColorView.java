package org.ros.android.android_vitulus_control;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;


import org.ros.android.MessageCallable;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;
import info.androidhive.fontawesome.FontTextView;


public class RosIconColorView<T>  extends info.androidhive.fontawesome.FontTextView implements NodeMain {

    private java.lang.String topicName;
    private java.lang.String messageType;
    private MessageCallable<String, T> callable;
    private String msg;

    public RosIconColorView(Context context) {
        super(context);
    }

    public RosIconColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RosIconColorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTopicName(java.lang.String topicName) {
        this.topicName = topicName;
    }

    public void setMessageType(java.lang.String messageType) {
        this.messageType = messageType;
    }

    public void setMessageToStringCallable(MessageCallable<String, T> callable) {
        this.callable = callable;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android/ros_icon_color_view");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<T> subscriber = connectedNode.newSubscriber(topicName, messageType);
        subscriber.addMessageListener(new MessageListener<T>() {
            @Override
            public void onNewMessage(final T message) {
                if (callable != null) {
                    post(new Runnable() {
                        @Override
                        public void run() {
//                            setText(callable.call(message));
                            //setTextColor(Color.parseColor(callable.call(message)));
                            msg = callable.call(message);
                            //setTextColor();
                        }
                    });
                } else {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            //setTextColor(Color.parseColor(message.toString()));
                            msg = message.toString();
                            //setText(message.toString());
                        }
                    });
                }
                postInvalidate();
            }
        });
    }

    @Override
    public void onShutdown(Node node) {
    }

    @Override
    public void onShutdownComplete(Node node) {
    }

    @Override
    public void onError(Node node, Throwable throwable) {
    }
}
