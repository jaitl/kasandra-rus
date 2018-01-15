"""
    Вычисление коэффициента Хёрста
"""


import math
from scipy import stats
import numpy as np
import matplotlib.pyplot as plt


class HistResult:
    # Матрица с результатами, в виде списка таплов: (mi, ln_mi, variance, ln_variance)
    matrix = []
    
    # коэффициет b
    slope = 0
    
    # свободный коэффициет k
    intercept = 0
    
    # коэффициент корелляции
    correlation_coefficient = 0
    
    # коэффициент Харста
    hirst_coefficient = 0
    
    def __init__(self, matrix, slope, intercept, correlation_coefficient, hirst_coefficient):
        self.matrix = matrix
        self.slope = slope
        self.intercept = intercept
        self.correlation_coefficient = correlation_coefficient
        self.hirst_coefficient = hirst_coefficient
        
    def print_result(self): 
        print("---")
        for mi, ln_mi, variance, ln_variance in self.matrix:
            print("mi: 1/%s, variance: %s, ln(variance): %s, ln(m): %s)" % (mi, variance, ln_variance, ln_mi))
        
        print("---")
        print("y = %s * x + %s" %(self.slope, self.intercept))
        print("Correlation coefficient = %s" % self.correlation_coefficient)
        print("Hirst Coefficient = %s" % self.hirst_coefficient)
            
    def plot(self, path, figsize=(10, 5)):

        fig = plt.figure(figsize=figsize)
        ax = fig.gca()

        x = [x[1] for x in self.matrix]
        y = [y[3] for y in self.matrix]
        
        x1 = []
        y1 = []

        for xx in np.arange(min(x) - 0.1, max(x) + 0.1, 0.1):
            x1.append(xx)
            y1.append(self.slope * xx + self.intercept)
        
        ax.scatter(x, y)
        ax.plot(x1, y1)
        
        fig.savefig(path)


class SelfSimilarityHirst:
    """
        Самоподобие Херста
    """
    
    def __blocks_split(self, arr, size):
        """
            Делит входной массив на блоки указанного размера,
            например arr = [1, 2, 3, 4, 5], size = 2,
            результат: [[1, 2], [3, 4]]
            отбрасывает элементы для которых не хватает блоков
        """
        blocks = []
        pos = 0
        for i in range(size, len(arr) + 1, size):
            blocks.append(arr[pos:i])
            pos = pos + size
        return blocks

    def __expected_value(self, arr):
        """
            Математическое ожидание (среднее)
        """
        return sum(arr) / len(arr)

    def __variance(self, arr, exp):
        """
            Дисперсия
        """
        d = [math.pow(x - exp, 2) for x in arr]
        return sum(d) / len(d)
    
    def __block_count(self, data_len):
        """
            Рассчитывает количество блоков
        """
        return int(data_len / 2)
    
    def __variance_matrix(self, data, log_base):
        """
            Рассчитывает зависимость логарифма среднего значения дисперсии от логарифма mi
        """
        result = []
        
        last_cnt = -1
        
        block_count = self.__block_count(len(data))
        
        for mi in range(block_count, 0, -1):
            block_cnt = int(len(data)/mi)
            
            # Если уже были блоки такого размера, то пропускаем
            if block_cnt == last_cnt:
                continue
            last_cnt = block_cnt
            
            dds = []
            
            # Рассчет среднего и дисперсии для каждого блока
            for block in self.__blocks_split(data, block_cnt):
                vals = [y for x, y in block]
                ev = self.__expected_value(vals)
                v = self.__variance(vals, ev)
                dds.append(v)
               
            # Рассчет среднего значения дисперсий блока
            variance = self.__expected_value(dds)
            
            ln_mi = math.log(1/mi, log_base)
            ln_variance = math.log(variance, log_base)
            
            result.append((mi, ln_mi, variance, ln_variance))
            
        return result
    
    def __regress(self, x, y):
        slope, intercept, correlation_coefficient, p_value, std_err = stats.linregress(x,y)
        h = (2 - slope) / 2
        
        return (slope, intercept, correlation_coefficient, h)
    
    def compute(self, data, log_base = math.e):
        matrix = self.__variance_matrix(data, log_base)
        
        x = [x[1] for x in matrix]
        y = [y[3] for y in matrix]
        
        slope, intercept, correlation_coefficient, h = self.__regress(x, y)
        
        return HistResult(matrix, slope, intercept, correlation_coefficient, h)
