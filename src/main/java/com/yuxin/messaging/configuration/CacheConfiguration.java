package com.yuxin.messaging.configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.yuxin.messaging.dao.FriendInvitationDAO;
import com.yuxin.messaging.dao.GroupChatMemberDAO;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    @Autowired
    private FriendInvitationDAO friendInvitationDAO;

    @Autowired
    private GroupChatMemberDAO groupChatMemberDAO;
    @Bean
    @Qualifier("friendCache")
    public LoadingCache<Integer, List<Integer>> friendCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, List<Integer>>() {
                    @Override
                    public List<Integer> load(Integer userId) {
                        return friendInvitationDAO.listFriendIdsByUserId(userId);
                    }
                });
    }

    @Bean
    @Qualifier("userGroupChatCache")
    public LoadingCache<Integer, List<Integer>> userGroupChatCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, List<Integer>>() {
                    @Override
                    public List<Integer> load(Integer userId) {
                        return groupChatMemberDAO.listGroupChatIdsByUserId(userId);
                    }
                });
    }
}
