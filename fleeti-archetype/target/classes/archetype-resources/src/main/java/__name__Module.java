package ${package};

import com.google.inject.AbstractModule;
import ${package}.api.${name}Service;
import ${package}.api.impl.${name}ServiceImpl;

public class ${name}Module
    extends AbstractModule
{
    @Override
    protected void configure() {
        bind(${name}Service.class).to(${name}ServiceImpl.class);
    }
}
