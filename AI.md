iP Week 3 
Class affected : Storage.java
Prompt : What is a way that I can save my current arrayList<Task> into a text file?
Output : Use fileWriter and stringBuilder,
The AI recommended fileWriter for as the java API to use to write into files and provided some example formats
try (FileWriter writer = new FileWriter(filePath, false)) {
stringBuilder is an API that I learned from asking AI as I learned that it is a good and
clean way to construct strings before writing them into a file.

Class affected : General
Prompt : Based on the test cases I provided, can you provide some test cases which stretches the edge cases out of my test scenarios?
Output : I provided some test cases that stretches the edge cases out of my test scenarios.
Interesting learnings : We can use AI to help us generate large test cases at scale, and the AI even
catches some of the edge cases that I have missed. Moving forward, I will use AI and my own thinking to make better test cases
Based on the test cases that i have failed, I went back to edit my code to pass those specific test cases whilst, trying to not break my code

Class Affected : All
Prompt : Add class-level Javadoc to all classes.
Output : Added class-level Javadoc to all classes.
Reason : To save time as I have already commented out previously what the purpose of each class is
method-level javadoc has been omitted from the AI as the AI might not 100% understand what I am trying to do in each method
