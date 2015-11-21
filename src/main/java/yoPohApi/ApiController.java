package yoPohApi;

import com.google.android.gcm.server.InvalidRequestException;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yoPohApi.YoPohClasses.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ayushb on 28/9/15.
 */

@RestController
public class ApiController {

    AtomicLong ticketNumber = new AtomicLong(1);

    @RequestMapping(value = "/post/customer", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Customer> postCustomer(@ModelAttribute Customer customer) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_TABLE);

        /*
         *Make sure client app saves customerId for future use
         */

        //If client calls with customerId then customer already exists need to update
        if(customer.getCustomerId()!=null) {
            BasicDBObject customerBasic = new BasicDBObject();
            customerBasic.put("customerId", customer.getCustomerId());
            DBObject object = collection.findOne(customerBasic);
            DBObject old = object;
            if(object!=null) {
                Map map = object.toMap();
                map.put("name", customer.getName());
                map.put("emailId", customer.getEmailId());
                map.put("phoneNum", customer.getPhoneNum());
                map.put("address", customer.getAddress());
                object.putAll(map);
                collection.update(customerBasic, object);
            }
            else{
                JSONObject customerJson = new JSONObject(customer);
                object = (DBObject) JSON.parse(customerJson.toString());
                collection.insert(object);
            }
        }
        mongoClient.close();
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }



    @RequestMapping(value = "/post/company", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Company> postCompany(@ModelAttribute Company company) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPANY_TABLE);

        /*
         *Make sure client app saves companyId for future use
         */

        if(company.getCompanyId()!=null) {
            BasicDBObject companyBasic = new BasicDBObject();
            companyBasic.put("companyId", company.getCompanyId());
            DBObject object = collection.findOne(companyBasic);
            DBObject old = object;
            Map map = object.toMap();
            map.put("companyDivision",company.getCompanyDivision());
            map.put("companyName",company.getCompanyName());
            object.putAll(map);
            collection.update(companyBasic,object);

        }
        else {
            company.setCompanyId(UUID.randomUUID().toString());
            JSONObject customerJson = new JSONObject(company);
            DBObject object = (DBObject) JSON.parse(customerJson.toString());
            collection.insert(object);
        }
        mongoClient.close();
        return new ResponseEntity<Company>(company, HttpStatus.OK);
    }

    @RequestMapping(value = "/post/customercare", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<CustomerCareExec> postCustomerCare(@ModelAttribute CustomerCareExec customerCareExec) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_CARE_EXEC_TABLE);
        /*
         *Make sure client app saves executiveId for future use
         */

        //If client calls with executiveId then customer already exists need to update
        if(customerCareExec.getExecutiveId()!=null) {
            BasicDBObject executiveBasic = new BasicDBObject();
            executiveBasic.put("companyId", customerCareExec.getExecutiveId());
            DBObject object = collection.findOne(executiveBasic);
            DBObject old = object;
            if (object != null) {
                Map map = object.toMap();
                map.put("designation", customerCareExec.getDesignation());
                map.put("emailId", customerCareExec.getEmailId());
                map.put("name", customerCareExec.getName());
                object.putAll(map);
                collection.update(executiveBasic,object);
            }
            else{
                customerCareExec.setExecutiveId(customerCareExec.getExecutiveId());
                JSONObject customerJson = new JSONObject(customerCareExec);
                object = (DBObject) JSON.parse(customerJson.toString());
                collection.insert(object);
            }
        }
        mongoClient.close();
        return new ResponseEntity<CustomerCareExec>(customerCareExec, HttpStatus.OK);
    }

    public void addProductToCustomer(String customerId, Product product) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_TABLE);
        Customer customer = new Customer();

        if(customerId!=null) {
            BasicDBObject customerBasic = new BasicDBObject();
            customerBasic.put("customerId", customer.getCustomerId());
            DBObject object = collection.findOne(customerBasic);
            DBObject old = object;
            if(object!=null) {
                Map map = object.toMap();
                customer.setAddress((String)map.get("address"));
                customer.setName((String) map.get("name"));
                customer.setCustomerId((String) map.get("customerId"));
                customer.setPhoneNum((String) map.get("phoneNum"));
                customer.setEmailId((String) map.get("email"));
                if(map.get("products")==null){
                    customer.setProducts(new ArrayList<>());
                    customer.getProducts().add(product);
                }
                JSONObject customerJson = new JSONObject(customer);
                object = (DBObject) JSON.parse(customerJson.toString());
                collection.update(customerBasic, object);
            }
        }
        mongoClient.close();
    }


    @RequestMapping(value = "/post/product", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Product> postProduct(@ModelAttribute Product product, @RequestParam String customerId) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.PRODUCT_TABLE);
        /*
         *Make sure client app saves executiveId for future use
         */

        //If client calls with executiveId then customer already exists need to update
        if(product.getProductId()!=null){
            BasicDBObject productBasic = new BasicDBObject();
            productBasic.put("productId", product.getProductId());
            DBObject object = collection.findOne(productBasic);
            DBObject old = object;
            Map map = object.toMap();
            map.put("price", product.getPrice());
            map.put("productName", product.getProductName());
            map.put("category", product.getCategory());
            map.put("companyId",product.getCompanyId());
            object.putAll(map);
            collection.update(productBasic,object);
        }
        else {
            BasicDBObject productBasic = new BasicDBObject();
            productBasic.put("productName", product.getProductName());
            DBObject object = collection.findOne(productBasic);
            if(object==null){
                product.setProductId(UUID.randomUUID().toString());
                JSONObject customerJson = new JSONObject(product);
                object = (DBObject) JSON.parse(customerJson.toString());
                collection.insert(object);
            }
        }
        mongoClient.close();
        addProductToCustomer(customerId,product);
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @RequestMapping(value = "/post/complaint", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Complaint> postComplaint(@ModelAttribute Complaint complaint){
        return new ResponseEntity<Complaint>(complaint, HttpStatus.OK);
    }

    @RequestMapping(value = "/post/log", method= RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Log> postLog(@ModelAttribute Log log){
        return new ResponseEntity<Log>(log, HttpStatus.OK);
    }

    @RequestMapping(value =  "/sendmessage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public boolean pushNotificationToGCM(@ModelAttribute GcmMessage gcmMessage) {
        final String GCM_API_KEY = "AIzaSyAcWH7TD6k4aJgh5N_hFNJ2socd4AUfyyk";
        final int retries = 3;
        Sender sender = new Sender(GCM_API_KEY);
        Message msg = new Message.Builder().addData("message",gcmMessage.message).build();

        try {
            if(!gcmMessage.getGcmRegId().isEmpty()) {
                Result result = sender.send(msg, gcmMessage.getGcmRegId(), retries);
                /**
                 * if you want to send to multiple then use below method
                 * send(Message message, List<String> regIds, int retries)
                 **/


                if (!result.getErrorCodeName().isEmpty()) {
                    System.out.println("GCM Notification is sent successfully" + result.toString());
                    return true;
                }

                System.out.println("Error occurred while sending push notification :" + result.getErrorCodeName());

            }
        } catch (InvalidRequestException e) {
            System.out.println("Invalid Request");
        } catch (IOException e) {
            System.out.println("IO Exception");
        }
        return false;

    }

    @RequestMapping(value="/get/customer", method=RequestMethod.GET)
    public ResponseEntity<Map> getCustomer(@RequestParam("customerId") String customerId) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_TABLE);
        if(customerId!=null){
            BasicDBObject customerBasic = new BasicDBObject();
            customerBasic.put("customerId", customerId);
            DBObject object = collection.findOne(customerBasic);
            Map map = object.toMap();
            map.remove("_id");
            return new ResponseEntity<Map>(map, HttpStatus.OK);
        }
        mongoClient.close();
        return new ResponseEntity<Map>(null,HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/get/acceptcomplaint", method = RequestMethod.GET)
    public void acceptTicket(@RequestParam("ticketNumber") long ticketNumber) throws UnknownHostException{
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPLAINT_TABLE);
        if(ticketNumber!=0){
            BasicDBObject complaint = new BasicDBObject();
            complaint.put("ticketNumber", ticketNumber);
            DBObject object = collection.findOne(complaint);
            Map map = object.toMap();
            String customerId = (String) map.get("customerId");
            mongoClient.close();
            getCustomer(customerId);
        }
        else{
            mongoClient.close();
            return;
        }
    }

    @RequestMapping(value = "/get/generatecomplaint", method = RequestMethod.GET)
    public ResponseEntity<Complaint> generateTicket(@RequestParam("productId") String productName,
                                              @RequestParam("customerId") String customerId,
                                              @RequestParam("companyName") String companyName
                                              ) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPLAINT_TABLE);

        Complaint complaint = new Complaint();
        complaint.setCustomerId(customerId);
        complaint.setDateCreated(System.currentTimeMillis());
        collection = database.getCollection(Constants.PRODUCT_TABLE);

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("productName",productName);
        DBObject object = collection.findOne(dbObject);
        Map map = object.toMap();
        String productId = (String) map.get("productId");
        complaint.setProductId(productId);

        dbObject.clear();
        dbObject.put("companyName", companyName);
        collection = database.getCollection(Constants.COMPANY_TABLE);
        object = collection.findOne(dbObject);
        map = object.toMap();
        String companyId = (String) map.get("companyId");
        complaint.setCompanyId(companyId);

        complaint.setTicketNumber(ticketNumber.getAndIncrement());

        collection = database.getCollection(Constants.COMPLAINT_TABLE);
        JSONObject customerJson = new JSONObject(complaint);
        object = (DBObject) JSON.parse(customerJson.toString());
        collection.insert(object);

        mongoClient.close();

        return new ResponseEntity<Complaint>(complaint, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/alltickets", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Complaint>> getAllTickets(@RequestParam("companyName") String companyName) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPANY_TABLE);

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("companyName",companyName);
        DBObject object = collection.findOne(dbObject);
        Map map = object.toMap();
        String companyId = (String) map.get("companyId");

        dbObject.clear();
        dbObject.put("companyId", companyId);
        collection = database.getCollection(Constants.COMPLAINT_TABLE);
        DBCursor cursor = collection.find(dbObject);
        ArrayList<Complaint> complaintArrayList = new ArrayList<>();
        while(cursor.hasNext()){
            map = cursor.next().toMap();
            map.remove("_id");
            Complaint complaint = new Complaint();
            complaint.setCompanyId((String) map.get("companyId"));
            complaint.setCustomerId((String) map.get("customerId"));
            complaint.setProductId((String) map.get("productid"));
            complaint.setTicketNumber((long) map.get("ticketNumber"));
            complaintArrayList.add(complaint);
        }
        mongoClient.close();
        return new ResponseEntity<ArrayList<Complaint>>(complaintArrayList, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/allcompanies", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Company>> getAllCompanies() throws UnknownHostException{
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPANY_TABLE);
        DBCursor cursor = collection.find();
        ArrayList<Company> companyArrayList = new ArrayList<>();
        while(cursor.hasNext()){
            Map map = cursor.next().toMap();
            map.remove("_id");
            Company company = new Company();
            company.setCompanyDivision((String) map.get("companyDivision"));
            company.setCompanyName((String) map.get("companyName"));
            company.setCompanyId((String) map.get("companyId"));
            companyArrayList.add(company);
        }
        mongoClient.close();
        return new ResponseEntity<ArrayList<Company>>(companyArrayList, HttpStatus.OK);
    }
}
