
mvn clean package -DskipTests

find . -name "*.jar"

cp ./approach1/target/approach1-0.0.1-SNAPSHOT.jar WordCountAndSort_Final.jar
cp ./approach2/target/approach2-0.0.1-SNAPSHOT.jar WordCountAndSort_InvertedFinal.jar
