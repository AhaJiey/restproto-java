package my.restproto.common.security.permission;

import my.restproto.common.security.annotations.PermissionScan;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 读取标注类上 PermissionScan 的 basePackages, 注册 PermissionCollector Bean
 */
public class PermissionScanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(PermissionScan.class.getName());
        if (attributes == null) {
            return;
        }

        String[] basePackages = (String[]) attributes.get("basePackages");

        List<String> scanPackages = Arrays.stream(basePackages).toList();

        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(PermissionCollector.class);

        builder.addConstructorArgValue(scanPackages);
        builder.addConstructorArgReference(StringUtils.uncapitalize(PermissionCollections.class.getSimpleName()));

        registry.registerBeanDefinition(
                StringUtils.uncapitalize(PermissionCollector.class.getSimpleName()), builder.getBeanDefinition());
    }
}
