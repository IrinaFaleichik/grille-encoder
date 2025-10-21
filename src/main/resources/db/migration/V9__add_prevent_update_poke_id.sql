CREATE TRIGGER prevent_update
    BEFORE UPDATE ON poke
    FOR EACH ROW
BEGIN
    IF (NEW.id) THEN
        SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Manual ID update is forbidden';
END IF;
END;