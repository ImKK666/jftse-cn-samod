package com.jftse.emulator.server.networking;
import com.aliyun.ecs20140526.Client;
import com.aliyun.tea.*;
import com.aliyun.ecs20140526.*;
import com.aliyun.ecs20140526.models.*;
import com.aliyun.teaopenapi.*;
import com.aliyun.teaopenapi.models.*;
import com.aliyun.teaconsole.*;
import com.aliyun.darabonba.env.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AliyunSecurity {
    private static String regionId="cn-guangzhou";
    //private static String securityGroupId="sg-*";
    private static String securityGroupId="sg-*";
    public static com.aliyun.ecs20140526.Client initialization() throws Exception {
        Config config = new Config();
        // 您的AccessKey ID
        //config.accessKeyId = com.aliyun.darabonba.env.EnvClient.getEnv("LTAI5tN1r7dcXxSXL3ec1oZE");
        config.setAccessKeyId("*");
        // 您的AccessKey Secret
        //config.accessKeySecret = com.aliyun.darabonba.env.EnvClient.getEnv("Bp29l1dotUWxl5RDE01cUeNLcPkGoH");
        config.setAccessKeySecret("*");
        // 您的可用区ID
        config.regionId = AliyunSecurity.regionId;
        return new com.aliyun.ecs20140526.Client(config);
    }

    public static void authorizeSecurityGroup(String sourceCidrIp) throws Exception {
        AuthorizeSecurityGroupRequest req = new AuthorizeSecurityGroupRequest();
        // 目标安全组地域ID。
        req.regionId = AliyunSecurity.regionId;
        // 目标安全组ID。
        req.securityGroupId = AliyunSecurity.securityGroupId;
        // 传输层协议。取值大小写敏感。取值范围：tcp udp icmp gre all：支持所有协议。
        req.ipProtocol = "all";
        // SecurityGroupId方开放的传输层协议相关的端口范围。取值范围。
        // TCP/UDP协议：取值范围为1~65535。使用斜线（/）隔开起始端口和终止端口。正确示范：1/200；错误示范：200/1。
        // ICMP协议：-1/-1。
        // GRE协议：-1/-1。
        // IpProtocol取值为all：-1/-1。
        req.portRange = "-1/-1";
        // 网卡类型。取值范围：
        // internet：公网网卡。
        // intranet：内网网卡。
        req.nicType = "intranet";
        // 设置访问权限。取值范围：
        // accept（默认）：接受访问。
        // drop：拒绝访问，不返回拒绝信息。
        req.policy = "drop";
        // 安全组规则优先级。取值范围：1~100。
        req.priority = "1";
        // 源端IPv4 CIDR地址段。支持CIDR格式和IPv4格式的IP地址范围。
        // 需要设置访问权限的源端安全组ID。至少设置一项SourceGroupId或者SourceCidrIp参数。
        // 如果指定了SourceGroupId没有指定参数SourceCidrIp，则参数NicType取值只能为intranet。
        // 如果同时指定了SourceGroupId和SourceCidrIp，则默认以SourceCidrIp为准。
        req.description="API自动策略";
        req.sourceCidrIp = sourceCidrIp+"/32";
        try {
            com.aliyun.ecs20140526.Client client=AliyunSecurity.initialization();
            client.authorizeSecurityGroup(req);
            log.info(sourceCidrIp+" 已在阿里云安全组禁止入方向访问");
        } catch (TeaException error) {
            log.error(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.error(error.message);
        }
    }

    public static void revokeSecurityGroup(String sourceCidrIp) throws Exception {
        RevokeSecurityGroupRequest req = new RevokeSecurityGroupRequest();
        // 目标安全组地域ID。
        req.regionId = AliyunSecurity.regionId;
        // 目标安全组ID。
        req.securityGroupId = AliyunSecurity.securityGroupId;
        // 传输层协议。取值大小写敏感。取值范围：tcp udp icmp gre all：支持所有协议。
        req.ipProtocol = "all";
        // SecurityGroupId方开放的传输层协议相关的端口范围。取值范围。
        // TCP/UDP协议：取值范围为1~65535。使用斜线（/）隔开起始端口和终止端口。正确示范：1/200；错误示范：200/1。
        // ICMP协议：-1/-1。
        // GRE协议：-1/-1。
        // IpProtocol取值为all：-1/-1。
        req.portRange = "-1/-1";
        // 设置访问权限。取值范围：
        // accept（默认）：接受访问。
        // drop：拒绝访问，不返回拒绝信息。
        req.policy = "drop";
        // 源端IPv4 CIDR地址段。支持CIDR格式和IPv4格式的IP地址范围。
        // 需要设置访问权限的源端安全组ID。至少设置一项SourceGroupId或者SourceCidrIp参数。
        // 如果指定了SourceGroupId没有指定参数SourceCidrIp，则参数NicType取值只能为intranet。
        // 如果同时指定了SourceGroupId和SourceCidrIp，则默认以SourceCidrIp为准。
        req.description="API自动策略";
        req.sourceCidrIp = sourceCidrIp+"/32";
        try {
            com.aliyun.ecs20140526.Client client=AliyunSecurity.initialization();
            client.revokeSecurityGroup(req);
            log.info(sourceCidrIp+" 已解除阿里云安全组访问限制");
        } catch (TeaException error) {
            log.error(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.error(error.message);
        }
    }

}
