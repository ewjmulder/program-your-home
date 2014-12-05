package com.programyourhome.common.config;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

@Component
public class ConfigXsdSchema {

    public Schema loadSchema(final String path) {
        final Source sourceServer = this.getSource(path);

        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setResourceResolver(new LSResourceResolver() {
            @Override
            public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
                // The systemId contains the path as it is encountered in the <xsd:include> tag.
                // So if we take the base path of the XSD, we can find the right file to include.
                // Note: this only suffices if there are no sub-includes in the included files themselves.
                final String basePath = path.substring(0, path.lastIndexOf('/'));
                final InputStream resourceAsStream = ConfigXsdSchema.this.getStream(basePath + systemId);
                return new XsdSchemaIncludeInput(publicId, systemId, resourceAsStream);
            }
        });

        try {
            return factory.newSchema(sourceServer);
        } catch (final SAXException e) {
            throw new IllegalStateException("Exception while loading XSD schema's", e);
        }
    }

    private StreamSource getSource(final String path) {
        return new StreamSource(this.getStream(path));
    }

    private InputStream getStream(final String path) {
        return ConfigXsdSchema.class.getResourceAsStream(path);
    }

}
