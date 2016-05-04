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

import net.gfipm.shibboleth.dataconnector.GfipmTestDataConnector;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.idp.attribute.resolver.spring.dc.impl.AbstractDataConnectorParser;
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
public class GfipmTestDataConnectorParser extends AbstractDataConnectorParser {

    /** Schema type name. */
    public static final QName TYPE_NAME = new QName(GFIPMNamespaceHandler.NAMESPACE, "Test");

    /** Local name of attribute. */
    public static final QName ATTRIBUTE_ELEMENT_NAME = new QName(GFIPMNamespaceHandler.NAMESPACE, "Attribute");

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(GfipmTestDataConnectorParser.class);

    /** {@inheritDoc} */
    @Override protected Class<GfipmTestDataConnector> getNativeBeanClass() {
        return GfipmTestDataConnector.class;
    }


    /** {@inheritDoc} */
    @Override protected void doV2Parse(@Nonnull final Element config, @Nonnull final ParserContext parserContext,
            @Nonnull final BeanDefinitionBuilder builder) {
        //super.doParse(config, parserContext, builder);
        log.debug("doV2Parse {}", config);

        final String userPath = AttributeSupport.getAttributeValue(config, new QName("pathToAttributeFiles"));
        final String uidAttr  = AttributeSupport.getAttributeValue(config, new QName("uidAttribute"));

        log.debug("Parsing configuration Path {}, Attribute {}", userPath, uidAttr);

        builder.addPropertyValue("pathToAttributeFiles", userPath);
        builder.addPropertyValue("uidAttribute", uidAttr);
    }

}
