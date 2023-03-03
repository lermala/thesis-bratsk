package com.company.bratsk.core.app;

import org.springframework.jmx.export.annotation.*;
import com.haulmont.cuba.core.sys.jmx.JmxBean;

@JmxBean(module = "bratsk", alias = "ExtDeployer")
@ManagedResource(description = "Used to initialize THESIS specific components (processes, reports, filters and so on).")
public interface ExtDeployerMBean {
    @ManagedOperation(description = "Initialize functionality for extended documents (add Card types to procs, add screens to EndorecementList Report, init Filters)")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "password", description = "Password")})
    String initDefault(String password);

    String deployMpaEndorsementProcess();

}