/******************************************************************************
 * Copyright © 2013-2016 The XEL Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * XEL software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

package nxt;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import nxt.db.DbIterator;
import nxt.util.Observable;

public interface TransactionProcessor extends Observable<List<? extends Transaction>, TransactionProcessor.Event> {

	enum Event {
		REMOVED_UNCONFIRMED_TRANSACTIONS, ADDED_UNCONFIRMED_TRANSACTIONS, ADDED_CONFIRMED_TRANSACTIONS, RELEASE_PHASED_TRANSACTION, REJECT_PHASED_TRANSACTION, BROADCASTED_OWN_TRANSACTION,
	}

	void broadcast(Transaction transaction) throws NxtException.ValidationException;

	void clearUnconfirmedThatGotInvalidLately();

	void clearUnconfirmedTransactions();

	Transaction getUnconfirmedSNCleanTransaction(final long snclean);

	Transaction[] getAllBroadcastedTransactions();

	DbIterator<? extends Transaction> getAllUnconfirmedTransactions();

	DbIterator<? extends Transaction> getAllUnconfirmedTransactions(String sort);

	Transaction[] getAllWaitingTransactions();

	SortedSet<? extends Transaction> getCachedUnconfirmedTransactions(List<String> exclude);

	Transaction getUnconfirmedTransaction(long transactionId);

	void processLater(Collection<? extends Transaction> transactions);

	void processPeerTransactions(JSONObject request) throws NxtException.ValidationException;

	void rebroadcastAllUnconfirmedTransactions();

	void requeueAllUnconfirmedTransactions();

	List<Transaction> restorePrunableData(JSONArray transactions) throws NxtException.NotValidException;

}
