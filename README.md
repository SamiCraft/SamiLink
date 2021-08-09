# SamiLink

Backend application written in Java using Spring Boot Framework. This application handles whitelisting on the server. It
stores and manages Discord and Minecraft account links.

### Documented Endpoints:

- GET `https://link.samifying.com/api/data` - Returns all the data stored in the database
- GET `https://link.samifying.com/api/data/<id>` - Returns a specific record from the databased based on the record id
- GET `https://link.samifying.com/api/data/discord/<id>` - Returns a specific record from the databased based on the
  user's Discord id
- GET `https://link.samifying.com/api/status` - Returns the HideySMP server status
- GET `https://link.samifying.com/api/status/<hostname>` - Returns the minecraft server status based on the hostname (
  with SRV lookup)
- GET `https://link.samifying.com/api/status/<hostname>/<port>` - Returns the minecraft server stauts based on the
  hostname and port

> Note that the JSON response of the `/api/status` endpoints is represented as the `StatusResponse` object of the [ServerPing](https://github.com/Pequla/ServerPing) library. If the server is offline or is not found response code will be HTTP 404 Not Found

### The `/api/user` endpoint

The following endpoint is called by the plugin in order to check if the user is allowed to join the server. It also
returns all the required permissions a user should have when they are on the server. The only parameter it takes is the
user's (or in this case player's) unique user id or in short uuid:

- GET `https://link.samifying.com/api/user/<uuid>`

#### Example Response:

- GET `https://link.samifying.com/api/user/06805a4280d0463dbf7151b1e1317cd4`

```json
{
  "id": "358236836113547265",
  "name": "Pequla#3038",
  "nickname": "Pequla (ProNoob2016)",
  "avatar": "https://cdn.discordapp.com/avatars/358236836113547265/0c90142668c10ac4ab71f3bc0292dcef.png",
  "supporter": true,
  "moderator": true
}
```

If the user's access is not permitted the backend will respond with a `403 Forbidden`

This is the example response in that case:

- GET `https://link.samifying.com/api/user/21ea77c8be294f49af2a4ece5cd20886`

```json
{
  "name": "com.samifying.link.error.LoginRejectedException",
  "message": "You are not verified",
  "path": "/api/user/21ea77c8be294f49af2a4ece5cd20886"
}
```

#### Error messages

- `You are not verified` - Minecraft Discord account link was not found in the database
- `Discord server not found` - Discord server got deleted or the bot is no longer its member
- `You are not a Discord server member` - The user has verified but is no longer a Discord server member.
- `You are not level 5 on the Discord server` - The user has verified but does not have a `Bronze I` role

### Libraries used

- Spring Boot
- Spring Data JPA
- [JDA](https://github.com/DV8FromTheWorld/JDA)
- [MySQL Connector J](https://github.com/mysql/mysql-connector-j)
- [Google GSON](https://github.com/google/gson)
- [DNSJava](https://github.com/dnsjava/dnsjava)
- [ServerPing](https://github.com/Pequla/ServerPing)