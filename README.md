YYGE(COMS 4160 Programming Assignment)
====

## Part 1 ObjLoader/Transforamtion

### Overview
This project is based on sample code on [LWJGL](https://github.com/lwjglgamedev/lwjglbook/tree/master/chapter07). What I am trying to implement here is the ObjLoader.java/Transformation.java. In the ObjLoader, I wrote the function to read in .obj format files as GL Object. Tricky part here is to deal with files in different condition. Because a .obj file is allowed to omit some unnecessary parts of information.

Besides the basic part, I also implement the Texture part. Since the origin code was either Texture in 2D shape or 3D model without texture, it took me some time to figure out how OpenGL dealing with texture. 

The glDrawElement() call can only take one set of indices, that is to say when we come across a vertex with several texture coordinate, we cannot reuse the data anymore. That's the part troubles me most. 

Also, although we usually use the coordinate unmodified, but when .png texture comes from .dds file, it will inverse the y-axis(v-axis), so we have to take care to that part in order to load correct texture.  

### Running
After loading project, you can directly run the Main class. I build this project via Intellij IDEA. And I believe you can use Eclipse to build too.
 
The setting in current code was a demonstration of model with texture. A bullet will fly through a little horse and you should use your mouse to help him dodge.

I also included several model/texture files with same file name, you can try them all. 

All the functions are stay the same as origin, **Except** loadMesh() in Mesh.java. I modified it in order to load texture and dealing with .dds file's problem. You can see more detail in comments. 

## Part 2 (Creative Scene)FPS Game

### Overview

The game is about to hold a m4a1 with silencer to shoot horse for the points:D Please notice that in order to implement the FPS game and make the code style more clean, I've made many changes in 
the starter code. I always want to made a 3D game engine by myself, but it will be very time consuming. Instead, I made this Counter-Strike-like FPS game. There's several essential part of my game.

- FPSCamera.java
	- FPSCamera extends origin Camera and add rotateTarget() and move() function in order to get a first person vision.
- FPSGameItem.java
	- FPSGameItem extends GameItem and have its own functions like AABB(Axis-aligned Bounding Box) for shooting detect. Also it has moving function.
- FrontItem.java
	- This part is for the HUD. Since we need crosshair, a gun and a scoreboard, it will be another essential part of the game. 
	- In order to get a 2D effect, I write new shaders for HUD. hud\_vertex.vs and hud\_fragment.fs.
- UserInput.java
	- Original MouseInput has rather vague usage and wasn't structured very well. The new UserInput will be the only class that connect I/O input with the game and now I can use callback function to define the game's behaviour.

### Operate
Just running the Main.class. And enjoy shooting. 

| Key | Function    |
| --- |:----------- |
|  W  | Move Forward|
|  S  | Move Back   |
|  A  | Move Left   |
|  D  | Move Right  |
|Left mouse| Shoot  |
|  M  | Mute/Unmute Bg music|


