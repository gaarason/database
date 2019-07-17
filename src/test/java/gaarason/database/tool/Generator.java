package gaarason.database.tool;

import gaarason.database.eloquent.Model;
import gaarason.database.generator.Manager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class Generator extends Manager {

    @Resource
    ToolModel toolModel;

    @Override
    public Model getModel() {
        return toolModel;
    }
}


