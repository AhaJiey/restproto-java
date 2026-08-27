package my.restproto.common.security.core.access.collector;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.restproto.common.security.core.access.AccessRuleSet;
import my.restproto.common.security.core.access.annotations.NoRequired;
import my.restproto.common.security.core.access.annotations.RequireAuthenticated;
import my.restproto.common.security.core.access.annotations.RequirePermission;
import my.restproto.common.security.core.access.model.Endpoint;
import my.restproto.common.security.core.access.model.Rule;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class RuleCollectorFormHandler {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Import(RuleCollectorFormHandler.class)
    public @interface Enable { }

    private final AccessRuleSet accessRuleSet;

    private final ObjectProvider<RequestMappingHandlerMapping> handlerMappingProvider;

    @PostConstruct
    public void collect() {
        RequestMappingHandlerMapping handlerMapping = handlerMappingProvider.getIfAvailable();

        // 无映射表说明并非 Web 环境, 不登记任何规则
        if (handlerMapping == null) {
            log.warn("未找到控制器映射表, 端点规则集为空, 全部请求将被拒绝");
            return;
        }

        handlerMapping.getHandlerMethods().forEach((mappingInfo, handlerMethod) -> {
            // 解析方法上的访问注解, 归一为访问要求
            String required = resolveRequired(handlerMethod);

            // 未标注访问注解则不登记, 请求期查不到规则即被拒绝
            if (required == null) {
                return;
            }

            // 一个控制器方法可映射多个端点, 先展开为端点集合
            Set<Endpoint> endpoints = resolveEndpoint(mappingInfo);

            // 每个端点配上同一份访问要求, 组装为规则后登记
            Set<Rule> rules = endpoints.stream().map(endpoint -> Rule.builder()
                    .endpoint(endpoint)
                    .required(required)
                    .build()
            ).collect(Collectors.toSet());

            rules.forEach(accessRuleSet::add);
        });

        log.info("端点规则收集完成");
    }

    /** 按映射的 HTTP 方法与模板路径展开为端点集合 */
    private Set<Endpoint> resolveEndpoint(RequestMappingInfo mappingInfo) {
        Set<RequestMethod> requestMethods = mappingInfo.getMethodsCondition().getMethods();
        Set<Endpoint> endpoints = new LinkedHashSet<>();

        // 一个控制器方法可映射多路径多方法, 逐一展开为独立规则
        for (String pattern : mappingInfo.getPatternValues()) {

            // 未限定 HTTP 方法即全部方法可达, 逐个方法展开为独立规则
            if (requestMethods.isEmpty()) {
                for (HttpMethod httpMethod : HttpMethod.values()) {
                    endpoints.add(Endpoint.builder()
                            .method(httpMethod)
                            .url(pattern)
                            .build()
                    );
                }
                continue;
            }

            for (RequestMethod requestMethod : requestMethods) {
                endpoints.add(Endpoint.builder()
                        .method(HttpMethod.valueOf(requestMethod.name()))
                        .url(pattern)
                        .build()
                );
            }
        }
        return endpoints;
    }

    /** 解析方法上的访问注解并归一为访问要求, 未标注返回 null */
    private String resolveRequired(HandlerMethod handlerMethod) {

        Annotation[] annotations =  {
                handlerMethod.getMethodAnnotation(NoRequired.class),
                handlerMethod.getMethodAnnotation(RequireAuthenticated.class),
                handlerMethod.getMethodAnnotation(RequirePermission.class)
        };

        List<String> required = Arrays.stream(annotations)
                .filter(Objects::nonNull)
                .map(RequiredHelper::getRequired)
                .toList();

        if (required.isEmpty()) {
            return null;
        }
        if (required.size() == 1) {
            return required.get(0);
        }

        throw new IllegalStateException("%s 出现了 %s, 但要求只能存在一个".formatted(
                handlerMethod.getMethod().getDeclaringClass().getName() + "#" + handlerMethod.getMethod().getName(),
                Arrays.stream(annotations)
                        .map(annotation -> "@" + annotation.annotationType().getSimpleName())
                        .collect(Collectors.joining(","))
        ));
    }
}
