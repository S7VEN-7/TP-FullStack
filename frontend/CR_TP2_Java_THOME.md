# <div style="display: flex; justify-content: space-between; align-items: center;"> <img src="./public/img/logo-polytech-dijon.png" style="width : 5em; "> <div style="font-size : 0.6em; "><strong style="color:#0c4473">DEVELOPPEMENT D’APPLICATION WEB :</strong><strong style="color:#00adef"> JAVA – SERVEUR WEB</strong></div></div>

> Auteur : THOME Vincent

<br>

## <span style="color:#0c4473"><strong>INTRODUCTION & OBJECTIFS</strong></span>

Ce projet permettra de créer un serveur web en utilisant Java. Le serveur web sera capable de reproduire le comportement d'un serveur web réel en manipulant les sockets, les fichiers et en étant capable de gérer les requêtes et les réponses HTTP.

Et voici donc les objectifs à atteindre pour ce projet :
- Etudier et reproduire le comportement d’un serveur web,
- Manipuler les sockets,
- Manipuler les fichiers,
- Créer une application multithread.

<br>

## <span style="color:#0c4473"><strong>STRUCTURE DU PROJET</strong></span>


Nous verrons ici comment le projet a été structuré ainsi que les liens qui existent entre les différentes classes ultérieurement créées.

<br>

### <span style="color:#00adef"><strong>Organisation des classes</strong></span>

Dans le cadre de ce projet, permettant de simuler l'interaction ***client - serveur***, nous utiliserons une structure de classe appropriée qui est la suivante :

<br>

<div style="margin-left : 100px"> <img src="./img/Capture d’écran 2024-04-10 à 11.36.38.png" style="border-radius: 8px; width : 400px;">
</div>

<br>

### <span style="color:#00adef"><strong>Relations entre les classes</strong></span>

Voyons en détail ce que doit faire chaque classe et des liens qui les unissent :

- `HttpRequest` & `HttpResponse` : réception de la réponse du client et envoi du contenu au client (sur la page du navigateur),

- `HttpContext` : connexion entre le client et le serveur avec un accès aux éléments de chaque requête en ayant deux attributs de type `HttpRequest` & `HttpResponse`,

- `RequestProcessor`: traitement des requêtes à travers d'un attribut de type `HttpContext`,

- `WebServer` : gestion du processus de traitement en utlisant une instance de `RequestProcessor`,

- `WebServerApplication` : lancement du serveur en instanciant un objet de type `WebServer`. 

<br>

## <span style="color:#0c4473"><strong>LE CODE</strong></span>

Dans cette partie, qui est d'ailleurs la partie la plus fun, nous allons voir en détail chaque classe précédemment donnée.

D'ailleurs, deux points importants : 

- J'omettrai l'entête de chaque classe qui reste le même pour toutes, à savoir :
```java
public class nomClasse { ... }
```

- et aussi, tout le long de ce travail, on capturera les éventuelles exceptions avec un :

```java
try {
    ...
} catch (...) {
    ...
}
```

**A noter:** le code entier sera donné en annexe du document.

<br>

### <span style="color:#00adef"><strong>HttpRequest</strong></span>

Pour commencer, cette classe `HttpRequest` permettra de lire les requêtes reçues d'un ou plusieurs clients en récupérant donc la ***méthode*** ainsi que l'***url*** de celles-ci. 

<br>

#### <span style="color:#0c4473"><strong>Les librairies</strong></span>

Pour la classe `HttpRequest`, les librairies utilisées seront celles-ci :

```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
```

<br>

#### <span style="color:#0c4473"><strong>Les attributs</strong></span>

Simplement deux attributs seront nécessaires :

```java
private String method;
private String url;
```

<br>

#### <span style="color:#0c4473"><strong>Le constructeur (et getters)</strong></span>

Instancier un objet de `HttpRequest` se fera en appelant la méthode `readClientSocket()` que l'on verra par la suite. Et pour les getters, ils nous seront utiles pour plus tard pour nous donner la ***méthode*** et l'***url*** de chaque requête. 

```java
public HttpRequest(Socket socket) {
    readClientRequest(socket);
}

public String getMethod() {
    return this.method;
}

public String getUrl() {
    return this.url;
}
```

<br>

#### <span style="color:#0c4473"><strong>La méthode</strong></span>

Voici donc la méthode `readClientRequest()`:

```java
private void readClientRequest(Socket socket) {
    try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = reader.readLine();
        String[] informationsHttp = line.split(" ");
        
        this.method = informationsHttp[0];
        this.url = informationsHttp[1];
        String versionHttp = informationsHttp[2];

    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

Elle permettra de récupérer tous les éléments des requêtes via la variable : `BufferedReader reader` et grâce à l'instruction suivante, on récupère simplement la 1ère ligne qui contient les informations utiles pour nous (la ***méthode*** et l'***url***). Ensuite, avec la méthode `split()` on sépare chaque élément de la ligne séparé par un espace et on les associe à nos attributs.

<br>

### <span style="color:#00adef"><strong>HttpResponse</strong></span>

Celle-ci permettra de donner une réponse appropriée (avec un format adéquat) aux différents clients qui ont pu interroger le serveur.

<br>

#### <span style="color:#0c4473"><strong>Les librairies</strong></span>

Pour la classe `HttpResponse`, on aura besoin de toutes ces librairies pour pouvoir faire fonctionner le code dans celle-ci. Les voici :

```java
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.net.Socket;
import java.io.File;
import java.util.Scanner;
import java.io.FileInputStream;
import java.net.SocketException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
```

(**Remarque :** les deux dernières serviront seulement à pouvoir afficher le temps ou plutôt la date précise des requêtes. Cela reste un *plus* pour moi, me permettant de vérifier le fonctionnement de ce travail.)
    
<br>

#### <span style="color:#0c4473"><strong>L'attribut</strong></span>

Même si c'est sans doute la classe la plus louurde, elle ne possède qu'un seul attribut qui permettra de faire la réponse adéquate. Le voici :

```java
private OutputStream output;
```

<br>

#### <span style="color:#0c4473"><strong>Le constructeur</strong></span>

Pour celle-ci, le constructeur permettra d'attribuer (à chaque instance créée) à l'attribut un `socket` permetant d'établir une réponse.

```java
public HttpResponse(Socket socket) {
    try {
        this.output = socket.getOutputStream();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

<br>

#### <span style="color:#0c4473"><strong>Les méthodes</strong></span>


Commençons d'abord par les deux méthodes principales de cette classe qui sont `ok(...)` et `notFound(...)`. 

```java
public void ok(String message, String contentType, String fileName) {
    try {
        System.out.println("HTTP/1.1 200 " + message + " -> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")));
        sendFile(contentType, fileName);
        // sendResponse(contentType, message);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void notFound(String message) {
    try {
        System.out.println("HTTP/1.1 404 " + message + " -> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")));
        this.output.write("HTTP/1.1 404 Not Found \r\n".getBytes());
        this.output.write("Content-Type: text/html; charset=utf-8\r\n".getBytes());
        this.output.write("\r\n".getBytes());
        this.output.write(("<strong>" + message + "</strong>").getBytes());
        this.output.flush();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

Pour ces deux méthodes, la 1ère instruction sert simplement à afficher la date de la requête avec un format de la date adapté.
La méthode `ok(...)` par la suite appelera la méthode `sendFile(...)` que l'on verra juste après et dans un même temps on verra la méthode `sendResponse(...)` qui nous avait servi au début pour comprendre la structure de la réponse à donner.
En ce qui concerne la suite de la méthode `notFound(...)`, on utilisera la méthode `write(...)` pour écrire une réponse avec la bonne version '**HTTP/1.1**', le bon format '**text/html**' et à la fin, on peut envoyer (écrire) le message souhaité au format (ici ***HTML***).


Regardons donc dans un 2ème temps la méthode `sendResponse(...)` :

```java
public void sendResponse(String contentType, String message) {
    try {
        this.output.write("HTTP/1.1 200 OK\r\n".getBytes());
        this.output.write(("Content-Type: " + contentType + "; charset=utf-8\r\n").getBytes());
        this.output.write("\r\n".getBytes());

        this.output.write(("<strong>" + message + "</strong>").getBytes());
        this.output.flush();

    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

Les instructions dans cette méthode ressemblent très fortement à celles dans la méthode `notFound(...)`, sauf la partie sur le type de réponse qui sera renvoyé et comme ici on a utilisé cette méthode simplement pour envoyer une réponse de type suucès alors nous n'aurons pas '**HTTP/1.1 404 Not Found \r\n**' mais bien '**HTTP/1.1 200 OK\r\n**'.

Maintenant, attaquons nous à la méthode `sendFile(...)` :

```java
public void sendFile(String contentType, String fileName) {
    File file = new File(fileName);

    if (file.exists()) {
        try {
            this.output.write(("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + file.length() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n").getBytes());

            FileInputStream input = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                try {
                    this.output.write(buffer, 0, bytesRead);
                } catch (SocketException e) {
                    // Le client a probablement fermé la connexion, donc on arrête d'écrire
                    break;
                }                }
            this.output.flush();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
        notFound("Not Found");
    }
}
```

Cette méthode vérifie dans un 1er temps si le fichier existe, et si c'est le cas, elle envoie une réponse '**HTTP 200 OK**' avec le type de contenu '`Content-Type`', la longueur du fichier '`Content-Length`', et indiquera que la connexion sera fermée après la réponse '`Connection: close`'.

Avec la variable ***input*** de type `FileInputStream`, on pourra lire le contenu du fichier. Quant à la variable ***buffer***, elle coupera le fichier (qui sera envoyé) en morceaux de 4096 octets (ou moins si la taille du fichier est plus petit) que l'on lira. Et chacun de ces morceaux sera écrit dans l'attribut `output`.
Et à la toute fin, on oubliera de vider le flux de sortie (`output`) et de fermer évidemment l'`input`.

<br>

### <span style="color:#00adef"><strong>HttpContext</strong></span>

Partons maintenant sur la classe `HttpContext` qui fait le lien entre `HttpRequest` et `HttpResponse`.


<br>

#### <span style="color:#0c4473"><strong>Les librairies</strong></span>

On utilisera seulement pour cette classe : 

```java
import java.io.IOException;
import java.net.Socket;
```

<br>

#### <span style="color:#0c4473"><strong>Les attributs</strong></span>

Cette fois-ci on va avoir trois attributs, un de type `Socket` et les deux autres de type `HttpRequest` et `HttpResponse` faisant le lien entre ces classes. Les voici : 

```java
private Socket socket;
private HttpRequest request;
private HttpResponse response;
```

<br>

#### <span style="color:#0c4473"><strong>Le constructeur (et getters)</strong></span>

```java
public HttpContext(Socket socket) {
    this.socket = socket;
    this.request = new HttpRequest(socket);
    this.response = new HttpResponse(socket);
}

public HttpRequest getRequest() {
    return this.request;
}

public HttpResponse getResponse() {
    return this.response;
}
```

Ici le constructeur donnera à l'attribut `socket` le paramètre `socket`donné dans celui-ci, ensuit il appellera à la suite les constructeurs de `HttpRequest` et `HttpResponse` pour les associer aux attributs `request` et `response`. Et pour les getters, ils renvoient les attributs.

<br>

#### <span style="color:#0c4473"><strong>La méthode</strong></span>
    
Afin de remplir complètement cette classe, il nous faut la méthode `close()` qui fermera simplement le `socket` de `HttpContext`. La voici :

```java
public void close() {
    try {
        this.socket.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

<br>

### <span style="color:#00adef"><strong>RequestProcessor</strong></span>

Regardons ici la classe permettant de traiter les différentes requêtes, à savoir `RequestProcessor`.

<br>

#### <span style="color:#0c4473"><strong>Les librairies</strong></span>

Dans celle-ci, il sera utilisé trois librairies : 

```java
import java.net.Socket;
import java.io.File;
import java.io.IOException;
```

<br>

#### <span style="color:#0c4473"><strong>L'attribut</strong></span>

On aura besoin uniquement ici d'un seul attribut qui liera cette classe avec `HttpContext`. Le voici :

```java
private HttpContext context;
```

<br>

#### <span style="color:#0c4473"><strong>Le constructeur</strong></span>

L'instanciation des objets de type `RequestProcessor` se fera par l'appel du constructeur de `HttpContext` et de la méthode `process()` que l'on va voir. Voici donc à quoi il ressemble :

```java
public RequestProcessor(Socket socket) {
    this.context = new HttpContext(socket);
    process();
}
```

<br>

#### <span style="color:#0c4473"><strong>Les méthodes</strong></span>

Commençons par une méthode qui n'était pas demandée mais qui servira de donner, pour chaque type de fichier (selon l'extension), le `ContentType` associé. La voici :

```java
private String getContentType(String fileExtension) {
    switch (fileExtension) {
        case "html":
            return "text/html";
        case "css":
            return "text/css";
        case "png":
            return "image/png";
        case "jpg":
            return "image/jpg";
        case "webp":
            return "image/webp";
        case "svg":
            return "image/svg+xml";
        case "mp4":
            return "video/mp4";
        default:
            return "text/plain";
    }
}
```

Passons maintenant à la méthode principale `process()` :

```java
    private void process() {
        try {
            HttpRequest request = context.getRequest();
            HttpResponse response = context.getResponse();

            String url = request.getUrl();
            String fileExtension = url.substring(url.lastIndexOf(".") + 1);

            if (url.equals("/")) {
                response.ok("OK", "text/html", "./public/index.html");
            }
            else {
                File urlFile;

                if (url.contains("/img/") || url.contains("/videos/")) {
                    url = "/" + url.split("/")[2];
                }
                
                if (fileExtension.equals("mp4")) {
                    urlFile = new File("./public/videos" + url);
                    url = "/videos" + url;
                }
                else if (fileExtension.equals("jpg") || fileExtension.equals("png") || fileExtension.equals("webp") || fileExtension.equals("svg")){
                    urlFile = new File("./public/img" + url);
                    url = "/img" + url;
                }
                else {
                    urlFile = new File("./public" + url);
                }

                if (!urlFile.exists()) {
                    response.notFound("Not Found");
                } else {
                    String contentType = getContentType(fileExtension);
                    response.ok("OK", contentType, "./public" + url);
                }
            }
            context.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
``` 

Dans cette méthode, on récupèrera, pour chaque fichier demandé, l'extension associée. Et on fera des comparaisons pour savoir s'il s'agit ou non de la racine uniquement '`/`'. Si ce n'était pas le cas, on fera ensorte d'associer le bon chemin pour atteindre le fichier demandé. Mais si jamais le fichier n'existe pas, on appellera alors la méthode `notFound(...)` ou sinon quand le fichier existe on s'occupe de récupérer ce qu'il nous faut (le ***ContentType*** et de l'***url*** de chaque fichier) et d'envoyer une réponse avec la méthode `ok(...)`.

<br>

#### <span style="color:#0c4473"><strong>Ajout de la classe RequestProcessing</strong></span>

Afin de traiter au mieux les requêtes et avec un temps réduit, on implémente l'interface `Runnable` grâce à la classe `RequestProcessing`.

```java
class RequestProcessing implements Runnable {
    private Socket socket;

    public RequestProcessing(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        new RequestProcessor(socket);
    }
}
```

Cette structure a été vue entièrement en cours, je l'ai donc simplement reprise.

<br>

### <span style="color:#00adef"><strong>WebServer</strong></span>

Pour l'avant dernière classe, on va voir qu'elle permettra de démarrer le serveur en instanciant un objet de `RequestProcessing` qui créera un thread pour chaque nouvelle connexion (requête), ce qui permettra de réduire le délai de traitement des requêtes au lieu de les traiter une par une.

<br>

#### <span style="color:#0c4473"><strong>Les librairies</strong></span>

Voici les librairies que l'on a utilisées dans cette classe : 

```java
import java.net.Socket;
import java.util.List;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
```

<br>

#### <span style="color:#0c4473"><strong>La méthode</strong></span>

La seule méthode de cette classe créera une instance de `serverSocket` sur le port choisi. Par la suite, dès qu'il y a une nouvelle connexion, un thread est créé. Au niveau du code, cela donne :

```java
public void run(int portNumber) {
    ServerSocket serverSocket = null;
    try {
        serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is listening on port " + portNumber);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            
            RequestProcessing request = new RequestProcessing(clientSocket);
            Thread thread = new Thread(request);
            thread.start();
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

<br>

### <span style="color:#00adef"><strong>WebServerApplication</strong></span>

Afin de lancer complètement lancer le serveur, il nous faut cette dernière classe qui utilisera une instance de `WebServerApplication` pour le faire.

Et voici donc le contenu de la classe :

```java
public class WebServerApplication {
    public static void main(String[] args) {     
        WebServer webServer = new WebServer();
        webServer.run(80);
    }
}
```

Ici, l'instance est créée et ensuite on appelle la méthode `run()` de `WebServer` sur le port 80.

<br>

## <span style="color:#0c4473"><strong>TESTS & VALIDATION</strong></span>

Une fois que toutes les classes précédemment vues sont bien créées, on peut passer à la partie des tests et voir si tout fonctionne correctement.

<br>

### <span style="color:#00adef"><strong>Lancement du serveur</strong></span>

Pour ce faire, on compile tous les fichiers : 

```java
javac *.java     
```

Et on lance le serveur en exécutant cette commande :

```java
java WebServerApplication
```

<br>

### <span style="color:#00adef"><strong>Serveur en fonctionnement</strong></span>

#### <span style="color:#0c4473"><strong>A la racine</strong></span>

Maintenant, on passe sur le navigateur et on tape simplement `localhost` et cela devrait nous afficher la page ***HTML*** que l'on a faite (voir TD1 DevWeb). 

On repasse du côté du serveur (au niveau de la console de *Visual Studio Code* par exemple) et on remarque ces 7 requêtes : 

```
Server is listening on port 80
HTTP/1.1 200 OK -> 15:59:46.155648
HTTP/1.1 200 OK -> 15:59:46.187088
HTTP/1.1 200 OK -> 15:59:46.191180
HTTP/1.1 200 OK -> 15:59:46.205835
HTTP/1.1 200 OK -> 15:59:46.208393
HTTP/1.1 200 OK -> 15:59:46.211546
HTTP/1.1 200 OK -> 15:59:46.225418
```

La première c'est pour la page ***HTML***, après on a celle du ***CSS***, ensuite les requêtes pour les images (***png***, ***jpg***, ***svg***, ***webp***) et la dernière pour la vidéo (***mp4***).

<br>

#### <span style="color:#0c4473"><strong>Avec un fichier</strong></span>

Si cette fois, on tape dans le navigateur : `localhost/logo-polytech-dijon.svg` par exemple. Et le client (le navigateur) recevra alors non plus la page ***HTML*** mais bien simplement l'image choisie, si bien évidemment celle-ci existe.
Au niveau de la console, on remarquera que pour n'importe quelle image, nous aurons bien une seule requête :

```
Server is listening on port 80
HTTP/1.1 200 OK -> 16:14:57.923097
```

Mais pour une vidéo, nous en aurons deux :

```
Server is listening on port 80
HTTP/1.1 200 OK -> 16:16:01.428547
HTTP/1.1 200 OK -> 16:16:01.497888
```

Et je ne savais pas vraiment pourquoi il y avait deux requêtes donc j'ai demandé à Copilot et selon lui :

> La première requête est pour obtenir les métadonnées de la vidéo (comme la durée, les dimensions, etc.). Le navigateur n'a pas besoin de télécharger l'ensemble du fichier pour cela, il demande donc généralement seulement le début du fichier.

> Et la deuxième requête est pour télécharger la vidéo elle-même. Cette requête est généralement une requête de type "range", où le navigateur demande des parties spécifiques de la vidéo à mesure qu'il en a besoin. Cela permet au navigateur de commencer à lire la vidéo avant que tout le fichier ne soit téléchargé, et de chercher à différentes parties de la vidéo sans avoir à télécharger tout le contenu intermédiaire.

<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>

## <span style="color:#0c4473"><strong>ANNEXE</strong></span>

Comme promis, voici le code entier pour chacune des classes.

<br>

### <span style="color:#00adef"><strong>HttpRequest</strong></span>

```java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    private String method;
    private String url;

    private void readClientRequest(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            String[] informationsHttp = line.split(" ");
            
            this.method = informationsHttp[0];
            this.url = informationsHttp[1];
            String versionHttp = informationsHttp[2];

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpRequest(Socket socket) {
        readClientRequest(socket);
    }

    public String getMethod() {
        return this.method;
    }

    public String getUrl() {
        return this.url;
    }
}
```

<br>

### <span style="color:#00adef"><strong>HttpResponse</strong></span>

```java
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.net.Socket;
import java.io.File;
import java.util.Scanner;
import java.io.FileInputStream;
import java.net.SocketException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class HttpResponse {
    
    private OutputStream output;

    public HttpResponse(Socket socket) {
        try {
            this.output = socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ok(String message, String contentType, String fileName) {
        try {
            System.out.println("HTTP/1.1 200 " + message + " -> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")));
            sendFile(contentType, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notFound(String message) {
        try {
            System.out.println("HTTP/1.1 404 " + message + " -> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")));
            this.output.write("HTTP/1.1 404 Not Found \r\n".getBytes());
            this.output.write("Content-Type: text/html; charset=utf-8\r\n".getBytes());
            this.output.write("\r\n".getBytes());
            this.output.write(("<strong>" + message + "</strong>").getBytes());
            this.output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendResponse(String contentType, String message) {
        try {
            this.output.write("HTTP/1.1 200 OK\r\n".getBytes());
            this.output.write(("Content-Type: " + contentType + "; charset=utf-8\r\n").getBytes());
            this.output.write("\r\n".getBytes());

            this.output.write(("<strong>" + message + "</strong>").getBytes());
            this.output.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String contentType, String fileName) {
        File file = new File(fileName);

        if (file.exists()) {
            try {
                this.output.write(("HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + file.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes());

                FileInputStream input = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    try {
                        this.output.write(buffer, 0, bytesRead);
                    } catch (SocketException e) {
                        // Le client a probablement fermé la connexion, donc on arrête d'écrire
                        break;
                    }                }
                this.output.flush();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            notFound("Not Found");
        }
    }
}
```

<br>

### <span style="color:#00adef"><strong>HttpContext</strong></span>

```java
import java.io.IOException;
import java.net.Socket;

public class HttpContext {
    
    private Socket socket;
    private HttpRequest request;
    private HttpResponse response;


    public HttpContext(Socket socket) {
        this.socket = socket;
        this.request = new HttpRequest(socket);
        this.response = new HttpResponse(socket);
    }

    public HttpRequest getRequest() {
        return this.request;
    }

    public HttpResponse getResponse() {
        return this.response;
    }
    
    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

<br>

### <span style="color:#00adef"><strong>RequestProcessor</strong></span>

```java
import java.net.Socket;
import java.io.File;
import java.io.IOException;

public class RequestProcessor {
    
    private HttpContext context;

    private String getContentType(String fileExtension) {
        switch (fileExtension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "png":
                return "image/png";
            case "jpg":
                return "image/jpg";
            case "webp":
                return "image/webp";
            case "svg":
                return "image/svg+xml";
            case "mp4":
                return "video/mp4";
            default:
                return "text/plain";
        }
    }

    private void process() {
        try {
            HttpRequest request = context.getRequest();
            HttpResponse response = context.getResponse();

            String url = request.getUrl();
            String fileExtension = url.substring(url.lastIndexOf(".") + 1);

            if (url.equals("/")) {
                response.ok("OK", "text/html", "./public/index.html");
            }
            else {
                File urlFile;

                if (url.contains("/img/") || url.contains("/videos/")) {
                    url = "/" + url.split("/")[2];
                }
                
                if (fileExtension.equals("mp4")) {
                    urlFile = new File("./public/videos" + url);
                    url = "/videos" + url;
                }
                else if (fileExtension.equals("jpg") || fileExtension.equals("png") || fileExtension.equals("webp") || fileExtension.equals("svg")){
                    urlFile = new File("./public/img" + url);
                    url = "/img" + url;
                }
                else {
                    urlFile = new File("./public" + url);
                }

                if (!urlFile.exists()) {
                    response.notFound("Not Found");
                } else {
                    String contentType = getContentType(fileExtension);
                    response.ok("OK", contentType, "./public" + url);
                }
            }
            context.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RequestProcessor(Socket socket) {
        this.context = new HttpContext(socket);
        process();
    }
}

class RequestProcessing implements Runnable {
    private Socket socket;

    public RequestProcessing(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        new RequestProcessor(socket);
    }
}
```

<br>

### <span style="color:#00adef"><strong>WebServer</strong></span>

```java
import java.net.Socket;
import java.util.List;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebServer {
    
    public void run(int portNumber) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                
                RequestProcessing request = new RequestProcessing(clientSocket);
                Thread thread = new Thread(request);
                thread.start();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

<br>

### <span style="color:#00adef"><strong>WebServerApplication</strong></span>

```java
public class WebServerApplication {
    public static void main(String[] args) {     
        WebServer webServer = new WebServer();
        webServer.run(80);
    }
}
```