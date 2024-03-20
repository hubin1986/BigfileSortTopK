1,summary
multi thread to read file
multi thread to sort file  / quick sort for top k
final result run in single thread
time complexity：O(n)
space complexity:O(n)
test with 2GB file
use 10 hours for this project


2,env
jdk:11
maven:3.6.3
ide:idea
file encode:UTF-8
test run in mac or linux


3,run
way1:
mvn package assembly:single -Dmaven.test.skip=true
java -jar BigFileTopK-1.0-SNAPSHOT-jar-with-dependencies.jar
way2:
idea import maven project and run
way3：
find zip/target/BigFileTopK-1.0-SNAPSHOT-jar-with-dependencies.jar
java -jar BigFileTopK-1.0-SNAPSHOT-jar-with-dependencies.jar


run input :
please input abs file path=:
/Users/hubin/dev/test1
please input a number, X=:
8