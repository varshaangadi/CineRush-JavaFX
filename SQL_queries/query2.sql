ALTER TABLE movies
ADD COLUMN id INT PRIMARY KEY AUTO_INCREMENT FIRST;
SELECT * FROM movies;
DELETE FROM movies
WHERE title='Interstellar Returns'
AND id > 0;
SELECT title FROM movies;