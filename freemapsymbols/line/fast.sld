<?xml version="1.0" encoding="UTF-8"?>
<sld:UserStyle xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml">
  <sld:Name>default1</sld:Name>
  <sld:Title>wikisquare.de</sld:Title>
  <sld:Abstract>One-pixel lines are rendered fastest!</sld:Abstract>
  <sld:FeatureTypeStyle>
    <sld:Name>name</sld:Name>
    <sld:Title>title</sld:Title>
    <sld:Abstract>abstract</sld:Abstract>
    <sld:FeatureTypeName>Feature</sld:FeatureTypeName>
    <sld:SemanticTypeIdentifier>generic:geometry</sld:SemanticTypeIdentifier>
    <sld:Rule>
      <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
      <sld:LineSymbolizer>
        <sld:Geometry>
          <sld:PropertyName>the_geom</sld:PropertyName>
        </sld:Geometry>
        <sld:Stroke>
          <sld:CssParameter name="stroke">
            <ogc:Literal>#000000</ogc:Literal>
          </sld:CssParameter>
          <sld:CssParameter name="stroke-linecap">
            <ogc:Literal>mitre</ogc:Literal>
          </sld:CssParameter>
          <sld:CssParameter name="stroke-linejoin">
            <ogc:Literal>miter</ogc:Literal>
          </sld:CssParameter>
          <sld:CssParameter name="stroke-opacity">
            <ogc:Literal>1.0</ogc:Literal>
          </sld:CssParameter>
          <sld:CssParameter name="stroke-width">
            <ogc:Literal>1.0</ogc:Literal>
          </sld:CssParameter>
          <sld:CssParameter name="stroke-dashoffset">
            <ogc:Literal>0.0</ogc:Literal>
          </sld:CssParameter>
        </sld:Stroke>
      </sld:LineSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
</sld:UserStyle>
