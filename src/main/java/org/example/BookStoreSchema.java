package org.example;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

@ProtoSchema(
        includeClasses = {
                Book.class,
                Author.class,
                Language.class
        },
        schemaFileName = "books.proto",
        schemaFilePath = "proto/",
        schemaPackageName = "books"
)
interface BookStoreSchema extends GeneratedSchema {}

