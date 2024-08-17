The StudentManager class provides basic CRUD operations for instances of Student and a read operation for instances of Degree. The database is connected using JDBC and managed in-memory with Derby.

Potential for memory Leaks:
StudentManager uses caches (studentCache and degreeCache) to store instances of 'Student' and 'Degree'. This could potentially lead to memory leaks by preventing Garbage Collection of those objects. This is addressed by making sure that entries are removed from the cache after deletion (remove()). To make it even safer I could implement a cache eviction strategy or clear the cache periodically to free up memory.

To compile the program (with mvn compile) the library first needs to be installed with this:
mvn install:install-file -Dfile=lib/studentdb-2.0.0.jar -DgroupId=nz.ac.wgtn.swen301 -DartifactId=studentdb -Dversion=2.0.0 -Dpackaging=jar



