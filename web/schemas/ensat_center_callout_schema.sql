-- ENSAT CENTER CALLOUT SCHEMA FILE (AJS, 28/07/11)----

USE center_callout;

-- Removes tables if previously created
DROP TABLE Center_Callout;

-- Now create the tables ----

-- Center_Callout table
CREATE TABLE Center_Callout (
	center_id			VARCHAR(5),
	ensat_id			INTEGER,
	PRIMARY KEY(center_id,ensat_id)
);

INSERT INTO Center_Callout VALUES('FRBO',8);
INSERT INTO Center_Callout VALUES('FRGR',6);
INSERT INTO Center_Callout VALUES('FRNA',7);
INSERT INTO Center_Callout VALUES('FRPA1',93);
INSERT INTO Center_Callout VALUES('FRPA2',84);
INSERT INTO Center_Callout VALUES('FRPA3',76);
INSERT INTO Center_Callout VALUES('FRMA',13);
INSERT INTO Center_Callout VALUES('FRTO',3);

INSERT INTO Center_Callout VALUES('GBBI',8);

INSERT INTO Center_Callout VALUES('GYBN',95);
INSERT INTO Center_Callout VALUES('GYMU',294);
INSERT INTO Center_Callout VALUES('GYWU',684);

INSERT INTO Center_Callout VALUES('ITBR',2);
INSERT INTO Center_Callout VALUES('ITFL',9);
INSERT INTO Center_Callout VALUES('ITTU',3);

INSERT INTO Center_Callout VALUES('SPMA',8);

---- PMT CENTERS

INSERT INTO Center_Callout VALUES('GYDR',55);
INSERT INTO Center_Callout VALUES('PLWW',122);

---- CENTERS EMPTY TO DATE

INSERT INTO Center_Callout VALUES('FRLY1',0);
INSERT INTO Center_Callout VALUES('FRLY2',0);
INSERT INTO Center_Callout VALUES('FRLI',0);
INSERT INTO Center_Callout VALUES('FRMO',0);
INSERT INTO Center_Callout VALUES('FRAN',0);
INSERT INTO Center_Callout VALUES('FRST',0);
INSERT INTO Center_Callout VALUES('FRRE',0);
INSERT INTO Center_Callout VALUES('FRBR',0);

INSERT INTO Center_Callout VALUES('NLRO',0);
INSERT INTO Center_Callout VALUES('NLNI',0);

INSERT INTO Center_Callout VALUES('HYBU',0);

---- HAVE BEEN SOME ADDITIONS SINCE (some now containing entries)

INSERT INTO Center_Callout VALUES('GBLE',0);
INSERT INTO Center_Callout VALUES('GBSH',0);
INSERT INTO Center_Callout VALUES('NLEI',0);
INSERT INTO Center_Callout VALUES('BGSO',0);
INSERT INTO Center_Callout VALUES('GYDF',0);
INSERT INTO Center_Callout VALUES('GBOX',0);
INSERT INTO Center_Callout VALUES('ITTU2',0);
INSERT INTO Center_Callout VALUES('ITPD',0);
INSERT INTO Center_Callout VALUES('ITRO',0);
INSERT INTO Center_Callout VALUES('ITRO2',0);
INSERT INTO Center_Callout VALUES('PROP',0);
INSERT INTO Center_Callout VALUES('ITPD2',0);

---- Adding the investigator details (best place to put them)

ALTER TABLE Center_Callout ADD COLUMN investigator_name VARCHAR(100) AFTER ensat_id;
ALTER TABLE Center_Callout ADD COLUMN investigator_email VARCHAR(100) AFTER investigator_name;

UPDATE Center_Callout SET investigator_name='';
UPDATE Center_Callout SET investigator_email='';

UPDATE Center_Callout SET investigator_name='Wiebke Arlt', investigator_email='w.arlt@bham.ac.uk' WHERE center_id='GBBI';



