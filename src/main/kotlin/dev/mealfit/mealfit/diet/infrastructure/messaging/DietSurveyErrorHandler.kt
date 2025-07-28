//package dev.mealfit.mealfit.diet.infrastructure.messaging
//
//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Component
//import org.xml.sax.ErrorHandler
//import org.xml.sax.SAXParseException
//
//@Component
//class DietSurveyErrorHandler : ErrorHandler {
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    fun handle(thrownException: Exception, record: ConsumerRecord<*, *>) {
//        // 3회 이상 실패한 메시지를 로그에 기록하거나 DLQ로 보냄
//        logger.error("Kafka Consumer 처리 실패: ${record.value()}", thrownException)
//        // 추후 DLQ 토픽으로 전송할 수도 있음
//    }
//
//    override fun warning(exception: SAXParseException?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun error(exception: SAXParseException?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun fatalError(exception: SAXParseException?) {
//        TODO("Not yet implemented")
//    }
//}
