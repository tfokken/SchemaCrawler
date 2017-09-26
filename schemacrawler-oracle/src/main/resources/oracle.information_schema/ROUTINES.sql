SELECT /*+ PARALLEL(AUTO) */
  NULL AS ROUTINE_CATALOG,
  PROCEDURES.OWNER AS ROUTINE_SCHEMA,
  PROCEDURES.OBJECT_NAME AS ROUTINE_NAME,
  PROCEDURES.OBJECT_NAME AS SPECIFIC_NAME,
  'SQL' AS ROUTINE_BODY,
  DBMS_METADATA.GET_DDL(OBJECT_TYPE, PROCEDURES.OBJECT_NAME, PROCEDURES.OWNER) 
    AS ROUTINE_DEFINITION
FROM
  ALL_PROCEDURES PROCEDURES
WHERE
  PROCEDURES.OWNER NOT IN 
    ('ANONYMOUS', 'APEX_PUBLIC_USER', 'APPQOSSYS', 'BI', 'CTXSYS', 'DBSNMP', 'DIP', 
    'EXFSYS', 'FLOWS_30000', 'FLOWS_FILES', 'HR', 'IX', 'LBACSYS', 
    'MDDATA', 'MDSYS', 'MGMT_VIEW', 'OE', 'OLAPSYS', 'ORACLE_OCM', 
    'ORDPLUGINS', 'ORDSYS', 'OUTLN', 'OWBSYS', 'PM', 'SCOTT', 'SH', 
    'SI_INFORMTN_SCHEMA', 'SPATIAL_CSW_ADMIN_USR', 'SPATIAL_WFS_ADMIN_USR', 
    'SYS', 'SYSMAN', 'SYSTEM', 'TSMSYS', 'WKPROXY', 'WKSYS', 'WK_TEST', 
    'WMSYS', 'XDB', 'XS$NULL', 'RDSADMIN')  
  AND NOT REGEXP_LIKE(PROCEDURES.OWNER, '^APEX_[0-9]{6}$')
  AND NOT REGEXP_LIKE(PROCEDURES.OWNER, '^FLOWS_[0-9]{5,6}$')
  AND REGEXP_LIKE(PROCEDURES.OWNER, '${schemas}')
ORDER BY
  ROUTINE_SCHEMA,
  ROUTINE_NAME
