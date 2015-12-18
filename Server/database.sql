  CREATE TABLE movies(
  id INT NOT NULL AUTO_INCREMENT,
  imdb_id VARCHAR(9),
  movie_name VARCHAR(100),
  file_name TEXT NOT NULL,
  gender VARCHAR(100),
  rating VARCHAR(4),
  plot TEXT,
  poster_url TEXT,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
  );

  CREATE TABLE movies_audit(
    id INT NOT NULL AUTO_INCREMENT,
    movie_id INT NOT NULL,
    rating VARCHAR(4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    PRIMARY KEY(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON UPDATE CASCADE ON DELETE CASCADE
  );

  CREATE TABLE shared_data(
    id INT NOT NULL AUTO_INCREMENT,
    data_key VARCHAR(10) NOT NULL,
    data TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    PRIMARY KEY(id),
    UNIQUE (data_key)
  );

  /* Change the delimiter so we can use ";" within the CREATE TRIGGER */
  DELIMITER $$

  CREATE TRIGGER before_movies_update
  BEFORE UPDATE ON movies
  FOR EACH ROW BEGIN
  IF OLD.rating <> NEW.rating THEN
  INSERT INTO movies_audit
  SET movie_id = OLD.id,
  rating = OLD.rating;
  END IF;
  END$$
  /* This is now "END$$" not "END;" */

  /* Reset the delimiter back to ";" */
  DELIMITER ;