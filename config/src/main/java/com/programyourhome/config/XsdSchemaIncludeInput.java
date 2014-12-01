package com.programyourhome.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.ls.LSInput;

public class XsdSchemaIncludeInput implements LSInput {

    private String publicId;
    private String systemId;
    private BufferedInputStream inputStream;

    public XsdSchemaIncludeInput(final String publicId, final String systemId, final InputStream inputStream) {
        this.publicId = publicId;
        this.systemId = systemId;
        this.inputStream = new BufferedInputStream(inputStream);
    }

    @Override
    public String getPublicId() {
        return this.publicId;
    }

    @Override
    public void setPublicId(final String publicId) {
        this.publicId = publicId;
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    @Override
    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }

    public BufferedInputStream getInputStream() {
        return this.inputStream;
    }

    public void setInputStream(final BufferedInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public String getEncoding() {
        return "UTF-8";
    }

    @Override
    public String getStringData() {
        try {
            return IOUtils.toString(this.inputStream);
        } catch (final IOException e) {
            throw new IllegalArgumentException("IOException while reading config.", e);
        }
    }

    /*
     * All methods below have no actual implementation, but are necessary to implement the LSInput interface.
     */

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    public boolean getCertifiedText() {
        return false;
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public void setBaseURI(final String baseURI) {
    }

    @Override
    public void setByteStream(final InputStream byteStream) {
    }

    @Override
    public void setCertifiedText(final boolean certifiedText) {
    }

    @Override
    public void setCharacterStream(final Reader characterStream) {
    }

    @Override
    public void setEncoding(final String encoding) {
    }

    @Override
    public void setStringData(final String stringData) {
    }

}
