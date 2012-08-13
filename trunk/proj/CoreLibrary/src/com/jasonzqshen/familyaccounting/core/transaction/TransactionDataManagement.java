package com.jasonzqshen.familyaccounting.core.transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import com.jasonzqshen.familyaccounting.core.ManagementBase;
import com.jasonzqshen.familyaccounting.core.exception.*;
import com.jasonzqshen.familyaccounting.core.exception.runtime.*;
import com.jasonzqshen.familyaccounting.core.exception.format.*;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.utils.*;

public class TransactionDataManagement extends ManagementBase {

	public static final String TRANSACTION_DATA_FOLDER = "transaction_data";
	private final Hashtable<MonthIdentity, MonthLedger> _list;
	private MonthLedger _openLedger; // current open ledger

	private final MasterDataManagement _masterDataMgmt;
	private final GLAccountBalanceCollection _glAccBalCol;
	private final ClosingManagement _clsMgmt;

	/**
	 * constructor
	 * 
	 * @param coreDriver
	 */
	public TransactionDataManagement(CoreDriver coreDriver,
			MasterDataManagement masterDataMgmt) {
		super(coreDriver);
		_masterDataMgmt = masterDataMgmt;
		_list = new Hashtable<MonthIdentity, MonthLedger>();

		_glAccBalCol = new GLAccountBalanceCollection(_coreDriver);
		_clsMgmt = new ClosingManagement(_coreDriver, this);
	}

	/**
	 * get closing management
	 * 
	 * @return
	 */
	public ClosingManagement getClsMgmt() {
		return _clsMgmt;
	}

	/**
	 * get G/L account balance collection
	 * 
	 * @return
	 */
	public GLAccountBalanceCollection getAccBalCol() {
		return _glAccBalCol;
	}

	/**
	 * load data from file
	 * 
	 * @throws SystemException
	 *             bug
	 */
	public void initialize() throws TransactionDataFileFormatException {

		MonthIdentity cureMonthId = _coreDriver.getCurMonthId();
		_coreDriver.logDebugInfo(
				this.getClass(),
				75,
				String.format("Current month identity is %s",
						cureMonthId.toString()), MessageType.INFO);

		_coreDriver.logDebugInfo(this.getClass(), 75, String.format(
				"Starting month identity is %s", _coreDriver.getStartMonthId()
						.toString()), MessageType.INFO);
		try {
			for (MonthIdentity startId = _coreDriver.getStartMonthId(); startId
					.compareTo(cureMonthId) < 0; startId = startId.addMonth()) {
				MonthLedger collection = load(startId);
				if (collection.isClosed() == false) {
					throw new TransactionDataFileFormatException(
							startId.toString());
				}

			}
			// load current month identity
			_openLedger = load(cureMonthId);
		} catch (NoTransactionDataFileException e) {
			_coreDriver.logDebugInfo(this.getClass(), 99, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException("");
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
	public MonthLedger load(MonthIdentity monthId)
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

		MonthLedger colletion = new MonthLedger(monthId);
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
				throw new TransactionDataFileFormatException(filePath);
			}

			nodeList = rootElem.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node child = nodeList.item(i);
				if (child instanceof Element) {
					Element elem = (Element) child;
					if (elem.getNodeName().equals(TransDataUtils.XML_DOCUMENT)) {

						HeadEntity head = HeadEntity.parse(_coreDriver,
								_masterDataMgmt, elem);
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

						if (head.getDocText().equals(
								MonthLedger.CLOSING_DOC_TAG)) {
							colletion.setClosingDoc(head);
						}

						// raise load document
						_coreDriver.getListenersManagement()
								.loadDoc(this, head);
					}
				}
			}

			return colletion;
		} catch (ParserConfigurationException e) {
			_coreDriver.logDebugInfo(this.getClass(), 170, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		} catch (SAXException e) {
			_coreDriver.logDebugInfo(this.getClass(), 170, e.toString(),
					MessageType.ERROR);
			throw new TransactionDataFileFormatException(filePath);
		} catch (IOException e) {
			_coreDriver.logDebugInfo(this.getClass(), 170, e.toString(),
					MessageType.ERROR);
			throw new NoTransactionDataFileException(filePath);
		} catch (TransactionDataFileFormatException e) {
			_coreDriver.logDebugInfo(this.getClass(), 170, e.toString(),
					MessageType.ERROR);
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
			MonthLedger ledger = _list.get(monthId);
			if (ledger == null) {
				_coreDriver.logDebugInfo(this.getClass(), 239,
						"Error in document month identity", MessageType.ERROR);
				return false;
			}

			if (ledger.isClosed()) {
				_coreDriver.logDebugInfo(this.getClass(), 239,
						"Ledger is closed.", MessageType.ERROR);
				return false;
			}

			// set document number
			DocumentNumber num = null;
			HeadEntity[] entities = ledger.getEntities();
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

			ledger.add(head);
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

		MonthLedger collection = _list.get(monthId);

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
	public HeadEntity reverseDocument(DocumentIdentity docId) {
		_coreDriver.logDebugInfo(this.getClass(), 348,
				"Start reversing document " + docId.toString(),
				MessageType.INFO);

		try {
			HeadEntity orgHead = this.getEntity(docId);
			if (orgHead == null) {
				_coreDriver.logDebugInfo(this.getClass(), 348,
						"No such document", MessageType.WARNING);
				return null;
			}

			HeadEntity head = new HeadEntity(_coreDriver, _masterDataMgmt);
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
			boolean ret = head.save(false);
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
	 * get month ledger
	 * 
	 * @param monthId
	 * @return
	 */
	public MonthLedger getLedger(MonthIdentity monthId) {
		return _list.get(monthId);
	}

	/**
	 * get current ledger
	 * 
	 * @return
	 */
	public MonthLedger getCurrentLedger() {
		return _openLedger;
	}

	/**
	 * get documents
	 * 
	 * @param fiscalYear
	 * @param fiscalMonth
	 * @return
	 */
	public MonthLedger getLedger(int fiscalYear, int fiscalMonth) {
		MonthIdentity monthId;
		try {
			monthId = new MonthIdentity(fiscalYear, fiscalMonth);
		} catch (FiscalYearRangeException e) {
			return null;
		} catch (FiscalMonthRangeException e) {
			return null;
		}

		return getLedger(monthId);
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
		MonthLedger collection = _list.get(docId._monthIdentity);
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

	@Override
	public void establishFiles() {
		// set up transaction data folder
		String transFolderPath = String.format("%s/%s",
				_coreDriver.getRootPath(), TRANSACTION_DATA_FOLDER);
		File transFolder = new File(transFolderPath);
		if (!transFolder.exists()) {
			_coreDriver
					.logDebugInfo(
							this.getClass(),
							125,
							"Transaction data root folder does not exist. Make directory.",
							MessageType.INFO);
			transFolder.mkdir();
		}

		MonthIdentity monthId = _coreDriver.getStartMonthId();
		// create transaction file
		MonthLedger ledger = new MonthLedger(monthId);
		_list.put(monthId, ledger);
		_openLedger = ledger;
		String xdoc = ledger.toXML();

		String filePath = generateFilePath(monthId);
		try {
			File file = new File(filePath);
			if (!file.exists()) {

				file.createNewFile();

			}
			FileWriter writer = new FileWriter(file);
			writer.write(xdoc, 0, xdoc.length());
			writer.close();
		} catch (IOException e) {
			_coreDriver.logDebugInfo(this.getClass(), 530, e.toString(),
					MessageType.ERROR);
			throw new SystemException(e);
		}
	}

	/**
	 * month end close
	 */
	public void monthEndClose() {

	}
}
