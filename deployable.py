from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from google.cloud import storage
import tensorflow as tf
import time
import base64
import json
import base64
import io
from flask import jsonify

model = None
BUCKET_NAME = 'image-labeler'
LABEL_MAP_PATH = '/tmp/classes-trainable.txt'
LABEL_DICT_PATH = '/tmp/class-descriptions.csv'
MODEL_PATH = '/tmp/saved_model.ckpt'

TOP_K = 2
SCORE_THRESHOLD = 0.6


class CustomModel():
    
    g = None
    sess = None
    labelMap = None
    labelDict = None
    input_values = None
    predictions = None
    def __init__(self):
        # global MODEL_PATH
        tf.compat.v1.disable_eager_execution()
        self.g = tf.Graph()
        with self.g.as_default():
            self.sess = tf.compat.v1.Session()
            
            saver = tf.compat.v1.train.import_meta_graph(MODEL_PATH + '.meta')
            saver.restore(self.sess, MODEL_PATH)
            self.input_values = self.g.get_tensor_by_name('input_values:0')
            self.predictions = self.g.get_tensor_by_name('multi_predictions:0')

    def loadFiles(self):
        # global LABEL_MAP_PATH, LABEL_DICT_PATH
        labelmap = [line.rstrip() for line in tf.io.gfile.GFile(LABEL_MAP_PATH)]
        label_dict = {}
        for line in tf.io.gfile.GFile(LABEL_DICT_PATH):
            words = [word.strip(' "\n') for word in line.split(',', 1)]
            label_dict[words[0]] = words[1]

        self.labelMap = labelmap
        self.labelDict = label_dict
    
    def predict(self, image):
        # compressed_image = tf.compat.v1.io.gfile.GFile(image_filename, 'rb').read()
        
        start = time.time()
        predictions_eval = self.sess.run(
            self.predictions, feed_dict={
                self.input_values: [image]
            })
        end = time.time()
        print('elapsed = ', (end - start))
        top_k = predictions_eval.argsort()[::-1]  # indices sorted by score
        if TOP_K > 0:
          top_k = top_k[:TOP_K]
        if SCORE_THRESHOLD is not None:
          top_k = [i for i in top_k
                   if predictions_eval[i] >= SCORE_THRESHOLD]
        print('Image: \n')
        ret = []
        for idx in top_k:
          mid = self.labelMap[idx]
          display_name = self.labelDict[mid]
          score = predictions_eval[idx]
          ret.append((idx, mid, display_name, score))
        return ret

    def serialize(self, lst, id):
        jsonlist = []
        for i in lst:
            jsonlist.append({
                'label' : str(i[2]).lower(),
                'score' : float(i[3]),
                'isProcessed' : True
            })
        return {'id' : id, 'data' : jsonlist}


def download_blob(bucket_name, source_blob_name, destination_file_name):
  """Downloads a blob from the bucket."""
  storage_client = storage.Client()
  bucket = storage_client.get_bucket(bucket_name)
  blob = bucket.blob(source_blob_name)

  blob.download_to_filename(destination_file_name)

  print('Blob {} downloaded to {}.'.format(source_blob_name, destination_file_name))


def handler(request):
    print('header = ', request.headers)
    content_type = request.headers['content-type']
    if content_type == 'application/json':
        print('Its application json')
        request_json = request.get_json(silent=True)
        print('request_json got')
        if request_json and 'imageStructures' in request_json:
            imageStructures = request_json['imageStructures']
        else:
            raise ValueError("JSON is invalid, or missing a 'imageStructures' property")
    else:
        raise ValueError("Content type is invalid")
    print('Started loading model')
    global model
    if model == None:
        download_blob(BUCKET_NAME, 'tensorflow/class-descriptions.csv',
                  LABEL_DICT_PATH)

        download_blob(BUCKET_NAME, 'tensorflow/classes-trainable.txt',
                    LABEL_MAP_PATH)
                    
        download_blob(BUCKET_NAME, 'tensorflow/saved_model.ckpt.data-00000-of-00001',
                    MODEL_PATH + '.data-00000-of-00001')

        download_blob(BUCKET_NAME, 'tensorflow/saved_model.ckpt.meta',
                     MODEL_PATH + '.meta')

        download_blob(BUCKET_NAME, 'tensorflow/saved_model.ckpt.index',
                    MODEL_PATH + '.index')
        
        model = CustomModel()
        model.loadFiles()
    
    print('imageStructures = ', imageStructures)
    print('imageStructures list = ' , list(imageStructures))
    print('type of imageStructures = ', type(imageStructures))
    imageStructures = list(imageStructures)
    retList = []
    for image in imageStructures:
        print('decoding')
        imageBytes = decodeStringToBytes(image['imageBase64'])
        print('imageByts = ', imageBytes)
        labels = model.predict(imageBytes)
        print('serializing')
        jsonlist = model.serialize(labels, image['id'])
        print('appending')
        retList.append(jsonlist)
    print('retList = ', retList)
    return jsonify({'responses' : retList})
 
def encodeBytesToString(b):
    return base64.b64encode(b).decode('utf-8')

def decodeStringToBytes(encoded):
    string = encoded.encode('utf-8')
    return base64.decodebytes(string)

def img_to_json(image_filename):
    with open(image_filename, "rb") as image:
        f = image.read()
        b = bytearray(f)
        
    string = encodeBytesToString(f)
    
    data = {}
    data['id'] = image_filename
    data['img'] = string
    json_data = json.dumps(data)
    decoded = decodeStringToBytes(string)
    return data

 