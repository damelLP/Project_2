# Documentation for Stackelberg Game Project 2

The task wass to make a program which plays repeated 2-person Stackelberg pricing games as the leader
under imperfect information. There were 3 followers (MK1, MK2, MK3) each given with 100 data points. The data points represent a time series for 100 days.

## How to `run` the system?

To run the system, we need to start the:

`rmiregistry &`

Then, we need to start with GUI interface with the follower called in as well:

`java -classpath poi-3.7-20101029.jar: -Djava.rmi.server.hostname=127.0.0.1 comp34120.ex2.Main`

Then we need to connect the **our** leader to the GUI interface:

Since, the java versions might be different it might be needed that the *RidgeLeader.java* needs to be recompiled.
The path for the *.java file* is in the path *~/Project_2/src/main/java*.

`java -Djava.rmi.server.hostname=127.0.0.1 RidgeLeader`

## Background

For the unknown follower's function: R'[u<sub>L</sub>] finds it's estimator R'[u<sub>L</sub>] where R' is a linear function.

For each timestep *t* in *T* we try to **minimize the difference** between the predicted and the actual follower's value:   u<sub>F</sub><sup>R</sup>(t) - R'[u<sub>L</sub>(t)]<sup>2</sup>.

We can find the best strategy for the leader by solving the following maximisation problem of: max <sub><sub>u<sub>L</sub></sub> <sub>-></sub> <sub>U<sub>L</sub></sub></sub> J<sub>L</sub>[u<sub>L</sub>, R'(u<sub>L</sub>)].

## Approaches used

This section basically gives an overview as to the various approaches been used to predict the Stackelberg strategy.

### Ordinary Least Square Regression


![alt text](https://i.ytimg.com/vi/gb4qqX4uhYA/hqdefault.jpg "OLS Regression Graph")

Ordinary Least Square (OLS) is a method for estimating the unknown parameters in a linear regression model by taking linear combinations, which in this case is to multiply the values of x (Leader's strategy, u<sub>L</sub>, over a time period) with a constant β to generate a line represented by a vector y<sup>^</sup> which we model as a linear function of x. **y** here is an approximation of the follower's reaction function R(u<sub>L</sub>).

Determing an accurate value for β is difficult when the data is uncorrelated and so we estimate a value β<sup>^</sup> which best explains the observations of changes to the follower's strategy, u<sub>F</sub>, based on the Leader's strategy, u<sub>L</sub>. 

How well the real model is estimated (i.e. goodness of fit, R<sup>2</sup>) can be derived in OLS using a combination of the 3 following variables:

1.) **Sum of Squared Residuals** (SSR)

2.) **Explained Sum of Squares** (SSE)

3.) **Total Sum of Squares** (SST)

R<sup>2</sup> = 


`NB: The goodness of fit value represents how much of the original model we have explained and not the causal relationships.`

### Ridge Regression (Implemented Technique)

Collinearity, is the existence of near-linear relationships among the independent variables. 

Ridge Regression is a technique for analyzing multiple regression data that suffer from collinearity. When
collinearity occurs, least squares estimates are unbiased, but their variances (for *x*) are large so they may be far from
the true value. By adding a degree of bias to the regression estimates, ridge regression reduces the standard errors.

We need to reduce collinearity since it can create inaccurate estimates of the regression coefficients, inflate the standard errors of the regression coefficients.

To solve a linear function for which the solution is not unique, the OLS regression technique might lead to an overfitted or an underfitted solution. But to get rid of overfit/underfit situation and support a solution with suitable properties; one must add a **regularization** term such as gamma and where *x* is the input and can be represented as the equation: `|Ax - b| + |Γ * x|`

This is also known as L2 regularization. The solving of the linear equation is exactly the same as OLS regression just with the addition of the regularization term to balance the variance shown by the model. 


### Neural Network and Long Short Term Memory

A Neural network and an LSTM can be used to approximate any function and hence they are universal function approximators. These can be used to fit the curve for follower's historical data. 

All the follower's price are fed as input to the deep learning architectures mentioned and the weights are updated using back-propagation.
LSTM is better for time series since it can learn long term temporal dependencies.

Since, predicting *iteration* `t` with *iteration*  `t + 1` are very less features to learn the deep learning architectures from. 
We use a **`window size`** so that iteration `t` can be dependent on the window size, for e.g. if the window size is 20, so iteration 
`t + 20` depends on every iteration from `t` to `t + 19`. For adding other features to deal with non linear data, we also add **`polynomial`**
features for each of the data points. For example if the input is the series: `2,4,6,8,..` we can also add in additional features by
taking the square of the inputs such as `4,16,36,64..`.

We used grid search to find **optimal parameters** for *polynomial* and *window size* which were 4 and 3 respectively.

The models were experimented using Python programming language using the Keras Machine Learning library. 

## Evaluation 

In this section, we look at how we evaluate the two models of OLS and Ridge Regression using the **correlation** technique.

### Correlation

To correlate the two regression techniques we generate a correlation of the predicted follower's value with the actual follower's value. For the purpose of explaination, we are taking into account the first followers MK1's prices.

![alt text](linear_regressor_correlation.png "OLS regression correlation with follower's prices")

We get a low correlation of 0.6% as shown in the image above with predicted and actual follower's prices with OLS regression with a window size of 20.

![alt text](ridge_regressor_correlation.png "Ridge regression correlation with follower's prices")

We get a higher correlation of -15% as shown in the image above with predicted and actual follower's prices with ridge regression with a window size of 20. This shows that ridge regression was able to correlate with the prices much more and hence this technique was used. 

We use the *spearmanr* function from the **scipy** library in Python to calculate the correlation.

### Integration with GUI Interface

All of the models above were first written in Python for experimentation purposes. Then, they were interfaced with the Java GUI interface as provided. Since, all the models needed to be evaluated and transferred to Java, a generic **Regressor** class was made so that its easier to inculcate new techniques. For Java, **EJML** library was used for doing matrix calculation and construction of various regression models.

For LSTM and Neural Network, models were written in Keras in Python and were imported to DL4J which is a machine learning library in Java. These models were not submitted as the final result since there were version issues between Keras and DL4J. Certain aspects of the model such as *initializers - xavier initializer* were available in Keras but not available in DL4J when the model was imported. This forced the team to not take this approach.
