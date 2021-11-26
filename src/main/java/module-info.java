module utilities {
    requires javax.servlet.api;
    requires java.xml;
    requires jdom2;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.core;
    requires org.apache.commons.io;
    requires com.fasterxml.jackson.databind;
    opens org.korpora.useful;
}