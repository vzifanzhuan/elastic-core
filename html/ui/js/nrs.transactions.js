/**
 * @depends {nrs.js}
 */
var NRS = (function(NRS, $, undefined) {

	NRS.lastTransactions = "";

	NRS.unconfirmedTransactions = [];
	NRS.unconfirmedTransactionIds = "";
	NRS.unconfirmedTransactionsChange = true;


	NRS.handleIncomingTransactions = function(transactions, confirmedTransactionIds) {
		var oldBlock = (confirmedTransactionIds === false); //we pass false instead of an [] in case there is no new block..

		if (typeof confirmedTransactionIds != "object") {
			confirmedTransactionIds = [];
		}

		if (confirmedTransactionIds.length) {
			NRS.lastTransactions = confirmedTransactionIds.toString();
		}

		if (confirmedTransactionIds.length || NRS.unconfirmedTransactionsChange) {
			transactions.sort(NRS.sortArray);
		}

		//always refresh peers and unconfirmed transactions..
		if (NRS.currentPage == "peers") {
			NRS.incoming.peers();
		} else if (NRS.currentPage == "transactions" && $('#transactions_type_navi li.active a').attr('data-transaction-type') == "unconfirmed") {
			NRS.incoming.transactions();
		} else {
			if (NRS.currentPage != 'messages' && (!oldBlock || NRS.unconfirmedTransactionsChange)) {
				if (NRS.incoming[NRS.currentPage]) {
					NRS.incoming[NRS.currentPage](transactions);
				}
			}
		}
		if (!oldBlock || NRS.unconfirmedTransactionsChange) {
			// always call incoming for messages to enable message notifications
			NRS.incoming['messages'](transactions);
			NRS.updateNotifications();
		}
	}

	NRS.getUnconfirmedTransactions = function(callback) {
		NRS.sendRequest("getUnconfirmedTransactions", {
			"account": NRS.account
		}, function(response) {
			if (response.unconfirmedTransactions && response.unconfirmedTransactions.length) {
				var unconfirmedTransactions = [];
				var unconfirmedTransactionIds = [];

				response.unconfirmedTransactions.sort(function(x, y) {
					if (x.timestamp < y.timestamp) {
						return 1;
					} else if (x.timestamp > y.timestamp) {
						return -1;
					} else {
						return 0;
					}
				});
				
				for (var i = 0; i < response.unconfirmedTransactions.length; i++) {
					var unconfirmedTransaction = response.unconfirmedTransactions[i];

					unconfirmedTransaction.confirmed = false;
					unconfirmedTransaction.unconfirmed = true;
					unconfirmedTransaction.confirmations = "/";

					if (unconfirmedTransaction.attachment) {
						for (var key in unconfirmedTransaction.attachment) {
							if (!unconfirmedTransaction.hasOwnProperty(key)) {
								unconfirmedTransaction[key] = unconfirmedTransaction.attachment[key];
							}
						}
					}
					unconfirmedTransactions.push(unconfirmedTransaction);
					unconfirmedTransactionIds.push(unconfirmedTransaction.transaction);
				}
				NRS.unconfirmedTransactions = unconfirmedTransactions;
				var unconfirmedTransactionIdString = unconfirmedTransactionIds.toString();

				if (unconfirmedTransactionIdString != NRS.unconfirmedTransactionIds) {
					NRS.unconfirmedTransactionsChange = true;
					NRS.unconfirmedTransactionIds = unconfirmedTransactionIdString;
				} else {
					NRS.unconfirmedTransactionsChange = false;
				}

				if (callback) {
					callback(unconfirmedTransactions);
				}
			} else {
				NRS.unconfirmedTransactions = [];

				if (NRS.unconfirmedTransactionIds) {
					NRS.unconfirmedTransactionsChange = true;
				} else {
					NRS.unconfirmedTransactionsChange = false;
				}

				NRS.unconfirmedTransactionIds = "";
				if (callback) {
					callback([]);
				}
			}
		});
	}

	NRS.handleInitialTransactions = function(transactions, transactionIds) {
		if (transactions.length) {
			var rows = "";

			transactions.sort(NRS.sortArray);

			if (transactionIds.length) {
				NRS.lastTransactions = transactionIds.toString();
			}

			for (var i = 0; i < transactions.length; i++) {
				var transaction = transactions[i];
				rows += NRS.getTransactionRowHTML(transaction);
			}

			$("#dashboard_table tbody").empty().append(rows);
			NRS.addPendingInfoToTransactionRows(transactions);
		}

		NRS.dataLoadFinished($("#dashboard_table"));
	}

	NRS.getInitialTransactions = function() {
		NRS.sendRequest("getAccountTransactions", {
			"account": NRS.account,
			"firstIndex": 0,
			"lastIndex": 9
		}, function(response) {
			if (response.transactions && response.transactions.length) {
				var transactions = [];
				var transactionIds = [];

				for (var i = 0; i < response.transactions.length; i++) {
					var transaction = response.transactions[i];

					transaction.confirmed = true;
					transactions.push(transaction);

					transactionIds.push(transaction.transaction);
				}

				NRS.getUnconfirmedTransactions(function(unconfirmedTransactions) {
					NRS.handleInitialTransactions(transactions.concat(unconfirmedTransactions), transactionIds);
				});
			} else {
				NRS.getUnconfirmedTransactions(function(unconfirmedTransactions) {
					NRS.handleInitialTransactions(unconfirmedTransactions, []);
				});
			}
		});
	}

	NRS.getNewTransactions = function() {
		//check if there is a new transaction..
		NRS.sendRequest("getAccountTransactionIds", {
			"account": NRS.account,
			"timestamp": NRS.blocks[0].timestamp + 1,
			"firstIndex": 0,
			"lastIndex": 0
		}, function(response) {
			//if there is, get latest 10 transactions
			if (response.transactionIds && response.transactionIds.length) {
				NRS.sendRequest("getAccountTransactions", {
					"account": NRS.account,
					"firstIndex": 0,
					"lastIndex": 9
				}, function(response) {
					if (response.transactions && response.transactions.length) {
						var transactionIds = [];

						$.each(response.transactions, function(key, transaction) {
							transactionIds.push(transaction.transaction);
							response.transactions[key].confirmed = true;
						});

						NRS.getUnconfirmedTransactions(function(unconfirmedTransactions) {
							NRS.handleIncomingTransactions(response.transactions.concat(unconfirmedTransactions), transactionIds);
						});
					} else {
						NRS.getUnconfirmedTransactions(function(unconfirmedTransactions) {
							NRS.handleIncomingTransactions(unconfirmedTransactions);
						});
					}
				});
			} else {
				NRS.getUnconfirmedTransactions(function(unconfirmedTransactions) {
					NRS.handleIncomingTransactions(unconfirmedTransactions);
				});
			}
		});
	}

	//todo: add to dashboard? 
	NRS.addUnconfirmedTransaction = function(transactionId, callback) {
		NRS.sendRequest("getTransaction", {
			"transaction": transactionId
		}, function(response) {
			if (!response.errorCode) {
				response.transaction = transactionId;
				response.confirmations = "/";
				response.confirmed = false;
				response.unconfirmed = true;

				if (response.attachment) {
					for (var key in response.attachment) {
						if (!response.hasOwnProperty(key)) {
							response[key] = response.attachment[key];
						}
					}
				}
				var alreadyProcessed = false;
				try {
					var regex = new RegExp("(^|,)" + transactionId + "(,|$)");

					if (regex.exec(NRS.lastTransactions)) {
						alreadyProcessed = true;
					} else {
						$.each(NRS.unconfirmedTransactions, function(key, unconfirmedTransaction) {
							if (unconfirmedTransaction.transaction == transactionId) {
								alreadyProcessed = true;
								return false;
							}
						});
					}
				} catch (e) {}

				if (!alreadyProcessed) {
					NRS.unconfirmedTransactions.unshift(response);
				}
				if (callback) {
					callback(alreadyProcessed);
				}
				if (NRS.currentPage == 'transactions' || NRS.currentPage == 'dashboard') {
					NRS.incoming[NRS.currentPage]();
				}

				NRS.getAccountInfo();
			} else if (callback) {
				callback(false);
			}
		});
	}

	NRS.sortArray = function(a, b) {
		return b.timestamp - a.timestamp;
	}

	NRS.getTransactionIconHTML = function(type, subType) {
		var iconHTML = NRS.transactionTypes[type]['iconHTML'] + " " + NRS.transactionTypes[type]['subTypes'][subType]['iconHTML'];
		var html = '';
		html += '<span class="label label-primary" style="font-size:12px;">' + iconHTML + '</span>';
		return html;
	}

	NRS.addPendingTransactionHTML = function(t) {
		var html = "";
		var $td = $('#tr_transaction_' + t.transaction + ' .td_transaction_pending');

		if (t.attachment && t.attachment["version.Phasing"] && t.attachment.phasingVotingModel != undefined) {
			NRS.sendRequest("getPhasingPoll", {
				"transaction": t.transaction
			}, function(response) {
				if (response.transaction) {
					if (!response.votes) {
						response.votes = 0;
					}
					var attachment = t.attachment;
					var vm = attachment.phasingVotingModel;

					var state = "";
					var color = "";
					var icon = "";
					var votesFormatted = "";
					var quorumFormatted = "";
					var unitFormattted = "";
					var finishHeightFormatted = String(response.finishHeight);
					var percentageFormatted = NRS.calculatePercentage(response.votes, response.quorum) + "%";
					var percentageProgressBar = Math.round(response.votes * 100 / response.quorum);
					var progressBarWidth = Math.round(percentageProgressBar / 2);

					if (response.finished) {
						var finishedFormatted = "Yes";
					} else {
						var finishedFormatted = "No";
					}

					if (response.finished) {
						if (response.votes >= response.quorum) {
							state = "success";
							color = "#00a65a";	
						} else {
							state = "danger";
							color = "#f56954";							
						}
					} else {
						state = "warning";
						color = "#f39c12";
					}
					if (vm == 0) {
						icon = '<i class="fa fa-group"></i>';
						votesFormatted = String(response.votes);
						quorumFormatted = String(response.quorum);
						unitFormattted = "";
					}
					if (vm == 1) {
						icon = '<i class="fa fa-money"></i>';
						votesFormatted = NRS.convertToNXT(response.votes);
						quorumFormatted = NRS.convertToNXT(response.quorum);
						unitFormattted = "NXT";
					}
					if (vm == 2) {
						icon = '<i class="fa fa-signal"></i>';
						votesFormatted = String(response.votes);
						quorumFormatted = String(response.quorum);
						unitFormattted = "";
					}
					if (vm == 3) {
						icon = '<i class="fa fa-bank"></i>';
						votesFormatted = String(response.votes);
						quorumFormatted = String(response.quorum);
						unitFormattted = "";
					}
					var popover = "<table class='table table-striped'>";
					popover += "<tr><td>Votes:</td><td>" + votesFormatted + " / " + quorumFormatted + " " + unitFormattted + "</td></tr>";
					popover += "<tr><td>Percentage:</td><td>" + percentageFormatted + "</td></tr>";
					popover += "<tr><td>Finish Height:</td><td>" + finishHeightFormatted + "</td></tr>";
					popover += "<tr><td>Finished:</td><td>" + finishedFormatted + "</td></tr>";
					popover += "</table>";

					html += '<div class="show_popover" style="display:inline-block;min-width:94px;text-align:left;border:1px solid #e2e2e2;background-color:#fff;padding:3px;" ';
 				 	html += 'data-toggle="popover" data-placement="top" data-content="' + popover + '" data-container="body">';
					html += "<div class='label label-" + state + "' style='display:inline-block;margin-right:5px;'>" + icon + "</div>";
					
					if (vm == 0) {
						html += '<span style="color:' + color + '">' + votesFormatted + '</span> / <span>' + quorumFormatted + '</span>';
					} else {
						html += '<div class="progress" style="display:inline-block;height:10px;width: 50px;">';
    					html += '<div class="progress-bar progress-bar-' + state + '" role="progressbar" aria-valuenow="' + percentageProgressBar + '" ';
    					html += 'aria-valuemin="0" aria-valuemax="100" style="height:10px;width: ' + progressBarWidth + 'px;">';
      					html += '<span class="sr-only">' + percentageProgressBar + '% Complete</span>';
    					html += '</div>';
  						html += '</div> ';
  					}

					html += "</div>";
					$td.html(html);
				} else {
					html = "&nbsp;";
					$td.html(html);
				}
			}, false);
		} else {
			html = "&nbsp;";
			$td.html(html);
		}
	}

	NRS.addPendingInfoToTransactionRows = function(transactions) {
		for (var i = 0; i < transactions.length; i++) {
			var transaction = transactions[i];
			NRS.addPendingTransactionHTML(transaction);
		}
	}


	NRS.getTransactionRowHTML = function(transaction, actions) {
		var transactionType = $.t(NRS.transactionTypes[transaction.type]['subTypes'][transaction.subtype]['i18nKeyTitle']);

		if (transaction.type == 1 && transaction.subtype == 6 && transaction.attachment.priceNQT == "0") {
			if (transaction.sender == NRS.account && transaction.recipient == NRS.account) {
				transactionType = $.t("alias_sale_cancellation");
			} else {
				transactionType = $.t("alias_transfer");
			}
		}

		var receiving = transaction.recipient == NRS.account;
		var account = (receiving ? "sender" : "recipient");

		if (transaction.amountNQT) {
			transaction.amount = new BigInteger(transaction.amountNQT);
			transaction.fee = new BigInteger(transaction.feeNQT);
		}

		var hasMessage = false;

		if (transaction.attachment) {
			if (transaction.attachment.encryptedMessage || transaction.attachment.message) {
				hasMessage = true;
			} else if (transaction.sender == NRS.account && transaction.attachment.encryptToSelfMessage) {
				hasMessage = true;
			}
		}

		var html = "";
		html += "<tr id='tr_transaction_" + transaction.transaction + "'>";
		
		html += "<td style='vertical-align:middle;'>";
  		html += "<a href='#' data-timestamp='" + String(transaction.timestamp).escapeHTML() + "' ";
  		html += "data-transaction='" + String(transaction.transaction).escapeHTML() + "'>";
  		html += NRS.formatTimestamp(transaction.timestamp) + "</a>";
  		html += "</td>";

  		html += "<td style='vertical-align:middle;text-align:center;'>" + (hasMessage ? "&nbsp; <i class='fa fa-envelope-o'></i>&nbsp;" : "&nbsp;") + "</td>";
		
		
		html += '<td style="vertical-align:middle;">';
		html += NRS.getTransactionIconHTML(transaction.type, transaction.subtype) + '&nbsp; ';
		html += '<span style="font-size:11px;display:inline-block;margin-top:5px;">' + transactionType + '</span>';
		html += '</td>';
		
		html += "<td style='width:5px;padding-right:0;vertical-align:middle;'>";
		html += (transaction.type == 0 ? (receiving ? "<i class='fa fa-plus-circle' style='color:#65C62E'></i>" : "<i class='fa fa-minus-circle' style='color:#E04434'></i>") : "") + "</td>";
		html += "<td style='vertical-align:middle;" + (transaction.type == 0 && receiving ? " color:#006400;" : (!receiving && transaction.amount > 0 ? " color:red;" : "")) + "'>" + NRS.formatAmount(transaction.amount) + "</td>";
		html += "<td style='vertical-align:middle;text-align:center;" + (!receiving ? " color:red;" : "") + "'>" + NRS.formatAmount(transaction.fee) + "</td>";

		html += "<td>" + ((NRS.getAccountLink(transaction, "sender") == "/" && transaction.type == 2) ? "Asset Exchange" : NRS.getAccountLink(transaction, "sender")) + " ";
		html += "<i class='fa fa-arrow-circle-right' style='color:#777;'></i> " + ((NRS.getAccountLink(transaction, "recipient") == "/" && transaction.type == 2) ? "Asset Exchange" : NRS.getAccountLink(transaction, "recipient")) + "</td>";

		html += "<td class='td_transaction_pending' style='vertical-align:middle;text-align:center;'></td>";

		html += "<td class='confirmations' style='vertical-align:middle;text-align:center;font-size:12px;'>";
		html += "<span class='show_popover' data-content='" + (transaction.confirmed ? NRS.formatAmount(transaction.confirmations) + " " + $.t("confirmations") : $.t("unconfirmed_transaction")) + "' ";
		html += "data-container='body' data-placement='left'>";
		html += (!transaction.confirmed ? "-" : (transaction.confirmations > 1440 ? "1440+" : NRS.formatAmount(transaction.confirmations))) + "</span></td>";
		if (actions) {
			var disabledHTML = "";
			var unconfirmedTransactions = NRS.unconfirmedTransactions;
			if (unconfirmedTransactions) {
				for (var i = 0; i < unconfirmedTransactions.length; i++) {
					var ut = unconfirmedTransactions[i];
					if (ut.attachment && ut.attachment["version.PhasingVoteCasting"] && ut.attachment.transactionFullHashes && ut.attachment.transactionFullHashes.length > 0) {
						if (ut.attachment.transactionFullHashes[0] == transaction.fullHash) {
						disabledHTML = "disabled";
					}
					}
				}
			}

			html += '<td style="vertical-align:middle;text-align:right;">';
			html += "<a class='btn btn-xs btn-default approve_transaction_btn " + disabledHTML + "' href='#' data-toggle='modal' data-target='#approve_transaction_modal' ";
			html += "data-transaction='" + String(transaction.transaction).escapeHTML() + "' data-full-hash='" + String(transaction.fullHash).escapeHTML() + "' ";
			html += "data-i18n='approve' >Approve</a>";
			html += "</td>";
		}
		html += "</tr>";
		return html;
	}

	NRS.buildTransactionsTypeNavi = function() {
		var html = '';
		html += '<li role="presentation" class="active"><a href="#" data-transaction-type="" ';
		html += 'data-toggle="popover" data-placement="top" data-content="All" data-container="body" data-i18n="[data-content]all">';
		html += '<span data-i18n="all">All</span></a></li>';
		$('#transactions_type_navi').append(html);

		$.each(NRS.transactionTypes, function(typeIndex, typeDict) {
			titleString = $.t(typeDict.i18nKeyTitle);
			html = '<li role="presentation"><a href="#" data-transaction-type="' + typeIndex + '" ';
			html += 'data-toggle="popover" data-placement="top" data-content="' + titleString + '" data-container="body">';
			html += typeDict.iconHTML + '</a></li>';
			$('#transactions_type_navi').append(html);
		});

		html  = '<li role="presentation"><a href="#" data-transaction-type="unconfirmed" ';
		html += 'data-toggle="popover" data-placement="top" data-content="Unconfirmed" data-container="body" data-i18n="[data-content]unconfirmed">';
		html += '<span data-i18n="unconfirmed">Unconfirmed</span></a></li>';
		$('#transactions_type_navi').append(html);
		html  = '<li role="presentation"><a href="#" data-transaction-type="pending" ';
		html += 'data-toggle="popover" data-placement="top" data-content="Pending" data-container="body" data-i18n="[data-content]pending">';
		html += '<i class="fa fa-gavel"></i>&nbsp; <span data-i18n="pending">Pending</span></a></li>';
		$('#transactions_type_navi').append(html);

		$('#transactions_type_navi a[data-toggle="popover"]').popover({
			"trigger": "hover"
		});
	}

	NRS.buildTransactionsSubTypeNavi = function() {
		$('#transactions_sub_type_navi').empty();
		html  = '<li role="presentation" class="active"><a href="#" data-transaction-sub-type="">';
		html += '<span data-i18n="all_types">All Types</span></a></li>';
		$('#transactions_sub_type_navi').append(html);

		var typeIndex = $('#transactions_type_navi li.active a').attr('data-transaction-type');
		if (typeIndex && typeIndex != "unconfirmed" && typeIndex != "pending") {
				var typeDict = NRS.transactionTypes[typeIndex];
				$.each(typeDict["subTypes"], function(subTypeIndex, subTypeDict) {
				subTitleString = $.t(subTypeDict.i18nKeyTitle);
				html = '<li role="presentation"><a href="#" data-transaction-sub-type="' + subTypeIndex + '">';
				html += subTypeDict.iconHTML + ' ' + subTitleString + '</a></li>';
				$('#transactions_sub_type_navi').append(html);
			});
		}
	}

	NRS.displayUnconfirmedTransactions = function() {
		NRS.sendRequest("getUnconfirmedTransactions", function(response) {
			var rows = "";

			if (response.unconfirmedTransactions && response.unconfirmedTransactions.length) {
				for (var i = 0; i < response.unconfirmedTransactions.length; i++) {
					rows += NRS.getTransactionRowHTML(response.unconfirmedTransactions[i]);
				}
			}
			NRS.dataLoaded(rows);
		});
	}

	NRS.displayPendingTransactions = function() {
		var params = {
			"account": NRS.account,
			"firstIndex": NRS.pageNumber * NRS.itemsPerPage - NRS.itemsPerPage,
			"lastIndex": NRS.pageNumber * NRS.itemsPerPage
		};
		NRS.sendRequest("getAccountPendingTransactions", params, function(response) {
			var rows = "";

			if (response.transactions && response.transactions.length) {
				for (var i = 0; i < response.transactions.length; i++) {
					t = response.transactions[i];
					t.confirmed = true;
					rows += NRS.getTransactionRowHTML(t);
				}
				NRS.dataLoaded(rows);
				NRS.addPendingInfoToTransactionRows(response.transactions);
			} else {
				NRS.dataLoaded(rows);
			}
			
		});
	}

	NRS.pages.dashboard = function() {
		var rows = "";
		var params = {
			"account": NRS.account,
			"firstIndex": 0,
			"lastIndex": 9
		};
		
		var unconfirmedTransactions = NRS.unconfirmedTransactions;
		if (unconfirmedTransactions) {
			for (var i = 0; i < unconfirmedTransactions.length; i++) {
				rows += NRS.getTransactionRowHTML(unconfirmedTransactions[i]);
			}
		}

		NRS.sendRequest("getAccountTransactions+", params, function(response) {
			if (response.transactions && response.transactions.length) {
				for (var i = 0; i < response.transactions.length; i++) {
					var transaction = response.transactions[i];
					transaction.confirmed = true;
					rows += NRS.getTransactionRowHTML(transaction);
				}

				NRS.dataLoaded(rows);
				NRS.addPendingInfoToTransactionRows(response.transactions);
			} else {
				NRS.dataLoaded(rows);
			}
		});
	}

	NRS.incoming.dashboard = function() {
		NRS.loadPage("dashboard");
	}

	NRS.pages.transactions = function() {
		if ($('#transactions_type_navi').children().length == 0) {
			NRS.buildTransactionsTypeNavi();
			NRS.buildTransactionsSubTypeNavi();
		}

		var selectedType = $('#transactions_type_navi li.active a').attr('data-transaction-type');
		var selectedSubType = $('#transactions_sub_type_navi li.active a').attr('data-transaction-sub-type');
		if (!selectedSubType) {
			selectedSubType = "";
		}
		if (selectedType == "unconfirmed") {
			NRS.displayUnconfirmedTransactions();
			return;
		}
		if (selectedType == "pending") {
			NRS.displayPendingTransactions();
			return;
		}

		var rows = "";
		var params = {
			"account": NRS.account,
			"firstIndex": NRS.pageNumber * NRS.itemsPerPage - NRS.itemsPerPage,
			"lastIndex": NRS.pageNumber * NRS.itemsPerPage
		};

		if (selectedType) {
			params.type = selectedType;
			params.subtype = selectedSubType;

			var unconfirmedTransactions = NRS.getUnconfirmedTransactionsFromCache(params.type, (params.subtype ? params.subtype : []));
		} else {
			var unconfirmedTransactions = NRS.unconfirmedTransactions;
		}

		if (unconfirmedTransactions) {
			for (var i = 0; i < unconfirmedTransactions.length; i++) {
				rows += NRS.getTransactionRowHTML(unconfirmedTransactions[i]);
			}
		}

		NRS.sendRequest("getAccountTransactions+", params, function(response) {
			if (response.transactions && response.transactions.length) {
				if (response.transactions.length > NRS.itemsPerPage) {
					NRS.hasMorePages = true;
					response.transactions.pop();
				}

				for (var i = 0; i < response.transactions.length; i++) {
					var transaction = response.transactions[i];

					transaction.confirmed = true;

					rows += NRS.getTransactionRowHTML(transaction);
				}

				NRS.dataLoaded(rows);
				NRS.addPendingInfoToTransactionRows(response.transactions);
			} else {
				NRS.dataLoaded(rows);
			}
		});
	}

	NRS.updateApprovalRequests = function() {
		var params = {
			"account": NRS.account,
			"firstIndex": 0,
			"lastIndex": 20
		};
		NRS.sendRequest("getVoterPendingTransactions", params, function(response) {
			if (response.transactions && response.transactions.length != undefined) {
				var $badge = $('#dashboard_link .sm_treeview_submenu a[data-page="approval_requests_account"] span.badge');
				if (response.transactions.length == 0) {
					$badge.hide();
				} else {
					if (response.transactions.length == 21) {
						var length = "20+";
					} else {
						var length = String(response.transactions.length);
					}
					$badge.text(length);
					$badge.show();
				}
			}
		});
		if (NRS.currentPage == 'approval_requests_account') {
			NRS.loadPage(NRS.currentPage);
		}
	}

	NRS.pages.approval_requests_account = function() {
		var params = {
			"account": NRS.account,
			"firstIndex": NRS.pageNumber * NRS.itemsPerPage - NRS.itemsPerPage,
			"lastIndex": NRS.pageNumber * NRS.itemsPerPage
		};
		NRS.sendRequest("getVoterPendingTransactions", params, function(response) {
			var rows = "";

			if (response.transactions && response.transactions.length != undefined) {
				for (var i = 0; i < response.transactions.length; i++) {
					t = response.transactions[i];
					t.confirmed = true;
					rows += NRS.getTransactionRowHTML(t, ['approve']);
				}
			}
			NRS.dataLoaded(rows);
			NRS.addPendingInfoToTransactionRows(response.transactions);
		});
	}

	NRS.incoming.transactions = function(transactions) {
		NRS.loadPage("transactions");
	}

	NRS.setup.transactions = function() {
		var sidebarId = 'dashboard_link';
		var options = {
			"id": sidebarId,
			"titleHTML": '<i class="fa fa-dashboard"></i> <span data-i18n="dashboard">Dashboard</span>',
			"page": 'dashboard',
			"desiredPosition": 10
		}
		NRS.addTreeviewSidebarMenuItem(options);
		options = {
			"titleHTML": '<span data-i18n="dashboard">Dashboard</span>',
			"type": 'PAGE',
			"page": 'dashboard'
		}
		NRS.appendToTSMenuItem(sidebarId, options);
		options = {
			"titleHTML": '<span data-i18n="my_transactions">My Transactions</span>',
			"type": 'PAGE',
			"page": 'transactions'
		}
		NRS.appendToTSMenuItem(sidebarId, options);
		options = {
			"titleHTML": '<span data-i18n="approval_requests">Approval Requests</span>',
			"type": 'PAGE',
			"page": 'approval_requests_account'
		}
		NRS.appendToTSMenuItem(sidebarId, options);
	}

	$(document).on("click", "#transactions_type_navi li a", function(e) {
		e.preventDefault();
		$('#transactions_type_navi li.active').removeClass('active');
  		$(this).parent('li').addClass('active');
  		NRS.buildTransactionsSubTypeNavi();
  		NRS.pageNumber = 1;
		NRS.loadPage("transactions");
	});

	$(document).on("click", "#transactions_sub_type_navi li a", function(e) {
		e.preventDefault();
		$('#transactions_sub_type_navi li.active').removeClass('active');
  		$(this).parent('li').addClass('active');
  		NRS.pageNumber = 1;
		NRS.loadPage("transactions");
	});

	$(document).on("click", "#transactions_sub_type_show_hide_btn", function(e) {
		e.preventDefault();
		if ($('#transactions_sub_type_navi_box').is(':visible')) {
			$('#transactions_sub_type_navi_box').hide();
			$(this).text($.t('show_type_menu', 'Show Type Menu'));
		} else {
			$('#transactions_sub_type_navi_box').show();
			$(this).text($.t('hide_type_menu', 'Hide Type Menu'));
		}
	});

	return NRS;
}(NRS || {}, jQuery));