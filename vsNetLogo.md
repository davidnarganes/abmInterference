## Causal Inference ABM vs. NetLogo
The Spread of Disease model of NetLogo (SODNL) can be found as one of the set models in http://ccl.northwestern.edu/netlogo/models/SpreadofDisease.

Feature | CIABM | SODNL
--- | --- | ---
Causal effects| Adjustable for (i) confounder on vaccine, (ii) confounder on infection, (iii) vaccine on infection, (iv) contagion, (v) infectiousness | Fixed contagion
Environment | Continuous | Discrete
Confounding? | Sex of agent | NA
Intervention? | Vaccine, prob adjustable | NA
Network| Adjustable: variation though simulation | Fixed at start
Analysis GUI | (i) Frequency vaccinated and infected over time, (ii) cumulative infected distance over time, and (iii) histograms of cumulative infected distance and degree of nodes | Frequency of infected over time
Wandering | All agents move randomly | Degree 0 agents held fixed positions
Partners | Tendency to get closed, adjustable | NA
Infected agents | Baseline probability of both infection and vaccination though the simulation | Fixed infected patients at start
multi SIM | External `.csv` file with defined params | NA
Output | Saved at `t` intervals in `.csv` | NA
