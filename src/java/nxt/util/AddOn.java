/******************************************************************************
 * Copyright © 2013-2016 The Nxt Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * Nxt software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

package nxt.util;

import nxt.Account;
import nxt.BlockchainProcessor;
import nxt.Nxt;

public interface AddOn {

    void init();

    void shutdown();

    final class Demo implements AddOn {

        @Override
        public void init() {
            Nxt.getBlockchainProcessor().addListener(block -> Logger.logInfoMessage("Block " + block.getStringId()
                    + " has been forged by account " + Convert.rsAccount(block.getGeneratorId()) + " having effective balance of "
                    + Account.getAccount(block.getGeneratorId()).getEffectiveBalanceNXT()),
                    BlockchainProcessor.Event.BEFORE_BLOCK_APPLY);
        }

        @Override
        public void shutdown() {
            Logger.logInfoMessage("Goodbye!");
        }

    }

    final class AfterStart implements AddOn {

        @Override
        public void init() {
            String afterStartScript = Nxt.getStringProperty("nxt.afterStartScript");
            if (afterStartScript != null) {
                ThreadPool.runAfterStart(() -> {
                    try {
                        Runtime.getRuntime().exec(afterStartScript);
                    } catch (Exception e) {
                        Logger.logErrorMessage("Failed to run after start script: " + afterStartScript, e);
                    }
                });
            }
        }

        @Override
        public void shutdown() {

        }

    }

    final class BeforeShutdown implements AddOn {

        final String beforeShutdownScript = Nxt.getStringProperty("nxt.beforeShutdownScript");

        @Override
        public void init() {
        }

        @Override
        public void shutdown() {
            if (beforeShutdownScript != null) {
                try {
                    Runtime.getRuntime().exec(beforeShutdownScript);
                } catch (Exception e) {
                    Logger.logShutdownMessage("Failed to run after start script: " + beforeShutdownScript, e);
                }
            }
        }

    }

}
