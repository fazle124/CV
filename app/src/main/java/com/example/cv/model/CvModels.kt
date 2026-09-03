package com.example.cv.model

data class CvHeader(
    val name: String = "Md. Fazle Rabbi",
    val address: String = "Bason, Gazipur, Dhaka",
    val phone: String = "01720094069",
    val email: String = "fazlerabbi66122@gmail.com",
    val linkedin: String = "",
    val photo: String = "profile",
    val signature: String = "signature"
)

data class CvSection(
    val id: String,
    val title: String,
    val body: String
)

data class CvDocument(
    val id: String,
    val name: String,
    val dateAdded: String,
    val imageUri: String? = null
)

object DefaultCvData {
    val defaultHeader = CvHeader(
        name = "Md. Fazle Rabbi",
        address = "Bason, Gazipur, Dhaka",
        phone = "01720094069",
        email = "fazlerabbi66122@gmail.com",
        linkedin = "",
        photo = "profile",
        signature = "signature"
    )

    val defaultSections = listOf(
        CvSection(
            id = "career",
            title = "CAREER OBJECTIVE",
            body = "To build a professional career in the garments and apparel industry by applying my\n" +
                    "experience in quality inspection, reporting, and documentation. I aim to contribute to\n" +
                    "maintaining high quality standards while improving my skills and supporting organizational\n" +
                    "growth."
        ),
        CvSection(
            id = "job",
            title = "JOB EXPERIENCE",
            body = "Colossus Apparel Limited\n" +
                    "Designation: Quality Reporter\n" +
                    "Experience: 3 Years\n\n" +
                    "Key Responsibilities:\n" +
                    "• Prepare daily, weekly, and monthly quality inspection reports\n" +
                    "• Maintain quality records and documentation\n" +
                    "• Coordinate with Quality Inspectors and Production Team\n" +
                    "• Ensure compliance with quality standards and buyer requirements\n" +
                    "• Data analysis and reporting using Excel\n\n" +
                    "Pacific Quality Control Center Ltd\n" +
                    "Designation: Quality Inspector\n" +
                    "Experience: 1 Year\n\n" +
                    "Key Responsibilities:\n" +
                    "• Inspect garments at different production stages\n" +
                    "• Identify defects and ensure corrective actions\n" +
                    "• Follow buyer quality standards and AQL system\n" +
                    "• Prepare inspection reports and feedback"
        ),
        CvSection(
            id = "education",
            title = "EDUCATIONAL QUALIFICATION",
            body = "Bachelor of Science (BSc)\n" +
                    "Institute : Govt. Abdul Khalek Memorial College - Dewanganj\n" +
                    "Group : BSc\n" +
                    "Passing Year : 2023\n" +
                    "Result : 2.64\n\n" +
                    "Higher Secondary Certificate (HSC)\n" +
                    "Institute : Sanandabari College\n" +
                    "Group : Science\n" +
                    "Passing Year : 2020\n" +
                    "Result : 3.67\n\n" +
                    "Secondary School Certificate (SSC)\n" +
                    "Institute : Sanandabari High School\n" +
                    "Group : Science\n" +
                    "Passing Year : 2018\n" +
                    "Result : 3.56"
        ),
        CvSection(
            id = "computer",
            title = "COMPUTER SKILLS",
            body = "• Microsoft Excel (Advanced level – reporting, data analysis)\n" +
                    "• Microsoft Word (official documentation, reporting)\n" +
                    "• Microsoft PowerPoint\n" +
                    "• Data entry and record management\n" +
                    "• Office documentation and email communication"
        ),
        CvSection(
            id = "languages",
            title = "LANGUAGES",
            body = "Bangla\nEnglish"
        ),
        CvSection(
            id = "personal",
            title = "PERSONAL INFORMATION",
            body = "• Name : Md. Fazle Rabbi\n" +
                    "• Father’s Name : Md. Badsha Mondal\n" +
                    "• Mother’s Name : Mst. Nazma Begum\n" +
                    "• Date of Birth : 23 Oct 2002\n" +
                    "• Gender : Male\n" +
                    "• Marital Status : Unmarried\n" +
                    "• National ID Number : 2425819188\n" +
                    "• Religion : Islam\n" +
                    "• Permanent Address : Uttar Mokirchar, Sanandabari, Dewanganj, Jamalpur"
        ),
        CvSection(
            id = "declaration",
            title = "DECLARATION",
            body = "I hereby declare that the above information is true and correct to the best of my knowledge and belief.\n\n" +
                    "Signature:\nDate:"
        )
    )

    val defaultDocuments = listOf(
        CvDocument(
            id = "doc1",
            name = "National ID Card (NID)",
            dateAdded = "Verified",
            imageUri = null
        ),
        CvDocument(
            id = "doc2",
            name = "BSc Certificate (Govt. AKMC)",
            dateAdded = "2023",
            imageUri = null
        ),
        CvDocument(
            id = "doc3",
            name = "Experience Certificate (Colossus Apparel)",
            dateAdded = "3 Years",
            imageUri = null
        )
    )
}
