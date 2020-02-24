resource BaseDictionaryEst = open ResEst, ParadigmsEst, SyntaxEst, UtilsEst in {
  oper

    suitable : A2 = mkA2 (mkA (mkN "paras" "paraja" "parajat" "parajasse" "parajate" "parajaid")) for_Prep;
    allows: V2 = mkV2 (mkV "lubama") for_Prep; --guessed
    features: V2 = mkV2 (mkV "arvama"); --guessed
    includes: V2 = mkV2 (mkV "sisaldama");

    this: CN = mkCN (mkN "see");

    t1000 : CN = mkCN (mkN "t1000" );
    kitchen : CN = mkCN (mkN "köök" "köögi" "kööki" "köögisse" "köökide" "kööke");
    toaster : CN = mkCN (mkN "röster" "röstri" "röstrit" "röstrisse" "röstrite" "röstreid");
    water : CN = mkCN (mkN "vesi" "vee" "vett" "veesse" "vete" "vesi");
    cleaning : CN = mkCN (mkN "puhastamine" "puhastamise" "puhastamist" "puhastamisesse" "puhastamiste" "puhastamisi");
    safe_operation : CN = mkCN (mkA (mkN "turvaline" "turvalise" "turvalist" "turvalisesse" "turvaliste" "turvalisi"))
                               (mkN "operatsioon" "operatsiooni" "operatsiooni" "operatsioonisse" "operatsioonide" "operatsioone");
    auto_switch : CN =  mkCN (mkA (mkN "automaatne" "automaatse" "automaatset" "automaatsesse" "automaatsete" "automaatseid"))
                             (mkN "väljalülitus");
    kettle : CN =  mkCN (mkN "katel" "katla" "katelt" "katlasse" "katelde" "katlaid");
    fridge : CN =  mkCN (mkN "külmik" "külmiku" "külmikut" "külmikusse" "külmikute" "külmikuid");
    door: CN =  mkCN (mkN "uks" "ukse" "ust" "uksesse" "uste" "uksi");
    steel : CN =  mkCN (mkN "teras" "terase" "terast" "terasesse" "teraste" "teraseid");
    removable_filter : CN = mkCN (mkA "eemaldatav") (mkN "filter" "filtri" "filtrit" "filtrisse" "filtrite" "filtreid");
    package : CN = mkCN (mkN "pakk" "paki" "pakki" "pakisse" "pakkide" "pakke");
    interior_use : CN = mkCN (mkA (mkN "seesmine" "seesmise" "seesmist" "seesmisesse" "seesmiste" "seesmisi"))
                             (mkN "kasutus" "kasutuse" "kasutust" "kasutusesse" "kasutuste" "kasutusi");
    fast_boiling : CN = mkCN (mkA (mkN "kiire" "kiire" "kiiret" "kiiresse" "kiirete" "kiireid"))
                             (mkN "keetmine" "keetmise" "keetmist" "keetmisesse" "keetmiste" "keetmisi");
    power : CN = mkCN (mkN "W");
    lock : CN = mkCN (mkN "lukk" "luku" "lukku" "lukusse" "lukkude" "lukke");

    easy_N : N = (mkN "lihtne" "lihtsa" "lihtsat" "lihtsasse" "lihtsate" "lihtsaid");

    low_power : A = (mkA "väike võimsus"); --uncheked
    average_size : A = (mkA "keskmine suurus"); --uncheked
    modern_design : A = (mkA "kaasaegne disain"); --uncheked

    standard : A = (mkA "standard");
    fast : A = mkA (mkN "nobe" "nobeda" "nobedat" "nobedasse" "nobedate" "nobedaid");
    small : A = mkA (mkN "väike" "väikese" "väikest" "väikesesse" "väikeste" "väikesi");
    regular : A = mkA (mkN "regulaarne" "regulaarse" "regulaarset" "regulaarsesse" "regulaarsete" "regulaarseid");
    wood : A =  mkA (mkN "puit" "puidu" "puitu" "puidusse" "puitude" "puite");

    make : V2 = mkV2 (mkV "tegema");

    -- for dev
    -- toasterWithMods : CN = mkCN (combineMods low_power average_size) toaster;
    -- kitchenWithMods : CN = mkCN standard kitchen;
}
