package gamescreen

object GameState {
    var character: String = "Zwiadowca"
    var gold: Int = 0
    var strength: Int = 0      // siła - wpływa na obrażenia mieczem
    var intelligence: Int = 0  // intelekt - wpływa na obrażenia magiczne
    var dexterity: Int = 0    // zręczność - wpływa na obrażenia łukiem

    fun resetState() {
        character = "Zwiadowca"
        gold = 0
        strength = 0
        intelligence = 0
        dexterity = 0
    }
}