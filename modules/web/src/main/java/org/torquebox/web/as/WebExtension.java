package org.torquebox.web.as;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;

import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.parsing.ExtensionParsingContext;
import org.jboss.as.controller.registry.ModelNodeRegistration;
import org.jboss.logging.Logger;

public class WebExtension implements Extension {

    @Override
    public void initialize(ExtensionContext context) {
        log.info( "Initializing TorqueBox Web Subsystem" );
        final SubsystemRegistration registration = context.registerSubsystem( SUBSYSTEM_NAME );
        final ModelNodeRegistration subsystem = registration.registerSubsystemModel( WebSubsystemProviders.SUBSYSTEM );

        subsystem.registerOperationHandler( ADD,
                WebSubsystemAdd.ADD_INSTANCE,
                WebSubsystemProviders.SUBSYSTEM_ADD,
                false );
    }

    @Override
    public void initializeParsers(ExtensionParsingContext context) {
        context.setSubsystemXmlMapping(Namespace.CURRENT.getUriString(), WebSubsystemParser.getInstance());
    }
    
    
    public static final String SUBSYSTEM_NAME = "torquebox-web";
    static final Logger log = Logger.getLogger( "org.torquebox.web.as" );


}
