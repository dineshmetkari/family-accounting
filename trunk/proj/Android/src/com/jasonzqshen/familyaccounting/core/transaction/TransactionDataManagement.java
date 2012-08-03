package com.jasonzqshen.familyaccounting.core.transaction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.NoRootElem;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage.MessageType;

public class TransactionDataManagement {

	public static final String TRANSACTION_DATA_FOLDER = "transaction_data";
	public final CoreDriver _coreDriver;
	private final Hashtable<MonthIdentity, HeadEntityCollection> _list;

	/**
	 * constructor
	 * 
	 * @param coreDriver
	 */
	public TransactionDataManagement(CoreDriver coreDriver) {
		_coreDriver = coreDriver;
		_list = new Hashtable<MonthIdentity, HeadEntityCollection>();
	}

	/**
	 * load data from file
	 * 
	 * @throws SystemException
	 */
	public void load(ArrayList<CoreMessage> messages) throws SystemException {
		// get the collection for each month
		final Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH) + 1;

		MonthIdentity monthId = null;
		try {
			monthId = new MonthIdentity(curYear, curMonth);
		} catch (FiscalYearRangeException e) {
			throw new SystemException(e);
		} catch (FiscalMonthRangeException e) {
			throw new SystemException(e);
		}

		for (MonthIdentity startId = _coreDriver.getStartMonthId(); startId
				.compareTo(monthId) <= 0; startId = startId.addMonth()) {
			load(startId, messages);
		}
	}

	/**
	 * load data from file based on month identity
	 * 
	 * @param monthId
	 * @throws SystemException
	 * @throws MandatoryFieldIsMissing
	 */
	public HeadEntityCollection load(MonthIdentity monthId,
			ArrayList<CoreMessage> messages) {
		String filePath = generateFilePath(monthId);
		HeadEntityCollection colletion = new HeadEntityCollection(monthId);
		_list.put(monthId, colletion);

		try {
			File file = new File(filePath);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;

			builder = docFactory.newDocumentBuilder();
			Document doc = builder.parse(file);

			// get root element
			NodeList nodeList = doc.getChildNodes();
			Element rootElem = null;

			// get root elem
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element elem = (Element) child;
					String nodeName = elem.getNodeName();
					if (nodeName.equals(TransDataUtils.XML_ROOT)) {
						rootElem = elem;
						break;
					}
				}
			}
			// no root element
			if (rootElem == null) {
				messages.add(new CoreMessage(
						CoreMessage.ERR_FILE_NOT_ROOT_ELEM,
						CoreMessage.MessageType.ERROR, new NoRootElem()));
			}

			nodeList = rootElem.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element elem = (Element) child;
					if (elem.getNodeName().equals(TransDataUtils.XML_DOCUMENT)) {

						HeadEntity head = HeadEntity.parse(_coreDriver, elem);
						colletion.add(head);
					}
				}
			}

		} catch (ParserConfigurationException e) {
			messages.add(new CoreMessage(String.format(
					CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
					MessageType.ERROR, e));
		} catch (SAXException e) {
			messages.add(new CoreMessage(String.format(
					CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
					MessageType.ERROR, e));
		} catch (IOException e) {
			messages.add(new CoreMessage(String.format(
					CoreMessage.ERR_FILE_NOT_EXISTS, filePath),
					MessageType.ERROR, e));
		} catch (MandatoryFieldIsMissing e) {
			messages.add(new CoreMessage(String.format(
					CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
					MessageType.ERROR, e));
		} catch (SystemException e) {
			messages.add(new CoreMessage(String.format(
					CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
					MessageType.ERROR, e));
		}

		return colletion;
	}

	/**
	 * generate transaction file path for the month identity
	 * 
	 * @param str
	 * @return
	 */
	private String generateFilePath(MonthIdentity monthId) {
		return String.format("%s/%s/%s.xml", _coreDriver.getRootPath(),
				TRANSACTION_DATA_FOLDER, monthId.toString());
	}

	/**
	 * store all data
	 */
	public void store() {

	}

	/**
	 * store based on month identity
	 * 
	 * @param monthId
	 */
	public void store(MonthIdentity monthId) {

	}

	/**
	 * get documents
	 * 
	 * @param monthId
	 * @return
	 */
	public HeadEntity[] getDocs(MonthIdentity monthId) {
		HeadEntityCollection collection = _list.get(monthId);
		return collection.getEntities();
	}

	/**
	 * get documents
	 * 
	 * @param fiscalYear
	 * @param fiscalMonth
	 * @return
	 */
	public HeadEntity[] getDocs(int fiscalYear, int fiscalMonth) {
		MonthIdentity monthId;
		try {
			monthId = new MonthIdentity(fiscalYear, fiscalMonth);
		} catch (FiscalYearRangeException e) {
			return null;
		} catch (FiscalMonthRangeException e) {
			return null;
		}

		return getDocs(monthId);
	}

	/**
	 * get all month identities
	 * 
	 * @return
	 */
	public MonthIdentity[] getAllMonthIds() {
		MonthIdentity[] ids = new MonthIdentity[_list.size()];
		int i = 0;
		for (MonthIdentity id : _list.keySet()) {
			ids[i++] = id;
		}

		return ids;
	}

	/**
	 * get document based on the document identity
	 * 
	 * @param docId
	 * @return
	 */
	public HeadEntity getEntity(DocumentIdentity docId) {
		HeadEntityCollection collection = _list.get(docId._monthIdentity);
		return collection.getEntity(docId);
	}
}
