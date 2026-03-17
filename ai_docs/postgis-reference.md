# Chapter 7. PostGIS Reference

| Prev | Next |
|:-:|:-:|
| [Chapter 6. Performance Tips](performance_tips.html) | [box2d](box2d_type.html) |

The functions given below are the ones which a user of PostGIS is likely to need. There are other functions which are required support functions to the PostGIS objects which are not of use to a general user.

> **Note**
> PostGIS has begun a transition from the existing naming convention to an SQL-MM-centric convention. As a result, most of the functions that you know and love have been renamed using the standard spatial type (ST) prefix. Previous functions are still available, though are not listed in this document where updated functions are equivalent. The non ST_ functions not listed in this documentation are deprecated and will be removed in a future release so STOP USING THEM.

## 7.1. PostGIS Geometry/Geography/Box Data Types

### Abstract

This section lists the custom PostgreSQL data types installed by PostGIS to represent spatial data.

Each data type describes its type casting behavior. A type cast converts values of one data type into another type. PostgreSQL allows defining casting behavior for custom types, along with the functions used to convert type values. Casts can have __automatic__ behavior, which allows automatic conversion of a function argument to a type supported by the function.

Some casts have __explicit__ behavior, which means the cast must be specified using the syntax `CAST(myval As sometype)` or `myval::sometype`. Explicit casting avoids the issue of ambiguous casts, which can occur when using an overloaded function which does not support a given type. For example, a function may accept a box2d or a box3d, but not a geometry. Since geometry has an automatic cast to both box types, this produces an "ambiguous function" error. To prevent the error use an explicit cast to the desired box type.

All data types can be cast to `text`, so this does not need to be specified explicitly.

- **box2d** — The type representing a 2-dimensional bounding box.
- **box3d** — The type representing a 3-dimensional bounding box.
- **geometry** — The type representing spatial features with planar coordinate systems.
- **geometry_dump** — A composite type used to describe the parts of complex geometry.
- **geography** — The type representing spatial features with geodetic (ellipsoidal) coordinate systems.

## 7.2. Table Management Functions

### Abstract

These functions assist in defining tables containing geometry columns.

## 7.3. Geometry Constructors

### Abstract

These functions create geometry objects from various sources.

- **ST_Collect** — Creates a GeometryCollection or Multi* geometry from a set of geometries.
- **ST_LineFromMultiPoint** — Creates a LineString from a MultiPoint geometry.
- **ST_MakeEnvelope** — Creates a rectangular Polygon from minimum and maximum coordinates.
- **ST_MakeLine** — Creates a LineString from Point, MultiPoint, or LineString geometries.
- **ST_MakePoint** — Creates a 2D, 3DZ or 4D Point.
- **ST_MakePointM** — Creates a Point from X, Y and M values.
- **ST_MakePolygon** — Creates a Polygon from a shell and optional list of holes.
- **ST_Point** — Creates a Point with X, Y and SRID values.
- **ST_PointZ** — Creates a Point with X, Y, Z and SRID values.
- **ST_PointM** — Creates a Point with X, Y, M and SRID values.
- **ST_PointZM** — Creates a Point with X, Y, Z, M and SRID values.
- **ST_Polygon** — Creates a Polygon from a LineString with a specified SRID.
- **ST_TileEnvelope** — Creates a rectangular Polygon in Web Mercator (SRID:3857) using the XYZ tile system.
- **ST_HexagonGrid** — Returns a set of hexagons and cell indices that completely cover the bounds of the geometry argument.
- **ST_Hexagon** — Returns a single hexagon, using the provided edge size and cell coordinate within the hexagon grid space.
- **ST_SquareGrid** — Returns a set of grid squares and cell indices that completely cover the bounds of the geometry argument.
- **ST_Square** — Returns a single square, using the provided edge size and cell coordinate within the square grid space.
- **ST_Letters** — Returns the input letters rendered as geometry with a default start position at the origin and default text height of 100.

## 7.4. Geometry Accessors

### Abstract

These functions extract information about geometries.

- **GeometryType** — Returns the type of a geometry as text.
- **ST_Boundary** — Returns the boundary of a geometry.
- **ST_BoundingDiagonal** — Returns the diagonal of a geometry's bounding box.
- **ST_CoordDim** — Return the coordinate dimension of a geometry.
- **ST_Dimension** — Returns the topological dimension of a geometry.
- **ST_Dump** — Returns a set of `geometry_dump` rows for the components of a geometry.
- **ST_DumpPoints** — Returns a set of `geometry_dump` rows for the coordinates in a geometry.
- **ST_DumpSegments** — Returns a set of `geometry_dump` rows for the segments in a geometry.
- **ST_DumpRings** — Returns a set of `geometry_dump` rows for the exterior and interior rings of a Polygon.
- **ST_EndPoint** — Returns the last point of a LineString or CircularLineString.
- **ST_Envelope** — Returns a geometry representing the bounding box of a geometry.
- **ST_ExteriorRing** — Returns a LineString representing the exterior ring of a Polygon.
- **ST_GeometryN** — Return an element of a geometry collection.
- **ST_GeometryType** — Returns the SQL-MM type of a geometry as text.
- **ST_HasArc** — Tests if a geometry contains a circular arc
- **ST_InteriorRingN** — Returns the Nth interior ring (hole) of a Polygon.
- **ST_IsClosed** — Tests if a LineStrings's start and end points are coincident. For a PolyhedralSurface tests if it is closed (volumetric).
- **ST_IsCollection** — Tests if a geometry is a geometry collection type.
- **ST_IsEmpty** — Tests if a geometry is empty.
- **ST_IsPolygonCCW** — Tests if Polygons have exterior rings oriented counter-clockwise and interior rings oriented clockwise.
- **ST_IsPolygonCW** — Tests if Polygons have exterior rings oriented clockwise and interior rings oriented counter-clockwise.
- **ST_IsRing** — Tests if a LineString is closed and simple.
- **ST_IsSimple** — Tests if a geometry has no points of self-intersection or self-tangency.
- **ST_M** — Returns the M coordinate of a Point.
- **ST_MemSize** — Returns the amount of memory space a geometry takes.
- **ST_NDims** — Returns the coordinate dimension of a geometry.
- **ST_NPoints** — Returns the number of points (vertices) in a geometry.
- **ST_NRings** — Returns the number of rings in a polygonal geometry.
- **ST_NumGeometries** — Returns the number of elements in a geometry collection.
- **ST_NumInteriorRings** — Returns the number of interior rings (holes) of a Polygon.
- **ST_NumInteriorRing** — Returns the number of interior rings (holes) of a Polygon. Alias for ST_NumInteriorRings
- **ST_NumPatches** — Return the number of faces on a Polyhedral Surface. Will return null for non-polyhedral geometries.
- **ST_NumPoints** — Returns the number of points in a LineString or CircularString.
- **ST_PatchN** — Returns the Nth geometry (face) of a PolyhedralSurface.
- **ST_PointN** — Returns the Nth point in the first LineString or circular LineString in a geometry.
- **ST_Points** — Returns a MultiPoint containing the coordinates of a geometry.
- **ST_StartPoint** — Returns the first point of a LineString.
- **ST_Summary** — Returns a text summary of the contents of a geometry.
- **ST_X** — Returns the X coordinate of a Point.
- **ST_Y** — Returns the Y coordinate of a Point.
- **ST_Z** — Returns the Z coordinate of a Point.
- **ST_Zmflag** — Returns a code indicating the ZM coordinate dimension of a geometry.

## 7.5. Geometry Editors

### Abstract

These functions create modified geometries by changing type, structure or vertices.

- **ST_AddPoint** — Add a point to a LineString.
- **ST_CollectionExtract** — Given a geometry collection, returns a multi-geometry containing only elements of a specified type.
- **ST_CollectionHomogenize** — Returns the simplest representation of a geometry collection.
- **ST_CurveToLine** — Converts a geometry containing curves to a linear geometry.
- **ST_Scroll** — Change start point of a closed LineString.
- **ST_FlipCoordinates** — Returns a version of a geometry with X and Y axis flipped.
- **ST_Force2D** — Force the geometries into a "2-dimensional mode".
- **ST_Force3D** — Force the geometries into XYZ mode. This is an alias for ST_Force3DZ.
- **ST_Force3DZ** — Force the geometries into XYZ mode.
- **ST_Force3DM** — Force the geometries into XYM mode.
- **ST_Force4D** — Force the geometries into XYZM mode.
- **ST_ForcePolygonCCW** — Orients all exterior rings counter-clockwise and all interior rings clockwise.
- **ST_ForceCollection** — Convert the geometry into a GEOMETRYCOLLECTION.
- **ST_ForcePolygonCW** — Orients all exterior rings clockwise and all interior rings counter-clockwise.
- **ST_ForceSFS** — Force the geometries to use SFS 1.1 geometry types only.
- **ST_ForceRHR** — Force the orientation of the vertices in a polygon to follow the Right-Hand-Rule.
- **ST_ForceCurve** — Upcast a geometry into its curved type, if applicable.
- **ST_LineToCurve** — Converts a linear geometry to a curved geometry.
- **ST_Multi** — Return the geometry as a MULTI* geometry.
- **ST_LineExtend** — Returns a line with the last and first segments extended the specified distance(s).
- **ST_Normalize** — Return the geometry in its canonical form.
- **ST_Project** — Returns a point projected from a start point by a distance and bearing (azimuth).
- **ST_QuantizeCoordinates** — Sets least significant bits of coordinates to zero
- **ST_RemovePoint** — Remove a point from a linestring.
- **ST_RemoveRepeatedPoints** — Returns a version of a geometry with duplicate points removed.
- **ST_Reverse** — Return the geometry with vertex order reversed.
- **ST_Segmentize** — Returns a modified geometry/geography having no segment longer than a given distance.
- **ST_SetPoint** — Replace point of a linestring with a given point.
- **ST_ShiftLongitude** — Shifts the longitude coordinates of a geometry between -180..180 and 0..360.
- **ST_WrapX** — Wrap a geometry around an X value.
- **ST_SnapToGrid** — Snap all points of the input geometry to a regular grid.
- **ST_Snap** — Snap segments and vertices of input geometry to vertices of a reference geometry.
- **ST_SwapOrdinates** — Returns a version of the given geometry with given ordinate values swapped.

## 7.6. Geometry Validation

### Abstract

These functions test whether geometries are valid according to the OGC SFS standard. They also provide information about the nature and location of invalidity. There is also a function to create a valid geometry out of an invalid one.

- **ST_IsValid** — Tests if a geometry is well-formed in 2D.
- **ST_IsValidDetail** — Returns a `valid_detail` row stating if a geometry is valid or if not a reason and a location.
- **ST_IsValidReason** — Returns text stating if a geometry is valid, or a reason for invalidity.
- **ST_MakeValid** — Attempts to make an invalid geometry valid without losing vertices.

## 7.7. Spatial Reference System Functions

### Abstract

These functions work with the Spatial Reference System of geometries as defined in the `spatial_ref_sys` table.

- **ST_InverseTransformPipeline** — Return a new geometry with coordinates transformed to a different spatial reference system using the inverse of a defined coordinate transformation pipeline.
- **ST_SetSRID** — Set the SRID on a geometry.
- **ST_SRID** — Returns the spatial reference identifier for a geometry.
- **ST_Transform** — Return a new geometry with coordinates transformed to a different spatial reference system.
- **ST_TransformPipeline** — Return a new geometry with coordinates transformed to a different spatial reference system using a defined coordinate transformation pipeline.
- **postgis_srs_codes** — Return the list of SRS codes associated with the given authority.
- **postgis_srs** — Return a metadata record for the requested authority and srid.
- **postgis_srs_all** — Return metadata records for every spatial reference system in the underlying Proj database.
- **postgis_srs_search** — Return metadata records for projected coordinate systems that have areas of useage that fully contain the bounds parameter.

## 7.8. Geometry Input

### Abstract

These functions create geometry objects from various textual or binary formats.

### 7.8.1. Well-Known Text (WKT)

- **ST_BdPolyFromText** — Construct a Polygon given an arbitrary collection of closed linestrings as a MultiLineString Well-Known text representation.
- **ST_BdMPolyFromText** — Construct a MultiPolygon given an arbitrary collection of closed linestrings as a MultiLineString text representation Well-Known text representation.
- **ST_GeogFromText** — Return a specified geography value from Well-Known Text representation or extended (WKT).
- **ST_GeographyFromText** — Return a specified geography value from Well-Known Text representation or extended (WKT).
- **ST_GeomCollFromText** — Makes a collection Geometry from collection WKT with the given SRID. If SRID is not given, it defaults to 0.
- **ST_GeomFromEWKT** — Return a specified ST_Geometry value from Extended Well-Known Text representation (EWKT).
- **ST_GeomFromMARC21** — Takes MARC21/XML geographic data as input and returns a PostGIS geometry object.
- **ST_GeometryFromText** — Return a specified ST_Geometry value from Well-Known Text representation (WKT). This is an alias name for ST_GeomFromText
- **ST_GeomFromText** — Return a specified ST_Geometry value from Well-Known Text representation (WKT).
- **ST_LineFromText** — Makes a Geometry from WKT representation with the given SRID. If SRID is not given, it defaults to 0.
- **ST_MLineFromText** — Return a specified ST_MultiLineString value from WKT representation.
- **ST_MPointFromText** — Makes a Geometry from WKT with the given SRID. If SRID is not given, it defaults to 0.
- **ST_MPolyFromText** — Makes a MultiPolygon Geometry from WKT with the given SRID. If SRID is not given, it defaults to 0.
- **ST_PointFromText** — Makes a point Geometry from WKT with the given SRID. If SRID is not given, it defaults to unknown.
- **ST_PolygonFromText** — Makes a Geometry from WKT with the given SRID. If SRID is not given, it defaults to 0.
- **ST_WKTToSQL** — Return a specified ST_Geometry value from Well-Known Text representation (WKT). This is an alias name for ST_GeomFromText

### 7.8.2. Well-Known Binary (WKB)

- **ST_GeogFromWKB** — Creates a geography instance from a Well-Known Binary geometry representation (WKB) or extended Well Known Binary (EWKB).
- **ST_GeomFromEWKB** — Return a specified ST_Geometry value from Extended Well-Known Binary representation (EWKB).
- **ST_GeomFromWKB** — Creates a geometry instance from a Well-Known Binary geometry representation (WKB) and optional SRID.
- **ST_LineFromWKB** — Makes a `LINESTRING` from WKB with the given SRID
- **ST_LinestringFromWKB** — Makes a geometry from WKB with the given SRID.
- **ST_PointFromWKB** — Makes a geometry from WKB with the given SRID
- **ST_WKBToSQL** — Return a specified ST_Geometry value from Well-Known Binary representation (WKB). This is an alias name for ST_GeomFromWKB that takes no srid

### 7.8.3. Other Formats

## 7.9. Geometry Output

### Abstract

These functions convert geometry objects into various textual or binary formats.

### 7.9.1. Well-Known Text (WKT)

- **ST_AsEWKT** — Return the Well-Known Text (WKT) representation of the geometry with SRID meta data.
- **ST_AsText** — Return the Well-Known Text (WKT) representation of the geometry/geography without SRID metadata.

### 7.9.2. Well-Known Binary (WKB)

- **ST_AsBinary** — Return the OGC/ISO Well-Known Binary (WKB) representation of the geometry/geography without SRID meta data.
- **ST_AsEWKB** — Return the Extended Well-Known Binary (EWKB) representation of the geometry with SRID meta data.
- **ST_AsHEXEWKB** — Returns a Geometry in HEXEWKB format (as text) using either little-endian (NDR) or big-endian (XDR) encoding.

### 7.9.3. Other Formats

- **ST_AsEncodedPolyline** — Returns an Encoded Polyline from a LineString geometry.
- **ST_AsFlatGeobuf** — Return a FlatGeobuf representation of a set of rows.
- **ST_AsGeobuf** — Return a Geobuf representation of a set of rows.
- **ST_AsGeoJSON** — Return a geometry as a GeoJSON element.
- **ST_AsGML** — Return the geometry as a GML version 2 or 3 element.
- **ST_AsKML** — Return the geometry as a KML element.
- **ST_AsLatLonText** — Return the Degrees, Minutes, Seconds representation of the given point.
- **ST_AsMARC21** — Returns geometry as a MARC21/XML record with a geographic datafield (034).
- **ST_AsMVTGeom** — Transforms a geometry into the coordinate space of a MVT tile.
- **ST_AsMVT** — Aggregate function returning a MVT representation of a set of rows.
- **ST_AsSVG** — Returns SVG path data for a geometry.
- **ST_AsTWKB** — Returns the geometry as TWKB, aka "Tiny Well-Known Binary"
- **ST_AsX3D** — Returns a Geometry in X3D xml node element format: ISO-IEC-19776-1.2-X3DEncodings-XML
- **ST_GeoHash** — Return a GeoHash representation of the geometry.

## 7.10. Operators

### 7.10.1. Bounding Box Operators

- **&&** — Returns `TRUE` if A's 2D bounding box intersects B's 2D bounding box.
- **&&(geometry,box2df)** — Returns `TRUE` if a geometry's (cached) 2D bounding box intersects a 2D float precision bounding box (BOX2DF).
- **&&(box2df,geometry)** — Returns `TRUE` if a 2D float precision bounding box (BOX2DF) intersects a geometry's (cached) 2D bounding box.
- **&&(box2df,box2df)** — Returns `TRUE` if two 2D float precision bounding boxes (BOX2DF) intersect each other.
- **&&&** — Returns `TRUE` if A's n-D bounding box intersects B's n-D bounding box.
- **&&&(geometry,gidx)** — Returns `TRUE` if a geometry's (cached) n-D bounding box intersects a n-D float precision bounding box (GIDX).
- **&&&(gidx,geometry)** — Returns `TRUE` if a n-D float precision bounding box (GIDX) intersects a geometry's (cached) n-D bounding box.
- **&&&(gidx,gidx)** — Returns `TRUE` if two n-D float precision bounding boxes (GIDX) intersect each other.
- **&<** — Returns `TRUE` if A's bounding box overlaps or is to the left of B's.
- **&<|** — Returns `TRUE` if A's bounding box overlaps or is below B's.
- **&>** — Returns `TRUE` if A' bounding box overlaps or is to the right of B's.
- **<<** — Returns `TRUE` if A's bounding box is strictly to the left of B's.
- **<<|** — Returns `TRUE` if A's bounding box is strictly below B's.
- **=** — Returns `TRUE` if the coordinates and coordinate order geometry/geography A are the same as the coordinates and coordinate order of geometry/geography B.
- **>>** — Returns `TRUE` if A's bounding box is strictly to the right of B's.
- **@** — Returns `TRUE` if A's bounding box is contained by B's.
- **@(geometry,box2df)** — Returns `TRUE` if a geometry's 2D bounding box is contained into a 2D float precision bounding box (BOX2DF).
- **@(box2df,geometry)** — Returns `TRUE` if a 2D float precision bounding box (BOX2DF) is contained into a geometry's 2D bounding box.
- **@(box2df,box2df)** — Returns `TRUE` if a 2D float precision bounding box (BOX2DF) is contained into another 2D float precision bounding box.
- **|&>** — Returns `TRUE` if A's bounding box overlaps or is above B's.
- **|>>** — Returns `TRUE` if A's bounding box is strictly above B's.
- **~** — Returns `TRUE` if A's bounding box contains B's.
- **~(geometry,box2df)** — Returns `TRUE` if a geometry's 2D bonding box contains a 2D float precision bounding box (GIDX).
- **~(box2df,geometry)** — Returns `TRUE` if a 2D float precision bounding box (BOX2DF) contains a geometry's 2D bonding box.
- **~(box2df,box2df)** — Returns `TRUE` if a 2D float precision bounding box (BOX2DF) contains another 2D float precision bounding box (BOX2DF).
- **~=** — Returns `TRUE` if A's bounding box is the same as B's.

### 7.10.2. Distance Operators

- **<->** — Returns the 2D distance between A and B.
- **|=|** — Returns the distance between A and B trajectories at their closest point of approach.
- **<#>** — Returns the 2D distance between A and B bounding boxes.
- **<<->>** — Returns the n-D distance between the centroids of A and B bounding boxes.
- **<<#>>** — Returns the n-D distance between A and B bounding boxes.

## 7.11. Spatial Relationships

### Abstract

These functions determine spatial relationships between geometries.

### 7.11.1. Topological Relationships

- **ST_3DIntersects** — Tests if two geometries spatially intersect in 3D - only for points, linestrings, polygons, polyhedral surface (area)
- **ST_Contains** — Tests if every point of B lies in A, and their interiors have a point in common
- **ST_ContainsProperly** — Tests if every point of B lies in the interior of A
- **ST_CoveredBy** — Tests if every point of A lies in B
- **ST_Covers** — Tests if every point of B lies in A
- **ST_Crosses** — Tests if two geometries have some, but not all, interior points in common
- **ST_Disjoint** — Tests if two geometries have no points in common
- **ST_Equals** — Tests if two geometries include the same set of points
- **ST_Intersects** — Tests if two geometries intersect (they have at least one point in common)
- **ST_LineCrossingDirection** — Returns a number indicating the crossing behavior of two LineStrings
- **ST_OrderingEquals** — Tests if two geometries represent the same geometry and have points in the same directional order
- **ST_Overlaps** — Tests if two geometries have the same dimension and intersect, but each has at least one point not in the other
- **ST_Relate** — Tests if two geometries have a topological relationship matching an Intersection Matrix pattern, or computes their Intersection Matrix
- **ST_RelateMatch** — Tests if a DE-9IM Intersection Matrix matches an Intersection Matrix pattern
- **ST_Touches** — Tests if two geometries have at least one point in common, but their interiors do not intersect
- **ST_Within** — Tests if every point of A lies in B, and their interiors have a point in common

### 7.11.2. Distance Relationships

- **ST_3DDWithin** — Tests if two 3D geometries are within a given 3D distance
- **ST_3DDFullyWithin** — Tests if two 3D geometries are entirely within a given 3D distance
- **ST_DFullyWithin** — Tests if two geometries are entirely within a given distance
- **ST_DWithin** — Tests if two geometries are within a given distance
- **ST_PointInsideCircle** — Tests if a point geometry is inside a circle defined by a center and radius

## 7.12. Measurement Functions

### Abstract

These functions compute measurements of distance, area and angles. There are also functions to compute geometry values determined by measurements.

- **ST_Area** — Returns the area of a polygonal geometry.
- **ST_Azimuth** — Returns the north-based azimuth of a line between two points.
- **ST_Angle** — Returns the angle between two vectors defined by 3 or 4 points, or 2 lines.
- **ST_ClosestPoint** — Returns the 2D point on g1 that is closest to g2. This is the first point of the shortest line from one geometry to the other.
- **ST_3DClosestPoint** — Returns the 3D point on g1 that is closest to g2. This is the first point of the 3D shortest line.
- **ST_Distance** — Returns the distance between two geometry or geography values.
- **ST_3DDistance** — Returns the 3D cartesian minimum distance (based on spatial ref) between two geometries in projected units.
- **ST_DistanceSphere** — Returns minimum distance in meters between two lon/lat geometries using a spherical earth model.
- **ST_DistanceSpheroid** — Returns the minimum distance between two lon/lat geometries using a spheroidal earth model.
- **ST_FrechetDistance** — Returns the Fréchet distance between two geometries.
- **ST_HausdorffDistance** — Returns the Hausdorff distance between two geometries.
- **ST_Length** — Returns the 2D length of a linear geometry.
- **ST_Length2D** — Returns the 2D length of a linear geometry. Alias for `ST_Length`
- **ST_3DLength** — Returns the 3D length of a linear geometry.
- **ST_LengthSpheroid** — Returns the 2D or 3D length/perimeter of a lon/lat geometry on a spheroid.
- **ST_LongestLine** — Returns the 2D longest line between two geometries.
- **ST_3DLongestLine** — Returns the 3D longest line between two geometries
- **ST_MaxDistance** — Returns the 2D largest distance between two geometries in projected units.
- **ST_3DMaxDistance** — Returns the 3D cartesian maximum distance (based on spatial ref) between two geometries in projected units.
- **ST_MinimumClearance** — Returns the minimum clearance of a geometry, a measure of a geometry's robustness.
- **ST_MinimumClearanceLine** — Returns the two-point LineString spanning a geometry's minimum clearance.
- **ST_Perimeter** — Returns the length of the boundary of a polygonal geometry or geography.
- **ST_Perimeter2D** — Returns the 2D perimeter of a polygonal geometry. Alias for `ST_Perimeter`.
- **ST_3DPerimeter** — Returns the 3D perimeter of a polygonal geometry.
- **ST_ShortestLine** — Returns the 2D shortest line between two geometries
- **ST_3DShortestLine** — Returns the 3D shortest line between two geometries

## 7.13. Overlay Functions

### Abstract

These functions compute results arising from the overlay of two geometries. These are also known as point-set theoretic boolean operations. Some related functions are also provided.

- **ST_ClipByBox2D** — Computes the portion of a geometry falling within a rectangle.
- **ST_Difference** — Computes a geometry representing the part of geometry A that does not intersect geometry B.
- **ST_Intersection** — Computes a geometry representing the shared portion of geometries A and B.
- **ST_MemUnion** — Aggregate function which unions geometries in a memory-efficent but slower way
- **ST_Node** — Nodes a collection of lines.
- **ST_Split** — Returns a collection of geometries created by splitting a geometry by another geometry.
- **ST_Subdivide** — Computes a rectilinear subdivision of a geometry.
- **ST_SymDifference** — Computes a geometry representing the portions of geometries A and B that do not intersect.
- **ST_UnaryUnion** — Computes the union of the components of a single geometry.
- **ST_Union** — Computes a geometry representing the point-set union of the input geometries.

## 7.14. Geometry Processing

### Abstract

These functions compute geometric constructions, or alter geometry size or shape.

- **ST_Buffer** — Computes a geometry covering all points within a given distance from a geometry.
- **ST_BuildArea** — Creates a polygonal geometry formed by the linework of a geometry.
- **ST_Centroid** — Returns the geometric center of a geometry.
- **ST_ChaikinSmoothing** — Returns a smoothed version of a geometry, using the Chaikin algorithm
- **ST_ConcaveHull** — Computes a possibly concave geometry that contains all input geometry vertices
- **ST_ConvexHull** — Computes the convex hull of a geometry.
- **ST_DelaunayTriangles** — Returns the Delaunay triangulation of the vertices of a geometry.
- **ST_FilterByM** — Removes vertices based on their M value
- **ST_GeneratePoints** — Generates random points contained in a Polygon or MultiPolygon.
- **ST_GeometricMedian** — Returns the geometric median of a MultiPoint.
- **ST_LineMerge** — Return the lines formed by sewing together a MultiLineString.
- **ST_MaximumInscribedCircle** — Computes the largest circle contained within a geometry.
- **ST_LargestEmptyCircle** — Computes the largest circle not overlapping a geometry.
- **ST_MinimumBoundingCircle** — Returns the smallest circle polygon that contains a geometry.
- **ST_MinimumBoundingRadius** — Returns the center point and radius of the smallest circle that contains a geometry.
- **ST_OrientedEnvelope** — Returns a minimum-area rectangle containing a geometry.
- **ST_OffsetCurve** — Returns an offset line at a given distance and side from an input line.
- **ST_PointOnSurface** — Computes a point guaranteed to lie in a polygon, or on a geometry.
- **ST_Polygonize** — Computes a collection of polygons formed from the linework of a set of geometries.
- **ST_ReducePrecision** — Returns a valid geometry with points rounded to a grid tolerance.
- **ST_SharedPaths** — Returns a collection containing paths shared by the two input linestrings/multilinestrings.
- **ST_Simplify** — Returns a simplified version of a geometry, using the Douglas-Peucker algorithm.
- **ST_SimplifyPreserveTopology** — Returns a simplified and valid version of a geometry, using the Douglas-Peucker algorithm.
- **ST_SimplifyPolygonHull** — Computes a simplifed topology-preserving outer or inner hull of a polygonal geometry.
- **ST_SimplifyVW** — Returns a simplified version of a geometry, using the Visvalingam-Whyatt algorithm
- **ST_SetEffectiveArea** — Sets the effective area for each vertex, using the Visvalingam-Whyatt algorithm.
- **ST_TriangulatePolygon** — Computes the constrained Delaunay triangulation of polygons
- **ST_VoronoiLines** — Returns the boundaries of the Voronoi diagram of the vertices of a geometry.
- **ST_VoronoiPolygons** — Returns the cells of the Voronoi diagram of the vertices of a geometry.

## 7.15. Coverages

### Abstract

These functions operate on sets of polygonal geometry that form "implicit coverages". To form a valid coverage polygons must not overlap, and the vertices of adjacent edges must match exactly. Coverages are fast to process, and can be operated on with window functions, which retain the coverage topology inside the window partition while altering the edges.

- **ST_CoverageInvalidEdges** — Window function that finds locations where polygons fail to form a valid coverage.
- **ST_CoverageSimplify** — Window function that simplifies the edges of a polygonal coverage.
- **ST_CoverageUnion** — Computes the union of a set of polygons forming a coverage by removing shared edges.

## 7.16. Affine Transformations

- **ST_Affine** — Apply a 3D affine transformation to a geometry.
- **ST_Rotate** — Rotates a geometry about an origin point.
- **ST_RotateX** — Rotates a geometry about the X axis.
- **ST_RotateY** — Rotates a geometry about the Y axis.
- **ST_RotateZ** — Rotates a geometry about the Z axis.
- **ST_Scale** — Scales a geometry by given factors.
- **ST_Translate** — Translates a geometry by given offsets.
- **ST_TransScale** — Translates and scales a geometry by given offsets and factors.

## 7.17. Clustering Functions

### Abstract

These functions implement clustering algorithms for sets of geometries.

- **ST_ClusterDBSCAN** — Window function that returns a cluster id for each input geometry using the DBSCAN algorithm.
- **ST_ClusterIntersecting** — Aggregate function that clusters input geometries into connected sets.
- **ST_ClusterIntersectingWin** — Window function that returns a cluster id for each input geometry, clustering input geometries into connected sets.
- **ST_ClusterKMeans** — Window function that returns a cluster id for each input geometry using the K-means algorithm.
- **ST_ClusterWithin** — Aggregate function that clusters geometries by separation distance.
- **ST_ClusterWithinWin** — Window function that returns a cluster id for each input geometry, clustering using separation distance.

## 7.18. Bounding Box Functions

### Abstract

These functions produce or operate on bounding boxes. They can also provide and accept geometry values, by using automatic or explicit casts.

See also Section 12.7, "PostGIS Box Functions".

- **Box2D** — Returns a BOX2D representing the 2D extent of a geometry.
- **Box3D** — Returns a BOX3D representing the 3D extent of a geometry.
- **ST_EstimatedExtent** — Returns the estimated extent of a spatial table.
- **ST_Expand** — Returns a bounding box expanded from another bounding box or a geometry.
- **ST_Extent** — Aggregate function that returns the bounding box of geometries.
- **ST_3DExtent** — Aggregate function that returns the 3D bounding box of geometries.
- **ST_MakeBox2D** — Creates a BOX2D defined by two 2D point geometries.
- **ST_3DMakeBox** — Creates a BOX3D defined by two 3D point geometries.
- **ST_XMax** — Returns the X maxima of a 2D or 3D bounding box or a geometry.
- **ST_XMin** — Returns the X minima of a 2D or 3D bounding box or a geometry.
- **ST_YMax** — Returns the Y maxima of a 2D or 3D bounding box or a geometry.
- **ST_YMin** — Returns the Y minima of a 2D or 3D bounding box or a geometry.
- **ST_ZMax** — Returns the Z maxima of a 2D or 3D bounding box or a geometry.
- **ST_ZMin** — Returns the Z minima of a 2D or 3D bounding box or a geometry.

## 7.19. Linear Referencing

## 7.20. Trajectory Functions

### Abstract

These functions support working with trajectories. A trajectory is a linear geometry with increasing measures (M value) on each coordinate. Spatio-temporal data can be modeled by using relative times (such as the epoch) as the measure values.

- **ST_IsValidTrajectory** — Tests if the geometry is a valid trajectory.
- **ST_ClosestPointOfApproach** — Returns a measure at the closest point of approach of two trajectories.
- **ST_DistanceCPA** — Returns the distance between the closest point of approach of two trajectories.
- **ST_CPAWithin** — Tests if the closest point of approach of two trajectories is within the specified distance.

## 7.21. SFCGAL Functions

### Abstract

SFCGAL is a C++ wrapper library around CGAL that provides advanced 2D and 3D spatial functions. For robustness, geometry coordinates have an exact rational number representation.

Installation instructions for the library can be found on the SFCGAL home page (http://www.sfcgal.org). To enable the functions use `create extension postgis_sfcgal`.

## 7.22. Long Transaction Support

### Abstract

These functions implement a row locking mechanism to support long transactions. They are provided primarily for implementors of the Web Feature Service specification.

- **AddAuth** — Adds an authorization token to be used in the current transaction.
- **CheckAuth** — Creates a trigger on a table to prevent/allow updates and deletes of rows based on authorization token.
- **DisableLongTransactions** — Disables long transaction support.
- **EnableLongTransactions** — Enables long transaction support.
- **LockRow** — Sets lock/authorization for a row in a table.
- **UnlockRows** — Removes all locks held by an authorization token.

## 7.23. Version Functions

### Abstract

These functions report and upgrade PostGIS versions.

## 7.24. Grand Unified Custom Variables (GUCs)

### Abstract

This section lists custom PostGIS Grand Unified Custom Variables (GUC). These can be set globally, by database, by session or by transaction. Best set at global or database level.

- **postgis.backend** — The backend to service a function where GEOS and SFCGAL overlap. Options: geos or sfcgal. Defaults to geos.
- **postgis.gdal_datapath** — A configuration option to assign the value of GDAL's GDAL_DATA option. If not set, the environmentally set GDAL_DATA variable is used.
- **postgis.gdal_enabled_drivers** — A configuration option to set the enabled GDAL drivers in the PostGIS environment. Affects the GDAL configuration variable GDAL_SKIP.
- **postgis.enable_outdb_rasters** — A boolean configuration option to enable access to out-db raster bands.
- **postgis.gdal_vsi_options** — A string configuration to set options used when working with an out-db raster.

## 7.25. Troubleshooting Functions

### Abstract

These functions are utilities for troubleshooting and repairing geometry data. They are only needed if the geometry data is corrupted in some way, which should never happen under normal circumstances.