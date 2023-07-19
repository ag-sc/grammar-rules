# QueGG
The project creates QA system given a lemon lexica or Csv file (contains information Syntactic Frame such NounPPframe, TransitiveFrame, etc. )

go to director: 

````installation
mvn clean package
```` 

Run the system:
````input file example: [input.csv](https://github.com/ag-sc/grammar-rules/blob/main/grammarFiles/en/input.csv)
id	question
1	List all boardgames by GMT.
2	Who developed Skype?
3	Which people were born in Heraklion?
4	In which U.S. state is Area 51 located?
5	Who is the mayor of New York City?
```` 

run the command
````
java -jar target/grammar-rules.jar "en" "grammarFiles/en/grammar_FULL_DATASET_EN.json" "grammarFiles/en/input.csv"
````  

output [output.csv](https://github.com/ag-sc/grammar-rules/blob/main/grammarFiles/en/output.csv)
````
ID	status	sentence	sparqlQald
1	WORK	List all boardgames by GMT.	SELECT ?Answer WHERE { <http://dbpedia.org/resource/GMT_Games> <http://dbpedia.org/ontology/publisher> ?Answer .}
2	WORK	Who developed Skype?	SELECT ?Answer WHERE { ?Answer <http://dbpedia.org/ontology/product> <http://dbpedia.org/resource/Skype> .}
3	WORK	Which people were born in Heraklion?	SELECT ?Answer WHERE { ?Answer <http://dbpedia.org/ontology/birthPlace> <http://dbpedia.org/resource/Heraklion> .}
4	N	In which U.S. state is Area 51 located?	N
5	WORK	Who is the mayor of New York City?	SELECT ?Answer WHERE { <http://dbpedia.org/resource/New_York_City> <http://dbpedia.org/ontology/leaderName> ?Answer .}

```` 












