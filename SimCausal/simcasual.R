library("simcausal")

D <- DAG.empty()
D <- D +
  node("X",
       distr = "rcat.b1",
       probs = c(0.5, 0.25, 0.25)) +
  node("W1",
       distr = "rnorm",
       mean = ifelse(X == 1, 0, ifelse(X == 2, 3, 10)),
       sd = 1) +
  node("W2",
       distr = "runif",
       min = 0, max = 1) +
  node("W3",
       distr = "rbern",
       prob = plogis(-0.5 + 0.7 * W1 + 0.3 * W2)) +
  node("Anode",
       distr = "rbern",
       prob = plogis(-0.5 - 0.3 * W1 - 0.3 * W2 - 0.2 * W3)) +
  node("Y",
       distr = "rbern",
       prob = plogis(-0.1 + 1.2 * Anode + 0.1 * W1 + 0.3 * W2 + 0.2 * W3))
Dset <- set.DAG(D)

# Plottting the DAG
plotDAG(Dset, xjitter = 0.3, yjitter = 0.04,
        edge_attrs = list(width = 0.5, arrow.width = 0.4, arrow.size = 0.8),
        vertex_attrs = list(size = 12, label.cex = 1.0))

# Generating the DataFrame
Odat <- sim(DAG = Dset, n = 10000, rndseed = 123)

# Specifying the interventions (Conterfactuals)
A1 <- node("Anode", distr = "rbern", prob = 1)
Dset <- Dset + action("A1", nodes = A1)
A0 <- node("Anode", distr = "rbern", prob = 0)
Dset <- Dset + action("A0", nodes = A0)

# To see the interventions or actions performed in the Dset:
A(Dset)
str(A(Dset)[["A0"]])

# Simulation of conterfactual data:
# Each dataframe object  (A0 and A1) contains observations simulated from the postintervention distributions
Xdat1 <- sim(DAG = Dset, actions = c("A1", "A0"), n = 1000, rndseed = 123)
View(Xdat1[["A1"]])
View(Xdat1[["A0"]])

# Setting and evaluating target params:
# 1. Difference
# 2. Ratio
Dset <- set.targetE(Dset, outcome = "Y", param = "A1-A0")
eval.target(Dset, data = Xdat1)$res

Dset <- set.targetE(Dset, outcome = "Y", param = "A1/A0")
eval.target(Dset, data = Xdat1)$res

