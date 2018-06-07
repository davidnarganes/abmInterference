# ABM_CausalInf and HPV
_______________________

## 1. General description of the project
This repository will contain the code for generating (`Java`) and analysing (`R programming language`) a simple multi-Agent Based Model (mABM) of patients in a 2D continuous space. The idea is to simulate (i) causal diagrams for interference, (ii) ABMs, and the contagion of a disease thought a social network: human papilloma virus (HPV).

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
Traditionally, causal inference relied on the assumption of no interference. Nonetheless, one individual's exposure may affect another individual's outcome. In the literature, there are described two effects:
- __Indirect effect__ of one individual's treatment has an effect on another individual's outcome
- __Direct effect__ of one individual's treatment on her/his own outcome.

VanderWeele et al. (2012b) demonstrated that the individual-level indirect effect of vaccination could be decomposed into two effects:

- __Contagion__ is the indirect effect that vaccinating one individual may have on another by preventing the vaccinated individual from getting the disease and therefore from passing it on. E.g. vaccine for tetanus, hepatitis A and B, rabies, and measles reduce the susceptibility of treated individuals to the disease.

- __Infectiousness__ is the indirect effect that vaccination might have if, instead of preventing the vaccinated individual from getting the disease, it renders the disease less infectious, thereby reducing the probability that the vaccinated infected individual transmits the disease even if infected. E.g. malaria transmission-blocking vaccine prevents mosquitos from acquiring, and thereby from transmitting, malaria parasites upon biting infected individuals.

Our example will be a combination of all effects: direct, indirect with combination of contagion and infectiousness.

### 1.4 Social network
A social network is a collection of individuals and the ties betwen them. The presence of a tie between two individuals indicates that the individuals share some kind of relationship: family, friendship, partnership, etc. We will assume that the ties between agents will be undirectional.

Vaccine programs do not in general target distant, independent pairs of individuals; they target villages, cities, communities in which individuals are interconnected and their outcomes correlated. Therefore, assessing the presence of vaccine effects in social network data may be informative for real-world applications.

__Note!__: This simulation is not going to be 100% realistic, it will be a proof-of-concept on how to integrate causal interference and ABMs in the ongoing endeavor to develop methods for valid causal inference using simulated data from a single network of agents.

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

Let `A ≡ (A_1,...,A_n)` be the vector of vaccination assignment under the assumption of single version of treatment for agents at a given time `t`. Let `Y ≡ (Y_1,...,Y_n)` be the vector of outcomes, let `C ≡ (C_1,...,C_n)` be the array of covariates, and let `f(Y) ≡ (f(Y)_1,...,f(Y)_n)` be the vector of a distance function of the outcome for `n` agents at given time `T = t`. `Yi(a),a = 0,1` is defined as the counterfactual outcome we would have observed if, contrary to the fact, agent `i` had received treatment `a`, this is, if we would have observed for agent `i` under an intervention that set `A` to `a`.

Table 1. Variables of the DAG

Variable | Meaning | Type
--- | --- | ---
`C`| Sex of the agent | Boolean
`A` | Has received vaccination? | Boolean
`Y`| Is infected? | Boolean
`f(Y)`| Interference of contagion, or inverse cumulative distance of infected | Double
`a`| Causal weight of `C` in `A` | Double
`b`| Causal weight of `C` in `Y` | Double
`c`| Causal weight of `A` in `Y` | Double
`d`| Causal weight of `f(Y)` in `Y` or contagion weight | Double

The causal structure of the effect of of `Ai` in `Yi` is straightforward: `Ai` has a direct protective effect on `Yi`, represented by a direct arrow from `Ai` to `Yi` on the DAG. The effect of `Ai` on `Yj` will be represented as a mediated effect through `Yi` and a function of the latter `f(Yi)`. But this cannot be correct since `Yi` and `Yj` are contemporaneous and therefore one cannot cause the other. Instead, the effect of `Ai` on `Yj` will be mediated though a distance function `f(Y)` of the evolution of the outcome of agent `i`. This assumption is represented in Figure 1 where ![](https://latex.codecogs.com/svg.latex?%5Cinline%20Y%5Et_%7Bi%7D) represents the outcome of individual `i` at time `t`. `T` is the time of the end of the simulation. The dashed arrows represent times through `4` to `T-1` which do not fit in the DAG (but which are observed in the simulation).

`f(Y)` will be a vector of length `n-1` comprised by the values of distance function `f(Y)` of the vector of outcomes ![](https://latex.codecogs.com/svg.latex?%5Cmathbf%7BY%7D_%7B-%7D) where the subindex `-` can be indexed by `-i` for all agents except for agent `i`, `D` represents the indexed distance `D` between each agent `-i` excluding agent `i` to `i` (Figure 1) at the time `T = t`. `f(Y)` was defined at time `T = t` as:

![](https://latex.codecogs.com/svg.latex?f%28%5Cmathbf%7BY%7D%5Et%29%5Et_i%3D%20%5Csum_%7B-i%20%3D%201%7D%5E%7Bn-1%7D%20%5Cfrac%7BY_%7B-i%7D%5Et%7D%7BD%5Et_%7Bi%2C-i%7D%7D)

In the extreme case where all `-i` infected agents are at at distance `D = 0` of the uninfected `Yi = 0` agent `i`, `f(Y)i` will get its maximum value .Conversely, in the oposite extreme case where all `-i` infected agents are infinitely distant `D = +∞` from the uninfected `Yi = 0` agent `i`, `f(Y)` causal effect will be minimum.

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

### 3.6 Posterior probability of getting the vaccine
The posterior probability of getting the vaccine is just going to depend on the confounder sex (`C`), a time-independent variable, and the baseline  probability of getting the vaccine:

![](https://latex.codecogs.com/svg.latex?%5Cinline%20A%3D%281&plus;aC%29P_%7Bbaseline%7D%28A%29)

[I have to put this formula in proper mathematical terms!]

### 3.5 Posterior probability of getting infected
The posterior probability of infection `Y` at time `T = t` will depend on the confounder sex (`C`), the vaccination status (`A`), and the function of the outcome (`f(Y)`):

![](https://latex.codecogs.com/svg.latex?%5Cinline%20Y%3D%281&plus;bC-cA&plus;f%28Y%29d%29P_%7Bbaseline%7D%28Y%29)

## 4. ABM and its variables
In this mABM, the agents will be created at the same location but there will be some forces that will control their movement in the 2D space:
 - Partner force
 - Central force
 - Random force

At every step of the simulation, each agent will have at least one partner.

The following variables were defined for the ABM:

Variable | Meaning | Type
--- | --- | ---
`probInfection`| Baseline probability of infection | Double
`probVaccine` | Baseline probability of vaccination | Double
`maxForce`| Max value of partner forces | Double
`centralForce`| Joining force to keep agents in the center of the 2D space | Double
`randomForce`| Weight that control the force that makes the agents wander randomly | Double
`promiscuityPopulation`| Probability of changing partners defined for the whole population | Double

### References
1. E. L. Ogburn and T. J. VanDerWeele:
   "Causal Diagrams for Interference" Statistical Science, Vol. 29, No. 4, Special Issues on Semiparametrics and Causal Inference (November 2014), pp. 559-578 at https://www.jstor.org/stable/43288499.
  Accessed: 02-06-2018
2. M. G. Hudgens and M. E. Halloran:
   "Towards Causal Inference with Interference" Journal of the American Statistical Association. 2008, June; 103 (482): pp. 832-842 at https://amstat.tandfonline.com/doi/abs/10.1198/016214508000000292#.WxaoRRzTWGA
   Accessed: 02-06-2018
