CREATE TRIGGER IF NOT EXISTS prevent_manual_poke_id
    BEFORE INSERT ON poke
    FOR EACH ROW
BEGIN
    IF NEW.id >0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Manual ID insertion is forbidden';
END IF;
END;