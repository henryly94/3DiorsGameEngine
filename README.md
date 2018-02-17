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

## Part 2 SceneManager & ScriptLoader

TODO
