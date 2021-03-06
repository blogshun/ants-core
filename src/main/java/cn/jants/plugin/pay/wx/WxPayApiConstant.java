package cn.jants.plugin.pay.wx;

/**
 * @author MrShun
 * @version 1.0
 */
public interface WxPayApiConstant {

    /**
     * 支付订单统一订单接口
     * POST 发送xml数据
     */
    String UNIFIED_ORDER_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 查询订单接口
     * POST 发送xml数据
     */
    String UNIFIED_ORDER_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     * 关闭订单接口
     * POST 发送xml数据
     */
    String CLOSE_ORDER_API = "https://api.mch.weixin.qq.com/pay/closeorder";

    /**
     * 退款订单查询
     * POST 发送xml数据
     */
    String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

    /**
     * 申请退款，需要双向证书
     * POST 发送xml数据
     */
    String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * 企业付款到零钱，需要双向证书
     * POST 发送xml数据
     */
    String WITHDRAWALS_API = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
}
