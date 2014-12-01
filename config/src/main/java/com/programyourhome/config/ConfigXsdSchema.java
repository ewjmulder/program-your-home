package com.programyourhome.config;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

public class ConfigXsdSchema {

    private static final String XSD_BASE_PATH = "/com/programyourhome/config/xsd/";

    private static Schema schema;

    public static Schema getSchema() {
        if (schema == null) {
            schema = loadSchema();
        }
        return schema;
    }

    private static Schema loadSchema() {
        final Source sourceServer = getSource(XSD_BASE_PATH + "program-your-home-config-server.xsd");

        final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setResourceResolver(new LSResourceResolver() {
            @Override
            public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, final String systemId, final String baseURI) {
                // The systemId contains the path as it is encountered in the <xsd:include> tag.
                // So if we take the base path of the server XSD, we can find the right file to include.
                // Note: this only suffices if there are no sub-includes in the module files themselves.
                final InputStream resourceAsStream = getStream(XSD_BASE_PATH + systemId);
                return new XsdSchemaIncludeInput(publicId, systemId, resourceAsStream);
            }
        });

        try {
            return factory.newSchema(sourceServer);
        } catch (final SAXException e) {
            throw new IllegalStateException("Exception while loading XSD schema's", e);
        }
    }

    private static StreamSource getSource(final String path) {
        return new StreamSource(getStream(path));
    }

    private static InputStream getStream(final String path) {
        return ConfigXsdSchema.class.getResourceAsStream(path);
    }

}
