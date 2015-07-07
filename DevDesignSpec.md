# Development Design Spec #

## Functionality Strategy ##
  * Basic functions of financial ledger.(Very High)
  1. Record daily financial event with some necessary fields;
  1. View the records;
  1. Keep data consistently, which means that the data in the system must be the same with the actual data.
  * Fast entry: Try to provide user a way to enter financial record easier than other other financial software;(High)
  1. Records entry templates
  1. Schedule templates
  1. Credit card management
  1. ...
  * Store data with transparency(Very High)
  1. Do not provide space for user to store the data. User must store their data in their own space.
  1. Provide the function to export the data in XML or CVS format so that use can use Excel to view the data.
  1. Provide the way that users can store the data on multiple devices and sync data between the devices. It is not the Cloud service.
The function is based on the Cloud Service, like IO Cloud and SkyDriver, to sync data.
  * Reporting(High)
  1. Investment analysis(Medium)
  * Help user to make decision on their investment, like liquidity forcast report


# Functionality #
## 1. Master data ##


## 2. Financial record(Transaction Data) ##
Principal of the transaction, **documents in the ledger file(Storage File) only can be added.** The saved document cannot be deleted. The saved documents cannot be updated, excepted
the field "is\_reversed"

## 3. Account Balance ##

## 4. Entry templates ##

## 5. Sync Algorithm ##
  * For example, initial state

device A:



&lt;document id="1000000001" text="org1"... /&gt;





&lt;document id="1000000002" text="org2".../&gt;




device B:



&lt;document id="1000000001" text="org1"... /&gt;





&lt;document id="1000000002" text="org2".../&gt;



  * User add a document in device A, and then sync to Cloud Storage



&lt;document id="1000000001" text="org1"... /&gt;





&lt;document id="1000000002" text="org2".../&gt;





&lt;document id="1000000003" text="create on device A" /&gt;



  * User add a document and reverse document 1000000002 on device B



&lt;document id="1000000001" text="org1"... /&gt;





<document id="1000000002" text="org2" is\_reversed="true".../>





&lt;document id="1000000003" text="create on device B" /&gt;



  * User try to sync data with Cloud Storage.
  1. 1. System reads data from the first document and compare with data on Device B.
  1. 2. If the document with the same id is different, and if field is\_reversed is the only different field, just update "is\_reversed" field.("is\_reversed" field only can be changed from false to true, but not true to false)
  1. 3. If not only is\_reversed is different, then the document is first NOT\_MATCHED document.
  1. 4. Append all NOT\_MATCHED documents, from the first NOT\_MATCHED document, to the end of Cloud Storage.
  1. 5. Replace storage on Device B with Cloud Storage, then the data is,



&lt;document id="1000000001" text="org1"... /&gt;





<document id="1000000002" text="org2" is\_reversed="true".../>





&lt;document id="1000000004" text="create on device A" /&gt;





&lt;document id="1000000005" text="create on device B" /&gt;



  * User sync data on device A follow the same algorithm in above steps