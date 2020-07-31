#!/usr/bin/env python

from flask import Flask, request
import model
import encoding
import proximity
import numpy as np

app = Flask(__name__)


@app.route("/")
def status() -> str:
    status = str("Model trained: " + str(model.check_for_saved_model()))
    return status


@app.route("/old_predict/<military_time>/<lat>/<longitude>/<age>/<gender>")
def predict(military_time, lat, longitude, age, gender) -> float:
    # get proximity score
    location_node = [float(lat), float(longitude)]
    proximity_score = proximity.get_proximity(location_node)

    # get prediction of emergency
    sin_time = encoding.sin_time(encoding.military_time_in_minutes_fn(military_time))
    cos_time = encoding.cos_time(encoding.military_time_in_minutes_fn(military_time))
    prediction = model.predict(np.asarray([int(age), int(gender), sin_time, cos_time]).reshape(1, -1))
   
    # return multiplication
    return str(proximity_score * prediction)


@app.route("/predict", methods=["GET"])
def get_predict():
    if request.method == "GET":
        military_time = request.args.get("military_time")
        lat = request.args.get("lat")
        longitude = request.args.get("long")
        age = request.args.get("age")
        gender = request.args.get("gender")
        if gender == "male":
            gender = 1
        else:
            gender = 0

        # get proximity score
        location_node = [float(lat), float(longitude)]
        proximity_score = proximity.get_proximity(location_node)

        # get prediction of emergency
        sin_time = encoding.sin_time(encoding.military_time_in_minutes_fn(military_time))
        cos_time = encoding.cos_time(encoding.military_time_in_minutes_fn(military_time))
        prediction = model.predict(np.asarray([int(age), int(gender), sin_time, cos_time]).reshape(1, -1))

        # return multiplication
        return str(proximity_score * prediction)


# run it directly via python3 main.py
if __name__ == "__main__":
    hostname = "192.168.43.212"
    port = 3005
    app.run(host=hostname, port=port)
