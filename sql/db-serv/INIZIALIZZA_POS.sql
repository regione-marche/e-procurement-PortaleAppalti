--SET search_path = public, pg_catalog;

SET statement_timeout = 0;
-- decommentare la riga seguente in caso di lancio da interprete di comando direttamente da linux
--SET client_encoding = 'LATIN1';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

------------------------------------------
-- CREAZIONE TABELLE STANDARD JAPS 2.0.10
------------------------------------------

SET default_tablespace = '';

SET default_with_oids = true;

CREATE TABLE authgroups (
    groupname character varying(20) NOT NULL,
    descr character varying(50)
);

CREATE TABLE authpermissions (
    permissionname character varying(30) NOT NULL,
    descr character varying(50)
);

CREATE TABLE authrolepermissions (
    rolename character varying(30) NOT NULL,
    permissionname character varying(30) NOT NULL
);

CREATE TABLE authroles (
    rolename character varying(20) NOT NULL,
    descr character varying(50)
);

CREATE TABLE authusergroups (
    username character varying(40) NOT NULL,
    groupname character varying(20) NOT NULL
);


SET default_with_oids = false;

CREATE TABLE authuserroles (
    username character varying(40) NOT NULL,
    rolename character varying(20) NOT NULL
);

SET default_with_oids = true;

CREATE TABLE authusers (
    username character varying(40) NOT NULL,
    passwd character varying(40),
    registrationdate date NOT NULL,
    lastaccess date,
    lastpasswordchange date,
    active smallint
);

ALTER TABLE ONLY authgroups
    ADD CONSTRAINT authgroups_pkey PRIMARY KEY (groupname);

ALTER TABLE ONLY authpermissions
    ADD CONSTRAINT authpermissions_pkey PRIMARY KEY (permissionname);

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_pkey PRIMARY KEY (rolename, permissionname);

ALTER TABLE ONLY authroles
    ADD CONSTRAINT authroles_pkey PRIMARY KEY (rolename);

ALTER TABLE ONLY authusergroups
    ADD CONSTRAINT authusergroups_pkey PRIMARY KEY (username, groupname);

ALTER TABLE ONLY authuserroles
    ADD CONSTRAINT authuserroles_pkey PRIMARY KEY (username, rolename);

ALTER TABLE ONLY authusers
    ADD CONSTRAINT authusers_pkey PRIMARY KEY (username);

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_permissionname_fkey FOREIGN KEY (permissionname) REFERENCES authpermissions(permissionname) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY authrolepermissions
    ADD CONSTRAINT authrolepermissions_rolename_fkey FOREIGN KEY (rolename) REFERENCES authroles(rolename) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY authusergroups
    ADD CONSTRAINT authusergroups_groupname_fkey FOREIGN KEY (groupname) REFERENCES authgroups(groupname) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY authuserroles
    ADD CONSTRAINT authuserroles_rolename_fkey FOREIGN KEY (rolename) REFERENCES authroles(rolename) ON UPDATE RESTRICT ON DELETE RESTRICT;

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;

---------------------
-- CREAZIONE TABELLE PLUGIN jpmail-1.2
---------------------

---------------------
-- CREAZIONE TABELLE PLUGIN jpuserprofile-1.5
---------------------

CREATE TABLE jpuserprofile_authuserprofiles
(
  username character varying(40) NOT NULL,
  profiletype character varying(30) NOT NULL,
  "xml" character varying NOT NULL,
  publicprofile smallint NOT NULL,
  CONSTRAINT jpuserprofile_autuserprofiles_pkey PRIMARY KEY (username)
);

CREATE TABLE jpuserprofile_profilesearch
(
  username character varying(40) NOT NULL,
  attrname character varying(30) NOT NULL,
  textvalue character varying(255),
  datevalue date,
  numvalue integer,
  langcode character varying(2),
  CONSTRAINT jpuserprofile_profilesearch_username_fkey FOREIGN KEY (username)
      REFERENCES jpuserprofile_authuserprofiles (username) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

---------------------
-- CREAZIONE TABELLE DATI PLUGIN jpuserreg-1.1
---------------------

CREATE TABLE jpuserreg_activationtokens
(
  username character varying(40) NOT NULL,
  token character varying NOT NULL,
  regtime timestamp without time zone NOT NULL,
  tokentype character varying(25) NOT NULL,
  CONSTRAINT jpuserreg_activationtokens_pkey PRIMARY KEY (username)
);
------------------------------------------
-- POPOLAMENTO TABELLE STANDARD JAPS 2.0.10
------------------------------------------

INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('admin', 'adminadmin', '2008-10-10', '2009-07-09', NULL, 1);


------------------------------------------------------------------------
-- VERTICALIZZAZIONE PORTALE APPALTI
-- personalizzazioni rispetto allo standard jAPS+plugin ufficiali
------------------------------------------------------------------------

ALTER TABLE authusers ADD delegateuser character varying(40) NULL;
ALTER TABLE authusers ADD COLUMN crc VARCHAR(64);

DELETE FROM authusers WHERE username = 'admin';
INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('admin', 'ZVzjxTZrEa4=', '2008-10-10', CURRENT_TIMESTAMP, NULL, 1);

update authusers set crc = 'c6d6a506e1b540f79b591f2c928fedd8e40c97d78fb05b033513259c0e60894b' where username = 'admin';





-------------------------------------------------------------------------
-- NUOVE TABELLE PER LA STORICIZZAZIONE DELLE PASSWORD DEGLI UTENTI, TRACCIATURA ACCESSI E PASSWORD ERRATE
-------------------------------------------------------------------------

CREATE SEQUENCE ppcommon_passwords_id_seq;

CREATE TABLE ppcommon_passwords (
  id integer NOT NULL DEFAULT NEXTVAL('ppcommon_passwords_id_seq'),
  username character varying(40) NOT NULL,
  passwordchange timestamp NOT NULL DEFAULT now(),
  passwd character varying(40) NOT NULL
);

ALTER TABLE ONLY ppcommon_passwords
    ADD CONSTRAINT ppcommon_passwords_pk PRIMARY KEY (id);

ALTER TABLE ONLY ppcommon_passwords
    ADD CONSTRAINT ppcommon_passwords_username_fk FOREIGN KEY (username) REFERENCES authusers (username) MATCH SIMPLE ON DELETE CASCADE;

ALTER TABLE ONLY ppcommon_passwords
    ADD CONSTRAINT ppcommon_passwords_un_passwd UNIQUE (username, passwd);


CREATE SEQUENCE ppcommon_accesses_id_seq;

CREATE TABLE ppcommon_accesses (
  id integer NOT NULL DEFAULT NEXTVAL('ppcommon_accesses_id_seq'),
  username character varying(40) NOT NULL,
  logintime timestamp NOT NULL DEFAULT now(),
  logouttime timestamp,
  ipaddress character varying(40)
);

ALTER TABLE ONLY ppcommon_accesses
    ADD CONSTRAINT ppcommon_accesses_pk PRIMARY KEY (id);

ALTER TABLE ONLY ppcommon_accesses
    ADD CONSTRAINT ppcommon_accesses_username_fk FOREIGN KEY (username) REFERENCES authusers (username) MATCH SIMPLE ON DELETE CASCADE;


CREATE SEQUENCE ppcommon_wrongaccesses_id_seq;

CREATE TABLE ppcommon_wrongaccesses (
  id integer NOT NULL DEFAULT NEXTVAL('ppcommon_wrongaccesses_id_seq'),
  username character varying(40) NOT NULL,
  logintime timestamp NOT NULL DEFAULT now(),
  ipaddress character varying(40)
);

ALTER TABLE ONLY ppcommon_wrongaccesses
    ADD CONSTRAINT ppcommon_wrongaccesses_pk PRIMARY KEY (id);

--------------------------------------------------
-- NUOVA TABELLA PER LA TRACCIATURA DELLE VERSIONI
--------------------------------------------------

CREATE TABLE ppcommon_ver (
    plugin character varying(30) NOT NULL,
    version character varying(10) NOT NULL,
	lastupdate timestamp NULL,
    CONSTRAINT ppcommon_ver_pkey PRIMARY KEY (plugin)
);

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
  message character varying(500) NOT NULL,
  detailmessage character varying(4000) NULL,
  ipaddress character varying(40) NULL,
  sessionid character varying(100) NULL
);

ALTER TABLE ONLY ppcommon_events
    ADD CONSTRAINT ppcommon_events_pk PRIMARY KEY (id);



------------------------------------------
-- POPOLAMENTO TABELLE STANDARD JAPS 2.0.10
------------------------------------------

INSERT INTO authgroups (groupname, descr) VALUES ('administrators', 'Amministratori');
INSERT INTO authgroups (groupname, descr) VALUES ('free', 'Accesso Libero');


INSERT INTO authpermissions (permissionname, descr) VALUES ('superuser', 'All functions');
INSERT INTO authpermissions (permissionname, descr) VALUES ('validateContents', 'Supervision of contents');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageResources', 'Operations on Resources');
INSERT INTO authpermissions (permissionname, descr) VALUES ('managePages', 'Operations on Pages');
INSERT INTO authpermissions (permissionname, descr) VALUES ('enterBackend', 'Access to Administration Area');
INSERT INTO authpermissions (permissionname, descr) VALUES ('manageCategories', 'Operations on Categories');
INSERT INTO authpermissions (permissionname, descr) VALUES ('editContents', 'Content Editing');


INSERT INTO authroles (rolename, descr) VALUES ('admin', 'Tutte le funzioni');


INSERT INTO authrolepermissions (rolename, permissionname) VALUES ('admin', 'superuser');


INSERT INTO authusergroups (username, groupname) VALUES ('admin', 'administrators');


INSERT INTO authuserroles (username, rolename) VALUES ('admin', 'admin');


--INSERT INTO authusers (username, passwd, registrationdate, lastaccess, lastpasswordchange, active) VALUES ('admin', 'adminadmin', convert(datetime,'2008-10-10',121), convert(datetime,'2009-07-09',121), NULL, 1);


------------------------------------------
-- POPOLAMENTO TABELLE PLUGIN jpmail-1.2
------------------------------------------

------------------------------------------
-- POPOLAMENTO TABELLE PLUGIN jpuserprofile-1.5
------------------------------------------

------------------------------------------
-- POPOLAMENTO TABELLE DATI PLUGIN jpuserreg-1.1
------------------------------------------

------------------------------------------------------------------------
-- VERTICALIZZAZIONE PORTALE APPALTI
-- personalizzazioni rispetto allo standard jAPS+plugin ufficiali
------------------------------------------------------------------------

-------------------------------------------------------------------------
-- definizione dei ruoli e permessi per gestire:
-- * l'accesso alla lista utenti in sola lettura
-- * la modifica alle etichette configurate
-- * amministrazione dei soli contenuti CMS e attribuzione dei permessi relativi
-------------------------------------------------------------------------

-- definizione del gruppo gare per gli utenti che si registrano in modo da abilitare alcune voci di menu' se loggati
INSERT INTO authgroups (groupname, descr) VALUES ('gare', 'Gare appalto');
INSERT INTO authgroups (groupname, descr) VALUES ('sso', 'Utente autenticato mediante Single Sign On');

INSERT INTO authpermissions(permissionname, descr) VALUES ('viewUsers', 'View users only');
INSERT INTO authpermissions(permissionname, descr) VALUES ('manageLanguages', 'Personalize labels and languages');

INSERT INTO authroles(rolename, descr) VALUES ('users_ro', 'Gestione utenti in sola lettura');
INSERT INTO authroles(rolename, descr) VALUES ('languages', 'Amministratore Lingue');
INSERT INTO authroles(rolename, descr) VALUES ('cms', 'Amministratore C.M.S.');

INSERT INTO authrolepermissions(rolename, permissionname) VALUES ('users_ro', 'enterBackend');
INSERT INTO authrolepermissions(rolename, permissionname) VALUES ('users_ro', 'viewUsers');

INSERT INTO authrolepermissions(rolename, permissionname) VALUES ('languages', 'enterBackend');
INSERT INTO authrolepermissions(rolename, permissionname) VALUES ('languages', 'manageLanguages');

INSERT INTO authrolepermissions(rolename, permissionname) VALUES ('cms', 'manageResources');
INSERT INTO authrolepermissions(rolename, permissionname) VALUES ('cms', 'validateContents');
INSERT INTO authrolepermissions(rolename, permissionname) VALUES ('cms', 'editContents');
INSERT INTO authrolepermissions(rolename, permissionname) VALUES ('cms', 'enterBackend');


-------------------------------------------------------------------------
-- autorizzazioni di altri utenti standard
------------------------------------------------------------------------

--------------------------------------------------
-- ALLINEAMENTO DELLE VERSIONI DEI PLUGIN
--------------------------------------------------

INSERT INTO ppcommon_ver(plugin, version, lastupdate) VALUES ('japs', '2.0.10', CURRENT_TIMESTAMP);
INSERT INTO ppcommon_ver(plugin, version, lastupdate) VALUES ('jpuserreg', '1.1', CURRENT_TIMESTAMP);
INSERT INTO ppcommon_ver(plugin, version, lastupdate) VALUES ('jpuserprofile', '1.5', CURRENT_TIMESTAMP);
INSERT INTO ppcommon_ver(plugin, version, lastupdate) VALUES ('ppcommon', '3.13.0', CURRENT_TIMESTAMP);
