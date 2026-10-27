package com.example.data.repository

import com.example.data.model.PakistaniCity
import com.example.data.model.Salon
import com.example.data.model.SalonGenderType
import com.example.data.model.SalonReview
import com.example.data.model.SalonService
import com.example.data.model.Stylist

object SalonCatalog {
    val sampleSalons: List<Salon> = listOf(
        Salon(
            id = "haider_salon_naindowal",
            name = "Haider Salon (حیدر سیلون)",
            nameUrdu = "حیدر سیلون نیندووال",
            tagline = "Village Naindowal, Lalamusa - Executive Haircut, Beard Fade, Charcoal Facial & Grooming",
            city = PakistaniCity.LALAMUSA,
            areaAddress = "Main Bazaar, Village Naindowal, Lalamusa, Gujrat",
            phone = "0300-8451920",
            rating = 4.95,
            reviewCount = 185,
            genderType = SalonGenderType.MEN,
            startingPricePkr = 300,
            openingHours = "09:00 AM - 10:30 PM",
            isFeatured = true,
            facilities = listOf("Village Naindowal", "Air Conditioned", "Sanitized Tools", "Generator Backup 24/7", "Head Massage", "Chai & Refreshments"),
            services = listOf(
                SalonService("hs_1", "Haider Signature Haircut & Styling", "حیدر سگنیچر ہیئر کٹ", "Haircut", 30, 400, "Expert scissor cut, skin fade, taper fade with styling spray & powder."),
                SalonService("hs_2", "Beard Setting & Sharp Razor Lining", "داڑھی سیٹنگ اور بلیڈ لائیننگ", "Beard", 25, 250, "Detailed beard shaping, razor lining contour, and organic oil finish."),
                SalonService("hs_3", "Royal Shahi Shave with Hot Towel", "شاہی شیو اور گرم تولیہ", "Shave", 25, 300, "Rich foam lather, steaming hot towel treatment, and cooling aftershave splash."),
                SalonService("hs_4", "Herbal Glow & Charcoal Blackhead Facial", "چارکول اور ہربل فیشل", "Facial", 45, 1200, "Deep skin cleansing, steam, blackhead scrub, face polish, and mud mask."),
                SalonService("hs_5", "Full Wedding Groom Package (دولہا پیکج)", "دولہا کا مکمل شاہی گرومنگ پیکج", "Wedding Package", 90, 5000, "VIP wedding makeover: Designer haircut, beard perfection, bridal facial, bleach, hair wash & massage."),
                SalonService("hs_6", "Hair Wash & Relaxing Head Champi Massage", "ہیئر واش اور سر کی مالش", "Hair Care", 20, 350, "Refreshing shampoo wash followed by traditional almond/mustard oil scalp massage."),
                SalonService("hs_7", "Hair Color & Natural Black Beard Dye", "ہیئر کلر اور ڈائی", "Hair Color", 30, 600, "Natural black or dark brown ammonia-free hair and beard dye application."),
                SalonService("hs_8", "Protein & Keratin Hair Treatment", "پروٹین اور کیراٹین ٹریٹمنٹ", "Treatment", 60, 2500, "Smooth, shiny hair straightening, anti-dandruff deep repair and conditioning."),
                SalonService("hs_9", "Classic Simple Haircut", "سادہ بال کٹنگ", "Haircut", 20, 250, "Standard regular scissor and machine haircut."),
                SalonService("hs_10", "Kids Haircut (بچوں کی کٹنگ)", "بچوں کا ہیئر کٹ", "Haircut", 15, 200, "Gentle and patient haircut styling for kids and boys."),
                SalonService("hs_11", "Gold Face Polish & Cleansing", "گولڈ فیس پالش اور کلینزنگ", "Facial", 35, 800, "Exfoliating gold polish, scrub and refreshing toner for instant brightness."),
                SalonService("hs_12", "Jumma Special Grooming Combo", "جمعہ مبارک سپیشل کومبو", "Special Combo", 50, 850, "Haircut + Beard Setting + Hot Towel Shave + Head Oil Massage."),
                SalonService("hs_13", "Nose & Ear Waxing Detailing", "ناک اور کان ویکسنگ", "Grooming", 10, 150, "Hygienic unwanted hair removal around nose and ears."),
                SalonService("hs_14", "Anti-Dandruff Cooling Scalp Wash", "اینٹی ڈینڈرف کولنگ واش", "Hair Care", 25, 450, "Menthol infused deep scalp dandruff clearing shampoo wash.")
            ),
            stylists = listOf(
                Stylist("st_h1", "Ustad Haider Ali", "Chief Master Barber & Owner (استاد حیدر)", 4.98, 12),
                Stylist("st_h2", "Hamza Haider", "Senior Fade & Beard Artist", 4.92, 6),
                Stylist("st_h3", "Usman Gujjar", "Skin Care & Facial Expert", 4.89, 5)
            ),
            reviews = listOf(
                SalonReview("rev_h1", "Chaudhry Bilal", "Village Naindowal", 5f, "Naindowal aur Lalamusa ka sab se behtareen salon! Ustad Haider ki cutting lajawab hai.", "Yesterday"),
                SalonReview("rev_h2", "Raza Butt", "Lalamusa", 5f, "Got my wedding groom package done here. Top-notch service and very reasonable rates.", "3 days ago"),
                SalonReview("rev_h3", "Waqas Warraich", "Kharian", 5f, "Very clean salon with AC and great head massage. Highly recommended!", "1 week ago")
            )
        )
    )

    fun getSalonById(id: String): Salon? {
        return sampleSalons.find { it.id == id }
    }
}
