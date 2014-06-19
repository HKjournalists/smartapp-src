/************************************************************************/
/* 
account.c Դ����
����: ba  [-b] [-r desc] BUSYBOX  PATH
ѡ��: -b ����
		 -r �ָ�
����: ����account,ֱ�ӿ���/data/system/accounts.db���ݿⱸ�ݣ�-b��-r����ͬʱָ��
*/
/************************************************************************/

#include <string.h>
#include <stdio.h>
#include <getopt.h>
#include <unistd.h>
#include <pwd.h>
#include <sys/stat.h>
#include "define.h"

#define ACCOUNT_BACKUP_ERROR "backup account error"
#define ACCOUNT_BACKUP_SUCCESS "backup account success"
#define ACCOUNT_RESTORE_ERROR "restore account error"
#define ACCOUNT_RESTORE_SUCCESS "restore account success"
#define ACCOUNT_DB_PATH "ACCOUNT_DB_PATH"

#define DEFAULT_ACCOUNT_DB_PATH "/data/system/accounts.db"
#define ACCOUNT_DB_FILE_NOT_EXIST "account db file not exist"
#define ACCOUNT_PATH_NOT_EQUAE "account path not equal"

int account_main(int argc, char **argv){
	int backup_account = 0, restore_account = 0;
	char* busybox=NULL;
	char* desc = NULL;
	char* path=NULL;
	int arg_error = 0;
	int c;

	while((c = getopt(argc, argv, "br:")) != EOF){
		switch(c){
		case 'b':
			backup_account = 1;
			break;
		case 'r':
			restore_account = 1;
			desc = optarg;
			DLOG_INT(DEBUG_FALG, desc);
			break;
		default:
			arg_error = 1;
			break;;
		}
	};
	
	if(argc - optind != 2){
		arg_error = 1;
		DLOG_INT(DEBUG_FALG, argc);
		DLOG_INT(DEBUG_FALG, optind);
	}

	if(restore_account && desc == NULL){
		arg_error = 1;
	}

	if(backup_account && restore_account){
		arg_error = 1;
	}

	if(arg_error == 1){
		fprintf(stderr, "%s\n%s\n", ERROR_ARGUMENT_INVALID, ACCOUNT_BACKUP_ERROR);
		return 1;
	}

	busybox = argv[optind];
	path = argv[optind+1];
	DLOG_MSG(DEBUG_FALG, busybox);
	DLOG_MSG(DEBUG_FALG, desc);
	if(busybox == NULL || path == NULL){
		fprintf(stderr, "%s\n%s\n", ERROR_ARGUMENT_INVALID, ACCOUNT_BACKUP_ERROR);
		return 1;
	}

	if(backup_account){
		if(backupAccount(busybox, path)){
			return 1;
		}
	}

	if(restore_account){
		if(restoreAccount(busybox, path, desc)){
			return 1;
		}
	}

	return 0;
}

int backupAccount(const char* busybox, const char* desc){
	char* account_db_path = NULL;
	char buffer[1024];
	
	if(!isFileExist(busybox, DEFAULT_ACCOUNT_DB_PATH)){
		//�ļ������ڣ�����ʧ��
		fprintf(stderr, "%s\n%s, %s\n", ACCOUNT_DB_FILE_NOT_EXIST, ACCOUNT_BACKUP_ERROR, GO_FAILURE);
		return 1;
	}

	sprintf(buffer, "%s cp -f %s %s 1>%s 2>%s", busybox, DEFAULT_ACCOUNT_DB_PATH, desc, STDOUT_LOG, STDERR_LOG);
	DLOG_MSG(DEBUG_FALG, buffer);
	system(buffer);

	if(checkFileNotExitError() || !checkCmdResult()){
		fprintf(stderr, "%s,%s\n", ACCOUNT_BACKUP_ERROR, GO_FAILURE);
		return 1;
	}

	fprintf(stdout, "%s=%s\n%s, %s\n", ACCOUNT_DB_PATH, DEFAULT_ACCOUNT_DB_PATH, ACCOUNT_BACKUP_SUCCESS, GO_SUCCESS);
	return 0;
}

int restoreAccount(const char* busybox, const char* src, const char* desc){
	int account_gid = 0;
	int account_uid = 0;
	struct stat statBuffer;
	char buffer[1024];

	//�жϱ��ݵ��ʺ����ݿ���Ĭ�ϵ��Ƿ���ͬ���������ͬ����ָ�ʧ��
	if(strcmp(desc, DEFAULT_ACCOUNT_DB_PATH)){
		fprintf(stderr, "%s\n%s, %s\n", ACCOUNT_PATH_NOT_EQUAE, ACCOUNT_RESTORE_ERROR, GO_FAILURE);
		return 1;
	}

	//��ȡ���ݿ�������Ժ��û����ԣ����ʧ�ܣ���ʹ��Ĭ��ֵ1000(system)
	if(isFileExist(busybox, DEFAULT_ACCOUNT_DB_PATH)){
		if(!statDataDirectoryAttr(DEFAULT_ACCOUNT_DB_PATH, &statBuffer)){
			account_gid = statBuffer.st_gid;
			account_uid = statBuffer.st_uid;
		}
	}
	if(account_gid == 0){
		account_gid = 1000;
	}
	if(account_uid == 0){
		account_uid = 1000;
	}

	//cp���ݿ�
	sprintf(buffer, "%s cp -f %s %s 1>%s 2>%s", busybox, src, DEFAULT_ACCOUNT_DB_PATH, STDOUT_LOG, STDERR_LOG);
	DLOG_MSG(DEBUG_FALG, buffer);
	system(buffer);

	if(checkFileNotExitError() || !checkCmdResult()){
		fprintf(stderr, "%s,%s\n", ACCOUNT_RESTORE_ERROR, GO_FAILURE);
		return 1;
	}

	//�ı����ݿ�����
	memset(buffer, 0, sizeof(buffer));
	sprintf(buffer, "%s chown -R %d:%d %s 1>%s 2>%s", busybox, account_gid, account_uid, DEFAULT_ACCOUNT_DB_PATH, STDOUT_LOG, STDERR_LOG);
	DLOG_MSG(DEBUG_FALG, buffer);
	system(buffer);
	if(checkFileNotExitError() || !checkCmdResult()){
		fprintf(stderr, "%s, %s\n", ACCOUNT_RESTORE_ERROR, GO_FAILURE);
		return 1;
	}

	//�ı��ļ���ִ��Ȩ��
	memset(buffer, 0, sizeof(buffer));
	sprintf(buffer, "%s chmod 660 %s 1>%s 2>%s", busybox, DEFAULT_ACCOUNT_DB_PATH, STDOUT_LOG, STDERR_LOG);
	DLOG_MSG(DEBUG_FALG, buffer);
	system(buffer);
	if(checkFileNotExitError() || !checkCmdResult()){
		fprintf(stderr, "%s, %s\n", ACCOUNT_RESTORE_ERROR, GO_FAILURE);
		return 1;
	}

	fprintf(stdout, "%s, %s\n", ACCOUNT_RESTORE_SUCCESS, GO_SUCCESS);
	return 0;
}