/**
 * this module contains utility functions, so make it open
 */
open module org.korpora.useful {
    requires java.xml;
    requires org.jdom2;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.core;
    requires org.apache.commons.io;
    requires com.fasterxml.jackson.databind;
    requires jakarta.servlet;
    requires org.slf4j;
    exports org.korpora.useful;
}
