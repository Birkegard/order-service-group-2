# Order Service

Order Service är en Spring Boot-mikrotjänst för att skapa och hämta ordrar. Den lagrar orderdata i PostgreSQL,
kontrollerar och reserverar produktlager via Product Service och hanterar betalningsbekräftelser via RabbitMQ.

## Funktioner

- Skapar ordrar med en eller flera produkter.
- Hämtar produktinformation och minskar lagersaldot via Product Service.
- Beräknar orderns totalpris och sparar ordern med status `PENDING`.
- Hämtar betalningsinformation för en order som tillhör den inloggade användaren.
- Tar emot betalningsbekräftelser via RabbitMQ. När betalningen har status `COMPLETED` ändras orderstatus till
  `COMPLETED` och orderbekräftelse skickas till RabbitMQ.
- Skyddar API:et med JWT. API-dokumentationen är tillgänglig utan autentisering.

## Tech stack

- Java 17
- Spring Boot 4.0.6
- Spring MVC och Spring Security OAuth2 Resource Server
- Spring Data JPA och PostgreSQL via Neon
- RabbitMQ
- Springdoc OpenAPI/Swagger UI
- Maven

## Förutsättningar

För att köra tjänsten behöver du:

- Java 17
- Maven, eller projektets Maven Wrapper
- En PostgreSQL-databas
- En RabbitMQ-instans med TLS aktiverat
- En tillgänglig Product Service
- En autentiseringstjänst som publicerar JWT-nycklar på `/auth/jwks`

Tjänsten förväntar sig att dessa beroenden går att nå vid uppstart och användning.

## Konfiguration

```Markdown
Konfigurationen läses från miljövariabler. Ange följande innan du startar tjänsten:
| Miljövariabel | Krävs | Beskrivning |
| --- | --- | --- |
| `SERVER_PORT` | Nej | Port för HTTP-servern. Standard är `9000`. |
| `PRODUCT_SERVICE_BASE_URL` | Ja | Product Services bas-URL. Tjänsten anropar `/api/products/decrease-stock` på denna
adress. |
| `AUTH_SERVER` | Ja | Autentiseringstjänstens bas-URL. Tjänsten hämtar JWT-nycklar från `{AUTH_SERVER}/auth/jwks`. |
| `DB_URL` | Ja | JDBC-URL till PostgreSQL-databasen. |
| `DB_USERNAME` | Ja | Databasanvändare. |
| `DB_PASSWORD` | Ja | Databaslösenord. |
| `RABBIT_HOST` | Ja | RabbitMQ-värdnamn. |
| `RABBIT_PORT` | Ja | RabbitMQ-port. Använd en port som stöder TLS. |
| `RABBIT_USERNAME` | Ja | RabbitMQ-användare. |
| `RABBIT_PASSWORD` | Ja | RabbitMQ-lösenord. |
| `RABBIT_VIRTUAL_HOST` | Ja | RabbitMQ virtual host. |
```

Exempel på miljövariabler för en lokal utvecklingsmiljö:

```dotenv
SERVER_PORT=9000
PRODUCT_SERVICE_BASE_URL=http://localhost:8080
AUTH_SERVER=http://localhost:8081
DB_URL=jdbc:postgresql://localhost/orderdb
DB_USERNAME=orderuser
DB_PASSWORD=orderpassword
RABBIT_HOST=localhost
RABBIT_PORT=5671
RABBIT_USERNAME=orderuser
RABBIT_PASSWORD=orderpassword
RABBIT_VIRTUAL_HOST=ordervhost
```

Värdena ovan är exempel. Byt ut dom mot värden som passar din miljö. RabbitMQ-anslutningen har TLS aktiverat i tjänstens
konfiguration.

## Köra lokalt

Med Maven Wrapper på Windows (PowerShell):

```PowerShell
.\mvnw spring-boot:run
```

Med Maven Wrapper på macOS eller Linux (Bash):

```Bash
./mvnw spring-boot:run
```

Du kan också bygga och starta JAR-filen (Bash):

```Bash
./mvnw clean package
java -jar target/order-service-group-2-0.0.1-SNAPSHOT.jar
```

Se till att miljövariabler och beroendetjänster är konfigurerade innan du startar applikationen.

## Docker

Bygg först JAR-filen och skapa sedan Docker-imagen (Bash):

```Bash
./mvnw clean package
docker build -t order-service-group-2:local .
```

Starta containern med miljövariabler konfigurerade i din miljö (Bash):

```Bash
docker run --rm -p 9000:9000 --env-file .env order-service-group-2:local
```

## API

Alla order-endpoints kräver en giltig JWT som skickas som en bearer-token:

```Http
Authorization: Bearer <token>
```

JWT:n måste innehålla en roles-claim för rollskyddade endpoints. Ordern kopplas till JWT:ns sub-claim.

### Skapa en order

```Http
POST /order
Content-type: application/json
Authorization: Bearer <token>
```

Exempel på begäran:

```JSON
{
  "items": [
    {
      "id": 42,
      "quantity": 2
    },
    {
      "id": 73,
      "quantity": 1
    }
  ]
}
```

items måste finnas och innehålla minst en produkt. Varje produkt måste ha ett id, och quantity måste vara minst 1.

Vid skapandet skickar tjänsten produkt-ID och antal till Product Service för att minska lagersaldot. Bearer-tokenen
vidarebefordras till det anropet. Produktinformationen som Product Service returnerar används för att spara orderraderna
och beräkna totalpriset.

Ett lyckat anrop returnerar 201 created, exempelvis:

```JSON
{
  "id": 123,
  "customerName": "user-identifier",
  "items": [
    {
      "id": 42,
      "name": "Exempelprodukt",
      "description": "Produktbeskrivning",
      "price": 99.50,
      "quantity": 2
    }
  ],
  "totalPrice": 199.00
}
```

Ordern sparas initialt med statusen `PENDING`. customerName hämtas från JWT:ns sub-claim.

### Hämta en order för betalning

```Http
GET /order/{id}
Authorization: Bearer <token>
```

Endpointen returnerar orderns betalningsuppgifter om ordern tillhör användaren och fortfarande har statusen `PENDING`.

Exempel på svar:

```JSON
{
  "id": 123,
  "amount": 199.00,
  "currency": "SEK",
  "status": "PENDING"
}
```

Användaren måste vara autentiserad och JWT:ns sub måste matcha orderns kund. En order som inte längre är `PENDING` kan
inte hämtas för betalning.

### Valideringsfel

Ogiltiga fält i en orderbegäran returneras som 400 Bad Request med ett JSON-objekt med fältnamn och
valideringsmeddelande, exempelvis:

```JSON
{
  "items": "Items can not be empty"
}
```

## Autentisering och åtkomst

Tjänsten fungerar som en JWT-skyddad OAuth2 Resource Server och verifierar JWT:er med nycklar från
`{AUTH_SERVER}/auth/jwks`.

* `POST /order` kräver rollen `ADMIN` eller `USER`
* `GET /order/{id}` kräver autentisering. Användaren får bara hämta en order vars customerName matchar JWT:ens sub.
* `/swagger-ui/**`, `/swagger-ui.html` och `/v3/api-docs/**` är öppna för alla användare.
* CORS är konfigurerat för http://localhost:5173.

## Integrationer och orderflöde

### Product Service

Vid skapande av en order skickas ett POST-anrop till:

```Plain text
{PRODUCT_SERVICE_BASE_URL}/api/products/decrease-stock
```

Anropet innehåller en lista med produkt-ID och antal. Tjänsten förväntar sig en lista med produktinformation som
innehåller id, name, description, price och quantity.

### RabbitMQ

Tjänsten deklarerar två köer:

* Payment-queue: tar emot betalningsbekräftelser.
* Order-queue: skickar orderbekräftelser efter genomförd betalning.

Meddelandena använder JSON. Endast betalningsbekräftelser med statusen `COMPLETED` gör att ordern markeras som betald.
Andra betalningsstatusar ignoreras.

### Databas

Order och orderrader sparas i PostgreSQL. Hibernate är konfigurerat med ddl-auto=update, vilket innebär att
databasschemat uppdateras automatiskt vid start. För produktionslösning bör schemamigrering hanteras med lämplig
lösning.

Orderstatusar som används av modellen:

* `PENDING`
* `COMPLETED`
* `FAILED`
* `CANCELLED`

## API-dokumentation

När tjänsten körs finns Swagger-ui på:

```Plain text
http://localhost:9000/swagger-ui/index.html
```

OpenAPI-specifikationen finns på:

```Plain text
http://localhost:9000/v3/api-docs
```

Om du har ändrat server-port använder du den porten istället för 9000.

## CI och Docker-publicering

GitHub Actions-workflowen .github/workflows/workflow.yml bygger projektet med Java 17 och Maven vid push eller pull
request mot main. Workflowen publicerar även en Docker-image med taggen birkegard/order-service-group-2:latest och
kräver secreten DOCKERHUB_TOKEN.