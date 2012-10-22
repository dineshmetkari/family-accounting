package com.jasonzqshen.familyaccounting.core.transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

	// private MonthLedger _openLedger; // current open ledger

	private final MasterDataManagement _masterDataMgmt;

	private final GLAccountBalanceCollection _glAccBalCol;

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

		_glAccBalCol = new GLAccountBalanceCollection(_coreDriver,
				_masterDataMgmt);
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
		_coreDriver.logDebugInfo(this.getClass(), 75, String.format(
				"Starting month identity is %s", _coreDriver.getStartMonthId()
						.toString()), MessageType.INFO);

		MonthIdentity[] monthIdSet = _coreDriver.getAllMonthIds();

		// loop all the month ledger file to load transaction data
		try {
			for (MonthIdentity monthId : monthIdSet) {
				_coreDriver.logDebugInfo(
						this.getClass(),
						75,
						String.format("loading month identity is %s",
								monthId.toString()), MessageType.INFO);

				load(monthId);
			}
			// load current month identity
			// _openLedger = load(cureMonthId);
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

		// get file path
		String filePath = generateFilePath(monthId);
		_coreDriver.logDebugInfo(this.getClass(), 109,
				String.format("Transaction data file: %s .", filePath),
				MessageType.INFO);

		// construct month ledger
		MonthLedger monthledger = new MonthLedger(monthId);
		_list.put(monthId, monthledger);

		File file = new File(filePath);
		// check if file exist
		if (!file.exists()) {
			return monthledger;
		}
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = docFactory.newDocumentBuilder();
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

			// -------------------------------------------------------------------
			// parse all the documents
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
						monthledger.add(head);

						// raise load document
						_coreDriver.getListenersManagement()
								.loadDoc(this, head);

						// raise reverse document
						if (head.IsReversed())
							_coreDriver.getListenersManagement()
									.reverseDoc(head);
					}
				}
			}
			// -----------------------------------------------------------------

			return monthledger;
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
	 * @throws SaveClosedLedgerException
	 * @throws StorageException
	 */
	void saveDocument(HeadEntity head, boolean needStroe)
			throws SaveClosedLedgerException, StorageException {
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
			MonthLedger ledger = this.getLedger(monthId);
			if (ledger == null) {
				_coreDriver.logDebugInfo(this.getClass(), 239,
						"Error in document month identity", MessageType.ERROR);
				throw new SaveClosedLedgerException();
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
	}

	/**
	 * store based on month identity
	 * 
	 * @param monthId
	 * @throws SystemException
	 */
	public void store(MonthIdentity monthId) throws StorageException {
		// store master data
		_coreDriver.logDebugInfo(this.getClass(), 293,
				String.format("Start storing %s to disk", monthId),
				MessageType.INFO);

		_coreDriver.logDebugInfo(this.getClass(), 297,
				"Store master data at first.", MessageType.INFO);
		MasterDataManagement manage = _coreDriver.getMasterDataManagement();
		manage.store();

		MonthLedger collection = this.getLedger(monthId);

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
				throw new StorageException();
			}
		}
		String xdoc = collection.toXML();
		_coreDriver
				.logDebugInfo(this.getClass(), 297,
						"Parsed document collections to XML document",
						MessageType.INFO);

		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), XMLTransfer.default_charset));

			String header = null;
			Language lang = _coreDriver.getLanguage();
			if (lang == Language.Engilish) {
				header = Language.ENGLISH_XML_HEADER;
			} else if (lang == Language.SimpleChinese) {
				header = Language.SIMPLE_CHINESE_XML_HEADER;
			}
			writer.write(header, 0, header.length());
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
	 * @throws SaveClosedLedgerException
	 * @throws ReverseOrgDocNotExistException
	 * @throws DocReservedException
	 */
	public void reverseDocument(DocumentIdentity docId)
			throws ReverseOrgDocNotExistException, DocReservedException {
		_coreDriver.logDebugInfo(this.getClass(), 348,
				"Start reversing document " + docId.toString(),
				MessageType.INFO);

		// ------------------------------------------
		// check document
		HeadEntity orgHead = this.getEntity(docId);
		if (orgHead == null) {
			_coreDriver.logDebugInfo(this.getClass(), 348, "No such document",
					MessageType.ERROR);
			throw new ReverseOrgDocNotExistException();
		}
		if (orgHead.IsReversed()) {
			_coreDriver.logDebugInfo(this.getClass(), 348,
					"Document has been reserved before", MessageType.ERROR);
			throw new DocReservedException();
		}

		try {
			_coreDriver.logDebugInfo(this.getClass(), 399,
					"Update reverse document information", MessageType.INFO);
			// update reverse information
			orgHead._isReversed = true;

			// raise event to update balance
			_coreDriver.getListenersManagement().reverseDoc(orgHead);

			_coreDriver.logDebugInfo(this.getClass(), 399,
					"Store memory to disk during reverse document",
					MessageType.INFO);

			// store to file system
			this.store(orgHead.getMonthId());
		} catch (StorageException e) {
			_coreDriver.logDebugInfo(this.getClass(), 452,
					"Dirty data does not store to file system",
					MessageType.WARNING);
		}

		String info = String.format("Document %s is reversed.",
				orgHead.getDocumentNumber());

		_coreDriver.logDebugInfo(this.getClass(), 416, info, MessageType.INFO);

	}

	/**
	 * get month ledger
	 * 
	 * @param monthId
	 * @return
	 */
	public MonthLedger getLedger(MonthIdentity monthId) {
		// get current calendar month
		MonthIdentity curMonthId = _coreDriver.getCurCalendarMonthId();

		// check whether ledger beyond the range
		if (_coreDriver.getStartMonthId().compareTo(monthId) > 0
				|| curMonthId.compareTo(monthId) < 0) {
			return null;
		}

		// create new month ledger
		if (!_list.containsKey(monthId)) {
			MonthLedger ledger = new MonthLedger(monthId);
			_list.put(monthId, ledger);
			return ledger;
		}

		return _list.get(monthId);
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
		return _coreDriver.getAllMonthIds();
	}

	/**
	 * get document based on the document identity
	 * 
	 * @param docId
	 * @return
	 */
	public HeadEntity getEntity(DocumentIdentity docId) {
		MonthLedger ledger = this.getLedger(docId._monthIdentity);
		if (ledger == null) {
			return null;
		}
		return ledger.getEntity(docId);
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

		MonthIdentity[] monthIds = _coreDriver.getAllMonthIds();
		for (MonthIdentity monthId : monthIds) {
			// create transaction file for each available month ledger
			MonthLedger ledger = new MonthLedger(monthId);
			_list.put(monthId, ledger);
			String xdoc = ledger.toXML();

			String filePath = generateFilePath(monthId);
			try {
				File file = new File(filePath);
				if (!file.exists()) {
					file.createNewFile();
				}
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file),
								XMLTransfer.default_charset));
				writer.write(xdoc, 0, xdoc.length());
				writer.close();
			} catch (IOException e) {
				_coreDriver.logDebugInfo(this.getClass(), 530, e.toString(),
						MessageType.ERROR);
				throw new SystemException(e);
			}
		}
	}

}
