package kotlinlang.compose

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object ColorSerializer : KSerializer<Color> {
    @Serializable
    private data class ReadableColor(
        val alpha: Float,
        val red: Float,
        val green: Float,
        val blue: Float,
    ) {
        constructor(color: Color): this(
            alpha = color.alpha,
            red = color.red,
            green = color.green,
            blue = color.blue,
        )
        fun toColor() = Color(red, green, blue, alpha)
    }

    private val readableColorSerializer = ReadableColor.serializer()

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = SerialDescriptor("Color", readableColorSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeSerializableValue(readableColorSerializer, ReadableColor(value))
    }

    override fun deserialize(decoder: Decoder): Color {
        return decoder.decodeSerializableValue(readableColorSerializer).toColor()
    }
}
