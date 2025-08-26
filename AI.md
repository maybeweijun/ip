iP Week 3 
Class affected : Storage.java
Prompt : What is a way that I can save my current arrayList<Task> into a text file?
Output : Use fileWriter and stringBuilder,
The AI recommended fileWriter for as the java API to use to write into files and provided some example formats
try (FileWriter writer = new FileWriter(filePath, false)) {
stringBuilder is an API that I learned from asking AI as I learned that it is a good and
clean way to construct strings before writing them into a file.

