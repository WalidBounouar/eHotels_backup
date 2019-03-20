# eHotels

hotels

Possible concerns when migrating to hosted DB:
- version of driver in pom.xml
- driver used in jdbc. Aka "com.mysql.cj.jdbc.Driver" vs "com.mysql.jdbc.Driver"

Remember to do (for Walid):
- [X] Trigger (or other mechanism) to enforce that the manager of a hotel needs to have 'manager' as a role.
- [ ] Trigger (or other mechanism) to enforce only one session per LoginCred
- [ ] Caching for sessions

Endpoints:

Login stuff
- [X] Login
- [X] Register (client only)
- [ ] Sign out

Booking/Renting stuff common (differences client/employee at UI level)
- [X] Search rooms
- [X] Room detail

Client stuff
- [ ] "My bookings"
- [ ] Book room
- [ ] Cancel booking

Employee stuff
- [ ] Search all bookings
- [ ] Rent room
- [ ] See rentings
- [ ] Cancel renting
- [ ] Pay booking

- [ ] Search hotel
- [ ] Add hotel
- [ ] Delete hotel
- [ ] Modify hotel

- [ ] Search room
- [ ] Add room
- [ ] Delete room
- [ ] Modify room

- [ ] Search clients
- [ ] Add client
- [ ] Delete client
- [ ] Modify client

- [ ] Search employee
- [ ] Add employee
- [ ] Delete employee
- [ ] Modify employee

Extra
- [ ] Search hotel chain
- [ ] Add hotel chain
- [ ] Delete hotel chain
- [ ] Modify hotel chain
- [ ] "My profile" (client only)
- [ ] Search issue
- [ ] Add issue
- [ ] Delete issue
- [ ] Modify issue