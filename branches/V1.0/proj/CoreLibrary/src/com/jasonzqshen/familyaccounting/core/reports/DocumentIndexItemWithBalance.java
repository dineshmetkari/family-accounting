package com.jasonzqshen.familyaccounting.core.reports;

import java.util.ArrayList;

import com.jasonzqshen.familyaccounting.core.CoreDriver;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataIdentity;
import com.jasonzqshen.familyaccounting.core.masterdata.MasterDataType;
import com.jasonzqshen.familyaccounting.core.transaction.HeadEntity;
import com.jasonzqshen.familyaccounting.core.transaction.ItemEntity;
import com.jasonzqshen.familyaccounting.core.transaction.MonthIdentity;
import com.jasonzqshen.familyaccounting.core.utils.CreditDebitIndicator;
import com.jasonzqshen.familyaccounting.core.utils.CurrencyAmount;

/**
 * 
 * @author Jason
 * 
 */
public class DocumentIndexItemWithBalance extends DocumentIndexItem {

	private final MasterDataType _type;

	private final CurrencyAmount _sum;

	/**
	 * 
	 * @param coreDriver
	 * @param id
	 */
	DocumentIndexItemWithBalance(CoreDriver coreDriver, MasterDataIdentity id,
			MasterDataType type) {
		super(coreDriver, id);

		_type = type;
		_sum = new CurrencyAmount();
	}

	@Override
	protected void addDoc(HeadEntity head) {
		super.addDoc(head);

		// add the balance items
		ItemEntity[] items = head.getItems();
		for (ItemEntity item : items) {
			_sum.addTo(getAmountFromItem(item));
		}
	}

	/**
	 * remove document
	 */
	protected void removeDoc(HeadEntity head) {
		super.removeDoc(head);

		// add the balance items
		ItemEntity[] items = head.getItems();
		for (ItemEntity item : items) {
			CurrencyAmount amount = getAmountFromItem(item);
			amount.negate();
			_sum.addTo(amount);
		}
	}

	/**
	 * get the amount from item
	 * 
	 * @param item
	 * @return
	 */
	private CurrencyAmount getAmountFromItem(ItemEntity item) {
		if (_type == MasterDataType.BUSINESS_AREA) {
			MasterDataIdentity business = item.getBusinessArea();
			if (this._id.equals(business)) {
				if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
					return item.getAmount();
				} else {
					CurrencyAmount amount = item.getAmount();
					amount.negate();
					return amount;
				}
			}
		} else if (_type == MasterDataType.CUSTOMER) {
			MasterDataIdentity customer = item.getCustomer();
			if (this._id.equals(customer)) {
				if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
					return item.getAmount();
				} else {
					CurrencyAmount amount = item.getAmount();
					amount.negate();
					return amount;
				}
			}
		} else if (_type == MasterDataType.VENDOR) {
			MasterDataIdentity vendor = item.getVendor();
			if (this._id.equals(vendor)) {
				if (item.getCDIndicator() == CreditDebitIndicator.DEBIT) {
					return item.getAmount();
				} else {
					CurrencyAmount amount = item.getAmount();
					amount.negate();
					return amount;
				}
			}
		}

		return new CurrencyAmount();
	}

	/**
	 * get amount sum
	 * 
	 * @return
	 */
	public CurrencyAmount getAmountSum() {
		return new CurrencyAmount(_sum);
	}

	/**
	 * get amount
	 * 
	 * @param startId
	 * @param endId
	 * @return
	 */
	public CurrencyAmount getAmount(MonthIdentity startId, MonthIdentity endId) {
		CurrencyAmount amount = new CurrencyAmount();
		ArrayList<HeadEntity> docs = this.getEntities(startId, endId);
		for (HeadEntity head : docs) {
			ItemEntity[] items = head.getItems();
			for (ItemEntity item : items) {
				amount.addTo(getAmountFromItem(item));
			}
		}

		return amount;
	}
}
