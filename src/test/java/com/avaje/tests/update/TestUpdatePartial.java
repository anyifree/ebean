package com.avaje.tests.update;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.tests.model.basic.Customer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestUpdatePartial extends BaseTestCase {

  @Test
  public void test() {

    Customer c = new Customer();
    c.setName("TestUpdateMe");
    c.setStatus(Customer.Status.ACTIVE);
    c.setSmallnote("a note");

    Ebean.save(c);
    checkDbStatusValue(c.getId(), "A");

    Customer c2 = Ebean.find(Customer.class)
        .select("status, smallnote")
        .setId(c.getId())
        .findUnique();
    
    c2.setStatus(Customer.Status.INACTIVE);
    c2.setSmallnote("2nd note");

    Ebean.save(c2);
    checkDbStatusValue(c.getId(), "I");

    Customer c3 = Ebean.find(Customer.class)
        .select("status")
        .setId(c.getId())
        .findUnique();
   
    c3.setStatus(Customer.Status.NEW);
    c3.setSmallnote("3rd note");

    Ebean.save(c3);
    checkDbStatusValue(c.getId(), "N");
  }

  private void checkDbStatusValue(Integer custId, String dbStatus) {
    SqlQuery sqlQuery = Ebean.createSqlQuery("select id, status from o_customer where id = ?");
    sqlQuery.setParameter(1, custId);
    SqlRow sqlRow = sqlQuery.findUnique();
    String status = sqlRow.getString("status");
    assertEquals(dbStatus, status);
  }

  /**
   * If we have no changes detected, don't execute an Update and don't update the Version column.
   */
  @Test
  public void testWithoutChangesAndVersionColumn() {
    // arrange
    Customer customer = new Customer();
    customer.setName("something");

    Ebean.save(customer);

    // act
    Customer customerWithoutChanges = Ebean.find(Customer.class, customer.getId());
    Ebean.save(customerWithoutChanges);

    // assert
    assertEquals(customer.getUpdtime().getTime(), customerWithoutChanges.getUpdtime().getTime());
  }
}
