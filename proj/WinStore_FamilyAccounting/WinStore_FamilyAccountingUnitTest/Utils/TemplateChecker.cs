using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore.DocumentEntries;

namespace WinStore_FamilyAccountingUnitTest.Utils
{
    public class TemplateChecker
    {
        /// <summary>
        /// check templates
        /// </summary>
        /// <param name="templates"></param>
        public static void CheckTemplate(List<EntryTemplate> templates)
        {
            Assert.AreEqual(3, templates.Count);

            for (int i = 0; i < 3; ++i)
            {
                EntryTemplate temp = templates[i];
                Assert.AreEqual(i + 1, temp.Identity);

                switch (i + 1)
                {
                    case 1:
                        // vendor
                        Assert.AreEqual(EntryType.VendorEntry,
                                temp.EntryT);
                        Assert.AreEqual("Traffic expense on work", temp.TempName);
                        Assert.AreEqual(TestData.VENDOR_BUS,
                                temp.getDefaultValue(VendorEntry.VENDOR).ToString());
                        Assert.AreEqual(TestData.GL_ACCOUNT_CASH,
                                temp.getDefaultValue(VendorEntry.REC_ACC).ToString());
                        Assert.AreEqual(TestData.GL_ACCOUNT_COST,
                                temp.getDefaultValue(VendorEntry.GL_ACCOUNT).ToString());
                        Assert.AreEqual(TestData.BUSINESS_AREA_WORK,
                                temp.getDefaultValue(VendorEntry.BUSINESS_AREA)
                                        .ToString());
                        Assert.AreEqual(TestData.AMOUNT_VENDOR,
                                temp.getDefaultValue(EntryTemplate.AMOUNT));
                        Assert.AreEqual(TestData.TEXT_VENDOR_DOC,
                                temp.getDefaultValue(EntryTemplate.TEXT));

                        break;
                    case 2:
                        // G/L
                        Assert.AreEqual(EntryType.GLEntry, temp.EntryT);
                        Assert.AreEqual(TestData.TEXT_GL_DOC, temp.TempName);
                        Assert.AreEqual(TestData.GL_ACCOUNT_BANK,
                                temp.getDefaultValue(GLAccountEntry.SRC_ACCOUNT)
                                        .ToString());
                        Assert.AreEqual(TestData.GL_ACCOUNT_CASH,
                                temp.getDefaultValue(GLAccountEntry.DST_ACCOUNT)
                                        .ToString());
                        break;

                    case 3:

                        // customer
                        Assert.AreEqual(EntryType.CustomerEntry,
                                temp.EntryT);
                        Assert.AreEqual(TestData.TEXT_CUSTOMER_DOC, temp.TempName);
                        Assert.AreEqual(TestData.CUSTOMER1,
                                temp.getDefaultValue(CustomerEntry.CUSTOMER).ToString());
                        Assert.AreEqual(TestData.GL_ACCOUNT_BANK,
                                temp.getDefaultValue(CustomerEntry.REC_ACC).ToString());
                        Assert.AreEqual(TestData.GL_ACCOUNT_REV,
                                temp.getDefaultValue(CustomerEntry.GL_ACCOUNT)
                                        .ToString());

                        Assert.AreEqual(TestData.TEXT_CUSTOMER_DOC,
                                temp.getDefaultValue(EntryTemplate.TEXT));
                        break;
                }
            }
        }
    }
}
