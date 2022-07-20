@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
package ca.gc.aafc.collection.api.entities;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.TypeDef;
