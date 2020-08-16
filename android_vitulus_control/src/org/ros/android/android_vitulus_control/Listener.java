package org.ros.android.android_vitulus_control;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import org.ros.android.MessageCallable;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;


import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import info.androidhive.fontawesome.FontTextView;
import std_msgs.String;



/**
 * A simple {@link Subscriber} {@link NodeMain}.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */

/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */



/**
 * @author damonkohler@google.com (Damon Kohler)
 */




public class Listener extends AbstractNodeMain {

    public java.lang.String colorState = "#000000";

    private FontTextView fontWiewiconups;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android/iconups");
    }

    public java.lang.String getText() {
        return colorState;
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final Log log = connectedNode.getLog();
        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("/ups_manager/colorups", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                android.util.Log.d("vitulus", java.lang.String.valueOf(message.getData()));
                colorState = java.lang.String.valueOf(message.getData());
//
//                fontWiewiconups = findViewById(R.id.iconups);
//                fontWiewiconups.setTextColor(Color.parseColor(colorState));


            }
        });
    }
}