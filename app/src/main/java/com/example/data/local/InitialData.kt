package com.example.data.local

import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.data.model.MessageStatus
import com.example.data.model.MessageType
import com.example.data.model.UserProfile
import com.example.data.model.UserSettings

object InitialData {

    // Default base coordinates (e.g. San Francisco downtown)
    const val DEFAULT_LAT = 37.7749
    const val DEFAULT_LNG = -122.4194

    fun getInitialProfiles(): List<UserProfile> = listOf(
        UserProfile(
            id = "user_1",
            name = "Elena Rostova",
            age = 25,
            occupation = "Architect & Ceramicist",
            education = "UC Berkeley",
            city = "San Francisco, CA",
            bio = "Designing minimal spaces by day, sculpting imperfect pottery by night. Looking for someone who appreciates spontaneous road trips and golden hour light.",
            latitude = DEFAULT_LAT + 0.008, // ~0.9 km away
            longitude = DEFAULT_LNG + 0.004,
            photosCsv = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=800&q=80",
            interestsCsv = "Architecture,Pottery,Film Photography,Espresso,Indie Folk",
            promptQuestion = "A life goal of mine is...",
            promptAnswer = "To build a sun-drenched wooden cabin surrounded by redwoods and host candlelit dinners.",
            isVerified = true,
            verificationBadgeText = "Selfie & Liveness Verified ✓",
            isLiked = true,
            isPassed = false,
            isMatch = true,
            matchTimestamp = System.currentTimeMillis() - 7200000,
            lastActiveText = "Online now",
            compatibilityScore = 96
        ),
        UserProfile(
            id = "user_2",
            name = "Marcus Chen",
            age = 27,
            occupation = "Sound Designer & Musician",
            education = "Stanford University",
            city = "San Francisco, CA",
            bio = "Recording everyday ambient textures and turning them into vinyl tracks. Huge fan of spicy ramen, record shop digging, and late night city strolls.",
            latitude = DEFAULT_LAT - 0.012, // ~1.4 km away
            longitude = DEFAULT_LNG + 0.009,
            photosCsv = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=800&q=80",
            interestsCsv = "Vinyl Records,Audio Production,Analog Synths,Japanese Cuisine,Cycling",
            promptQuestion = "My most controversial opinion is...",
            promptAnswer = "Matcha lattes are superior to pour-overs, especially on rainy Sunday mornings.",
            isVerified = true,
            verificationBadgeText = "Identity & Face Verified ✓",
            isLiked = true,
            isPassed = false,
            isMatch = true,
            matchTimestamp = System.currentTimeMillis() - 86400000,
            lastActiveText = "Active 15m ago",
            compatibilityScore = 91
        ),
        UserProfile(
            id = "user_3",
            name = "Maya Lin",
            age = 24,
            occupation = "Botanical Illustrator",
            education = "Rhode Island School of Design",
            city = "Oakland, CA",
            bio = "Always covered in watercolor paint. Plant mom of 38 houseplants (yes, they all have names). Let's go to the botanical gardens and trade plant clippings.",
            latitude = DEFAULT_LAT + 0.035, // ~4.2 km away
            longitude = DEFAULT_LNG - 0.025,
            photosCsv = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=800&q=80",
            interestsCsv = "Illustration,Plants,Hiking,Thrifting,Baking Sourdough",
            promptQuestion = "I geek out on...",
            promptAnswer = "The mathematical fractal geometry of ferns and how moss can survive in practically any corner.",
            isVerified = true,
            verificationBadgeText = "Liveness Verified ✓",
            isLiked = false,
            isPassed = false,
            isMatch = false,
            lastActiveText = "Active 2h ago",
            compatibilityScore = 88
        ),
        UserProfile(
            id = "user_4",
            name = "Julian Thorne",
            age = 28,
            occupation = "Aerospace Engineer & Pilot",
            education = "MIT",
            city = "San Francisco, CA",
            bio = "Working on satellite propulsion systems. Weekend private pilot. Looking for a co-pilot for sunset fly-bys and mountain getaways.",
            latitude = DEFAULT_LAT - 0.045, // ~5.3 km away
            longitude = DEFAULT_LNG - 0.030,
            photosCsv = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=800&q=80",
            interestsCsv = "Aviation,Bouldering,Astrophotography,Craft Coffee,Sci-Fi",
            promptQuestion = "Together, we could...",
            promptAnswer = "Pack a weekend bag on Friday evening, rent a two-seater Cessna, and watch the Pacific sunset from 5,000 feet.",
            isVerified = true,
            verificationBadgeText = "Biometric & Pilot ID Verified ✓",
            isLiked = false,
            isPassed = false,
            isMatch = false,
            lastActiveText = "Active now",
            compatibilityScore = 94
        ),
        UserProfile(
            id = "user_5",
            name = "Sofia Morales",
            age = 26,
            occupation = "Documentary Filmmaker",
            education = "Columbia University",
            city = "San Francisco, CA",
            bio = "Telling stories about ocean conservation and coastal communities. Can usually be found in wetsuits, vintage bookstores, or editing bays.",
            latitude = DEFAULT_LAT + 0.015, // ~2.1 km away
            longitude = DEFAULT_LNG - 0.018,
            photosCsv = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80",
            interestsCsv = "Surfing,Documentary,Ocean Conservation,Tacos,Film Festivals",
            promptQuestion = "Two truths and a lie...",
            promptAnswer = "1. I freedive down to 25 meters. 2. I've never eaten an avocado. 3. I won an award at Sundance.",
            isVerified = false,
            verificationBadgeText = "Pending Verification",
            isLiked = false,
            isPassed = false,
            isMatch = false,
            lastActiveText = "Active 30m ago",
            compatibilityScore = 85
        ),
        UserProfile(
            id = "user_6",
            name = "Leo Sterling",
            age = 29,
            occupation = "Pastry Chef & Bakery Founder",
            education = "Le Cordon Bleu Paris",
            city = "San Francisco, CA",
            bio = "Waking up at 4 AM to laminate croissant dough. Life's too short for bad butter. I promise to cook you the best French breakfast you've ever had.",
            latitude = DEFAULT_LAT - 0.022, // ~2.8 km away
            longitude = DEFAULT_LNG + 0.015,
            photosCsv = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=800&q=80",
            interestsCsv = "Baking,French Cooking,Wine Tasting,Running Marathons,Jazz",
            promptQuestion = "The best way to ask me out is...",
            promptAnswer = "Pick a local bakery you've always wanted to try and let's judge their pain au chocolat together.",
            isVerified = true,
            verificationBadgeText = "Liveness Verified ✓",
            isLiked = false,
            isPassed = false,
            isMatch = false,
            lastActiveText = "Online now",
            compatibilityScore = 93
        ),
        UserProfile(
            id = "user_7",
            name = "Zoe Katsaros",
            age = 25,
            occupation = "Marine Biologist",
            education = "Stanford Hopkins Marine Station",
            city = "Monterey / SF Bay",
            bio = "Studying bioluminescent jellyfish and kelp forest ecosystems. When on land, I love museum dates, thrift fashion, and spicy cocktails.",
            latitude = DEFAULT_LAT + 0.065, // ~8.2 km away
            longitude = DEFAULT_LNG + 0.040,
            photosCsv = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=800&q=80",
            interestsCsv = "Marine Biology,Scuba Diving,Museums,Vintage Fashion,Bioluminescence",
            promptQuestion = "I want someone who looks at me like...",
            promptAnswer = "I look at a freshly brewed morning cappuccino on a misty sea cliff.",
            isVerified = true,
            verificationBadgeText = "Identity Verified ✓",
            isLiked = false,
            isPassed = false,
            isMatch = false,
            lastActiveText = "Active 5m ago",
            compatibilityScore = 90
        )
    )

    fun getInitialMessages(): List<ChatMessage> {
        val now = System.currentTimeMillis()
        return listOf(
            ChatMessage(
                id = "msg_1",
                profileId = "user_1",
                sender = MessageSender.PARTNER,
                text = "Hey Alex! Loved your profile photo with the film camera. What model are you shooting on?",
                timestamp = now - 3600000,
                status = MessageStatus.READ,
                type = MessageType.TEXT
            ),
            ChatMessage(
                id = "msg_2",
                profileId = "user_1",
                sender = MessageSender.USER,
                text = "Hey Elena! That's an old Olympus OM-1 with a 50mm f/1.4 lens. Love mechanical shutter feel!",
                timestamp = now - 2400000,
                status = MessageStatus.READ,
                type = MessageType.TEXT
            ),
            ChatMessage(
                id = "msg_3",
                profileId = "user_1",
                sender = MessageSender.PARTNER,
                text = "No way, that's such a classic! I actually sculpted a set of espresso cups inspired by vintage camera dials last week haha.",
                timestamp = now - 1200000,
                status = MessageStatus.READ,
                type = MessageType.TEXT
            ),
            ChatMessage(
                id = "msg_4",
                profileId = "user_1",
                sender = MessageSender.PARTNER,
                text = "Would you want to grab coffee this Friday at Sightglass and test out those cups?",
                timestamp = now - 600000,
                status = MessageStatus.DELIVERED,
                type = MessageType.DATE_INVITE,
                payload = "Sightglass Coffee • Friday 4:00 PM"
            ),
            ChatMessage(
                id = "msg_m1",
                profileId = "user_2",
                sender = MessageSender.PARTNER,
                text = "Hey! You matched with Marcus. Send a message to start the conversation!",
                timestamp = now - 86400000,
                status = MessageStatus.READ,
                type = MessageType.ICEBREAKER,
                payload = "Ask about his favorite vinyl record"
            )
        )
    }
}
