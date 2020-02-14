module org.korpora.utilities {
    requires java.xml;
    requires javax.servlet.api;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires jdom2;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    exports org.korpora.useful;
}