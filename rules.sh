#!/bin/sh

# english and dbpedia
echo "What is the capital of Canada?"
java -jar target/QuestionGrammarGenerator.jar "en" "What is the capital of Canada?"
echo "Who created Hollywood Darlings?"
java -jar target/QuestionGrammarGenerator.jar "en" "Who created Hollywood Darlings?"
echo "which grape grows in Swan Creek AVA?"
java -jar target/QuestionGrammarGenerator.jar "en" "which grape grows in Swan Creek AVA?"
echo "How high is Wylam Railway Bridge?"
java -jar target/QuestionGrammarGenerator.jar "en" "How high is Wylam Railway Bridge?"
echo "What is the highest mountain in Australia?"
java -jar target/QuestionGrammarGenerator.jar "en" "What is the highest mountain in Australia?"

