package utils

import com.univocity.parsers.csv.{CsvParser, CsvParserSettings}

object StringParser {
  val csvParser = {
    val settings = new CsvParserSettings()
    val format = settings.getFormat
    format.setLineSeparator("\n")
    format.setDelimiter(",")
    format.setQuote('"')
    format.setQuoteEscape('\\')
    format.setComment('\u0000')
    settings.setIgnoreLeadingWhitespaces(true)
    settings.setIgnoreTrailingWhitespaces(true)
    settings.setReadInputOnSeparateThread(false)
    settings.setNullValue("")
    settings.setEmptyValue("")
    settings.setMaxCharsPerColumn(-1)
    settings.setMaxColumns(20000)
    new CsvParser(settings)
  }
}
