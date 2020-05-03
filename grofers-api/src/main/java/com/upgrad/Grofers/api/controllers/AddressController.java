package com.upgrad.Grofers.api.controllers;

import com.upgrad.Grofers.api.*;
import com.upgrad.Grofers.service.business.AddressService;
import com.upgrad.Grofers.service.business.AddressServiceImpl;
import com.upgrad.Grofers.service.business.CustomerService;
import com.upgrad.Grofers.service.entity.*;
import com.upgrad.Grofers.service.exception.AddressNotFoundException;
import com.upgrad.Grofers.service.exception.AuthorizationFailedException;
import com.upgrad.Grofers.service.exception.CategoryNotFoundException;
import com.upgrad.Grofers.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;
    private String authorization;

    /**
     * A controller method to save an address in the database.
     *
     * @body SaveAddressRequest - This argument contains all the attributes required to store address details in the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<SaveAddressResponse> type object along with Http status CREATED.
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException///
     */
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> address(@RequestHeader("authorization") final String authorization, SaveAddressRequest saveAddressRequest) throws AuthorizationFailedException,SaveAddressException,AddressNotFoundException
    {
        final AddressEntity addressEntity=new AddressEntity();
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality((saveAddressRequest.getLocality()));
        addressEntity.setPincode(saveAddressRequest.getPincode());
        final CustomerAddressEntity customerAddressEntity=new CustomerAddressEntity();
        customerAddressEntity.setAddress(addressEntity);
        AddressServiceImpl addressService=new AddressServiceImpl();
        AddressEntity createdAddressEntity= addressService.saveAddress(addressEntity,customerAddressEntity);
        SaveAddressResponse addressResponse=new SaveAddressResponse().id(createdAddressEntity.getUuid()).status("created");
        return new ResponseEntity<SaveAddressResponse>(addressResponse, HttpStatus.CREATED);
    }

    /**
     * A controller method to delete an address from the database.
     *
     * @param addressId    - The uuid of the address to be deleted from the database.
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<DeleteAddressResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> userDelete(@PathVariable("addressId") final String addressId,
                                                         @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AddressNotFoundException {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }
        AddressEntity addressEntity=new AddressEntity();
        AddressServiceImpl addressService=new AddressServiceImpl();
        CustomerAddressEntity customerAddressEntity=new CustomerAddressEntity();
       String uuid =addressEntity.getUuid();// addressService.deleteAddress(customerAddressEntity.getAddress());

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);
    }


    /**
     * A controller method to get all address from the database.
     *
     * @param authorization - A field in the request header which contains the JWT token.
     * @return - ResponseEntity<AddressListResponse> type object along with Http status OK.
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAddress(@RequestHeader("authorization") String authorization) throws AuthorizationFailedException {
        CustomerEntity customerEntity=new CustomerEntity();
         CustomerAddressEntity customerAddressEntity =new CustomerAddressEntity();
        customerAddressEntity= (CustomerAddressEntity) addressService.getAllAddress(customerService.getCustomer(authorization));
        List<AddressEntity> address = (List<AddressEntity>) customerAddressEntity.getAddress();
        Comparator<AddressEntity> compareByAddressSavedTime= new Comparator<AddressEntity>() {
            @Override
            public int compare(AddressEntity i1, AddressEntity i2) {
                return i1.getId().compareTo(i2.getId());
            }
        };
        Collections.sort(address, compareByAddressSavedTime);
        List<ItemList> itemLists = new ArrayList<ItemList>();
        AddressListResponse addressListResponse=new AddressListResponse();
        addressListResponse.getAddresses();
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);

    }

}
