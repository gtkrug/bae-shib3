/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gfipm.shibboleth.config;

import net.gfipm.shibboleth.dataconnector.GfipmBAEDataConnector;
import net.gfipm.shibboleth.dataconnector.BAEAttributeNameMap;

import java.util.List;
import java.util.Vector;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.ext.spring.util.SpringSupport;
import net.shibboleth.idp.attribute.resolver.spring.dc.AbstractDataConnectorParser;
import net.shibboleth.idp.attribute.IdPAttribute;
import net.shibboleth.idp.attribute.StringAttributeValue;
import net.shibboleth.utilities.java.support.xml.AttributeSupport;
import net.shibboleth.utilities.java.support.xml.ElementSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * Spring Bean Definition Parser for GFIPM Test data connector.
 */
public class GfipmBAEDataConnectorParser extends AbstractDataConnectorParser {

    /** Schema type name. */
    public static final QName TYPE_NAME = new QName(GFIPMNamespaceHandler.NAMESPACE, "BAE");

    /** Local name of attribute. */
    public static final QName ATTRIBUTE_ELEMENT_NAME = new QName(GFIPMNamespaceHandler.NAMESPACE, "Attribute");

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GfipmBAEDataConnectorParser.class);

    /** {@inheritDoc} */
    @Override protected Class<GfipmBAEDataConnector> getNativeBeanClass() {
        return GfipmBAEDataConnector.class;
    }


    /** {@inheritDoc} */
    @Override protected void doV2Parse(@Nonnull final Element config, @Nonnull final ParserContext parserContext,
            @Nonnull final BeanDefinitionBuilder builder) {
        //super.doParse(config, parserContext, builder);
        log.debug("doV2Parse {}", config);

        log.debug("{} Parsing v2 configuration {}", getLogPrefix(), config);

        long timeLimit = 5000;  // Default to 5s

        final String baeURL    = AttributeSupport.getAttributeValue(config, new QName("baeURL"));
        final String subjectId = AttributeSupport.getAttributeValue(config, new QName("subjectId"));
        final String baeEntId  = AttributeSupport.getAttributeValue(config, new QName("baeEntityId"));
        final String myEntId   = AttributeSupport.getAttributeValue(config, new QName("myEntityId"));
        //if ( AttributeSupport.hasAttribute(config, new QName("searchTimeLimit")) ) {
        //   timeLimit = AttributeSupport.getAttribute(config, new QName("searchTimeLimit")).getDateTimeAttributeAsLong();
        //}

        log.debug("Parsing configuration baeURL {}, baeId {}, myId {}, subject attribute {}", baeURL, baeEntId, myEntId, subjectId);

        builder.addPropertyValue("baeURL", baeURL );
        builder.addPropertyValue("subjectId", subjectId);
        builder.addPropertyValue("baeEntityId", baeEntId);
        builder.addPropertyValue("myEntityId", myEntId);
        builder.addPropertyValue("searchTimeLimit", timeLimit);

        // Parsing Attributes
        List<BAEAttributeNameMap> attributes = parseAttributes(config);
        log.debug("Setting the following attributes for BAE plugin: {}", attributes);
        builder.addPropertyValue("baeAttributes", attributes);

        // Parsing Certificates
        final List<Element> trustElements = ElementSupport.getChildElements(config, new QName(GFIPMNamespaceHandler.NAMESPACE, "TrustCredential"));
        if (trustElements != null && !trustElements.isEmpty()) {
            if (trustElements.size() > 1) {
               log.warn("Too many TrustCredential elements in {}; only the first has been consulted",
                       parserContext.getReaderContext().getResource().getDescription());
            }
            builder.addPropertyValue("trustCredential", SpringSupport.parseCustomElements(trustElements, parserContext).get(0));
        } else {
            log.warn("No TrustCredential element in {}; this is required.", parserContext.getReaderContext().getResource().getDescription());
        }

        final List<Element> authElements = ElementSupport.getChildElements(config, new QName(GFIPMNamespaceHandler.NAMESPACE, "AuthenticationCredential"));

        if (authElements != null && !authElements.isEmpty()) {
            if (authElements.size() > 1) {
                 log.warn("Too many AuthenticationCredential elements in {}; only the first has been consulted", parserContext.getReaderContext().getResource().getDescription());
            }
            builder.addPropertyValue("authCredential", SpringSupport.parseCustomElements(authElements, parserContext).get(0));
        } else {
           log.warn("No AuthenticationCredential element in {}; this is required.", parserContext.getReaderContext().getResource().getDescription());
        }
    }

    /**
     * Parse attribute requirements
     *
     * @param elements DOM elements of type <code>Attribute</code>
     *
     * @return the attributes
     */
    protected List<BAEAttributeNameMap> parseAttributes(Element config) {
        List<Element> elements = ElementSupport.getChildElements (config, ATTRIBUTE_ELEMENT_NAME);
        
        if (elements == null || elements.size() == 0) {
            return null;
        }
        List<BAEAttributeNameMap> mapAttributes = new Vector<BAEAttributeNameMap>();
        for (Element ele : elements) {
            BAEAttributeNameMap mapAttribute = new BAEAttributeNameMap();
            mapAttribute.QueryName  = AttributeSupport.getAttributeValue(ele, new QName("QueryName")); 
            mapAttribute.ReturnName = AttributeSupport.getAttributeValue(ele, new QName("ReturnName"));
            log.debug("BAE Attribute " + mapAttribute.QueryName + " will be returned as local attribute " + mapAttribute.ReturnName);
            mapAttributes.add(mapAttribute);
        }
        return mapAttributes;
    }
}
