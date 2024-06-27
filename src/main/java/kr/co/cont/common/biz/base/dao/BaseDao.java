package kr.co.cont.common.biz.base.dao;

import java.util.List;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.cont.common.biz.base.model.BaseMap;

public class BaseDao {

	@Autowired
	private SqlSession sqlSession;
	
	@Autowired
	private SqlSession sqlSessionBatch;
	
	/**
	 * DataBase 에서 목록을 조회한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected List<BaseMap> selectList(String queryId, Object parameter) {
		return sqlSession.selectList(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 단건을 조회한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected Object selectOne(String queryId, Object parameter) {
		return sqlSession.selectOne(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 단건을 조회한다.
	 * 
	 * @param queryId
	 * @return
	 */
	protected Object selectOne(String queryId) {
		return sqlSession.selectOne(queryId);
	}
	
	/**
	 * DataBase 에서 단건을 조회한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected BaseMap select(String queryId, Object parameter) {
		return sqlSession.selectOne(queryId, parameter);
	}	
	
	/**
	 * DataBase 에서 단건을 조회한다.
	 * 
	 * @param queryId
	 * @return
	 */
	protected BaseMap select(String queryId) {
		return sqlSession.selectOne(queryId);
	}
	
	/**
	 * DataBase 에서 MERGE 문을 실행한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected int merge(String queryId, Object parameter) {
		return sqlSession.insert(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 INSERT 문을 실행한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected int insert(String queryId, Object parameter) {
		return sqlSession.insert(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 UPDATE 문을 실행한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected int update(String queryId, Object parameter) {
		return sqlSession.update(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 DELETE 문을 실행한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected int delete(String queryId, Object parameter) {
		return sqlSession.delete(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 대용량 데이터를  조회한다.
	 * @param <T>
	 * @param queryId
	 * @param parameter
	 * @param handler
	 */
	protected <T> void selectBatch(String queryId, Object parameter, ResultHandler<T> handler) {
		sqlSessionBatch.select(queryId, sqlSession, handler);
	}
	
	/**
	 * DataBase 에서 BATCH 로 MERGE 문을 실행한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected int mergeBatch(String queryId, Object parameter) {
		return sqlSessionBatch.insert(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 BATCH 로 INSERT 문을 실행한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected int insertBatch(String queryId, Object parameter) {
		return sqlSessionBatch.insert(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 BATCH 로 UPDATE 문을 실행한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected int updateBatch(String queryId, Object parameter) {
		return sqlSessionBatch.update(queryId, parameter);
	}
	
	/**
	 * DataBase 에서 BATCH 로 DELETE 문을 실행한다.
	 * 
	 * @param queryId
	 * @param parameter
	 * @return
	 */
	protected int deleteBatch(String queryId, Object parameter) {
		return sqlSessionBatch.delete(queryId, parameter);
	}
	
}
