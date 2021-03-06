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
- GET `https://link.samifying.com/api/status/<hostname>/<port>` - Returns the minecraft server status based on the
  hostname and port
- GET `http://link.samifying.com/api/lookup/<username>` - Returns a discord user from a minecraft username

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

Few request arguments are available, one of theme is `role`. With this query parameter you can specify witch role should be used for authentication. The only valid format is role id.
Example url would be: `https://link.samifying.com/api/user/06805a4280d0463dbf7151b1e1317cd4?role=712696664606900265`
By default if the role parameter is missing it will be set to `Media (ID: 426156903555399680)`

List of all query params:
- `guild` - Discord server ID that is required for auth
- `role` - Role id that is used for whitelisting
- `staff` - Staff role id that is used to identify staff members
- `supporter` - Channel id for server supporters

> Default values of all query parameters are set to work the best with [Sami's Hidey Hole](discord.gg/samifying)

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
- `Required role: ROLE_NAME` - The user has verified but does not have the required role
- `You are banned from this server` - You are banned :(

### Libraries used

- Spring Boot
- Spring Data JPA
- [JDA](https://github.com/DV8FromTheWorld/JDA)
- [MySQL Connector J](https://github.com/mysql/mysql-connector-j)
- [Google GSON](https://github.com/google/gson)
- [DNSJava](https://github.com/dnsjava/dnsjava)
- [ServerPing](https://github.com/Pequla/ServerPing)
- [Jackson](https://github.com/FasterXML/jackson-databind)
- [Lombok](https://projectlombok.org/)