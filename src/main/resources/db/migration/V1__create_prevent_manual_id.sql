DELIMITER //
CREATE TRIGGER IF NOT EXISTS prevent_manual_id
    BEFORE INSERT ON grid
    FOR EACH ROW
BEGIN
    IF NEW.id > 0
  then
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Manual ID insertion is forbidden for table grid';
END IF;
END//
DELIMITER ;