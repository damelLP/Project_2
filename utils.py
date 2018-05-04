import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
from scipy.stats import spearmanr


## Pandas Column Names
lp = 'Leader Price'
fp = 'Follower Price'
lpayoff = 'Leader Payoff'


def get_polynomials(x, polynomial=1, axis=1):
    return np.stack([np.power(x, p+1) for p in range(polynomial)], axis=axis).squeeze()

def rolling_window(X,y, window_size=2):
    n_feat = 1 if X.ndim == 1 else X.shape[1]
    n_samples = X.shape[0] - window_size
    new_X = np.zeros((n_samples, n_feat * window_size))
    new_y = np.zeros((n_samples, 1))
    for i in range(n_samples):
        new_X[i] = X[i:i+window_size].flatten()
        new_y[i] = y[i+window_size]
    return new_X, new_y

def evaluate(reg, X, y, window_size):
    train_index, test_index = X.shape[0] // 2, (X.shape[0] // 2) + 1
    X_train, X_test, y_train, y_test = X[:train_index, :], X[test_index:,:], y[:train_index,:], y[test_index:,:]
    reg.fit(X_train,y_train)
    days = [i for i in range(X.shape[0])]
    plt.plot(days, y, label="actual")
    plt.plot(days[:train_index], reg.predict(X_train), label="training")
    plt.plot(days[test_index:], reg.predict(X_test), label="prediction")
    plt.legend(loc=1)
    print(spearmanr(y_test, reg.predict(X_test)))
    
    
def get_prices_xy(data, days=30):
    return data[lp].iloc[:days], data[fp].iloc[:days]

def get_historic_prices_xy(data, days=30):
    return data['Follower\'s Price'].iloc[:days], data['Leader\'s Price'].iloc[:days]

def plotscat(x, y, func):
    plt.scatter(x.index.values, y)
    plt.plot(x.index.values, func(x))
    
def get_data_for(follower_type, xls, sheet_names=None):
    sheet_names = sheet_names if sheet_names else xls.sheet_names
    for sheet_name in sheet_names:
        if follower_type in sheet_name:
            return pd.read_excel(xls, sheet_name)