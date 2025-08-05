from spellchecker import SpellChecker
import sys
import re
import io

# Force UTF-8 output
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
# List of field labels where correction should be skipped
FIELDS_TO_SKIP = [
    "nom complet", "email", "cin", "téléphone", "adresse", "date de naissance",
    "montant souhaité", "durée souhaitée", "employeur", "motif du crédit"
]

# Optional: extra words to teach the spellchecker
CUSTOM_WORDS = ["CDD", "CDI", "marié", "privé", "public"]

def correct_text(text):
    spell = SpellChecker(language='fr')
    spell.word_frequency.load_words(CUSTOM_WORDS)

    corrected_lines = []

    for line in text.splitlines():
        # Separate label and value if possible
        parts = line.split(":", 1)
        if len(parts) == 2:
            label, value = parts[0].strip().lower(), parts[1].strip()
        else:
            label, value = "", line.strip()

        # If the label should be skipped, leave the line unchanged
        if any(label.startswith(skip) for skip in FIELDS_TO_SKIP):
            corrected_lines.append(line)
            continue

        # Otherwise, correct each word in the value
        corrected_words = []
        for word in value.split():
            if word.lower() in spell:
                corrected_words.append(word)
            else:
                corrected = spell.correction(word)
                corrected_words.append(corrected if corrected else word)

        # Reconstruct line
        if label:
            corrected_lines.append(f"{parts[0]}: {' '.join(corrected_words)}")
        else:
            corrected_lines.append(' '.join(corrected_words))

    return "\n".join(corrected_lines)


if __name__ == "__main__":
    raw_text = sys.stdin.read()
    print(correct_text(raw_text))
