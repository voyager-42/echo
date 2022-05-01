package com.voyager.community.dao.elasticserch;

import com.voyager.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Voyager1
 * @create 2022-03-21 15:57
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
