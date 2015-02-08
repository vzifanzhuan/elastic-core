package nxt;

import nxt.db.DbClause;
import nxt.db.DbIterator;
import nxt.db.DbKey;
import nxt.db.EntityDbTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VotePhased {

    private static final DbKey.LinkKeyFactory<VotePhased> voteDbKeyFactory =
            new DbKey.LinkKeyFactory<VotePhased>("id", "pending_transaction_id") {
                @Override
                public DbKey newKey(VotePhased vote) {
                    return vote.dbKey;
                }
            };

    private static final EntityDbTable<VotePhased> votePhasedTable = new EntityDbTable<VotePhased>("vote_phased", voteDbKeyFactory) {

        @Override
        protected VotePhased load(Connection con, ResultSet rs) throws SQLException {
            return new VotePhased(rs);
        }

        @Override
        protected void save(Connection con, VotePhased vote) throws SQLException {
            vote.save(con);
        }

    };

    public static DbIterator<VotePhased> getByTransaction(long pendingTransactionId, int from, int to) {
        return votePhasedTable.getManyBy(new DbClause.LongClause("pending_transaction_id", pendingTransactionId), from, to);
    }

    public static long countVotes(PendingTransactionPoll poll) {
        if (poll.getDefaultVoteWeighting().getVotingModel() == Constants.VOTING_MODEL_ACCOUNT && poll.getDefaultVoteWeighting().getMinBalance() == 0) {
            return votePhasedTable.getCount(new DbClause.LongClause("pending_transaction_id", poll.getId()));
        }
        long cumulativeWeight = 0;
        try (DbIterator<VotePhased> votes = VotePhased.getByTransaction(poll.getId(), 0, Integer.MAX_VALUE)) {
            for (VotePhased vote : votes) {
                cumulativeWeight += poll.getDefaultVoteWeighting().calcWeight(vote.getVoterId(), Math.min(poll.getFinishHeight(), Nxt.getBlockchain().getHeight()));
            }
        }
        return cumulativeWeight;
    }

    static boolean addVote(PendingTransactionPoll poll, Transaction transaction) {
        votePhasedTable.insert(new VotePhased(transaction, poll.getId()));
        return poll.getDefaultVoteWeighting().getVotingModel() == Constants.VOTING_MODEL_ACCOUNT && poll.getDefaultVoteWeighting().getMinBalance() == 0
                && votePhasedTable.getCount(new DbClause.LongClause("pending_transaction_id", poll.getId())) >= poll.getQuorum();
    }

    static boolean isVoteGiven(long pendingTransactionId, long voterId) {
        DbClause clause = new DbClause.LongClause("pending_transaction_id", pendingTransactionId).and(new DbClause.LongClause("voter_id", voterId));
        return votePhasedTable.getCount(clause) > 0;
    }

    static void init() {
    }

    private final long id;
    private final DbKey dbKey;
    private final long pendingTransactionId;
    private final long voterId;

    private VotePhased(Transaction transaction, long pendingTransactionId) {
        this.id = transaction.getId();
        this.pendingTransactionId = pendingTransactionId;
        this.dbKey = voteDbKeyFactory.newKey(this.id, this.pendingTransactionId);
        this.voterId = transaction.getSenderId();
    }

    private VotePhased(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.pendingTransactionId = rs.getLong("pending_transaction_id");
        this.dbKey = voteDbKeyFactory.newKey(this.id, this.pendingTransactionId);
        this.voterId = rs.getLong("voter_id");
    }

    public long getId() {
        return id;
    }

    public long getPendingTransactionId() {
        return pendingTransactionId;
    }

    public long getVoterId() {
        return voterId;
    }

    private void save(Connection con) throws SQLException {
        try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO vote_phased (id, pending_transaction_id, "
                + "voter_id, height) VALUES (?, ?, ?, ?)")) {
            int i = 0;
            pstmt.setLong(++i, this.getId());
            pstmt.setLong(++i, this.getPendingTransactionId());
            pstmt.setLong(++i, this.getVoterId());
            pstmt.setInt(++i, Nxt.getBlockchain().getHeight());
            pstmt.executeUpdate();
        }
    }

}
