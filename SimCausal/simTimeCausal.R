library("simcausal")
library("tictoc")
options(simcausal.verbose=FALSE)

D <- DAG.empty()
D <- D +
  node("L2", t = 0, distr = "rbern",
       prob = 0.05) +
  node("L1", t = 0, distr = "rbern",
       prob = ifelse(L2[0] == 1, 0.5, 0.1)) +
  node("A1", t = 0, distr = "rbern",
       prob = ifelse(L1[0] == 1 & L2[0] == 0, 0.5,
                     ifelse(L1[0] == 0 & L2[0] == 0, 0.1,
                            ifelse(L1[0] == 1 & L2[0] == 1, 0.9, 0.5)))) +
  node("A2", t = 0, distr = "rbern",
       prob = 0, EFU = TRUE)

t.end <- 20
D <- D +
  node("Y", t = 1:t.end, distr = "rbern",
       prob =
         plogis(-6.5 + L1[0] + 4 * L2[t-1] + 0.05 * sum(I(L2[0:(t-1)] == rep(0, t)))),
       EFU = TRUE) +
  node("L2", t = 1:t.end, distr = "rbern",
       prob =
         ifelse(A1[t-1] == 1, 0.1,
                ifelse(L2[t-1] == 1, 0.9, min(1, 0.1 + t / 16)))) +
  node("A1", t = 1:t.end, distr = "rbern",
       prob = ifelse(A1[t-1] == 1, 1,
                     ifelse(L1[0] == 1 & L2[t] == 0, 0.3,
                            ifelse(L1[0] == 0 & L2[t] == 0, 0.1,
                                   ifelse(L1[0] == 1 & L2[t] == 1, 0.7, 0.5))))) +
  node("A2", t = 1:t.end, distr = "rbern",
       prob = {if(t == 16) {1} else {0}},
       EFU = TRUE)
lDAG <- set.DAG(D)

# Plot DAG
# Plottting the DAG
plotDAG(lDAG, xjitter = 0.3, yjitter = 0.01)
plotDAG(lDAG, tmax = 3, xjitter = 0.3, yjitter = 0.03,
        edge_attrs = list(width = 0.5, arrow.width = 0.4, arrow.size = 0.8),
        vertex_attrs = list(size = 12, label.cex = 0.8))

# Generate DataFrame
tic()
Odat <- sim(DAG = lDAG, n = 1000000, rndseed = 123)
toc()

View(Odat)

# Action
act_theta <-c(node("A1", t = 0, distr = "rbern",
                   prob = ifelse(L2[0] >= theta , 1, 0)),
              node("A1", t = 1:(t.end), distr = "rbern",
                   prob = ifelse(A1[t-1] == 1, 1, ifelse(L2[t] >= theta, 1, 0))))

Ddyn <- lDAG
Ddyn <- Ddyn + action("A1_th0", nodes = act_theta, theta = 0)
Ddyn <- Ddyn + action("A1_th1", nodes = act_theta, theta = 1)

plotDAG(A(Ddyn)[["A1_th0"]], tmax = 4, xjitter = 0.3, yjitter = 0.03,
        edge_attrs = list(width = 0.5, arrow.width = 0.4, arrow.size = 0.8),
        vertex_attrs = list(size = 15, label.cex = 0.7))

# Static intervention: start one timestep before the treatment
`%+%` <- function(a, b) paste0(a, b)
Dstat <- lDAG
act_A1_tswitch <- node("A1",t = 0:(t.end), distr = "rbern",
                       prob = ifelse(t >= tswitch, 1, 0))


tswitch_vec <- (0:t.end)
for (tswitch_i in tswitch_vec) {
  abar <- rep(0, length(tswitch_vec))
  abar[which(tswitch_vec >= tswitch_i)] <- 1
  Dstat <- Dstat + action("A1_ts"%+%tswitch_i,
                          nodes = act_A1_tswitch,
                          tswitch = tswitch_i,
                          abar = abar)
}

plotDAG(A(Dstat)[["A1_ts3"]], tmax = 3, xjitter = 0.3, yjitter = 0.03,
        edge_attrs = list(width = 0.5, arrow.width = 0.4, arrow.size = 0.8),
        vertex_attrs = list(size = 15, label.cex = 0.7), excludeattrs = "abar")

# Simulating conterfactual data
# Optional: Last Time point value Carried Forward LTCF
# 1. Dynamic conterfactuals
tic()
Xdyn <- sim(Ddyn, 
            actions = c("A1_th0", "A1_th1"), 
            #wide = FALSE,
            LTCF = "Y",
            n = 100000, 
            rndseed = 123)
toc()

nrow(Xdyn[["A1_th0"]])
View(Xdyn[["A1_th0"]])

# 2. Static conterfactuals
tic()
Xstat <- sim(Dstat, 
             actions = names(A(Dstat)),
             #wide = FALSE,
             LTCF = "Y",
             n = 100000,
             rndseed = 123)
toc()
length(Xstat)
View(Xstat[[names(A(Dstat))[1]]])

# Defining and estimating causal params
# 1. Dynamic
Ddyn <- set.targetE(Ddyn, outcome = 'Y', t = 1:16, param = 'A1_th1')
surv_th1 <- 1- eval.target(Ddyn, data = Xdyn)$res

Ddyn <- set.targetE(Ddyn, outcome = 'Y', t = 1:16, param = 'A1_th0')
Ddyn <- 1- eval.target(Ddyn, data = Xdyn)$res

plotSurvEst(surv = list(d_theta1 = surv_th1, d_theta0 = surv_th0),
            xindx = 1:17,
            ylab = 'Contervactual survival per intervention',
            ylim = c(0.75,1.0))
