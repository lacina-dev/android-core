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


public class RosIconView<T>  extends FontTextView implements NodeMain {

    private String topicName;
    private String messageType;
    private MessageCallable<String, T> callable;

    public RosIconView(Context context) {
        super(context);
    }

    public RosIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RosIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setMessageToStringCallable(MessageCallable<String, T> callable) {
        this.callable = callable;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android/ros_icon_view");
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
                            setText(callable.call(message));
                            //setTextColor();
                        }
                    });
                } else {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            setText(message.toString());
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
