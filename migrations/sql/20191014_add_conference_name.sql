
BEGIN;

ALTER TABLE author_record ADD conference_name varchar(255) DEFAULT 'defaultConf';
ALTER TABLE review_record ADD conference_name varchar(255) DEFAULT 'defaultConf';
ALTER TABLE submission_record ADD conference_name varchar(255) DEFAULT 'defaultConf';
ALTER TABLE submission_author_record ADD conference_name varchar(255) DEFAULT 'defaultConf';

COMMIT;
