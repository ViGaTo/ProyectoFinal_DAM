# Proyecto Final | Proyecto Integrado de Desarrollo de Aplicaciones Multiplataforma
### 📋 Descripción del proyecto 📋
Este proyecto consiste en una aplicación de uso empresarial dedicada a una determinada empresa, esta aplicación la realizo para demostrar los conocimientos que he adquirido en este ciclo formativo (DAM), para ello juntare lo que hemos hecho en todas las asignaturas y las fusionare para hacer una aplicación móvil que se encargue de la gestión de una empresa, usando de referencia los proyectos que hemos estado usando en las asignaturas de este año y de mi proyecto anterior (que en vez de una aplicación, fue una web para la gestión de una empresa), creare una aplicación para el mismo propósito.

### 📁 Contenido de la publicación 📁
1.	Data: Esta carpeta contiene todos los datos de nuestro proyecto.
      a.	Models: Contiene los modelos de las tablas que nos encargaremos de guardar en nuestra respectiva base de datos (básicamente contiene los datos y los constructores de cada tabla en la aplicación).
      b.	Providers: Es la carpeta que se encarga de gestionar casi toda la interacción con los datos de nuestra base de datos.
      i.	API: Se encarga de la gestión de la API Fakestore para poder obtener datos de esa API.
      ii.	Repository: Interactúa con la base de datos de Firebase para poder añadir, editar, borrar u obtener diferentes tipos de listas de datos.


2.	UI: Es la carpeta que se encarga de la gestión de los datos de nuestra carpeta Data para poder mostrarlos o interactuar con ellos haciendo uso de la interfaz y las interacciones de los usuarios.
      a.	Adapters: Les da funcionalidad y mostrar datos de los elementos de las tablas de datos a sus respectivos layouts.
      b.	Main: Contienen los activities de nuestra aplicación que son los que hacen que nuestra aplicación pueda funcionar como quiera el desarrollador (es lo principal).
      c.	ViewModels: Hacen llamadas a las gestiones de la carpeta repository y actualizan a tiempo real (con estas mismas llamadas) los datos mostrados.
3.	Utils: Son elementos que usamos de ayuda o para evitar usar poner más código del que deberíamos poner.
4.	Res: Esta carpeta guarda todo lo relacionado a las vistas graficas de nuestra aplicación (por si no se ve en la imagen esta más debajo de donde están el resto de carpetas).

### ⚙️ Desarrollo del proyecto ⚙️
Este proyecto se ha realizado en **Android Studio** y se ha hecho uso de **Kotlin**, **Material Design**, **Firebase**, **Google Cloud**  e importaciones con varias librerias.

### 📦 Despliegue (como ejecutar la aplicación desde el ejecutable) 📦
Hay dos maneras de hacerlo con Android Studio o con el APK que se genera al compilar el proyecto.
1.	Android Studio: Para ejecutar el proyecto desde Android Studio, simplemente hay que descargar el proyecto desde la Moodle, abrir el proyecto y compilarlo, una vez compilado se puede ejecutar en un emulador o en un dispositivo físico.

2.  APK: Para ejecutar el proyecto desde el APK, simplemente hay que descargar el APK desde la Moodle, instalarlo en un dispositivo Android y ejecutarlo.

### 🛠️ Construido con 🛠️
* ![Android Studio](https://img.shields.io/badge/android%20studio-346ac1?style=for-the-badge&logo=android%20studio&logoColor=white)
* ![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
* ![Firebase](https://img.shields.io/badge/firebase-a08021?style=for-the-badge&logo=firebase&logoColor=ffcd34)
* ![Google Cloud](https://img.shields.io/badge/GoogleCloud-%234285F4.svg?style=for-the-badge&logo=google-cloud&logoColor=white)
* ![Material Design](https://img.shields.io/badge/material%20design-757575?style=for-the-badge&logo=material-design&logoColor=white)

### 💡 Versionado 💡
Esta es última versión del proyecto, las anteriores versiones se pueden encontrar en el repositorio de GitHub del proyecto.

### ✒️ Autores ✒️
* <a href="https://github.com/ViGaTo">ViGaTo</a>

### 📄 Licencia 📄
Este proyecto está bajo licencia gratuita.

### 🌐 Recursos adicionales 🌐
Página Principal: <a href="https://github.com/ViGaTo">https://github.com/ViGaTo"</a>
Android Studio: <a href="https://developer.android.com/studio">https://developer.android.com/studio</a>
Firebase: <a href="https://firebase.google.com/">https://firebase.google.com/</a>
Google Cloud: <a href="https://cloud.google.com/">https://cloud.google.com/</a>
Material Design 3: <a href="https://m3.material.io/">https://m3.material.io/</a>