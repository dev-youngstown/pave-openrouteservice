package org.heigit.ors.geojson;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.heigit.ors.geojson.exception.GeoJSONException;
import org.json.JSONException;
import org.json.JSONObject;

public class Geometry extends GeoJSON {
  public static final String[] ALLOWED_SIMPLE_GEOMETRY_TYPES = {"Point", "LineString", "Polygon"};
  public static final String[] ALLOWED_MULTI_GEOMETRY_TYPES = {"MultiPoint", "MultiLineString", "MultiPolygon", "GeometryCollection"};
  public static final String[] ALLOWED_GEOMETRY_TYPES = Stream.concat(Arrays.stream(ALLOWED_SIMPLE_GEOMETRY_TYPES), Arrays.stream(ALLOWED_MULTI_GEOMETRY_TYPES)).toArray(String[]::new);

  private com.vividsolutions.jts.geom.Geometry geometry;

  public Geometry(JSONObject input) {
    try {
      this.geometry = GeometryJSON.parse(input);
    } catch (JSONException e) {
      throw new GeoJSONException("Geometry could not be parsed.\n" + e.getMessage());
    }
    this.geoJSONType = "Geometry";
  }

  public com.vividsolutions.jts.geom.Geometry getGeometry() {
    return geometry;
  }

  public String getGeometryType() {
    return geometry.getGeometryType();
  }

  public Feature[] getFeatures() {
    throw new GeoJSONException("Geometry does not contain any features.");
  }

  public JSONObject toJSON() {
    if (!Arrays.asList(ALLOWED_GEOMETRY_TYPES).contains(getGeometryType())) {
      throw new GeoJSONException("Invalid geometry type building GeoJSON.");
    }
    JSONObject geoJson = new JSONObject();
    geoJson.put("type", getGeometryType());
    geoJson.put("coordinates", GeometryJSON.toJSON(geometry));
    return geoJson;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Geometry geometry1 = (Geometry) o;
    return Objects.equals(geometry, geometry1.geometry) &&
        Objects.equals(geoJSONType, geometry1.geoJSONType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(geometry, geoJSONType);
  }
}