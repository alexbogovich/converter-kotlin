package com.bogovich

enum class NamespaceEnum(val prefix: String, val link: String, val location: String) {
    XSI("xsi", "http://www.w3.org/2001/XMLSchema-instance", "http://www.xbrl.org/2003/xbrl-instance-2003-12-31.xsd"),
    XSD("xsd", "http://www.w3.org/2001/XMLSchema", ""),
    XLINK("xlink", "http://www.w3.org/1999/xlink", ""),
    LINK("link", "http://www.xbrl.org/2003/linkbase", ""),
    XBRLI("xbrli", "http://www.xbrl.org/2003/instance", ""),
    MODEL("model", "http://www.eurofiling.info/xbrl/ext/model", "http://www.eurofiling.info/eu/fr/xbrl/ext/model.xsd"),
    NONNUM("nonnum", "http://www.xbrl.org/dtr/type/non-numeric", "http://www.xbrl.org/dtr/type/nonNumeric-2009-12-16.xsd")
}