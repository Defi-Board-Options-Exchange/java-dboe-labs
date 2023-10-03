package com.ngontro86.component.testing

import org.springframework.context.annotation.AnnotationConfigApplicationContext

import static com.ngontro86.utils.ResourcesUtils.lines

class ComponentEnv {

    private AnnotationConfigApplicationContext context

    private ComponentEnv() {
        context = new AnnotationConfigApplicationContext()
        lines('defaultEnvs').each {
            context.scan(it)
        }
    }

    private ComponentEnv initComps(Collection<Class> classes) {
        classes.each {
            this.context.register(it)
        }
        context.refresh()
        return this
    }

    static ComponentEnv env(Collection<Class> classes) {
        return new ComponentEnv().initComps(classes)
    }

    def <T> T component(Class<T> classObj) {
        return context.getBean(classObj)
    }

}
