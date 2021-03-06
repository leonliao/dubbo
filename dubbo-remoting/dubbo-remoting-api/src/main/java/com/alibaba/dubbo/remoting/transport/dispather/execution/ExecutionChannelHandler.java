/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.remoting.transport.dispather.execution;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.remoting.Channel;
import com.alibaba.dubbo.remoting.ChannelHandler;
import com.alibaba.dubbo.remoting.RemotingException;
import com.alibaba.dubbo.remoting.exchange.Request;
import com.alibaba.dubbo.remoting.transport.dispather.ChannelEventRunnable;
import com.alibaba.dubbo.remoting.transport.dispather.WrappedChannelHandler;
import com.alibaba.dubbo.remoting.transport.dispather.ChannelEventRunnable.ChannelState;

public class ExecutionChannelHandler extends WrappedChannelHandler {
    
    public ExecutionChannelHandler(ChannelHandler handler, URL url) {
        super(handler, url);
    }

    public void connected(Channel channel) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CONNECTED));
    }

    public void disconnected(Channel channel) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.DISCONNECTED));
    }

    public void received(Channel channel, Object message) throws RemotingException {
      //FIXME 包的依赖顺序有问题
        if (message instanceof Request && ((Request)message).isEvent()){
           super.received(channel, message);
           return;
        }
        executor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.RECEIVED, message));
    }

    public void caught(Channel channel, Throwable exception) throws RemotingException {
        executor.execute(new ChannelEventRunnable(channel, handler ,ChannelState.CAUGHT, exception));
    }

}