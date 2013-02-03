using Microsoft.VisualStudio.TestPlatform.UnitTestFramework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using WinStore_FamilyAccountingCore;
using WinStore_FamilyAccountingCore.DocumentEntries;
using WinStore_FamilyAccountingUnitTest.Utils;

namespace WinStore_FamilyAccountingUnitTest.CreationTestCases
{
    [TestClass]
    public class CreatingTemplateTester
    {
        CoreDriver _coreDriver;
        [TestInitialize]
        public async Task Initialize()
        {
            _coreDriver = new CoreDriver();
            await StoragePreparation.EstablishFolder_2012_07(
                TestFolderPath.CREATING_TEMPLATE_PATH
                , _coreDriver);
        }

        [TestMethod]
        public async Task TestCreateTemplate()
        {
            Assert.IsTrue(_coreDriver.IsInitialize);
            MasterDataCreater.CreateMasterData(_coreDriver);

            await TemplateCreater.CreateTemplate(_coreDriver, _coreDriver.TmMgmt);
            List<EntryTemplate> templates = _coreDriver.TmMgmt.EntryTemplates;
            TemplateChecker.CheckTemplate(templates);

            await _coreDriver.RestartAsync();
            TemplateChecker.CheckTemplate(templates);
        }
    }
}
