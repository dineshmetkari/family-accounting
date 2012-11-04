using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WinStore_FamilyAccountingCore.Transaction
{
public class TransDataUtils {
	public static readonly String XML_ROOT = "root";
	public static readonly String XML_DOCUMENT = "document";
	public static readonly String XML_ITEM = "item";
	public static readonly String XML_DOC_NUM = "doc_num";
	public static readonly String XML_YEAR = "fiscal_year";
	public static readonly String XML_MONTH = "fiscal_month";
	public static readonly String XML_DATE = "date";
	public static readonly String XML_TEXT = "text";
	public static readonly String XML_DOC_TYPE = "doc_type";
	public static readonly String XML_IS_REVERSED = "is_reversed";

	public static readonly String[] HEAD_XML_TAGS = new String[] { XML_DOC_NUM,
			XML_YEAR, XML_MONTH, XML_DATE, XML_TEXT, XML_DOC_TYPE,
			XML_IS_REVERSED };

	public static readonly String XML_LINE_NUM = "line_num";
	public static readonly String XML_ACCOUNT_TYPE = "account_type";
	public static readonly String XML_GL_ACCOUNT = "gl_account";
	public static readonly String XML_VENDOR = "vendor";
	public static readonly String XML_CUSTOMER = "customer";
	public static readonly String XML_AMOUNT = "amount";
	public static readonly String XML_CD_INDICATOR = "cd_indicator";
	public static readonly String XML_BUSINESS_AREA = "business_area";

}

}
