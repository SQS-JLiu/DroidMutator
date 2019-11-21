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
    ```<br>
    
    NullBluetoothAdapter
Description:<br>
Replace a BluetoothAdapter instance with null  <br>
Code Example:  <br>
Before <br>
    ```
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    ``` <br>
After <br>
    ```
    BluetoothAdapter btAdapter = null;  
    ```<br>

    NullIntent
Description:<br>
Replace an Intent instantiation with null  <br>
Code Example:  <br>
Before <br>
    ```
    Intent intent = new Intent(main.this, ImportActivity.class); 
    ``` <br>
After <br>
    ```
    Intent intent = null;   
    ```<br>

    NullLocation
Description:<br>
Inject a Null GPS location in the location services  <br>
Code Example:  <br>
Before <br>
    ```
    Location GPSLocation = new Location(provider);  
    ``` <br>
After <br>
    ```
    Location GPSLocation = null;   
    ```<br>

    RandomActionIntentDefinition
Description:<br>
Replace the intent instantiation parameter with a random value <br>
Code Example:  <br>
Before <br>
    ```
    Intent intent = new Intent("com.myclass.action");  
    ``` <br>
After <br>
    ```
    Intent intent = new Intent("com.myclass.otheraction");   
    ```<br>
