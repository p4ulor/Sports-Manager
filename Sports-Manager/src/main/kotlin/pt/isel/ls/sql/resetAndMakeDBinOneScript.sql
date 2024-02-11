-- RUN THIS SCRIPT BEFORE RUNNING THE APP

DROP TABLE IF EXISTS ACTIVITY;
DROP TABLE IF EXISTS ROUTE;
DROP TABLE IF EXISTS SPORT;
DROP TABLE IF EXISTS "user";

CREATE TABLE "user"(
    uid  INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    uuid TEXT NOT NULL UNIQUE,
    hashedPassword TEXT NOT NULL
);

CREATE TABLE ROUTE(
                      rid INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
                      startLocation TEXT NOT NULL,
                      endLocation TEXT NOT NULL,
                      distance DECIMAL(8,2) NOT NULL,
                      uid INT NOT NULL,
                      CONSTRAINT fk_uid FOREIGN KEY(uid) REFERENCES "user"(uid)
);

CREATE TABLE SPORT(
                      sid INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
                      name TEXT NOT NULL,
                      description TEXT,
                      uid INT NOT NULL,
                      CONSTRAINT fk_uid FOREIGN KEY(uid) REFERENCES "user"(uid)
);

CREATE TABLE ACTIVITY(
                         aid INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY, ----"BY DEFAULT" instead of "ALWAYS" because it gives more flexibility on the tests in order to manipulate this value, because "ALWAYS" forces it to be strictly incremental and not skip "positions"
                         date TEXT NOT NULL,
                         duration TEXT NOT NULL,
                         uid INT NOT NULL,
                         sid INT NOT NULL,
                         rid INT,
                         CONSTRAINT fk_uid FOREIGN KEY(uid) REFERENCES "user"(uid) ON DELETE CASCADE,
                         CONSTRAINT fk_sid FOREIGN KEY(sid) REFERENCES SPORT(sid) ON DELETE CASCADE,
                         CONSTRAINT fk_rid FOREIGN KEY(rid) REFERENCES ROUTE(rid) ON DELETE CASCADE
);

-- Add sample data

INSERT INTO "user" VALUES (DEFAULT,'Filipe','filipesporting@hotmail.com','8304c3c9-c4ca-4b1a-848c-74b5f415a62f', '11j/PVKLTn2ARnNnsfACIA==');
INSERT INTO "user" VALUES (DEFAULT,'Luis', 'luisbenfica@hotmail.com','c9c49334-b452-11ec-b909-0242ac120002', '0ndsAdTKMeBWsbR5AtRn4Q==');
INSERT INTO "user" VALUES (DEFAULT,'Daniel','danielporto@gmail.com','d83a659c-b452-11ec-b909-0242ac120002', 'WvMo4G/ZAYrENqPUXkdD9Q==');

INSERT INTO SPORT VALUES (DEFAULT,'Soccer','Is a sport you play with your feet',1);
INSERT INTO SPORT VALUES (DEFAULT,'Basketball','Is a sport you play with hands and put ball in basket',3);
INSERT INTO SPORT VALUES (DEFAULT,'Pool','Pool is sports played on a table with six pockets, which balls are deposited.',2);

INSERT INTO ROUTE VALUES (DEFAULT,'Chelas','Alameda',100, 1);
INSERT INTO ROUTE VALUES (DEFAULT,'Olivais','Cabo Ruivo',150, 3);
INSERT INTO ROUTE VALUES (DEFAULT,'Olaias','Aeroporto',130, 2);

INSERT INTO ACTIVITY VALUES (DEFAULT,'2010-05-03', '13:10:10.200', 3, 1, 2);
INSERT INTO ACTIVITY VALUES (DEFAULT,'2010-05-04', '23:10:11.200', 1, 2, 3);
INSERT INTO ACTIVITY VALUES (DEFAULT,'2010-05-05', '23:10:12.200', 2, 3, 1);
INSERT INTO ACTIVITY VALUES (DEFAULT,'2010-05-06', '23:10:13.200', 2, 3, 1);
INSERT INTO ACTIVITY VALUES (DEFAULT,'2010-05-07', '23:10:14.200', 2, 3, 1);
INSERT INTO ACTIVITY VALUES (DEFAULT,'2010-05-08', '23:10:15.200', 1, 2, 2);
