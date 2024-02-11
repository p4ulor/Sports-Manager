# Demonstration Guide #

## Steps to reproduce in the phase 2 demonstration

Each group will have a maximum of 15 minutes for their demonstration. Within this time frame,one or more selected students 
must complete the following steps: 

1. Clone the group repo to a new folder.
2. Checkout the phase 2 tag (`0.2.*` tag).
3. Use gradle at command line, to clean and build the project.
4. Use gradle at command line, to clean and build the project again.
5. At command line, launch HTTP Server with Postgres implementation 
6. With Postman, do the following actions through the API:
   1. List all sports
   2. Create one user and one route
   3. Try to create another user with the same email 
   4. Create two sports, cycling and 10km run
   5. Show the details of the 10km run
   6. Try to get the sport details for a non-existing sport (e.g. /sports/999abc)
   7. Add one activity to the "10km run" sport without a route
   8. Show the details of the new activity
   9. Add three activities to the "cycling" sport with the same route
   10. Try to add an activity for a non-existing route
   11. List all cycling activities order by duration time, `ascending` and `descending`
   12. List all cycling activities format using a page of length 2, going through all the pages.
   13. Delete two cycling activities
   14. List all cycling activities
7. Launch the browser
8. Go to the application root and follow all edges of the navigation graph
 