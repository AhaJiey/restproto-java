package my.restproto.common.security.permission;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.restproto.common.security.annotations.Permission;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * Permission 启动收集, 扫描 PermissionScan 配置的包下含 Permission 注解方法的类, 注册到注册表
 */
@Slf4j
@RequiredArgsConstructor
public class PermissionCollector implements SmartInitializingSingleton {

    private final List<String> scanPackages;

    private final PermissionCollections collections;

    @Override
    public void afterSingletonsInstantiated() {
        collect();
    }

    /** 遍历扫描包, 将含 Permission 注解方法的类收集到注册表 */
    private void collect() {
        if (CollectionUtils.isEmpty(scanPackages)) {
            log.warn("PermissionScan 未配置扫描包, 跳过 Permission 收集");
            return;
        }
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        // 仅保留含 Permission 注解方法的类
        scanner.addIncludeFilter((reader, factory) -> {
            AnnotationMetadata metadata = reader.getAnnotationMetadata();
            return !metadata.getAnnotatedMethods(Permission.class.getCanonicalName()).isEmpty();
        });

        for (String scanPackage : scanPackages) {
            scanner.findCandidateComponents(scanPackage).forEach(definition -> {
                String beanClassName = Objects.requireNonNull(definition.getBeanClassName());
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, getClass().getClassLoader());
                registerClass(clazz);
            });
        }
    }

    /** 反射遍历声明方法, 注册 Permission 注解 value */
    private void registerClass(Class<?> clazz) {
        ReflectionUtils.doWithMethods(clazz, method -> {
            Permission permission = method.getAnnotation(Permission.class);
            if (permission != null) {
                collections.register(permission.value());
            }
        });
    }
}
