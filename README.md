# Gestor de Proyectos API

Bienvenido al repositorio de la API de Gestor de Proyectos. Esta API te permite gestionar proyectos, tareas, archivos y salas de chat asociadas a cada proyecto. Es una herramienta poderosa para colaborar en proyectos y mantener un seguimiento de las actividades y comunicaciones.

## Características

- Autenticación de usuarios para acceder a la plataforma.
- Creación, edición y eliminación de proyectos.
- Asignación de tareas a los miembros de cada proyecto.
- Subida de archivos a la nube utilizando AWS S3.
- Salas de chat en tiempo real para cada proyecto.
- Comunicación en tiempo real mediante websockets.
- Verificacion de Email mediante un codigo de verificacion.

## Instalación

1. Clona este repositorio: `git clone https://github.com/Frankester/gestor-de-proyectos.git`
2. Ve al directorio del proyecto: `cd gestor-de-proyectos`
3. Configura el proyecto (ver seccion Configuracion)

## Configuración

Modifica el archivo `application.properties` agregando la siguiente informacion para hacer que funcione el proyecto:

### 1. Credenciales de AWS S3
La API utiliza el servicio de almacenamiento en la nube de Amazon Web Services (AWS S3) para gestionar la subida de archivos. Para configurar las credenciales de AWS S3, sigue estos pasos:

1. Accede a la Consola de AWS o crea una cuenta si no tienes una
2. Obtén tus credenciales de acceso para s3 (Access Key ID y Secret Access Key) desde la sección de credenciales de seguridad en IAM dentro de la seccion de Usuarios (debes agregar un usuario si no lo hiciste).
3. Abre el archivo application.properties en el directorio raíz del proyecto y agrega las siguientes líneas, reemplazando con tus credenciales:
    ```application.properties
    aws.accessKeyId=<ACCESS_KEY_ID>
    aws.secretAccessKey=<SECRET_ACCESS_KEY>
    ```
4. Luego accede a S3, crea un bucket y llena los siguiente datos:
    ```application.properties
   aws.region=<BUCKET_REGION>
   aws.bucketName=<BUCKET_NAME>
    ```

### 2. Configuración de la Base de Datos
La API utiliza una base de datos para almacenar la información de usuarios, proyectos y tareas. Sigue estos pasos para configurar la conexión a la base de datos:

1. Instala y configura en tu sistema MySQL con Docker o Xampp.
2. Crea la base de datos ejecutando la siguiente consulta en Mysql (ejecutando `mysql` en la terimnal de Docker o mediante MySQL Workbench):
   `CREATE SCHEMA IF NOT EXIST gestorDeProyectos;`
3. Abre nuevamente el archivo application.properties y reemplaza los siguientes valores:
```application.properties
spring.datasource.url=jdbc:mysql://localhost:<DB_PORT>/<SCHEMA_NAME>
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>
 ```
### 3. Configuracion de Email para Confirmacion de Usuarios
La API incluye un sistema de registro de usuarios que requiere la confirmación de correo electrónico. Esto ayuda a verificar la autenticidad de las cuentas de usuario. Para habilitar esta función, sigue estos pasos:

1. Ve a la cuenta de gmail y sigue los pasos para generar una contraseña de applicaciones.
2. Abre el archivo application.properties y agrega tu email y la contraseña de applicaciones de 16 digitos generada:
```application.properties
spring.mail.username=<your-email-from-gmail>
spring.mail.password=<your-account-app-password-for-gmail>
 ```
### 4. Finalizando la Configuración
Una vez que hayas realizado todas las configuraciones necesarias en el archivo application.properties, la API estará lista para ser ejecutada. Asegúrate de haber completado todos los pasos anteriores antes de iniciar el servidor.