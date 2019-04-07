# MultiRepoGitHubTool
A tool for working with lots of repos in a GitHub org


All the functions are documented.

You'll need to create a CourseSpecs.txt doc with the following format (some examples are filled in):

****************************************************
organization name: ORG_NAME_HERE_FOR_EXAMPLE_SIT-ECE
answer location: /src/mypkg/my_answers.py
moss id: 85*****51
language: python
git access token: 680f*****************************1cdee







*********************************************************

Sample code to quickly download all the repos for an assignment and upload them to moss might looks something like this:

RepoManager rm = RepoManager.cloneOrg("CourseSpecs.txt","homework5","EE551_Spring2019_homework5");

System.out.println(new Moss(rm).compute());

rm.cleanUp();

************************
The above code will use the courseSpecs.txt file to guide downloading all the repos whos names start with homework5 from the GitOrg.  EE551_Spring2019_homework5 will be used as the base file for Moss.