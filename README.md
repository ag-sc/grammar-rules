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

### Multilingual question parse test

| Language      | NounPPFrame        | TransitiveFrame | InTransitivePP | Gradable | Attributive|test|
| :------------ |:---------------| :-----|:-----|:-----|:-----|
| English       |What is the capital of Bangladesh?| en_Templates| en_Grammar|en_Questions|en_QASystem|[result]|
| German        |[de_LexicalEntries]| [de_Templates] |[de_Grammar]|[de_Questions]|[de_QASystem]| |
| Italian       |[it_LexicalEntries]| [it_Templates] |[it_Grammar]|[it_Questions]|[it_QASystem]| |
| Spanish       |[es_LexicalEntries]| [es_Templates]|[es_Grammar]|[es_Questions]|[es_QSystem]| |











