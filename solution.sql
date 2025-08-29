SELECT e.emp_id AS EMP_ID,
       e.first_name AS FIRST_NAME,
       e.last_name AS LAST_NAME,
       d.department_name AS DEPARTMENT_NAME,
       COUNT(e2.emp_id) AS YOUNGER_EMPLOYEES_COUNT
FROM employee e JOIN department d ON d.department_id = e.department LEFT JOIN employee e2
  ON e2.department = e.department
 AND e2.dob > e.dob
GROUP BY e.emp_id, e.first_name, e.last_name, d.department_name ORDER BY e.emp_id DESC;