<?xml version="1.0" encoding="UTF-8"?>
<sld:UserStyle xmlns="http://www.opengis.net/sld" xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml">
  <sld:Name>AtlasStyler v1.6-r201011140149</sld:Name>
  <sld:Title/>
  <sld:FeatureTypeStyle>
    <sld:Name>QUANTITIES_COLORIZED_POINT:VALUE#HABITANTS:NORM#null:METHOD#MANUAL:PALETTE#PuBuGn</sld:Name>
    <sld:FeatureTypeName>waypoints_wgs_84</sld:FeatureTypeName>
    <sld:Rule>
      <sld:Name>AS: 1/4 GraduatedColorPointRuleList</sld:Name>
      <sld:Title>de{&lt; 200}en{&lt; 200}</sld:Title>
      <ogc:Filter>
        <ogc:And>
          <ogc:Not>
            <ogc:PropertyIsNull>
              <ogc:PropertyName>HABITANTS</ogc:PropertyName>
            </ogc:PropertyIsNull>
          </ogc:Not>
          <ogc:PropertyIsBetween>
            <ogc:PropertyName>HABITANTS</ogc:PropertyName>
            <ogc:LowerBoundary>
              <ogc:Literal>129.0</ogc:Literal>
            </ogc:LowerBoundary>
            <ogc:UpperBoundary>
              <ogc:Literal>200.0</ogc:Literal>
            </ogc:UpperBoundary>
          </ogc:PropertyIsBetween>
        </ogc:And>
      </ogc:Filter>
      <sld:MinScaleDenominator>4.9E-324</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
      <sld:PointSymbolizer>
        <sld:Geometry>
          <ogc:PropertyName>the_geom</ogc:PropertyName>
        </sld:Geometry>
        <sld:Graphic>
          <sld:Mark>
            <sld:Fill>
              <sld:CssParameter name="fill">#FBFAFB</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke/>
          </sld:Mark>
        </sld:Graphic>
      </sld:PointSymbolizer>
    </sld:Rule>
    <sld:Rule>
      <sld:Name>AS: 2/4 GraduatedColorPointRuleList</sld:Name>
      <sld:Title>de{200 - 300}en{200 - 300}</sld:Title>
      <ogc:Filter>
        <ogc:And>
          <ogc:Not>
            <ogc:PropertyIsNull>
              <ogc:PropertyName>HABITANTS</ogc:PropertyName>
            </ogc:PropertyIsNull>
          </ogc:Not>
          <ogc:PropertyIsBetween>
            <ogc:PropertyName>HABITANTS</ogc:PropertyName>
            <ogc:LowerBoundary>
              <ogc:Literal>200.0</ogc:Literal>
            </ogc:LowerBoundary>
            <ogc:UpperBoundary>
              <ogc:Literal>300.0</ogc:Literal>
            </ogc:UpperBoundary>
          </ogc:PropertyIsBetween>
        </ogc:And>
      </ogc:Filter>
      <sld:MinScaleDenominator>4.9E-324</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
      <sld:PointSymbolizer>
        <sld:Geometry>
          <ogc:PropertyName>the_geom</ogc:PropertyName>
        </sld:Geometry>
        <sld:Graphic>
          <sld:Mark>
            <sld:Fill>
              <sld:CssParameter name="fill">#BDC9E1</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke/>
          </sld:Mark>
        </sld:Graphic>
      </sld:PointSymbolizer>
    </sld:Rule>
    <sld:Rule>
      <sld:Name>AS: 3/4 GraduatedColorPointRuleList</sld:Name>
      <sld:Title>de{300 - 400}en{300 - 400}</sld:Title>
      <ogc:Filter>
        <ogc:And>
          <ogc:Not>
            <ogc:PropertyIsNull>
              <ogc:PropertyName>HABITANTS</ogc:PropertyName>
            </ogc:PropertyIsNull>
          </ogc:Not>
          <ogc:PropertyIsBetween>
            <ogc:PropertyName>HABITANTS</ogc:PropertyName>
            <ogc:LowerBoundary>
              <ogc:Literal>300.0</ogc:Literal>
            </ogc:LowerBoundary>
            <ogc:UpperBoundary>
              <ogc:Literal>400.0</ogc:Literal>
            </ogc:UpperBoundary>
          </ogc:PropertyIsBetween>
        </ogc:And>
      </ogc:Filter>
      <sld:MinScaleDenominator>4.9E-324</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
      <sld:PointSymbolizer>
        <sld:Geometry>
          <ogc:PropertyName>the_geom</ogc:PropertyName>
        </sld:Geometry>
        <sld:Graphic>
          <sld:Mark>
            <sld:Fill>
              <sld:CssParameter name="fill">#5EA0D0</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke/>
          </sld:Mark>
        </sld:Graphic>
      </sld:PointSymbolizer>
    </sld:Rule>
    <sld:Rule>
      <sld:Name>AS: 4/4 GraduatedColorPointRuleList</sld:Name>
      <sld:Title>de{400 - 592}en{400 - 592}</sld:Title>
      <ogc:Filter>
        <ogc:And>
          <ogc:Not>
            <ogc:PropertyIsNull>
              <ogc:PropertyName>HABITANTS</ogc:PropertyName>
            </ogc:PropertyIsNull>
          </ogc:Not>
          <ogc:PropertyIsBetween>
            <ogc:PropertyName>HABITANTS</ogc:PropertyName>
            <ogc:LowerBoundary>
              <ogc:Literal>400.0</ogc:Literal>
            </ogc:LowerBoundary>
            <ogc:UpperBoundary>
              <ogc:Literal>592.0</ogc:Literal>
            </ogc:UpperBoundary>
          </ogc:PropertyIsBetween>
        </ogc:And>
      </ogc:Filter>
      <sld:MinScaleDenominator>4.9E-324</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
      <sld:PointSymbolizer>
        <sld:Geometry>
          <ogc:PropertyName>the_geom</ogc:PropertyName>
        </sld:Geometry>
        <sld:Graphic>
          <sld:Mark>
            <sld:Fill>
              <sld:CssParameter name="fill">#01747C</sld:CssParameter>
            </sld:Fill>
            <sld:Stroke/>
          </sld:Mark>
        </sld:Graphic>
      </sld:PointSymbolizer>
    </sld:Rule>
    <sld:Rule>
      <sld:PointSymbolizer>
        <sld:Geometry>
          <ogc:PropertyName>the_geom</ogc:PropertyName>
        </sld:Geometry>
        <sld:Graphic>
          <sld:ExternalGraphic>
            <sld:OnlineResource xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple" xlink:href="http://chart?cht=bvg&amp;chl=male|female&amp;chd=t:${100 * 2 / (2 + 3)},${100 * 3 / (2 + 3)}&amp;chs=200x100&amp;chf=bg,s,FFFFFF00"/>
            <sld:Format>application/chart</sld:Format>
          </sld:ExternalGraphic>
        </sld:Graphic>
      </sld:PointSymbolizer>
    </sld:Rule>
    <sld:Rule>
      <sld:Name>NODATA_RULE_HIDE_IN_LEGEND</sld:Name>
      <sld:Title>de{k.A.}en{missing data}</sld:Title>
      <ogc:Filter>
        <ogc:PropertyIsNull>
          <ogc:PropertyName>HABITANTS</ogc:PropertyName>
        </ogc:PropertyIsNull>
      </ogc:Filter>
      <sld:MinScaleDenominator>4.9E-324</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
      <sld:PointSymbolizer>
        <sld:Geometry>
          <ogc:PropertyName>the_geom</ogc:PropertyName>
        </sld:Geometry>
        <sld:Graphic>
          <sld:Mark>
            <sld:WellKnownName>circle</sld:WellKnownName>
            <sld:Fill>
              <sld:CssParameter name="fill">#FFFFFF</sld:CssParameter>
            </sld:Fill>
          </sld:Mark>
          <sld:Size>
            <ogc:Literal>8.0</ogc:Literal>
          </sld:Size>
        </sld:Graphic>
      </sld:PointSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
  <sld:FeatureTypeStyle>
    <sld:Name>TEXT_LABEL</sld:Name>
    <sld:FeatureTypeName>Feature</sld:FeatureTypeName>
    <sld:Rule>
      <sld:Name>DEFAULT</sld:Name>
      <ogc:Filter>
        <ogc:And>
          <ogc:PropertyIsEqualTo>
            <ogc:Literal>ALL_LABEL_CLASSES_ENABLED</ogc:Literal>
            <ogc:Literal>ALL_LABEL_CLASSES_ENABLED</ogc:Literal>
          </ogc:PropertyIsEqualTo>
          <ogc:And>
            <ogc:PropertyIsEqualTo>
              <ogc:Literal>LABEL_CLASS_ENABLED</ogc:Literal>
              <ogc:Literal>LABEL_CLASS_ENABLED</ogc:Literal>
            </ogc:PropertyIsEqualTo>
            <ogc:PropertyIsEqualTo>
              <ogc:Literal>1</ogc:Literal>
              <ogc:Literal>1</ogc:Literal>
            </ogc:PropertyIsEqualTo>
          </ogc:And>
        </ogc:And>
      </ogc:Filter>
      <sld:MinScaleDenominator>1.0</sld:MinScaleDenominator>
      <sld:MaxScaleDenominator>1.7976931348623157E308</sld:MaxScaleDenominator>
      <sld:TextSymbolizer>
        <sld:Geometry>
          <ogc:PropertyName>the_geom</ogc:PropertyName>
        </sld:Geometry>
        <sld:Label>
          <ogc:PropertyName>VILLAGE</ogc:PropertyName>
        </sld:Label>
        <sld:Font>
          <sld:CssParameter name="font-family">Arial</sld:CssParameter>
          <sld:CssParameter name="font-size">12.0</sld:CssParameter>
          <sld:CssParameter name="font-style">normal</sld:CssParameter>
          <sld:CssParameter name="font-weight">normal</sld:CssParameter>
        </sld:Font>
        <sld:LabelPlacement>
          <sld:PointPlacement>
            <sld:AnchorPoint>
              <sld:AnchorPointX>
                <ogc:Literal>0.0</ogc:Literal>
              </sld:AnchorPointX>
              <sld:AnchorPointY>
                <ogc:Literal>0.0</ogc:Literal>
              </sld:AnchorPointY>
            </sld:AnchorPoint>
            <sld:Displacement>
              <sld:DisplacementX>
                <ogc:Literal>14.0</ogc:Literal>
              </sld:DisplacementX>
              <sld:DisplacementY>
                <ogc:Literal>-6.0</ogc:Literal>
              </sld:DisplacementY>
            </sld:Displacement>
            <sld:Rotation>
              <ogc:Literal>0</ogc:Literal>
            </sld:Rotation>
          </sld:PointPlacement>
        </sld:LabelPlacement>
        <sld:Halo>
          <sld:Radius>
            <ogc:Literal>1.0</ogc:Literal>
          </sld:Radius>
          <sld:Fill>
            <sld:CssParameter name="fill">#FFFFFF</sld:CssParameter>
            <sld:CssParameter name="fill-opacity">0.5</sld:CssParameter>
          </sld:Fill>
        </sld:Halo>
        <sld:Fill>
          <sld:CssParameter name="fill">#000000</sld:CssParameter>
        </sld:Fill>
        <sld:Priority>
          <ogc:PropertyName>HABITANTS</ogc:PropertyName>
        </sld:Priority>
      </sld:TextSymbolizer>
    </sld:Rule>
  </sld:FeatureTypeStyle>
</sld:UserStyle>
