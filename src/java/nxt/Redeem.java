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

package nxt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import nxt.db.DbIterator;
import nxt.db.DbKey;
import nxt.db.DbUtils;
import nxt.db.PrunableDbTable;

public final class Redeem {

	Map<String, Long> allowedRedeems = new HashMap<String, Long>();
	
	public static String[] listOfAddresses = {"1JwSSubhmg6iPtRjtyqhUYYH7bZg3Lfy1T","1Cy2FFLrqDTFzeScfieawPafAXj2i8ybK3", "1BCC9SkVuBmnWr1gocQgCzTZEjdvupjK7T", "1DE6puFK3MFZxMtnFiAWcLewQNqpn7GxDV", "13iCYHRpivtTNhYKc7sgC2AxzS3SzNTuVD", "18VD8S7Tc9u9B87u5DZAbbshzxLhmqtE9R", "1815FyZMLvjTKiTSmxVhnw81imJZeScdtm", "16HLVBKgSmTWq7igbtVmxKU2YujuckpEnA", "1Kf6ggJHdRS8oeyjDSx4MouG9Bdzr61EUf", "1F1NVgJe6KTNRucyZTZvWoEqBUFnNWRSMY", "1NU9xzCWnrZCXHa3rRZtdi1QqvNpRMRhvH", "1DuQF15bDFp64qsxEDJXYabshnkudvfWar", "1DuQF15bDFp64qsxEDJXYabshnkudvfWar", "1KF6tLemSoVjgtQ7WiFNseqKdnf1HZh27V", "1JmFmpbKq7S36J7w2zpXPhJHFFj9Zc4Gbr", "1LvVTc9TyxYVYdPQcqKxpNoJfbJNdGNs1b", "1DksYUW6J9wGuArxM6n1eMhjG86mqxnmA8", "1J5qXLjQ3o5QJYnzriQdsLhMNCnQVBGU9a", "13pCKj73DbGgGcteSvZns19vF1XywyNsJU", "1BVap5sg6gTNg9VkU5eu5t4JNjbLAxEpRM", "1LBKdycznxbx5egJBqz19ijugwHcHFY1jm", "1JyuxSxPsd2A9HfF2X7EbFFZfSAPZPs4M5", "17SnP5CDEUKPZBa8yXTUqrvtnM3VHB2VkV", "1G1tyHcpFQzQyxMFxna8mB99kPfaQjdLPW", "1GK62XAceZtLQmikjNUVCQrrLRgkAN8GfJ", "13NeNTpwFW2EMeh8B6q75t6Vxj4pGBMAKW", "1M5xPWSfQw4qAhSibZuaSWrtukso1eMmBq", "1NXscUA1e7EDDaPEDwNgqP53BF3a3yMQEh", "1QEEd5GjiWaeE6ZdgB7UPteyDQpMs6PRSp", "1ByBfJpqvZs6QGutzh134PEqYMpsM8Xwkq", "1KyDFiDR21fo9XL4Dp76UJV8ThfdzYuqFf", "1JuTYh6yGiSgdD5HebFYAgDrhzXN2vioR1", "13pCKj73DbGgGcteSvZns19vF1XywyNsJU", "1FzxRgEPRcsjzQ3X2tbZPUkfa5ktH9vG1Q", "1ELZkisGYyocYdSsd6MTsbXAyHWwwPBuGz", "196fbmwTBkFm3xjUisMBSggmV4xyR5642W", "1KFVdnGMVPQ2gGJJZNKWQq5pJP1yVjUcLb", "1KyDFiDR21fo9XL4Dp76UJV8ThfdzYuqFf", "19ZFUNJ2Jdwm4yR2Y2uQ4MRNdE6L3fQFZH", "1KyDFiDR21fo9XL4Dp76UJV8ThfdzYuqFf", "14F2JkzcJBhkGFExfPQLsWxzUtZaME3JSs", "14F2JkzcJBhkGFExfPQLsWxzUtZaME3JSs", "1FzxRgEPRcsjzQ3X2tbZPUkfa5ktH9vG1Q", "1FecDmcz9uLbDFrFQz7jbxCi9mFNHnTisW", "1EDTRFD6JFb2N6EYzAcB2RwESLLfUbT9a5", "1Voidf1WWAwqMd2uMLr5uXisRyjPnWRA7", "1M5VHFAvwrTgeFVASZHeiAbETMrbaUWHqE", "1KrJ6e325yXVrNpd6NgYcuiRfvTcbZbBAR", "1KHsvBy9B7vxMgoNL1n64ChfTAczbp1ASh", "1MNStNrkraqYaawKSNaZ5d6GqzJm4EGrJY", "1D8AJUBvkFLiLi7qqzLNe1SnD7sA3duVNP", "1Voidf1WWAwqMd2uMLr5uXisRyjPnWRA7", "12XRgca2NLFWTEsmm9gg3FqfDSYhbnCXcD", "1PhcxuPuhGnDH7GeAcYTDkky1cdcZt6mZT", "12VZ3shGNn8wkt6tWYSDSr1ehiz2NDGBhP", "1FzxRgEPRcsjzQ3X2tbZPUkfa5ktH9vG1Q", "1AZMaW5vswDpboeGfNVA1ueoNab1nfHfCe", "13aKeNqzEzNrdFV1SfbZ4Spg8x8QqM1VpW", "1P37KAuSLGUmoRTQYWqeXtTCRuLsvszuZp", "1BZCTgfergVvwwUx5kMDFPHYKjrk3fnf5W", "1JuW2mEzofGwKrYfVx5wW67hrPvpBAJd43", "18dz62bLpfMepyg59nCnkBrufrQAz2H5gt", "14CpY8dRsjfLBzussdADgAQwFKbeWuz8j3", "1PACs4zsCX3CLeybt1ijj74Jj4q71wrBeL", "1Naira51gy4pc6hnSeokQ2mCm7tQ5eZyZU", "1G2pGEM88fqnMtZdJNCLF8wh7T6YQSkWog", "1LtDqWvBoFDF4aoAYzBMmsgGQ4g4HsmYJo", "1nMrSrMAiEDWopZKPUFQC6fBx5EGAsEdH", "12iDQNAbfVduDhfWhFevyLCJBGCJHd7NmH", "1B9f9HChAAonTUxJPNU9PkUu2zRLVA1sLv", "18szmRivgPcsPVes3QNyR1Xne6ND4xj8DK", "1HhRfRPDLcUk8J1zpURFug1xSpacaeXCtL", "1PBpVC2P5YrMyabyFD4QiS5jvhGCxCKANa", "1JFAUpkrjwU6cn8Af2npwaQ3c8b4eMsbSw", "15E1CktEoJGbwm4XNS9aKk7ijP93jbWNuV", "1FTqLnbjCUiF84cbQMxaguQ8sU9rikKhwW", "1L6KsD32NSo5kcDeDAwNiYuUdpozS7mMPs", "17MLkiXDu4iFoaUoMPhaUrvjVSSMuBkJhH", "15imAyVyDtsuwUdLccY52epJt7Kakg7ZnP", "1JAnM7Xy1xJ7Ufzv46CNiVwrRfV2esVotL", "1MhLm4VaZ7XWPPsFrZnqUfUUehNhG84Ww2", "1Pw6qbzQivyb7kBpaBrQedcXJt7d4uQu9G", "17hdJnw7J5jvWx75nPenQP8hfstQSnw7uG", "13rUkZv3mmTqSpRMaPmKhnBJ3f139ncAeA", "195zUtZLEHYvwDTbPNkzHBBF1SoZThDZyy", "1GVdqzA4ULCctJJZRwKX1mRpYdHfEL1WwY", "1BLrpCrTXa194ZszTNYoXiT6VESR4nyo8M", "17y4rTjnqLe6AST5Ygv4DixagaRj4o75Wf", "17736rfhafvjFk3usfo9zQFR3PfHsvtiYQ", "1uCjf49oZSLaduGNF8nPgCAeEQDzYqEgC", "16SxRuDSeUQp1mDnaLTHBMjWsUtPXup1dZ", "1GxENbATk1HfUx2ijYaSvRDENXBymF91mw", "17MDYNKvWD7JzB1aoxX7pnCiPYUrC8LQgn", "1KeFywuPFoZVUkmzZC2qgNco8qU9rPVTvR", "128XumH2QsZ4BGo6jEuTDjShcretUxDqLi", "172aiWHKukWFGE11vUFniZVU7kp6ZRUZyU", "18szmRivgPcsPVes3QNyR1Xne6ND4xj8DK", "1Annm6pLezYsCDR8q32zFGoWqWkERDwaGu", "1B7jF7NDfFSdToSiPvYWnHAxEUmNaMfYe", "115docnYkRJopTHTeEgTAii7Jv7N5wBhSz", "1G2pGEM88fqnMtZdJNCLF8wh7T6YQSkWog", "1DyHXmfFqfyX2HHt6RhqXdePDwGELwUbJx", "1GV6FRbvNuunDxRdJ5UfT7oHefwB6ugcZd", "1MneNPGDvscZdn1aGcVKcRvLoauCBTRfaB", "1JPVXfTmC6xwbYW2RgSeM6iKZP3NsMBByT", "1CmpnV341LScP1iakDSJqsf6EGinJjuP6G", "1BemsM9i3gjshwmJ2txfjXXnFHfqWs8Huq", "1DMHGapZs43By6siPrzZUgWxCFt95V65nk", "17dfDBiqCmFbUvPaXDACNk37TbkYTQ7bQB", "1GVSLaHWsaMHkEqFYdrFbraHxLx8Mi5RX", "1McnyFoBSC1eeBaXPA876MTP7PEQGrNAWc", "1MVqoPXUTJ2BgFetBFojvtauotz7TGvnka", "1FvqVxaCtsxtNTBTYndhsS9Xa5LxqjZGoo", "1G82zewBq8NzpC1gFHVsPRC6K67mFNon6a", "12NZvfXZ7VpuxCfWJ1GJ3A927An1vW1Uh2", "18szmRivgPcsPVes3QNyR1Xne6ND4xj8DK", "1hRHm9hxrvFuyUugH64iQxCdyxeFThWUf", "1EdmcmNLjtzxyrGcZ7TsMB7Up7aQ4qSFcr", "1Gc1Zz8VTrnTrq5DLa4wZ8CmD2AaSaUJ4t", "18brPBR9c2eNuHMtzEhr9ayTxCBmipzrk7", "1CcYJtqKDTMPtsPfiuYJjsJoL9nj5f3b3s", "1Mmm4c8PZvNttyzZLJJwyMR1QkFMA5GF5P", "1LM4KFSY6gT9HAyQFzKhkXK3A2LafLax1y", "1B5WmWZi8twk83iaN3YbjmZEYgvQRU5D2P", "1HJn55Y6wbtwpSh2rauTy8Un5Cduk12gaT", "19FH6UgAHbRSwNr7sGJxNYW7p8NA8ZxwYp", "1LJudB5t6sq8NTsDj2Cg7SvUq23z6Q1qju", "1ArturoQZAbmeYtZgJFyRnK5GsRLitcBfH", "1DxKXSnBxYcfrMqRtzrX9WbFYPktSRa5E3", "1FNqi66JVzE3kou4QQX4SHDAxvYLVpDg2U", "1PpUpmpGN848rmiVg29CHj6StP9GSFvCMe", "1614UqptoSZd5SznRsvoK8tzYZuR7TUfpE", "1LJudB5t6sq8NTsDj2Cg7SvUq23z6Q1qju", "1JainHFBinv3cCELLJW7scdxucu3hU9mgk", "18szmRivgPcsPVes3QNyR1Xne6ND4xj8DK", "1LJudB5t6sq8NTsDj2Cg7SvUq23z6Q1qju", "1CkH38kWDAf6d9EwVU38s6fjvAShu6tfvg", "16d9C5nBjh78JwasrPg9CimwUBBoGArfSr", "1HcpjzYFoonrhFYDs3AdUxbS7Qu6fS1XYN", "19m3QXzPX9gwZ3iD3gq2RS9EuqwkP6GkdV", "1LJudB5t6sq8NTsDj2Cg7SvUq23z6Q1qju", "1DsTZJvGo4ztdsBMdEpeqHHMdaTcZXMd2N", "1LJudB5t6sq8NTsDj2Cg7SvUq23z6Q1qju", "1NWg1Mga4n5CWLwQPrhkQdLJ9fJdJy8zbV", "1MqjkKzZjTTvXHvuNZzEqSSPhidhu8H44Y", "1LJudB5t6sq8NTsDj2Cg7SvUq23z6Q1qju", "1JainHFBinv3cCELLJW7scdxucu3hU9mgk", "13Qnwu6UUHrPfa2bo89v4kqKkUrDW8Z2Cf", "1D7E61eVfnbZJdk4cPWFaDYyQ9usqY8CxL", "129yMeagKaaEEyQQz88zAfuaeY8YYz26wL", "1LJudB5t6sq8NTsDj2Cg7SvUq23z6Q1qju", "1B84kRYre9YHXoASmAAVvaTRuEHNQ1tQCQ", "1B7R2yexLNxESAhTS6K8fY9epLXtzP2QN", "1LJudB5t6sq8NTsDj2Cg7SvUq23z6Q1qju", "13ujSkcNMmzmNHfqr7p8ddnH9KHZc5ngx7", "1ELCSHBK1JQQwDC96MFRAY2MtYYuVF3bwG", "1ELCSHBK1JQQwDC96MFRAY2MtYYuVF3bwG", "1odajeaZaCpHrxMPKq4b37KbsKnGRkxwt", "1AMbK69tF4rpjotkAekQoEiFiA9xZaRZ1J", "1AZMaW5vswDpboeGfNVA1ueoNab1nfHfCe", "19ToAspzr9VoRW5VgiSEGwSP5xHgdM64Z", "1B7jF7NDfFSdToSiPvYWnHAxEUmNaMfYe", "16Ztzi7YofzJx1Wm6xreM1VsrQybcsWem5", "1KW1eim7pRcduC2EaTBCPQHy9jCBp4BsX2", "1154rigDAHKtLQKRZgS3cz9PUxouck1c9k", "17o4Dyfc7GMbwMgaeXhQQ74nqDcdmP7RVM", "1FndprLxopAaZFMkWDmACEhmS2W9s5CVfH", "1Ha2sMvSMgow6Dz6bqhXLfx1d73JNPHn8y", "12bycbUapdamVGhrnvUJiooqVQQ95Mei4S", "1Ha2sMvSMgow6Dz6bqhXLfx1d73JNPHn8y", "18ZjiPMDuMdMqvknMdhbpDKBRbBXywCNKC", "1Kv6jbP9JWHagfrQjjfVzMcnKEGynZ7pf3", "1Nqf2fnLHEt1qLxGHFDmVspy2c4b36FeFo", "13jRQ6xKNiRq7uvMFVmV9kQnPVbxRe385W", "1L4kRvtiQvgBj4J79GhLyop1VNoYkFaN1J", "1Nqf2fnLHEt1qLxGHFDmVspy2c4b36FeFo", "1MS76xiUL311BSxkSmP4iT2zN74e62QyUP", "14T3ub4q64EFdLsKuvumRabscLUddh1aKc", "1HVPu1KhFCrjvzZLHvmU1zvmmCx35rCFbk", "1KcXadtYBJgavHKNfE3VfmTJyVZdXUpCmN", "1888888UhkzqRGNWBVcmkXcFXeca676BuY", "15sqodDr9GR1JsuKa3kTU1a75w52Bw44wG", "1STmcsewju8ykyuUGkmjVEuPiVyjXc9Pj", "1J4Z4VQtrnFw3MBPtgeXyAgqf1jedNhQzS", "1B7jF7NDfFSdToSiPvYWnHAxEUmNaMfYe", "1Mo2U2did2AFKX45sFg1tbK2z8K8LuHWyu", "18fjqZuCQ3ZM5XoGjAKfus5PsRfoZmf4EH", "1K44nMoCmDEf5Wa1v9Y2UFUiM8Qai99RQj", "1LXzyxj848i1d8QTnoVegaYziSYz7z6DNi", "17gvkFpYV9842qZriAtN9S2L28sZnJx4oE", "1K1eaDW9TvBeVxo5xafQRoY5BGhKMXsRnn", "1Gd4niapnmcn6UJpByhBJ2c6uxkpy1k1MJ", "1EmrdgNEfsqDwfGF4LygEFyowTTZTPV5Uh", "1BUZTuymLjkwEouiQuyR2Cp2RwLFKQ8Pdc", "1GL6C71jDeDsszFnkdYdQsYLxtJL2arGWm", "1Ptc1krYh1NAidPiqovQRjy8YtpcYH4kdL", "17aVroAkjg5dS7h3fdfKav72kDgivavc4d", "1GR4CaxvHwBgDVyBXroPDPkjNTj4HRkKkH", "1C9Hg66TbbQtoaWP3Y31MCQp2JC2TmVPd5", "1F5C89a5s9tfMYdUxcM7JfaBdZB6i9BKDP", "179ConMuWNepwGCkqaiJuFfqEhCvGANGwH", "17qB4S8KJhovDj2ag8iFtQfcW2isy3ZJj", "1Q7aBEendYyjtrA4wLPY2aHMYfD3kuPBXX", "1ATMQmcUszWtFpbLs8M8iZsZhS6oZCk7bN", "19n1xVC4JWsx8GhtfoxdDfoN4Ensz3mg1N", "1LcEW8ycTnjcSdQRZ1o5fz1c3GEFSwZtxQ", "14LEPr49hNgfM9EaDU67UFTwWey8rZc3wU", "1BH2KAuUpiuhkszMA9fenviQRbn6pHgGE", "1AZ52RzySPbdbTQZwxkUnAuYCnrQgQ1UHD", "1ATMQmcUszWtFpbLs8M8iZsZhS6oZCk7bN", "18jbntdbaQLEkc8sknUed5QyJ2AgAy3a5V", "1EQ7zx9HLdmBdfUUwkA2fnCiKF2M5bEzwd", "1CiYTFjgVWJG3GFAXehmyp38Z96uHPTqut", "1Fd6r9JrvB1W2MgU4n9vkjzG2NJ5Rx8b2A", "12B571aTWKsrq6fohnQXjT1nhHcC2617rx", "16E4kUAf739pK7c48J5uJpcdB3fUN39Fcb", "1LzCmnXqvNApPN8BLcyUWnddc7x4QQ5caR", "19tCadLqSh3hD7v9W2AomqKj7WpoQFjJYr", "18FCNW11Bv5emmMRj7P1pek9hxXXCZqda4", "19bCRByV1WE51vfRqEKtweJknfzKevm7wM", "1Cw65HfhdehZfu3MEPwpUhdcooBSpy2rjo", "1HW2VtmAuomDjBPAqVVWPWfRuDx8S3qzMK", "19oo6gsF6qf6C4CX1yFR7fv5wHPLm78Hjo", "18FCNW11Bv5emmMRj7P1pek9hxXXCZqda4", "1GQjLN8jEVWLEPxVXgYszbwhhDurmxar9r", "14skxeEv8cniqDm2TxjPumM5sJpghqZo1b", "1NArvaEWFrt4R6G6Uxar6XNM4s3h4sBqkw", "14Tc6PU1kzLnmooPHXdsy4ePtPxMgoXEYj", "1GQjLN8jEVWLEPxVXgYszbwhhDurmxar9r", "122R8pEGWT4ABeh9BWsz4DPhJ8xyYMq9TQ", "1DEDnBdU8FCjkacRf6Ttc2CQAS7FNFrfwS", "1EAhDVNZK4kPAoBswQeDWeHkV4taVkqfsb", "17Awa5AbqkFiUmW9PxdBhYFGs9n4BWBTgf", "13L6Qb9VJXS4PvGPbtWwyz8roHwC7DEHZX", "18jsbdMm2AyiAv54rrFC5ncnQiLTh8FzoP", "1pr62K6d6MYddi6LfY5oPoaY5AEfn7Utx", "13sUuLiyLU2kdxRpfsNUJXgLR1xiNdadBG", "1PXbfNGNGB65kTTd7bxeD7RBkPiL1AJDiR", "16wNqcVCy57GdtTKcPfQaQmtmkyGZtMcBA", "16oCBjNuxStiQq5RbvdgkCgCnkSpewCr8N", "15MvcgPMpWLZuRtTFHhE8LBTGP45CE7Cos", "1FDg5uY5SVEQexpwmvZcnLGxFRuy4q7NQp", "1D3rqNoj9CjYFqSv7nzL54BFaZMUGkQ29A", "1B7jF7NDfFSdToSiPvYWnHAxEUmNaMfYe", "1PeCuCMcHcvBHzQTKr1egZRjZJqQ1UnLb1", "1NTQfZFBN61ke2Dxb7dQ4d4oNEMsf5fq1u", "1Lu7yvwyfsPno1rdA4mu2reGsC6FKHnmso", "16YBpNdzKe58KmwPA3cBaf6zhqXCdTHiGp", "14W27o3HBYxVRkeJj36Qj6RHHDj8ga4fPY", "1ABpYSgv6tcUpF4ryiQarGy5Rqfy1qwC9v", "1LovVqqwnLYzVUjFsycPDtx334BmCtcha7", "1AdWkjLpaCteeKznVbCXLXFoxymJeKz2Xz", "1LZNS7UNiL3Q74RuGTzY593jMP8t2hKfDb", "1GHkwabM8z66PiTfcNCTTYkNnvdF22SQwY", "1AmmLPhnrTQAse2dWzTPnV6mHYjhYVCr3j", "1ZcgfRp7sEt3XYr6QMc4x4AkYKHfrkLAE", "13StFxJgNBiHMatJqnkBGt1damZPZV4qeM", "19NL1hXyD6omKMMbomui9z1o9mgYENXKva", "1NKbkA4KxwGayoK5cBeQzPMVXCXAbcLtdp", "133b63y8JB8zFCVnE6YhP442nnSPKj4YfF", "1NkATnRy31UTDpkuzEru2o3nGzE5JYm3p8", "13tLKf7nhd7t1rgnT5mVMNpEDyQbbbiABJ", "1K414ZEozoqWr5zjMpFjYuKtGTXSAJb6QE", "144EvecNyPVS3wS4uuVgcQrnK6UreKFk4v", "1EQ7zx9HLdmBdfUUwkA2fnCiKF2M5bEzwd", "12ucKWqPxXBxctauuPcJY154rTm1bnPNDM", "1CiBuR4aYoAfhKt1AhMKiJMGwkSCaQuWxm", "1Kr5JqiaCGo5SugDPdFSd8KJ5GofVhzhqW", "1BexuDc3UUpMRuxE9YV6TqVWTv9dhEvucF", "19HrkEdm6sbwdm5cGoPAaY2ccxpUYAA5mm", "1PeCuCMcHcvBHzQTKr1egZRjZJqQ1UnLb1", "19fLMHcFPX3CiHXTDUnNJg3ES23yXQTYbv", "1EAesPzaxxiffDVHAvHJZWxqNNyH1yq2YZ", "1HgVAQfFqSycVMxRUtmWUc772RS1BQU2XF", "1EQ7zx9HLdmBdfUUwkA2fnCiKF2M5bEzwd", "13UCGZ4qnGwMiP4PqqUx4Qi6tqQfvcqt2m", "141Np5ZGtNhbms7ArCrSmALN8q7vVUXDTw", "1LE1hYNegqX2CQaPsuw3ovQrbMKuAacbFd", "1BexuDc3UUpMRuxE9YV6TqVWTv9dhEvucF", "1Ha2sMvSMgow6Dz6bqhXLfx1d73JNPHn8y", "1JMGMnkv4skseqfkcSizUFdT1JuN9wVUke", "1J23z9TRWiZRDHyE6xQTsTnkA2g8cPp3UY", "1H1sAkEMqJ6w2yms3V26nzSKcjtkVdzkkv", "16bekfRKhSKpUkz6brFCyKXwqJobzmM6mi", "1AA6KycbJ32bTphqFKZTkSCYhaeh348jkE", "1Fp1oVFRGBpShbWLL5VjRsAs7sVKKHNE5y", "1BexuDc3UUpMRuxE9YV6TqVWTv9dhEvucF", "1BrKwsVmHvHxnbgGnnQ4U2KC4rV1a2nUS3", "14MKronX9pQJui5vVENveDqwKFE6bxWVk4", "18iYdBTcAY2hPZiKHqDz5D61YjrKgJ7gyY", "13ckSm9CJL2SPqiQVrDC2C1UxLBUV2Y1Xe", "17gJqvqZ26i22p3sKaKRqAb12sZF64meFY", "1KDhNhA3m1mg1CoB6CT4tgzjAKBLqUrLmG", "1B3zoYVVRNXkv7TegHQ2xrGbrq4pdZyG2m", "1GGaL5BZM4LMScS1mphamvC9PmkTDT9VFw", "18Z18VmxKiXRz3Zd45ejRv8vtkFUa7e9GS", "1DnXbLXgAoVxwKVmXtETaEPcDLxhb8HHox", "18u8n8GjQJavb6Cs9WywR2ZhfJxUKP3YJU", "1JRuH2BFgtFgiMaVtCPzvnTY1PYAmEJEG", "1Gx4nmEt8VyNeSjn675o3bHZ7VSaZjS5yk", "169R7xxSRxZXmxNX3YoKNTtaYQwHLNZNao", "1K4iZ9FFDx5CyAWKgfTH3hSCnVRtrmm1eS", "12oipr5BLUhanfsPy9xG5X4Foai4Gh9FNv", "141Np5ZGtNhbms7ArCrSmALN8q7vVUXDTw", "1KVcWB6VZhmAJgaWez6pgETEoX8nmAjzmE", "1C1ZXeQUJQGi8GL7iyjoi6zfGd4EC25981", "1CQ6koPvnna4d5ENNhaEzRHmLEDegMaP2S", "1HLFG8EuA6vsfUi5tw37PcUCnASqA4x1w3", "1MrEo9uD8tAgHML5JLzo9biUkKH7ggcsp", "1BeyK8UPBAuvKahaeLbdDBwuqu1WJRdnca", "1J8kXzvDLHuQQTTNt7bNmMKUz25RPJC8wz", "1BEbASKFwGuJfwLHFh1jSTWSr6RMviN9hs", "1KazZQVFXT1LdTAKPh4b36N1ETwJSJv4Rq", "1zuDxdhaLrEyzmSbKrBRnJputgccSY5wD", "1Eq8bqMRBxADqFVW1YUP1zYyufYBp4kfs1", "1FtdAZvrTYNNkgUR6WHmM8q9r6mt2g7tT3", "1GzikhiugNF4uHxsWaLHgLBsD4GUeMQiDy", "12S34uZ2HDiVgdsY2sV8nZpQZHwsENiqoH", "1FT3ko6cZwTsNWxgroujBpsB3wrpu4NQ2a", "14UwzH27d7YGKvkzQLbp2EbnAQErkSHTKB", "14UwzH27d7YGKvkzQLbp2EbnAQErkSHTKB", "1LhuczTy4Ep4VVXQHraL2jAN2AZS95z1nE", "155VUG4hd9pGYsQeuyMvrbGgzpP7nSzJSn", "1ArturoQZAbmeYtZgJFyRnK5GsRLitcBfH", "1friNTBnsATv3Voyujd5Fn2eDAYXcNE9S", "15QmJV44C8Zs2AqATqBpvgp8poyCcABY53", "1NMTFJ278TJHFoFPZwxV9ZyseD6inn2Zj9", "1GVDe4y5VwZ8jHNuF8HxXuKzKSXPfjF223", "1B7jF7NDfFSdToSiPvYWnHAxEUmNaMfYe", "149pNpMNVQCCzgXHwckkkd1u34efahQicQ", "18LLinoGvUvSSQXLovt3fDWXwMSfmS1UDG", "1Ahauxg3uxhcnxnSdmqKVhomz3MF61ynxA", "12py9NUvkLQBqbBS4De8miMREhjpUrcKZp", "16K2qfVXgwzAJkXun933zPp1CxpqjeGLbR", "1J8kXzvDLHuQQTTNt7bNmMKUz25RPJC8wz", "1594FrR5ZbS2npVC3irmtNfKV3n92nQ8Zq", "13Qr22PY9862o2AMwGiJjELbPFg1EVqjvY", "14yZJxhDKQkgaK19PEUpguaPtBrv2hbJxe", "15vUxBo9S8Seob94aAhdsGps7MEzBoViPa", "135uZKXDhYYhiLisdCvyx3fLhKS1mbp8nE", "17cHZHiKHpUk8jmSYCKqpzT3YcaPom16Ev", "1K5pv63rag715mN2REYcfMbQs7RyukPuSK", "12wspBHpbn3UvZvmNUFQ4tWephVcx9Nt6Y", "16y4wASazPdKEGDcoxDAL3CWrfBse3g5B6", "1KKVprQg2zYLceAYbx8kJxbzGFUn6bUX1C", "14f4hDX2z9cwkpZuSZqiK2uj3aakNFnoHx", "1Kk1uqZqZiWWGFzZ8onYGWWG3mRVR6P94H", "1J8kXzvDLHuQQTTNt7bNmMKUz25RPJC8wz", "1PCmmJu8c7BgUXjnM8QqTQ1Yu5YM7MeBDa", "1D3miCgxVoV3yuJDPG4RAtXu8qnLdRE6j5", "1MzNRG1WUtLgKTkpxMnUAh3F257Wkg1JQw", "196PUcEJfiNYMdDaXhnqgqzzA3cjHpLmSt", "1LovVqqwnLYzVUjFsycPDtx334BmCtcha7", "1J8kXzvDLHuQQTTNt7bNmMKUz25RPJC8wz", "1LovVqqwnLYzVUjFsycPDtx334BmCtcha7", "1LovVqqwnLYzVUjFsycPDtx334BmCtcha7", "17KWUsihUPxeiVYxFZqTJxFtno5rxvV89g", "12cNxny1B7mmMMNTZgrsXGiqk5hCZCJnGu", "1LovVqqwnLYzVUjFsycPDtx334BmCtcha7", "1KKV9aNR1TnWnHNzqpX17Jio8NMoXLycCt", "1EQx6f1zYK8LpUa7mWBPTEJ9Ny34jTvio", "1Fwpiz9CpFJvtca1aaJP7oQKq51FSF4R1X", "1KeBxECUCqQ53MoJSbYvuKCe8idDGUHoxR", "1BDt2KN7GcBAvzKgTLUDTogg1zZiWKmhaW", "1BxVCHGP9FZA2BcvLtLma1V3pDJAuo44DK", "17hGzZMUzMB3r1MCeHefohyYkfxXPKfKN1", "1Q4P6QW5TmtB8u5QsSoo8FihD7AphHML1w", "19yegjdDMdx3ArzASkxkpkpyycSpmag6sW", "1HLEZL9LxSmCeZMr3XkMsWYSUCC2EXUSaL", "1J75pvyVUyRt7JpW6RQHzYi99QzSwkfjAS", "17hGzZMUzMB3r1MCeHefohyYkfxXPKfKN1", "1B5nabx9sJs6somo3PhN2zC65iVG8uFTiQ", "1EePAYbyhidFDhKsu3KsfnY8BtmcTqttrb", "1DLbmALgy7saTfpEsbweP1es9CmVVzHpts", "1DsBd2BSefpZ7NWKJYgjCWHem6VXSip2PL", "14d1PAGLCdLA4t5KHzPhCAf5V6ekYhDCME", "1GYvGYrUqLGzQoErLhsegPw3nygpNFwZR9", "1KTKP2kacPMA824UJ7BN9PxUx4fman9M6m", "1EranfPkcFFAwbXDjzTix1m3AxkXjr7ZDD", "1MaL859XbZ6AKrzCj8Q73QTGA5fVZ3mhYm", "17ASKQx5PxCM42Ym8MEvaeAPjRwCP3asXK", "1M7rCmfjBGxwGRtZXv5K8Nzdy3A7v4QTPm", "1GmY61vTsBjvY9cqvQY6LS9BBy6JSqjBYm", "176bEJsG2tHEo4bsA17AguZN9jMU6LmYBX", "1CbhHTFFaUjmDwwFYucSLKRmD6pUtKbVNN", "12LDkCjLBJJSXpfrTcR8dMFVMtrvmF2Prr", "19Zd1HCUXyveUvj7QwHEqWiF3To7wuv5PQ", "14ndafdPdqB2wLQddXT1N4kaVzvgdgi9eY", "1AokP9W85pRrH1TdvvfyAVuMvPUpi3Jyie", "1BexuDc3UUpMRuxE9YV6TqVWTv9dhEvucF", "1LE1hYNegqX2CQaPsuw3ovQrbMKuAacbFd", "1Db9bf82bZT5hnrZjjqoagGv2nQr6X5dLV", "1Db9bf82bZT5hnrZjjqoagGv2nQr6X5dLV", "1BexuDc3UUpMRuxE9YV6TqVWTv9dhEvucF", "1MqE1mkEX5Jj1VBsHuEkGDiaFFpHA1E5LF", "16tQCYcfJMJ2n36T9chCbNUQiB4hZeCB6j", "12v4d15AuopsVzudct7J8LJMm7qH95SxA1", "12knCup7U5V8oQ3FWPD6Be9L4HfFYD4MDZ", "1BP1Ai1CurMqtpvLx7jU1NB38oe2Vrm1b6", "1JJuVWjefhYk7VfFi5D7UovVdA19X7hCv5", "14HADDEQz5KY6V4VyP96War1UqQdq66W1e", "16Z2uhUBhg6VWk3eXEpgbbXVTm5Phxove9", "1HNPGJmb1bSUk6tsbJgsFjVc8iDJ2uNdgN", "12py9NUvkLQBqbBS4De8miMREhjpUrcKZp", "14FPA9qD3hFDtLjZHDy2JUDkixFtEXDiwx", "18RQH8yB7qMapzqRQ4Um8qdHvs1EcoKhsY", "1EwBpn9NNPGE174NvkSjhtdAsFdUCiHdk7", "1NUtkYVqjF6rmUkSrZEdmmRjcq3fgWAag3", "1JoGVcZdSXHKKH2W5YjxwqectqsTT2QAj5", "1STmcsewju8ykyuUGkmjVEuPiVyjXc9Pj", "19Ua5ieT7V4opixMv6aSc2pCK3Rp4sJz9T", "17GiWvV6Sb4damGNKUFzNdesBxYXG7cknm", "1GYvGYrUqLGzQoErLhsegPw3nygpNFwZR9", "1JEhp9E4aLfAw1HCaXTAQMUGRLGmbP7Xh4", "18q3cUrw6g7CGs1mPYuxcQ5XaBFqU4sR27", "1Jgdk1NcMVck9nXPjJSE4LxBtG9SsEEWRy", "14UwzH27d7YGKvkzQLbp2EbnAQErkSHTKB", "16ayhut16RXFeKYTnWaBhCj7DNocRL3u8Q", "12GTLir6rEfwJpShcSqYxybjoZYVhF9t8d", "1Le5NkD6aYnzZPKCiadVAPzcbWsH6CjbYJ", "16XNyCf9rEnn7qxPeaQPDXz4JY1FhzDbDA", "18GfHvyRd3UWRB8iVQpKJg1trVAbL8vq5G", "1Fm6b2qFJvp5HiTtBJn8vwsgynm18yD2Ru", "13UtpxQJuoTigEeeuq4HrkUvp1uyPxYXEu", "13UtpxQJuoTigEeeuq4HrkUvp1uyPxYXEu", "18YLC2tD1yGEdaNitXuaueyFPJ9yeJRwgL", "1CM64CTCd5FCCX7kb29g3mghM8GkT5c6UX", "1P21GkkUFt8ME2U65SbGwutP9EZcuQoFs", "14WMdsXApZjz1ybye79nA3JJf6h2R5T2SH", "1CVUSDGjd3jWrmGU7Vb2VzN3z6eMaxZ5dn", "1M6pdtoTPe36pXtChNZvH4c8mSB6vzSJHJ", "1Du2DiSoiZKEK22jPHWvRkyAeJH7KWFckQ", "16LUs7scDuJvGk4A4LaR24DwHiXuwsZuVf", "1DHKTB55ZPEQ4Ng1rxwe39i5jiF5ebmYHf", "1KDm91GSEjob7PyVF5GUPmSiMg4KMSAXsY", "1ACajk8H6fknesYhJVSVw62faDQsUfxzSC", "1JW3wHLApAgwpFTYg5KTMc8MJe4b2E1vtd", "1D7w471ZDkbtp8p8QZoMGduzJ26V9u2Jdm", "1GtTeRLhsvyFjS65Jcs3fL1R3Myhxi6S2o", "1MsaU3qFkz84Lid6RfiACSf5qqLbDgdDwz", "1FBSjuR2vFqCuSW5QbfSG6uz3T7RVEwhGB", "1LmJ7z3WezMRWyKx1ZHYdTgg9cX23bHNHJ", "1GYvGYrUqLGzQoErLhsegPw3nygpNFwZR9", "1GYvGYrUqLGzQoErLhsegPw3nygpNFwZR9", "1HsFYGTJddaCspLWCmxydg8vWpeRPMrEmF", "1HsFYGTJddaCspLWCmxydg8vWpeRPMrEmF", "1B8vxvHfc9whEkhKyUoE9fMVNUbAaKehfU", "17hz5ebzoig5jqvNbQbLeQ5y23TNW5i8sn", "1HXC9ra6x5VZy4GRSCNSaDSWw3aNjzidwi", "1CDz6WgBbNNXBhbtsRCLBXHvnzZdF4YMeJ", "14G3vYt7hSHkpf8e8A8c3nTvbegTSv7GzP", "19MxTmoksJEj9CgzifZKcy145Q6sDpMo7t", "1HqxgWn4yc76auxd5GTAfP1ShzHmYHg6RA", "1EccR9M3dfsVTPxvrEygFWoxyLRt6r4tnZ", "186MNGTmgdoesFNhCvQAts43K9ys13DjRK", "1CEagPKG8WL6oX5baJ16uWacD5kQRCCQvk", "19AKeSTDAWT4VB9WUAK3aiw5KSfLKooTKy", "1EY7UYP49CdEwVWExmekAKGUwGPppepV1R", "1Nwg73ThKbEtTJMxL8Fmp3nXPoB6F2sANs", "1HoYtFhQbYnx9UY1dfWfoM3gksuEP8397m", "1JHyxrRhvhpKmDvkcVL6m6v4pZs7iE2N87", "14erK2S3EDZ1YnKnkRJqiTopfkvvCQRXPD", "1NJJs32wnaA6NAjhTGPg1KyKv5R3PrEw1Z", "1J15wnNEE44Nn7yVH6hc5gVc4EZn6ejghM", "1HP81SXjHkhYoEAxuVWmJXVpxffwZWMZs5", "1EPwygdMJXY7wAQUMhQuBZGkXA5h47R3dn", "13bgUr8JugfD2rb5PjYe9CzTPTGPNdBaTT", "18eiw3eMbW7iLDvwrpyJoVRA9drL2DQeHa", "1PCNXireXa6mZGub7vJ8jRCMnKj5D3sVtt", "1GC2SoDHDUzctaKGGn9mhhBu6j4qaWCHFN", "1FuMDoQh7fj7NTm1p6zqhgfpTXBRVsXm54", "1BgLudnKgbYGuJkvyDLzS3qgNaeJ51ngoQ", "1PuntP7sziEBVfDHWsPXiDEN2ZQo1YibwD", "1J8kXzvDLHuQQTTNt7bNmMKUz25RPJC8wz", "17q6DdCN4wnURrsBVduMjkpAGygmiwaoHg", "2-1P7JAqMWndrh7r96mUtgtHJpi3DDjB6W6Q-1M9sfm4KVEBQtCBFvesq2GuCJGeBTfNWYP-1JwefaNfiTd4WzKnXirdMTmzp3eaoo341A", "2-1MgJG1Lan1f5gz9S1MZFfL7AmkKjCyDjcP-1KjUMwz1GAXRzzQMEjUEJEbqsHR5TkQjDV-1PFZvwoszumZi6cYqYjJxAJbY7nRhP6q8H", "2-1HTTuVyKtcDcQJtFUykU3tWSJUu3Z89RVy-16XsaUzxBqXRyEScspkvNhgtmKZgohSjWf-128iuf7pgWu1y4RUj7VttEVHpU5uoASPmW", "2-1J6YrmvRZMvw7idk4JhLChhxs4aBSFX3ni-1L4EoMA6BPeVAvedykpDZNUmQLK6JQGefs-14HnMuS3tqXBtoejZg9pusiMzrZR2Jnezv", "2-149YJRmvksFwxhVsdBkWM4yhgrwzTSzkhD-17S81r4HEYhxx6jCYwbUj72F5SwhdUpvSA-1M8nxGnxwkurzkfq6GTLb1cPf8PPasrpzm", "2-12MBcoBnjxdVBpceCaPCdnq1Y3p2U4kY2L-17KTsyEggmpMUjHJBNfjE7WraHaMBCrmVd-1MQyNFQCDo8wm9WksryTmWc41GW1xHppUG", "2-19QKxdx5aq9L8Z7nBPrBhQCQLP8tD4SyQQ-1LFmQMM76N7N2GvDqmCLAMCfH7GvgLP8dL-12NStY7wC9cFzUurSYd7ffShCY4stzecwr", "2-16n4kKvWVqJhwQY4dMdt9M4uVcxdajpLFr-1PFU1NvhqSXwgw3RMBWHgvFyqgPgkZbnkP-15q28uDQk7NNKn7hLWW39w8UHryqPt1gM7", "2-1F8kmFiJkFqtbAhkUEDdwipitGNZULGEec-1L6N25HHcUupvb2d89xcrAnsWeuFbBS3k7-14xVGvBQWgDDE8Lr4HBAepA7VHZWsXzJWh", "2-1EdBSiNBmP6TkJt4EoCi4vPf9MMoWQACXN-15LicSLzBPAAeUznRwLXRNdyGGidAqbENJ-18NmmFBVDF28meU9MFtLP4D3GVjThYH8HA", "2-1DNxCpKHDBHYzmmasV1gC3166ndbLR5tUX-16bvdPbFynNXWWyKXuFnm9KrcziKVXxFhd-1Fi14hM1XP4noNwjHHjJF4FgWF7r1kNdau", "2-1FbmCy3vduzEYjyBmYsLyUtVG9YUiuhbvB-1aq8giKn7Fx6DvU1wr3egymmnr6u2CKWW-1374o987v7F52Rvxtu64xZRnLFmfmNK4dY", "2-13pLi61HGaK13ToqR4nfBDBSq8PuCg4XiX-1MyVGJUTdfE6S2na5ZAmWAbimFWzmMBu1X-1KScv7hKYeBT6FsUaAy2mtsUFrf163vz6x", "2-1MpUNJTgD1Ej8cQBz2d9EiaKmAWWaLGvcc-1Gb179YipBzUMRnUgzEjLR4dAaZWzHnwZw-1NbpXbBLDEwi8QN26cA3zY9nLFuxqfCPjN", "2-13vY9JGHRae2z4s3wKWtCBdbS7q3qwhguJ-1FSto1iU7P4rbWFejPHzUT7Fkyr2YNSQFS-1PdNdK4YuAcPsz49iFhyMuWmW76qtSzkkZ", "2-1E9TZGqarDJgFDrj3amLhTcgEqSJuYGjfh-12mA33A8uh5HfKKrQtVGMDMwRapcmR5XpG-1DC5JMZzh8KNSjgQgGEwRCjN6FbVe8x27o", "2-1CPjYDawNh8TDmWi3be5fz1oLT7M9z6N21-134Tft53vyYLAEUeo85JA7zhVcck1o63oc-1AmbWf4kXhZR7FrRjKodA6TDKScrvbfJdh", "2-18sKBw7LDtVDCF9qW6fgL5Cix6t8EJbazL-1ZUK8tvVP5MgYxA9K81ryLwSG57xpbsdh-1PywAgQx2PrywqaWqEwDZ4ZyE4akqsEG77", "2-17CXevWLZVTooW5H1kCLJkv7tiXZFfkbom-1DFhbHytfbWUpKiy3hoaCatD59oRg7djn5-1J442e3ijRbKEH4VZWxVm4QstYBVBCr1va", "2-1HPRacvTUSPXzFB1xuYkAftZ9R9kiLz3y9-1PxGBYYjjc5aKVe38s9JEwqHPinGVntyn2-1Mh1bm4MJA9LFreL5fLynKR4GEaEns4xGU", "2-1HPRacvTUSPXzFB1xuYkAftZ9R9kiLz3y9-1PxGBYYjjc5aKVe38s9JEwqHPinGVntyn2-1Mh1bm4MJA9LFreL5fLynKR4GEaEns4xGU", "2-1AJxRNCFEgXQYjEKQNuFFkRbXtETg63KEb-14E9Fyoyt4a1USHPXhsBKAUrbEkYwyN4bt-18D6iK5n6H9jetHKJBReWaKT2NdG6Y9XD9"};
	public static Long[] amounts = {1419299300000L,400000000000L, 9400000000L, 400000000000L, 4160000000000L, 800000000000L, 37500000000L, 800000000000L, 159900000000L, 2400000000000L, 40000000000L, 8800000000L, 12000000000L, 79900000000L, 13900000000L, 479900000000L, 114500000000L, 240000000000L, 128000000000L, 816000000000L, 100000000000L, 200000000000L, 105300000000L, 8200000000L, 105000000000L, 800000000000L, 880000000000L, 799900000000L, 15200000000L, 800000000000L, 233500000000L, 8000000000L, 360000000000L, 8000000000L, 56000000000L, 80000000000L, 8000000000L, 808000000000L, 8000000000L, 410400000000L, 8000000000L, 16000000000L, 8000000000L, 4000000000000L, 360000000000L, 11200000000L, 91500000000L, 400000000000L, 400100000000L, 800000000000L, 1005200000000L, 84300000000L, 59000000000L, 400300000000L, 400000000000L, 8000000000L, 2400000000000L, 120000000000L, 200000000000L, 46300000000L, 7900000000L, 119300000000L, 13800000000L, 2397900000000L, 92900000000L, 101300000000L, 39800000000L, 816200000000L, 198000000000L, 79500000000L, 119300000000L, 40000000000L, 1191400000000L, 39700000000L, 119000000000L, 39600000000L, 333100000000L, 39500000000L, 98800000000L, 7900000000L, 157600000000L, 2165900000000L, 25400000000L, 53700000000L, 254900000000L, 7800000000L, 78200000000L, 2335500000000L, 224900000000L, 27300000000L, 54400000000L, 1552200000000L, 77900000000L, 24500000000L, 7700000000L, 34800000000L, 77400000000L, 580400000000L, 773800000000L, 15400000000L, 77300000000L, 793400000000L, 103300000000L, 115900000000L, 77000000000L, 30800000000L, 782100000000L, 76900000000L, 230500000000L, 76800000000L, 8500000000L, 971800000000L, 478500000000L, 8700000000L, 579900000000L, 114500000000L, 76300000000L, 210500000000L, 38000000000L, 761400000000L, 76100000000L, 163200000000L, 26400000000L, 37900000000L, 15100000000L, 227500000000L, 378600000000L, 302800000000L, 75700000000L, 90700000000L, 378000000000L, 755400000000L, 566600000000L, 135800000000L, 75400000000L, 90500000000L, 112900000000L, 188400000000L, 7500000000L, 371100000000L, 97900000000L, 376500000000L, 75200000000L, 752400000000L, 75200000000L, 105200000000L, 226300000000L, 112600000000L, 375400000000L, 41300000000L, 75000000000L, 299900000000L, 749500000000L, 104900000000L, 599300000000L, 187100000000L, 187100000000L, 172000000000L, 66100000000L, 2240600000000L, 373100000000L, 372300000000L, 4848400000000L, 48400000000L, 745700000000L, 37200000000L, 37200000000L, 7400000000L, 14800000000L, 44600000000L, 594400000000L, 5194500000000L, 11100000000L, 3708700000000L, 25900000000L, 11000000000L, 369300000000L, 369300000000L, 1477200000000L, 151400000000L, 4679000000000L, 738200000000L, 1464600000000L, 2921600000000L, 950900000000L, 107900000000L, 80000000000L, 109500000000L, 363500000000L, 7500000000L, 7200000000L, 7200000000L, 72500000000L, 290100000000L, 7900000000L, 739600000000L, 1087200000000L, 43400000000L, 18000000000L, 361500000000L, 361200000000L, 270400000000L, 359200000000L, 7100000000L, 732300000000L, 7100000000L, 713700000000L, 9100000000L, 142700000000L, 355700000000L, 71000000000L, 212600000000L, 708700000000L, 156100000000L, 2121100000000L, 141300000000L, 282600000000L, 777000000000L, 346100000000L, 352200000000L, 250400000000L, 165100000000L, 1548200000000L, 360000000000L, 35000000000L, 32000000000L, 1951000000000L, 1100900000000L, 34800000000L, 139300000000L, 694300000000L, 4501500000000L, 813400000000L, 3461800000000L, 17200000000L, 69100000000L, 694000000000L, 2418100000000L, 69000000000L, 144100000000L, 689800000000L, 171700000000L, 34700000000L, 688300000000L, 1375000000000L, 1374200000000L, 68700000000L, 124600000000L, 686700000000L, 343200000000L, 3520500000000L, 349600000000L, 91500000000L, 73700000000L, 27300000000L, 303000000000L, 340300000000L, 680400000000L, 285500000000L, 693900000000L, 679500000000L, 67800000000L, 33800000000L, 610600000000L, 95000000000L, 678400000000L, 170400000000L, 88000000000L, 249300000000L, 291200000000L, 2029000000000L, 168900000000L, 67500000000L, 687500000000L, 107700000000L, 20000000000L, 1399500000000L, 133800000000L, 187000000000L, 20000000000L, 19900000000L, 13200000000L, 165500000000L, 331700000000L, 1326100000000L, 223800000000L, 295800000000L, 3615700000000L, 6040400000000L, 162800000000L, 1951200000000L, 162400000000L, 1622800000000L, 649000000000L, 318000000000L, 160400000000L, 702500000000L, 641600000000L, 643900000000L, 1274000000000L, 94500000000L, 944000000000L, 3460200000000L, 1934800000000L, 49800000000L, 2738700000000L, 155400000000L, 155200000000L, 12300000000L, 2700300000000L, 61200000000L, 91800000000L, 15100000000L, 1507400000000L, 1641100000000L, 2632100000000L, 86500000000L, 90000000000L, 223100000000L, 1199700000000L, 1361400000000L, 1355700000000L, 896100000000L, 1541400000000L, 889200000000L, 1778500000000L, 596400000000L, 3522000000000L, 2344100000000L, 292500000000L, 11600000000L, 5812500000000L, 5700000000L, 1680800000000L, 28900000000L, 5700000000L, 577600000000L, 319500000000L, 273700000000L, 114700000000L, 1433800000000L, 8596100000000L, 149100000000L, 1140500000000L, 2835300000000L, 1133800000000L, 1132800000000L, 14100000000L, 569700000000L, 93900000000L, 56200000000L, 56000000000L, 1928000000000L, 11700000000L, 1886900000000L, 2272000000000L, 111000000000L, 24700000000L, 492600000000L, 110400000000L, 62900000000L, 1097500000000L, 1096800000000L, 5500000000L, 272400000000L, 5400000000L, 108600000000L, 537800000000L, 359300000000L, 3420500000000L, 5200000000L, 26100000000L, 260200000000L, 103000000000L, 5800000000L, 51100000000L, 5139500000000L, 252500000000L, 5050100000000L, 25200000000L, 89400000000L, 17100000000L, 9900000000L, 49100000000L, 148100000000L, 9200000000L, 135500000000L, 48200000000L, 8000000000L, 47900000000L, 863800000000L, 9500000000L, 206100000000L, 4779600000000L, 955600000000L, 383100000000L, 23200000000L, 1618600000000L, 1657300000000L, 102100000000L, 235600000000L, 225100000000L, 4900000000L, 440700000000L, 102200000000L, 310500000000L, 109200000000L, 5709900000000L, 2183100000000L, 1309300000000L, 871200000000L, 4350700000000L, 2471000000000L, 216600000000L, 42200000000L, 10376600000000L, 929400000000L, 2943700000000L, 3711100000000L, 1281900000000L, 7789300000000L, 21200000000L, 21200000000L, 855500000000L, 787200000000L, 169900000000L, 20700000000L, 842500000000L, 63100000000L, 230400000000L, 2093900000000L, 627800000000L, 125300000000L, 104300000000L, 8859100000000L, 1117400000000L, 103400000000L, 413600000000L, 20500000000L, 82300000000L, 43800000000L, 123600000000L, 15656800000000L, 190300000000L, 86500000000L, 198200000000L, 3211900000000L, 1160800000000L, 447600000000L, 813100000000L, 60900000000L, 2438400000000L, 2723300000000L, 405400000000L, 404900000000L, 396700000000L, 12900000000L, 43500000000L, 262100000000L, 402800000000L, 603900000000L, 1368200000000L, 1253100000000L, 201200000000L, 401900000000L, 2004700000000L, 20000000000L, 584200000000L, 700000000000L, 345500000000L, 1381400000000L, 1196000000000L, 44800000000L, 10000000000L, 160100000000L, 32000000000L, 800000000000L, 800000000000L, 89600000000L, 7700000000L, 7700000000L, 26600000000L, 186100000000L, 15400000000L, 283800000000L, 15300000000L, 455600000000L, 63700000000L, 356700000000L, 14000000000L, 148600000000L, 505900000000L, 35400000000L, 18800000000L, 8650300000000L, 12818200000000L, 9569600000000L};

	
	public static boolean hasAddress(String targetValue) {
		for(String s: listOfAddresses){
			if(s.equals(targetValue))
				return true;
		}
		return false;
	}
	
	public static Long getClaimableAmount(String targetValue) {
		int cntr = 0;
		for(String s: listOfAddresses){
			if(s.equals(targetValue))
				return amounts[cntr];
			cntr += 1;
		}
		return 0L;
	}
	
	private static final DbKey.LongKeyFactory<Redeem> redeemKeyFactory = new DbKey.LongKeyFactory<Redeem>("id") {
		@Override
		public DbKey newKey(Redeem prunableSourceCode) {
			return prunableSourceCode.dbKey;
		}
	};

	private static final PrunableDbTable<Redeem> redeemTable = new PrunableDbTable<Redeem>("redeems",
			redeemKeyFactory) {
		@Override
		protected Redeem load(Connection con, ResultSet rs, DbKey dbKey) throws SQLException {
			return new Redeem(rs, dbKey);
		}

		@Override
		protected void save(Connection con, Redeem prunableSourceCode) throws SQLException {
			prunableSourceCode.save(con);
		}

		@Override
		protected String defaultSort() {
			return " ORDER BY block_timestamp DESC, db_id DESC ";
		}
	};

	public static int getCount() {
		return redeemTable.getCount();
	}

	public static DbIterator<Redeem> getAll(int from, int to) {
		return redeemTable.getAll(from, to);
	}

	static void init() {
	}

	private final long id;
	private final DbKey dbKey;
	private String address;
	private String secp_signatures;
	private long receiver_id;
	private long amount;
	private final int transactionTimestamp;
	private final int blockTimestamp;
	private final int height;

	private Redeem(Transaction transaction, int blockTimestamp, int height) {
		this.id = transaction.getId();
		this.dbKey = redeemKeyFactory.newKey(this.id);
		this.blockTimestamp = blockTimestamp;
		this.height = height;
		this.transactionTimestamp = transaction.getTimestamp();

		Attachment.RedeemAttachment r = (Attachment.RedeemAttachment) transaction.getAttachment();
		this.address = r.getAddress();
		this.receiver_id = transaction.getRecipientId();
		this.secp_signatures = r.getSecp_signatures();
		this.amount = transaction.getAmountNQT();
	}

	private void update(Transaction tx) {
		Attachment.RedeemAttachment r = (Attachment.RedeemAttachment) tx.getAttachment();
		this.address = r.getAddress();
		this.receiver_id = tx.getRecipientId();
		this.secp_signatures = r.getSecp_signatures();
		this.amount = tx.getAmountNQT();
	}

	private Redeem(ResultSet rs, DbKey dbKey) throws SQLException {
		this.id = rs.getLong("id");
		this.dbKey = dbKey;
		this.address = rs.getString("address");
		this.secp_signatures = rs.getString("secp_signatures");
		this.receiver_id = rs.getLong("receiver_id");
		this.blockTimestamp = rs.getInt("block_timestamp");
		this.transactionTimestamp = rs.getInt("timestamp");
		this.height = rs.getInt("height");
		this.amount = rs.getLong("amount");
	}

	private void save(Connection con) throws SQLException {

		try (PreparedStatement pstmt = con.prepareStatement(
				"MERGE INTO redeems (id, address, secp_signatures, receiver_id, amount, block_timestamp, timestamp, height) "
						+ "KEY (id) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
			int i = 0;
			pstmt.setLong(++i, this.id);
			pstmt.setString(++i, this.address);
			pstmt.setString(++i, this.secp_signatures);
			pstmt.setLong(++i, this.receiver_id);
			pstmt.setLong(++i, this.amount);
			pstmt.setInt(++i, this.blockTimestamp);
			pstmt.setInt(++i, this.transactionTimestamp);
			pstmt.setInt(++i, this.height);
			pstmt.executeUpdate();
		}
	}

	public long getId() {
		return id;
	}

	public int getTransactionTimestamp() {
		return transactionTimestamp;
	}

	public int getBlockTimestamp() {
		return blockTimestamp;
	}

	public int getHeight() {
		return height;
	}

	static void add(TransactionImpl transaction) {
		add(transaction, Nxt.getBlockchain().getLastBlockTimestamp(), Nxt.getBlockchain().getHeight());
	}

	static void add(TransactionImpl transaction, int blockTimestamp, int height) {

		boolean was_fresh = false;

		Redeem prunableSourceCode = redeemTable.get(transaction.getDbKey());
		if (prunableSourceCode == null) {
			was_fresh = true;
			prunableSourceCode = new Redeem(transaction, blockTimestamp, height);
		} else if (prunableSourceCode.height != height) {
			throw new RuntimeException("Attempt to modify prunable source code from height " + prunableSourceCode.height
					+ " at height " + height);
		}
		prunableSourceCode.update(transaction);
		redeemTable.insert(prunableSourceCode);

		// Credit the redeemer account
		AccountLedger.LedgerEvent event = AccountLedger.LedgerEvent.REDEEM_PAYMENT;
		Account participantAccount = Account.addOrGetAccount(prunableSourceCode.receiver_id);
		if (participantAccount == null) { // should never happen
			participantAccount = Account.getAccount(Genesis.FUCKED_TX_ID);
		}
		
	}

	public static boolean isAlreadyRedeemed(String address) {
		try (Connection con = Db.db.getConnection();
				PreparedStatement pstmt = con.prepareStatement("SELECT receiver_id FROM redeems WHERE address = ?")) {
			pstmt.setString(1, address);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

}
