package com.todaii.english.shared.constants;

public class Gemini {
	public static final String CHAT_MODEL = "gemini-2.5-flash";

	public static final String TTS_MODEL = "gemini-2.5-flash-tts";

	public static final String DICTIONARY_PROMPT = """
			You are an English-Vietnamese dictionary assistant.
			Task:
			- Input: raw dictionary data (headword, IPA, audio, part of speech, definitions, examples, synonyms).
			- Output: normalized JSON with only essential fields.
			- Rules:
			  1. Keep only the most relevant senses (max 3).
			  2. For each sense: one short definition (English), one natural example (English).
			  3. Add a simple Vietnamese meaning in "meaning".
			  4. Include 2–3 synonyms and 1–2 common collocations if possible.
			  5. Always include headword, ipa, audio_url.

				Return JSON as an array of objects.
				Always return an array, even if there is only one entry.

				Each object must strictly follow this format:
				[
				  {
				    "headword": "<string>",
				    "ipa": "<string>",
				    "audio_url": "<string>",
				    "senses": [
				      {
				        "pos": "<string>",
				        "meaning": "<string>",       // Vietnamese
				        "definition": "<string>",    // English
				        "example": "<string>",       // English
				        "synonyms": ["..."],		 // English
				        "collocations": ["..."]		 // English
				      }
				    ]
				  }
				]

			Raw data:
			%s

			Target word: %s
			""";

	public static final String DECK_WORDS_PROMPT = """
			Generate a list of 20 English vocabulary words suitable for CEFR level %s.
			The topic is about: "%s".
			Focus on words that are relevant to this description: "%s".
			Each item must be a SINGLE English word (not a phrase, not a sentence, not a collocation).
			Avoid multi-word expressions, idioms, or compound nouns.
			⚠️ Avoid using the following words that already exist in the deck: [%s].
			Return ONLY a valid JSON array of words, like: ["word1", "word2", ...].
			""";

	public static final String TRANSLATE_PROMPT = """
			Translate the following English paragraph into natural, fluent Vietnamese:
			---
			%s
			---
			Only return the Vietnamese translation.
			""";

	public static final String RELATED_WORD_PROMPT = """
			Generate between 1 and 10 English words that are semantically related to the target word: "%s".
			Rules:
			- Only return SINGLE English words (no phrases).
			- Words must be common, meaningful, and contextually linked.
			- Avoid duplicates and avoid returning the target word itself.
			- Output ONLY a JSON array of strings, e.g. ["word1", "word2"].
			""";

}
