# Installation
install.packages('tmlenet')
devtools::install_github('osofr/tmlenet', build_vignettes = TRUE)

# ERRORS:
# Downloading GitHub repo osofr/tmlenet@master
# from URL https://api.github.com/repos/osofr/tmlenet/zipball/master
# Installing tmlenet
# Installing 1 package: data.table
# trying URL 'https://cran.rstudio.com/bin/windows/contrib/3.4/data.table_1.11.4.zip'
# Content type 'application/zip' length 1813010 bytes (1.7 MB)
# downloaded 1.7 MB
# 
# package ‘data.table’ successfully unpacked and MD5 sums checked
# Warning: cannot remove prior installation of package ‘data.table’
# 
# The downloaded binary packages are in
# C:\Users\geodna\AppData\Local\Temp\RtmpiYN7Sz\downloaded_packages
# Installing 1 package: Rcpp
# trying URL 'https://cran.rstudio.com/bin/windows/contrib/3.4/Rcpp_0.12.18.zip'
# Content type 'application/zip' length 4424531 bytes (4.2 MB)
# downloaded 4.2 MB
# 
# package ‘Rcpp’ successfully unpacked and MD5 sums checked
# Warning: cannot remove prior installation of package ‘Rcpp’
# 
# The downloaded binary packages are in
# C:\Users\geodna\AppData\Local\Temp\RtmpiYN7Sz\downloaded_packages
# Installing 1 package: speedglm
# trying URL 'https://cran.rstudio.com/bin/windows/contrib/3.4/speedglm_0.3-2.zip'
# Content type 'application/zip' length 101148 bytes (98 KB)
# downloaded 98 KB
# 
# package ‘speedglm’ successfully unpacked and MD5 sums checked
# 
# The downloaded binary packages are in
# C:\Users\geodna\AppData\Local\Temp\RtmpiYN7Sz\downloaded_packages
# Installing 1 package: stringr
# also installing the dependency ‘stringi’
# 
# trying URL 'https://cran.rstudio.com/bin/windows/contrib/3.4/stringi_1.1.7.zip'
# Content type 'application/zip' length 14295248 bytes (13.6 MB)
# downloaded 13.6 MB
# 
# trying URL 'https://cran.rstudio.com/bin/windows/contrib/3.4/stringr_1.3.1.zip'
# Content type 'application/zip' length 171029 bytes (167 KB)
# downloaded 167 KB
# 
# package ‘stringi’ successfully unpacked and MD5 sums checked
# Warning: cannot remove prior installation of package ‘stringi’
# package ‘stringr’ successfully unpacked and MD5 sums checked
# 
# The downloaded binary packages are in
# C:\Users\geodna\AppData\Local\Temp\RtmpiYN7Sz\downloaded_packages
# "C:/PROGRA~1/R/R-34~1.2/bin/x64/R" --no-site-file --no-environ --no-save --no-restore --quiet CMD INSTALL  \
# "C:/Users/geodna/AppData/Local/Temp/RtmpiYN7Sz/devtools1a4816b318e9/osofr-tmlenet-13cfc44" --library="C:/Program  \
# Files/R/R-3.4.2/library" --install-tests 
# 
# ERROR: dependencies 'data.table', 'Rcpp' are not available for package 'tmlenet'
# * removing 'C:/Program Files/R/R-3.4.2/library/tmlenet'
# Installation failed: Command failed (1)

library(tmlenet)
#data(df_netKmax6)
head(df_netKmax6)
Kmax <- ncol(NetInd_mat_Kmax6) # Max number of friends in this network:

sW <- def_sW(netW1 = W1[[0:Kmax]], netW2 = W2[[0:Kmax]], netW3 = W3[[0:Kmax]])
sA <- def_sA(netA = A[[0:Kmax]])

eval_res <- eval.summaries(sW = sW,
                           sA = sA,
                           Kmax = 6,
                           data = df_netKmax6,
                           NETIDmat = NetInd_mat_Kmax6)
