/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.testsuite.proxy;

import java.util.ArrayList;
import java.util.List;

import javax.sip.ListeningPoint;
import javax.sip.SipProvider;
import javax.sip.address.SipURI;
import javax.sip.message.Response;

import org.apache.log4j.Logger;
import org.mobicents.servlet.sip.SipServletTestCase;
import org.mobicents.servlet.sip.testsuite.ProtocolObjects;
import org.mobicents.servlet.sip.testsuite.TestSipListener;

public class ProxyRecordRouteReInviteTest extends SipServletTestCase {	
	private static transient Logger logger = Logger.getLogger(ProxyRecordRouteReInviteTest.class);
	private static final boolean AUTODIALOG = true;
	TestSipListener sender;
	TestSipListener receiver;
	ProtocolObjects senderProtocolObjects;
	ProtocolObjects	receiverProtocolObjects;


	private static final int TIMEOUT = 20000;

	public ProxyRecordRouteReInviteTest(String name) {
		super(name);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();				
	}

	public void testProxyCallerSendBye() throws Exception {
		setupPhones(ListeningPoint.UDP);
		String fromName = "unique-location";
		String fromSipAddress = "sip-servlets.com";
		SipURI fromAddress = senderProtocolObjects.addressFactory.createSipURI(
				fromName, fromSipAddress);		
		
		String toSipAddress = "sip-servlets.com";
		String toUser = "proxy-receiver";
		SipURI toAddress = senderProtocolObjects.addressFactory.createSipURI(
				toUser, toSipAddress);
		
		sender.sendSipRequest("INVITE", fromAddress, toAddress, null, null, false);		
		Thread.sleep(TIMEOUT);
		assertTrue(sender.isAckSent());
		assertTrue(receiver.isAckReceived());
		receiver.setAckReceived(false);
		sender.setAckSent(false);
		sender.sendInDialogSipRequest("INVITE", null, null, null, null);		
		Thread.sleep(TIMEOUT);
		assertTrue(sender.isAckSent());
		assertTrue(receiver.isAckReceived());
		receiver.setAckReceived(false);
		sender.setAckSent(false);		
		receiver.sendInDialogSipRequest("INVITE", null, null, null, null);
		Thread.sleep(TIMEOUT);
		assertTrue(receiver.isAckSent());
		assertTrue(sender.isAckReceived());
		sender.sendBye();
		Thread.sleep(TIMEOUT);
		assertTrue(receiver.getByeReceived());
		assertTrue(sender.getOkToByeReceived());		
	}
	
	public void testProxyCalleeSendBye() throws Exception {
		setupPhones(ListeningPoint.UDP);
		String fromName = "unique-location";
		String fromSipAddress = "sip-servlets.com";
		SipURI fromAddress = senderProtocolObjects.addressFactory.createSipURI(
				fromName, fromSipAddress);		
		
		String toSipAddress = "sip-servlets.com";
		String toUser = "proxy-receiver";
		SipURI toAddress = senderProtocolObjects.addressFactory.createSipURI(
				toUser, toSipAddress);
		
		sender.sendSipRequest("INVITE", fromAddress, toAddress, null, null, false);		
		Thread.sleep(TIMEOUT);
		assertTrue(sender.isAckSent());
		assertTrue(receiver.isAckReceived());
		receiver.setAckReceived(false);
		sender.setAckSent(false);
		sender.sendInDialogSipRequest("INVITE", null, null, null, null);		
		Thread.sleep(TIMEOUT);
		assertTrue(sender.isAckSent());
		assertTrue(receiver.isAckReceived());
		receiver.setAckReceived(false);
		sender.setAckSent(false);		
		receiver.sendInDialogSipRequest("INVITE", null, null, null, null);
		Thread.sleep(TIMEOUT);
		assertTrue(receiver.isAckSent());
		assertTrue(sender.isAckReceived());
		receiver.sendBye();
		Thread.sleep(TIMEOUT);
		assertTrue(sender.getByeReceived());
		assertTrue(receiver.getOkToByeReceived());		
	}
	
	/*
	 * Non regression test for Issue 1792
	 */
	public void testProxyCancelTCP() throws Exception {
		tomcat.addSipConnector(serverName, sipIpAddress, 5070, ListeningPoint.TCP);
		setupPhones(ListeningPoint.TCP);
		String fromName = "unique-location";
		String fromSipAddress = "sip-servlets.com";
		SipURI fromAddress = senderProtocolObjects.addressFactory.createSipURI(
				fromName, fromSipAddress);		
		
		String toSipAddress = "sip-servlets.com";
		String toUser = "proxy-receiver";
		SipURI toAddress = senderProtocolObjects.addressFactory.createSipURI(
				toUser, toSipAddress);
		
		List<Integer> provisionalResponsesToSend = new ArrayList<Integer>();
		provisionalResponsesToSend.add(Response.TRYING);
		provisionalResponsesToSend.add(Response.RINGING);
		receiver.setProvisionalResponsesToSend(provisionalResponsesToSend);
		
		receiver.setWaitForCancel(true);
		sender.sendSipRequest("INVITE", fromAddress, toAddress, null, null, false);
		Thread.sleep(1000);
		sender.sendCancel();
		Thread.sleep(TIMEOUT);
		assertTrue(receiver.isCancelReceived());
		assertTrue(sender.isCancelOkReceived());		
		assertTrue(sender.isRequestTerminatedReceived());			
	}

	public void setupPhones(String transport) throws Exception {
		senderProtocolObjects = new ProtocolObjects("proxy-sender",
				"gov.nist", transport, AUTODIALOG, null);
		receiverProtocolObjects = new ProtocolObjects("proxy-receiver",
				"gov.nist", transport, AUTODIALOG, null);
		sender = new TestSipListener(5080, 5070, senderProtocolObjects, false);
		sender.setRecordRoutingProxyTesting(true);
		SipProvider senderProvider = sender.createProvider();

		receiver = new TestSipListener(5057, 5070, receiverProtocolObjects, false);
		receiver.setRecordRoutingProxyTesting(true);
		SipProvider receiverProvider = receiver.createProvider();

		receiverProvider.addSipListener(receiver);
		senderProvider.addSipListener(sender);

		senderProtocolObjects.start();
		receiverProtocolObjects.start();
	}
	
	@Override
	public void tearDown() throws Exception {
		senderProtocolObjects.destroy();
		receiverProtocolObjects.destroy();			
		logger.info("Test completed");
		super.tearDown();
	}

	@Override
	public void deployApplication() {
		assertTrue(tomcat
				.deployContext(
						projectHome
								+ "/sip-servlets-test-suite/applications/proxy-sip-servlet/src/main/sipapp",
						"sip-test-context", "sip-test"));
	}

	@Override
	protected String getDarConfigurationFile() {
		return "file:///"
				+ projectHome
				+ "/sip-servlets-test-suite/testsuite/src/test/resources/"
				+ "org/mobicents/servlet/sip/testsuite/proxy/simple-sip-servlet-dar.properties";
	}	
}
