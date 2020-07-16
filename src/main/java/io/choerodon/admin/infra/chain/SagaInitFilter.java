package io.choerodon.admin.infra.chain;

import org.hzero.admin.domain.vo.InitChainContext;
import org.hzero.admin.domain.vo.Service;
import org.hzero.admin.infra.chain.InitChain;
import org.hzero.admin.infra.chain.InitFilter;
import org.hzero.admin.infra.feign.PermissionRefreshService;
import org.hzero.core.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.admin.infra.feign.AsgardClient;
import io.choerodon.core.exception.CommonException;

/**
 * saga初始化filter
 *
 * @author scp
 * @date 2020/6/28 10:40 上午
 */
@Component
public class SagaInitFilter implements InitFilter, Ordered {

    @Autowired
    private AsgardClient asgardClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(SagaInitFilter.class);


    @Override
    public void doFilter(InitChain chain, InitChainContext context) {
        Service service = context.getService();
        ResponseEntity<Void> response = asgardClient.refresh(service.getServiceName());

        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.warn("saga refresh failed, cause: " + (response.getBody() == null ? response.getStatusCodeValue() : response.getBody()));
        }
        chain.initNext(chain, context);
    }

    @Override
    public int getOrder() {
        return 99;
    }
}
