package com.calaratjada.insider.data

import com.calaratjada.insider.config.ActiveCountryConfig
import com.calaratjada.insider.data.model.Event
import com.calaratjada.insider.data.model.Poi

object SeedData {

    private val categoryImages = mapOf(
        "beach" to "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
        "gastronomy" to "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&w=800&q=80",
        "nightlife" to "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80",
        "culture" to "https://images.unsplash.com/photo-1533154683836-84ea7a0bc310?auto=format&fit=crop&w=800&q=80",
        "nature" to "https://images.unsplash.com/photo-1500049222539-6593a380014e?auto=format&fit=crop&w=800&q=80",
        "shopping" to "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80",
        "service" to "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?auto=format&fit=crop&w=800&q=80"
    )

    private val categoryEventDefaults = mapOf(
        "market" to "market",
        "music" to "music",
        "fiesta" to "fiesta"
    )

    val pois: List<Poi> get() {
        val config = ActiveCountryConfig.config
        return config.topPois.mapIndexed { index, poi ->
            Poi(
                id = (index + 1).toString(),
                name = poi.name,
                category = poi.category,
                description = poi.description,
                lat = poi.lat,
                lng = poi.lng,
                imageUrl = categoryImages[poi.category] ?: categoryImages["culture"]!!,
                rating = poi.rating,
                address = poi.address
            )
        }
    }

    val events: List<Event> get() {
        val config = ActiveCountryConfig.config
        return config.topEvents.mapIndexed { index, event ->
            Event(
                id = "e${index + 1}",
                title = event.title,
                date = "2026-%02d-15".format(event.month),
                category = if (event.isRecurring) "fiesta" else "music",
                description = event.description,
                location = config.primaryCity
            )
        }
    }

    // Legacy hardcoded data kept for germany flavor fallback
    val legacyPois = listOf(
        Poi(
            id = "1",
            name = "Cala Agulla",
            category = "beach",
            description = "Einer der schönsten Strände Mallorcas, umgeben von Pinienwäldern und Bergen. Kristallklares Wasser und feiner Sand.",
            lat = 39.7214,
            lng = 3.4528,
            imageUrl = "https://images.unsplash.com/photo-1515238152791-8216bfdf89a7?auto=format&fit=crop&w=800&q=80",
            rating = 4.8f,
            address = "Cala Agulla, 07590 Cala Ratjada"
        ),
        Poi(
            id = "2",
            name = "Son Moll",
            category = "beach",
            description = "Zentraler Strand in Cala Ratjada mit feinem Sand und kristallklarem Wasser. Perfekt für einen schnellen Sprung ins Meer.",
            lat = 39.7089,
            lng = 3.4589,
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
            rating = 4.5f,
            address = "Passeig Marítim, 07590 Cala Ratjada"
        ),
        Poi(
            id = "3",
            name = "Noah's Lounge",
            category = "gastronomy",
            description = "Beliebte Lounge am Hafen mit tollem Ausblick und exzellenten Cocktails. Ein Muss für den Sonnenuntergang.",
            lat = 39.7115,
            lng = 3.4645,
            imageUrl = "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&w=800&q=80",
            rating = 4.6f,
            address = "Passeig Colom, 07590 Cala Ratjada",
            phone = "+34 971 56 30 00"
        ),
        Poi(
            id = "4",
            name = "Keops Disco",
            category = "nightlife",
            description = "Legendäre Diskothek in Cala Ratjada für lange Partynächte mit verschiedenen Musikbereichen.",
            lat = 39.7135,
            lng = 3.4612,
            imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=800&q=80",
            rating = 4.2f,
            address = "Carrer de Leonor Servera, 07590 Cala Ratjada"
        ),
        Poi(
            id = "5",
            name = "Far de Capdepera",
            category = "culture",
            description = "Historischer Leuchtturm mit atemberaubendem Blick auf das Meer und Menorca bei klarer Sicht.",
            lat = 39.7158,
            lng = 3.4775,
            imageUrl = "https://images.unsplash.com/photo-1500049222539-6593a380014e?auto=format&fit=crop&w=800&q=80",
            rating = 4.9f,
            address = "Cap de Capdepera, 07590 Cala Ratjada"
        ),
        Poi(
            id = "6",
            name = "Bolero Disco",
            category = "nightlife",
            description = "Eine der ältesten und bekanntesten Diskotheken im Ort. Bekannt für gute Stimmung und gemischtes Publikum.",
            lat = 39.7128,
            lng = 3.4625,
            imageUrl = "https://images.unsplash.com/photo-1566737236500-c8ac43014a67?auto=format&fit=crop&w=800&q=80",
            rating = 4.3f,
            address = "Carrer de Leonor Servera, 07590 Cala Ratjada"
        ),
        Poi(
            id = "7",
            name = "Cala Gat",
            category = "beach",
            description = "Kleine, malerische Bucht auf dem Weg zum Leuchtturm. Ideal zum Schnorcheln.",
            lat = 39.7132,
            lng = 3.4712,
            imageUrl = "https://images.unsplash.com/photo-1519046904884-53103b34b206?auto=format&fit=crop&w=800&q=80",
            rating = 4.7f,
            address = "Carrer de Cala Gat, 07590 Cala Ratjada"
        ),
        Poi(
            id = "8",
            name = "Pasta Pasta",
            category = "gastronomy",
            description = "Direkt am Hafen gelegen, bietet dieses Restaurant hervorragende italienische Küche mit Meerblick.",
            lat = 39.7108,
            lng = 3.4638,
            imageUrl = "https://images.unsplash.com/photo-1498579150354-977475b7ea0b?auto=format&fit=crop&w=800&q=80",
            rating = 4.4f,
            address = "Passeig Colom, 07590 Cala Ratjada"
        ),
        Poi(
            id = "9",
            name = "Müller Drogerie",
            category = "shopping",
            description = "Große Drogerie mit einer riesigen Auswahl an Parfüms, Kosmetik und Spielzeug. Zentral gelegen.",
            lat = 39.7138,
            lng = 3.4605,
            imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80",
            rating = 4.5f,
            address = "Carrer de Leonor Servera, 07590 Cala Ratjada"
        ),
        Poi(
            id = "10",
            name = "Apotheke Cala Ratjada",
            category = "service",
            description = "Zentrale Apotheke mit deutschsprachigem Personal. Hilfsbereit und gut sortiert.",
            lat = 39.7125,
            lng = 3.4615,
            imageUrl = "https://images.unsplash.com/photo-1587854692152-cbe660dbbb88?auto=format&fit=crop&w=800&q=80",
            rating = 4.6f,
            address = "Carrer de Elionor Servera, 07590 Cala Ratjada"
        ),
        Poi(
            id = "11",
            name = "Castell de Capdepera",
            category = "culture",
            description = "Mittelalterliche Burgruine hoch über Capdepera mit fantastischem Rundumblick über die Region.",
            lat = 39.7012,
            lng = 3.4335,
            imageUrl = "https://images.unsplash.com/photo-1533154683836-84ea7a0bc310?auto=format&fit=crop&w=800&q=80",
            rating = 4.8f,
            address = "Capdepera Castle, 07580 Capdepera"
        ),
        Poi(
            id = "12",
            name = "Cala Mesquida",
            category = "beach",
            description = "Weitläufiger Naturstrand mit Dünenlandschaft. Beliebt bei Surfern und Naturliebhabern.",
            lat = 39.7301,
            lng = 3.4372,
            imageUrl = "https://images.unsplash.com/photo-1506929562872-bb421503ef21?auto=format&fit=crop&w=800&q=80",
            rating = 4.7f,
            address = "Cala Mesquida, 07580 Capdepera"
        ),
        Poi(
            id = "13",
            name = "Sa Font de Sa Cala",
            category = "beach",
            description = "Ruhige Bucht südlich von Cala Ratjada, umgeben von Felsen und Pinien. Weniger touristisch.",
            lat = 39.6932,
            lng = 3.4501,
            imageUrl = "https://images.unsplash.com/photo-1471922694854-ff1b63b20054?auto=format&fit=crop&w=800&q=80",
            rating = 4.4f,
            address = "Sa Font de Sa Cala, 07590"
        ),
        Poi(
            id = "14",
            name = "Hafen Cala Ratjada",
            category = "culture",
            description = "Malerischer Fischerhafen mit bunten Booten, Restaurants und dem Ausgangspunkt für Bootsausflüge.",
            lat = 39.7105,
            lng = 3.4660,
            imageUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=800&q=80",
            rating = 4.5f,
            address = "Port de Cala Ratjada, 07590"
        ),
        Poi(
            id = "15",
            name = "Jardins Casa March",
            category = "culture",
            description = "Prachtvoller Skulpturengarten der Familie March mit modernen Kunstwerken und Meerblick.",
            lat = 39.7141,
            lng = 3.4688,
            imageUrl = "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?auto=format&fit=crop&w=800&q=80",
            rating = 4.6f,
            address = "Carrer de Joan March, 07590 Cala Ratjada"
        ),
        Poi(
            id = "16",
            name = "Panadería Artesana",
            category = "gastronomy",
            description = "Traditionelle Bäckerei mit mallorquinischen Ensaimadas und frischem Brot jeden Morgen.",
            lat = 39.7120,
            lng = 3.4610,
            imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=800&q=80",
            rating = 4.7f,
            address = "Carrer de L'Agulla, 07590 Cala Ratjada"
        ),
        Poi(
            id = "17",
            name = "Physical Disco",
            category = "nightlife",
            description = "Kultiger Club mit verschiedenen Tanzflächen und internationalem DJ-Line-Up in der Hochsaison.",
            lat = 39.7131,
            lng = 3.4618,
            imageUrl = "https://images.unsplash.com/photo-1571266028243-3716f02d3a25?auto=format&fit=crop&w=800&q=80",
            rating = 4.1f,
            address = "Carrer de Leonor Servera, 07590 Cala Ratjada"
        ),
        Poi(
            id = "18",
            name = "Mango Beachclub",
            category = "gastronomy",
            description = "Trendiger Beachclub am Strand Son Moll mit Cocktails, Tapas und Chill-Out Musik.",
            lat = 39.7092,
            lng = 3.4582,
            imageUrl = "https://images.unsplash.com/photo-1540541338287-41700207dee6?auto=format&fit=crop&w=800&q=80",
            rating = 4.5f,
            address = "Platja de Son Moll, 07590 Cala Ratjada"
        ),
        Poi(
            id = "19",
            name = "Mercado Semanal",
            category = "shopping",
            description = "Wochenmarkt jeden Samstag am Pinienplatz mit frischem Obst, Gemüse, Gewürzen und Kunsthandwerk.",
            lat = 39.7133,
            lng = 3.4598,
            imageUrl = "https://images.unsplash.com/photo-1488459716781-31db52582fe9?auto=format&fit=crop&w=800&q=80",
            rating = 4.6f,
            address = "Plaça dels Pins, 07590 Cala Ratjada"
        ),
        Poi(
            id = "20",
            name = "Centro de Salud",
            category = "service",
            description = "Gesundheitszentrum mit Notaufnahme. Deutschsprachige Ärzte verfügbar.",
            lat = 39.7118,
            lng = 3.4595,
            imageUrl = "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?auto=format&fit=crop&w=800&q=80",
            rating = 4.3f,
            address = "Carrer des Port, 07590 Cala Ratjada",
            phone = "+34 971 56 33 11"
        )
    )

    val legacyEvents = listOf(
        Event(
            id = "e1",
            title = "Wochenmarkt Cala Ratjada",
            date = "2026-03-21",
            category = "market",
            description = "Jeden Samstag am Pinienplatz. Frisches Obst, Gemüse und lokales Kunsthandwerk.",
            location = "Plaça dels Pins"
        ),
        Event(
            id = "e2",
            title = "Live Musik am Hafen",
            date = "2026-03-22",
            category = "music",
            description = "Verschiedene Künstler treten entlang der Hafenpromenade auf. Genießen Sie den Abend bei tollen Klängen.",
            location = "Hafenpromenade"
        ),
        Event(
            id = "e3",
            title = "Fiesta de San Juan",
            date = "2026-06-23",
            category = "fiesta",
            description = "Die Nacht des Feuers. Traditionelle Feierlichkeiten am Strand mit Lagerfeuer und Musik.",
            location = "Strand Son Moll"
        ),
        Event(
            id = "e4",
            title = "Wochenmarkt Capdepera",
            date = "2026-03-25",
            category = "market",
            description = "Jeden Mittwoch in der Altstadt von Capdepera. Lokale Produkte und Spezialitäten.",
            location = "Capdepera Altstadt"
        ),
        Event(
            id = "e5",
            title = "Festes del Carme",
            date = "2026-07-16",
            category = "fiesta",
            description = "Das Hafenfest zu Ehren der Schutzpatronin der Fischer mit Bootsprozession und Feuerwerk.",
            location = "Hafen Cala Ratjada"
        ),
        Event(
            id = "e6",
            title = "Sunset Session am Leuchtturm",
            date = "2026-04-10",
            category = "music",
            description = "DJ-Set bei Sonnenuntergang am Far de Capdepera mit Blick auf das Meer.",
            location = "Far de Capdepera"
        ),
        Event(
            id = "e7",
            title = "Noche de Tapas",
            date = "2026-05-15",
            category = "fiesta",
            description = "Tapas-Nacht entlang der Hafenpromenade. Restaurants bieten spezielle Menüs.",
            location = "Passeig Colom"
        ),
        Event(
            id = "e8",
            title = "Kunstmarkt am Hafen",
            date = "2026-04-05",
            category = "market",
            description = "Lokale Künstler stellen ihre Werke aus. Gemälde, Keramik und Schmuck.",
            location = "Hafen Cala Ratjada"
        )
    )
}
