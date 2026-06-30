UPDATE users
SET role='ADMIN'
WHERE username='varsha';
SELECT username, role FROM users;
DELETE FROM users
WHERE username='varsha'
AND role='USER';
SELECT username, role FROM users;
SELECT title, imdb, release_date
FROM movies
ORDER BY id DESC;