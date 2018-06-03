#install packages (if needed)
install.packages("tableone")
install.packages("ipw")
install.packages("sandwich")
install.packages("survey")

# Get directory, filename, and libraries
dirName <- getwd()
fileName <- paste(dirName, "state_abm_causal_v3.txt", sep = "/")

library(tableone)
library(ipw)
library(sandwich)
library(survey)

# Generate dataset and view: subselect
all_content = readLines(fileName)
skip_first_line = all_content[-1]

df = read.csv(textConnection(skip_first_line),
              header = TRUE,
              stringsAsFactors = FALSE
              )

dat1 <- df[df$step == 99,]
dat0 <- df[df$step == 98,]

# Simplify number of variables
sex <- as.integer(as.logical(dat1$sex))
treatment <- as.integer(as.logical(dat1$treatment))
infected <- as.integer(as.logical(dat1$outcome))

# I have to account for all the past exposure per individual to the time point to work

cd <- 0
for (i in c(1:99)){
  cd <- cd + (df[df$step == i,]$count_infected / (1 + df[df$step == i,]$cumulative_distance))
  }
summary(cd)

# Standarise from 0 to 1 the vector
range01 <- function(x){(x-min(x))/(max(x)-min(x))}
cd2 <- range01(cd)
summary(cd2)

# Create dataframe and independent variables
xvars <- c("treatment","sex","cd2")
mydata <-as.data.frame(cbind(sex,treatment,cd2,infected))

# View table1 without weighting
table1 <- CreateTableOne(vars = xvars,
                         strata = "treatment", 
                         data = mydata,
                         test = TRUE)
print(table1, smd = TRUE)

# Propensity score model: propensity score per subject: has to be similar to causal weight
psmodel <- glm(treatment ~ sex,  # sex is the only confounder for treatment
               family = binomial(link = 'logit')
               )
summary(psmodel)
# sex and intercept have to be significant (there's a baseline probability of getting infected: intercept)
ps <- predict(psmodel,
              type = "response"
              )

# Create weights based on the inverse probabilities
weight<-ifelse(treatment == 1,
               1/(ps),
               1/(1-ps)
               )

# Apply weights to data
weightedData<-svydesign(ids = ~ 1, # No clusters: ~0 or ~1 is a formula for no clusters in the function
                        data = mydata,
                        weights = ~ weight
                        )

# weighted table1
weightedTable <- svyCreateTableOne(vars = c("sex","cd2"),
                                   strata = "treatment", 
                                   data = weightedData,
                                   test = TRUE)
print(weightedTable, smd = TRUE)

# Define a function to get CI for the results of the model
asymptotic_var_estimator <- function(model, mode = 'logit'){
  beta <- coef(model)
  
  # To properly account for weighting, use asymptotic (sandwich) variance estimator
  SE <- sqrt(diag(vcovHC(model,
                         type = "HC0")
                  )
             )
  
  # Make sure treatment is the second after intercept
  if (mode == 'logit'){
    causal <- exp (beta[2])
    SD <- 1.96 * SE[2]
    lower <- exp(beta[2] - SD)
    upper <- exp(beta[2] + SD)
  }
  else{
    causal <-  beta[2]
    SD <- 1.96 * SE[2]
    lower <- (beta[2] - SD)
    upper <- (beta[2] + SD)
  }
  
  return (c(lower, causal, upper))
} 

# get causal relative risk: weighted GLM
model.obj <- glm(infected ~ treatment + sex + cd2,
                   weights = weight,
                   family = binomial(link = 'logit')
                   )
summary(model.obj)
asymptotic_var_estimator(model.obj, mode = 'logit')


## What about the risk difference?
model.obj <- glm(infected ~ treatment + sex + cd2,
                   weights = weight,
                   family = binomial(link = 'identity')
)
summary(model.obj)
asymptotic_var_estimator(model.obj, mode = 'linear')

#############################
# Alternative: the IPW package
#############################

# Propensity score model alternative
ipw_model <- ipwpoint(exposure = treatment, # Get weights when predicting treatment
                      family = 'binomial',
                      link = 'logit',
                      denominator = ~ sex, # just the sex is a confounder
                      data = mydata
                      )

# Truncate weights to avoid that some patients are overrepresented in the pseudopopulation
weigths_ipw <- ipw_model$ipw.weights
summary(weigths_ipw)

# Plot the weight density
ipwplot(weights = weigths_ipw,
        logscale = TRUE,
        main = 'Distribution of weights',
        xlim = c(0,max(weigths_ipw))
        )

#fit a marginal structural model (risk difference, RD)
msm <- (svyglm(infected ~ treatment + sex + cd2,
               design = svydesign(ids = ~ 1,
                                  weights = ~weigths_ipw,
                                  data = mydata
                                  )
               )
        )
coef(msm)
confint(msm)
summary(msm)

asymptotic_var_estimator(model.obj, mode = 'linear')
