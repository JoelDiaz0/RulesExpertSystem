
diagnostic(X) :-
    findall(S, indicator(S), UserIndicators),
    problem(X, ProblemIndicators),
    subList(UserIndicators, ProblemIndicators).

subList([], _).

subList([H|T], List) :-
    member(H, List),
    subList(T, List).