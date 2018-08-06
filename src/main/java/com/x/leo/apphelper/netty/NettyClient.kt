package com.x.leo.apphelper.netty

import com.google.protobuf.AbstractMessage
import com.google.protobuf.MessageLite
import com.x.leo.apphelper.log.xlog.XLog
import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.group.ChannelGroup
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
import io.netty.util.concurrent.DefaultThreadFactory
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor

/**
 * @作者:XLEO
 * @创建日期: 2017/10/19 14:26
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class NettyClient(private val protobuf: MessageLite) {
    private var channels: ChannelGroup
    private var group: NioEventLoopGroup
    private var bootStrap: Bootstrap

    init {
        channels = DefaultChannelGroup(UnorderedThreadPoolEventExecutor(Runtime.getRuntime().availableProcessors() * 2 + 1, DefaultThreadFactory("netty")))
        group = NioEventLoopGroup(0)
        bootStrap = Bootstrap()
                .group(group)
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        ch.pipeline().addLast(ProtobufVarint32FrameDecoder())
                        ch.pipeline().addLast(ProtobufDecoder(protobuf))
                        ch.pipeline().addLast(ProtobufVarint32LengthFieldPrepender())
                        ch.pipeline().addLast(ProtobufEncoder())
                        ch.pipeline().addLast(ProspectorClientHandler())
                    }
                })
    }

    fun sendMessage(message: AbstractMessage, host: String, port: Int) {
        val channel = bootStrap.connect(host, port).sync().channel()
        channels.add(channel)
        channel.writeAndFlush(message).sync()
    }

    fun queryChannelState() {
        XLog.d( "channelSize:" + channels.size, 10)
        channels.forEach {
            XLog.d( "channelName:" + it.toString() + "||channelWritable:" + it.isWritable + "||channelIsOpen:" + it.isOpen + "||channelIsActive:" + it.isActive, 10)
        }
    }

    companion object {
        private var sNettyClient: NettyClient? = null
        @JvmStatic
        fun getInstance(protobuf: MessageLite): NettyClient {
            if (sNettyClient == null) {
                synchronized(NettyClient.javaClass, {
                    if (sNettyClient == null) {
                        sNettyClient = NettyClient(protobuf)
                    }
                })
            }
            return sNettyClient!!
        }
    }
}

class ProspectorClientHandler : ChannelInboundHandlerAdapter() {
    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        super.channelReadComplete(ctx)
        XLog.d("channelReadComplete==channelName:" + ctx?.channel()?.toString(), 10)
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        XLog.d("channelInactive==isActive" + ctx?.channel()?.isActive + "\nChannelName:" + ctx?.channel()?.toString(), 10)
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        super.channelRead(ctx, msg)
        XLog.d("channelRead==msg" + msg?.toString() + "\nChannelName:" + ctx?.channel()?.toString(), 10)
    }

    override fun userEventTriggered(ctx: ChannelHandlerContext?, evt: Any?) {
        super.userEventTriggered(ctx, evt)
        XLog.d("userEventTriggered==event" + evt.toString(), 10)
    }

    override fun handlerAdded(ctx: ChannelHandlerContext?) {
        super.handlerAdded(ctx)
        XLog.d("handlerAdded==currentChannel" + ctx?.channel()?.toString(), 10)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        super.exceptionCaught(ctx, cause)
        XLog.e("catchException==cause" + cause?.message,cause, 10)
    }

    override fun channelUnregistered(ctx: ChannelHandlerContext?) {
        super.channelUnregistered(ctx)
        XLog.d("channelUnregistered==channelName:" + ctx?.channel()?.toString(), 10)
    }

    override fun channelWritabilityChanged(ctx: ChannelHandlerContext?) {
        super.channelWritabilityChanged(ctx)
        XLog.d("channelWritabilityChanged==channelName:" + ctx?.channel()?.toString(), 10)
    }
}