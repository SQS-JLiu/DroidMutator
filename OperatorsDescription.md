Android-specific operators
===============================================
    InvalidIDFindView
Description:<br>
Replace the id argument in an Activitity.findViewById call  <br>
Code Example:  <br>
Before <br>
    ```
    TextView emailTextView = (TextView) findViewById(R.id.EmailTextView);
    ``` <br>
After <br>
    ```
    TextView emailTextView = (TextView) findViewById(839); 
    ```
