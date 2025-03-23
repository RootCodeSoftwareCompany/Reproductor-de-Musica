## Como contribuir en GitHub desde NetBeans

Lo primera condicione es que debes ser colaborador del repositorio.

---
### Clonar el repositorio
Si es la primera vez que entras al proyecto debes clonarlo.

1. Inicias sesión en GitHub, abres el repositorio y te diriges al botón **code** para copiar el link del repositorio.
![[copiar-url-del-repositorio.png]]

2. Abres el NetBeans, en la barra superior vas a:
`Team > Git > Clone`

![[team-git-clone.png]]

3. Se abre la pestaña **Remote Repository**:
	1. Pegamos el link de repositorio que copiamos al inicio
	2. Introducimos nuestro usuario y la contraseña de nuestro GitHub
	3. El folder de destino se completa automáticamente aunque puedes cambiarlo.
	4. **Next**

![[url-usuario-contrasenia.png]]

4. **Remote Branches**: asegurarse de seleccionar la rama `develop`

![[seleccionar-rama.png]]

5. **Destination Directory** : Se autocompleta

**Finish**

![[directorio-de-destino.png]]

Esperamos un poco a cargue

![[porcentaje-clonacion.png]]

6. Se completa la clonación : **Open Project**

![[abrir-el-proyecto.png]] 

Ya podemos ver que tenemos los archivos y podemos ver que estamos en la rama `develop`

![[archivos-del-proyecto.png]] 

---
### Crear nuestra rama para trabajar 

1. vamos a la barra superior in entramos en: `Team > Branch/Tag > Create Branch...`

![[team-crear-branch.png]]

2. En la ventana que se abre:
	- Introducir tu nombre a la rama
	- Verificar que Revision sea `develop`
	- **Create**

![[nombre-del-branch.png]]

3. Si se crea correctamente podremos ver que se cambia de rama automáticamente.

![[se-cambio-a-la-nueva-rama.png]]
Ya podemos empezar a trabajar.

---




