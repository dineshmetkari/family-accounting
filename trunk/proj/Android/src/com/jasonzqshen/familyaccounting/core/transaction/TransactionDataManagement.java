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
import com.jasonzqshen.familyaccounting.core.exception.FiscalMonthRangeException;
import com.jasonzqshen.familyaccounting.core.exception.FiscalYearRangeException;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MandatoryFieldIsMissing;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.NoRootElem;
import com.jasonzqshen.familyaccounting.core.exception.NoTransactionDataFileException;
import com.jasonzqshen.familyaccounting.core.exception.NullValueNotAcceptable;
import com.jasonzqshen.familyaccounting.core.exception.TransactionDataFileFormatException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.AccountType;
import com.jasonzqshen.familyaccounting.core.utils.CoreMessage;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.MessageType;

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
	 *             bug
	 */
	public void load(ArrayList<CoreMessage> messages) {
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
		_coreDriver.logDebugInfo(
				this.getClass(),
				75,
				String.format("Current month identity is %s",
						monthId.toString()), MessageType.INFO);

		_coreDriver.logDebugInfo(this.getClass(), 75, String.format(
				"Starting month identity is %s", _coreDriver.getStartMonthId()
						.toString()), MessageType.INFO);

		for (MonthIdentity startId = _coreDriver.getStartMonthId(); startId
				.compareTo(monthId) <= 0; startId = startId.addMonth()) {
			try {
				load(startId, messages);
			} catch (TransactionDataFileFormatException e) {
				// nothing need to handle. whatever happen, the head collection
				// for the month identity has been add to the list.
				_coreDriver.logDebugInfo(this.getClass(), 93, e.toString(),
						MessageType.ERROR);
			} catch (NoTransactionDataFileException e) {
				// nothing need to handle. whatever happen, the head collection
				// for the month identity has been add to the list.
				_coreDriver.logDebugInfo(this.getClass(), 99, e.toString(),
						MessageType.ERROR);
			}
		}
	}

	/**
	 * load data from file based on month identity
	 * 
	 * @param monthId
	 * @throws TransactionDataFileFormatException
	 * @throws NoTransactionDataFileException
	 * @throws SystemException
	 * @throws MandatoryFieldIsMissing
	 */
	public void load(MonthIdentity monthId, ArrayList<CoreMessage> messages)
			throws TransactionDataFileFormatException,
			NoTransactionDataFileException {
		_coreDriver.logDebugInfo(
				this.getClass(),
				102,
				String.format("Loading transaction data %s ...",
						monthId.toString()), MessageType.INFO);

		String filePath = generateFilePath(monthId);
		_coreDriver.logDebugInfo(this.getClass(), 109,
				String.format("Transaction data file: %s .", filePath),
				MessageType.INFO);

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

			// get root element
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
						CoreMessage.ERR_FILE_NOT_ROOT_ELEM, MessageType.ERROR,
						new NoRootElem()));
				throw new TransactionDataFileFormatException(filePath);
			}

			nodeList = rootElem.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element elem = (Element) child;
					if (elem.getNodeName().equals(TransDataUtils.XML_DOCUMENT)) {

						HeadEntity head = HeadEntity.parse(_coreDriver, elem);
						_coreDriver
								.logDebugInfo(
										this.getClass(),
										172,
										String.format(
												"Document %s add to list during loading.",
												head.getDocIdentity()
														.toString()),
										MessageType.INFO);
						colletion.add(head);
					}
				}
			}

		} catch (ParserConfigurationException e) {
			_coreDriver.logDebugInfo(this.getClass(), 170, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (SAXException e) {
			_coreDriver.logDebugInfo(this.getClass(), 170, e.toString(),
					MessageType.ERROR);
			messages.add(new CoreMessage(CoreMessage.ERR_FILE_FORMAT_ERROR,
					MessageType.ERROR, new TransactionDataFileFormatException(
							filePath)));
			throw new TransactionDataFileFormatException(filePath);
		} catch (IOException e) {
			_coreDriver.logDebugInfo(this.getClass(), 170, e.toString(),
					MessageType.ERROR);
			messages.add(new CoreMessage(String.format(
					CoreMessage.ERR_FILE_NOT_EXISTS, filePath),
					MessageType.ERROR, e));
			throw new NoTransactionDataFileException(filePath);
		} catch (TransactionDataFileFormatException e) {
			_coreDriver.logDebugInfo(this.getClass(), 170, e.toString(),
					MessageType.ERROR);
			messages.add(new CoreMessage(String.format(
					CoreMessage.ERR_FILE_FORMAT_ERROR, filePath),
					MessageType.ERROR, e));
			throw new TransactionDataFileFormatException(filePath);
		}

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
	boolean saveDocument(HeadEntity head, boolean needStroe) {
		_coreDriver.logDebugInfo(this.getClass(), 231,
				"Call transaction to save the document", MessageType.INFO);

		if (head.isSaved()) {
			_coreDriver.logDebugInfo(this.getClass(), 235,
					"Document is saved, just to store the update on disk.",
					MessageType.INFO);
		} else {
			_coreDriver.logDebugInfo(this.getClass(), 239,
					"Document is never saved", MessageType.INFO);

			MonthIdentity monthId = head.getMonthId();
			HeadEntityCollection collection = _list.get(monthId);

			// set document number
			DocumentNumber num = null;
			HeadEntity[] entities = collection.getEntities();
			if (entities.length == 0) {
				try {
					num = new DocumentNumber("1000000001".toCharArray());
				} catch (IdentityTooLong e) {
					_coreDriver.logDebugInfo(this.getClass(), 239,
							e.toString(), MessageType.ERROR);
					throw new SystemException(e);
				} catch (IdentityNoData e) {
					_coreDriver.logDebugInfo(this.getClass(), 239,
							e.toString(), MessageType.ERROR);
					throw new SystemException(e);
				} catch (IdentityInvalidChar e) {
					_coreDriver.logDebugInfo(this.getClass(), 239,
							e.toString(), MessageType.ERROR);
					throw new SystemException(e);
				}
			} else {
				HeadEntity last = entities[entities.length - 1];
				num = last.getDocumentNumber().next();
			}
			_coreDriver.logDebugInfo(this.getClass(), 239,
					"Generate document number " + num.toString(),
					MessageType.INFO);
			head._docNumber = num;

			collection.add(head);
		}

		if (needStroe) {
			store(head.getMonthId());
			_coreDriver.logDebugInfo(this.getClass(), 465,
					"Memory has been stored to disk", MessageType.INFO);
		} else {
			_coreDriver.logDebugInfo(this.getClass(), 465,
					"Memory has NOT been stored to disk", MessageType.INFO);
		}

		_coreDriver.logDebugInfo(this.getClass(), 278,
				"Call transaction to save the document successfully",
				MessageType.INFO);

		return true;
	}

	/**
	 * store based on month identity
	 * 
	 * @param monthId
	 * @throws SystemException
	 */
	public void store(MonthIdentity monthId) {
		// store master data
		_coreDriver.logDebugInfo(this.getClass(), 293,
				String.format("Start storing %s to disk", monthId),
				MessageType.INFO);

		_coreDriver.logDebugInfo(this.getClass(), 297,
				"Store master data at first.", MessageType.INFO);
		MasterDataManagement manage = _coreDriver.getMasterDataManagement();
		manage.store();

		HeadEntityCollection collection = _list.get(monthId);

		String filePath = this.generateFilePath(monthId);
		_coreDriver.logDebugInfo(this.getClass(), 297, "Generate file path: "
				+ filePath, MessageType.INFO);

		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				_coreDriver.logDebugInfo(this.getClass(), 304, e.toString(),
						MessageType.ERROR);
				throw new SystemException(e);
			}
		}
		String xdoc = collection.toXML();
		_coreDriver
				.logDebugInfo(this.getClass(), 297,
						"Parsed document collections to XML document",
						MessageType.INFO);

		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.write(xdoc, 0, xdoc.length());
			writer.close();
		} catch (IOException e) {
			_coreDriver.logDebugInfo(this.getClass(), 316, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		}

		_coreDriver.logDebugInfo(this.getClass(), 335,
				"Save document collection successfully.", MessageType.INFO);
	}

	/**
	 * 
	 * @param docId
	 * @param msgs
	 * @return
	 */
	public HeadEntity reverseDocument(DocumentIdentity docId,
			ArrayList<CoreMessage> msgs) {
		_coreDriver.logDebugInfo(this.getClass(), 348,
				"Start reversing document " + docId.toString(),
				MessageType.INFO);

		try {
			HeadEntity orgHead = this.getEntity(docId);
			if (orgHead == null) {
				msgs.add(new CoreMessage("No such document" + docId.toString(),
						MessageType.WARNING, null));
				_coreDriver.logDebugInfo(this.getClass(), 348,
						"No such document", MessageType.WARNING);
				return null;
			}

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

			_coreDriver.logDebugInfo(this.getClass(), 392,
					"Save the reverse document", MessageType.INFO);
			boolean ret = head.save(msgs, false);
			if (ret == false) {
				return null;
			}

			_coreDriver.logDebugInfo(this.getClass(), 399,
					"Update reverse document information", MessageType.INFO);
			// update reverse information
			head._isReversed = true;
			orgHead._isReversed = true;
			head._ref = head.getDocIdentity();
			orgHead._ref = head.getDocIdentity();

			_coreDriver.logDebugInfo(this.getClass(), 399,
					"Store memory to disk during reverse document",
					MessageType.INFO);
			this.store(head.getMonthId());

			String info = String.format(
					"Document %s is reversed by document %s.",
					orgHead.getDocumentNumber(), head.getDocumentNumber());
			msgs.add(new CoreMessage(info, MessageType.INFO, null));
			_coreDriver.logDebugInfo(this.getClass(), 416, info,
					MessageType.INFO);
			return head;
		} catch (NullValueNotAcceptable e) {
			_coreDriver.logDebugInfo(this.getClass(), 422, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (MasterDataIdentityNotDefined e) {
			_coreDriver.logDebugInfo(this.getClass(), 422, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
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
		if (collection == null) {
			return null;
		}
		return collection.getEntity(docId);
	}

	/**
	 * clear
	 */
	public void clear() {
		_list.clear();
	}
}
