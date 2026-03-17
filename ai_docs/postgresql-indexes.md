February 26, 2026: [PostgreSQL 18.3, 17.9, 16.13, 15.17, and 14.22 Released!](https://www.postgresql.org/about/news/postgresql-183-179-1613-1517-and-1422-released-3246/)

[Documentation](https://www.postgresql.org/docs/ "Documentation") → [PostgreSQL 18](https://www.postgresql.org/docs/18/index.html)

Supported Versions:

[Current](https://www.postgresql.org/docs/current/indexes.html "PostgreSQL 18 - Chapter 11. Indexes")
( [18](https://www.postgresql.org/docs/18/indexes.html "PostgreSQL 18 - Chapter 11. Indexes"))

/

[17](https://www.postgresql.org/docs/17/indexes.html "PostgreSQL 17 - Chapter 11. Indexes")

/

[16](https://www.postgresql.org/docs/16/indexes.html "PostgreSQL 16 - Chapter 11. Indexes")

/

[15](https://www.postgresql.org/docs/15/indexes.html "PostgreSQL 15 - Chapter 11. Indexes")

/

[14](https://www.postgresql.org/docs/14/indexes.html "PostgreSQL 14 - Chapter 11. Indexes")

Development Versions:

[devel](https://www.postgresql.org/docs/devel/indexes.html "PostgreSQL devel - Chapter 11. Indexes")

Unsupported versions:

[13](https://www.postgresql.org/docs/13/indexes.html "PostgreSQL 13 - Chapter 11. Indexes")

/

[12](https://www.postgresql.org/docs/12/indexes.html "PostgreSQL 12 - Chapter 11. Indexes")

/

[11](https://www.postgresql.org/docs/11/indexes.html "PostgreSQL 11 - Chapter 11. Indexes")

/

[10](https://www.postgresql.org/docs/10/indexes.html "PostgreSQL 10 - Chapter 11. Indexes")

/

[9.6](https://www.postgresql.org/docs/9.6/indexes.html "PostgreSQL 9.6 - Chapter 11. Indexes")

/

[9.5](https://www.postgresql.org/docs/9.5/indexes.html "PostgreSQL 9.5 - Chapter 11. Indexes")

/

[9.4](https://www.postgresql.org/docs/9.4/indexes.html "PostgreSQL 9.4 - Chapter 11. Indexes")

/

[9.3](https://www.postgresql.org/docs/9.3/indexes.html "PostgreSQL 9.3 - Chapter 11. Indexes")

/

[9.2](https://www.postgresql.org/docs/9.2/indexes.html "PostgreSQL 9.2 - Chapter 11. Indexes")

/

[9.1](https://www.postgresql.org/docs/9.1/indexes.html "PostgreSQL 9.1 - Chapter 11. Indexes")

/

[9.0](https://www.postgresql.org/docs/9.0/indexes.html "PostgreSQL 9.0 - Chapter 11. Indexes")

/

[8.4](https://www.postgresql.org/docs/8.4/indexes.html "PostgreSQL 8.4 - Chapter 11. Indexes")

/

[8.3](https://www.postgresql.org/docs/8.3/indexes.html "PostgreSQL 8.3 - Chapter 11. Indexes")

/

[8.2](https://www.postgresql.org/docs/8.2/indexes.html "PostgreSQL 8.2 - Chapter 11. Indexes")

/

[8.1](https://www.postgresql.org/docs/8.1/indexes.html "PostgreSQL 8.1 - Chapter 11. Indexes")

/

[8.0](https://www.postgresql.org/docs/8.0/indexes.html "PostgreSQL 8.0 - Chapter 11. Indexes")

/

[7.4](https://www.postgresql.org/docs/7.4/indexes.html "PostgreSQL 7.4 - Chapter 11. Indexes")

/

[7.3](https://www.postgresql.org/docs/7.3/indexes.html "PostgreSQL 7.3 - Chapter 11. Indexes")

/

[7.2](https://www.postgresql.org/docs/7.2/indexes.html "PostgreSQL 7.2 - Chapter 11. Indexes")

| Chapter 11. Indexes |
| :-: |
| [Prev](https://www.postgresql.org/docs/current/typeconv-select.html "10.6. SELECT Output Columns") | [Up](https://www.postgresql.org/docs/current/sql.html "Part II. The SQL Language") | Part II. The SQL Language | [Home](https://www.postgresql.org/docs/current/index.html "PostgreSQL 18.3 Documentation") | [Next](https://www.postgresql.org/docs/current/indexes-intro.html "11.1. Introduction") |

* * *

## Chapter 11. Indexes

**Table of Contents**

[11.1. Introduction](https://www.postgresql.org/docs/current/indexes-intro.html)[11.2. Index Types](https://www.postgresql.org/docs/current/indexes-types.html)[11.2.1. B-Tree](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPES-BTREE)[11.2.2. Hash](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPES-HASH)[11.2.3. GiST](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPE-GIST)[11.2.4. SP-GiST](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPE-SPGIST)[11.2.5. GIN](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPES-GIN)[11.2.6. BRIN](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPES-BRIN)[11.3. Multicolumn Indexes](https://www.postgresql.org/docs/current/indexes-multicolumn.html)[11.4. Indexes and ORDER BY](https://www.postgresql.org/docs/current/indexes-ordering.html)[11.5. Combining Multiple Indexes](https://www.postgresql.org/docs/current/indexes-bitmap-scans.html)[11.6. Unique Indexes](https://www.postgresql.org/docs/current/indexes-unique.html)[11.7. Indexes on Expressions](https://www.postgresql.org/docs/current/indexes-expressional.html)[11.8. Partial Indexes](https://www.postgresql.org/docs/current/indexes-partial.html)[11.9. Index-Only Scans and Covering Indexes](https://www.postgresql.org/docs/current/indexes-index-only-scans.html)[11.10. Operator Classes and Operator Families](https://www.postgresql.org/docs/current/indexes-opclass.html)[11.11. Indexes and Collations](https://www.postgresql.org/docs/current/indexes-collations.html)[11.12. Examining Index Usage](https://www.postgresql.org/docs/current/indexes-examine.html)

Indexes are a common way to enhance database performance. An index allows the database server to find and retrieve specific rows much faster than it could do without an index. But indexes also add overhead to the database system as a whole, so they should be used sensibly.

* * *

|     |     |     |
| --- | --- | --- |
| [Prev](https://www.postgresql.org/docs/current/typeconv-select.html "10.6. SELECT Output Columns") | [Up](https://www.postgresql.org/docs/current/sql.html "Part II. The SQL Language") | [Next](https://www.postgresql.org/docs/current/indexes-intro.html "11.1. Introduction") |
| 10.6. SELECT Output Columns | [Home](https://www.postgresql.org/docs/current/index.html "PostgreSQL 18.3 Documentation") | 11.1. Introduction |

## Submit correction

If you see anything in the documentation that is not correct, does not match
your experience with the particular feature or requires further clarification,
please use [this form](https://www.postgresql.org/account/comments/new/18/indexes.html/)
to report a documentation issue.

## 11.1. Introduction [&#35;](https://www.postgresql.org/docs/current/indexes-intro.html&#35;INDEXES-INTRO)

Suppose we have a table similar to this:

```sql
CREATE TABLE test1 (
    id integer,
    content varchar
);
```

and the application issues many queries of the form:

```sql
SELECT content FROM test1 WHERE id = constant;
```

With no advance preparation, the system would have to scan the entire `test1` table, row by row, to find all matching entries. If there are many rows in `test1` and only a few rows (perhaps zero or one) that would be returned by such a query, this is clearly an inefficient method. But if the system has been instructed to maintain an index on the `id` column, it can use a more efficient method for locating matching rows. For instance, it might only have to walk a few levels deep into a search tree.

A similar approach is used in most non-fiction books: terms and concepts that are frequently looked up by readers are collected in an alphabetic index at the end of the book. The interested reader can scan the index relatively quickly and flip to the appropriate page(s), rather than having to read the entire book to find the material of interest. Just as it is the task of the author to anticipate the items that readers are likely to look up, it is the task of the database programmer to foresee which indexes will be useful.

The following command can be used to create an index on the `id` column, as discussed:

```sql
CREATE INDEX test1_id_index ON test1 (id);
```

The name `test1_id_index` can be chosen freely, but you should pick something that enables you to remember later what the index was for.

To remove an index, use the `DROP INDEX` command. Indexes can be added to and removed from tables at any time.

Once an index is created, no further intervention is required: the system will update the index when the table is modified, and it will use the index in queries when it thinks doing so would be more efficient than a sequential table scan. But you might have to run the `ANALYZE` command regularly to update statistics to allow the query planner to make educated decisions. See [Chapter 14](https://www.postgresql.org/docs/current/performance-tips.html "Chapter 14. Performance Tips") for information about how to find out whether an index is used and when and why the planner might choose _not_ to use an index.

Indexes can also benefit `UPDATE` and `DELETE` commands with search conditions. Indexes can moreover be used in join searches. Thus, an index defined on a column that is part of a join condition can also significantly speed up queries with joins.

In general, PostgreSQL indexes can be used to optimize queries that contain one or more `WHERE` or `JOIN` clauses of the form

```sql
indexed-column indexable-operator comparison-value
```

Here, the _`indexed-column`_ is whatever column or expression the index has been defined on. The _`indexable-operator`_ is an operator that is a member of the index's _operator class_ for the indexed column. (More details about that appear below.) And the _`comparison-value`_ can be any expression that is not volatile and does not reference the index's table.

In some cases the query planner can extract an indexable clause of this form from another SQL construct. A simple example is that if the original clause was

```sql
comparison-value operator indexed-column
```

then it can be flipped around into indexable form if the original _`operator`_ has a commutator operator that is a member of the index's operator class.

Creating an index on a large table can take a long time. By default, PostgreSQL allows reads (`SELECT` statements) to occur on the table in parallel with index creation, but writes (`INSERT`, `UPDATE`, `DELETE`) are blocked until the index build is finished. In production environments this is often unacceptable. It is possible to allow writes to occur in parallel with index creation, but there are several caveats to be aware of — for more information see [Building Indexes Concurrently](https://www.postgresql.org/docs/current/sql-createindex.html#SQL-CREATEINDEX-CONCURRENTLY "Building Indexes Concurrently").

After an index is created, the system has to keep it synchronized with the table. This adds overhead to data manipulation operations. Indexes can also prevent the creation of [heap-only tuples](https://www.postgresql.org/docs/current/storage-hot.html "66.7. Heap-Only Tuples (HOT)"). Therefore indexes that are seldom or never used in queries should be removed.

* * *

|     |     |     |
| --- | --- | --- |
| [Prev](https://www.postgresql.org/docs/current/indexes.html "Chapter 11. Indexes") | [Up](https://www.postgresql.org/docs/current/indexes.html "Chapter 11. Indexes") | [Next](https://www.postgresql.org/docs/current/indexes-types.html "11.2. Index Types") |
| Chapter 11. Indexes | [Home](https://www.postgresql.org/docs/current/index.html "PostgreSQL 18.3 Documentation") | 11.2. Index Types |

## Submit correction

If you see anything in the documentation that is not correct, does not match
your experience with the particular feature or requires further clarification,
please use [this form](https://www.postgresql.org/account/comments/new/18/indexes-intro.html/)
to report a documentation issue.

## 11.2. Index Types [&#35;](https://www.postgresql.org/docs/current/indexes-types.html&#35;INDEXES-TYPES)

[11.2.1. B-Tree](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPES-BTREE)[11.2.2. Hash](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPES-HASH)[11.2.3. GiST](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPE-GIST)[11.2.4. SP-GiST](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPE-SPGIST)[11.2.5. GIN](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPES-GIN)[11.2.6. BRIN](https://www.postgresql.org/docs/current/indexes-types.html#INDEXES-TYPES-BRIN)

PostgreSQL provides several index types: B-tree, Hash, GiST, SP-GiST, GIN, BRIN, and the extension [bloom](https://www.postgresql.org/docs/current/bloom.html "F.6. bloom — bloom filter index access method"). Each index type uses a different algorithm that is best suited to different types of indexable clauses. By default, the [`CREATE INDEX`](https://www.postgresql.org/docs/current/sql-createindex.html "CREATE INDEX") command creates B-tree indexes, which fit the most common situations. The other index types are selected by writing the keyword `USING` followed by the index type name. For example, to create a Hash index:

```sql
CREATE INDEX name ON table USING HASH (column);
```

### 11.2.1. B-Tree [&#35;](https://www.postgresql.org/docs/current/indexes-types.html&#35;INDEXES-TYPES-BTREE)

B-trees can handle equality and range queries on data that can be sorted into some ordering. In particular, the PostgreSQL query planner will consider using a B-tree index whenever an indexed column is involved in a comparison using one of these operators:

```sql
<   <=   =   >=   >
```

Constructs equivalent to combinations of these operators, such as `BETWEEN` and `IN`, can also be implemented with a B-tree index search. Also, an `IS NULL` or `IS NOT NULL` condition on an index column can be used with a B-tree index.

The optimizer can also use a B-tree index for queries involving the pattern matching operators `LIKE` and `~`_if_ the pattern is a constant and is anchored to the beginning of the string — for example, `col LIKE 'foo%'` or `col ~ '^foo'`, but not `col LIKE '%bar'`. However, if your database does not use the C locale you will need to create the index with a special operator class to support indexing of pattern-matching queries; see [Section 11.10](https://www.postgresql.org/docs/current/indexes-opclass.html "11.10. Operator Classes and Operator Families") below. It is also possible to use B-tree indexes for `ILIKE` and `~*`, but only if the pattern starts with non-alphabetic characters, i.e., characters that are not affected by upper/lower case conversion.

B-tree indexes can also be used to retrieve data in sorted order. This is not always faster than a simple scan and sort, but it is often helpful.

### 11.2.2. Hash [&#35;](https://www.postgresql.org/docs/current/indexes-types.html&#35;INDEXES-TYPES-HASH)

Hash indexes store a 32-bit hash code derived from the value of the indexed column. Hence, such indexes can only handle simple equality comparisons. The query planner will consider using a hash index whenever an indexed column is involved in a comparison using the equal operator:

```sql
=
```

### 11.2.3. GiST [&#35;](https://www.postgresql.org/docs/current/indexes-types.html&#35;INDEXES-TYPE-GIST)

GiST indexes are not a single kind of index, but rather an infrastructure within which many different indexing strategies can be implemented. Accordingly, the particular operators with which a GiST index can be used vary depending on the indexing strategy (the _operator class_). As an example, the standard distribution of PostgreSQL includes GiST operator classes for several two-dimensional geometric data types, which support indexed queries using these operators:

```sql
<<   &<   &>   >>   <<|   &<|   |&>   |>>   @>   <@   ~=   &&
```

(See [Section 9.11](https://www.postgresql.org/docs/current/functions-geometry.html "9.11. Geometric Functions and Operators") for the meaning of these operators.) The GiST operator classes included in the standard distribution are documented in [Table 65.1](https://www.postgresql.org/docs/current/gist.html#GIST-BUILTIN-OPCLASSES-TABLE "Table 65.1. Built-in GiST Operator Classes"). Many other GiST operator classes are available in the `contrib` collection or as separate projects. For more information see [Section 65.2](https://www.postgresql.org/docs/current/gist.html "65.2. GiST Indexes").

GiST indexes are also capable of optimizing "nearest-neighbor" searches, such as

```sql
SELECT * FROM places ORDER BY location <-> point '(101,456)' LIMIT 10;
```

which finds the ten places closest to a given target point. The ability to do this is again dependent on the particular operator class being used. In [Table 65.1](https://www.postgresql.org/docs/current/gist.html#GIST-BUILTIN-OPCLASSES-TABLE "Table 65.1. Built-in GiST Operator Classes"), operators that can be used in this way are listed in the column "Ordering Operators".

### 11.2.4. SP-GiST [&#35;](https://www.postgresql.org/docs/current/indexes-types.html&#35;INDEXES-TYPE-SPGIST)

SP-GiST indexes, like GiST indexes, offer an infrastructure that supports various kinds of searches. SP-GiST permits implementation of a wide range of different non-balanced disk-based data structures, such as quadtrees, k-d trees, and radix trees (tries). As an example, the standard distribution of PostgreSQL includes SP-GiST operator classes for two-dimensional points, which support indexed queries using these operators:

```sql
<<   >>   ~=   <@   <<|   |>>
```

(See [Section 9.11](https://www.postgresql.org/docs/current/functions-geometry.html "9.11. Geometric Functions and Operators") for the meaning of these operators.) The SP-GiST operator classes included in the standard distribution are documented in [Table 65.2](https://www.postgresql.org/docs/current/spgist.html#SPGIST-BUILTIN-OPCLASSES-TABLE "Table 65.2. Built-in SP-GiST Operator Classes"). For more information see [Section 65.3](https://www.postgresql.org/docs/current/spgist.html "65.3. SP-GiST Indexes").

Like GiST, SP-GiST supports "nearest-neighbor" searches. For SP-GiST operator classes that support distance ordering, the corresponding operator is listed in the "Ordering Operators" column in [Table 65.2](https://www.postgresql.org/docs/current/spgist.html#SPGIST-BUILTIN-OPCLASSES-TABLE "Table 65.2. Built-in SP-GiST Operator Classes").

### 11.2.5. GIN [&#35;](https://www.postgresql.org/docs/current/indexes-types.html&#35;INDEXES-TYPES-GIN)

GIN indexes are "inverted indexes" which are appropriate for data values that contain multiple component values, such as arrays. An inverted index contains a separate entry for each component value, and can efficiently handle queries that test for the presence of specific component values.

Like GiST and SP-GiST, GIN can support many different user-defined indexing strategies, and the particular operators with which a GIN index can be used vary depending on the indexing strategy. As an example, the standard distribution of PostgreSQL includes a GIN operator class for arrays, which supports indexed queries using these operators:

```sql
<@   @>   =   &&
```

(See [Section 9.19](https://www.postgresql.org/docs/current/functions-array.html "9.19. Array Functions and Operators") for the meaning of these operators.) The GIN operator classes included in the standard distribution are documented in [Table 65.3](https://www.postgresql.org/docs/current/gin.html#GIN-BUILTIN-OPCLASSES-TABLE "Table 65.3. Built-in GIN Operator Classes"). Many other GIN operator classes are available in the `contrib` collection or as separate projects. For more information see [Section 65.4](https://www.postgresql.org/docs/current/gin.html "65.4. GIN Indexes").

### 11.2.6. BRIN [&#35;](https://www.postgresql.org/docs/current/indexes-types.html&#35;INDEXES-TYPES-BRIN)

BRIN indexes (a shorthand for Block Range INdexes) store summaries about the values stored in consecutive physical block ranges of a table. Thus, they are most effective for columns whose values are well-correlated with the physical order of the table rows. Like GiST, SP-GiST and GIN, BRIN can support many different indexing strategies, and the particular operators with which a BRIN index can be used vary depending on the indexing strategy. For data types that have a linear sort order, the indexed data corresponds to the minimum and maximum values of the values in the column for each block range. This supports indexed queries using these operators:

```sql
<   <=   =   >=   >
```

The BRIN operator classes included in the standard distribution are documented in [Table 65.4](https://www.postgresql.org/docs/current/brin.html#BRIN-BUILTIN-OPCLASSES-TABLE "Table 65.4. Built-in BRIN Operator Classes"). For more information see [Section 65.5](https://www.postgresql.org/docs/current/brin.html "65.5. BRIN Indexes").

* * *

|     |     |     |
| --- | --- | --- |
| [Prev](https://www.postgresql.org/docs/current/indexes-intro.html "11.1. Introduction") | [Up](https://www.postgresql.org/docs/current/indexes.html "Chapter 11. Indexes") | [Next](https://www.postgresql.org/docs/current/indexes-multicolumn.html "11.3. Multicolumn Indexes") |
| 11.1. Introduction | [Home](https://www.postgresql.org/docs/current/index.html "PostgreSQL 18.3 Documentation") | 11.3. Multicolumn Indexes |

## Submit correction

If you see anything in the documentation that is not correct, does not match
your experience with the particular feature or requires further clarification,
please use [this form](https://www.postgresql.org/account/comments/new/18/indexes-types.html/)
to report a documentation issue.

## 11.3. Multicolumn Indexes [&#35;](https://www.postgresql.org/docs/current/indexes-multicolumn.html&#35;INDEXES-MULTICOLUMN)

An index can be defined on more than one column of a table. For example, if you have a table of this form:

```sql
CREATE TABLE test2 (
  major int,
  minor int,
  name varchar
);
```

(say, you keep your `/dev` directory in a database...) and you frequently issue queries like:

```sql
SELECT name FROM test2 WHERE major = constant AND minor = constant;
```

then it might be appropriate to define an index on the columns `major` and `minor` together, e.g.:

```sql
CREATE INDEX test2_mm_idx ON test2 (major, minor);
```

Currently, only the B-tree, GiST, GIN, and BRIN index types support multiple-key-column indexes. Whether there can be multiple key columns is independent of whether `INCLUDE` columns can be added to the index. Indexes can have up to 32 columns, including `INCLUDE` columns. (This limit can be altered when building PostgreSQL; see the file `pg_config_manual.h`.)

A multicolumn B-tree index can be used with query conditions that involve any subset of the index's columns, but the index is most efficient when there are constraints on the leading (leftmost) columns. The exact rule is that equality constraints on leading columns, plus any inequality constraints on the first column that does not have an equality constraint, will always be used to limit the portion of the index that is scanned. Constraints on columns to the right of these columns are checked in the index, so they'll always save visits to the table proper, but they do not necessarily reduce the portion of the index that has to be scanned. If a B-tree index scan can apply the skip scan optimization effectively, it will apply every column constraint when navigating through the index via repeated index searches. This can reduce the portion of the index that has to be read, even though one or more columns (prior to the least significant index column from the query predicate) lacks a conventional equality constraint. Skip scan works by generating a dynamic equality constraint internally, that matches every possible value in an index column (though only given a column that lacks an equality constraint that comes from the query predicate, and only when the generated constraint can be used in conjunction with a later column constraint from the query predicate).

For example, given an index on `(x, y)`, and a query condition `WHERE y = 7700`, a B-tree index scan might be able to apply the skip scan optimization. This generally happens when the query planner expects that repeated `WHERE x = N AND y = 7700` searches for every possible value of `N` (or for every `x` value that is actually stored in the index) is the fastest possible approach, given the available indexes on the table. This approach is generally only taken when there are so few distinct `x` values that the planner expects the scan to skip over most of the index (because most of its leaf pages cannot possibly contain relevant tuples). If there are many distinct `x` values, then the entire index will have to be scanned, so in most cases the planner will prefer a sequential table scan over using the index.

The skip scan optimization can also be applied selectively, during B-tree scans that have at least some useful constraints from the query predicate. For example, given an index on `(a, b, c)` and a query condition `WHERE a = 5 AND b >= 42 AND c < 77`, the index might have to be scanned from the first entry with `a` = 5 and `b` = 42 up through the last entry with `a` = 5\\. Index entries with `c` >= 77 will never need to be filtered at the table level, but it may or may not be profitable to skip over them within the index. When skipping takes place, the scan starts a new index search to reposition itself from the end of the current `a` = 5 and `b` = N grouping (i.e. from the position in the index where the first tuple `a = 5 AND b = N AND c >= 77` appears), to the start of the next such grouping (i.e. the position in the index where the first tuple `a = 5 AND b = N + 1` appears).

A multicolumn GiST index can be used with query conditions that involve any subset of the index's columns. Conditions on additional columns restrict the entries returned by the index, but the condition on the first column is the most important one for determining how much of the index needs to be scanned. A GiST index will be relatively ineffective if its first column has only a few distinct values, even if there are many distinct values in additional columns.

A multicolumn GIN index can be used with query conditions that involve any subset of the index's columns. Unlike B-tree or GiST, index search effectiveness is the same regardless of which index column(s) the query conditions use.

A multicolumn BRIN index can be used with query conditions that involve any subset of the index's columns. Like GIN and unlike B-tree or GiST, index search effectiveness is the same regardless of which index column(s) the query conditions use. The only reason to have multiple BRIN indexes instead of one multicolumn BRIN index on a single table is to have a different `pages_per_range` storage parameter.

Of course, each column must be used with operators appropriate to the index type; clauses that involve other operators will not be considered.

Multicolumn indexes should be used sparingly. In most situations, an index on a single column is sufficient and saves space and time. Indexes with more than three columns are unlikely to be helpful unless the usage of the table is extremely stylized. See also [Section 11.5](https://www.postgresql.org/docs/current/indexes-bitmap-scans.html "11.5. Combining Multiple Indexes") and [Section 11.9](https://www.postgresql.org/docs/current/indexes-index-only-scans.html "11.9. Index-Only Scans and Covering Indexes") for some discussion of the merits of different index configurations.

* * *

|     |     |     |
| --- | --- | --- |
| [Prev](https://www.postgresql.org/docs/current/indexes-types.html "11.2. Index Types") | [Up](https://www.postgresql.org/docs/current/indexes.html "Chapter 11. Indexes") | [Next](https://www.postgresql.org/docs/current/indexes-ordering.html "11.4. Indexes and ORDER BY") |
| 11.2. Index Types | [Home](https://www.postgresql.org/docs/current/index.html "PostgreSQL 18.3 Documentation") | 11.4. Indexes and ORDER BY |

## Submit correction

If you see anything in the documentation that is not correct, does not match
your experience with the particular feature or requires further clarification,
please use [this form](https://www.postgresql.org/account/comments/new/18/indexes-multicolumn.html/)
to report a documentation issue.