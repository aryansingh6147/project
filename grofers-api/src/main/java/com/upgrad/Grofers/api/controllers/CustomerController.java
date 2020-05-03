package com.upgrad.Grofers.api.controllers;


import com.upgrad.Grofers.api.*;
import com.upgrad.Grofers.service.business.AddressService;
import com.upgrad.Grofers.service.business.AddressServiceImpl;
import com.upgrad.Grofers.service.business.CustomerService;
import com.upgrad.Grofers.service.business.CustomerServiceImpl;
import com.upgrad.Grofers.service.entity.CustomerAuthEntity;
import com.upgrad.Grofers.service.entity.CustomerEntity;
import com.upgrad.Grofers.service.exception.AuthenticationFailedException;
import com.upgrad.Grofers.service.exception.AuthorizationFailedException;
import com.upgrad.Grofers.service.exception.SignUpRestrictedException;
import com.upgrad.Grofers.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	@Autowired
    private CustomerService customerService;

	/**
	 * A controller method for customer signup.
	 *
	 * @param signupCustomerRequest - This argument contains all the attributes required to store customer details in the database.
	 * @return - ResponseEntity<SignupCustomerResponse> type object along with Http status CREATED.
	 * @throws SignUpRestrictedException
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SignupCustomerResponse> register(@RequestBody final SignupCustomerRequest signupCustomerRequest)
			throws SignUpRestrictedException {
		final CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setUuid(UUID.randomUUID().toString());
		customerEntity.setFirstName(signupCustomerRequest.getFirstName());
		customerEntity.setLastName(signupCustomerRequest.getLastName());
		customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
		customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
		customerEntity.setPassword(signupCustomerRequest.getPassword());
		CustomerServiceImpl customerService=new CustomerServiceImpl();
		CustomerEntity createdCoustmerserEntity = customerService.saveCustomer(customerEntity);
		SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(createdCoustmerserEntity.getUuid())
				.status("Customer SUCCESSFULLY REGISTERED");
		return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
	}

	/**
	 * A controller method for customer authentication.
	 *
	 * @param authorization - A field in the request header which contains the customer credentials as Basic authentication.
	 * @return - ResponseEntity<LoginResponse> type object along with Http status OK.
	 * @throws AuthenticationFailedException
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/customer/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization)
			throws AuthenticationFailedException {
		String[] authorizationArray = authorization.split("Basic ");
		byte[] decode = Base64.getDecoder().decode(authorizationArray[1]);
		String decodedText = new String(decode);
		String[] authArray = decodedText.split(":");
		CustomerServiceImpl customerService=new CustomerServiceImpl();
		CustomerAuthEntity customerAuthEntity = customerService.authenticate(authArray[0], authArray[1]);
		CustomerEntity customer = customerAuthEntity.getCustomer();

		LoginResponse loginResponse = new LoginResponse().id(customer.getUuid()).message("SIGNED IN SUCCESSFULLY");

		HttpHeaders headers = new HttpHeaders();
		headers.add("access-token", customerAuthEntity.getAccessToken());
		return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
	}

	/**
	 * A controller method for customer logout.
	 *
	 * @param accessToken - A field in the request header which contains the JWT token.
	 * @return - ResponseEntity<LogoutResponse> type object along with Http status OK.
	 * @throws AuthorizationFailedException
	 */
	@RequestMapping(method = RequestMethod.POST, path = "/customer/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String accessToken)
			throws AuthorizationFailedException {
		LogoutResponse logoutResponse = null;
		// Logic to handle Bearer <accesstoken>
		// User can give only Access token or Bearer <accesstoken> as input.
		String bearerToken = null;
		try {
			bearerToken = accessToken.split("Bearer ")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			bearerToken = accessToken;
		}
		CustomerServiceImpl customerService=new CustomerServiceImpl();
		CustomerAuthEntity customerAuthEntity = customerService.logout(bearerToken);
		if (customerAuthEntity != null) {
			logoutResponse = new LogoutResponse().id(customerAuthEntity.getUuid()).message("SIGN OUT SUCCESSFULLY");
		}
		return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
	}

	/**
	 * A controller method for updating customer password.
	 *
	 * @param updatePasswordRequest - This argument contains all the attributes required to update customer password in the database.
	 * @param authorization - A field in the request header which contains the JWT token.
	 * @return - ResponseEntity<LogoutResponse> type object along with Http status OK.
	 * @throws AuthorizationFailedException
	 * @throws UpdateCustomerException
	 */
    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public <updatePasswordRequest> ResponseEntity<LogoutResponse> updatePassword(@RequestHeader("authorization") final String authorization , final String updatePasswordRequest)throws UpdateCustomerException, AuthorizationFailedException
    {
		LogoutResponse logoutResponse = null;
		CustomerService customerService=new CustomerServiceImpl();
		CustomerEntity customerEntity=new CustomerEntity();
		String oldPassword=customerEntity.getPassword();
		CustomerEntity customerEntity1 = customerService.updateCustomerPassword(oldPassword,updatePasswordRequest,customerEntity);
		logoutResponse = new LogoutResponse().id(customerEntity1.getUuid()).message("password updated");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }
}
