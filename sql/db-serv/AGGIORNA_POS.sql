--SET search_path = public, pg_catalog;

SET statement_timeout = 0;
-- decommentare la riga seguente in caso di lancio da interprete di comando direttamente da linux
--SET client_encoding = 'LATIN1';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

-- 1.11.1to1.11.2

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.11.1') THEN
	
---------------------------------------------------
-- NUOVA TABELLA PER LA GESTIONE EVENTI APPLICATIVI
---------------------------------------------------

CREATE SEQUENCE ppcommon_events_id_seq;

CREATE TABLE ppcommon_events (
  id integer NOT NULL DEFAULT NEXTVAL('ppcommon_events_id_seq'),
  eventtime timestamp NOT NULL DEFAULT now(),
  eventlevel numeric(1) NOT NULL,
  username character varying(40) NULL,
  destination character varying(40) NULL,
  eventtype character varying(20) NOT NULL,
  message character varying(1000) NOT NULL
);

ALTER TABLE ONLY ppcommon_events ADD CONSTRAINT ppcommon_events_pk PRIMARY KEY (id);

ALTER TABLE ONLY ppcommon_events ADD CONSTRAINT ppcommon_events_username_fk FOREIGN KEY (username) REFERENCES authusers (username) MATCH SIMPLE ON DELETE CASCADE;


-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.11.2', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.11.1';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.11.2to1.11.3

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.11.2') THEN
	
-------------------------------------------------------------
-- VARIAZIONI ALLA TABELLA PER LA GESTIONE EVENTI APPLICATIVI
-------------------------------------------------------------
ALTER TABLE ppcommon_events ADD detailmessage character varying(4000) NULL;

ALTER TABLE ppcommon_events ALTER COLUMN message TYPE character varying(500);


-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.11.3', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.11.2';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.11.3to1.12.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.11.3') THEN
	
-------------------------------------------------------------
-- VARIAZIONI ALLA TABELLA PER LA GESTIONE EVENTI APPLICATIVI
-------------------------------------------------------------
ALTER TABLE ppcommon_events ADD ipaddress character varying(40) NULL;

-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.12.0', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.11.3';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.12.0to1.12.1

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.12.0') THEN
	
alter table ppcommon_events drop constraint ppcommon_events_username_fk;

-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.12.1', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.12.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.12.1to1.13.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.12.1') THEN
	
ALTER TABLE authusers ADD delegateuser character varying(40) NULL;

INSERT INTO authgroups (groupname, descr) VALUES ('sso', 'Utente autenticato mediante Single Sign On');

-- cambia la password di admin

-- definizione utente amministratore ad uso esclusivo di terzi non interni


-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.13.0', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.12.1';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.13.0to1.13.1

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.13.0') THEN
	

-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.13.1', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.13.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.13.1to1.13.2

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.13.1') THEN
	

-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.13.2', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.13.1';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.13.2to1.14.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.13.2') THEN
	
-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.0', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.13.2';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.0to1.14.0.a

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.0') THEN
	
-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.0.a', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.14.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.0.a_to_1.14.0.b

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.0.a') THEN
	
-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.0.b', lastupdate = now() WHERE plugin = 'ppcommon' AND version = '1.14.0.a';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.0.b_to_1.14.1

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.0.b') THEN
	
-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.1', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.0.b';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.1_to_1.14.2

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.1') THEN

ALTER TABLE ppcommon_events ADD sessionid character varying(60) NULL;
	
-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.2', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.1';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.2_to_1.14.3

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.2') THEN

-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.3', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.2';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.3_to_1.14.4

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.3') THEN

-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.4', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.3';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.4_to_1.14.5

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.4') THEN

-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.5', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.4';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.5_to_1.14.6

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.5') THEN

-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.6', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.5';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.6_to_1.14.6.a

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.6') THEN

	delete from authusers where username in (select username from jpuserprofile_authuserprofiles where profiletype='PFL');
	delete from jpuserprofile_profilesearch where username in (select username from jpuserprofile_authuserprofiles where profiletype='PFL');
	delete from jpuserprofile_authuserprofiles where profiletype='PFL';

	-- AGGIORNAMENTO DELLE VERSIONI
UPDATE ppcommon_ver SET version = '1.14.6.a', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.6';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.6.a_to_1.14.6.b

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.6.a') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '1.14.6.b', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.6.a';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();
-- 1.14.6.b_to_1.14.7

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.6.b') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '1.14.7', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.6.b';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.14.7_to_1.14.8

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.7') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '1.14.8', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.7';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.14.8_to_1.14.9

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.8') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '1.14.9', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.8';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.14.9_to_1.14.10

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.9') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '1.14.10', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.9';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.14.10_to_1.14.10.c

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.10') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '1.14.10.c', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.10';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.14.10.c_to_1.14.10.f

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.10.c') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '1.14.10.f', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.10.c';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 1.14.10.f_to_2.0.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '1.14.10.f') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '2.0.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '1.14.10.f';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 2.0.0_to_2.1.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '2.0.0') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '2.1.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '2.0.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 2.1.0_to_2.2.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '2.1.0') THEN
	-- INIZIO AGGIORNAMENTI
	
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '2.2.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '2.1.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 2.2.0_to_2.2.6

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '2.2.0') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '2.2.6', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '2.2.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 2.2.6_to_2.3.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '2.2.6') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '2.3.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '2.2.6';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 2.3.0_to_2.4.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '2.3.0') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '2.4.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '2.3.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 2.4.0_to_3.0.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '2.4.0') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.0.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '2.4.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.0.0_to_3.1.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.0.0') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.1.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.0.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.1.0_to_3.2.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.1.0') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.2.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.1.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.2.0_to_3.3.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.2.0') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.3.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.2.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.3.0_to_3.4.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.3.0') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.4.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.3.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.4.0_to_3.5.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.4.0') THEN
	-- INIZIO AGGIORNAMENTI
	ALTER TABLE authusers ADD COLUMN crc VARCHAR(64);
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.5.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.4.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.5.0_to_3.5.1

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.5.0') THEN
	-- INIZIO AGGIORNAMENTI
	
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.5.1', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.5.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.5.1_to_3.6.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.5.1') THEN
	-- INIZIO AGGIORNAMENTI
		
	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.6.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.5.1';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.6.0_to_3.7.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.6.0') THEN
	-- INIZIO AGGIORNAMENTI
	update authusers set passwd = 'ZVzjxTZrEa4=', crc = 'c6d6a506e1b540f79b591f2c928fedd8e40c97d78fb05b033513259c0e60894b', lastpasswordchange = CURRENT_TIMESTAMP where username = 'admin';

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.7.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.6.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.7.0_to_3.8.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.7.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.8.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.7.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.8.0_to_3.9.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.8.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.9.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.8.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.9.0_to_3.10.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.9.0') THEN
	-- INIZIO AGGIORNAMENTI
	ALTER TABLE ppcommon_events ALTER COLUMN sessionid type VARCHAR(100);

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.10.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.9.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.10.0_to_3.11.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.10.0') THEN
	-- INIZIO AGGIORNAMENTI


	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.11.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.10.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.11.0_to_3.11.1

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.11.0') THEN
	-- INIZIO AGGIORNAMENTI


	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.11.1', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.11.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.11.1_to_3.12.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.11.1') THEN
	-- INIZIO AGGIORNAMENTI


	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.12.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.11.1';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.12.0_to_3.13.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.12.0') THEN
	-- INIZIO AGGIORNAMENTI


	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.13.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.12.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.13.0_to_3.13.7

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.13.0') THEN
	-- INIZIO AGGIORNAMENTI
	update authusers set passwd = 'bHl5qFBExyI197Ah6ZYVzvdwox5q7EdQ' where passwd = 'hmAkKomZ7zA=';

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.13.7', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.13.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.13.7_to_3.14.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.13.7') THEN
	-- INIZIO AGGIORNAMENTI

	update authusers set passwd = 'bHl5qFBExyI197Ah6ZYVzvdwox5q7EdQ' where passwd = 'hmAkKomZ7zA=';

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.14.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.13.7';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.14.0_to_3.15.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.14.0') THEN
	-- INIZIO AGGIORNAMENTI
	ALTER TABLE authusers ADD acceptance_version integer;

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.15.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.14.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.15.0_to_3.16.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.15.0') THEN
	-- INIZIO AGGIORNAMENTI
	UPDATE authusers SET passwd = 'njBnHz0lgNmfba5HO6NsPA==', crc = '2b8a84219d181c980c6ee0937b4f862f4121cd8f9c67633b0641a6ccefe3ee26' WHERE username = 'admin';

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.16.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.15.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.16.0_to_3.17.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.16.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.17.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.16.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.17.0_to_3.18.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.17.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.18.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.17.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.18.0_to_3.19.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.18.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.19.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.18.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.19.0_to_3.20.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.19.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.20.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.19.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.20.0_to_3.21.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.20.0') THEN
	-- INIZIO AGGIORNAMENTI

	ALTER TABLE authusers ALTER COLUMN passwd TYPE VARCHAR(64);  
	ALTER TABLE ppcommon_passwords ALTER COLUMN passwd TYPE VARCHAR(64);  
	
	ALTER TABLE ppcommon_accesses ADD COLUMN sessionid VARCHAR(100);  
	

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.21.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.20.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.21.0_to_3.22.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.21.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.22.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.21.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.22.0_to_3.23.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.22.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.23.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.22.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

-- 3.23.0_to_3.24.0

CREATE OR REPLACE FUNCTION aggiornamento() 
	RETURNS void AS
$$
BEGIN
    IF (select count(*) = 1 from ppcommon_ver where plugin = 'ppcommon' and version = '3.23.0') THEN
	-- INIZIO AGGIORNAMENTI

	-- AGGIORNAMENTO DELLE VERSIONI
	UPDATE ppcommon_ver SET version = '3.24.0', lastupdate = CURRENT_TIMESTAMP WHERE plugin = 'ppcommon' AND version = '3.23.0';

	-- FINE AGGIORNAMENTI
	END IF;
END;
$$
LANGUAGE 'plpgsql' ;

select * from aggiornamento();
drop function aggiornamento();

