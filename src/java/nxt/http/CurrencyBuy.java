package nxt.http;

import nxt.Account;
import nxt.Attachment;
import nxt.Currency;
import nxt.NxtException;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

/**
 * Buy currency for NXT
 * </p>
 * Parameters
 * <ul>
 * <li>currency - currency id
 * <li>rateNQT - exchange rate between NXT amount and currency units
 * <li>units - number of units to buy
 * </ul>
 *
 * </p>
 * currency buy transaction attempts to match existing exchange offers. When a match is found, the minimum number of units
 * between the number of units offered and the units requested are exchanged at a rate matching the highest sell offer<br>
 * A single transaction can match multiple sell offers or none.
 * Unlike asset bid order, currency buy is not saved. It's either executed immediately (fully or partially) or not executed
 * at all.
 * For every match between buyer and seller an exchange record is saved, exchange records can be retrieved using the {@link GetExchanges} API
 */
public final class CurrencyBuy extends CreateTransaction {

    static final CurrencyBuy instance = new CurrencyBuy();

    private CurrencyBuy() {
        super(new APITag[] {APITag.MS, APITag.CREATE_TRANSACTION}, "currency", "rateNQT", "units");
    }

    @Override
    JSONStreamAware processRequest(HttpServletRequest req) throws NxtException {
        Currency currency = ParameterParser.getCurrency(req);
        long rateNQT = ParameterParser.getLong(req, "rateNQT", 0, Long.MAX_VALUE, true);
        long units = ParameterParser.getLong(req, "units", Long.MIN_VALUE, Long.MAX_VALUE, true);
        Account account = ParameterParser.getSenderAccount(req);

        Attachment attachment = new Attachment.MonetarySystemExchange(currency.getId(), rateNQT, units);
        return createTransaction(req, account, attachment);
    }

}
