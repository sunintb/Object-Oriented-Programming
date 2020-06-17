/*
Sunint Bindra, CS10, S20, Professor Li
Class for retaining character and frequency information
 */
public class CharacterCounter {
    private Integer thisFrequency;
    private Character thisCharacter;

    @Override
    public String toString() {
        return thisCharacter + ": " + thisFrequency;
    }
    public CharacterCounter(Character c, Integer f) {
        thisFrequency = f;
        thisCharacter = c;
    }
    public Integer getFrequency() {
        return thisFrequency;
    }
    public Character getCharacter() {
        return thisCharacter;
    }
}
