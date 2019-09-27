# Question 1

The file to test was  50 lines containing a one or two digit line number (not padded) followed by a tab,
followed by a random three digit number 100 <= num <= 500. 

I did not test that the random numbers were actually random,
as as all the ways of doing this wouldn't guarantee randomness with such a small sample and could fail randomly.

I wrote a python script to generate text files to test called `generateRandomFile.py`,
and a file that would fail the tests called `fail_numbers.txt`.

The tests are in `RandomNumbersTest.java`, which is a plain java file that only uses only classes included with java. 

The tests are broken in to three methods, each of which can pass or fail:

 - `testLineNumbers` parses a group of digits (of any size) from the start of the lines
   - It asserts that there digits at the start of the line.
   - that those digits can be parsed to an integer
   - that the integer matches the 1-indexed line number
   - that there is no 51st line.
 - `testRandomNumbers` parses a group of digits from the end of the lines
   - It asserts that there is a ggroup of digits (of any size) at the end of the line.
   - that those digits can be parsed to an integer
   - that the integer is not less than 100 or greater than 500.
   - It also warns but doesn't fail if the line numbers match the random numbers.
 - `testLinePattern` asserts that the line consists of only 1 or 2 digits, a tab, and exactly 3 digits.

The results are written to `output.txt`.

To run:

```
javac /home/adam/Desktop/hostelworld/q1_random_numbers/RandomNumbersTest.java
java RandomNumbers fail_numbers.txt
```

# Question 3

I needed a json parser to do this question right so I set up a gradle build with simple_json and junit as dependencies.
The test implementation is in `src/test/java/GistTests.java` andd it loads the github token from `src/test/resources/`

Tests are:
 - `createGist` runs before each test and creates a new gist.
   - It asserts that the response is 201 created.
 - `destroyGist` is run after each test and deleted the previously created gist.
   - It asserts that the response is 204 no content.
   - It asserts that the gist's url now returns 404 not found.
 - `testGistWasCreated` tests that the gist created by `createGist` can be fetched by id and returns 200 OK.
 - `testCreateGistResponse` tests that the response object returned by `createGist` has all fields frm the documentation.
 -  `testGistUserListing` tests listing a users gists
   - It asserts that the response code is 200 ok.
   - that the response is an array.
   - that a gist with the created gist's id is in the array.
   - that all objects in the array have the fields of a gist.

I didn't test what happens when multiple gists are created, what happens when gists are starred
or check the types of the returned fields due to time constraints.

To run:

```
gradlew test
```


