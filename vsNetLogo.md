## Causal Inference ABM vs. NetLogo
The Spread of Disease model of NetLogo (SODNL) can be found as one of the set models in http://ccl.northwestern.edu/netlogo/models/SpreadofDisease.

Feature | CIABM | SODNL
--- | --- | ---
Causal effects| Adjustable for (i) confounder on vaccine, (ii) confounder on infection, (iii) vaccine on infection, (iv) contagion, (v) infectiousness | Fixed contagion
Environment | Continuous | Discrete
Confounding? | Y | NA
Intervention? | Y | NA
Network| Erdős–Rényi model (https://en.wikipedia.org/wiki/Erd%C5%91s%E2%80%93R%C3%A9nyi_model) type B with Poisson distribution `G(n,p)`. Non-static: `promiscuityPopulation`. Lecture: https://www.youtube.com/watch?v=AmZ_MOQ-XwA | Erdős–Rényi model type A: `G(n,m)`. Static, fixed at start
Analysis GUI | (i) Frequency vaccinated and infected over time, (ii) cumulative infected distance over time, and (iii) histograms of cumulative infected distance and degree of nodes | Frequency of infected over time
Free wandering? | Y | Degree 0 agents held fixed positions
Peers get together? | Y | NA
Number edges | Adjustable | Adjustable
Infected agents | Baseline probability of both infection and vaccination though the simulation | Fixed infected patients at start
multi SIM | External `.csv` file with defined params | NA
Output | Saved at `t` intervals in `.csv` | NA
Contagion | Though distance | Though network
maxInfected | NA | Fixed? Defined resistance?
