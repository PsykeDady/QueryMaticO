# Documentazione di QueryCraft

QueryCraft è un framework che include 

- un sistema di costruzione dinamica delle query 
- costruzione e gestione del socket di connessione con il DBMN
- mapping da classi java a tipi/tabelle del DB 



Attraverso questo framework, la persistenza con Java ottiene un approccio Orientato ad Oggetti, astraendo completamente il linguaggio di connessione del DB.

- [Salta ad inizio documentazione](#ConnectionCraft)



## MYSQL : speed start e confronto con le metodologie classiche

Ancora prima di mostrare la documentazione, ecco una piccola anteprima di come facilmente si pu&ograve; impostare una connessione MySql con il QueryCraft.

Supponiamo di voler creare la seguente tabella :

![ENTITY](diagrams/SPEED_START_ENTITY.png)

con identity **chiave primaria**. Quindi di voler inserire in tabella:

| **IDENTITY** | **NAME** | **DESCRIPTION**                          |
| -----------: | :------: | ---------------------------------------- |
|          `1` |   DOGE   | *funny dog*                              |
|          `2` |  MARIO   | *italian plumber*                        |
|          `3` |  STEVEN  | *strange magic mix of diamond and a kid* |



### senza QueryCraft

```java
public static void main(String [] main){
    String URL="jdbc:mysql://localhost:3306";
    Connection connessione=null;
    
    try{
        connessione=DriverManager.getConnection(URL,"root",psk);
    }catch(SQLException s){
        throw new IllegalStateException(s.getMessage());
    }
    
    try{
        connessione.createStatement().execute("CREATE DATABASE `DBName`");
    }catch(SQLException s){
        throw new IllegalStateException(s);
    }//try-catch
    
     try{
        connessione.createStatement().execute("CREATE TABLE `DBName`.`Entity` (identity INT,name TEXT,description TEXT,PRIMARY KEY(identity))");
    }catch(SQLException s){
        throw new IllegalStateException(s);
    }//try-catch
    
    try{
        connessione.createStatement().execute("INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (1,'DOGE','funny dog')");
    }catch(SQLException s){
        throw new IllegalStateException(s);
    }//try-catch
    
    try{
        connessione.createStatement().execute("INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (2,'MARIO','italian plumber')");
    }catch(SQLException s){
        throw new IllegalStateException(s);
    }//try-catch
    
    try{
        connessione.createStatement().execute("INSERT INTO `DBName`.`Entity` ( `identity`,`name`,`description`) VALUES (3,'STEVEN','strange magic mix of diamond and a kid')");
    }catch(SQLException s){
        throw new IllegalStateException(s);
    }//try-catch
    
    try{
        ResultSet rs=connessione.createStatement().executeQuery("SELECT * FROM `DBName`.`Entity`");
        while (rs.next()) {
            System.out.println(rs.getInt("identity")+" "+rs.getString("name")+" "+rs.getString("description"));
        }
    }catch(SQLException s){
        errMsg=buildSQLErrMessage(s);
    }//try-catch
    
}
```



### con QueryCraft

Crea una classe che rappresenti la tabella:

```java
public class Entity {
    private int identity;
    private String name;
    private String description;
    public Entity(int identity, String name, String description){
        setIdentity(identity);
        setName(name);
        setDescription(description);
    }
    public void setIdentity(int identity){this.identity=identity;}
    public void setName(String name){this.name=name;}
    public void setDescription(String description){this.description=description;}
}
```



Quindi in un programma esegui:

```java
public static void main(String []args){
	MySqlConnection.createConnection((SQLConnectionCraft) 
				new SQLConnectionCraft().psk(psk));
	MySqlConnection m = new MySqlConnection();

    // create db
    DBCraft dbc = new SQLDBCraft().DB("DBName");
    m.exec(dbc.create());
    if (!m.getErrMsg().equals(""))
        throw new IllegalStateException("an error occur: " + m.getErrMsg());

    // create table
    TableCraft tc = new SQLTableCraft().DB("DBName").table(Entity.class).primary("identity");
    m.exec(tc.create());
    if (!m.getErrMsg().equals(""))
        throw new IllegalStateException("an error occur: " + m.getErrMsg());

    // insert data
    Entity e = new Entity(1, "DOGE", "funny dog");
    m.exec(tc.insertData(e).craft());
    if (!m.getErrMsg().equals(""))
        throw new IllegalStateException("an error occur: " + m.getErrMsg());

    e = new Entity(2, "MARIO", "italian plumber"); 
    m.exec(tc.insertData(e).craft());
    if (!m.getErrMsg().equals(""))
        throw new IllegalStateException("an error occur: " + m.getErrMsg());

    e = new Entity(3, "STEVEN", "strange magic mix of diamond and a kid");
    m.exec(tc.insertData(e).craft());
    if (!m.getErrMsg().equals(""))
        throw new IllegalStateException("an error occur: " + m.getErrMsg());
    
    List<Entity> res=m.queryList(Entity.class, sel.craft());
    if (!m.getErrMsg().equals(""))
        throw new IllegalStateException("an error occur: " + m.getErrMsg());
    for(Entity ent : res ) {
        System.out.println(ent.identity+" "+ent.name+" "+ent.description);
    }
    
}
```



### Pro e contro nell'esempio



|                              |                      Senza Query Craft                       |                       Con Query Craft                        |
| ---------------------------- | :----------------------------------------------------------: | :----------------------------------------------------------: |
| *verbosità*                  | il codice è più lungo, contiene più elementi di distrazione e risulta meno pulito |              il codice è più corto, più pulito               |
| *riusabilità e manutenzione* | il codice dipende strettamente dall'esempio, non è riadattabile facilmente né facilmente aggiornabile | il codice preleva dalle classi automaticamente le informazioni che servono, il che rende l'approccio facilmente aggiornabile e più manutenibile |
| *gestione errori*            |      la gestione degli errori viene fatto via try catch      | la gestione degli errori è demandata all'interrogazione di apposite stringhe all'interno della classe che effettua query |



### Tutto qua? 

No, non è tutto qua. 
Con query craft puoi:

- generare facilmente update a partire dalle istanze
- creare più istanze di select diverse e farne la join su determinati campi.
- preoccuparti di meno delle injection, i controlli sono effettuati già dai builder

E molto altro



## Struttura generale dei package

```
workspace/QueryCraft/QueryCraft/src
└── psykeco
    └── querycraft
        ├── err
        │   ├── dict
        │   │   ├── ENErrMsg.java
        │   │   └── ITErrMsg.java
        │   └── ErrMsg.java
        ├── sql
        │   ├── models
        │   │   └── Tables.java
        │   ├── runners
        │   │   ├── InformationSchema.java
        │   │   └── MySqlConnection.java
        │   ├── utility
        │   │   └── SQLClassParser.java
        │   ├── SQLConnectionCraft.java
        │   ├── SQLDBCraft.java
        │   ├── SQLDeleteCraft.java
        │   ├── SQLInsertCraft.java
        │   ├── SQLSelectCraft.java
        │   ├── SQLTableCraft.java
        │   └── SQLUpdateCraft.java
        ├── ConnectionCraft.java
        ├── DBCraft.java
        ├── QueryCraft.java
        ├── SelectCraft.java
        └── TableCraft.java

```



### package querycraft

Contiene le interfacce del framework di QueryCraft: 

- ConnectionCraft : gestisce e crea la connessione
- DBCraft : genera query per la creazione e gestione di un database
- TableCraft : genera query per la creazione e gestione di una tabella, comprese insert, select, ecc..
- QueryCraft : ogni implementazione genera una tipologia di istruzioni sql
- SelectCraft :  estende QueryCraft, genera query di selezione dei dati

 

## ConnectionCraft

Il framework inizia da `ConnectionCraft`, un builder che semplifica i processi di connessione.


| nome metodo ( parametri input ) |        output         |                      breve spiegazione                       |
| ------------------------------- | :-------------------: | :----------------------------------------------------------: |
| `driver(String)`                |   `ConnectionCraft`   |          imposta il nome dei driver (se necessario)          |
| `url(String)`                   |   `ConnectionCraft`   |         imposta nome url (`localhost` predefinito )          |
| `user(String)`                  |   `ConnectionCraft`   |          imposta nome utente  (`root` predefinito )          |
| `psk(String)`                   |   `ConnectionCraft`   |            imposta password (`blank` by default)             |
| `db(String)`                    |   `ConnectionCraft`   |          imposta nome database  (`null` by default)          |
| `autocommit(boolean)`           |   `ConnectionCraft`   |            imposta autocommit (`true` by default)            |
| `port(int)`                     |   `ConnectionCraft`   |         imposta numero di porta (`3306` by defautl)          |
| `connect()`                     | `java.sql.Connection` |         connette al db e restituisce la connessione          |
| `validate()`                    |       `String`        | restituisce stringa vuota se i parametri superano il controllo |



L'unica implementazione attualmente disponibile di **ConnectionCraft** è `SQLConnectionCraft`

### SQLConnectionCraft

Ecco un esempio di connessione con SQLConnectionCraft:

```java

```



## MySqlConnection

Crea un istanza singleton di `SQLConnectionCraft` e di `java.sql.Connection` e la usa per le query

Instanziazione in due fasi:

```java
// creazione della connessione, una tantum nel codice fino a chiusura connessione
MySqlConnection.createConnection(
    (SQLConnectionCraft) 
	new SQLConnectionCraft().url(url).port(nport)
    .psk(psk).user(user).db(db).autocommit(true)
);

// preleva l'istanza e la usa
MySqlConnection m = new MySqlConnection();
```



Fornisce anche alcune **query**:  

| nome metodo ( parametri input ) | output         | breve spiegazione                                   |
| ------------------------------- | -------------- | --------------------------------------------------- |
| `exec( String queryCompleta)`   | `String`       | esegue un istruzione MySql *(sconsigliato uso con stringa diretta)* |
| `query( String queryCompleta)`  | `ResultSet`    | esegue una query, restituisce il ResultSet *(sconsigliato uso con stringa diretta)* |
| `queryList(Class<T>, String)` 	| `List<T>` 		| Esegue la query e trasforma ogni riga con un oggetto della classe passata, quindi ne costruisce una lista *(sconsigliato uso con stringa diretta)* |
| `queryMap(String)` 				| `Map <String,Object>`	| Esegue la query e restituisce una mappa dove la chiave rappresenta il nome della colonna e l'oggetto il valore *(sconsigliato uso con stringa diretta)* |
| `getErrMsg()` 					| `String` 		| Restituisce, se esiste, un messaggio di errore dell'ultima query |



> <u>**NOTA BENE**:</u>
>
> I metodi di `exec`, `query`, `queryList` e `queryMap` possono essere usati direttamente con delle Stringhe che rappresentano le query. Però non viene effettuato nessun controllo sui parametri, questo approccio è **Injection UNSAFE** 
>
> La politica principale di QueryCraft è di non scrivere mai sintassi del DB a mano!



La classe fornisce i seguenti metodi statici per interagire con la Connessione, il suo stato ed eventualmente effettuarne reset e controlli:
| nome metodo ( parametri input ) | output         | breve spiegazione                                   |
| ------------------------------- | -------------- | --------------------------------------------------- |
|`createConnection(SQLConnectionCraft)` 		 | `void` 	 | se non esiste un istanza attiva, ne crea una con le informazioni passate 	|
|`createConnection(String url, int port, String user, String psk)` | `void` 	 | se non esiste un istanza attiva, ne crea una con le informazioni passate (versione con parametri) |
|`statoConnessione()`	 						 | `boolean` | restituisce true se &egrave; connesso 										|
|`reset()` 										 | `void` 	 | imposta a null l'istanza statica di connessione								|
|`reboot()` 									 | `void` 	 | ricrea la connessione con le informazioni passate a momento di creazione		|
|`commit()` 									 | `void` 	 | esegue il commit dello statement corrente 									|
|`rollback()` 									 | `void` 	 | esegue il rollback dello statement corrente ( se autocommit &egrave; false ) |
|`close()` 										 | `void` 	 | chiude la connessione 														|



## DBCraft

La `DBCraft` crea le istruzioni per generare, eliminare e trarre informazioni dei database.

Espone i seguenti metodi:

| **Nome metodo**         | **Descrizione**   (*obbligatorio)                            |
| ----------------------- | ------------------------------------------------------------ |
| `DB(String) : DBCraft`  | imposta il nome del DB *                                     |
| `validate() : boolean`  | valida la query, se false qualche parametro necessario non è stato impostato, oppure qualche valore non ha passato la regex |
| `create() : String`     | costruisce l' istruzione di creazione sotto forma di stringa |
| `exists() : String`     | costruisce una query di select per prelevare il db con questo nome sotto forma di stringa |
| `drop() : String`       | costruisce l' istruzione di drop sotto forma di stringa      |
| `listTables() : String` | costruisce la select sotto forma di stringa                  |



L'unica implementazione disponibile è quella di `SQLDBCraft`



## TableCraft

La `TableCraft` crea le istruzioni per generare, eliminare e trarre informazioni dalle tabelle a partire dalle classi java. Per farlo usa la **reflection**.

Espone i seguenti metodi:

| **Nome metodo**                    | **Descrizione**   (*obbligatorio)                            |
| ---------------------------------- | ------------------------------------------------------------ |
| `DB(String) : TableCraft`          | imposta il nome del DB *                                     |
| `table(Class) : TableCraft`        | imposta il nome della tabella *                              |
| `suffix(String) : TableCraft`      | imposta un suffisso                                          |
| `prefix(String) : TableCraft`      | imposta un prefisso                                          |
| `primary(String) : TableCraft`     | aggiunge una chiave primaria                                 |
| `validate() : boolean`             | valida la query, se false qualche parametro necessario non è stato impostato, oppure qualche valore non ha passato la regex |
| `create() : String`                | costruisce l' istruzione di creazione sotto forma di stringa |
| `exists() : String`                | costruisce una query di select per prelevare la table con questo nome (se almeno 1 risultato: esiste) |
| `drop() : String`                  | costruisce l' istruzione di drop sotto forma di stringa      |
| `insertData(Object) : QueryCraft`  | costruisce l' istruzione di insert usando un istanza         |
| `selectData(Object) : SelectCraft` | costruisce l' istruzione di select usando un istanza (se null, `select *`) |
| `updateData(Object) : QueryCraft`  | costruisce l' istruzione di update usando un istanza  (la where viene impostata sui campi indicati con primary) |
| `deleteData(Object) : QueryCraft`  | costruisce l' istruzione di delete usando un istanza         |
| `countData() :SelectCraft`         | costruisce un istruzione di di count usando un istanza (se null, `select count( *)`) |



L'unica implementazione disponibile è quella di `SQLTableCraft`



## QueryCraft

Nel package `psykeco.ioeasier.db.querycraft`  si può trovare un sottosistema di creazione delle query da mandare al DB.
Le istanze di `QueryCraft` sono builder che creano delle query a partire da coppia **chiave-valore** che gli vengono date in pasto.

L'interfaccia espone i metodi:

| **Nome metodo**                                    | **Descrizione**   (*obbligatorio)                            |
| -------------------------------------------------- | ------------------------------------------------------------ |
| `DB(String) : QueryCraft`                          | imposta il nome del DB*                                      |
| `table(String) : QueryCraft`                       | imposta il nome della tabella*                               |
| `entry(String column, String value) : QueryCraft`  | imposta una coppia colonna-valore all'operatore principale (select, update, insert, etc...) |
| `filter(String column, String value) : QueryCraft` | imposta una coppia colonna-valore alla where                 |
| `validate() : boolean`                             | valida la query, se false qualche parametro necessario non è stato impostato, oppure qualche valore non ha passato la regex |
| `craft() : String`                                 | costruisce la query sotto forma di stringa                   |

 

Sono inoltre disponibili i seguenti metodi statici :

- `str(Object o):String`  :  restituisce la rappresentazione stringa dell'oggetto che verrà messa nel DB
  - nel caso delle stringhe ad esempio verranno aggiunti apici singoli `'`
- `validateBase(String)` : restituisce `null` se la stringa non è valida da usare come nome colonna/tabella/db, altrimenti viene restituita una stringa eventualmente con i caratteri &#96;   
- `validateValue(String)` : restituisce `null` se la stringa non è valida da usare come nome colonna/tabella/db, altrimenti viene restituita una stringa eventualmente con i caratteri '



Al momento son presenti le seguenti implementazioni di QueryCraft:

![](diagrams/QueryCraft_Hierarchy.png)


### exception

| Eccezione                       | messaggio                                                 | quando                                                       |
| ------------------------------- | --------------------------------------------------------- | ------------------------------------------------------------ |
| `UnsupportedOperationException` | SqlInsertCraft does not support filter                    | uso dei metodi filter su SqlInsertCraft (non  ha una where)  |
| `UnsupportedOperationException` | SqlDeleteCraft does not support entry                     | uso dei metodi entry su SqlDeleteCraft ( non ha campi di selezione ) |
| `IllegalArgumentException`      | nome tabella/db necessario                                | Durante la fase di validazione, è stata trovata una tabella o db esistente ( son due messaggi diversi, a seconda di cosa non è stato trovato) |
| `IllegalArgumentException`      | Una chiave/ il valore di una chiave è stata trovato vuoto | Durante la fase di validazione, è stata trovata una chiave o il valore di una chiave vuoti ( son due messaggi diversi, a seconda di cosa non è stato trovato) |
| `IllegalArgumentException`      | Nome tabella/db/chiave/valore non valido                  | Durante la fase di validazione, son stati trovati dei valori di tabella/db/chiave/valore non validi () son quattro messaggi diversi, a seconda di cosa non ha passato la regex) |



### MySql e File : throubleshoot

Gli inserimenti dei **BLOB** vengono effettuati tramite la direttiva di MySQL `LOAD_FILE`. 

Se tutti i file che caricate risultano null provate questi procedimenti


#### linux e systemd
Se avete a che fare con systemd, potrebbe essere necessario invece sovrascrivere alcune configurazioni del servizio. Modificate o create eventualmente il file  `/etc/systemd/system/mariadb.service.d/MY_SPECIAL.conf` scrivendo:

```properties
[Service]
PrivateTmp=false
```

#### permessi di scrittura o dimensione dei file

Potrebbe essere necessario che siano attivi alcuni parametri sul vostro processo di MySQL attivo.  Provate a rieseguire il daemon di mysql con i parametri:

- `--secure-file-priv=/tmp` per linux, `--secure-file-priv=C:\Users\NomeUtente\AppData\Local\Temp\`  per windows
- `--max-allowed-packet=1024M`  ( o un qualsiasi valore appropriato alle vostre esigenze )

Ecco un esempio di chiamata su **linux**:
`sudo mysqld --secure-file-priv=/tmp --max-allowed-packet=1024M`
è possibile eventualmente inserire queste informazioni all'interno del file `/etc/my.cnf` o `/etc/my.cnf.d/server.cnf`. Aggiungete queste due linee:

```properties
[mysqld]
secure-file-priv=/tmp`
max-allowed-packet=1024M`
```


> **<u>ATTENZIONE</u>**:
>
> su windows non è stato testato.



## SelectCraft

estende l'interfaccia `QueryCraft` aggiungendo le funzioni di join. Espone i metodi:

| **Nome metodo**                                              | **Descrizione**   (*obbligatorio)                            |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| `alis(String) : SelectCraft`                                 | imposta un nome da usare come alias nella query              |
| `join(SelectCraft) : SelectCraft`                            | imposta un selectCraft per la join (al momento max=1)        |
| `joinFilter(Entry<String,String>) : SelectCraft`             | imposta una coppia di colonne che deve essere uguale tra la select this (primo valore) e la select in join |
| `joinFilter(String columntThis,String columnOther) : SelectCraft` | come sopra, ma preleva due stringhe in ingresso              |
| `selectCraft() : String`                                     | restituzione dei campi nella select                          |
| `fromCraft() : String`                                       | restituzione dei campi nella from                            |
| `whereCraft() : String`                                      | restituzione dei campi nella where                           |


L'unica implementazione attuale è `SQLSelectCraft`



 
