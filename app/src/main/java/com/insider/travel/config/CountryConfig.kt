// =============================================================================
// COUNTRY CONFIGURATION - All 25 Target Markets
// =============================================================================
// This file defines all country-specific data for the Insider App Framework.
// Each country has: app identity, geo data, POIs, localization, legal, currency.
// =============================================================================

package com.insider.travel.config

data class CountryConfig(
    val code: String,               // ISO 3166-1 alpha-2
    val flavorName: String,         // Gradle product flavor name
    val applicationId: String,      // Unique package name
    val appName: String,            // App display name
    val primaryCity: String,        // Main tourist destination
    val countryName: String,        // Full country name
    val defaultLocale: String,      // Primary language code
    val supportedLocales: List<String>, // All supported languages
    val lat: Double,                // Center latitude
    val lng: Double,                // Center longitude
    val mapZoom: Float,             // Default map zoom level
    val currencyCode: String,       // ISO 4217
    val currencySymbol: String,
    val legalFramework: String,     // GDPR, CCPA, LGPD, POPIA, etc.
    val timezone: String,
    val playStoreCountry: String,   // Play Store target country
    val topPois: List<PoiTemplate>, // Top points of interest
    val topEvents: List<EventTemplate>
)

data class PoiTemplate(
    val name: String,
    val category: String,           // beach, gastronomy, nightlife, culture, nature, shopping
    val description: String,
    val lat: Double,
    val lng: Double,
    val rating: Float,
    val address: String
)

data class EventTemplate(
    val title: String,
    val description: String,
    val month: Int,                 // 1-12
    val isRecurring: Boolean
)

// =============================================================================
// ALL 25 COUNTRY CONFIGURATIONS
// =============================================================================

object Countries {

    val all: List<CountryConfig> = listOf(

        // =====================================================================
        // 1. DEUTSCHLAND (MASTER) - Cala Ratjada, Mallorca
        // =====================================================================
        CountryConfig(
            code = "DE", flavorName = "germany",
            applicationId = "com.calaratjada.insider",
            appName = "Cala Ratjada Insider",
            primaryCity = "Cala Ratjada", countryName = "Deutschland",
            defaultLocale = "de", supportedLocales = listOf("de", "en", "es", "fr", "it", "nl", "pl", "pt", "ru", "tr"),
            lat = 39.7095, lng = 3.4631, mapZoom = 14f,
            currencyCode = "EUR", currencySymbol = "€",
            legalFramework = "GDPR_EU", timezone = "Europe/Berlin",
            playStoreCountry = "DE",
            topPois = listOf(
                PoiTemplate("Cala Agulla", "beach", "Einer der schönsten Strände Mallorcas mit kristallklarem Wasser", 39.7214, 3.4528, 4.8f, "Cala Agulla, 07590"),
                PoiTemplate("Son Moll", "beach", "Zentraler Stadtstrand mit feinem Sand", 39.7089, 3.4589, 4.5f, "Passeig Marítim, 07590"),
                PoiTemplate("Far de Capdepera", "culture", "Historischer Leuchtturm mit Blick auf Menorca", 39.7158, 3.4775, 4.7f, "Capdepera"),
                PoiTemplate("Noah's Lounge", "gastronomy", "Beliebte Lounge am Hafen", 39.7115, 3.4645, 4.6f, "Passeig Colom, 07590"),
                PoiTemplate("Keops Disco", "nightlife", "Legendäre Diskothek", 39.7135, 3.4612, 4.2f, "Carrer de Leonor Servera, 07590")
            ),
            topEvents = listOf(
                EventTemplate("Festa de Sant Antoni", "Traditionelles Fest mit Feuerläufen", 1, true),
                EventTemplate("Mercat de Nit", "Nachtmarkt am Hafen", 7, true)
            )
        ),

        // =====================================================================
        // 2. USA - Miami Beach, Florida
        // =====================================================================
        CountryConfig(
            code = "US", flavorName = "usa",
            applicationId = "com.insider.travel.usa",
            appName = "Miami Beach Insider",
            primaryCity = "Miami Beach", countryName = "USA",
            defaultLocale = "en", supportedLocales = listOf("en", "es", "pt", "fr", "de", "zh", "ja", "ko", "ru", "it"),
            lat = 25.7907, lng = -80.1300, mapZoom = 13f,
            currencyCode = "USD", currencySymbol = "$",
            legalFramework = "CCPA", timezone = "America/New_York",
            playStoreCountry = "US",
            topPois = listOf(
                PoiTemplate("South Beach", "beach", "Iconic sandy beach with Art Deco backdrop", 25.7826, -80.1341, 4.7f, "Ocean Drive, Miami Beach, FL 33139"),
                PoiTemplate("Ocean Drive", "culture", "Famous Art Deco strip with restaurants and nightlife", 25.7811, -80.1306, 4.6f, "Ocean Drive, Miami Beach"),
                PoiTemplate("Joe's Stone Crab", "gastronomy", "Legendary seafood restaurant since 1913", 25.7687, -80.1371, 4.8f, "11 Washington Ave"),
                PoiTemplate("LIV Nightclub", "nightlife", "Premier nightclub at Fontainebleau", 25.8198, -80.1222, 4.4f, "4441 Collins Ave"),
                PoiTemplate("Everglades National Park", "nature", "Unique subtropical wilderness", 25.2866, -80.8987, 4.9f, "Homestead, FL")
            ),
            topEvents = listOf(
                EventTemplate("Art Basel Miami Beach", "World-renowned contemporary art fair", 12, true),
                EventTemplate("Ultra Music Festival", "Global electronic music festival", 3, true)
            )
        ),

        // =====================================================================
        // 3. BRASILIEN - Rio de Janeiro
        // =====================================================================
        CountryConfig(
            code = "BR", flavorName = "brazil",
            applicationId = "com.insider.travel.brazil",
            appName = "Rio Insider",
            primaryCity = "Rio de Janeiro", countryName = "Brasilien",
            defaultLocale = "pt", supportedLocales = listOf("pt", "en", "es", "fr", "de", "it", "ja", "zh", "ko", "ru"),
            lat = -22.9068, lng = -43.1729, mapZoom = 12f,
            currencyCode = "BRL", currencySymbol = "R$",
            legalFramework = "LGPD", timezone = "America/Sao_Paulo",
            playStoreCountry = "BR",
            topPois = listOf(
                PoiTemplate("Copacabana Beach", "beach", "Praia lendária com calçadão icônico", -22.9711, -43.1826, 4.7f, "Copacabana, Rio de Janeiro"),
                PoiTemplate("Cristo Redentor", "culture", "Estátua icônica no topo do Corcovado", -22.9519, -43.2105, 4.9f, "Parque Nacional da Tijuca"),
                PoiTemplate("Ipanema Beach", "beach", "Praia famosa mundialmente", -22.9838, -43.2096, 4.8f, "Ipanema, Rio de Janeiro"),
                PoiTemplate("Lapa Arches", "nightlife", "Coração da vida noturna carioca", -22.9133, -43.1809, 4.5f, "Lapa, Rio de Janeiro"),
                PoiTemplate("Pão de Açúcar", "nature", "Vista panorâmica espetacular", -22.9489, -43.1570, 4.8f, "Urca, Rio de Janeiro")
            ),
            topEvents = listOf(
                EventTemplate("Carnaval do Rio", "O maior carnaval do mundo", 2, true),
                EventTemplate("Réveillon de Copacabana", "Festa de Ano Novo na praia", 12, true)
            )
        ),

        // =====================================================================
        // 4. PERU - Cusco / Machu Picchu
        // =====================================================================
        CountryConfig(
            code = "PE", flavorName = "peru",
            applicationId = "com.insider.travel.peru",
            appName = "Cusco Insider",
            primaryCity = "Cusco", countryName = "Peru",
            defaultLocale = "es", supportedLocales = listOf("es", "en", "pt", "fr", "de", "it", "ja", "zh", "qu", "ru"),
            lat = -13.5320, lng = -71.9675, mapZoom = 13f,
            currencyCode = "PEN", currencySymbol = "S/",
            legalFramework = "GDPR_LIKE", timezone = "America/Lima",
            playStoreCountry = "PE",
            topPois = listOf(
                PoiTemplate("Machu Picchu", "culture", "Ciudad inca perdida, maravilla del mundo", -13.1631, -72.5450, 4.9f, "Aguas Calientes, Cusco"),
                PoiTemplate("Plaza de Armas", "culture", "Corazón histórico de Cusco", -13.5170, -71.9785, 4.7f, "Plaza de Armas, Cusco"),
                PoiTemplate("Sacsayhuamán", "culture", "Impresionante fortaleza inca", -13.5094, -71.9820, 4.8f, "Cusco"),
                PoiTemplate("San Pedro Market", "gastronomy", "Mercado tradicional con comida local", -13.5190, -71.9810, 4.6f, "Cusco Centro"),
                PoiTemplate("Rainbow Mountain", "nature", "Montaña de colores a 5.200m", -13.8711, -71.3030, 4.7f, "Cusipata, Cusco")
            ),
            topEvents = listOf(
                EventTemplate("Inti Raymi", "Festival del Sol inca", 6, true),
                EventTemplate("Semana Santa Cusco", "Procesiones religiosas coloniales", 4, true)
            )
        ),

        // =====================================================================
        // 5. MEXIKO - Cancún / Riviera Maya
        // =====================================================================
        CountryConfig(
            code = "MX", flavorName = "mexico",
            applicationId = "com.insider.travel.mexico",
            appName = "Cancún Insider",
            primaryCity = "Cancún", countryName = "Mexiko",
            defaultLocale = "es", supportedLocales = listOf("es", "en", "fr", "de", "pt", "it", "ja", "zh", "ko", "ru"),
            lat = 21.1619, lng = -86.8515, mapZoom = 12f,
            currencyCode = "MXN", currencySymbol = "$",
            legalFramework = "LFPDPPP", timezone = "America/Cancun",
            playStoreCountry = "MX",
            topPois = listOf(
                PoiTemplate("Playa Delfines", "beach", "Playa pública con vista espectacular", 21.0821, -86.7746, 4.8f, "Zona Hotelera, Cancún"),
                PoiTemplate("Chichén Itzá", "culture", "Pirámide maya, maravilla del mundo", 20.6843, -88.5678, 4.9f, "Yucatán"),
                PoiTemplate("Isla Mujeres", "nature", "Isla paradisíaca con aguas cristalinas", 21.2319, -86.7338, 4.8f, "Isla Mujeres, Q.R."),
                PoiTemplate("Coco Bongo", "nightlife", "Club legendario con shows increíbles", 21.1359, -86.7475, 4.6f, "Zona Hotelera"),
                PoiTemplate("Puerto Morelos", "gastronomy", "Pueblo pesquero con restaurantes auténticos", 20.8462, -86.8753, 4.5f, "Puerto Morelos, Q.R.")
            ),
            topEvents = listOf(
                EventTemplate("Día de los Muertos", "Celebración ancestral mexicana", 11, true),
                EventTemplate("Spring Break Cancún", "Temporada de fiesta internacional", 3, true)
            )
        ),

        // =====================================================================
        // 6. ENGLAND (UK) - London
        // =====================================================================
        CountryConfig(
            code = "GB", flavorName = "uk",
            applicationId = "com.insider.travel.uk",
            appName = "London Insider",
            primaryCity = "London", countryName = "United Kingdom",
            defaultLocale = "en", supportedLocales = listOf("en", "fr", "de", "es", "it", "pt", "zh", "ja", "ar", "hi"),
            lat = 51.5074, lng = -0.1278, mapZoom = 11f,
            currencyCode = "GBP", currencySymbol = "£",
            legalFramework = "UK_GDPR", timezone = "Europe/London",
            playStoreCountry = "GB",
            topPois = listOf(
                PoiTemplate("Tower of London", "culture", "Historic castle and Crown Jewels", 51.5081, -0.0759, 4.8f, "London EC3N 4AB"),
                PoiTemplate("Borough Market", "gastronomy", "London's most famous food market", 51.5055, -0.0910, 4.7f, "8 Southwark St"),
                PoiTemplate("Camden Market", "shopping", "Eclectic market with street food", 51.5414, -0.1427, 4.5f, "Camden Town NW1"),
                PoiTemplate("Fabric", "nightlife", "World-renowned electronic music club", 51.5197, -0.1025, 4.6f, "77A Charterhouse St"),
                PoiTemplate("Hyde Park", "nature", "Central London's largest park", 51.5073, -0.1657, 4.7f, "London W2 2UH")
            ),
            topEvents = listOf(
                EventTemplate("Notting Hill Carnival", "Europe's biggest street festival", 8, true),
                EventTemplate("New Year's Eve Fireworks", "Spectacular Thames fireworks", 12, true)
            )
        ),

        // =====================================================================
        // 7. ALBANIEN - Saranda / Ksamil
        // =====================================================================
        CountryConfig(
            code = "AL", flavorName = "albania",
            applicationId = "com.insider.travel.albania",
            appName = "Sarandë Insider",
            primaryCity = "Sarandë", countryName = "Albanien",
            defaultLocale = "sq", supportedLocales = listOf("sq", "en", "de", "it", "el", "fr", "es", "tr", "ru", "pt"),
            lat = 39.8661, lng = 20.0047, mapZoom = 13f,
            currencyCode = "ALL", currencySymbol = "L",
            legalFramework = "GDPR_LIKE", timezone = "Europe/Tirane",
            playStoreCountry = "AL",
            topPois = listOf(
                PoiTemplate("Ksamil Beach", "beach", "Plazhet e bardha me ujë kristal", 39.7831, 20.0000, 4.9f, "Ksamil, Sarandë"),
                PoiTemplate("Blue Eye Spring", "nature", "Burim natyror me ujë blu", 39.9208, 20.1889, 4.8f, "Syri i Kaltër"),
                PoiTemplate("Butrint", "culture", "Qytet antik UNESCO", 39.7453, 20.0225, 4.8f, "Butrint National Park"),
                PoiTemplate("Lëkurësi Castle", "culture", "Kala me pamje panoramike", 39.8547, 20.0125, 4.6f, "Sarandë"),
                PoiTemplate("Sarandë Promenade", "gastronomy", "Shëtitore me restorante detare", 39.8661, 20.0047, 4.5f, "Rruga e Flamurit")
            ),
            topEvents = listOf(
                EventTemplate("Kala Festival", "Electronic music at Borsh Beach", 6, true),
                EventTemplate("Summer Day (Dita e Verës)", "Traditional Albanian celebration", 3, true)
            )
        ),

        // =====================================================================
        // 8. GEORGIEN - Tbilisi / Batumi
        // =====================================================================
        CountryConfig(
            code = "GE", flavorName = "georgia",
            applicationId = "com.insider.travel.georgia",
            appName = "Tbilisi Insider",
            primaryCity = "Tbilisi", countryName = "Georgien",
            defaultLocale = "ka", supportedLocales = listOf("ka", "en", "ru", "de", "fr", "es", "tr", "ar", "zh", "it"),
            lat = 41.7151, lng = 44.8271, mapZoom = 12f,
            currencyCode = "GEL", currencySymbol = "₾",
            legalFramework = "GDPR_LIKE", timezone = "Asia/Tbilisi",
            playStoreCountry = "GE",
            topPois = listOf(
                PoiTemplate("Old Town Tbilisi", "culture", "ძველი ქალაქი სულფურის აბანოებით", 41.6907, 44.8077, 4.8f, "Abanotubani, Tbilisi"),
                PoiTemplate("Narikala Fortress", "culture", "ნარიყალა — ციხესიმაგრე ქალაქს ზემოთ", 41.6879, 44.8089, 4.7f, "Narikala, Tbilisi"),
                PoiTemplate("Shardeni Street", "gastronomy", "შარდენის ქუჩა — რესტორნები და ბარები", 41.6915, 44.8082, 4.6f, "Shardeni, Tbilisi"),
                PoiTemplate("Fabrika", "nightlife", "Post-industrial creative hub", 41.6936, 44.7985, 4.5f, "8 Egnate Ninoshvili St"),
                PoiTemplate("Mtatsminda Park", "nature", "Park mit Panoramablick auf Tbilisi", 41.6947, 44.7866, 4.6f, "Mtatsminda, Tbilisi")
            ),
            topEvents = listOf(
                EventTemplate("Tbilisoba", "Stadtfest mit Wein und Kultur", 10, true),
                EventTemplate("Alaverdoba", "Traditionelles Herbstweinfest", 9, true)
            )
        ),

        // =====================================================================
        // 9. SÜDAFRIKA - Kapstadt
        // =====================================================================
        CountryConfig(
            code = "ZA", flavorName = "southafrica",
            applicationId = "com.insider.travel.southafrica",
            appName = "Cape Town Insider",
            primaryCity = "Cape Town", countryName = "Südafrika",
            defaultLocale = "en", supportedLocales = listOf("en", "af", "zu", "xh", "de", "fr", "es", "pt", "nl", "it"),
            lat = -33.9249, lng = 18.4241, mapZoom = 11f,
            currencyCode = "ZAR", currencySymbol = "R",
            legalFramework = "POPIA", timezone = "Africa/Johannesburg",
            playStoreCountry = "ZA",
            topPois = listOf(
                PoiTemplate("Table Mountain", "nature", "Iconic flat-topped mountain", -33.9628, 18.4098, 4.9f, "Table Mountain National Park"),
                PoiTemplate("Camps Bay Beach", "beach", "Stunning beach with mountain backdrop", -33.9508, 18.3777, 4.7f, "Camps Bay, Cape Town"),
                PoiTemplate("V&A Waterfront", "shopping", "Premier shopping & dining destination", -33.9036, 18.4209, 4.6f, "V&A Waterfront"),
                PoiTemplate("Boulders Beach", "nature", "African penguin colony", -34.1977, 18.4512, 4.8f, "Simon's Town"),
                PoiTemplate("Long Street", "nightlife", "Cape Town's party mile", -33.9222, 18.4167, 4.4f, "Long St, Cape Town")
            ),
            topEvents = listOf(
                EventTemplate("Cape Town Jazz Festival", "Africa's grandest jazz gathering", 3, true),
                EventTemplate("Cape Town Carnival", "Colourful street parade", 3, true)
            )
        ),

        // =====================================================================
        // 10. NIGERIA - Lagos
        // =====================================================================
        CountryConfig(
            code = "NG", flavorName = "nigeria",
            applicationId = "com.insider.travel.nigeria",
            appName = "Lagos Insider",
            primaryCity = "Lagos", countryName = "Nigeria",
            defaultLocale = "en", supportedLocales = listOf("en", "yo", "ha", "ig", "fr", "de", "es", "ar", "pt", "zh"),
            lat = 6.5244, lng = 3.3792, mapZoom = 11f,
            currencyCode = "NGN", currencySymbol = "₦",
            legalFramework = "NDPR", timezone = "Africa/Lagos",
            playStoreCountry = "NG",
            topPois = listOf(
                PoiTemplate("Lekki Conservation Centre", "nature", "Canopy walkway over mangroves", 6.4414, 3.5290, 4.5f, "Lekki, Lagos"),
                PoiTemplate("Nike Art Gallery", "culture", "5 floors of Nigerian art", 6.4329, 3.4757, 4.7f, "Lekki, Lagos"),
                PoiTemplate("Tarkwa Bay Beach", "beach", "Sheltered beach accessible by boat", 6.4104, 3.3963, 4.4f, "Lagos Island"),
                PoiTemplate("Freedom Park", "culture", "Former colonial prison, now cultural hub", 6.4533, 3.3945, 4.3f, "Broad St, Lagos Island"),
                PoiTemplate("Hard Rock Cafe Lagos", "gastronomy", "International dining on the lagoon", 6.4300, 3.4150, 4.2f, "Landmark Village, Victoria Island")
            ),
            topEvents = listOf(
                EventTemplate("Lagos Carnival", "Vibrant Afrobeats street festival", 4, true),
                EventTemplate("Felabration", "Celebrating Fela Kuti's legacy", 10, true)
            )
        ),

        // =====================================================================
        // 11. KENIA - Nairobi / Diani Beach
        // =====================================================================
        CountryConfig(
            code = "KE", flavorName = "kenya",
            applicationId = "com.insider.travel.kenya",
            appName = "Kenya Insider",
            primaryCity = "Nairobi", countryName = "Kenia",
            defaultLocale = "sw", supportedLocales = listOf("sw", "en", "de", "fr", "es", "it", "zh", "ja", "ar", "pt"),
            lat = -1.2921, lng = 36.8219, mapZoom = 11f,
            currencyCode = "KES", currencySymbol = "KSh",
            legalFramework = "DPA_KENYA", timezone = "Africa/Nairobi",
            playStoreCountry = "KE",
            topPois = listOf(
                PoiTemplate("Maasai Mara", "nature", "Mbuga maarufu ya wanyamapori", -1.5021, 35.1440, 4.9f, "Narok County"),
                PoiTemplate("Diani Beach", "beach", "Pwani nzuri na maji safi", -4.3167, 39.5833, 4.8f, "South Coast, Mombasa"),
                PoiTemplate("David Sheldrick Centre", "culture", "Kituo cha watoto wa tembo", -1.3684, 36.7932, 4.8f, "Nairobi National Park"),
                PoiTemplate("Carnivore Restaurant", "gastronomy", "Famous nyama choma experience", -1.3327, 36.7847, 4.6f, "Langata Rd, Nairobi"),
                PoiTemplate("Giraffe Centre", "nature", "Endangered Rothschild's giraffe sanctuary", -1.3781, 36.7449, 4.7f, "Langata, Nairobi")
            ),
            topEvents = listOf(
                EventTemplate("Great Wildebeest Migration", "Annual migration at Mara River", 8, true),
                EventTemplate("Lamu Cultural Festival", "Swahili arts and culture", 11, true)
            )
        ),

        // =====================================================================
        // 12. ÄGYPTEN - Hurghada / Kairo
        // =====================================================================
        CountryConfig(
            code = "EG", flavorName = "egypt",
            applicationId = "com.insider.travel.egypt",
            appName = "Egypt Insider",
            primaryCity = "Hurghada", countryName = "Ägypten",
            defaultLocale = "ar", supportedLocales = listOf("ar", "en", "de", "fr", "es", "it", "ru", "zh", "ja", "tr"),
            lat = 27.2579, lng = 33.8116, mapZoom = 12f,
            currencyCode = "EGP", currencySymbol = "E£",
            legalFramework = "PDPL_EGYPT", timezone = "Africa/Cairo",
            playStoreCountry = "EG",
            topPois = listOf(
                PoiTemplate("Pyramids of Giza", "culture", "الأهرامات — عجائب الدنيا القديمة", 29.9792, 31.1342, 4.9f, "Al Haram, Giza"),
                PoiTemplate("Mahmya Island", "beach", "جزيرة محمية بمياه فيروزية", 27.1833, 33.9333, 4.8f, "Red Sea, Hurghada"),
                PoiTemplate("Valley of the Kings", "culture", "وادي الملوك — مقابر الفراعنة", 25.7402, 32.6014, 4.9f, "Luxor"),
                PoiTemplate("Khan el-Khalili", "shopping", "سوق تاريخي في القاهرة القديمة", 30.0477, 31.2626, 4.5f, "Islamic Cairo"),
                PoiTemplate("Marina El Gouna", "gastronomy", "مطاعم وبارات على البحر", 27.1906, 33.6804, 4.6f, "El Gouna, Hurghada")
            ),
            topEvents = listOf(
                EventTemplate("Sound & Light Show Pyramids", "Historic spectacle at Giza", 1, true),
                EventTemplate("El Gouna Film Festival", "Egyptian cinema celebration", 10, true)
            )
        ),

        // =====================================================================
        // 13. BOTSWANA - Maun / Okavango Delta
        // =====================================================================
        CountryConfig(
            code = "BW", flavorName = "botswana",
            applicationId = "com.insider.travel.botswana",
            appName = "Botswana Insider",
            primaryCity = "Maun", countryName = "Botswana",
            defaultLocale = "en", supportedLocales = listOf("en", "tn", "de", "fr", "es", "it", "af", "pt", "zh", "ja"),
            lat = -19.9833, lng = 23.4167, mapZoom = 10f,
            currencyCode = "BWP", currencySymbol = "P",
            legalFramework = "DPA_BOTSWANA", timezone = "Africa/Gaborone",
            playStoreCountry = "BW",
            topPois = listOf(
                PoiTemplate("Okavango Delta", "nature", "UNESCO World Heritage inland delta", -19.2857, 22.8542, 4.9f, "Okavango, Northwest"),
                PoiTemplate("Chobe National Park", "nature", "50,000+ elephants in one park", -18.5200, 25.1500, 4.9f, "Kasane, Chobe"),
                PoiTemplate("Makgadikgadi Pans", "nature", "Largest salt flats in the world", -20.7500, 25.2500, 4.7f, "Central District"),
                PoiTemplate("Maun Backpackers", "gastronomy", "Gateway to the Delta", -19.9833, 23.4167, 4.3f, "Maun"),
                PoiTemplate("Tsodilo Hills", "culture", "Ancient San rock art, 'Louvre of the Desert'", -18.7471, 21.7274, 4.6f, "Northwest District")
            ),
            topEvents = listOf(
                EventTemplate("Maun International Arts Festival", "Music and arts celebration", 5, true),
                EventTemplate("Kuru Dance Festival", "San Bushmen traditional dance", 8, true)
            )
        ),

        // =====================================================================
        // 14. KAP VERDE - Sal / Boa Vista
        // =====================================================================
        CountryConfig(
            code = "CV", flavorName = "capeverde",
            applicationId = "com.insider.travel.capeverde",
            appName = "Cabo Verde Insider",
            primaryCity = "Santa Maria", countryName = "Kap Verde",
            defaultLocale = "pt", supportedLocales = listOf("pt", "en", "fr", "de", "es", "it", "nl", "ru", "zh", "ar"),
            lat = 16.5964, lng = -22.9056, mapZoom = 13f,
            currencyCode = "CVE", currencySymbol = "​$",
            legalFramework = "GDPR_LIKE", timezone = "Atlantic/Cape_Verde",
            playStoreCountry = "CV",
            topPois = listOf(
                PoiTemplate("Santa Maria Beach", "beach", "Praia dourada quilométrica", 16.5964, -22.9056, 4.8f, "Santa Maria, Sal"),
                PoiTemplate("Pedra de Lume Salt Crater", "nature", "Vulcão com lago de sal rosa", 16.7343, -22.8889, 4.6f, "Pedra de Lume, Sal"),
                PoiTemplate("Buracona Blue Eye", "nature", "Gruta com reflexo azul mágico", 16.7603, -22.9736, 4.7f, "Sal"),
                PoiTemplate("Morabeza Beach Bar", "gastronomy", "Bar na praia com cachupa", 16.5980, -22.9070, 4.5f, "Santa Maria, Sal"),
                PoiTemplate("Shark Bay", "nature", "Águas rasas com tubarões-limão", 16.5808, -22.9181, 4.7f, "Santa Maria, Sal")
            ),
            topEvents = listOf(
                EventTemplate("Santa Maria Festival", "Music and culture on the beach", 9, true),
                EventTemplate("Carnival de Mindelo", "Cape Verde's biggest carnival", 2, true)
            )
        ),

        // =====================================================================
        // 15. MAROKKO - Marrakesch
        // =====================================================================
        CountryConfig(
            code = "MA", flavorName = "morocco",
            applicationId = "com.insider.travel.morocco",
            appName = "Marrakech Insider",
            primaryCity = "Marrakech", countryName = "Marokko",
            defaultLocale = "ar", supportedLocales = listOf("ar", "fr", "en", "de", "es", "it", "nl", "pt", "zh", "ja"),
            lat = 31.6295, lng = -7.9811, mapZoom = 13f,
            currencyCode = "MAD", currencySymbol = "MAD",
            legalFramework = "PDPL_MOROCCO", timezone = "Africa/Casablanca",
            playStoreCountry = "MA",
            topPois = listOf(
                PoiTemplate("Jemaa el-Fnaa", "culture", "الساحة الأكثر شهرة في المغرب", 31.6259, -7.9891, 4.7f, "Medina, Marrakech"),
                PoiTemplate("Jardin Majorelle", "nature", "Yves Saint Laurent's blue garden", 31.6417, -8.0033, 4.8f, "Rue Yves Saint Laurent"),
                PoiTemplate("Bahia Palace", "culture", "قصر البهية — روائع العمارة المغربية", 31.6218, -7.9826, 4.7f, "Medina"),
                PoiTemplate("Café des Épices", "gastronomy", "Rooftop terrace über den Souks", 31.6312, -7.9875, 4.5f, "Rahba Kedima"),
                PoiTemplate("Essaouira Day Trip", "beach", "Küstenstadt 2h von Marrakesch", 31.5131, -9.7700, 4.6f, "Essaouira")
            ),
            topEvents = listOf(
                EventTemplate("Marrakech International Film Festival", "Star-studded cinema event", 12, true),
                EventTemplate("Gnaoua World Music Festival", "Spiritual music in Essaouira", 6, true)
            )
        ),

        // =====================================================================
        // 16. TANSANIA (Sansibar) - Sansibar
        // =====================================================================
        CountryConfig(
            code = "TZ", flavorName = "tanzania",
            applicationId = "com.insider.travel.zanzibar",
            appName = "Zanzibar Insider",
            primaryCity = "Zanzibar", countryName = "Tansania",
            defaultLocale = "sw", supportedLocales = listOf("sw", "en", "de", "fr", "es", "it", "ar", "pt", "zh", "ja"),
            lat = -6.1659, lng = 39.2026, mapZoom = 11f,
            currencyCode = "TZS", currencySymbol = "TSh",
            legalFramework = "GDPR_LIKE", timezone = "Africa/Dar_es_Salaam",
            playStoreCountry = "TZ",
            topPois = listOf(
                PoiTemplate("Nungwi Beach", "beach", "Pwani ya kifahari kaskazini mwa kisiwa", -5.7269, 39.2967, 4.8f, "Nungwi, Zanzibar"),
                PoiTemplate("Stone Town", "culture", "UNESCO World Heritage medina", -6.1622, 39.1925, 4.7f, "Stone Town, Zanzibar City"),
                PoiTemplate("The Rock Restaurant", "gastronomy", "Restaurant on a rock in the ocean", -6.2733, 39.4703, 4.8f, "Pingwe, Zanzibar"),
                PoiTemplate("Prison Island", "nature", "Aldabra giant tortoise sanctuary", -6.1294, 39.1714, 4.5f, "Changuu Island"),
                PoiTemplate("Jozani Forest", "nature", "Home of red colobus monkeys", -6.2722, 39.4142, 4.6f, "Jozani, Zanzibar")
            ),
            topEvents = listOf(
                EventTemplate("Zanzibar International Film Festival", "East African cinema", 7, true),
                EventTemplate("Sauti za Busara", "Swahili music festival in Stone Town", 2, true)
            )
        ),

        // =====================================================================
        // 17. VIETNAM - Ho Chi Minh City / Da Nang
        // =====================================================================
        CountryConfig(
            code = "VN", flavorName = "vietnam",
            applicationId = "com.insider.travel.vietnam",
            appName = "Vietnam Insider",
            primaryCity = "Ho Chi Minh City", countryName = "Vietnam",
            defaultLocale = "vi", supportedLocales = listOf("vi", "en", "zh", "ja", "ko", "fr", "de", "es", "ru", "th"),
            lat = 10.8231, lng = 106.6297, mapZoom = 12f,
            currencyCode = "VND", currencySymbol = "₫",
            legalFramework = "PDPD_VIETNAM", timezone = "Asia/Ho_Chi_Minh",
            playStoreCountry = "VN",
            topPois = listOf(
                PoiTemplate("Ha Long Bay", "nature", "Vịnh Hạ Long — Di sản UNESCO", 20.9101, 107.1839, 4.9f, "Quảng Ninh"),
                PoiTemplate("Hoi An Ancient Town", "culture", "Phố cổ Hội An đèn lồng", 15.8801, 108.3380, 4.8f, "Hội An, Quảng Nam"),
                PoiTemplate("My Khe Beach", "beach", "Bãi biển đẹp nhất Đà Nẵng", 16.0544, 108.2472, 4.7f, "Đà Nẵng"),
                PoiTemplate("Bui Vien Street", "nightlife", "Phố Tây sôi động Sài Gòn", 10.7680, 106.6934, 4.4f, "Phạm Ngũ Lão, Q.1"),
                PoiTemplate("Ben Thanh Market", "gastronomy", "Chợ Bến Thành — ẩm thực đường phố", 10.7725, 106.6981, 4.5f, "Q.1, TP.HCM")
            ),
            topEvents = listOf(
                EventTemplate("Tết Nguyên Đán", "Vietnamese Lunar New Year", 2, true),
                EventTemplate("Hoi An Lantern Festival", "Full moon festival with lanterns", 1, true)
            )
        ),

        // =====================================================================
        // 18. BHUTAN - Paro / Thimphu
        // =====================================================================
        CountryConfig(
            code = "BT", flavorName = "bhutan",
            applicationId = "com.insider.travel.bhutan",
            appName = "Bhutan Insider",
            primaryCity = "Thimphu", countryName = "Bhutan",
            defaultLocale = "dz", supportedLocales = listOf("dz", "en", "hi", "ne", "zh", "ja", "de", "fr", "es", "ko"),
            lat = 27.4728, lng = 89.6393, mapZoom = 12f,
            currencyCode = "BTN", currencySymbol = "Nu.",
            legalFramework = "GDPR_LIKE", timezone = "Asia/Thimphu",
            playStoreCountry = "BT",
            topPois = listOf(
                PoiTemplate("Tiger's Nest (Paro Taktsang)", "culture", "Iconic monastery on a cliff at 3,120m", 27.4913, 89.3633, 4.9f, "Paro Valley"),
                PoiTemplate("Punakha Dzong", "culture", "The Palace of Great Happiness", 27.6035, 89.8625, 4.8f, "Punakha"),
                PoiTemplate("Buddha Dordenma", "culture", "Giant Buddha statue overlooking Thimphu", 27.4595, 89.6480, 4.7f, "Kuenselphodrang, Thimphu"),
                PoiTemplate("Dochula Pass", "nature", "Mountain pass with 108 stupas", 27.5009, 89.7511, 4.8f, "Thimphu-Punakha Highway"),
                PoiTemplate("Folk Heritage Museum", "culture", "Traditional Bhutanese life exhibit", 27.4775, 89.6364, 4.4f, "Thimphu")
            ),
            topEvents = listOf(
                EventTemplate("Paro Tshechu", "Sacred mask dance festival", 3, true),
                EventTemplate("Thimphu Tshechu", "Biggest festival in the capital", 9, true)
            )
        ),

        // =====================================================================
        // 19. THAILAND - Bangkok / Phuket
        // =====================================================================
        CountryConfig(
            code = "TH", flavorName = "thailand",
            applicationId = "com.insider.travel.thailand",
            appName = "Thailand Insider",
            primaryCity = "Bangkok", countryName = "Thailand",
            defaultLocale = "th", supportedLocales = listOf("th", "en", "zh", "ja", "ko", "de", "fr", "ru", "es", "ar"),
            lat = 13.7563, lng = 100.5018, mapZoom = 12f,
            currencyCode = "THB", currencySymbol = "฿",
            legalFramework = "PDPA_THAILAND", timezone = "Asia/Bangkok",
            playStoreCountry = "TH",
            topPois = listOf(
                PoiTemplate("Grand Palace", "culture", "พระบรมมหาราชวัง — สถาปัตยกรรมอันวิจิตร", 13.7500, 100.4914, 4.8f, "Phra Nakhon, Bangkok"),
                PoiTemplate("Phi Phi Islands", "beach", "เกาะพีพี — หาดทรายขาวน้ำใส", 7.7407, 98.7784, 4.7f, "Krabi Province"),
                PoiTemplate("Chatuchak Weekend Market", "shopping", "ตลาดนัดจตุจักร — 15,000 แผง", 13.7998, 100.5500, 4.6f, "Chatuchak, Bangkok"),
                PoiTemplate("Khao San Road", "nightlife", "ถนนข้าวสาร — backpacker paradise", 13.7589, 100.4979, 4.4f, "Phra Nakhon"),
                PoiTemplate("Chinatown Bangkok", "gastronomy", "เยาวราช — street food heaven", 13.7415, 100.5105, 4.7f, "Yaowarat Rd")
            ),
            topEvents = listOf(
                EventTemplate("Songkran Water Festival", "Thai New Year water fight", 4, true),
                EventTemplate("Loi Krathong", "Floating lantern festival", 11, true)
            )
        ),

        // =====================================================================
        // 20. TADSCHIKISTAN - Dushanbe / Pamir
        // =====================================================================
        CountryConfig(
            code = "TJ", flavorName = "tajikistan",
            applicationId = "com.insider.travel.tajikistan",
            appName = "Tajikistan Insider",
            primaryCity = "Dushanbe", countryName = "Tadschikistan",
            defaultLocale = "tg", supportedLocales = listOf("tg", "ru", "en", "fa", "uz", "de", "fr", "zh", "ar", "tr"),
            lat = 38.5598, lng = 68.7870, mapZoom = 11f,
            currencyCode = "TJS", currencySymbol = "SM",
            legalFramework = "GDPR_LIKE", timezone = "Asia/Dushanbe",
            playStoreCountry = "TJ",
            topPois = listOf(
                PoiTemplate("Pamir Highway", "nature", "Second-highest highway in the world", 38.4000, 73.6000, 4.9f, "GBAO Region"),
                PoiTemplate("Iskanderkul Lake", "nature", "Кӯли Искандаркул — Alexander's Lake", 39.0833, 68.3667, 4.8f, "Fann Mountains"),
                PoiTemplate("National Museum of Tajikistan", "culture", "Тоҷикистон — история и культура", 38.5644, 68.7737, 4.5f, "Dushanbe"),
                PoiTemplate("Rudaki Park", "nature", "Central park of Dushanbe", 38.5576, 68.7740, 4.4f, "Dushanbe"),
                PoiTemplate("Wakhan Corridor", "nature", "Remote valley between Afghanistan & China", 36.9000, 71.5000, 4.8f, "GBAO")
            ),
            topEvents = listOf(
                EventTemplate("Nowruz", "Persian New Year celebration", 3, true),
                EventTemplate("Independence Day", "National celebration", 9, true)
            )
        ),

        // =====================================================================
        // 21. SAUDI-ARABIEN - Jeddah / AlUla
        // =====================================================================
        CountryConfig(
            code = "SA", flavorName = "saudiarabia",
            applicationId = "com.insider.travel.saudiarabia",
            appName = "Saudi Insider",
            primaryCity = "Jeddah", countryName = "Saudi-Arabien",
            defaultLocale = "ar", supportedLocales = listOf("ar", "en", "ur", "hi", "fr", "de", "es", "id", "zh", "tr"),
            lat = 21.4858, lng = 39.1925, mapZoom = 12f,
            currencyCode = "SAR", currencySymbol = "﷼",
            legalFramework = "PDPL_SAUDI", timezone = "Asia/Riyadh",
            playStoreCountry = "SA",
            topPois = listOf(
                PoiTemplate("Al-Balad (Historic Jeddah)", "culture", "البلد — حي تاريخي يونسكو", 21.4851, 39.1862, 4.7f, "Al-Balad, Jeddah"),
                PoiTemplate("AlUla / Hegra", "culture", "مدائن صالح — أول موقع يونسكو سعودي", 26.7833, 37.9500, 4.9f, "AlUla, Medina Region"),
                PoiTemplate("King Fahd Fountain", "culture", "أطول نافورة في العالم", 21.4898, 39.1532, 4.5f, "Jeddah Corniche"),
                PoiTemplate("Jeddah Corniche", "nature", "ممشى كورنيش جدة 30 كم", 21.5361, 39.1068, 4.6f, "Jeddah"),
                PoiTemplate("The Red Sea Project", "beach", "Luxury beach development NEOM", 25.9845, 36.4906, 4.4f, "Tabuk Region")
            ),
            topEvents = listOf(
                EventTemplate("Jeddah Season", "Entertainment & culture mega-event", 6, true),
                EventTemplate("Riyadh Season", "Largest entertainment season globally", 10, true)
            )
        ),

        // =====================================================================
        // 22. VAE (DUBAI) - Dubai
        // =====================================================================
        CountryConfig(
            code = "AE", flavorName = "dubai",
            applicationId = "com.insider.travel.dubai",
            appName = "Dubai Insider",
            primaryCity = "Dubai", countryName = "VAE",
            defaultLocale = "ar", supportedLocales = listOf("ar", "en", "hi", "ur", "fa", "de", "fr", "ru", "zh", "es"),
            lat = 25.2048, lng = 55.2708, mapZoom = 11f,
            currencyCode = "AED", currencySymbol = "د.إ",
            legalFramework = "PDPL_UAE", timezone = "Asia/Dubai",
            playStoreCountry = "AE",
            topPois = listOf(
                PoiTemplate("Burj Khalifa", "culture", "أطول مبنى في العالم — 828م", 25.1972, 55.2744, 4.9f, "Downtown Dubai"),
                PoiTemplate("JBR Beach", "beach", "شاطئ الجي بي آر العائلي", 25.0790, 55.1367, 4.7f, "Jumeirah Beach Residence"),
                PoiTemplate("Dubai Mall", "shopping", "Largest mall in the world", 25.1985, 55.2796, 4.7f, "Downtown Dubai"),
                PoiTemplate("White Dubai", "nightlife", "Premier rooftop nightclub", 25.1851, 55.2535, 4.5f, "Meydan Racecourse"),
                PoiTemplate("Al Fahidi Historical District", "culture", "Old Dubai heritage neighborhood", 25.2635, 55.2974, 4.6f, "Bur Dubai")
            ),
            topEvents = listOf(
                EventTemplate("Dubai Shopping Festival", "Mega sales & entertainment", 1, true),
                EventTemplate("Dubai Food Festival", "Culinary extravaganza", 3, true)
            )
        ),

        // =====================================================================
        // 23. JAPAN - Tokyo / Kyoto
        // =====================================================================
        CountryConfig(
            code = "JP", flavorName = "japan",
            applicationId = "com.insider.travel.japan",
            appName = "Japan Insider",
            primaryCity = "Tokyo", countryName = "Japan",
            defaultLocale = "ja", supportedLocales = listOf("ja", "en", "zh", "ko", "de", "fr", "es", "pt", "th", "vi"),
            lat = 35.6762, lng = 139.6503, mapZoom = 11f,
            currencyCode = "JPY", currencySymbol = "¥",
            legalFramework = "APPI_JAPAN", timezone = "Asia/Tokyo",
            playStoreCountry = "JP",
            topPois = listOf(
                PoiTemplate("Senso-ji Temple", "culture", "浅草寺 — 東京最古の寺院", 35.7148, 139.7967, 4.8f, "Asakusa, Taito"),
                PoiTemplate("Shibuya Crossing", "culture", "渋谷スクランブル交差点", 35.6595, 139.7005, 4.7f, "Shibuya, Tokyo"),
                PoiTemplate("Tsukiji Outer Market", "gastronomy", "築地場外市場 — 新鮮な寿司と海鮮", 35.6654, 139.7707, 4.7f, "Tsukiji, Chuo"),
                PoiTemplate("Shinjuku Golden Gai", "nightlife", "新宿ゴールデン街 — バーの迷路", 35.6942, 139.7036, 4.6f, "Kabukicho, Shinjuku"),
                PoiTemplate("Fushimi Inari", "culture", "伏見稲荷大社 — 千本鳥居", 34.9671, 135.7727, 4.9f, "Kyoto")
            ),
            topEvents = listOf(
                EventTemplate("Cherry Blossom Season", "Hanami — sakura viewing", 4, true),
                EventTemplate("Gion Matsuri", "Kyoto's famous summer festival", 7, true)
            )
        ),

        // =====================================================================
        // 24. AUSTRALIEN - Sydney / Gold Coast
        // =====================================================================
        CountryConfig(
            code = "AU", flavorName = "australia",
            applicationId = "com.insider.travel.australia",
            appName = "Australia Insider",
            primaryCity = "Sydney", countryName = "Australien",
            defaultLocale = "en", supportedLocales = listOf("en", "zh", "ja", "ko", "de", "fr", "es", "hi", "ar", "it"),
            lat = -33.8688, lng = 151.2093, mapZoom = 11f,
            currencyCode = "AUD", currencySymbol = "A$",
            legalFramework = "PRIVACY_ACT_AU", timezone = "Australia/Sydney",
            playStoreCountry = "AU",
            topPois = listOf(
                PoiTemplate("Bondi Beach", "beach", "Australia's most famous beach", -33.8915, 151.2767, 4.7f, "Bondi Beach, NSW"),
                PoiTemplate("Sydney Opera House", "culture", "UNESCO World Heritage icon", -33.8568, 151.2153, 4.9f, "Bennelong Point, Sydney"),
                PoiTemplate("Taronga Zoo", "nature", "Zoo with harbour views", -33.8435, 151.2414, 4.7f, "Mosman, NSW"),
                PoiTemplate("The Rocks", "gastronomy", "Historic precinct with restaurants", -33.8597, 151.2089, 4.6f, "The Rocks, Sydney"),
                PoiTemplate("Ivy Pool Club", "nightlife", "Rooftop pool bar in the CBD", -33.8698, 151.2095, 4.5f, "330 George St, Sydney")
            ),
            topEvents = listOf(
                EventTemplate("Vivid Sydney", "Festival of light, music & ideas", 5, true),
                EventTemplate("Sydney New Year's Eve", "World-famous harbour fireworks", 12, true)
            )
        ),

        // =====================================================================
        // 25. EU-SPEZIFISCH - Barcelona (Pan-European)
        // =====================================================================
        CountryConfig(
            code = "EU", flavorName = "europe",
            applicationId = "com.insider.travel.europe",
            appName = "Barcelona Insider",
            primaryCity = "Barcelona", countryName = "EU",
            defaultLocale = "es", supportedLocales = listOf("es", "ca", "en", "fr", "de", "it", "pt", "nl", "ru", "zh"),
            lat = 41.3874, lng = 2.1686, mapZoom = 12f,
            currencyCode = "EUR", currencySymbol = "€",
            legalFramework = "GDPR_EU", timezone = "Europe/Madrid",
            playStoreCountry = "ES",
            topPois = listOf(
                PoiTemplate("Sagrada Família", "culture", "Gaudí's unfinished masterpiece", 41.4036, 2.1744, 4.9f, "Carrer de Mallorca 401"),
                PoiTemplate("Barceloneta Beach", "beach", "City beach with buzzing promenade", 41.3797, 2.1899, 4.6f, "Barceloneta, Barcelona"),
                PoiTemplate("La Boqueria Market", "gastronomy", "Famous food market on La Rambla", 41.3816, 2.1718, 4.7f, "La Rambla 91"),
                PoiTemplate("Razzmatazz", "nightlife", "5-room superclub", 41.3975, 2.1918, 4.5f, "Carrer dels Almogàvers 122"),
                PoiTemplate("Park Güell", "nature", "Gaudí's colorful park UNESCO", 41.4145, 2.1527, 4.8f, "Carrer d'Olot, Barcelona")
            ),
            topEvents = listOf(
                EventTemplate("La Mercè", "Barcelona's biggest street festival", 9, true),
                EventTemplate("Primavera Sound", "Major music festival", 6, true)
            )
        )
    )

    fun getByCode(code: String): CountryConfig? = all.find { it.code == code }
    fun getByFlavor(flavor: String): CountryConfig? = all.find { it.flavorName == flavor }
}
