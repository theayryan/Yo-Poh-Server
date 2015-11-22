package yoPohApi;

import com.mongodb.*;
import com.mongodb.util.JSON;
import com.pubnub.api.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yoPohApi.YoPohClasses.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ayushb on 28/9/15.
 */

@RestController
public class ApiController {

    AtomicLong ticketNumber = new AtomicLong(1);

    @RequestMapping(value = "/post/customer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Customer> postCustomer(@ModelAttribute Customer customer) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_TABLE);

        /*
         *Make sure client app saves customerId for future use
         */

        //If client calls with customerId then customer already exists need to update
        if (customer.getCustomerId() != null) {
            BasicDBObject customerBasic = new BasicDBObject();
            customerBasic.put("customerId", customer.getCustomerId());
            DBObject object = collection.findOne(customerBasic);
            DBObject old = object;
            if (object != null) {
                Map map = object.toMap();
                map.put("name", customer.getName());
                map.put("emailId", customer.getEmailId());
                map.put("phoneNum", customer.getPhoneNum());
                map.put("address", customer.getAddress());
                map.put("products", new String[0]);
                object.putAll(map);
                collection.update(customerBasic, object);
            } else {
                customer.setProducts(new String[0]);
                JSONObject customerJson = new JSONObject(customer);
                object = (DBObject) JSON.parse(customerJson.toString());
                collection.insert(object);
            }
        }
        mongoClient.close();
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }


    @RequestMapping(value = "/post/company", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Company> postCompany(@ModelAttribute Company company) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPANY_TABLE);

        /*
         *Make sure client app saves companyId for future use
         */

        if (company.getCompanyId() != null) {
            BasicDBObject companyBasic = new BasicDBObject();
            companyBasic.put("companyId", company.getCompanyId());
            DBObject object = collection.findOne(companyBasic);
            DBObject old = object;
            Map map = object.toMap();
            map.put("companyDivision", company.getCompanyDivision());
            map.put("companyName", company.getCompanyName());
            object.putAll(map);
            collection.update(companyBasic, object);

        } else {
            company.setCompanyId(UUID.randomUUID().toString());
            JSONObject customerJson = new JSONObject(company);
            DBObject object = (DBObject) JSON.parse(customerJson.toString());
            collection.insert(object);
        }
        mongoClient.close();
        return new ResponseEntity<Company>(company, HttpStatus.OK);
    }

    @RequestMapping(value = "/post/customercare", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<CustomerCareExec> postCustomerCare(@ModelAttribute CustomerCareExec customerCareExec) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_CARE_EXEC_TABLE);
        /*
         *Make sure client app saves executiveId for future use
         */

        //If client calls with executiveId then customer already exists need to update
        if (customerCareExec.getExecutiveId() != null) {
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
                collection.update(executiveBasic, object);
            } else {
                customerCareExec.setExecutiveId(customerCareExec.getExecutiveId());
                JSONObject customerJson = new JSONObject(customerCareExec);
                object = (DBObject) JSON.parse(customerJson.toString());
                collection.insert(object);
            }
        }
        mongoClient.close();
        return new ResponseEntity<CustomerCareExec>(customerCareExec, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/addtomyproducts", method = RequestMethod.GET)
    public ResponseEntity<Customer> addProductToCustomer(@RequestParam("customerId") String customerId, @RequestParam("productId") String productId) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_TABLE);
        Customer customer = new Customer();

        if (customerId != null) {
            BasicDBObject customerBasic = new BasicDBObject();
            customerBasic.put("customerId", customerId);
            DBObject object = collection.findOne(customerBasic);
            if (object != null) {
                Map map = object.toMap();
                customer.setAddress((String) map.get("address"));
                customer.setName((String) map.get("name"));
                customer.setCustomerId((String) map.get("customerId"));
                customer.setPhoneNum((String) map.get("phoneNum"));
                customer.setEmailId((String) map.get("emailId"));
                BasicDBList products = (BasicDBList) map.get("products");
                if (products.size() == 0) {
                    customer.setProducts(new String[]{productId});
                } else {
                    String[] tempArray = new String[products.size() + 1];
                    for (int i = 0; i < products.size(); i++)
                        tempArray[i] = products.get(i).toString();
                    tempArray[tempArray.length - 1] = productId;
                    customer.setProducts(tempArray);
                }
                map.remove("_id");
                map.put("products", customer.getProducts());
                JSONObject customerJson = new JSONObject(map);
                object = (DBObject) JSON.parse(customerJson.toString());
                collection.update(customerBasic, object);
            }
        }
        mongoClient.close();
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }


    @RequestMapping(value = "/post/product", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Product> postProduct(@ModelAttribute Product product) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.PRODUCT_TABLE);
        /*
         *Make sure client app saves executiveId for future use
         */

        //If client calls with executiveId then customer already exists need to update
        if (product.getProductId() != null) {
            BasicDBObject productBasic = new BasicDBObject();
            productBasic.put("productId", product.getProductId());
            DBObject object = collection.findOne(productBasic);
            DBObject old = object;
            Map map = object.toMap();
            map.put("price", product.getPrice());
            map.put("productName", product.getProductName());
            map.put("category", product.getCategory());
            map.put("companyId", product.getCompanyId());
            object.putAll(map);
            collection.update(productBasic, object);
        } else {
            BasicDBObject productBasic = new BasicDBObject();
            productBasic.put("productName", product.getProductName());
            DBObject object = collection.findOne(productBasic);
            if (object == null) {
                product.setProductId(UUID.randomUUID().toString());
                JSONObject customerJson = new JSONObject(product);
                object = (DBObject) JSON.parse(customerJson.toString());
                collection.insert(object);
            }
        }
        mongoClient.close();

        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @RequestMapping(value = "/post/complaint", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Complaint> postComplaint(@ModelAttribute Complaint complaint) {
        return new ResponseEntity<Complaint>(complaint, HttpStatus.OK);
    }

    @RequestMapping(value = "/post/log", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Log> postLog(@ModelAttribute Log log) {
        return new ResponseEntity<Log>(log, HttpStatus.OK);
    }

    @RequestMapping(value = "/sendmessage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public boolean pushNotificationToGCM(@ModelAttribute GcmMessage gcmMessage) {
        final Pubnub pubnub = new Pubnub(
                "pub-c-11fab31d-7614-4488-b338-2953846af28a" /* replace with your publish key */,
                "sub-c-17a0ba6c-90ef-11e5-b0f3-02ee2ddab7fe" /* replace with your subscribe key */);
        PnGcmMessage pnGcmMessage = new PnGcmMessage();
        JSONObject jso = new JSONObject();
        try {
            jso.put("Message", gcmMessage.getMessage());
            jso.put("Channel", gcmMessage.getChannel());
            jso.put("From", "client");
        } catch (JSONException e) {
        }
        pnGcmMessage.setData(jso);
        PnMessage message = new PnMessage(
                pubnub,
                gcmMessage.getChannel(),
                new Callback() {
                    @Override
                    public void successCallback(String s, Object o) {
                        super.successCallback(s, o);
                    }

                    @Override
                    public void errorCallback(String s, PubnubError pubnubError) {
                        super.errorCallback(s, pubnubError);
                    }
                },
                pnGcmMessage);
        try {
            message.publish();
        } catch (PubnubException e) {
            e.printStackTrace();
        }

        return false;

    }


    @RequestMapping(value = "/get/customer", method = RequestMethod.GET)
    public ResponseEntity<Map> getCustomer(@RequestParam("customerId") String customerId) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_TABLE);
        if (customerId != null) {
            BasicDBObject customerBasic = new BasicDBObject();
            customerBasic.put("customerId", customerId);
            DBObject object = collection.findOne(customerBasic);
            Map map = object.toMap();
            map.remove("_id");
            return new ResponseEntity<Map>(map, HttpStatus.OK);
        }
        mongoClient.close();
        return new ResponseEntity<Map>(null, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/get/acceptcomplaint", method = RequestMethod.GET)
    public void acceptTicket(@RequestParam("channel") String channel) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPLAINT_TABLE);
        if (channel != null && !channel.isEmpty()) {
            BasicDBObject complaint = new BasicDBObject();
            complaint.put("channel", channel);
            DBObject object = collection.findOne(complaint);
            Map map = object.toMap();
            String customerId = (String) map.get("customerId");
            mongoClient.close();
            getCustomer(customerId);
        } else {
            mongoClient.close();
            return;
        }
    }

    @RequestMapping(value = "/get/generatecomplaint", method = RequestMethod.GET)
    public ResponseEntity<Complaint> generateTicket(@RequestParam("productId") String productId,
                                                    @RequestParam("customerId") String customerId,
                                                    @RequestParam("companyId") String companyId
    ) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_TABLE);
        Complaint complaint = new Complaint();

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("customerId", customerId);
        DBObject object = collection.findOne(dbObject);
        Map map = object.toMap();
        complaint.setCustomerName((String) map.get("name"));

        collection = database.getCollection(Constants.PRODUCT_TABLE);
        dbObject.clear();
        dbObject.put("productId", productId);
        object = collection.findOne(dbObject);
        map = object.toMap();
        complaint.setProductName((String) map.get("productName"));

        complaint.setCustomerId(customerId);
        complaint.setDateCreated(System.currentTimeMillis());

        complaint.setCompanyId(companyId);
        complaint.setProductId(productId);

        complaint.setTicketNumber(ticketNumber.getAndIncrement());
        complaint.setChannel(UUID.randomUUID().toString());
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
        dbObject.put("companyName", companyName);
        DBObject object = collection.findOne(dbObject);
        Map map = object.toMap();
        String companyId = (String) map.get("companyId");

        dbObject.clear();
        dbObject.put("companyId", companyId);
        collection = database.getCollection(Constants.COMPLAINT_TABLE);
        DBCursor cursor = collection.find(dbObject);
        ArrayList<Complaint> complaintArrayList = new ArrayList<>();
        while (cursor.hasNext()) {
            map = cursor.next().toMap();
            map.remove("_id");
            Complaint complaint = new Complaint();
            complaint.setCompanyId((String) map.get("companyId"));
            complaint.setCustomerId((String) map.get("customerId"));
            complaint.setProductId((String) map.get("productid"));
            complaint.setTicketNumber((long) map.get("ticketNumber"));
            complaint.setChannel((String) map.get("channel"));
            complaint.setCustomerName((String) map.get("customerName"));
            complaint.setProductName((String) map.get("productName"));
            complaintArrayList.add(complaint);
        }
        mongoClient.close();
        return new ResponseEntity<ArrayList<Complaint>>(complaintArrayList, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/mytickets", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Complaint>> getMyTickets(@RequestParam("customerId") String customerId) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPLAINT_TABLE);

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("customerId", customerId);
        DBCursor cursor = collection.find(dbObject);
        ArrayList<Complaint> complaintArrayList = new ArrayList<>();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            Map map = cursor.next().toMap();
            map.remove("_id");
            Complaint complaint = new Complaint();
            complaint.setCompanyId((String) map.get("companyId"));
            complaint.setCustomerId((String) map.get("customerId"));
            complaint.setProductId((String) map.get("productId"));
            complaint.setTicketNumber(Long.parseLong(map.get("ticketNumber").toString()));
            complaint.setChannel((String) map.get("channel"));
            complaint.setCustomerName((String) map.get("customerName"));
            complaint.setProductName((String) map.get("productName"));
            complaint.setDateCreated(Long.parseLong(map.get("dateCreated").toString()));
            complaintArrayList.add(complaint);
        }
        mongoClient.close();
        return new ResponseEntity<ArrayList<Complaint>>(complaintArrayList, HttpStatus.OK);
    }

    @RequestMapping(value = "/get/allcompanies", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Company>> getAllCompanies() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.COMPANY_TABLE);
        DBCursor cursor = collection.find();
        ArrayList<Company> companyArrayList = new ArrayList<>();
        while (cursor.hasNext()) {
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

    @RequestMapping(value = "/get/allproducts", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Product>> getAllProducts() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);

        DBCollection collection = database.getCollection(Constants.PRODUCT_TABLE);
        DBCursor cursor = collection.find();
        ArrayList<Product> productArrayList = new ArrayList<>();
        while (cursor.hasNext()) {
            Map map = cursor.next().toMap();
            map.remove("_id");
            Product product = new Product();
            product.setProductName((String) map.get("productName"));
            product.setProductId((String) map.get("productId"));
            product.setCompanyId((String) map.get("companyId"));
            product.setPrice((String) map.get("price"));
            product.setCategory((String) map.get("price"));
            productArrayList.add(product);
        }

        return new ResponseEntity<ArrayList<Product>>(productArrayList, HttpStatus.OK);
    }


    @RequestMapping(value = "/get/myproducts", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<Product>> getMyProducts(@RequestParam("customerId") String customerId) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB(Constants.DB_NAME);
        DBCollection collection = database.getCollection(Constants.CUSTOMER_TABLE);
        BasicDBObject customerBasic = new BasicDBObject();
        customerBasic.put("customerId", customerId);
        DBObject object = collection.findOne(customerBasic);
        Map map = object.toMap();
        ArrayList<Product> products = new ArrayList<>();
        BasicDBList productNames = (BasicDBList) object.get("products");
        Iterator iter = productNames.iterator();
        while (iter.hasNext()) {
            collection = database.getCollection(Constants.PRODUCT_TABLE);
            BasicDBObject productObject = new BasicDBObject();
            productObject.put("productId", iter.next().toString());
            object = collection.findOne(productObject);
            Product product = new Product();
            product.setCategory((String) object.get("category"));
            product.setCompanyId((String) object.get("companyId"));
            product.setPrice((String) object.get("price"));
            product.setProductId((String) object.get("productId"));
            product.setProductName((String) object.get("productName"));
            products.add(product);
        }
        return new ResponseEntity<ArrayList<Product>>(products, HttpStatus.OK);
    }

}
