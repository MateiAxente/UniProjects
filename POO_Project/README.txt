Facultatea de Automatica si Claculatoare
Sectia CTI

Axente Matei Sorin
Grupa 325 CC

			POO - Tema
			 MyCloud


Pentru rulare: POO_TEMA2.java

Parola pentru root: student


Clasele folosite in ordine alfabetica:

	Cat:
	Afiseaza numele fisierului "'s content:" (nu i-am
dat byte[].toString() deoarece nu am initializat array-ul
si dadea erori) sau numele directorului "'s not a file".

---------------------------------------------------------

	Cd:
	Are ca parametrii user-ul curent, calea catre
directorul unde se doreste mutarea si directorul curent.
	Daca nu se primeste un path, se parcurge ierarhia
de repository pana se ajunge in directorul home. Altfel
se parcurge path-ul cu delimitatori "/". Daca se
intalneste ".." cursorul dir se va pozitiona pe parintele
directorului curent, altfel se cauta denumirea in lista
de copii ai directorului curent si daca se gaseste se
pozitioneaza dir pe directorul gasit si se trece la
urmatorul token. Daca nu se poate executa mutarea se va
arunca o exceptie MyInvalidPathException.

---------------------------------------------------------

	CloudService:
	Serviciul de Cloud, contine 3 statii de stocare si
user-ul curent(necesar in initializarea MyNotEnoughSpace
Exception).
	Metode:
-upload: cloneaza un director si il salveaza pe statii
	Se verifica daca spatiul liber este sufiecient
pentru stocarea directorului, in caz contrar aruncandu-se
MyNotEnoughSpaceException.
	Daca exista spatiu suficient pe prima statie care
mai are spatiu, se cloneaza directorul cu totul si se
stocheaza.
	Altfel se transforma ierarhia dir intr-un vector
de repo-uri si se fac legaturile parinte-fiu pe vector.
Se reconstruieste dir cu primii fii din vector astfel
incat sa nu se depaseasca spatiul disponibil pe statie.
Fiii lui dir sunt pusi in vector prin parcurgere in
adancime, in preordine. Se parcurg apoi restul fiilor,
pentru a se realiza legaturile parinte-fiu cu tot cu
legaturile intre repo-uri de pe statii diferite(MachineId)
Daca repo-ul curent are parintele pe statia deja umpluta,
el se va uploada pe statia urmatoare. Daca este cazul se
trece pe a treia statie. La parcurgere se reconstruiesc
repo-urile de tip dir.

-sync: cauta mai intai statia pe care se afla dir, apoi
il reconstruieste si ii calculeaza recursiv dimensiunea.

-allFreeSpace: calculeaza spatiul liber total de pe Cloud

-searchAll: cauta un repo dupa nume pe tot Cloud-ul si
intoarce o referinta de tip MachineId catre acesta.

---------------------------------------------------------

	Command:
	Interfata din enunt care are cele trei metode
execute.

---------------------------------------------------------

	CommandFactory:
	Implementeaza FactoryPattern, cautand comanda de
sistem, corespunzatoare input-ului si transmite parametrii
necesari fiecarei comenzi. Parametrii pot fi fereastra
terminalului, user-ul curent, argumentele comenzii,
directorul curent si serviciul de Cloud.

---------------------------------------------------------

	Directory:
	Implementeaza interfata Repository.
	Constructorul primeste datele noului director si
verifica permisiunile parintelui, pentru a asigura
corectitudinea permisiunilor in ierarhie.
	Pentru fii exista o lista de repo-uri.

	Metode:

-accept: apeleaza metoda execute din interfata Command

-addChild: adauga repo-ul primit ca parametru la lista
de copii si recalculeaza dimensiunea directorului si
actualizeaza ierarhia.

-removeChild: sterge repo-ul primit ca parametru la lista
de copii si recalculeaza dimensiunea directorului si
actualizeaza ierarhia.

-toString: afiseaza numele directorului si daca primeste
parametru, afiseaza sub forma de tree

-toList: la fel ca toString dar afiseaza toate detaliile

-toLiniar: transforma un director intr-un vector de repo
prin parcurgere in adancime in preordine

-clone: cloneaza un director fara lista lui de copii

-cloneAll: cloneaza intreaga ierarhie a unui director,
verifica daca exista copii deja uploadati si daca exista
pune in locul lor un MachineId, de aceea primeste ca
parametru si Cloud-ul

-search: cauta dupa nume, un repo in ierarhia unui
director si intoarce pointer-ul acestuia

-rebuild: reconstruieste un director inlocuind in
ierarhia lui referintele de tip MachineId cu repo-ul
de pe statia corespunzatoare a Cloud-ului

-calculateSize: calculeaza dimensiunea totala a unui
director

-move: intoarce toate repo-urile care fac match pe
sirul de caractere primit ca parametru(inclusiv)

---------------------------------------------------------

	Echo:
	Fereastra de tip JDialog ce contine un JLabel cu
textul primit ca parametru de comanda "echo -POO".
Aceasta va aparea tot timpul in prim plan.

---------------------------------------------------------

	File:
	Implementeaza interfata Repository. Contine trei
constructori cu diferiti parametri. Verifica de asemenea
respectarea permisiunilor in ierarhie.

	Metode:

-accept: apeleaza execute din interfata Command

-toString: afiseaza numele fisierului si daca primeste
parametru, afiseaza sub forma de tree

-toList: la fel ca toString dar afiseaza toate detaliile

-clone: cloneaza un fisier cu toate detaliile aferente

---------------------------------------------------------

	Ls:
	Implementeaza interfata Command, mostenind clasa
abstracta ReadCommand.
	Primeste ca parametri fereastra terminalului,
user-ul curent si argumentele. Campurile rec, all si poo
corespund argumentelor -r, -a si -POO. Pentru File se
afiseaza numele sau detaliile acestuia, in functie de -a.
Pe Directory se afiseaza numai numele sau toate detaliile
pentru directorul curent sau toata ierarhia in functie de
-a si -r. Se verifica permisiunile de citire pentru
afisarea copiilor. In cazul -a -POO se afiseaza un JTable
cu toate detaliile fiecarui copil direct al lui dir.
Campul level este folosit pentru afisarea ierarhiei sub
forma de tree in cazul -r.

---------------------------------------------------------

	MachineId:
	Referinta pentru directoare si fisiere de pe
statii diferite legate parinte-copil. Are campurile: id
corespunde numarului statiei pe care se afla repo-ul,
child contine numele repo-ului. Contine metoda accept
deoarece implementeaza interfata Repository. Mai contine
metoda toString identica cu cea din cazul File si
Directory. Metodele toList apar deoarece au fost adaugate
in interfata Repository.

---------------------------------------------------------

	Mkdir:
	Implementeaza Command si extinde WriteCommand.
Contine fereastra, user-ul curent si argumentele. Mkdir
se executa pe directorul curent si daca acesta are
permisiune de scriere se adauga directoarele
corespunzatoare.

	Format:
mkdir dir1 ?permisiuni_dir1 dir2 ?permisiuni_dir2 etc

	Daca nu se primesc permisiuni directorul va avea
toate drepturile pentru toti user-i, campul owner din
perm ramanand null. Altfel permisiunile se aplica numai
user-ului care a dat comanda, restul nu au permisiuni.
Daca se primeste un nume se adauga fisierul iar daca este
urmat de permisiuni (-rw-) se modifica permisiunile.

---------------------------------------------------------

	MyInvalidPathException:
	Exceptia aferenta comenzii cd. Retine user-ul care
a dat comanda, directorul curent si argumentele comenzii,
precum si timpul.

---------------------------------------------------------

	MyNotEnoughSpaceException:
	Exceptia aferenta comenzii upload. Contine user-ul
care a dat comanda, dimensiunea directorului si timpul.

---------------------------------------------------------

	MyPathTooLongException:
	Exceptia aferenta comenzii pwd. Retine user-ul care
a dat comanda, directorul curent si argumentele comenzii,
precum si timpul. Extinde MyInvalidPathException.

---------------------------------------------------------

	Permission:
	Contine permisiunile unui repo precum si autorul
repo-ului(owner).

---------------------------------------------------------

	Pwd:
	Implementeaza Command si extinde ReadCommand.
Parametri: fereastra, user-ul curent si rezultatul.
Rezultatul se construieste prin parcurgerea ierarhiei din
directorul curent pana in directorul home si adaugarea
numelor directoarelor parcurse. Arunca exceptia
MyPathTooLongException daca rezultatul este mai lung de
255 de caractere.

---------------------------------------------------------

	ReadCommand:
	Clasa abstracta, implementeaza Command. Contine
metodele readPermission care intoarce permisiunea de
citire ale repoului primit ca parametru si inputToPath
care transforma o cale de forma dir1/dir2/file in 
dir1/dir2 pentru a se putea folosi comanda cd.

---------------------------------------------------------

	Repository:
	Interfata din enunt ce contine metoda
accept(Command), dar si metodele toString si toList cu si
fara parametru.

---------------------------------------------------------

	Rm:
	Contine fereastra, user-ul curent, argumentele si
o variabila booleana pentru parametrul -r. Pentru File se
verifica permisiunea de scriere si daca este true, se
sterge fisierul. Pentru Directory daca nu se da
parametrul -r, se verifica daca dir este gol si se sterge
sau se afiseaza mesajul "Directory not empty". Pentru -r
se sterge recursiv directorul, dar numai elementele din
ierarhie care au drept de scriere, iar parintii raman daca
nu se sterg toti copiii lor.

---------------------------------------------------------

	Singleton:
	Clasa din enunt, contine un camp de tip Directory
care retine directorul curent in programul principal.

---------------------------------------------------------

	Station:
	Extinde clasa abstracta StoreStation.
Initializeaza campurile specificate in StoreStation plus
numele statiei.

---------------------------------------------------------

	StoreStation:
	Clasa abstracta, contine un MachineId, capacitatea
de 10 KB si HashSet-ul care va stoca datele.

	Metode:

-store: adauga un repo la HashSet

-search: cauta un repo in HashSet, pe ierarhia fiecarui
director si intoarce adresa repo-ului cu numele cautat

-freeSpace: intoarce valoarea spatiului liber de pe statie

---------------------------------------------------------

	Sync:
	Implementeaza Command. Contine Cloud-ul, fereastra
si argumentul. Readuce dir la starea lui salvata pe Cloud.

---------------------------------------------------------

	Touch:
	Implementeaza Command si extinde WriteCommand.
Contine fereastra, user-ul curent si argumentele. Touch
se executa pe directorul curent si daca acesta are
permisiune de scriere se adauga fisierele
corespunzatoare.

	Format:
touch dir1 ?permisiuni_dir1 ?size1 ?type1 file2 etc

	Daca nu se primesc permisiuni fisierul va avea
toate drepturile pentru toti user-i, campul owner din
perm ramanand null. Altfel permisiunile se aplica numai
user-ului care a dat comanda, restul nu au permisiuni.
Daca se primeste un nume se adauga fisierul iar daca este
urmat de permisiuni (-rw-) se modifica permisiunile.

---------------------------------------------------------

	Upload:
	Implementeaza Command. Contine Cloud-ul, fereastra
si argumentul. Stocheaza dir pe Cloud, daca exista spatiu,
altfel, arunca exceptia MyNotEnoughSpaceException.

---------------------------------------------------------

	User:
	Clasa utilizatorilor, contine username, password,
firstname, lastname, timpul crearii si timpul ultimului
login. Metoda userinfo intoarce datele user-ului.

---------------------------------------------------------

	Userinfo:
	JList ce contine datele unui user.

---------------------------------------------------------

	WriteCommand:
	Clasa abstracta, implementeaza Command. Contine
metodele writePermission care intoarce permisiunea de
scriere ale repoului primit ca parametru si inputToPath
care transforma o cale de forma dir1/dir2/file in 
dir1/dir2 pentru a se putea folosi comanda cd.

---------------------------------------------------------

	TermW:
	Fereastra terminalului si practic main-ul temei.
Este un JFrame care implementeaza ActionListener. Dupa
setarile JFrame-ului, se initializeaza variabilele ce tin
de sistemul de fisiere:

	Se creeaza directorul home cu parintele null si
cursorul pentru ierarhia de fisiere current care se
initializeaza cu home.

	Se creeaza un vector de user-i la care se adauga
user-ii root si guest.

	Se creeaza single (Singleton) se initializeaza
campul single.user cu user-ul guest.

	Se creeaza o comanda pwd care va afisa path-ul
curent.

	Se creeaza serviciul de Cloud.

	Se creeaza un FileHandler care se aplica unui
Logger care va scrie in fisier exceptiile, login, logout.



	Interfata grafica:

	Se retine dimensiunea ferestrei.

	Se adauga un JPanel care va avea BoxLayout pe
verticala, apoi se adauga acesta intr-un JScrollPane.

	La JPanel-ul initial (act <- activity) se vor
adauga pentru fiecare comanda un nou Jpanel (p) care va
contine un JTextField care va afisa
user_curent@path_curent	si este needitabil si un al doilea
JTextField editabil care va avea focus si in care se va
scrie comanda de sistem.
	Pentru output-ul comenzilor exista metoda output
care adauga la act cate un nou JTextField needitabil
pentru fiecare linie din output-ul comenzii.
	Dupa apasarea tastei Enter se seteaza campul in
care s-a dat comanda ca fiind needitabil si apoi se adauga
un nou JPanel cu cele doua campuri de text, primul
needitabil.
	La fiecare apasare a tastei enter se muta
scrollbar-ul la cea mai joasa pozitie.
	Mai apar schimbari de culori ale Background-ului
campurilor de text.

	Metoda ActionPerformed:

	Preia comanda din JTextField-ul in, spoi se
parseaza inputul cu un StringTokenizer si se intra intr-un
switch cu primul cuvant din input:


	echo: daca primeste parametrul -POO creeaza o
fereastra JDialog ce va afisa textul primit ca parametru,
altfel afiseaza textul primit ca parametru.
	Format: echo ?-POO text


	newuser: adauga un user nou la vectorul de useri
	Format: newuser username password firstn lastn
	Trebuie sa primeasca toti cei 4 parametrii.

	userinfo: daca primeste parametrul -POO adauga un
JList de tip Userinfo la JPanel-ul act ce contine info
user-ului curent, altfel doar afiseaza info user-ului
curent.


	login: cauta dupa username in vectorul de useri si
daca gaseste user-ul (altfel afiseaza "User not found")
verifica daca parola aferenta este corecta (altfel
afiseaza "Incorrect Password"), single.user primeste
user-ul cu acest nume.(Singleton.user)
	Se afiseaza login-ul in fisierul Logger-ului.
	Format: login username password


	logout: single.user primeste user-ul guest.
	Se afiseaza logout-ul in fisierul Logger-ului.


	default:
	Se intra cu inputul in CommandFactory, se ia
comanda rezultata si se aplica pe:
-directorul curent pentru toate comenzile mai putin
"ls", rm, cat, upload si sync.
-pe toti fii directorului curent care fac match pe input
astfel se trateaza cazul expresiilor regulate de tip Java

	Daca comanda rezultata a fost "cd", cursorul de
tip Directory care retine directorul curent ia valoarea
campului cd.current pentru a se realiza mutarea.

	In cazul aparitiei exceptiilor acestea vor fi
preluate de Logger si afisate in fisierul "MyCloudLog".

	User-ul guest poate da numai comenzile echo,
newuser, userinfo, login si logout.

---------------------------------------------------------

	POO_TEMA2:
	Creeaza terminalul TermW cu numele "MyCloud".

---------------------------------------------------------

	LookAndFeel:
	Creeaza terminalul TermW cu numele "MyCloud
LookAndFeel". Seteaza si un nou Look and Feel, dar acesta
imi face probleme cu scrollbar-ul. Am download-at Classy
look and feel si l-am adaugat la Build Path-ul
proiectului.
pentru ca imi facea probleme