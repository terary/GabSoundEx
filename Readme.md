





Settings:
MINIMUM_SIMULARITY.GROUP_SAME;
compMethod.EXISTENCE;
uf_IgnoredMatching.MATCH;

Output
test2ctrl: 'rewrite' -> 'R1R0D0' :(re)w(r)i(t)e
ctrl2test 'reward' -> 'R1W0R0D0' :(re)[w]a(r)(d)
(Matched) [Not Matched]


Explained:
(re)ward <-> (re)write
rewa(r)d <-> rew(r)ite
rewri(t)e <-> rewar(d)  
re[w]ard -> w is silent in write


SxSIMULARITY.SOUNDEX_EQUAL
test2ctrl: 'rewrite' -> 'R1R0D0' :(re)(w)(r)(i)(t)(e)
ctrl2test 'reward' -> 'R1W0R0D0' :(re)[w](a)(r)(d)

(re)ward <-> (re)write
re[w]ard -> w is silent in write
rewa(r)d <-> rew(r)ite
rewri(t)e <-> rewar(d)  


