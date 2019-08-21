/*
 * COPYRIGHT 2019 SEAGATE LLC
 *
 * THIS DRAWING/DOCUMENT, ITS SPECIFICATIONS, AND THE DATA CONTAINED
 * HEREIN, ARE THE EXCLUSIVE PROPERTY OF SEAGATE TECHNOLOGY
 * LIMITED, ISSUED IN STRICT CONFIDENCE AND SHALL NOT, WITHOUT
 * THE PRIOR WRITTEN PERMISSION OF SEAGATE TECHNOLOGY LIMITED,
 * BE REPRODUCED, COPIED, OR DISCLOSED TO A THIRD PARTY, OR
 * USED FOR ANY PURPOSE WHATSOEVER, OR STORED IN A RETRIEVAL SYSTEM
 * EXCEPT AS ALLOWED BY THE TERMS OF SEAGATE LICENSES AND AGREEMENTS.
 *
 * YOU SHOULD HAVE RECEIVED A COPY OF SEAGATE'S LICENSE ALONG WITH
 * THIS RELEASE. IF NOT PLEASE CONTACT A SEAGATE REPRESENTATIVE
 * http://www.seagate.com/contact
 *
 * Original author:  Shalaka Dharap
 * Original creation date: 12-Aug-2019
 */

package com.seagates3.acl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

import com.seagates3.exception.GrantListFullException;
import com.seagates3.model.Account;
import com.seagates3.model.Requestor;
import com.seagates3.util.BinaryUtil;

@PowerMockIgnore({"javax.management.*"}) @RunWith(PowerMockRunner.class)
    @PrepareForTest({Files.class}) public class ACLCreatorTest {

 private
  ACLCreator spyAclCreator;
 private
  Requestor requestor;
 private
  Account account1, account2;
 private
  Map<String, String> requestBody = null;
  String aclXmlPath = null;
  File xmlFile = null;

  @Before public void setup() {
    account1 = new Account();
    account1.setId("1");
    account1.setName("Acc1");
    account1.setCanonicalId("fsdfsfsfdsfd12DD");
    account2 = new Account();
    account2.setId("2");
    account2.setName("Acc2");
    account2.setCanonicalId("wwQQadgfhdfsfsfdsfd12DD");
    requestor = new Requestor();
    requestor.setAccount(account1);
    spyAclCreator = Mockito.spy(new ACLCreator());
    aclXmlPath = "../resources/defaultAclTemplate.xml";
    xmlFile = new File(aclXmlPath);
  }

  /**
   * Below will test default acl creation
   *
   * @throws Exception
   */
  @Test public void testCreateDefaultAcl() throws Exception {
    Mockito.doReturn(
                new String(Files.readAllBytes(Paths.get(xmlFile.getPath()))))
        .when(spyAclCreator)
        .checkAndCreateDefaultAcp();
    String aclXml = spyAclCreator.createDefaultAcl(requestor);
    Assert.assertNotNull(aclXml);
    AccessControlPolicy acp = new AccessControlPolicy(aclXml);
    Assert.assertEquals(acp.getAccessControlList().getGrantList().size(), 1);
    Assert.assertEquals(acp.getAccessControlList()
                            .getGrantList()
                            .get(0)
                            .getGrantee()
                            .getCanonicalId(),
                        "fsdfsfsfdsfd12DD");
    Assert.assertEquals(
        acp.getAccessControlList().getGrantList().get(0).getPermission(),
        "FULL_CONTROL");
    Assert.assertEquals(acp.getOwner().getCanonicalId(), "fsdfsfsfdsfd12DD");
  }

  /**
   * Below will test acl creation with permission header
   *
   * @throws GrantListFullException
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws TransformerException
   */
  @Test public void testCreateAclFromPermissionHeaders()
      throws GrantListFullException,
      IOException, ParserConfigurationException, SAXException,
      TransformerException {
    Map<String, List<Account>> accountPermissionMap = new HashMap<>();
    List<Account> accountList = new ArrayList<>();
    requestBody = new TreeMap<>();
    accountList.add(account1);
    accountList.add(account2);
    accountPermissionMap.put("x-amz-grant-full-control", accountList);
    Mockito.doReturn(
                new String(Files.readAllBytes(Paths.get(xmlFile.getPath()))))
        .when(spyAclCreator)
        .checkAndCreateDefaultAcp();
    String aclXml = spyAclCreator.createAclFromPermissionHeaders(
        requestor, accountPermissionMap, requestBody);
    Assert.assertNotNull(aclXml);
    AccessControlPolicy acp = new AccessControlPolicy(aclXml);
    Assert.assertEquals(acp.getAccessControlList().getGrantList().size(), 2);
    Assert.assertEquals(acp.getAccessControlList()
                            .getGrantList()
                            .get(0)
                            .getGrantee()
                            .getCanonicalId(),
                        "fsdfsfsfdsfd12DD");
    Assert.assertEquals(
        acp.getAccessControlList().getGrantList().get(0).getPermission(),
        "FULL_CONTROL");
    Assert.assertEquals(acp.getAccessControlList()
                            .getGrantList()
                            .get(1)
                            .getGrantee()
                            .getCanonicalId(),
                        "wwQQadgfhdfsfsfdsfd12DD");
    Assert.assertEquals(
        acp.getAccessControlList().getGrantList().get(1).getPermission(),
        "FULL_CONTROL");
    Assert.assertEquals(acp.getOwner().getCanonicalId(), "fsdfsfsfdsfd12DD");
  }

  /**
   * Below test will pass acl through request body and check the response
   *
   * @throws GrantListFullException
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws TransformerException
   */
  @Test public void testCreateAclFromPermissionHeadersAclInRequestBody()
      throws GrantListFullException,
      IOException, ParserConfigurationException, SAXException,
      TransformerException {
    Map<String, List<Account>> accountPermissionMap = new HashMap<>();
    List<Account> accountList1 = new ArrayList<>();
    requestBody = new TreeMap<>();
    accountList1.add(account1);
    List<Account> accountList2 = new ArrayList<>();
    accountList2.add(account2);
    accountPermissionMap.put("x-amz-grant-full-control", accountList1);
    accountPermissionMap.put("x-amz-grant-write-acp", accountList2);
    String acl =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<AccessControlPolicy" +
        " xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">" + "<Owner><ID>" +
        "b103e16d027d24270d8facf37a48b141fd88ac8f43f9f942b91ba1cf1dc33f71" +
        "</ID>" +
        "<DisplayName>kirungeb</DisplayName></Owner><AccessControlList>" +
        "<Grant>" + "<Grantee " +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
        "xsi:type=\"CanonicalUser\"><ID>" +
        "b103e16d027d24270d8facf37a48b141fd88ac8f43f9f942b91ba1cf1dc33f71" +
        "</ID>" + "<DisplayName>kirungeb</DisplayName></Grantee>" +
        "<Permission>FULL_CONTROL</Permission></Grant></AccessControlList>" +
        "</AccessControlPolicy>";

    requestBody.put("ACL", BinaryUtil.encodeToBase64String(acl));
    String aclXml = spyAclCreator.createAclFromPermissionHeaders(
        requestor, accountPermissionMap, requestBody);
    AccessControlPolicy acp = new AccessControlPolicy(aclXml);
    Assert.assertEquals(acp.getAccessControlList().getGrantList().size(), 2);
    Assert.assertEquals(acp.getAccessControlList()
                            .getGrantList()
                            .get(0)
                            .getGrantee()
                            .getCanonicalId(),
                        "fsdfsfsfdsfd12DD");
    Assert.assertEquals(
        acp.getAccessControlList().getGrantList().get(0).getPermission(),
        "FULL_CONTROL");
    Assert.assertEquals(acp.getAccessControlList()
                            .getGrantList()
                            .get(1)
                            .getGrantee()
                            .getCanonicalId(),
                        "wwQQadgfhdfsfsfdsfd12DD");
    Assert.assertEquals(
        acp.getAccessControlList().getGrantList().get(1).getPermission(),
        "WRITE_ACP");
    Assert.assertNotNull(aclXml);
    Assert.assertEquals(acp.getOwner().getDisplayName(), "kirungeb");
  }
}
