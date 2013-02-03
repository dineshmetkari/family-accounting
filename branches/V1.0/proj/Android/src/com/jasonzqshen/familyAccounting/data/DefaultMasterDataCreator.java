package com.jasonzqshen.familyAccounting.data;

import android.app.Activity;

import com.jasonzqshen.familyAccounting.R;
import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.exception.IdentityInvalidChar;
import com.jasonzqshen.familyaccounting.core.exception.IdentityNoData;
import com.jasonzqshen.familyaccounting.core.exception.IdentityTooLong;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityExists;
import com.jasonzqshen.familyaccounting.core.exception.MasterDataIdentityNotDefined;
import com.jasonzqshen.familyaccounting.core.exception.ParametersException;
import com.jasonzqshen.familyaccounting.core.exception.runtime.SystemException;
import com.jasonzqshen.familyaccounting.core.masterdata.BusinessAreaMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.CustomerMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.GLAccountMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity_GLAccount;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataManagement;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.masterdata.VendorMasterDataFactory;
import com.jasonzqshen.familyaccounting.core.utils.CriticalLevel;
import com.jasonzqshen.familyaccounting.core.utils.GLAccountGroup;

public class DefaultMasterDataCreator {

	private static final String VENDOR_OTHER = "OTHER";
	private static final String CUSTOMER_OTHER = "OTHER";
	private static final String BUSI_WORK = "WORK";
	private static final String BUSI_FAMILY = "FAMILY";
	private static final String BUSI_FRIENDS = "FRIENDS";
	private static final String BUSI_LIFE = "LIFE";
	private static final String BUSI_ENTER = "ENTERTAIN";
	private static final String BUSI_HEALTH = "HEALTH";

	static class BusiPair {
		BusiPair(int descp, String id) {
			Descp = descp;
			Id = id;
		}

		public int Descp;
		public String Id;
	}

	static class GLPair {
		GLPair(int descp, int id, GLAccountGroup group) {
			Descp = descp;
			Id = id;
			Group = group;
		}

		int Descp;
		int Id;
		GLAccountGroup Group;
	}

	/**
	 * business area array
	 */
	private static final BusiPair[] BUSINESS_AREA_ARRAY = new BusiPair[] {
			new BusiPair(R.string.md_busiarea_work, BUSI_WORK),
			new BusiPair(R.string.md_busiarea_family, BUSI_FAMILY),
			new BusiPair(R.string.md_busiarea_friends, BUSI_FRIENDS),
			new BusiPair(R.string.md_busiarea_life, BUSI_LIFE),
			new BusiPair(R.string.md_busiarea_entertainment, BUSI_ENTER),
			new BusiPair(R.string.md_busiarea_health, BUSI_HEALTH) };

	/**
	 * gl account array
	 */
	private static final GLPair[] GL_ARRAY = new GLPair[] {
			new GLPair(R.string.md_gl_cloth, 1, GLAccountGroup.COST_PURE),
			new GLPair(R.string.md_gl_makeup, 2, GLAccountGroup.COST_PURE),
			new GLPair(R.string.md_gl_dinner, 3, GLAccountGroup.COST_PURE),
			new GLPair(R.string.md_gl_house_expence, 4,
					GLAccountGroup.COST_PURE),
			new GLPair(R.string.md_gl_telecommunication, 5,
					GLAccountGroup.COST_PURE),
			new GLPair(R.string.md_gl_electronic_equipment, 6,
					GLAccountGroup.COST_PURE),
			new GLPair(R.string.md_gl_grant, 7, GLAccountGroup.COST_PURE),
			new GLPair(R.string.md_gl_cash, 8, GLAccountGroup.CASH),
			new GLPair(R.string.md_gl_bank_account, 9,
					GLAccountGroup.BANK_ACCOUNT),
			new GLPair(R.string.md_gl_deposit, 10, GLAccountGroup.INVESTMENT),
			new GLPair(R.string.md_gl_house, 11, GLAccountGroup.ASSETS),
			new GLPair(R.string.md_gl_car, 12, GLAccountGroup.ASSETS),
			new GLPair(R.string.md_gl_credit_card, 13,
					GLAccountGroup.SHORT_LIABILITIES),
			new GLPair(R.string.md_gl_salary, 14, GLAccountGroup.SALARY),
			new GLPair(R.string.md_gl_traffic_card, 15, GLAccountGroup.PREPAID),
			new GLPair(R.string.md_gl_equity, 16, GLAccountGroup.EQUITY),
			new GLPair(R.string.md_gl_traffic, 17, GLAccountGroup.COST_PURE),
			new GLPair(R.string.md_gl_loss, 18, GLAccountGroup.COST_ACCI) };

	public static void createDefaultMD(Activity activity, CoreDriver coreDriver) {
		/**
		 * check the factory is initialized, and the factory with no master data
		 * entities
		 */
		MasterDataManagement masterDataManagement = coreDriver
				.getMasterDataManagement();

		// get factories
		VendorMasterDataFactory vendorFactory = (VendorMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.VENDOR);
		CustomerMasterDataFactory customerFactory = (CustomerMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.CUSTOMER);
		BusinessAreaMasterDataFactory businessFactory = (BusinessAreaMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.BUSINESS_AREA);
		GLAccountMasterDataFactory accountFactory = (GLAccountMasterDataFactory) masterDataManagement
				.getMasterDataFactory(MasterDataType.GL_ACCOUNT);

		try {

			/** add master data entities */
			// vendor
			vendorFactory
					.createNewMasterDataBase(new MasterDataIdentity(
							VENDOR_OTHER), activity
							.getString(R.string.md_vendor_other));

			// customer
			customerFactory.createNewMasterDataBase(new MasterDataIdentity(
					CUSTOMER_OTHER), activity
					.getString(R.string.md_customer_other));

			// business area
			for (BusiPair pair : BUSINESS_AREA_ARRAY) {
				businessFactory.createNewMasterDataBase(new MasterDataIdentity(
						pair.Id), activity.getString(pair.Descp),
						CriticalLevel.MEDIUM);
			}

			// G/L account
			for (GLPair pair : GL_ARRAY) {
				MasterDataIdentity_GLAccount glId = generateGLAccountId(
						pair.Group, pair.Id);
				accountFactory.createNewMasterDataBase(glId,
						activity.getString(pair.Descp));
			}

			// store
			masterDataManagement.store();
		} catch (ParametersException e) {
			throw new SystemException(e);// bug
		} catch (MasterDataIdentityNotDefined e) {
			throw new SystemException(e);// bug
		} catch (MasterDataIdentityExists e) {
			throw new SystemException(e);// bug
		} catch (IdentityTooLong e) {
			throw new SystemException(e);// bug
		} catch (IdentityNoData e) {
			throw new SystemException(e);// bug
		} catch (IdentityInvalidChar e) {
			throw new SystemException(e);// bug
		}

	}

	/**
	 * generate master data management
	 * 
	 * @param group
	 * @param mdMgmt
	 * @return
	 */
	public static MasterDataIdentity_GLAccount generateGLAccountId(
			GLAccountGroup group, int glId) {

		try {
			MasterDataIdentity num = new MasterDataIdentity(
					String.valueOf(glId));
			String str = num.toString()
					.substring(MasterDataIdentity.LENGTH - 6);
			String glAccStr = String.format("%s%s", group.toString(), str);
			return new MasterDataIdentity_GLAccount(glAccStr);
		} catch (IdentityTooLong e) {
			throw new SystemException(e);
		} catch (IdentityNoData e) {
			throw new SystemException(e);
		} catch (IdentityInvalidChar e) {
			throw new SystemException(e);
		}
	}
}
