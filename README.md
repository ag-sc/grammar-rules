# QueGG-Parser
The Parser takes a natural language question as input and returns the SPARQL query. 

go to directory: 

````installation
mvn clean package
```` 

input file example: [input.csv](https://github.com/ag-sc/grammar-rules/blob/main/grammarFiles/en/input.csv)
````
id,question
1,Who developed Calculator?
```` 

run the command
````
for English
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
    "sentence" : "Who developed Calculator?",
    "sparqls" : [ "SELECT ?Answer WHERE { ?Answer <http://dbpedia.org/ontology/product> <http://dbpedia.org/resource/Calculator> .}" ]
  } ]
}

run for other languages
````
For German
java -jar target/grammar-rules.jar de grammarFiles/de/grammar_FULL_DATASET_DE.json grammarFiles/de/input_DE.csv
For Italain
java -jar target/grammar-rules.jar en grammarFiles/en/grammar_FULL_DATASET_IT.json grammarFiles/en/input_IT.csv
For Spanish
java -jar target/grammar-rules.jar de grammarFiles/de/grammar_FULL_DATASET_ES.json grammarFiles/de/input_ES.csv
````  

output [output_DE.json](https://github.com/ag-sc/grammar-rules/blob/main/grammarFiles/de/output_DE.json)
output [output_IT.json](https://github.com/ag-sc/grammar-rules/blob/main/grammarFiles/it/output_IT.json)
output [output_ES.json](https://github.com/ag-sc/grammar-rules/blob/main/grammarFiles/es/output_ES.json)

## Developers
* **Mohammad Fazleh Elahi**
### Supervisors:
* **Dr. Philipp Cimiano**
* **Dr. Basil Ell**










