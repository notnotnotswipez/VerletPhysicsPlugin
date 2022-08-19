# VerletPhysicsPlugin

This is just a little test plugin for Verlet physics integration in Vanilla minecraft. (Calculated server side.)

It doesnt have much functionality unless you like messing with shapes and constructions and all that.

Commands:
 - /physics simulate - Stops/Resumes all simulations
- /physics mode - Puts you into "Simulation Mode", where you can make your own creations, etc.
- /physics save (name) - Saves a structure to be loaded.
- /physics load (name) (optional: scale) - Loads a saved structure.

# How to build a structure:
Make sure to be in physics mode first, then you can build your "physics structures" with physical blocks.

- Red concrete represents points
- Lime concrete represents sticks/connections between another point
- Yellow concrete represents a LOCKED point, meaning it will not be affected by physics/will be frozen in place.

# Here is an example structure of a cube:
![image](https://user-images.githubusercontent.com/13337586/185519221-60729c9e-5fbd-4205-be3d-c9bf7fd54a17.png)

Saving your structure is similar to how you would save something in worldedit, get a stick (Again, make sure you're in physics mode), left click 
one corner to select your first point, right click to select your second point. Then run /physics save (whatever), in this case cube.

You can then load it via /physics load cube.

THING TO NOTE BECAUSE IM LAZY: THE FIRST POINT ALWAYS HAS TO BE LESS THAN THE SECOND POINT OR IT WONT SAVE. (Like the first points xyz needs to all be less than the second
points xyz)

# Help
If you are a physics person, PLEASE make the plugin better and contrubute to fix some of these issues. Im begging you.
Things that dont work at the moment and that I just cant figure out:
- Collision and responses between two points
- Collisions for the sticks
- Collisions between entities and points
