package com.carlospinan.devfestlima2019.data

import com.carlospinan.devfestlima2019.R
import com.carlospinan.devfestlima2019.model.Author
import com.carlospinan.devfestlima2019.model.Session

/**
 * @author Carlos Piñan
 */
// Authors
val grantTimmerman = Author(
    name = "Grant Timmerman",
    company = "Google",
    pictureId = R.drawable.grant
)

val manuelRodriguez = Author(
    name = "Manuel Rodriguez",
    company = "Google",
    pictureId = R.drawable.manuel_rodriguez
)

val victoriaAlejandra = Author(
    name = "Victoria Alejandra Ubaldo Gamarra",
    company = "Women Techmakers Lima",
    pictureId = R.drawable.victoria_ubaldo380
)

val gefersonPillaca = Author(
    name = "Geferson Pillaca",
    company = "Fandango Latam",
    pictureId = R.drawable.geferson_pg380
)

val carlosPinan = Author(
    name = "Carlos Piñan",
    company = "Belatrix",
    pictureId = R.drawable.carlos_pinan380
)
val frankMoreno = Author(
    name = "Frank Andre Moreno Vera",
    company = "Mandü",
    pictureId = R.drawable.frank_moreno
)
val edduMelendez = Author(
    name = "Eddú Meléndez Gonzales",
    company = "PeruJUG",
    pictureId = R.drawable.eddu_melendez
)
val kattyaCuevas = Author(
    name = "Kattya Cuevas",
    company = "Able.co",
    pictureId = R.drawable.kattya_cuevas
)
val melissaGave = Author(
    name = "Melissa Gave",
    company = "GDGLima | Women Techmakers Lima",
    pictureId = R.drawable.melissa_gave380
)

val authors = listOf(
    grantTimmerman,
    manuelRodriguez,
    victoriaAlejandra,
    gefersonPillaca,
    carlosPinan,
    frankMoreno,
    edduMelendez,
    kattyaCuevas,
    melissaGave
)

// Sessions
val sessions = listOf(
    Session(
        title = "Go Serverless with Google Cloud!",
        description = "Go Serverless with Google Cloud!",
        author = grantTimmerman
    ),
    Session(
        title = "Como hacemos Big Data en Google",
        description = "Como hacemos Big Data en Google",
        author = manuelRodriguez
    ),
    Session(
        title = "Building ML things with Tensorflow",
        description = "Compartir lecciones aprendidas y prácticas en la construcción de aplicaciones con Tensorflow, biblioteca open source para aprendizaje automático.",
        author = victoriaAlejandra
    ),
    Session(
        title = "An introduction to Kotlin Coroutines (Android)",
        description = "Las corrutinas es una de las características más interesantes de Kotlin que puede simplificar el trabajo de las tareas asíncronas. En esta charla veremos una introducción a Kotlin Coroutines y cómo escribir código asíncrono de forma secuencial en el desarrollo de aplicaciones Android.",
        author = gefersonPillaca
    ),
    Session(
        title = "Jetpack Composer Design (Android)",
        description = "Veremos uno de los componentes de Jetpack que esta en pre-alpha el @Composer. Veremos ejemplos, viabilidad y comparaciones.",
        author = carlosPinan
    ),
    Session(
        title = "I'm remarkable",
        description = "I'm Reemarkable es una iniciativa de Google que capacita a las mujeres para celebrar sus logros en el lugar de trabajo y más allá. Nuestros objetivos se basan en mejorar la motivación y las habilidades de autopromoción de las mujeres Desafiar la percepción social en torno a la autopromoción. Taller: Solo para mujeres",
        author = melissaGave
    ),
    Session(
        title = "Buenas prácticas para crear tu propia paleta de colores en Flutter",
        description = "Los gestores de estados necesitan compartir data entre varios widgets (ancestros y descendientes). Todos están basados en InheritedWidget. Un widget tan potente como poco usado (explícitamente) por los desarrolladores, pero está en todos lados.",
        author = frankMoreno
    ),
    Session(
        title = "Building and Consuming a JSON API with Rails and React",
        description = "Crear y consumir APIs siguiendo JSON:API spec con Ruby on Rails y React.js",
        author = kattyaCuevas
    ),
    Session(
        title = "Bootiful Spring Cloud GCP",
        description = "Las aplicaciones que desarrollamos necesitan ejecutarse en algún lugar, y no hay mejor lugar que la nube! Ya que nuestras aplicaciones necesitan almacenamiento, base de datos y una arquitectura que escale y sea resiliente. No es tarea fácil! Afortunadamente, Spring ofrece integraciones con los servicios de GCP como: Config, Cloud SQL, Spanner, Pub/Sub, Stackdriver. En esta charla, construiremos una aplicación en Spring haciendo uso de las bondades de Google Cloud Platform.",
        author = edduMelendez
    )
)

val tabs = listOf(
    "INICIO",
    "AUTORES",
    "SESIONES"
)