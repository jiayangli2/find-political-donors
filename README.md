Jiayang Li's solution to the Data Engineering Challenge

I used several data structures to tackle this problem, including HashSet, HashMap and PriorityQueue. To count the running median of the donars, I created several data structure so that I can use <donar name, zip> as a key and update the corresponding total amount of donation and times of donation. For a specific donar plus a zip code, there are 2 PriorityQueues associated with it. One is a minHeap and the other is a maxHeap. The minHeap includes all values that are greater or equal to the median, while the maxHeap includes all valus that are smaller than the median. In this way, we can calculate the running median in O(1) time.

For the input, I assume that each line will consist of at least 15 items after splitting it with "|". Otherwise, we will not be able to process this record since the donation amount, the 15th item in the record, will be missing.

My solution runs in Java 1.7. All libraries it uses are built-in libraries for Java 1.7. You can run it by run.sh or run_tests.sh. However, if you want to compile it, you can go to the root directory and run the following command:

javac ./src/Contribution.java

After that, you can run the program with 3 arguments denoting the input file, the output file for zip, and the output file for date.


Example running the program in the root directory

java -classpath ./src Contribution ./input/itcont.txt ./output/medianvals_by_zip.txt ./output/medianvals_by_date.txt
