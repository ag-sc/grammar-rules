# QueGG
The project creates QA system given a lemon lexica or Csv file (contains information Syntactic Frame such NounPPframe, TransitiveFrame, etc. )

go to director: 

````installation
mvn clean package
```` 

Run the system:
````shell script
sh ./rules.sh
```` 

rules.sh contains the following input
````
java -jar target/QuestionGrammarGenerator.jar "en" "What is the capital of Bangladesh?"
````  

output
````
SELECT ?Answer WHERE { <http://dbpedia.org/resource/Bangladesh> <http://dbpedia.org/ontology/capital> ?Answer .}
```` 











