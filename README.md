# eHotels

hotels

Possible concerns when migrating to hosted DB:
- version of driver in pom.xml
- driver used in jdbc. Aka "com.mysql.cj.jdbc.Driver" vs "com.mysql.jdbc.Driver"

Remember to do (for Walid):
- [X] Trigger (or other mechanism) to enforce that the manager of a hotel needs to have 'manager' as a role.
- [ ] Trigger (or other mechanism) to enforce only one session per LoginCred
- [X] Caching for sessions

Endpoints:

Login stuff
- [X] Login
- [X] Session valid
- [X] Register (client only)
    - [X] ssn security
- [X] Client info from uuid (path is 'whoAmI')
- [ ] Sign out

Booking/Renting stuff common (differences client/employee at UI level)
- [X] Search rooms
    - [X] Hotel areas
- [X] Room detail

Client stuff
- [X] "My bookings"
    - [X] Add client info to return
- [X] Book room
- [X] Cancel booking or renting

Employee stuff
- [X] Get all hotels (return chainname, zip and id)
- [X] Search all bookings
- [X] Rent room directly
- [X] Rent room
    - [X] Security to not set rent before right date - DONT CARE
	- [X] Other securities, there are NONE right now. E.g. we can re-rent a room. - DONT CARE
- [X] See rentings
- [X] Cancel renting
    - Should there be any securities on this. There are none right now (beside that you can't delete a booking with this endpoint)
- [X] Pay booking
    - [X] Security to not set pay before right date - DONT CARE
	- [X] Other securities, there are NONE right now. E.g. we can re-pay a room. - DONT CARE
***
- [X] Search hotel
    - We have a "getAllHotels" and we can do search w/ JS
- [X] Hotel detail
    - [X] List of managers
	- [X] List of phone (not an endpoint, just reminder)
- [X] Add hotel
    - [X] Get hotelChain (id - chainname)
	- [X] checks in place for foreign key failures. Only error from DB. - JUST SEND BACK SQL EXCEPTION MESSAGE
- [X] Delete hotel
- [X] Modify hotel
    - [X] Add phone number
	    - [X] duplicate security. Overkill, be note that this can avoid a small bug with delete - DONT CARE
    - [X] Delete phone number
	- [X] checks in place for foreign key failures. Only error from DB. - JUST SEND BACK SQL EXCEPTION MESSAGE
***
- [X] Search room
    - I believe we decided to just use the same search as the client
- [X] Add room
	- [ ] checks in place for foreign key failures. Only error from DB.
- [X] Delete room
- [X] Modify room
	- [ ] checks in place for foreign key failures. Only error from DB.
    - [X] Add amenity
		- [ ] duplicate security. Overkill, be note that this can avoid a small bug with delete
    - [X] Add issue
		- [ ] duplicate security. Overkill, be note that this can avoid a small bug with delete
    - [X] Delete amenity
    - [X] Delete issue
***
- [X] Get all client
- [X] Add client
    - This is pretty much register
- [X] Delete client
- [X] Modify client
***
- [X] Search employee
- [X] Add employee
    - [X] ssn security
- [X] Delete employee
- [X] Modify employee
	- [X] Add role
		- [ ] duplicate security. Overkill, be note that this can avoid a small bug with delete
	- [X] Delete role
***
- [X] View for rooms per area
    - [X] SQL
	- [X] Endpoint
- [ ] View for rooms per hotel
    - [X] SQL
	- [ ] ENdpoint
***
Extra
- [ ] Refactor the time where I used if/else instead of setBoolean
- [ ] DB 'UNIQUE' for appropriate fields
- [ ] Search hotel chain
- [ ] Add hotel chain
- [ ] Delete hotel chain
- [ ] Modify hotel chain
- [ ] "My profile" (client only)
- [ ] Combine identical (almost) code
    - Delete issue and delete amenity
- [ ] Find solution to differentiate between missing params and purposeful empty strings