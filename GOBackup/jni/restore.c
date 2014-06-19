/************************************************************************/
/* 
restore.c ����Դ�ļ�
����: ra   [-i] [-l] [-a apk] [-d path] [-i path] [-D LD_LIBARY_PATH] BUSYBOX
ѡ�-d �ָ����� pathָ������·��
		  -i ָ��apk��װ�������·��
		  -a ָ���ָ�apk pathָ��apk�ļ�·��
		  -D ָ��LD_LIBARY_PATH����ʽΪ��LD_LIBARY_PATH=��
		  -l ָ����FORWARD_LOCK��ʽ��װapk
����: �ָ�apk�������ݡ�ָ���ָ�����ʱ����Ҫͬʱָ��-d -iѡ������android4.0�����Լ����ϣ���Ҫָ��-Dѡ��, -lѡ��ָ����FORWARD_LOCK��ʽ��װapk
*/
/************************************************************************/

#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <unistd.h>
#include <getopt.h>
#include <pwd.h>
#include "define.h"

#define DEBUG_FALG_RESTORE 0
#define DBUG_FLAG_RESTORE_APP 1

#define RESTORE_APK_ERROR "restore apk error" //�ָ�apkʧ��
#define RESTORE_APK_SUCCESS "restore apk success" //�ָ�apk�ɹ�
#define RESTORE_DATA_ERROR "restore data error"  //�ָ�����ʧ��
#define RESTORE_DATA_SUCCESS "restore data success" //�ָ����ݳɹ�
#define RESTORE_SUCCESS "restore success"  //�ָ��ɹ�
#define RESTORE_FAILURE "restore failure" //�ָ�ʧ��
#define UNRESTORE_DATA "not restore data" 
#define ERROR_RESTORE_GET_UUID "restore_get_uuid_error"

int restore_app_main(int argc, char** argv){
	int forward_locked = 0, specified_ld_library_path = 0, restore_apk=0, restore_data = 0, arg_error = 0;
	char buffer[1024];
	char* dataPath = NULL;
	char* internalFilePath = NULL;
	char* ld_library_path = NULL;
	char* busygoxPath = NULL;
	char* apkPath = NULL;
	int c;


	struct option longopts[] = {
		{"data", 1, 0, 'd'},
		{"innerpath", 1, 0, 'i'},
		{"librarypath", 1, 0, 'D'},
		{"apk", 1, 0, 'a'},
		{"lock", 0, 0, 'l'},
		{ 0, 0, 0, 0}
	};

	opterr = 0;
	while ((c = getopt_long (argc, argv, "d:i:D:a:l", longopts, NULL)) != EOF){
		switch(c){
		case 'd':
			restore_data = 1;
			dataPath = optarg;
			DLOG_MSG(DEBUG_FALG_RESTORE, dataPath);
			break;
		case 'i':
			internalFilePath = optarg;
			break;
		case 'D':
			specified_ld_library_path = 1;
			ld_library_path = optarg;
			DLOG_MSG(DEBUG_FALG_RESTORE, ld_library_path);
			break;
		case 'a':
			restore_apk = 1;
			apkPath = optarg;
			break;
		case 'l':
			forward_locked = 1;
			break;
		case ':':
			arg_error = 1;
			break;
		case '?':
			arg_error = 1;
			break;
		case -1:
			break;
		default:
			break;
		}
	}//end of while

	if(argc - optind != 1){
		arg_error = 1;
	}else{
		busygoxPath = argv[optind];
		DLOG_MSG(DEBUG_FALG_RESTORE, busygoxPath);
	}

	if(restore_data == 1 && (dataPath == NULL || internalFilePath == NULL)){
		arg_error = 1;
	}
	if(specified_ld_library_path == 1 && ld_library_path == NULL){
		arg_error = 1;
	}
	if(restore_apk && apkPath == NULL){
		arg_error = 1;
	}

	if(arg_error == 1){
		fprintf(stderr, "%s\n%s, %s\n", ERROR_ARGUMENT_INVALID, RESTORE_FAILURE, GO_FAILURE);
		return 1;
	}

// 	if(restore_apk && restore_data){
// 		if(restoreApkAndData(ld_library_path, apkPath, busygoxPath, dataPath, internalFilePath, forward_locked)){
// 			DLOG_MSG(DEBUG_FALG_RESTORE, "restore apk and data failed");
// 			return 1;
// 		}
// 		fprintf(stdout, "%s, %s\n", RESTORE_SUCCESS, GO_SUCCESS);
// 		return 0;
// 	}

	if(restore_apk){
		if(restoreApk(ld_library_path, apkPath, forward_locked)){
			DLOG_MSG(DEBUG_FALG_RESTORE, "restore apk failed");
			fprintf(stdout, "%s, %s\n", RESTORE_FAILURE, GO_FAILURE);
			return 1;
		}
	}

	if(restore_data){
		if(restoreData(busygoxPath, dataPath, internalFilePath)){
			DLOG_MSG(DEBUG_FALG_RESTORE, "restore data failed");
			fprintf(stdout, "%s, %s\n", RESTORE_FAILURE, GO_FAILURE);
			return 1;
		}
	}
	fprintf(stdout, "%s, %s\n", RESTORE_SUCCESS, GO_SUCCESS);
	return 0;
}

/*�ָ�
	lb_libary_path:linuxϵͳ�Ķ�̬���ӿ�·��(android 4.0���ϻ���ʹ��pm��Ҫָ�����·��)
*/
int restoreApkAndData(const char* lb_libary_path, const char* apkPath, const char* busyboxPath, const char* dataPath, const char* internalDataPath, int forwardLocked){
	char buffer[1024];
	int result = 0;

	//�ָ�apk
	if(restoreApk(lb_libary_path, apkPath, forwardLocked)){
		//��װapkʧ��
		return result;
	}

	//�ָ�����
	if(restoreData(busyboxPath, dataPath, internalDataPath)){
		//�ָ�����ʧ��
		return result;
	}
	return result;
}

int restoreApk(const char* lb_libary_path, const char* apkPath, int forwardLocked){
	char buffer[1024];
	int result = 1;
	char* forwardLockedArg = NULL;
	char *errMsg = NULL;

	if(lb_libary_path != NULL){
		if(!strncmp(lb_libary_path, "null", 4)){
			lb_libary_path = NULL;
		}
	}
	DLOG_MSG(DEBUG_FALG_RESTORE, lb_libary_path);
	DLOG_MSG(DEBUG_FALG_RESTORE, apkPath);
	DLOG_INT(DEBUG_FALG_RESTORE, forwardLocked);
	if(forwardLocked == 1){
		forwardLockedArg = "-l ";
	}else{
		forwardLockedArg = "";
	}

	if(lb_libary_path != NULL){
		sprintf(buffer, "%s pm install -r %s%s 1>%s 2>%s", lb_libary_path, forwardLockedArg, apkPath, STDOUT_LOG, STDERR_LOG);
	}else{
		sprintf(buffer, "pm install -r %s%s 1>%s 2>%s", forwardLockedArg, apkPath, STDOUT_LOG, STDERR_LOG);
	}

	system(buffer);

	if (DBUG_FLAG_RESTORE_APP) {
		errMsg = malloc(1024);
		getFile(errMsg, 1024, STDERR_LOG);
	}
	if(!checkStderr("Failure") && checkStdout("Success")){
		fprintf(stdout, "%s\n", RESTORE_APK_SUCCESS);
		result = 0;
	}else{
		// ������ӡ�ռ���Ϣ
		fprintf(stderr, "%s\n", buffer);
		if (errMsg != NULL) {
			fprintf(stderr, "%s\n", errMsg);
		}
		fprintf(stderr, "%s\n", RESTORE_APK_ERROR);
		result = 1;
	}
	if (errMsg != NULL) {
		free(errMsg);
		errMsg = NULL;
	}
	return result;
}

int restoreData(const char* busyboxFilePath, const char * dataFileFullPath, const char* internalDataFilePath)
{
	int result = 0;
	char buffer[1024];
	int oldUid = 0;
	struct stat statBuffer;
	char *errMsg = NULL;

	//�ж�Ӧ����û�а�װ���治�������Ӧ��dataĿ¼
	if(!isFileExist(busyboxFilePath, internalDataFilePath)){
		if (DBUG_FLAG_RESTORE_APP) {
			fprintf(stderr, "%s is not exit!\n", internalDataFilePath);
		}
		fprintf(stderr, "%s\n%s\n", RESTORE_DATA_ERROR);
		return 1;
	}

	//����Ӧ��dataĿ¼���û�id
	result = statDataDirectoryAttr(internalDataFilePath, &statBuffer);
	if(!result){
		oldUid = statBuffer.st_uid;
	}
	DLOG_INT(DEBUG_FALG_RESTORE, oldUid);
	if(oldUid == 0){//����uidʧ�ܣ�����
		fprintf(stderr, "%s\n", RESTORE_DATA_ERROR);
		result = 1;
		if (DBUG_FLAG_RESTORE_APP) {
			fprintf(stderr, " can not find olduid!\n");
		}
		return result;
	}

	//�ָ�����
	memset(buffer, 0, sizeof(buffer));
	sprintf(buffer, "%s tar -zxf %s -C /  1>%s 2>%s \n", busyboxFilePath, dataFileFullPath, STDOUT_LOG, STDERR_LOG);
	DLOG_MSG(DEBUG_FALG_RESTORE, buffer);
	system(buffer);

	if(DBUG_FLAG_RESTORE_APP) {
		errMsg = malloc(1024);
		getFile(errMsg, 1024, STDERR_LOG);
	}

	if(checkFileNotExitError() || !checkCmdResult()){
		// �ָ����ݳ�����ӡ�ռ���Ϣ
		fprintf(stderr, "%s\n", buffer);
		if (errMsg != NULL) {
			fprintf(stderr, "%s\n", errMsg);
			free(errMsg);
			errMsg = NULL;
		}
		fprintf(stderr, "%s\n", RESTORE_DATA_ERROR);
		result = 1;
		return result;
	}

	memset(buffer, 0, sizeof(buffer));
	if(!changeDataDirectoryAttr(busyboxFilePath, internalDataFilePath, oldUid)){
		fprintf(stdout, "%s\n", RESTORE_DATA_SUCCESS);
		result = 0;
	}else{
		if (DBUG_FLAG_RESTORE_APP) {
			fprintf(stderr, "can not changeDataDIrectoryAttr!\n");	
		}
		fprintf(stderr, "%s\n", RESTORE_DATA_ERROR);
		result = 1;
	}
	if (errMsg != NULL) {
		free(errMsg);
		errMsg = NULL;
	}
	return result;
}

int statDataDirectoryAttr(const char* dataFilePath, struct stat* pStatBuffer)
{
	if(stat(dataFilePath, pStatBuffer) != 0){
		return 1;
	}else{
		return 0;
	}
}

int changeDataDirectoryAttr(const char* busyboxFilePath, const char* dataFilePath, int uid){
	int result = 0;
	char buffer[1024];
	char *errMsg = NULL;

	sprintf(buffer, "%s chown -R %d:%d %s 1>%s 2>%s", busyboxFilePath, uid, uid, dataFilePath, STDOUT_LOG, STDERR_LOG);
	system(buffer);
	if (DBUG_FLAG_RESTORE_APP) {
		errMsg = malloc(1024);
		getFile(errMsg, 1024, STDERR_LOG);
	}
	DLOG_MSG(DEBUG_FALG_RESTORE, buffer);
	if(checkStderr("Read-only file system") || !checkCmdResult()){
		//�޸��ļ�����ʧ��
		if (errMsg != NULL) {
			fprintf(stderr, "%s\n", errMsg);
			free(errMsg);
			errMsg = NULL;
		}
		fprintf(stderr, "%s\n", RESTORE_DATA_ERROR);
		result = 1;
		return result;
	}

	memset(buffer, 0, sizeof(buffer));
	int systemUid = getUidByUserName("system");
	DLOG_INT(DEBUG_FALG_RESTORE, systemUid);
	if(systemUid == 0){
		systemUid = 1000;
	}
	sprintf(buffer, "%s chown -R %d:%d %s/lib* 1>%s 2>%s", busyboxFilePath, systemUid, systemUid, dataFilePath, STDOUT_LOG, STDERR_LOG);
	system(buffer);
	if (errMsg != NULL) {
		free(errMsg);
		errMsg = NULL;
	}
	return result;
}
