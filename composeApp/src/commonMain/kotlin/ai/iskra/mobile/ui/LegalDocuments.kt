package ai.iskra.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class MobileLegalDocType {
    PRIVACY, TERMS
}

data class MobileLegalSection(
    val title: String,
    val paragraphs: List<String>
)

val MOBILE_PRIVACY_POLICY_SECTIONS: List<MobileLegalSection> = listOf(
    MobileLegalSection(
        title = "1. Общие положения и правовые основания",
        paragraphs = listOf(
            "Настоящая Политика в отношении обработки и защиты персональных данных (далее — «Политика») составлена в соответствии с требованиями Федерального закона от 27.07.2006 № 152-ФЗ «О персональных данных», Федерального закона от 27.07.2006 № 149-ФЗ «Об информации, информационных технологиях и о защите информации» и определяет порядок обработки и меры по обеспечению безопасности данных пользователей сервиса IskraAi (далее — «Сервис», «Оператор», «Администрация»).",
            "Использование Сервиса (включая веб-сайт, десктопное и мобильное приложения), регистрация учётной записи и проставление соответствующей отметки о согласии означает полное и безоговорочное согласие Пользователя с настоящей Политикой и дачу согласия на обработку его данных в соответствии со ст. 9 Федерального закона № 152-ФЗ.",
            "Сервис придерживается принципа минимизации данных (Privacy by Design) и не запрашивает паспортные данные, ФИО по паспорту, номера телефонов, геолокацию, биометрические или специальные категории персональных данных."
        )
    ),
    MobileLegalSection(
        title = "2. Возрастное ограничение (Строго 18+)",
        paragraphs = listOf(
            "В соответствии с Федеральным законом от 29.12.2010 № 436-ФЗ «О защите детей от информации, причиняющей вред их здоровью и развитию», Сервис IskraAi предназначен исключительно для совершеннолетних дееспособных лиц, достигших возраста 18 лет.",
            "Оператор сознательно не осуществляет сбор, хранение и обработку данных несовершеннолетних лиц. При выявлении факта использования Сервиса лицом, не достигшим 18 лет, учётная запись и все связанные с ней данные подлежат незамедлительной блокировке и безвозвратному удалению."
        )
    ),
    MobileLegalSection(
        title = "3. Состав и категории обрабатываемых данных",
        paragraphs = listOf(
            "В рамках функционирования Сервиса обрабатываются исключительно следующие минимально необходимые данные, предоставляемые Пользователем добровольно:",
            "• Адрес электронной почты (e-mail) — для направления одноразовых кодов подтверждения (OTP), восстановления доступа и авторизации;",
            "• Отображаемый псевдоним (никнейм) и изображение профиля (по желанию Пользователя);",
            "• Технические данные сессии: зашифрованный токен авторизации и выбранная цветовая тема интерфейса.",
            "Оператор НЕ собирает и НЕ обрабатывает биометрические данные, сведения о состоянии здоровья, политических или религиозных взглядах."
        )
    ),
    MobileLegalSection(
        title = "4. Особенности работы с ИИ-диалогами и запрет ввода ПДн третьих лиц",
        paragraphs = listOf(
            "Тексты пользовательских ролевых диалогов передаются по защищённому протоколу HTTPS/TLS языковой нейросетевой модели для генерации художественных ответов и не публикуются в открытом доступе.",
            "Поскольку Сервис предназначен исключительно для художественного вымысла и ролевых сценариев, Пользователям КАТЕГОРИЧЕСКИ ЗАПРЕЩАЕТСЯ вводить в диалоги с ботами реальные персональные данные (ФИО реальных лиц, паспортные данные, адреса проживания, телефоны, банковские реквизиты, коммерческую или государственную тайну)."
        )
    ),
    MobileLegalSection(
        title = "5. Права Пользователя и контакты",
        paragraphs = listOf(
            "В соответствии со ст. 14 и ст. 21 ФЗ № 152-ФЗ Пользователь вправе в любой момент изменить свои данные в профиле, отозвать согласие на обработку персональных данных и самостоятельно удалить аккаунт.",
            "Официальный канал связи с Администрацией в Telegram: https://t.me/IskraAi_ru."
        )
    )
)

val MOBILE_TERMS_OF_SERVICE_SECTIONS: List<MobileLegalSection> = listOf(
    MobileLegalSection(
        title = "1. Общие положения и возраст 18+",
        paragraphs = listOf(
            "Настоящее Пользовательское соглашение (ст. 428, 437, 438 ГК РФ) регулирует отношения между Администрацией сервиса IskraAi и совершеннолетним Пользователем (строго 18+, ФЗ № 436-ФЗ).",
            "Регистрация или использование Сервиса означает полное согласие Пользователя с настоящим Соглашением и Политикой конфиденциальности."
        )
    ),
    MobileLegalSection(
        title = "2. Статус генераций ИИ и отказ от ответственности (AS IS)",
        paragraphs = listOf(
            "Все персонажи, сюжеты и ответы создаются нейросетью и являются исключительно художественным вымыслом. Сервис предоставляется на условиях «КАК ЕСТЬ» («AS IS»).",
            "Ответы виртуальных персонажей НЕ являются медицинской, психологической, юридической или финансовой консультацией."
        )
    ),
    MobileLegalSection(
        title = "3. Правила допустимого использования (Законодательство РФ)",
        paragraphs = listOf(
            "При использовании Сервиса СТРОГО ЗАПРЕЩАЕТСЯ создавать, запрашивать или распространять материалы, нарушающие законодательство Российской Федерации (в т.ч. ст. 10.6 и 15.1 ФЗ № 149-ФЗ):",
            "• Любые материалы с участием несовершеннолетних в недопустимом контексте (абсолютная нулевая толерантность);",
            "• Призывы к терроризму, экстремизму, насилию, разжигание вражды;",
            "• Информацию об изготовлении/обороте наркотических средств или способах совершения самоубийства;",
            "• Пропаганду запрещённых законодательством РФ отношений, клевету, доксинг или мошенничество."
        )
    ),
    MobileLegalSection(
        title = "4. Модерация и применимое право",
        paragraphs = listOf(
            "Администрация вправе без предупреждения удалить любого персонажа или заблокировать аккаунт при нарушении законодательства РФ или настоящего Соглашения.",
            "К настоящему Соглашению применяется право Российской Федерации. Обратная связь: https://t.me/IskraAi_ru."
        )
    )
)

@Composable
fun MobileLegalDocumentDialog(
    initialTab: MobileLegalDocType = MobileLegalDocType.PRIVACY,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf(initialTab) }
    val scrollState = rememberScrollState()

    LaunchedEffect(activeTab) {
        scrollState.scrollTo(0)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.86f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Правовая информация IskraAi", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("ФЗ № 152-ФЗ, № 149-ФЗ, № 436-ФЗ (18+)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Закрыть")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        MobileLegalDocType.PRIVACY to "Конфиденциальность",
                        MobileLegalDocType.TERMS to "Соглашение (18+)"
                    ).forEach { (type, label) ->
                        val selected = activeTab == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { activeTab = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .verticalScroll(scrollState)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val sections = if (activeTab == MobileLegalDocType.PRIVACY) MOBILE_PRIVACY_POLICY_SECTIONS else MOBILE_TERMS_OF_SERVICE_SECTIONS
                    sections.forEach { sec ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(sec.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            sec.paragraphs.forEach { p ->
                                Text(
                                    text = p,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Понятно")
                }
            }
        }
    }
}
