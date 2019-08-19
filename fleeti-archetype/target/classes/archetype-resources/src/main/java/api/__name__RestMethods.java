package ${package}.api;

import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/${name}")
@Api(value = "/${name}")
public class ${name}RestMethods
{
    private ${name}Service delegate;

    @Inject
    public ${name}RestMethods(final ${name}Service delegate) {
        this.delegate = delegate;
    }

    @GET
    @ApiOperation(value = "Getting developer name")
    @Path("/dev")
    public String getDeveloperName() {
        return delegate.getDeveloperName();
    }
}
