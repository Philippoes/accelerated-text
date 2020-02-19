resource IncludesEst = open ParadigmsEst, SyntaxEst, UtilsEst, BaseDictionaryEst in {

  oper wrapInText : Cl -> Text = \cl -> (mkText (mkS cl));

  oper
    -- The package includes X.
    included : CN -> Text =
      \subject ->
      (wrapInText
         (mkCl
            (mkNP the_Det package)
            includes
            (mkNP a_Det subject)));
}
