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

package org.ros.android.android_vitulus_control;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import std_msgs.Time;


/**
 * A simple {@link Publisher} {@link NodeMain}.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
public class TalkerGoalCancel extends AbstractNodeMain {
    Publisher<actionlib_msgs.GoalID> publisher;
//    final Publisher<actionlib_msgs.GoalID> publisher = node.newPublisher("chatter", actionlib_msgs.GoalID._TYPE);

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android/goalCancel");
    }

    // Note that you shouldn't call this before onStart is called;
    // check that publisher is not null before using it!
    public void publish(String message) {
        actionlib_msgs.GoalID toPublish = publisher.newMessage();
        toPublish.setId(message);

        org.ros.message.Time time = org.ros.message.Time.fromNano(0);
        toPublish.setStamp(time);
        publisher.publish(toPublish);
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher("/move_base/cancel", actionlib_msgs.GoalID._TYPE);


    }
}

