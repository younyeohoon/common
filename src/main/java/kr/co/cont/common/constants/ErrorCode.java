/**
 * 
 */
package kr.co.cont.common.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
	
	// 작서예시
	OK(200, "", ""), // 클라이언트의 요청을 서버가 정상적으로 처리했다.
	CREATED(201, "", ""), // 클라이언트의 요청을 서버가 정상적으로 처리했고 새로운 리소스가 생겼다.
	ACCEPTED(202, "", ""), // 클라이언트의 요청은 정상적이나, 서버가 요청을 완료하지 못했다.
	NO_CONTENT(204, "", ""), // 클라이언트의 요청은 정상적이다. 하지만 컨텐츠를 제공하지 않는다.(대표적인 예(200, "", ""), // 데이터 삭제 성공, 데이터 수정 성공)

	BAD_REQUEST(400, "ERR_0400", "올바르지 않은 접근입니다."), // 클라이언트의 요청이 유효하지 않아 더 이상 작업을 진행하지 않는 경우
	UNAUTHORIZED(401, "", ""), // 클라이언트가 인증이 되지 않아 작업을 진행할 수 없는 경우
	FORBIDDEN(403, "", ""), // 클라이언트가 인증은 됐으나 권한이 없기 때문에 작업을 진행할 수 없는 경우
	NOT_FOUND(404, "ERR_0404", "지원하지 않는 URL 입니다."), // 클라이언트가 요청한 자원이 존재하지 않다.
	METHOD_NOT_ALLOWED(405, "CM001", "해당기능을 이용할 수 업습니다."), // 클라이언트의 요청이 허용되지 않는 메소드인 경우
	CONFLICT(409, "", ""), // 클라이언트의 요청이 서버의 상태와 충돌이 발생한 경우
	TOO_MANY_REQUESTS(429, "", ""), // 클라이언트가 일정 시간 동안 너무 많은 요청을 보낸 경우

	INTERNAL_SERVER_ERROR(500, "", ""), // 서버에 에러가 발생하였다.
	NOT_IMPLEMENTED(502, "", ""), // 요청한 URI의 메소드에 대해 서버가 구현하고 있지 않다.
	SERVICE_UNAVAILABLE(503, "", ""), // 서버가 요청을 받을 준비가 되지 않은 경우	
	
	// Common
	REQUEST_SUCCESS(200, "0000", "성공하였습니다."),
	REQUEST_FAILURE(500, "ERR_9999", "시스템에서 오류가 발생하였습니다."),
	
	// validation
	INVALID_INPUT_VALUE(400, "ERR_0401", "{@}"), // 클라이언트의 요청이 유효하지 않아 더 이상 작업을 진행하지 않는 경우
	INVALID_OBJECT_NULL(400, "ERR_0402", " The object[{@}] to be validated must not be null."),
	INVALID_UNKWON_ERROR(500, "ERR_0599", "시스템에서 알 수 없는 오류 발생."), // 서버에 에러가 발생하였다.	
	
//	METHOD_NOT_ALLOWED(405, "ERR_0002", " Invalid Input Value"),
	HANDLE_ACCESS_DENIED(403, "ERR_0006", "Access is Denied"),
	
	// session 
	SESSION_NOT_EXSIT(400, "SES_0001", "세션이 존재하지 않습니다."),
	SESSION_INVALID_INPUT_VALUE(400, "SES_0001", "Invalid Input Value."),

	// DB
	DB_REGISTER_SUCCESS(201, "0000", "정상적으로 등록하였습니다."),
	DB_MODIFY_SUCCESS(204, "0000", "정상적으로 수정하였습니다."),
	DB_REMOVE_SUCCESS(204, "0000", "정상적으로 삭제하였습니다."),
	DB_SEARCH_SUCCESS(204, "0000", "정상적으로 조회하였습니다."),
	
	
	DB_REGISTER_FAILURE(500, "DBM_0001", "등록에 실패하였습니다."),
	DB_MODIFY_FAILURE(500, "DBM_0002", "수정에 실패하였습니다."),
	DB_REMOVE_FAILURE(500, "DBM_0003", "삭제에 실패하였습니다."),
	
	DB_SEARCH_NOT_FOUND(404, "DBM_M004", "검색결과가 존재하지 않습니다."),
	DB_SEARCH_NO_MORE(400, "DBM_M005", "검색결과가 더 이상 존재하지 않습니다."),
	
	// Member
	MEMBER_EMAIL_DUPLICATION(400, "MEM_0001", "Email is Duplication"),
	MEMBER_NOT_FOUND(400, "MEM_0003", "사용자 정보가 일치하지 않습니다."),
	MEMBER_ID_DUPLICATION(400, "MEM_0004", "이미 사용되고 있는 ID입니다."),
	MEMBER_PWD_MAX_COUNT(300, "MEM_0005", "비밀번호 오류 회수를 초과하였습니다."),
	MEMBER_PWD_FAIL_COUNT(400, "MEM_0006", "비밀번호를 {@} 회 불일치 하였습니다."),

	// 로그인
	LOGIN_INPUT_INVALID(400, "LGN_0001", "Login input is invalid"),
	LOGIN_ACCESS_DENIED(403, "LGN_0002", "로그인이 필요한 서비스 입니다."),
	
	// 파일 읽기 쓰기 오류
	FILE_NOT_FOUND(404, "FLE_0001", "파일이 존재하지 않습니다."), // 클라이언트가 요청한 자원이 존재하지 않다.
	FILE_SERVER_ERROR(500, "FLE_0002", "파일 작업중에 오류가 발생하였습니다."), // 서버에 에러가 발생하였다.
	FILE_UPLOAD_SIZE_ERROR(400, "FLE_0003", "업로드할 수 있는 파일의 최대 크기 초과 하였습니다."), // 서버에 에러가 발생하였다.
	FILE_UPLOAD_LIMIT_SIZE_ERROR(400, "FLE_0004", "업로드할 수 있는 총 파일의 최대 크기는 {@}MB 입니다."), // 서버에 에러가 발생하였다.
	FILE_UPLOAD_MAX_SIZE_ERROR(400, "FLE0_005", "업로드할 수 있는 개별 파일의 최대 크기 {@}MB 입니다."), // 서버에 에러가 발생하였다.
	
	
	// 통신오류
	HTTP_REQUEST_REDIRECT(300, "NET_0001", "리다이렉션 완료"), // 클라이언트의 요청이 유효하지 않아 더 이상 작업을 진행하지 않는 경우
	HTTP_BAD_REQUEST(400, "NET_0002", "요청 오류"), // 클라이언트의 요청이 유효하지 않아 더 이상 작업을 진행하지 않는 경우
	HTTP_INTERNAL_SERVER_ERROR(500, "NET_0003", "[서버 오류] {@}"), // 서버에 에러가 발생하였다.
	
	// 토큰
	TOKEN_BAD_REQUEST(400, "TKN_0001", "올바르지 않은 토근입니다."), // 클라이언트의 요청이 유효하지 않아 더 이상 작업을 진행하지 않는 경우
	TOKEN_UNAUTHORIZED(401, "TKN_0002", "유효하지 않은 토근입니다."), // 클라이언트가 인증이 되지 않아 작업을 진행할 수 없는 경우
	TOKEN_EXPIRED(401, "TKN_0003", "기간이 만료된 토근입니다."), // 클라이언트가 인증이 되지 않아 작업을 진행할 수 없는 경우
	
	//
	BIZ_APPROVE_SUCCESS(204, "0000", "정상적으로 승인처리 되었습니다."),
	BIZ_REJECT_SUCCESS(204, "0000", "정상적으로 반려처리 되었습였습니다."),
	
	BIZ_APPROVE_FAILURE(500, "BIZ_M001", "승인처리에 실패하였습니다."),
	BIZ_REJECT_FAILURE(500, "BIZ_M002", "반려처리에 실패하였습니다."),
	

	;

	private final String code;
	private final String message;
	private int status;

	ErrorCode(final int status, final String code, final String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}
}
