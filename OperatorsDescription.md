Description of 32 mutation operators implemented by DroidMutator
-------------------------------------------------------------
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

    NewParamIntentPutExtras
Description:<br>
Replace the parameters of the intent PutExtras method <br>
Code Example:  <br>
Before <br>
    ```
    intent.putExtras(mybundle/myintent);  
    ``` <br>
After <br>
    ```
    intent.putExtras(new Bundle()/new Intent()); 
    ```<br>

    InvalidKeyIntentPutExtra
Description:<br>
Randomly generate a different key in an Intent.putExtra(key, value) call <br>
Code Example:  <br>
Before <br>
    ```
    intent.putExtra(key, value);   
    ``` <br>
After <br>
    ```
    intent.putExtra(“ecab6839856b426fbdae3e6e8c46c38c”, value); 
    ```<br>

    ViewComponentNotVisible
Description:<br>
Set visible attribute (from a View) to false <br>
Code Example:  <br>
Before <br>
    ```
    TextView emailTextView = (TextView) findViewById(R.id.EmailTextView);  
    ``` <br>
After <br>
    ```
    TextView emailTextView = (TextView) findViewById(R.id.EmailTextView);
    emailTextView.setVisibility(android.view.View.INVISIBLE);  
    ```<br>

    FindViewByIdReturnNull
Description:<br>
Replace view ID return value is an empty object <br>
Code Example:  <br>
Before <br>
    ```
    button = findViewById(R.id.btn);  
    ``` <br>
After <br>
    ```
    button = null; 
    ```<br>

    BuggyGUIListener
Description:<br>
Assign null to a listener <br>
Code Example:  <br>
Before <br>
    ```
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            clicksCount += 1;
        }
    }  
    ``` <br>
After <br>
    ```
    private View.OnClickListener listener = null; 
    ```<br>

    LengthyGUICreation
Description:<br>
Insert a long delay (\ie Thread.sleep(..)) in the creation GUI thread <br>
Code Example:  <br>
Before <br>
    ```
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toast.makeText(getApplicationContext(),"2. onCreate()", Toast.LENGTH_SHORT).show();
    }   
    ``` <br>
After <br>
    ```
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        Toast.makeText(getApplicationContext(),"2. onCreate()", Toast.LENGTH_SHORT).show();
    }   
    ``` <br>

    LengthyGUIListener
Description:<br>
Insert a long delay (\ie Thread.sleep(..)) in a GUI Listener <br>
Code Example:  <br>
Before <br>
    ```
     private View.OnClickListener listener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      clicksCount += 1;
    }  
    ``` <br>
After <br>
    ```
       private View.OnClickListener listener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      clicksCount += 1;
      	try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }  
    ```<br>

Java-specific operators
===============================================
     NullBackEndServiceReturn
Description:<br>
Assign null to a response variable from a back-end service <br>
Code Example:  <br>
Before <br>
    ```
    HttpResponse response = client.execute(httpGet);  
    ``` <br>
After <br>
    ```
    HttpResponse response = null;
    ```<br>

     NullInputStream
Description:<br>
Assign an input stream to null before it is closed <br>
Code Example:  <br>
Before <br>
    ```
    fileStream.close(); 
    ``` <br>
After <br>
    ```
    fileStream = null;
    fileStream.close(); 
    ```<br>

     InvalidDate
Description:<br>
Set a random Date to a date object <br>
Code Example:  <br>
Before <br>
    ```
    Date stdDate = Date(year, month, date);  
    ``` <br>
After <br>
    ```
    Date stdDate = Date(12345678910L); 
    ```<br>

     InvalidFilePath
Description:<br>
Randomly mutate paths to files <br>
Code Example:  <br>
Before <br>
    ```
    File textFile = new File(“/sdcard/session.log”);  
    ``` <br>
After <br>
    ```
    File textFile = new File(“ecab6839856b426fbdae3e6e8c46c38c”); 
    ```<br>

     InvalidURI
Description:<br>
If URIs are used internally, randomly mutate the URIs <br>
Code Example:  <br>
Before <br>
    ```
    URI uri = new URI(u.toString());  
    ``` <br>
After <br>
    ```
    URI uri = new URI(“ecab6839856b426fbdae3e6e8c46c38c”);  
    ```<br>

     Shift Operator Replacement (SOR)
Description:<br>
Replacement displacement operator <br>
Code Example:  <br>
Before <br>
    ```
     a>>1 
    ``` <br>
After <br>
    ```
     a<<1 and a>>>1  
    ```<br>

     Relational Operator Replacement (ROR)
Description:<br>
Replacement relational operator <br>
Code Example:  <br>
Before <br>
    ```
     a>b
    ``` <br>
After <br>
    ```
     a>=b, a<b， a<=b， a==b， a!=b
    ```<br>

     Arithmetic Operator Deletion(Short-cut) (AODs)
Description:<br>
Remove auto increment and decrement operators <br>
Code Example:  <br>
Before <br>
    ```
     data=++num;
    ``` <br>
After <br>
    ```
     data=num;
    ```<br>

      Arithmetic Operator Deletion(Unary) (AODu)
Description:<br>
Delete unary arithmetic operator <br>
Code Example:  <br>
Before <br>
    ```
     a=-b;
    ``` <br>
After <br>
    ```
     a=b;
    ```<br>

      Arithmetic Operator Replacement(Binary) (AORb)
Description:<br>
Replace binary operator <br>
Code Example:  <br>
Before <br>
    ```
     int data = a+b;
    ``` <br>
After <br>
    ```
     int data = a-b;
    ```<br>

      Arithmetic Operator Replacement(Short-cut) (AORs)
Description:<br>
Replace auto increment and decrement arithmetic operators <br>
Code Example:  <br>
Before <br>
    ```
      a--;
    ``` <br>
After <br>
    ```
     a++; or ++a; or --a;
    ```<br>

      Arithmetic Operator Replacement(Unary) (AORu)
Description:<br>
Replace the unary arithmetic operator <br>
Code Example:  <br>
Before <br>
    ```
     -a;
    ``` <br>
After <br>
    ```
     +a;
    ```<br>

      Arithmetic Operator Insertion (AOI)
Description:<br>
Insert an arithmetic operator into an expression <br>
Code Example:  <br>
Before <br>
    ```
     a=b-c;
    ``` <br>
After <br>
    ```
     a=++b-c;
    ```<br>

      Assignment Operator Replacement (AOR)
Description:<br>
Replaces the assignment operator of an assignment expression <br>
Code Example:  <br>
Before <br>
    ```
    a+=b;
    ``` <br>
After <br>
    ```
    a-=b; or a/=b; or a*=b;
    ```<br>

      Logical Operator Replacement (LOR)
Description:<br>
Replace the logical operator in the expression <br>
Code Example:  <br>
Before <br>
    ```
    a & b;
    ``` <br>
After <br>
    ```
    a | b;
    ```<br>

      Conditional Operator Replacement (COR)
Description:<br>
Replace the conditional operator in the expression <br>
Code Example:  <br>
Before <br>
    ```
    a && b;
    ``` <br>
After <br>
    ```
    a || b;
    ```<br>

Java-specific operators (4 new mutation operators)
===============================================
     String Argument Replacement(SAR)
Description:<br>
Replace the value of the string type with a null value <br>
Code Example:  <br>
Before <br>
    ```
    getValueByName(“Tom”); 
    ``` <br>
After <br>
    ```
    getValueByName(“”);
    ```<br>

     String Call Replacement(SCR)
Description:<br>
Replace the call method of the string object <br>
Code Example:  <br>
Before <br>
    ```
    urlStr.startWith(“http”);
    ``` <br>
After <br>
    ```
    urlStr.endwith(“http”);  and  urlStr.contains((“http”);
    ```<br>

      Conditional Expression Replacement(CER)
Description:<br>
Replace the parameters of the conditional expression <br>
Code Example:  <br>
Before <br>
    ```
    int data = a>b?c:d;
    ``` <br>
After <br>
    ```
    int data = a>b?c:c  and  int data = a>b?d:d;
    ```<br>

      For Loop Replacement(FLR)
Description:<br>
Replace the initial condition and decision parameters of the for loop <br>
Code Example:  <br>
Before <br>
    ```
     for(int i=0;i<size;i++)
    ``` <br>
After <br>
    ```
     for(int i=0;i<size-1;i++)  and  for(int i=1;i<size;i++)
    ```<br>
