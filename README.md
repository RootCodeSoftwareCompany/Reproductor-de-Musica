Como contribuir al GitHub desde NetBeans
---

Lo primero es que debes ser colaborador del repositorio.

---
### Clonar el repositorio
Si es la primera vez que entras al proyecto debes clonarlo.

1. Inicias sesión en GitHub, abres el repositorio y te diriges al botón **code** para copiar el link del repositorio.

![](/images/copiar-url-del-repositorio.png)

2. Abres el NetBeans, en la barra superior vas a:
`Team > Git > Clone`

![](/images/team-git-clone.png)

3. Se abre la pestaña **Remote Repository**:
	1. Pegamos el link de repositorio que copiamos al inicio
	2. Introducimos nuestro usuario y la contraseña de nuestro GitHub
	3. El folder de destino se completa automáticamente aunque puedes cambiarlo.
	4. **Next**

![](/images/url-usuario-contrasenia.png)

4. **Remote Branches**: asegurarse de seleccionar la rama `develop`

![](/images/seleccionar-rama.png)

5. **Destination Directory** : Se autocompleta

**Finish**

![](/images/directorio-de-destino.png)

Esperamos un poco a cargue

![](/images/porcentaje-clonacion.png)

6. Se completa la clonación : **Open Project**

![](/images/abrir-el-proyecto.png) 

Ya podemos ver que tenemos los archivos y podemos ver que estamos en la rama `develop`

![](/images/archivos-del-proyecto.png) 

Si ya clonaste antes el repositorio y lo tienes en local debes asegurarte que este actualizado con el repositorio remoto, con la opción [pull](#pull) (que se explica casi al final de este documento).

---
### Crear nuestra rama para trabajar 

1. vamos a la barra superior in entramos en: `Team > Branch/Tag > Create Branch...`

![](/images/team-crear-branch.png)

2. En la ventana que se abre:
	- Introducir tu nombre a la rama
	- Verificar que Revision sea `develop`
	- **Create**

![](/images/nombre-del-branch.png)

3. Si se crea correctamente podremos ver que se cambia de rama automáticamente.

![](/images/se-cambio-a-la-nueva-rama.png)
Ya podemos empezar a trabajar.

---
### Adds y Commits

C
Cuando realizamos cambios en los `files` el nombre del archivo se pone de color azul y al final aparece una letra M de esta manera `[-/M]` y si añadimos  algún archivo el nombre se ve de color verde y con una A como esta `[-/A]` al final.
Quiere decir que no están agregados ni confirmados (no se les hizo commit)

![](/images/archivo-editado.png)

Para agregar vamos a: `Team > Add`

![](/images/team-add.png)

Podemos verificar que se ejecuto cuando el símbolo al final del archivo cambia a `[A/-]`

![](/images/archivo-agregado.png)

Ahora confirmaremos lo que agregamos: `Team > Commit...`

![](/images/team-commit.png)

Se abre esta pestaña donde debemos añadir el mensaje para que sepan que hicimos.

Podemos ver en la parte de abajo los archivos que se están confirmando

![](/images/mensaje-commit.png)

Cuando ya se hizo el **Commit** el archivo deja de estar de color verde y desaparece el símbolo 

![](/images/commit-realizado.png)

Se recomienda hacer commit a cada cambio que puede generar un nuevo error.

---

### Push

Cuando hayamos terminado nuestra jornada de trabajo subimos los cambios al repositorio.

Para esto `Team > Remote > Push...`
![](/images/team-remote-push.png)

En esta ventana el **Remote Repository** se agrega automáticamente. 

![](/images/repositorio-remoto.png)

Pero nos genera un inconveniente, esa ruta que se agrega autocompleta el **User** y **Password** de nuestra cuenta con la cual hicimos el `clone` y si seguimos adelante al terminar nos saldrá esta ventana: 

![](/images/ventana-user-password.png)

Y aunque le de demos **Ok** mucha veces volverá a salir, y si le damos a **Cancel** sale una ventana, que si deslizamos la barra inferior podemos ver que al final del texto dice **not authorized**.

![](/images/no-autorizado.png)

Para solucionar esto debemos ir a GitHub y crear un **Token**

Presionas en la imagen de perfil de tu GitHub y te da estas opciones > **Settings**
![](/images/opciones-perfil-github.png)

Es settings desliza hasta abajo

![](/images/settings.png)

Entra en **Developer settings**

![](/images/developer-settings.png)

**Personal access tokens > Tokens (classic)**

![](/images/tokens-de-acceso.png)

**Generate new token > Generate new token (classic)**

![](/images/generar-nuevo-token.png)

Ponerle un nombre al token en **Note**  y podrías cambiar el tiempo de Expiración (pero no es necesario)

![](/images/nombre-token.png)

Desliza hacia abajo y marca todas opciones

![](/images/selecionar-todos.png)

Darle a **Generar token**

![](/images/crear-token.png)

**copia** tu nuevo token

![](/images/copiar-token.png)

Ahora al hacer `Team > remote > push...`

Selecciona **Specify Git Repository Location** y cambia tu **Password** por el token > **Next**

![](/images/token-password.png)


**Select Local Branches** seleccionamos nuestra rama > **Next**

![](/images/rama-local.png)

**Update Local References** debe estar seleccionada nuestra rama > **Finish**

![](/images/seleccionar-rama-remota.png)

Si nos sale esta ventana > **Yes**

![](/images/confirmar-subir-a-rama-remota.png)

Ya se subió los cambios a la rama que creamos en el repositorio. Pero no se agregó a la rama `develop` para que se agregue debemos solicitar un `pull request`.

---
### Pull request 

Después de haber echo un push si vamos al repositorio en la rama `develop` en GitHub posiblemente nos aparezca este mensaje

![](/images/compare-pull-request.png)

Si le damos al botón verde **Compare & pull request** nos manda directamente la ventana para crear el pull request

En otro caso vemos el mensaje **This branch is 1 commit ahead of develop** lo que quiere decir que tiene un commit más que nuestra rama principal `develop`  

![](/images/contenido-rama-remota.png)

**clic** en el texto  "***1 commit ahead of***" resaltado en azul

Nos manda a esta sección **Comparing Changes** :
Entre el branch `develop` <-- `alexcris` 
Y *Able to merge* esta en verde, lo que significa que no hay conflictos

Le damas al botón **Create pull request**
![](/images/comparar-cambios.png)

Abre esta pantalla donde vemos que el mensaje del commit va como título y podemos añadir una descripción (no es necesaria).

Le damos a **Create pull request**

![](/images/crear-pull-request.png)

Y aquí termina nuestra labor **Se  agrego NUESTRA MODIFICACION**

![](/images/se-agrego-el-cambio.png)

Hay dos alertas en rojo:
**Review required** y **Merging is blocked** esto es porque para evitar cambios no deseados la rama dev tiene la condiciones para ser fusionado:
- Requiere pull request antes de ser fusionado
- Requiere 1 aprobación de alguno de los colaboradores

(NO deja aprobarse uno mismo)

![](/images/requiere-pull-request.png)

---

### Aprobar un pull request

*Recuerda que no puedes aprobarte tu mismo.*

El colaborador que quiere **revisar** un `pull request` para **aprobarlo** al abrir el repositorio puede ver que hay **pull request 1** entra en esa pestaña y presiona en el pull request que aparece.

![](/images/1-pull-request.png)

Se abre el pull request y nos muestra la pestaña que necesita aprobación que ya vimos antes.

Estamos en la sub pestaña  **Conversation**
Nos dirigimos a la sub pestaña **Files Changed** 

![](/images/entrar-al-pull-request.png)

Aquí se nos abre el código con los cambios realizados, podemos empezar a revisar.

Cuando terminemos de revisar vamos al boton verde **Review Changes**

![](/images/files-changed.png)

Se nos abre  un cuadro donde podemos escribir nuestros comentarios y tenemos tres opciones de como enviarlos:
- **Comment** : Envía un comentario sin dar aprobación
- **Approve** : Envía comentarios y aprueba la fusión
- **Request Changes** : Sugiere cambios antes de la aprobación

Para aprobar no es obligatorio comentar solo seleccionamos la opción y le damos a **Submit review**

![](/images/commet-aprove-change.png)

Nos envía a la subpestaña anterior donde podemos ver que aprobaste los nuevos cambios y los iconos que estaban en rojo están en verde.

Presionas en **Merge pull request**

![](/images/cambios-aprobados.png)

Ahora debes **Confirm merge**

![](/images/confirmar-union.png)

Y ya estaría fusionado correctamente.

![](/images/fusionado-correctamente.png)

Si vamos a la pestaña **code** podemos ver que el archivo se agrego a la rama develop.

![](/images/cambio-rama-develop.png)

---

### Pull
Ya que realizamos trabajo colaborativo debemos tomar en cuenta que otros harán cambios al repositorio remoto y nuestro repositorio local quedara desactualizado.

Alguien cambio el nombre de **CONTRIBUTING**.md a **README**.md

![](/images/cambios-repositorio-remoto.png)

Para actualizar nuestro repositorio local lo haremos con **pull**, vamos a la barra superior y entramos en: `Team > remote > pull...`
![](/images/team-remote-pull.png)

Se nos abre esta ventana **Remote Repository** donde la ruta completa automáticamente > **Next**

![](/images/repositorio-remoto-pull.png)

En **Remote Branche** en remote branches seleccionamos la rama `develop`  > **Finish**

![](/images/pull-rama.png)

Y así ya tendríamos el repositorio local actualizado.

![](/images/repositorio-local-actualizado.png)


**NOTA**: No hacer clone, push o pull a la rama `master`, la rama habilitada para colaborar es la rama `develop`.





