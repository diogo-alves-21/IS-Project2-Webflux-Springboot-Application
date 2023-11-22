CREATE TABLE IF NOT EXISTS owner(
    id SERIAL,
    name VARCHAR(255) NOT NULL ,
    number BIGINT NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(number)
);

CREATE TABLE IF NOT EXISTS pet(
    id SERIAL,
    name VARCHAR(255) NOT NULL,
    specie VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    weight FLOAT(8) NOT NULL,
    ownerid INT NOT NULL,
    PRIMARY KEY(id)
);


ALTER TABLE pet ADD CONSTRAINT pet_fk FOREIGN KEY (ownerid) REFERENCES owner(id);



INSERT INTO owner(name, number) VALUES('Diogo Alves',917876566);
INSERT INTO owner(name, number) VALUES('Adelino Pais',919853786);
INSERT INTO owner(name, number) VALUES('João Ferreira',917876568);
INSERT INTO owner(name, number) VALUES('José Lopes',919853789);


INSERT INTO pet(name, specie, birth_date, weight, ownerid) VALUES('Bobi','dog',TO_DATE('2018-05-15','YYYY-MM-DD'),21.2,1);
INSERT INTO pet(name, specie, birth_date, weight, ownerid) VALUES('Tareco','cat',TO_DATE('2021-02-19','YYYY-MM-DD'),5.3,2);
INSERT INTO pet(name, specie, birth_date, weight, ownerid) VALUES('Zeus','dog',TO_DATE('2018-05-15','YYYY-MM-DD'),31.4,1);
INSERT INTO pet(name, specie, birth_date, weight, ownerid) VALUES('Jacob','parrot',TO_DATE('2021-02-19','YYYY-MM-DD'),1.7,3);
INSERT INTO pet(name, specie, birth_date, weight, ownerid) VALUES('King','turtle',TO_DATE('2021-02-19','YYYY-MM-DD'),0.5,3);