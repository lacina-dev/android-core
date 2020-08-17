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


/**
 * A simple {@link Publisher} {@link NodeMain}.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
public class TalkerMapEdit extends AbstractNodeMain {
    Publisher<std_msgs.String> publisher;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android/mapedit");
    }

    // Note that you shouldn't call this before onStart is called;
    // check that publisher is not null before using it!
    public void publish(String message) {
        std_msgs.String toPublish = publisher.newMessage();
        toPublish.setData(message);
        publisher.publish(toPublish);
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher("/navi_manager/edit_map", std_msgs.String._TYPE);


    }
}

