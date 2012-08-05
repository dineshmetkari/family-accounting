package com.jasonzqshen.familyaccounting.core.transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import com.jasonzqshen.familyaccounting.core.exception.BalanceNotZero;
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoRootElem;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage.MessageType;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;

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
	 * save document
	 * 
	 * @param head
	 */
	boolean saveDocument(HeadEntity head) {
		if (head.isSaved() == false) {
			MonthIdentity monthId = head.getMonthId();
			HeadEntityCollection collection = _list.get(monthId);

			// set document number
			DocumentNumber num = null;
			HeadEntity[] entities = collection.getEntities();
			if (entities.length == 0) {
				try {
					num = new DocumentNumber("1000000001".toCharArray());
				} catch (IdentityTooLong e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IdentityNoData e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IdentityInvalidChar e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				HeadEntity last = entities[entities.length - 1];
				num = last.getDocumentNumber().next();
			}

			head._docNumber = num;

			collection.add(head);
		}

		try {
			store(head.getMonthId());
		} catch (SystemException e) {
			return false;
		}
		return true;
	}

	/**
	 * store based on month identity
	 * 
	 * @param monthId
	 * @throws SystemException
	 */
	public void store(MonthIdentity monthId) throws SystemException {
		// store master data
		MasterDataManagement manage = _coreDriver.getMasterDataManagement();
		manage.store();

		HeadEntityCollection collection = _list.get(monthId);

		String filePath = this.generateFilePath(monthId);
		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new SystemException(e);
			}
		}
		String xdoc = collection.toXML();
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(xdoc, 0, xdoc.length());
			writer.close();
		} catch (IOException e) {
			throw new SystemException(e);
		}

	}

	/**
	 * reverse document
	 * 
	 * @param head
	 * @return
	 * @throws
	 */
	public HeadEntity reverseDocument(DocumentIdentity docId) {
		try {
			HeadEntity orgHead = this.getEntity(docId);

			HeadEntity head = new HeadEntity(_coreDriver);
			head.setPostingDate(orgHead.getPostingDate());
			head.setDocumentType(orgHead.getDocumentType());

			// items
			ItemEntity[] items = orgHead.getItems();
			for (int i = 0; i < items.length; ++i) {
				ItemEntity newItem = head.createEntity();
				newItem.setBusinessArea(items[i].getBusinessArea());
				AccountType type = items[i].getAccountType();
				if (type == AccountType.GL_ACCOUNT) {
					newItem.setGLAccount(items[i].getGLAccount());
				} else if (type == AccountType.VENDOR) {
					newItem.setVendor(items[i].getVendor(),
							items[i].getGLAccount());
				} else if (type == AccountType.CUSTOMER) {
					newItem.setCustomer(items[i].getCustomer(),
							items[i].getGLAccount());
				}

				CreditDebitIndicator cd_indicator = items[i].getCDIndicator();
				if (cd_indicator == CreditDebitIndicator.DEBIT) {
					newItem.setAmount(CreditDebitIndicator.CREDIT,
							items[i].getAmount());
				} else if (cd_indicator == CreditDebitIndicator.CREDIT) {
					newItem.setAmount(CreditDebitIndicator.DEBIT,
							items[i].getAmount());
				}
			}

			head.save();

			head._isReversed = true;
			orgHead._isReversed = true;
			head._ref = head.getDocIdentity();
			orgHead._ref = head.getDocIdentity();

			this.store(head.getMonthId());
			return head;
		} catch (MasterDataIdentityNotDefined e) {
			return null;
		} catch (NullValueNotAcceptable e) {
			return null;
		} catch (MandatoryFieldIsMissing e) {
			return null;
		} catch (BalanceNotZero e) {
			return null;
		} catch (SystemException e) {
			return null;
		}
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
		ArrayList<MonthIdentity> idArray = new ArrayList<MonthIdentity>(
				_list.keySet());
		Collections.sort(idArray);
		MonthIdentity[] ids = new MonthIdentity[idArray.size()];
		int i = 0;
		for (MonthIdentity id : idArray) {
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
