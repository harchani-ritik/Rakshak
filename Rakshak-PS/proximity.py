#!/usr/bin/env python

# Return proximity

from scipy.spatial import distance
import numpy as np
import pandas as pd

locations_csv = "datasets/lat_long_only.csv"
dataset_nodes = pd.read_csv(locations_csv)


# Source: https://codereview.stackexchange.com/questions/28207/finding-the-closest-point-to-a-list-of-points
def closest_node(node, nodes):
    closest_index = distance.cdist([node], nodes).argmin()
    return nodes[closest_index]


def proximity(node, nodes) -> float:
    closest_n = closest_node(node, nodes)
    dist = distance.cdist([node], [closest_n])
    # get the actual euclidean distance
    dist = dist[0][0]

    # get the 4th decimal place (11m precision) precision
    precise_dist = dist * 10 ** 4
    reciprocal = 1/precise_dist

    # if you are too close, reciprocal will be more than 1. Remove the extra.
    if reciprocal > 1:
        reciprocal = 1

    return reciprocal


# driver for original dataset
def get_proximity(node: list) -> float:
    return proximity(node, np.asarray(dataset_nodes))
