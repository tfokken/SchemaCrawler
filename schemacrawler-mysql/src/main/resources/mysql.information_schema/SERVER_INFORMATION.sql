SELECT
  VARIABLES.NAME,
  VARIABLES.VALUE
FROM
  (
    SELECT
      VARIABLE_NAME AS NAME,
      VARIABLE_VALUE AS VALUE,
      '' AS DESCRIPTION
    FROM
      INFORMATION_SCHEMA.GLOBAL_VARIABLES
    UNION
    SELECT
      VARIABLE_NAME AS NAME,
      VARIABLE_VALUE AS VALUE,
      '' AS DESCRIPTION
    FROM
      INFORMATION_SCHEMA.SESSION_VARIABLES
  )
  AS VARIABLES
WHERE
  VARIABLES.NAME NOT LIKE '%DIR'
  AND
  VARIABLES.NAME NOT LIKE '%\_FILE'
  AND
  VARIABLES.NAME != 'SOCKET'
  AND
  VARIABLES.NAME != 'TIMESTAMP'