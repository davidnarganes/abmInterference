# ABM_CausalInf and HPV
_______________________

## 1. General description of the project
This repository will contain the code for generating (`Java`) and analysing (`R programming language`) a simple multi-Agent Based Model (mABM) of patients in a 2D continuous space. The idea is to simulate (i) causal diagrams for interference, (ii) ABMs, and the contagion of a disease thought a population: human papilloma virus (HPV).

__Note__: This `.java` project could be run by creating a project using your favourite Integrated Development Environment (IDE) and cloning this repository. This simulation relies on the `Mason` java library and its dependencies: already included in the repository. If there are any problems, the link to `Mason` dependencies is the following: https://cs.gmu.edu/~eclab/projects/mason/

### 1.1 Learning objectives of the intern
 - Learn Java programming while creating an ABM
 - Learn R programming while using the G-methods to analyse the results generated from the ABM
 
### 1.2 Project outcomes
 - Integrate of ABMs and Causal Inference
 - Integrate the spatial dimension with time-dependent confounders
 - Violate the Stable Unit Treatment Value Assumption (SUTVA) of causal inference

Why this integration? ABM are used to simulate individuals and the consequences of interactions and their behaviours. Nevertheless, the accuracy of this simulations is highly reliant on capturing the complexity of the relationships between individuals over spatio-temporal scenarios. Current methodologies lack the sophistication to capture causal relationships. By integrating ABM and causal inference:
 - more complex and accurate simulations could be implemented
 - better understanding about how populations react to interventions
 
Why the violation of SUTVA? SUTVA states that:
  - Individuals do not interfere with each other (this is the strength of AMBs)
  - Treatment assignment of one unit does not affect outcome of another unit

### 1.3 Interference introduction
This piece of research will be based on:

- E. L. Ogburn and T. J. VanDerWeele:
  "Causal Diagrams for Interference" Statistical Science, Vol. 29, No. 4, Special Issues on Semiparametrics and Causal Inference (November 2014), pp. 559-578 at https://www.jstor.org/stable/43288499.
  Accessed: 02-06-2018

Traditionally, causal inference relied on the assumption of no interference: one individual's exposure may affect another individual's outcome. SUTVA is violated when the outcome is an infectious disease and treating one individual may exert a protective effect on others in a given population. This is, it can involve feedbacks among different agents' outcomes over time.
  
In this example, the effectiveness of vaccination in HPV prevention will depend on how many people were vaccinated and how they interact. Individuals, in this case agents, will interfere with each other. Also, the vaccination of one individual, in our case agent, will reduce the contagion effect. This description will be formally expressed in mathematical terms and DAGs in the following sections.

__Note!__: This simulation is not going to be 100% realistic, it will be an example of how to integrate causal inference and ABMs.

## 2 Human Papilloma Virus (HPV)
__HPV__ is the most common sexually transmitted infection. Most HPV infections cause no symptoms and resolve spontaneously. Nevertheless, it increases the risk of cancer of the cervix, vulva, vagina, penis, anus, mouth, or throat.

__Risk factors__ include sexual intercourse, multiple partners, smoking, and poor immune system. HPV is typically transmitted by sustained direct skin-to-skin contact.

Once the HPV infects a person, an active infection occurs and the virus can be transmitted. Several months to years may elapse before the visible symptoms can be clinically detected in the form of intraepithelial lesions, making it difficult to know which partner was the source of infection.

HPV vaccines can prevent the most common types of infection. In women, HPV infection can cause cervix cancer and women are more likely to get vaccinated with Gardasil, preventing around 90% of infections. Nevertheless, vaccination is less common in men (here it comes our simple confounder: sex).

## 3. Directed Acyclic Graph (DAG) and causal description of the project
### 3.1 DAG definition and properties
Causal diagrams, or causal directed acyclic graphs (DAGs) consist of nodes, representing the variables in a study, and arrows, representing direct causal effects. A path on a DAG is any unbroken, non-repeating sequence of arrows connecting one variable to another.
 - DAGs are directed because there is a unique path that follows arrows from tail to head.
 - DAGs are acyclic because they do not contemplate the existance of loops of arrows that converge in the same variable the first arrow emanated from.
Reference for more information about DAGs: [include]

### 3.2 Causal description
Following the description from Ogburn and VanderWeele (2014), it is often reasonable to make a "partial interference" assumption where interference can only occur within subgroups or "blocks" of agents that are separated in time and/or space. The conterfactual notation for interference will follow Hudgens and Halloran (2008): suppose than `n` individual fall into `N` blocks, indexed by `k` with `m = n/N` individuals in each block. In this example, we will assume `N = 1` so that interference may occur between any two agens in the population: a full interference with no blocks.

Furthermore, we wish to estimate the average causal effect of a vaccine `A` on an outcome `Y`, infection, from simulation data on `n` individuals for whom we have also measured a vector of confounders `C`, the sex of the agents. For simplicity , we assume that both `A` and `Y` are dichotomous, binary variables.

Let `A ≡ (A_1,...,A_n)` be the vector of vaccination assignment under the assumption of single version of treatment for agents at a given time `t`. Let `Y ≡ (Y_1,...,Y_n)` be the vector of outcomes, let `C ≡ (C_1,...,C_n)` be the array of covariates, and let `f(Y) ≡ (f(Y)_1,...,f(Y)_n)` be the vector of a distance function of the outcome for `n` agents at given time `T = t`. Define `Yi(a),a = 0,1` is defined as the counterfactual outcome we would have observed if, contrary to the fact, agent `i` had received treatment `a`, this is, if we would have observed for agent `i` under an intervention that set `A` to `a`.

Table 1. Variables of the DAG

Variable | Meaning | Type
--- | --- | ---
`C`| Sex of the agent | Boolean
`A` | Has received vaccination? | Boolean
`Y`| Is infected? | Boolean
`f(Y)`| Interference of contagion, or inverse cumulative distance of infected | Double
`a`| Causal weight of `Z` in `X` | Double
`b`| Causal weight of `Z` in `Y` | Double
`c`| Causal weight of `X` in `Y` | Double
`d`| Causal weight of `f(Y)` in `Y` or contagion weight | Double

The causal structure of the effect of of `Ai` in `Yi` is straightforward: `Ai` has a direct protective effect on `Yi`, represented by a direct arrow from `Ai` to `Yi` on the DAG. The effect of `Ai` on `Yj` will be represented as a mediated effect through `Yi` and a function of the latter `f(Yi)`. But this cannot be correct since `Yi` and `Yj` are contemporaneous and therefore one cannot cause the other. Instead, the effect of `Ai` on `Yj` will be mediated though a distance function `f(Y)` of the evolution of the outcome of agent `i`. This assumption is represented in __FIGURE ADD__ where ![](https://latex.codecogs.com/gif.latex?%5Cinline%20Y%5ET_i) represents the outcome of individual `i` at time `t`. `T` is the time of the end of the simulation. The dashed arrows representtimes through `4` to `T-1` which do not fit in the DAG (but which are observed in the simulation).

`f(Y)` will be a vector comprised by the values of distance function of the vector of outcomes ![](https://latex.codecogs.com/svg.latex?%5Cmathbf%7BY%7D_%7B-%7D) where the subindex `-` can be indexed by `-i` representing all agents expect for `i`, `D` represents the indexed distance `D` between each agent `-i` excluding `i`  to `i` (Figure 1) at the time `T = t`. `f(Y)` was defined at time `T` as:

![](https://latex.codecogs.com/svg.latex?f%28%5Cmathbf%7BY%7D%5Et%29%5Et_i%3D%20%5Csum_%7B-i%20%3D%201%7D%5E%7Bn-1%7D%20%5Cfrac%7BY_%7B-i%7D%5Et%7D%7BD%5Et_%7Bi%2C-i%7D%7D)

<img src="pics/interaction.png" width="500" style = "text-align:center">

Figure 1. Distance between `n = 6` agents at time `T = t`. __THE INDEX IS WRONG:CHANGE__

### 3.3 Causal assumptions

We define the __consistency assumption__ based on Ogburn and VanderWeele (2014) as:

![](https://latex.codecogs.com/gif.latex?Y_i%28a%29%3DY_i)
when
![](https://latex.codecogs.com/gif.latex?A%3Da)

The __exchangeability assumption__, also known as the "no unmeasured confounding assumption" to account for the causal effects under interference: we assume that we have measured a set of prevaccination covariates `C` for each agent such that:

![](https://latex.codecogs.com/gif.latex?Y_i%28a%29%5Ccoprod%20A%7CC)

and the __positivity assumption__:

![](https://latex.codecogs.com/gif.latex?P%28A%3Da%7CC%3Dc%29%3E0)

for all `a` in support of `A` and all `c` in support of `C`

### 3.4 Definition of causal effects

The __overall effect__ (OE) of intervention `a` compared to intervention `a'` on subject `i` is defined as:

![](https://latex.codecogs.com/gif.latex?%5Cinline%20OE_i%20%28a%2Ca%27%29%20%3D%20E%5BY_i%28a%29%5D%29%20-%20E%5BY_i%28a%27%29%5D)

where `i` indicates that the expectations do not average over individuals and ![](https://latex.codecogs.com/gif.latex?%5Cinline%20%5Cfrac%7B1%7D%7Bn%7D%5Csum_%7Bi%3D1%7D%5En%20E%5BY_i%28a%29%5D) averages over the empirical mean of the conterfactual outcomes at time `t`.

The __unit level effect__ (UE) of treatment of agent `i` fixes the treatment assignments for all agents expect for agent `i`, and compares the conterfactual outcomes for agent `i` under two different treatment assignments. Let ![](https://latex.codecogs.com/gif.latex?%5Cinline%20a_%7B-1%7D) be a vector of length `n-1` of treatment values for all agents in the simulation except for agent `i`, where ![](https://latex.codecogs.com/gif.latex?%5Cinline%20Y_i%28%5Cmathbf%7Ba%7D_%7B-i%7D%2C%5Ctilde%7Ba%7D%29) 
represents the agent's `i` counterfactual outcome under the intervention in which all agents except for agent `i` receive treatment ![](https://latex.codecogs.com/gif.latex?%5Cinline%20%5Cmathbf%7Ba%7D_%7B-1%7D) 
and agent `i` receives treatment ![](https://latex.codecogs.com/gif.latex?%5Cinline%20%5Ctilde%7Ba%7D). Then, the UE will be defined as:

![](https://latex.codecogs.com/gif.latex?%5Cinline%20UE_i%28%5Cmathbf%7Ba%7D%3B%20%5Ctilde%7Ba%7D%2C%5Cbar%7Ba%7D%29%3DE%5BY_i%28%5Cmathbf%7Ba%7D_%7B-i%7D%2C%5Ctilde%7Ba%7D%29%5D-E%5BY_i%28%5Cmathbf%7Ba%7D_%7B-i%7D%2C%5Cbar%7Ba%7D%29%5D)

The __spillover effect__ (SE) of intervention ![](https://latex.codecogs.com/gif.latex?%5Cinline%20%5Cmathbf%7Ba%7D) compared to intervention ![](https://latex.codecogs.com/gif.latex?%5Cinline%20%5Cmathbf%7Ba%27%7D) on agent `i` fixes `i`s treatment level and compares its conterfactual outcomes under two different interventions. The SE will be defined as:

![](https://latex.codecogs.com/svg.latex?SE_i%28%5Cmathbf%7Ba%7D%2C%5Cmathbf%7Ba%27%7D%3B%5Ctilde%7Ba%7D%29%20%3D%20%5BY_i%28%5Cmathbf%7Ba_%7B-i%7D%7D%2C%5Ctilde%7Ba%7D%29%5D%20-%20E%5BY_i%28%5Cmathbf%7Ba%27_%7B-i%7D%7D%2C%5Ctilde%7Ba%7D%29%5D)

and the __total effect__ can be decomposed into a sum of unit level and spillover effects:

![](https://latex.codecogs.com/svg.latex?TE_i%28%5Cmathbf%7Ba%7D%2C%5Cmathbf%7Ba%27%7D%3B%5Ctilde%7Ba%7D%2C%5Cbar%7Ba%7D%29%20%3D%20E%5BY_i%28%5Cmathbf%7Ba_%7B-i%7D%7D%2C%5Ctilde%7Ba%7D%29%5D%20-%20E%5BY_i%28%5Cmathbf%7Ba_%7B-i%7D%7D%2C%5Cbar%7Ba%7D%29%5D&plus;E%5BY_i%28%5Cmathbf%7Ba_%7B-i%7D%7D%2C%5Ctilde%7Ba%7D%29%5D%20-%20E%5BY%28%5Cmathbf%7Ba%27_%7B-i%7D%7D%2C%5Ctilde%7Ba%7D%29%5D).

### 3.6 Probability of getting the vaccine
The probability of getting the vaccine is just going to depend on the sex (`Z`), a time-independent variable:

![equation4](https://latex.codecogs.com/gif.latex?P%28X%2CZ%29%20%3D%20P%28X%7CZ%29P%28Z%29)

and `P(X,Z)` is going to depend on the confounding weight and the baseline (can I say prior?) probability of getting the vaccine (__Note__: the probability of getting the vaccine, the vaccine, does not depend on having the outcome. It is a prevention that in this hypothetical scenario, for economical, social reasons is not used in the whole population. Nonetheless, the probability of getting the vaccine can be manipulated):

![equation5](https://latex.codecogs.com/gif.latex?X%20%3D%20%281%20&plus;%20cZ%29P_%7Bbaseline%7D%28X%29)

### 3.5 Probability of getting infected
Based on the DAG, the probability of getting infected `Y` at time `T = t` will depend on the agent's sex (`C`), the vaccination status (`A`), and the function of the outcome (`f(Y)`):

![equation6](https://latex.codecogs.com/gif.latex?P%28Y_%7Bn%7D%2CX_%7Bn%7D%2CZ_%7Bn%7D%2CI_%7Bn-1%7D%2CY_%7Bn-1%7D%29%20%3D%20P%28Y_%7Bn%7D%7CX_%7Bn%7D%2CZ_%7Bn%7D%2CI_%7Bn-1%7D%29P%28X_%7Bn%7D%7CZ_%7Bn%7D%29P%28I_%7Bn-1%7D%7CY_%7Bn-1%7D%29P%28Y_%7Bn-1%7D%29)

The probability of getting infected `Y` was defined as:

![equation7](https://latex.codecogs.com/gif.latex?p1%20%3D%20%281&plus;sZ-wX%29P_%7Bbaseline%7D%28Y%29)

![equation8](https://latex.codecogs.com/gif.latex?p2%20%3D%20t%5Cfrac%7Bn%7D%7B1&plus;I%7D)

![equation9](https://latex.codecogs.com/gif.latex?Y%20%3D%20p1%20&plus;%20p2)

The probability of getting infected `Y` depended on:
 - The sex (`Z`) and its causal weight on the outcome (`s`)
 - The vaccine (`X`) and its causal weight in the outcome (`w`)
 - The baseline probability of getting infected that will be randomly defined `P_{baseline}(Y)`
 - The transmission power or weight (`t`)
 - The number of infected patients (`n`) at the inmediate prior time (`time n-1`), and
 - The infected cumulative distance (`I`) at the inmmediate prior time (`time n-1`) that converts this simulation in an ABM and not a mere Marginal Structural Model (MSM).

Why this formula?

__First addend of the equation__ (`p1`): The probability of getting infected will depend on the sex, vaccine, and its causal weights. The vaccine causal weight (`w`) will control how effective the vaccine is: if `w` is big enough, the above result will be minor than zero and therefore the vaccination will completely prevent the infection.

__Second addend of the equation__(`p2`): The more infected agents in the simulation, the more likely an agent will be infected. The cumulative distance will account for the interactions between agents: (i) in the extreme case where all infected patients are at the exact same location of the remaining uninfected agent, `I` will have a value of 0 and the transmission effect will be maximum; (ii) in the oposite extreme case where all infected patients are infinitively distant from the remaining uninfected patient, `I` will have an infinite value and the transmission effect will be minimum.

## 4. ABM and its variables
In this mABM, the agents will be created at the same location but there will be some forces that will control their movement in the 2D space:
 - Friend force
 - Central force
 - Random force

At every step of the simulation, each agent will have at least one friend. Agents will tend to socialise, interact with their friends.

The following variables were defined for the ABM:

Variable | Meaning | Type
--- | --- | ---
`P_infected`| Baseline probability of infection | Double
`P_vaccine` | Baseline probability of vaccination | Double
`max_force`| Max value of friend forces | Double
`central_force`| Joining force to keep agents in the center of the 2D space | Double
`randomness`| Weight that control the force that makes the agents wander randomly | Double
`promiscuity`| Probability of changing Friends and Strangers | Double

### References
1. E. L. Ogburn and T. J. VanDerWeele:
   "Causal Diagrams for Interference" Statistical Science, Vol. 29, No. 4, Special Issues on Semiparametrics and Causal Inference (November 2014), pp. 559-578 at https://www.jstor.org/stable/43288499.
  Accessed: 02-06-2018
2. M. G. Hudgens and M. E. Halloran:
   "Towards Causal Inference with Interference" Journal of the American Statistical Association. 2008, June; 103 (482): pp. 832-842 at https://amstat.tandfonline.com/doi/abs/10.1198/016214508000000292#.WxaoRRzTWGA
   Accessed: 02-06-2018
