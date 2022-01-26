/**
 *
 */
package com.housekeeping.im.tio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.intf.Packet;
import org.tio.core.stat.IpStat;
import org.tio.utils.json.Json;


public class IpStatListener implements org.tio.core.stat.IpStatListener {

    private static Logger log = LoggerFactory.getLogger(IpStatListener.class);

    public static IpStatListener me = new IpStatListener();

    /**
     *
     */
    private IpStatListener() {
    }

    @Override
    public void onExpired(GroupContext groupContext, IpStat ipStat) {
        if (log.isInfoEnabled()) {
        }
    }

    @Override
    public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect, IpStat ipStat) throws Exception {
        if (log.isInfoEnabled()) {
        }
    }

    @Override
    public void onDecodeError(ChannelContext channelContext, IpStat ipStat) {
        if (log.isInfoEnabled()) {
        }
    }

    @Override
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess, IpStat ipStat) throws Exception {
        if (log.isInfoEnabled()) {
        }
    }

    @Override
    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize, IpStat ipStat) throws Exception {
        if (log.isInfoEnabled()) {
        }
    }

    @Override
    public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes, IpStat ipStat) throws Exception {
        if (log.isInfoEnabled()) {
        }
    }

    @Override
    public void onAfterHandled(ChannelContext channelContext, Packet packet, IpStat ipStat, long cost) throws Exception {
        if (log.isInfoEnabled()) {
        }
    }

}
