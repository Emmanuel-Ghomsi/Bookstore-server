package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import java.time.DateTimeException;
import java.time.*;
import java.util.Date;
import java.util.regex.*;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {

	private BookDao bookDao;
	private OrderDao orderDao;
	private LineItemDao lineItemDao;
	private CustomerDao customerDao;
	private Random random = new Random();

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void setLineItemDao(LineItemDao lineItemDao) {
		this.lineItemDao = lineItemDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) {
		Order order = orderDao.findByOrderId(orderId);
		Customer customer = customerDao.findByCustomerId(order.getCustomerId());
		List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
		List<Book> books = lineItems
				.stream()
				.map(lineItem -> bookDao.findByBookId(lineItem.getBookId()))
				.collect(Collectors.toList());
		return new OrderDetails(order, customer, lineItems, books);
	}

	@Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {

		validateCustomer(customerForm);
		validateCart(cart);

		try (Connection connection = JdbcUtils.getConnection()) {
			Date date = getDate(
					customerForm.getCcExpiryMonth(),
					customerForm.getCcExpiryYear());
			return performPlaceOrderTransaction(
					customerForm.getName(),
					customerForm.getAddress(),
					customerForm.getPhone(),
					customerForm.getEmail(),
					customerForm.getCcNumber(),
					date, cart, connection);
		} catch (SQLException e) {
			throw new BookstoreDbException("Error during close connection for customer order", e);
		}
	}


	private void validateCustomer(CustomerForm customerForm) {

		String name = customerForm.getName();
		String address = customerForm.getAddress();
		String phone = customerForm.getPhone();
		String email = customerForm.getEmail();
		String ccNumber = customerForm.getCcNumber();

		if (name == null || name.length() < 4 || name.length() > 45) {
			throw new ApiException.InvalidParameter("Invalid name field");
		}

		if (address == null || address.length() < 4 || address.length() > 45) {
			throw new ApiException.InvalidParameter("Invalid address field");
		}

		if (phone == null || phone.equals("") || phone.replaceAll("\\D", "").length() != 10) {
			throw new ApiException.InvalidParameter("Invalid phone field");
		}

		if (email == null || email.length() == 0 || doesNotLookLikeAnEmail(email) || email.endsWith(".")) {
			throw new ApiException.InvalidParameter("Invalid email field");
		}

		if (ccNumber == null || ccNumber.equals("") || ccNumber.replaceAll("\\D", "").length() < 14 || ccNumber.replaceAll("\\D", "").length() > 16) {
			throw new ApiException.InvalidParameter("Invalid ccNumber field");
		}

		if (customerForm.getCcExpiryMonth() == null || customerForm.getCcExpiryYear() == null || customerForm.getCcExpiryMonth().equals("") || customerForm.getCcExpiryYear().equals("") || expiryDateIsInvalid(customerForm.getCcExpiryMonth(), customerForm.getCcExpiryYear())) {
			throw new ApiException.InvalidParameter("Invalid expiry");
		}
	}

	private boolean expiryDateIsInvalid(String ccExpiryMonth, String ccExpiryYear) {
		try {
			if(Integer.parseInt(ccExpiryYear) > YearMonth.now().getYear() || (Integer.parseInt(ccExpiryMonth) >= YearMonth.now().getMonthValue() && Integer.parseInt(ccExpiryYear) == YearMonth.now().getYear()) ) {
				return false;
			}
		} catch (NumberFormatException e) {
			return true;
		}
		return true;
	}

	private void validateCart(ShoppingCart cart) {

		if (cart.getItems().size() <= 0) {
			throw new ApiException.InvalidParameter("Cart is empty.");
		}

		cart.getItems().forEach(item-> {
			if (item.getQuantity() < 0 || item.getQuantity() > 99) {
				throw new ApiException.InvalidParameter("Invalid quantity");
			}
			Book databaseBook = bookDao.findByBookId(item.getBookId());

			if(databaseBook.getPrice() != item.getBookForm().getPrice()) {
				throw new ApiException.InvalidParameter("Invalid price");
			}

			if(databaseBook.getCategoryId() != item.getBookForm().getCategoryId()) {
				throw new ApiException.InvalidParameter("Invalid category");
			}
		});
	}

	private Date getDate(String monthString, String yearString) {
		if(monthString.length() < 2) monthString = "0" + monthString;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM");
			return formatter.parse(yearString + "/" + monthString);
		} catch (ParseException e) {
			throw new ApiException.InvalidParameter("Invalid date");
		}
	}

	private int generateConfirmationNumber() {
		return random.nextInt(999999999);
	}

	private long performPlaceOrderTransaction(
			String name, String address, String phone,
			String email, String ccNumber, Date date,
			ShoppingCart cart, Connection connection) {
		try {
			connection.setAutoCommit(false);
			long customerId = customerDao.create(
					connection, name, address, phone, email,
					ccNumber, date);
			long customerOrderId = orderDao.create(
					connection,
					cart.getComputedSubtotal() + cart.getSurcharge(),
					generateConfirmationNumber(), customerId);
			for (ShoppingCartItem item : cart.getItems()) {
				lineItemDao.create(connection, customerOrderId,
						item.getBookId(), item.getQuantity());
			}
			connection.commit();
			return customerOrderId;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new BookstoreDbException("Failed to roll back transaction", e1);
			}
			return 0;
		}
	}

	private static Pattern SIMPLE_EMAIL_REGEX = Pattern.compile("^\\S+@\\S+$");

	private boolean doesNotLookLikeAnEmail(String email) {
		return !SIMPLE_EMAIL_REGEX.matcher(email).matches();
	}

}
