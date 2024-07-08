# QueGG-Parser
The Parser takes a natural language question as input and returns the SPARQL query. 

go to directory: 

````installation
mvn clean package
```` 

input file example: [input_EN.csv](https://github.com/ag-sc/grammar-rules/blob/main/grammarFiles/en/input_EN.csv)
````
id,question
1,Which airports does Air China serve?
2,How deep is Lake Placid?
```` 

For English run the command
````
java -jar target/grammar-rules.jar en grammarFiles/en/grammar_FULL_DATASET_EN.json grammarFiles/en/input_EN.csv
````  

output [output_EN.json](https://github.com/ag-sc/grammar-rules/blob/main/grammarFiles/en/output_EN.json)
- If Parse then the SPARQL query
- Else 'N' refers to Not parsed
Example for English
````
{
  "results" : [ {
    "id" : "1",
    "status" : "WORK",
    "sentence" : "Which airports does Air China serve?",
    "sparqls" : [ "SELECT ?Answer WHERE { ?Answer <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>   <http://dbpedia.org/ontology/Airport>. 
                          ?Answer <http://dbpedia.org/ontology/targetAirport> <http://dbpedia.org/resource/China> .}" ]
  }, {
    "id" : "2",
    "status" : "WORK",
    "sentence" : "How deep is Lake Placid?",
    "sparqls" : [ "SELECT ?Answer WHERE { <http://dbpedia.org/resource/Lake_Placid_(Texas)> <http://dbpedia.org/ontology/maximumDepth> ?Answer .}" ]
  } ]
}
````
run for other languages
````
For German
java -jar target/grammar-rules.jar de grammarFiles/de/grammar_FULL_DATASET_DE.json grammarFiles/de/input_DE.csv
For Italain
java -jar target/grammar-rules.jar en grammarFiles/en/grammar_FULL_DATASET_IT.json grammarFiles/en/input_IT.csv
For Spanish
java -jar target/grammar-rules.jar de grammarFiles/de/grammar_FULL_DATASET_ES.json grammarFiles/de/input_ES.csv
````


## Developers
* **Mohammad Fazleh Elahi**
### Supervisors:
* **Dr. Philipp Cimiano**
* **Dr. Basil Ell**










