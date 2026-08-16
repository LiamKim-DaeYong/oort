package io.oort.notification.adapter.output.id

import com.fasterxml.uuid.Generators
import io.oort.notification.application.port.output.NotificationIdGenerator
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UuidV7NotificationIdGenerator : NotificationIdGenerator {
    private val generator = Generators.timeBasedEpochGenerator()

    override fun generate(): UUID = generator.generate()
}
